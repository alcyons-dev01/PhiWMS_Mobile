package com.example.phiwms_mobile.BaseDeDonnees;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.phiwms_mobile.AuthentificationActivity;
import com.example.phiwms_mobile.Classes.Commande;
import com.example.phiwms_mobile.Classes.Depot;
import com.example.phiwms_mobile.Classes.PH_Reliquat;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.R;

import static com.example.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper.viderTablePH_Reliquat;
import static com.example.phiwms_mobile.OutilsSerialisation.WS_MIXED_BULK.handler;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class CommandeOpenHelper extends DBOpenHelper {

    public CommandeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Commande getCommandeTestAlcyons(SQLiteDatabase db)
    {
        Commande commande = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE + " WHERE " + Constantes.CLE_COL_NUMERO_COMMANDE + "=?", new String[]{"RECALCYONS01"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            commande = new Commande(cursor);
        }

        cursor.close();
        cursor = null;
        return commande;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        return db.delete(Constantes.TABLE_COMMANDE, Constantes.CLE_COL_NUMERO_COMMANDE + "=?", new String[]{"RECALCYONS01"});
    }

    public static long supprimerUneCommande(SQLiteDatabase db, Commande commande)
    {
        return db.delete(Constantes.TABLE_COMMANDE, Constantes.CLE_COL_NUMERO_COMMANDE + "=?", new String[]{commande.getNumero()});
    }

    public static long insererUneCommandeEnBDD(SQLiteDatabase db, Commande commande) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_COMMANDE_COMMANDE, commande.getID_commande());
        contentValues.put(Constantes.CLE_COL_NUMERO_COMMANDE, commande.getNumero());
        contentValues.put(Constantes.CLE_COL_ID_FRS_COMMANDE, commande.getID_Frs());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE, commande.getCommentaire());
        contentValues.put(Constantes.CLE_COL_MT_HT_COMMANDE, commande.getMt_ht());
        contentValues.put(Constantes.CLE_COL_MT_TVA_COMMANDE, commande.getMt_TVA());
        contentValues.put(Constantes.CLE_COL_TAUX_TVA_COMMANDE, commande.getTaux_TVA());
        contentValues.put(Constantes.CLE_COL_DATE_CDE_COMMANDE, commande.getDate_Cde());
        contentValues.put(Constantes.CLE_COL_DATE_LIV_COMMANDE, commande.getDate_Liv());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_COMMANDE, commande.getFournisseur());
        contentValues.put(Constantes.CLE_COL_VILLE_FRS_COMMANDE, commande.getVille_Frs());
        contentValues.put(Constantes.CLE_COL_DEVISE_COMMANDE, commande.getDevise());
        contentValues.put(Constantes.CLE_COL_FRAIS_DE_PORT_COMMANDE, commande.getFrais_de_port());
        contentValues.put(Constantes.CLE_COL_SITUATION_COMMANDE, commande.getSituation());
        contentValues.put(Constantes.CLE_COL_DATE_ECHEANCE_COMMANDE, commande.getDate_echeance());
        contentValues.put(Constantes.CLE_COL_MODALITES_COMMANDE, commande.getModalités());
        contentValues.put(Constantes.CLE_COL_FACTURE_DATE_COMMANDE, commande.getFacture_Date());
        contentValues.put(Constantes.CLE_COL_MT_TTC_COMMANDE, commande.getMt_TTC());
        contentValues.put(Constantes.CLE_COL_SITUATION2_COMMANDE, commande.getSituation2());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DEST_COMMANDE, commande.getRef_Depot_Dest());
        contentValues.put(Constantes.CLE_COL_VILLE_DEST_COMMANDE, commande.getVille_Frs());
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_COMMANDE, commande.getID_Depot());
        contentValues.put(Constantes.CLE_COL_STRUCT_DEPOT_COMMANDE, commande.getStruct_depot());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_COMMANDE, commande.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_COMMANDE, commande.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_COMMANDE, commande.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DELAI_LIVRAISON_COMMANDE, commande.getDelai_Livraison());
        contentValues.put(Constantes.CLE_COL_URGENT_COMMANDE, commande.getUrgent());
        contentValues.put(Constantes.CLE_COL_DATE_LIV2_COMMANDE, commande.getDate_Liv2());
        contentValues.put(Constantes.CLE_COL_CB_BON_COMMANDE_COMMANDE, commande.getCB_Bon_Commande());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_AUTRE_COMMANDE, commande.getLivraison_Autre());
        contentValues.put(Constantes.CLE_COL_DEPOT_ADRESSE_2_COMMANDE, commande.getDepot_adresse_2());
        contentValues.put(Constantes.CLE_COL_CODE_ANALYTIQUE_COMMANDE, commande.getCode_analytique());
        contentValues.put(Constantes.CLE_COL_PROTOCOLE_PATIENT_ID_COMMANDE, commande.getProtocole_Patient_ID());
        contentValues.put(Constantes.CLE_COL_PATIENT_IDENTITE_COMMANDE, commande.getPatient_identite());
        contentValues.put(Constantes.CLE_COL_PATIENT_IPP_COMMANDE, commande.getPatient_IPP());
        contentValues.put(Constantes.CLE_COL_LIVRERDATE_COMMANDE, commande.getLivrerDate());
        contentValues.put(Constantes.CLE_COL_BLNUMERO_COMMANDE, commande.getBLNumero());
        contentValues.put(Constantes.CLE_COL_FACTUREDATE_COMMANDE, commande.getFacture_Date());
        contentValues.put(Constantes.CLE_COL_FACTURENUMERO_COMMANDE, commande.getFactureNumero());
        contentValues.put(Constantes.CLE_COL_NBCOLISTOTAL_CE_COMMANDE, commande.getNbColisTotal_CE());
        contentValues.put(Constantes.CLE_COL_NBPALETTETOTAL_CE_COMMANDE, commande.getNbPaletteTotal_CE());
        contentValues.put(Constantes.CLE_COL_POIDSTOTAL_CE_COMMANDE, commande.getPoidsTotal_CE());
        contentValues.put(Constantes.CLE_COL_AVALISER_PAR_USERINITIALE_COMMANDE, commande.getAvaliser_Par_UserInitiale());
        contentValues.put(Constantes.CLE_COL_AVALISER_PAR_USERID_COMMANDE, commande.getAvaliser_Par_UserID());
        contentValues.put(Constantes.CLE_COL_AVALISER_LE_COMMANDE, commande.getAvaliser_Le());
        contentValues.put(Constantes.CLE_COL_VOLUME_TOTAL_COMMANDE, commande.getVolume_Total());
        contentValues.put(Constantes.CLE_COL_IMPORT_COMMANDE, commande.getImport());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_METROPOLE_COMMANDE, commande.getTransitaire_Metropole());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_LOCAL_COMMANDE, commande.getTransitaire_Local());
        contentValues.put(Constantes.CLE_COL_TRANSPORT_TYPE_COMMANDE, commande.getTransport_Type());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_COMMANDE, null, contentValues);

        commande.setPhiMR4UUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourUneCommande(SQLiteDatabase db, Commande commande) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_COMMANDE_COMMANDE, commande.getID_commande());
        contentValues.put(Constantes.CLE_COL_NUMERO_COMMANDE, commande.getNumero());
        contentValues.put(Constantes.CLE_COL_ID_FRS_COMMANDE, commande.getID_Frs());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE, commande.getCommentaire());
        contentValues.put(Constantes.CLE_COL_MT_HT_COMMANDE, commande.getMt_ht());
        contentValues.put(Constantes.CLE_COL_MT_TVA_COMMANDE, commande.getMt_TVA());
        contentValues.put(Constantes.CLE_COL_TAUX_TVA_COMMANDE, commande.getTaux_TVA());
        contentValues.put(Constantes.CLE_COL_DATE_CDE_COMMANDE, commande.getDate_Cde());
        contentValues.put(Constantes.CLE_COL_DATE_LIV_COMMANDE, commande.getDate_Liv());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_COMMANDE, commande.getFournisseur());
        contentValues.put(Constantes.CLE_COL_VILLE_FRS_COMMANDE, commande.getVille_Frs());
        contentValues.put(Constantes.CLE_COL_DEVISE_COMMANDE, commande.getDevise());
        contentValues.put(Constantes.CLE_COL_FRAIS_DE_PORT_COMMANDE, commande.getFrais_de_port());
        contentValues.put(Constantes.CLE_COL_SITUATION_COMMANDE, commande.getSituation());
        contentValues.put(Constantes.CLE_COL_DATE_ECHEANCE_COMMANDE, commande.getDate_echeance());
        contentValues.put(Constantes.CLE_COL_MODALITES_COMMANDE, commande.getModalités());
        contentValues.put(Constantes.CLE_COL_FACTURE_DATE_COMMANDE, commande.getFacture_Date());
        contentValues.put(Constantes.CLE_COL_MT_TTC_COMMANDE, commande.getMt_TTC());
        contentValues.put(Constantes.CLE_COL_SITUATION2_COMMANDE, commande.getSituation2());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_DEST_COMMANDE, commande.getRef_Depot_Dest());
        contentValues.put(Constantes.CLE_COL_VILLE_DEST_COMMANDE, commande.getVille_Frs());
        contentValues.put(Constantes.CLE_COL_ID_DEPOT_COMMANDE, commande.getID_Depot());
        contentValues.put(Constantes.CLE_COL_STRUCT_DEPOT_COMMANDE, commande.getStruct_depot());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_COMMANDE, commande.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_COMMANDE, commande.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_COMMANDE, commande.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DELAI_LIVRAISON_COMMANDE, commande.getDelai_Livraison());
        contentValues.put(Constantes.CLE_COL_URGENT_COMMANDE, commande.getUrgent());
        contentValues.put(Constantes.CLE_COL_DATE_LIV2_COMMANDE, commande.getDate_Liv2());
        contentValues.put(Constantes.CLE_COL_CB_BON_COMMANDE_COMMANDE, commande.getCB_Bon_Commande());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_AUTRE_COMMANDE, commande.getLivraison_Autre());
        contentValues.put(Constantes.CLE_COL_DEPOT_ADRESSE_2_COMMANDE, commande.getDepot_adresse_2());
        contentValues.put(Constantes.CLE_COL_CODE_ANALYTIQUE_COMMANDE, commande.getCode_analytique());
        contentValues.put(Constantes.CLE_COL_PROTOCOLE_PATIENT_ID_COMMANDE, commande.getProtocole_Patient_ID());
        contentValues.put(Constantes.CLE_COL_PATIENT_IDENTITE_COMMANDE, commande.getPatient_identite());
        contentValues.put(Constantes.CLE_COL_PATIENT_IPP_COMMANDE, commande.getPatient_IPP());
        contentValues.put(Constantes.CLE_COL_LIVRERDATE_COMMANDE, commande.getLivrerDate());
        contentValues.put(Constantes.CLE_COL_BLNUMERO_COMMANDE, commande.getBLNumero());
        contentValues.put(Constantes.CLE_COL_FACTUREDATE_COMMANDE, commande.getFacture_Date());
        contentValues.put(Constantes.CLE_COL_FACTURENUMERO_COMMANDE, commande.getFactureNumero());
        contentValues.put(Constantes.CLE_COL_NBCOLISTOTAL_CE_COMMANDE, commande.getNbColisTotal_CE());
        contentValues.put(Constantes.CLE_COL_NBPALETTETOTAL_CE_COMMANDE, commande.getNbPaletteTotal_CE());
        contentValues.put(Constantes.CLE_COL_POIDSTOTAL_CE_COMMANDE, commande.getPoidsTotal_CE());
        contentValues.put(Constantes.CLE_COL_AVALISER_PAR_USERINITIALE_COMMANDE, commande.getAvaliser_Par_UserInitiale());
        contentValues.put(Constantes.CLE_COL_AVALISER_PAR_USERID_COMMANDE, commande.getAvaliser_Par_UserID());
        contentValues.put(Constantes.CLE_COL_AVALISER_LE_COMMANDE, commande.getAvaliser_Le());
        contentValues.put(Constantes.CLE_COL_VOLUME_TOTAL_COMMANDE, commande.getVolume_Total());
        contentValues.put(Constantes.CLE_COL_IMPORT_COMMANDE, commande.getImport());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_METROPOLE_COMMANDE, commande.getTransitaire_Metropole());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_LOCAL_COMMANDE, commande.getTransitaire_Local());
        contentValues.put(Constantes.CLE_COL_TRANSPORT_TYPE_COMMANDE, commande.getTransport_Type());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID, commande.getPhiMR4UUID());

        return db.update(Constantes.TABLE_COMMANDE, contentValues, DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=" + commande.getPhiMR4UUID(), null);

    }

    public static void insererBDDLocaleCommandeReceptionPUI(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
        String urlRequete = null;
        try {
            urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUI.getDepot_Reference(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.equals("Aucun PH_Commande trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleCommandes";
                                    etat = false;
                                }
                            } else {
                                JSONArray commandeJSONArray = response.getJSONArray("PH_Commandes");
                                viderTableCommandes(db);
                                viderTablePH_Reliquat(db);

                                for (int i = 0; i < commandeJSONArray.length(); i++) {
                                    JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);

                                    Commande commandeCourant = new Commande(commandeJSONObject);

                                    JSONArray phReliquatJSONArray = commandeJSONObject.getJSONArray("ph_reliquat");

                                    boolean phReliquatPresent = false;

                                    for (int j = 0; j < phReliquatJSONArray.length(); j++) {

                                        PH_Reliquat reliquatCourant = new PH_Reliquat((phReliquatJSONArray.getJSONObject(j)));


                                        long phReliquatPHiMR4ID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquatCourant);
                                        if (phReliquatPHiMR4ID != -1) {
                                            phReliquatPresent = true;
                                        }
                                    }

                                    if (phReliquatPresent) {
                                        long rowID = insererUneCommandeEnBDD(db, commandeCourant);
                                    }
                                }
                            }

                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleCommandes", "alerte");
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
        requestQueue.add(obreq);
    }

    public static void insererBDDLocaleCommandeReceptionScannee(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur) {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
        String urlRequete = null;
        try {
            urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUI.getDepot_Reference(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.equals("Aucun PH_Commande trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleCommandes";
                                    etat = false;
                                }
                            } else {
                                JSONArray commandeJSONArray = response.getJSONArray("PH_Commandes");
                                viderTableCommandes(db);
                                viderTablePH_Reliquat(db);

                                for (int i = 0; i < commandeJSONArray.length(); i++) {
                                    JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);

                                    Commande commandeCourant = new Commande(commandeJSONObject);

                                    JSONArray phReliquatJSONArray = commandeJSONObject.getJSONArray("ph_reliquat");

                                    boolean phReliquatPresent = false;

                                    for (int j = 0; j < phReliquatJSONArray.length(); j++) {

                                        PH_Reliquat reliquatCourant = new PH_Reliquat((phReliquatJSONArray.getJSONObject(j)));


                                        long phReliquatPHiMR4ID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquatCourant);
                                        if (phReliquatPHiMR4ID != -1) {
                                            phReliquatPresent = true;
                                        }
                                    }

                                    if (phReliquatPresent) {
                                        long rowID = insererUneCommandeEnBDD(db, commandeCourant);
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleCommandes", "alerte");
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
        requestQueue.add(obreq);
    }

    public static void insererBDDLocaleCommandeReceptionPAD(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur)
    {
        if (!OutilsGestionConnexionReseau.isServerAccessible(context)) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        Depot depotPUIPAD = DepotOpenHelper.getDepotPUIPAD(db);
        String urlRequete = null;
        try {
            urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteCommandes + "depotreference/" + URLEncoder.encode(depotPUIPAD.getDepot_Reference(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String erreur = "";
                            boolean etat = true;
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    etat = false;
                                } else if (!erreur.equals("Aucun PH_Commande trouvé")) {
                                    erreur = "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocaleCommandes";
                                    etat = false;
                                }
                            } else {
                                JSONArray commandeJSONArray = response.getJSONArray("PH_Commandes");
                                viderTableCommandes(db);
                                viderTablePH_Reliquat(db);

                                for (int i = 0; i < commandeJSONArray.length(); i++) {
                                    JSONObject commandeJSONObject = commandeJSONArray.getJSONObject(i);

                                    Commande commandeCourant = new Commande(commandeJSONObject);

                                    JSONArray phReliquatJSONArray = commandeJSONObject.getJSONArray("ph_reliquat");

                                    boolean phReliquatPresent = false;

                                    for (int j = 0; j < phReliquatJSONArray.length(); j++) {

                                        PH_Reliquat reliquatCourant = new PH_Reliquat((phReliquatJSONArray.getJSONObject(j)));


                                        long phReliquatPHiMR4ID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquatCourant);
                                        if (phReliquatPHiMR4ID != -1) {
                                            phReliquatPresent = true;
                                        }
                                    }

                                    if (phReliquatPresent) {
                                        long rowID = insererUneCommandeEnBDD(db, commandeCourant);
                                    }
                                }
                            }
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                // ((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                            }
                            else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(etat, erreur);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocaleCommandes", "alerte");
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
        requestQueue.add(obreq);
    }
    public static void viderTableCommandes(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_COMMANDE, null, null);
    }

    public static List<Commande> getAllCommandesPUI(SQLiteDatabase db, String refDepot) {
        List<Commande> commandeList = new ArrayList<>();
        String situation1 = "E";
        String situation2 = "L";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE, null);
        while (cursor.moveToNext()) {
            String situationCursor = cursor.getString(Constantes.NUM_COL_SITUATION_COMMANDE);
            if (situationCursor.equals(situation1) || situationCursor.equals(situation2)) {
                if (cursor.getString(Constantes.NUM_COL_REF_DEPOT_DEST_COMMANDE).equals(refDepot)) {
                    commandeList.add(new Commande(cursor));
                }
            }
        }
        cursor.close();
        cursor = null;

        return commandeList;

    }

    public static Commande getCommandeByID(SQLiteDatabase db, int id) {
        Commande commande = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE + " WHERE " + Constantes.CLE_COL_ID_COMMANDE_COMMANDE + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            commande = new Commande(cursor);
        }

        cursor.close();
        cursor = null;
        return commande;
    }

    public static Commande getCommandeByNumero(SQLiteDatabase db, String numero) {
        Commande commande = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE + " WHERE " + Constantes.CLE_COL_NUMERO_COMMANDE + "=?", new String[]{numero});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            commande = new Commande(cursor);
        }

        cursor.close();
        cursor = null;
        return commande;
    }

    public static Commande getCommandeByPhiMR4UUID(SQLiteDatabase db, int phimr4uuid) {
        Commande commande = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE + " WHERE " + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + "=?", new String[]{String.valueOf(phimr4uuid)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            commande = new Commande(cursor);
        }

        cursor.close();
        cursor = null;
        return commande;
    }

    public static List<Commande> getAllCommandes(SQLiteDatabase db) {
        List<Commande> commandeList = new ArrayList<>();


        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_COMMANDE, null);
        while (cursor.moveToNext()) {
            commandeList.add(new Commande(cursor));
        }
        cursor.close();
        cursor = null;

        return commandeList;

    }

    public static List<Commande> getCommandeByProduit(SQLiteDatabase db, List<Integer> liste_id_produit) {
        List<Commande> liste_commande_produit_scan = new ArrayList<>();
        List<Commande> liste_commande = getAllCommandes(db);

        for(Commande commande_courante : liste_commande)
        {
            List<PH_Reliquat> liste_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commande_courante.getNumero());
            int nb_present = 0;
            for(PH_Reliquat reliquat_courant : liste_reliquat)
            {
                for(Integer id_produit_courant : liste_id_produit)
                {
                    if(id_produit_courant == reliquat_courant.getProduitID())
                    {
                        nb_present ++;
                    }
                }
            }

            if(nb_present == liste_id_produit.size())
                liste_commande_produit_scan.add(commande_courante);
        }

        return liste_commande_produit_scan;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_COMMANDE = "Commande";

        public static final String CLE_COL_NUMERO_COMMANDE = "Numero";
        public static final int NUM_COL_NUMERO_COMMANDE = 1;
        public static final String TYPE_COL_NUMERO_COMMANDE = "TEXT";
        public static final String CLE_COL_ID_FRS_COMMANDE = "ID_Frs";
        public static final int NUM_COL_ID_FRS_COMMANDE = 2;
        public static final String TYPE_COL_ID_FRS_COMMANDE = "INTEGER";
        public static final String CLE_COL_COMMENTAIRE_COMMANDE = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_COMMANDE = 3;
        public static final String TYPE_COL_COMMENTAIRE_COMMANDE = "TEXT";
        public static final String CLE_COL_MT_HT_COMMANDE = "Mt_ht";
        public static final int NUM_COL_MT_HT_COMMANDE = 4;
        public static final String TYPE_COL_MT_HT_COMMANDE = "REAL";
        public static final String CLE_COL_MT_TVA_COMMANDE = "Mt_TVA";
        public static final int NUM_COL_MT_TVA_COMMANDE = 5;
        public static final String TYPE_COL_MT_TVA_COMMANDE = "REAL";
        public static final String CLE_COL_TAUX_TVA_COMMANDE = "Taux_TVA";
        public static final int NUM_COL_TAUX_TVA_COMMANDE = 6;
        public static final String TYPE_COL_TAUX_TVA_COMMANDE = "REAL";
        public static final String CLE_COL_DATE_CDE_COMMANDE = "Date_Cde";
        public static final int NUM_COL_DATE_CDE_COMMANDE = 7;
        public static final String TYPE_COL_DATE_CDE_COMMANDE = "TEXT";
        public static final String CLE_COL_DATE_LIV_COMMANDE = "Date_Liv";
        public static final int NUM_COL_DATE_LIV_COMMANDE = 8;
        public static final String TYPE_COL_DATE_LIV_COMMANDE = "TEXT";
        public static final String CLE_COL_FOURNISSEUR_COMMANDE = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_COMMANDE = 9;
        public static final String TYPE_COL_FOURNISSEUR_COMMANDE = "TEXT";
        public static final String CLE_COL_VILLE_FRS_COMMANDE = "Ville_Frs";
        public static final int NUM_COL_VILLE_FRS_COMMANDE = 10;
        public static final String TYPE_COL_VILLE_FRS_COMMANDE = "TEXT";
        public static final String CLE_COL_DEVISE_COMMANDE = "Devise";
        public static final int NUM_COL_DEVISE_COMMANDE = 11;
        public static final String TYPE_COL_DEVISE_COMMANDE = "TEXT";
        public static final String CLE_COL_FRAIS_DE_PORT_COMMANDE = "Frais_de_port";
        public static final int NUM_COL_FRAIS_DE_PORT_COMMANDE = 12;
        public static final String TYPE_COL_FRAIS_DE_PORT_COMMANDE = "REAL";
        public static final String CLE_COL_SITUATION_COMMANDE = "Situation";
        public static final int NUM_COL_SITUATION_COMMANDE = 13;
        public static final String TYPE_COL_SITUATION_COMMANDE = "TEXT";
        public static final String CLE_COL_DATE_ECHEANCE_COMMANDE = "Date_echeance";
        public static final int NUM_COL_DATE_ECHEANCE_COMMANDE = 14;
        public static final String TYPE_COL_DATE_ECHEANCE_COMMANDE = "TEXT";
        public static final String CLE_COL_MODALITES_COMMANDE = "Modalités";
        public static final int NUM_COL_MODALITES_COMMANDE = 15;
        public static final String TYPE_COL_MODALITES_COMMANDE = "TEXT";
        public static final String CLE_COL_FACTURE_DATE_COMMANDE = "Facture_Date";
        public static final int NUM_COL_FACTURE_DATE_COMMANDE = 16;
        public static final String TYPE_COL_FACTURE_DATE_COMMANDE = "TEXT";
        public static final String CLE_COL_MT_TTC_COMMANDE = "Mt_TTC";
        public static final int NUM_COL_MT_TTC_COMMANDE = 17;
        public static final String TYPE_COL_MT_TTC_COMMANDE = "REAL";
        public static final String CLE_COL_SITUATION2_COMMANDE = "Situation2";
        public static final int NUM_COL_SITUATION2_COMMANDE = 18;
        public static final String TYPE_COL_SITUATION2_COMMANDE = "TEXT";
        public static final String CLE_COL_REF_DEPOT_DEST_COMMANDE = "Ref_Depot_Dest";
        public static final int NUM_COL_REF_DEPOT_DEST_COMMANDE = 19;
        public static final String TYPE_COL_REF_DEPOT_DEST_COMMANDE = "TEXT";
        public static final String CLE_COL_VILLE_DEST_COMMANDE = "Ville_Dest";
        public static final int NUM_COL_VILLE_DEST_COMMANDE = 20;
        public static final String TYPE_COL_VILLE_DEST_COMMANDE = "TEXT";
        public static final String CLE_COL_ID_DEPOT_COMMANDE = "ID_Depot";
        public static final int NUM_COL_ID_DEPOT_COMMANDE = 21;
        public static final String TYPE_COL_ID_DEPOT_COMMANDE = "INTEGER";
        public static final String CLE_COL_STRUCT_DEPOT_COMMANDE = "Struct_depot";
        public static final int NUM_COL_STRUCT_DEPOT_COMMANDE = 22;
        public static final String TYPE_COL_STRUCT_DEPOT_COMMANDE = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_COMMANDE = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_COMMANDE = 23;
        public static final String TYPE_COL_SYS_DT_MAJ_COMMANDE = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_COMMANDE = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_COMMANDE = 24;
        public static final String TYPE_COL_SYS_HEURE_MAJ_COMMANDE = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_COMMANDE = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_COMMANDE = 25;
        public static final String TYPE_COL_SYS_USER_MAJ_COMMANDE = "TEXT";
        public static final String CLE_COL_DELAI_LIVRAISON_COMMANDE = "Delai_Livraison";
        public static final int NUM_COL_DELAI_LIVRAISON_COMMANDE = 26;
        public static final String TYPE_COL_DELAI_LIVRAISON_COMMANDE = "INTEGER";
        public static final String CLE_COL_URGENT_COMMANDE = "Urgent";
        public static final int NUM_COL_URGENT_COMMANDE = 27;
        public static final String TYPE_COL_URGENT_COMMANDE = "INTEGER";
        public static final String CLE_COL_DATE_LIV2_COMMANDE = "Date_Liv2";
        public static final int NUM_COL_DATE_LIV2_COMMANDE = 28;
        public static final String TYPE_COL_DATE_LIV2_COMMANDE = "TEXT";
        public static final String CLE_COL_CB_BON_COMMANDE_COMMANDE = "CB_Bon_Commande";
        public static final int NUM_COL_CB_BON_COMMANDE_COMMANDE = 29;
        public static final String TYPE_COL_CB_BON_COMMANDE_COMMANDE = "TEXT";
        public static final String CLE_COL_LIVRAISON_AUTRE_COMMANDE = "Livraison_Autre";
        public static final int NUM_COL_LIVRAISON_AUTRE_COMMANDE = 30;
        public static final String TYPE_COL_LIVRAISON_AUTRE_COMMANDE = "TEXT";
        public static final String CLE_COL_DEPOT_ADRESSE_2_COMMANDE = "Depot_adresse_2";
        public static final int NUM_COL_DEPOT_ADRESSE_2_COMMANDE = 31;
        public static final String TYPE_COL_DEPOT_ADRESSE_2_COMMANDE = "INTEGER";
        public static final String CLE_COL_CODE_ANALYTIQUE_COMMANDE = "Code_analytique";
        public static final int NUM_COL_CODE_ANALYTIQUE_COMMANDE = 32;
        public static final String TYPE_COL_CODE_ANALYTIQUE_COMMANDE = "TEXT";
        public static final String CLE_COL_PROTOCOLE_PATIENT_ID_COMMANDE = "Protocole_Patient_ID";
        public static final int NUM_COL_PROTOCOLE_PATIENT_ID_COMMANDE = 33;
        public static final String TYPE_COL_PROTOCOLE_PATIENT_ID_COMMANDE = "INTEGER";
        public static final String CLE_COL_PATIENT_IDENTITE_COMMANDE = "Patient_identite";
        public static final int NUM_COL_PATIENT_IDENTITE_COMMANDE = 34;
        public static final String TYPE_COL_PATIENT_IDENTITE_COMMANDE = "TEXT";
        public static final String CLE_COL_PATIENT_IPP_COMMANDE = "Patient_IPP";
        public static final int NUM_COL_PATIENT_IPP_COMMANDE = 35;
        public static final String TYPE_COL_PATIENT_IPP_COMMANDE = "TEXT";
        public static final String CLE_COL_LIVRERDATE_COMMANDE = "LivrerDate";
        public static final int NUM_COL_LIVRERDATE_COMMANDE = 36;
        public static final String TYPE_COL_LIVRERDATE_COMMANDE = "TEXT";
        public static final String CLE_COL_BLNUMERO_COMMANDE = "BLNumero";
        public static final int NUM_COL_BLNUMERO_COMMANDE = 37;
        public static final String TYPE_COL_BLNUMERO_COMMANDE = "TEXT";
        public static final String CLE_COL_FACTUREDATE_COMMANDE = "FactureDate";
        public static final int NUM_COL_FACTUREDATE_COMMANDE = 38;
        public static final String TYPE_COL_FACTUREDATE_COMMANDE = "TEXT";
        public static final String CLE_COL_FACTURENUMERO_COMMANDE = "FactureNumero";
        public static final int NUM_COL_FACTURENUMERO_COMMANDE = 39;
        public static final String TYPE_COL_FACTURENUMERO_COMMANDE = "TEXT";
        public static final String CLE_COL_NBCOLISTOTAL_CE_COMMANDE = "NbColisTotal_CE";
        public static final int NUM_COL_NBCOLISTOTAL_CE_COMMANDE = 40;
        public static final String TYPE_COL_NBCOLISTOTAL_CE_COMMANDE = "INTEGER";
        public static final String CLE_COL_NBPALETTETOTAL_CE_COMMANDE = "NbPaletteTotal_CE";
        public static final int NUM_COL_NBPALETTETOTAL_CE_COMMANDE = 41;
        public static final String TYPE_COL_NBPALETTETOTAL_CE_COMMANDE = "INTEGER";
        public static final String CLE_COL_POIDSTOTAL_CE_COMMANDE = "PoidsTotal_CE";
        public static final int NUM_COL_POIDSTOTAL_CE_COMMANDE = 42;
        public static final String TYPE_COL_POIDSTOTAL_CE_COMMANDE = "INTEGER";
        public static final String CLE_COL_AVALISER_PAR_USERINITIALE_COMMANDE = "Avaliser_Par_UserInitiale";
        public static final int NUM_COL_AVALISER_PAR_USERINITIALE_COMMANDE = 43;
        public static final String TYPE_COL_AVALISER_PAR_USERINITIALE_COMMANDE = "TEXT";
        public static final String CLE_COL_AVALISER_PAR_USERID_COMMANDE = "Avaliser_Par_UserID";
        public static final int NUM_COL_AVALISER_PAR_USERID_COMMANDE = 44;
        public static final String TYPE_COL_AVALISER_PAR_USERID_COMMANDE = "INTEGER";
        public static final String CLE_COL_AVALISER_LE_COMMANDE = "Avaliser_Le";
        public static final int NUM_COL_AVALISER_LE_COMMANDE = 45;
        public static final String TYPE_COL_AVALISER_LE_COMMANDE = "TEXT";
        public static final String CLE_COL_VOLUME_TOTAL_COMMANDE = "Volume_Total";
        public static final int NUM_COL_VOLUME_TOTAL_COMMANDE = 46;
        public static final String TYPE_COL_VOLUME_TOTAL_COMMANDE = "INTEGER";
        public static final String CLE_COL_IMPORT_COMMANDE = "Import";
        public static final int NUM_COL_IMPORT_COMMANDE = 47;
        public static final String TYPE_COL_IMPORT_COMMANDE = "INTEGER";
        public static final String CLE_COL_TRANSITAIRE_METROPOLE_COMMANDE = "Transitaire_Metropole";
        public static final int NUM_COL_TRANSITAIRE_METROPOLE_COMMANDE = 48;
        public static final String TYPE_COL_TRANSITAIRE_METROPOLE_COMMANDE = "TEXT";
        public static final String CLE_COL_TRANSITAIRE_LOCAL_COMMANDE = "Transitaire_Local";
        public static final int NUM_COL_TRANSITAIRE_LOCAL_COMMANDE = 49;
        public static final String TYPE_COL_TRANSITAIRE_LOCAL_COMMANDE = "TEXT";
        public static final String CLE_COL_TRANSPORT_TYPE_COMMANDE = "Transport_Type";
        public static final int NUM_COL_TRANSPORT_TYPE_COMMANDE = 50;
        public static final String TYPE_COL_TRANSPORT_TYPE_COMMANDE = "TEXT";
        public static final String CLE_COL_ID_COMMANDE_COMMANDE = "ID_commande";
        public static final int NUM_COL_ID_COMMANDE_COMMANDE = 51;
        public static final String TYPE_COL_ID_COMMANDE_COMMANDE = "INTEGER";


        public static final String CREATION_TABLE_COMMANDE = "CREATE TABLE " +
                Constantes.TABLE_COMMANDE + "("
                + DBOpenHelper.Constantes.CLE_COL_PHIMR4UUID + " " + DBOpenHelper.Constantes.TYPE_COL_PHIMR4UUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_NUMERO_COMMANDE + " " + Constantes.TYPE_COL_NUMERO_COMMANDE + " ,"
                + Constantes.CLE_COL_ID_FRS_COMMANDE + " " + Constantes.TYPE_COL_ID_FRS_COMMANDE + " ,"
                + Constantes.CLE_COL_COMMENTAIRE_COMMANDE + " " + Constantes.TYPE_COL_COMMENTAIRE_COMMANDE + " ,"
                + Constantes.CLE_COL_MT_HT_COMMANDE + " " + Constantes.TYPE_COL_MT_HT_COMMANDE + " ,"
                + Constantes.CLE_COL_MT_TVA_COMMANDE + " " + Constantes.TYPE_COL_MT_TVA_COMMANDE + " ,"
                + Constantes.CLE_COL_TAUX_TVA_COMMANDE + " " + Constantes.TYPE_COL_TAUX_TVA_COMMANDE + " ,"
                + Constantes.CLE_COL_DATE_CDE_COMMANDE + " " + Constantes.TYPE_COL_DATE_CDE_COMMANDE + " ,"
                + Constantes.CLE_COL_DATE_LIV_COMMANDE + " " + Constantes.TYPE_COL_DATE_LIV_COMMANDE + " ,"
                + Constantes.CLE_COL_FOURNISSEUR_COMMANDE + " " + Constantes.TYPE_COL_FOURNISSEUR_COMMANDE + " ,"
                + Constantes.CLE_COL_VILLE_FRS_COMMANDE + " " + Constantes.TYPE_COL_VILLE_FRS_COMMANDE + " ,"
                + Constantes.CLE_COL_DEVISE_COMMANDE + " " + Constantes.TYPE_COL_DEVISE_COMMANDE + " ,"
                + Constantes.CLE_COL_FRAIS_DE_PORT_COMMANDE + " " + Constantes.TYPE_COL_FRAIS_DE_PORT_COMMANDE + " ,"
                + Constantes.CLE_COL_SITUATION_COMMANDE + " " + Constantes.TYPE_COL_SITUATION_COMMANDE + " ,"
                + Constantes.CLE_COL_DATE_ECHEANCE_COMMANDE + " " + Constantes.TYPE_COL_DATE_ECHEANCE_COMMANDE + " ,"
                + Constantes.CLE_COL_MODALITES_COMMANDE + " " + Constantes.TYPE_COL_MODALITES_COMMANDE + " ,"
                + Constantes.CLE_COL_FACTURE_DATE_COMMANDE + " " + Constantes.TYPE_COL_FACTURE_DATE_COMMANDE + " ,"
                + Constantes.CLE_COL_MT_TTC_COMMANDE + " " + Constantes.TYPE_COL_MT_TTC_COMMANDE + " ,"
                + Constantes.CLE_COL_SITUATION2_COMMANDE + " " + Constantes.TYPE_COL_SITUATION2_COMMANDE + " ,"
                + Constantes.CLE_COL_REF_DEPOT_DEST_COMMANDE + " " + Constantes.TYPE_COL_REF_DEPOT_DEST_COMMANDE + " ,"
                + Constantes.CLE_COL_VILLE_DEST_COMMANDE + " " + Constantes.TYPE_COL_VILLE_DEST_COMMANDE + " ,"
                + Constantes.CLE_COL_ID_DEPOT_COMMANDE + " " + Constantes.TYPE_COL_ID_DEPOT_COMMANDE + " ,"
                + Constantes.CLE_COL_STRUCT_DEPOT_COMMANDE + " " + Constantes.TYPE_COL_STRUCT_DEPOT_COMMANDE + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_COMMANDE + " " + Constantes.TYPE_COL_SYS_DT_MAJ_COMMANDE + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_COMMANDE + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_COMMANDE + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_COMMANDE + " " + Constantes.TYPE_COL_SYS_USER_MAJ_COMMANDE + " ,"
                + Constantes.CLE_COL_DELAI_LIVRAISON_COMMANDE + " " + Constantes.TYPE_COL_DELAI_LIVRAISON_COMMANDE + " ,"
                + Constantes.CLE_COL_URGENT_COMMANDE + " " + Constantes.TYPE_COL_URGENT_COMMANDE + " ,"
                + Constantes.CLE_COL_DATE_LIV2_COMMANDE + " " + Constantes.TYPE_COL_DATE_LIV2_COMMANDE + " ,"
                + Constantes.CLE_COL_CB_BON_COMMANDE_COMMANDE + " " + Constantes.TYPE_COL_CB_BON_COMMANDE_COMMANDE + " ,"
                + Constantes.CLE_COL_LIVRAISON_AUTRE_COMMANDE + " " + Constantes.TYPE_COL_LIVRAISON_AUTRE_COMMANDE + " ,"
                + Constantes.CLE_COL_DEPOT_ADRESSE_2_COMMANDE + " " + Constantes.TYPE_COL_DEPOT_ADRESSE_2_COMMANDE + " ,"
                + Constantes.CLE_COL_CODE_ANALYTIQUE_COMMANDE + " " + Constantes.TYPE_COL_CODE_ANALYTIQUE_COMMANDE + " ,"
                + Constantes.CLE_COL_PROTOCOLE_PATIENT_ID_COMMANDE + " " + Constantes.TYPE_COL_PROTOCOLE_PATIENT_ID_COMMANDE + " ,"
                + Constantes.CLE_COL_PATIENT_IDENTITE_COMMANDE + " " + Constantes.TYPE_COL_PATIENT_IDENTITE_COMMANDE + " ,"
                + Constantes.CLE_COL_PATIENT_IPP_COMMANDE + " " + Constantes.TYPE_COL_PATIENT_IPP_COMMANDE + " ,"
                + Constantes.CLE_COL_LIVRERDATE_COMMANDE + " " + Constantes.TYPE_COL_LIVRERDATE_COMMANDE + " ,"
                + Constantes.CLE_COL_BLNUMERO_COMMANDE + " " + Constantes.TYPE_COL_BLNUMERO_COMMANDE + " ,"
                + Constantes.CLE_COL_FACTUREDATE_COMMANDE + " " + Constantes.TYPE_COL_FACTUREDATE_COMMANDE + " ,"
                + Constantes.CLE_COL_FACTURENUMERO_COMMANDE + " " + Constantes.TYPE_COL_FACTURENUMERO_COMMANDE + " ,"
                + Constantes.CLE_COL_NBCOLISTOTAL_CE_COMMANDE + " " + Constantes.TYPE_COL_NBCOLISTOTAL_CE_COMMANDE + " ,"
                + Constantes.CLE_COL_NBPALETTETOTAL_CE_COMMANDE + " " + Constantes.TYPE_COL_NBPALETTETOTAL_CE_COMMANDE + " ,"
                + Constantes.CLE_COL_POIDSTOTAL_CE_COMMANDE + " " + Constantes.TYPE_COL_POIDSTOTAL_CE_COMMANDE + " ,"
                + Constantes.CLE_COL_AVALISER_PAR_USERINITIALE_COMMANDE + " " + Constantes.TYPE_COL_AVALISER_PAR_USERINITIALE_COMMANDE + " ,"
                + Constantes.CLE_COL_AVALISER_PAR_USERID_COMMANDE + " " + Constantes.TYPE_COL_AVALISER_PAR_USERID_COMMANDE + " ,"
                + Constantes.CLE_COL_AVALISER_LE_COMMANDE + " " + Constantes.TYPE_COL_AVALISER_LE_COMMANDE + " ,"
                + Constantes.CLE_COL_VOLUME_TOTAL_COMMANDE + " " + Constantes.TYPE_COL_VOLUME_TOTAL_COMMANDE + " ,"
                + Constantes.CLE_COL_IMPORT_COMMANDE + " " + Constantes.TYPE_COL_IMPORT_COMMANDE + " ,"
                + Constantes.CLE_COL_TRANSITAIRE_METROPOLE_COMMANDE + " " + Constantes.TYPE_COL_TRANSITAIRE_METROPOLE_COMMANDE + " ,"
                + Constantes.CLE_COL_TRANSITAIRE_LOCAL_COMMANDE + " " + Constantes.TYPE_COL_TRANSITAIRE_LOCAL_COMMANDE + " ,"
                + Constantes.CLE_COL_TRANSPORT_TYPE_COMMANDE + " " + Constantes.TYPE_COL_TRANSPORT_TYPE_COMMANDE + ","
                + Constantes.CLE_COL_ID_COMMANDE_COMMANDE + " " + Constantes.TYPE_COL_ID_COMMANDE_COMMANDE
                + ");";
    }
}
