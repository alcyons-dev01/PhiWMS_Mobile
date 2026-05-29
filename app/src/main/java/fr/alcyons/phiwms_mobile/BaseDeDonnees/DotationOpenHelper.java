package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.EVENT;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

public class DotationOpenHelper extends DBOpenHelper {
    public DotationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTableDotation(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DOTATION, null, null);
    }

    public static ArrayList<Dotation> getDotationGlobale(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_PLEINVIDE_DOTATION + " = 0 AND "+Constantes.CLE_COL_URGENCE_DOTATION+" = 0 AND "+Constantes.CLE_COL_INSTALLATION_DOTATION +" = 0", new String[]{});

        ArrayList<Dotation> dotationList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Dotation dotation = new Dotation(cursor);
            dotationList.add(dotation);
        }
        cursor.close();
        cursor = null;
        return dotationList;
    }


    public static long insererDotationEnBDD(SQLiteDatabase db, Dotation objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__UID_DOTATION, objet.get_UID());
        contentValues.put(Constantes.CLE_COL_INTITULE_DOTATION, objet.getIntitule());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DOTATION, objet.getRef_Depot());
        contentValues.put(Constantes.CLE_COL_DEBUT_DOTATION, objet.getDebut());
        contentValues.put(Constantes.CLE_COL_FIN_DOTATION, objet.getFin());
        contentValues.put(Constantes.CLE_COL_INTERROMPU_DOTATION, objet.isInterrompu());
        contentValues.put(Constantes.CLE_COL_NB_SEMAINE_DOTATION, objet.getNB_Semaine());
        contentValues.put(Constantes.CLE_COL_VALORISATION_TTC_DOTATION, objet.getValorisation_TTC());
        contentValues.put(Constantes.CLE_COL_DOTATION_STD_DOTATION, objet.getDotation_Std());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_DOTATION, objet.getCommentaire());
        contentValues.put(Constantes.CLE_COL_DEPOT_UID_DOTATION, objet.getDepot_UID());
        contentValues.put(Constantes.CLE_COL_NB_PATIENTS_DOTATION, objet.getNb_patients());
        contentValues.put(Constantes.CLE_COL_TOURNEE_REFERENCE_DOTATION, objet.getTournee_Reference());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_DOTATION, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_DOTATION, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_TECH_UID_DOTATION, objet.getTech_UID());
        contentValues.put(Constantes.CLE_COL_URGENCE_DOTATION, objet.isURGENCE());
        contentValues.put(Constantes.CLE_COL_SECURISE_DOTATION, objet.isSECURISE());
        contentValues.put(Constantes.CLE_COL_TAUXSTOCKIDEAL_DOTATION, objet.getTauxStockIdeal());
        contentValues.put(Constantes.CLE_COL_INSTALLATION_DOTATION, objet.isINSTALLATION());
        contentValues.put(Constantes.CLE_COL_PLEINVIDE_DOTATION, objet.isPLEINVIDE());
        contentValues.put(Constantes.CLE_COL_PROTOCOLE_UID_DOTATION, objet.getProtocole_UID());
        contentValues.put(Constantes.CLE_COL_COMMANDEAB_DOTATION, objet.isCommandeAB());
        long rowID = db.insert(Constantes.TABLE_DOTATION, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static Dotation getDotationByNomAndDepot(SQLiteDatabase db, String nom, String depotid) {
        Dotation dotationCorrespondant = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_DEPOT_UID_DOTATION + "= ? AND "+Constantes.CLE_COL_INTITULE_DOTATION+"= ?", new String[]{depotid, nom});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Dotation dotation = new Dotation(cursor);
            if (dotation.isPLEINVIDE()) {
                dotationCorrespondant = dotation;
            }

        }
        cursor.close();
        cursor = null;
        return dotationCorrespondant;
    }


    public static Dotation getDotationByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        Dotation dotation = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_DOTATION + "      WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " =? ", new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            dotation = new Dotation(cursor);
        }
        cursor.close();
        cursor = null;
        return dotation;
    }

    public static List<Dotation> getDotationByDepot(SQLiteDatabase db, Integer depotUID) {
        String critereRecherche = String.valueOf(depotUID);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_DEPOT_UID_DOTATION + " LIKE ?", new String[]{critereRecherche});

        List<Dotation> dotationList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Dotation dotation = new Dotation(cursor);
            if(!dotation.isPLEINVIDE()){
                dotationList.add(dotation);
            }
        }
        cursor.close();
        cursor = null;
        return dotationList;
    }

    public List<Dotation> getDotationPleinByDepot(SQLiteDatabase db, Integer depotUID) {
        String critereRecherche = String.valueOf(depotUID);
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL_DEPOT_UID_DOTATION + " LIKE ? AND " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1", new String[]{critereRecherche});

        List<Dotation> dotationList = new ArrayList<>();

        while (cursor.moveToNext()) {
            dotationList.add(new Dotation(cursor));
        }
        cursor.close();
        cursor = null;
        return dotationList;
    }

    public static Dotation getDotationPleinByStringId(SQLiteDatabase db, String id) {
        Dotation dotationCorrespondant = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DOTATION + " WHERE " + Constantes.CLE_COL__UID_DOTATION + "= ? ", new String[]{id});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Dotation dotation = new Dotation(cursor);
            if (dotation.isPLEINVIDE()) {
                dotationCorrespondant = dotation;
            }

        }
        cursor.close();
        cursor = null;
        return dotationCorrespondant;
    }

    public static List<Dotation> getAllDotationPleinVide(SQLiteDatabase db)
    {
        List<Dotation> listDotation = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * FROM " + Constantes.TABLE_DOTATION + " Where " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1", null);

        while(cursor.moveToNext())
        {
            Dotation courante = new Dotation(cursor);
            listDotation.add(courante);
        }

        cursor.close();
        cursor = null;

        return listDotation;
    }

    public List<String> getDepotDotationPleinVide(SQLiteDatabase db) {
        List<String> listeDepot = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * FROM " + Constantes.TABLE_DOTATION + " Where " + Constantes.CLE_COL_PLEINVIDE_DOTATION + "=1 group by " + Constantes.CLE_COL_REF_DEPOT_DOTATION, null);


        while (cursor.moveToNext()) {
            listeDepot.add(String.valueOf(cursor.getString(Constantes.NUM_COL_REF_DEPOT_DOTATION)));
        }

        cursor.close();
        cursor = null;
        return listeDepot;
    }

    public static void insererBDDLocaleDotation(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Dotations";
        final String erreurSynchronisationLibelle = "Dotations non synchronisées";

        if (!statutConnexion) {
            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDotationUF;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            viderTableDotation(db);
            Detail_DotOpenHelper.viderTableDetail_Dot(db);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String erreur = "";
                        boolean etat = true;
                        int resultCount = response.getInt("resultCount");

                        if (resultCount == 0) {
                            etat = false;
                            erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                erreur = "Votre session a expirée, veuillez vous reconnecter.";
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                            } else if (!erreur.equals("Aucune Dotation trouvée")) {
                                erreur = "Erreur API Dotations";
                            } else {
                                etat = true;
                            }
                            // ⬇️ Pas d'insertion → mise à jour directe de la modale sur le thread UI
                            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);

                        } else {
                            // Parsing sur le thread UI (rapide)
                            final List<Dotation> listeDotations = new ArrayList<>();
                            final List<Detail_Dot> listeDetailDots = new ArrayList<>();
                            final List<EVENT> listeEvents = new ArrayList<>();

                            JSONArray dotationJSONArray = response.getJSONArray("Dotations");
                            for (int i = 0; i < dotationJSONArray.length(); i++) {
                                JSONObject dotationJSONObject = dotationJSONArray.getJSONObject(i);
                                listeDotations.add(new Dotation(dotationJSONObject));

                                if (dotationJSONObject.has("detail_dots")) {
                                    JSONArray DetailDotJsonArray = dotationJSONObject.getJSONArray("detail_dots");
                                    for (int j = 0; j < DetailDotJsonArray.length(); j++) {
                                        listeDetailDots.add(new Detail_Dot(DetailDotJsonArray.getJSONObject(j)));
                                    }
                                }
                            }

                            if (response.has("Events")) {
                                JSONArray eventJSONArray = response.getJSONArray("Events");
                                for (int i = 0; i < eventJSONArray.length(); i++) {
                                    listeEvents.add(new EVENT(eventJSONArray.getJSONObject(i)));
                                }
                            }

                            // Insertion sur un thread background
                            new Thread(() -> {
                                boolean etatThread = true;
                                String erreurThread = "";
                                SQLiteStatement stmtDotation = db.compileStatement(
                                        "INSERT INTO " + DotationOpenHelper.Constantes.TABLE_DOTATION + " ("
                                                + DotationOpenHelper.Constantes.CLE_COL__UID_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_INTITULE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_REF_DEPOT_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_DEBUT_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_FIN_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_INTERROMPU_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_NB_SEMAINE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_VALORISATION_TTC_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_DOTATION_STD_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_COMMENTAIRE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_DEPOT_UID_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_NB_PATIENTS_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_TOURNEE_REFERENCE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_SYS_DT_MAJ_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_SYS_USER_MAJ_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_TECH_UID_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_URGENCE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_SECURISE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_TAUXSTOCKIDEAL_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_INSTALLATION_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_PLEINVIDE_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_PROTOCOLE_UID_DOTATION + ","
                                                + DotationOpenHelper.Constantes.CLE_COL_COMMANDEAB_DOTATION
                                                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                                );

                                SQLiteStatement stmtDetailDot = db.compileStatement(
                                        "INSERT INTO " + Detail_DotOpenHelper.Constantes.TABLE_DETAIL_DOT + " ("
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_DOTATION_UID_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_CODE_PRODUIT_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_DESIGNATION_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_COND_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_QTE_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_REF_FOUR_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_CATEGORIE_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_LIVRAISON_DIRECTE_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_SYS_DT_MAJ_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_SYS_HEURE_MAJ_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_SYS_USER_MAJ_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL__UID_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_VALEUR_TTC_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_STOCK_MINIMUM_DETAIL_DOT + ","
                                                + Detail_DotOpenHelper.Constantes.CLE_COL_PLEINVIDE_ADRESSAGE_DETAIL_DOT
                                                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                                );

                                SQLiteStatement stmtEvent = db.compileStatement(
                                        "INSERT INTO " + EVENTOpenHelper.Constantes.TABLE_EVENT + " ("
                                                + EVENTOpenHelper.Constantes.CLE_COL__UID_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_DATE_EVENT_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_ID_RESSOURCE_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_JOUR_EVENT_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_SEMAINE_EVENT_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_MOIS_DE_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_JOUR_DE_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_ANNEE_DE_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_MOIS_LIVRAISON_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_MOISREFERENCE_EVENT + ","
                                                + EVENTOpenHelper.Constantes.CLE_COL_TOURNEEID_EVENT
                                                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)"
                                );

                                db.beginTransaction();
                                try {
                                    // ⬇️ Insertion des Dotations
                                    for (Dotation dotation : listeDotations) {
                                        stmtDotation.clearBindings();
                                        stmtDotation.bindLong(1,   dotation.get_UID());
                                        bindStringOrNull(stmtDotation, 2,  dotation.getIntitule());
                                        bindStringOrNull(stmtDotation, 3,  dotation.getRef_Depot());
                                        bindStringOrNull(stmtDotation, 4,  dotation.getDebut());
                                        bindStringOrNull(stmtDotation, 5,  dotation.getFin());
                                        stmtDotation.bindLong(6,   dotation.isInterrompu() ? 1 : 0);
                                        stmtDotation.bindLong(7,   dotation.getNB_Semaine());
                                        stmtDotation.bindLong(8,   dotation.getValorisation_TTC());
                                        bindStringOrNull(stmtDotation, 9,  dotation.getDotation_Std());
                                        bindStringOrNull(stmtDotation, 10, dotation.getCommentaire());
                                        stmtDotation.bindLong(11,  dotation.getDepot_UID());
                                        stmtDotation.bindLong(12,  dotation.getNb_patients());
                                        bindStringOrNull(stmtDotation, 13, dotation.getTournee_Reference());
                                        bindStringOrNull(stmtDotation, 14, dotation.getSYS_DT_MAJ());
                                        bindStringOrNull(stmtDotation, 15, dotation.getSYS_HEURE_MAJ());
                                        bindStringOrNull(stmtDotation, 16, dotation.getSYS_USER_MAJ());
                                        stmtDotation.bindLong(17,  dotation.getTech_UID());
                                        stmtDotation.bindLong(18,  dotation.isURGENCE() ? 1 : 0);
                                        stmtDotation.bindLong(19,  dotation.isSECURISE() ? 1 : 0);
                                        stmtDotation.bindLong(20,  dotation.getTauxStockIdeal());
                                        stmtDotation.bindLong(21,  dotation.isINSTALLATION() ? 1 : 0);
                                        stmtDotation.bindLong(22,  dotation.isPLEINVIDE() ? 1 : 0);
                                        stmtDotation.bindLong(23,  dotation.getProtocole_UID());
                                        stmtDotation.bindLong(24,  dotation.isCommandeAB() ? 1 : 0);
                                        stmtDotation.executeInsert();
                                    }

                                    // ⬇️ Insertion des Detail_Dot
                                    for (Detail_Dot detail_dot : listeDetailDots) {
                                        stmtDetailDot.clearBindings();
                                        stmtDetailDot.bindLong(1,   detail_dot.getDotation_UID());
                                        stmtDetailDot.bindLong(2,   detail_dot.getCode_produit());
                                        bindStringOrNull(stmtDetailDot, 3,  detail_dot.getDesignation());
                                        stmtDetailDot.bindLong(4,   detail_dot.getCond());
                                        stmtDetailDot.bindLong(5,   detail_dot.getQte());
                                        bindStringOrNull(stmtDetailDot, 6,  detail_dot.getRef_four());
                                        bindStringOrNull(stmtDetailDot, 7,  detail_dot.getCategorie());
                                        stmtDetailDot.bindLong(8,   detail_dot.isLivraison_Directe() ? 1 : 0);
                                        bindStringOrNull(stmtDetailDot, 9,  detail_dot.getSYS_DT_MAJ());
                                        bindStringOrNull(stmtDetailDot, 10, detail_dot.getSYS_HEURE_MAJ());
                                        bindStringOrNull(stmtDetailDot, 11, detail_dot.getSYS_USER_MAJ());
                                        stmtDetailDot.bindLong(12,  detail_dot.get_UID());
                                        stmtDetailDot.bindLong(13,  detail_dot.getValeur_TTC());
                                        stmtDetailDot.bindLong(14,  detail_dot.getStock_minimum());
                                        bindStringOrNull(stmtDetailDot, 15, detail_dot.getPleinVide_Adressage());
                                        stmtDetailDot.executeInsert();
                                    }

                                    // ⬇️ Insertion des Events
                                    for (EVENT event : listeEvents) {
                                        stmtEvent.clearBindings();
                                        stmtEvent.bindLong(1,   event.get_UID());
                                        bindStringOrNull(stmtEvent, 2,  event.getDate_event());
                                        stmtEvent.bindLong(3,   event.getID_Ressource());
                                        bindStringOrNull(stmtEvent, 4,  event.getJour_event());
                                        stmtEvent.bindLong(5,   event.getSemaine_event());
                                        bindStringOrNull(stmtEvent, 6,  event.getMois_de());
                                        bindStringOrNull(stmtEvent, 7,  event.getJour_de());
                                        bindStringOrNull(stmtEvent, 8,  event.getAnnee_de());
                                        bindStringOrNull(stmtEvent, 9,  event.getMois_livraison());
                                        bindStringOrNull(stmtEvent, 10, event.getMoisReference());
                                        stmtEvent.bindLong(11,  event.getTourneeID());
                                        stmtEvent.executeInsert();
                                    }

                                    db.setTransactionSuccessful();
                                } catch (Exception e) {
                                    etatThread = false;
                                    erreurThread = "Erreur lors de l'insertion des dotations";
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    stmtDotation.close();
                                    stmtDetailDot.close();
                                    stmtEvent.close();
                                }

                                // ⬇️ Mise à jour de la modale sur le thread UI une fois tout terminé
                                final boolean resultatFinal = etatThread;
                                final String erreurFinale = erreurThread;
                                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                                        ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, resultatFinal, erreurFinale)
                                );

                            }).start();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Dotation volley", error.toString());
                            ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", token);
                    headers.put("UserId", String.valueOf(utilisateur.getId()));
                    headers.put("EtablissementId", String.valueOf(utilisateur.getEtablissementId()));
                    return headers;
                }
            };

            obreq.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 100000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            requestQueue.add(obreq);
        }
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DOTATION = "Dotation";
        public static final String CLE_COL__UID_DOTATION = "_UID";
        public static final int NUM_COL__UID_DOTATION = 1;
        public static final String TYPE_COL__UID_DOTATION = "INTEGER";
        public static final String CLE_COL_INTITULE_DOTATION = "Intitulé";
        public static final int NUM_COL_INTITULE_DOTATION = 2;
        public static final String TYPE_COL_INTITULE_DOTATION = "TEXT";
        public static final String CLE_COL_REF_DEPOT_DOTATION = "Ref_Depot";
        public static final int NUM_COL_REF_DEPOT_DOTATION = 3;
        public static final String TYPE_COL_REF_DEPOT_DOTATION = "TEXT";
        public static final String CLE_COL_DEBUT_DOTATION = "Début";
        public static final int NUM_COL_DEBUT_DOTATION = 4;
        public static final String TYPE_COL_DEBUT_DOTATION = "TEXT";
        public static final String CLE_COL_FIN_DOTATION = "Fin";
        public static final int NUM_COL_FIN_DOTATION = 5;
        public static final String TYPE_COL_FIN_DOTATION = "TEXT";
        public static final String CLE_COL_INTERROMPU_DOTATION = "Interrompu";
        public static final int NUM_COL_INTERROMPU_DOTATION = 6;
        public static final String TYPE_COL_INTERROMPU_DOTATION = "INTEGER";
        public static final String CLE_COL_NB_SEMAINE_DOTATION = "NB_Semaine";
        public static final int NUM_COL_NB_SEMAINE_DOTATION = 7;
        public static final String TYPE_COL_NB_SEMAINE_DOTATION = "INTEGER";
        public static final String CLE_COL_VALORISATION_TTC_DOTATION = "Valorisation_TTC";
        public static final int NUM_COL_VALORISATION_TTC_DOTATION = 8;
        public static final String TYPE_COL_VALORISATION_TTC_DOTATION = "INTEGER";
        public static final String CLE_COL_DOTATION_STD_DOTATION = "Dotation_Std";
        public static final int NUM_COL_DOTATION_STD_DOTATION = 9;
        public static final String TYPE_COL_DOTATION_STD_DOTATION = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_DOTATION = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_DOTATION = 10;
        public static final String TYPE_COL_COMMENTAIRE_DOTATION = "TEXT";
        public static final String CLE_COL_DEPOT_UID_DOTATION = "depot_UID";
        public static final int NUM_COL_DEPOT_UID_DOTATION = 11;
        public static final String TYPE_COL_DEPOT_UID_DOTATION = "INTEGER";
        public static final String CLE_COL_NB_PATIENTS_DOTATION = "nb_patients";
        public static final int NUM_COL_NB_PATIENTS_DOTATION = 12;
        public static final String TYPE_COL_NB_PATIENTS_DOTATION = "INTEGER";
        public static final String CLE_COL_TOURNEE_REFERENCE_DOTATION = "Tournee_Reference";
        public static final int NUM_COL_TOURNEE_REFERENCE_DOTATION = 13;
        public static final String TYPE_COL_TOURNEE_REFERENCE_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_DOTATION = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_DOTATION = 14;
        public static final String TYPE_COL_SYS_DT_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_DOTATION = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_DOTATION = 15;
        public static final String TYPE_COL_SYS_HEURE_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_DOTATION = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_DOTATION = 16;
        public static final String TYPE_COL_SYS_USER_MAJ_DOTATION = "TEXT";
        public static final String CLE_COL_TECH_UID_DOTATION = "tech_UID";
        public static final int NUM_COL_TECH_UID_DOTATION = 17;
        public static final String TYPE_COL_TECH_UID_DOTATION = "INTEGER";
        public static final String CLE_COL_URGENCE_DOTATION = "URGENCE";
        public static final int NUM_COL_URGENCE_DOTATION = 18;
        public static final String TYPE_COL_URGENCE_DOTATION = "INTEGER";
        public static final String CLE_COL_SECURISE_DOTATION = "SECURISE";
        public static final int NUM_COL_SECURISE_DOTATION = 19;
        public static final String TYPE_COL_SECURISE_DOTATION = "INTEGER";
        public static final String CLE_COL_TAUXSTOCKIDEAL_DOTATION = "TauxStockIdeal";
        public static final int NUM_COL_TAUXSTOCKIDEAL_DOTATION = 20;
        public static final String TYPE_COL_TAUXSTOCKIDEAL_DOTATION = "INTEGER";
        public static final String CLE_COL_INSTALLATION_DOTATION = "INSTALLATION";
        public static final int NUM_COL_INSTALLATION_DOTATION = 21;
        public static final String TYPE_COL_INSTALLATION_DOTATION = "INTEGER";
        public static final String CLE_COL_PLEINVIDE_DOTATION = "PLEINVIDE";
        public static final int NUM_COL_PLEINVIDE_DOTATION = 22;
        public static final String TYPE_COL_PLEINVIDE_DOTATION = "INTEGER";
        public static final String CLE_COL_PROTOCOLE_UID_DOTATION = "protocole_UID";
        public static final int NUM_COL_PROTOCOLE_UID_DOTATION = 23;
        public static final String TYPE_COL_PROTOCOLE_UID_DOTATION = "INTEGER";

        public static final String CLE_COL_COMMANDEAB_DOTATION = "commandeAB";
        public static final int NUM_COL_COMMANDEAB_DOTATION = 24;
        public static final String TYPE_COL_COMMANDEAB_DOTATION = "INTEGER";

        public static final String CREATION_TABLE_DOTATION = " CREATE TABLE       " + Constantes.TABLE_DOTATION
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__UID_DOTATION + " " + Constantes.TYPE_COL__UID_DOTATION + " , "
                + Constantes.CLE_COL_INTITULE_DOTATION + " " + Constantes.TYPE_COL_INTITULE_DOTATION + " , "
                + Constantes.CLE_COL_REF_DEPOT_DOTATION + " " + Constantes.TYPE_COL_REF_DEPOT_DOTATION + " , "
                + Constantes.CLE_COL_DEBUT_DOTATION + " " + Constantes.TYPE_COL_DEBUT_DOTATION + " , "
                + Constantes.CLE_COL_FIN_DOTATION + " " + Constantes.TYPE_COL_FIN_DOTATION + " , "
                + Constantes.CLE_COL_INTERROMPU_DOTATION + " " + Constantes.TYPE_COL_INTERROMPU_DOTATION + " , "
                + Constantes.CLE_COL_NB_SEMAINE_DOTATION + " " + Constantes.TYPE_COL_NB_SEMAINE_DOTATION + " , "
                + Constantes.CLE_COL_VALORISATION_TTC_DOTATION + " " + Constantes.TYPE_COL_VALORISATION_TTC_DOTATION + " , "
                + Constantes.CLE_COL_DOTATION_STD_DOTATION + " " + Constantes.TYPE_COL_DOTATION_STD_DOTATION + " , "
                + Constantes.CLE_COL_COMMENTAIRE_DOTATION + " " + Constantes.TYPE_COL_COMMENTAIRE_DOTATION + " , "
                + Constantes.CLE_COL_DEPOT_UID_DOTATION + " " + Constantes.TYPE_COL_DEPOT_UID_DOTATION + " , "
                + Constantes.CLE_COL_NB_PATIENTS_DOTATION + " " + Constantes.TYPE_COL_NB_PATIENTS_DOTATION + " , "
                + Constantes.CLE_COL_TOURNEE_REFERENCE_DOTATION + " " + Constantes.TYPE_COL_TOURNEE_REFERENCE_DOTATION + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_DT_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_DOTATION + " " + Constantes.TYPE_COL_SYS_USER_MAJ_DOTATION + " , "
                + Constantes.CLE_COL_TECH_UID_DOTATION + " " + Constantes.TYPE_COL_TECH_UID_DOTATION + " , "
                + Constantes.CLE_COL_URGENCE_DOTATION + " " + Constantes.TYPE_COL_URGENCE_DOTATION + " , "
                + Constantes.CLE_COL_SECURISE_DOTATION + " " + Constantes.TYPE_COL_SECURISE_DOTATION + " , "
                + Constantes.CLE_COL_TAUXSTOCKIDEAL_DOTATION + " " + Constantes.TYPE_COL_TAUXSTOCKIDEAL_DOTATION + " , "
                + Constantes.CLE_COL_INSTALLATION_DOTATION + " " + Constantes.TYPE_COL_INSTALLATION_DOTATION + " , "
                + Constantes.CLE_COL_PLEINVIDE_DOTATION + " " + Constantes.TYPE_COL_PLEINVIDE_DOTATION + " , "
                + Constantes.CLE_COL_PROTOCOLE_UID_DOTATION + " " + Constantes.TYPE_COL_PROTOCOLE_UID_DOTATION  + " , "
                + Constantes.CLE_COL_COMMANDEAB_DOTATION + " " + Constantes.TYPE_COL_COMMANDEAB_DOTATION
                + " ); ";

    }

    // ✅ Méthodes utilitaires (à ajouter dans DotationOpenHelper)
    private static void bindStringOrNull(SQLiteStatement stmt, int index, String value) {
        if (value != null) stmt.bindString(index, value);
        else stmt.bindNull(index);
    }
}

