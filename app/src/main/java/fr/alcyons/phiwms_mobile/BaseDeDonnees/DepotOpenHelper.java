package fr.alcyons.phiwms_mobile.BaseDeDonnees;

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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class DepotOpenHelper extends DBOpenHelper {

    public DepotOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static Depot getDepotParReference(SQLiteDatabase db, String depotReference) {
        Depot depot = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT + "=?", new String[]{depotReference});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }
        cursor.close();
        cursor = null;
        return depot;
    }

    public static Depot getDepotPUI(SQLiteDatabase db) {
        Depot depot = null;
        String structure = "PUI";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_STRUCTURE_DEPOT + "=?", new String[]{structure});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }
        cursor.close();
        cursor = null;
        return depot;
    }

    public static Depot getDepotPUIPAD(SQLiteDatabase db) {
        Depot depot = null;
        String structure = "PUI-PAD";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_STRUCTURE_DEPOT + "=?", new String[]{structure});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }
        cursor.close();
        cursor = null;
        return depot;
    }

    public static Depot getDepotByNom(SQLiteDatabase db, String Nom) {
        Depot depot = null;
        String structure = "PAD";

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_NOM_DEPOT + "=?", new String[]{Nom});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }
        cursor.close();
        cursor = null;
        return depot;
    }

    public static Depot getDepotParID(SQLiteDatabase db, int id) {
        Depot depot = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_ID_DEPOT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }

        cursor.close();
        cursor = null;
        return depot;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_DEPOT, Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT + "=?", new String[]{"DEPOT_PUF_ALCYONS_ESSAI"});
        return db.delete(Constantes.TABLE_DEPOT, Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT + "=?", new String[]{"DEPOT_PUI_ALCYONS_ESSAI"});
    }

    public static void viderTableDepot(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_DEPOT, null, null);
    }

    public static long insererUnDepotEnBDD(SQLiteDatabase db, Depot depot) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_DEPOT, depot.getDepot_UID());
        contentValues.put(Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT, depot.getDepot_Reference());
        contentValues.put(Constantes.CLE_COL_NOM_DEPOT, depot.getNom());
        contentValues.put(Constantes.CLE_COL_ADRESSE1_DEPOT, depot.getAdresse1());
        contentValues.put(Constantes.CLE_COL_ADRESSE2_DEPOT, depot.getAdresse2());
        contentValues.put(Constantes.CLE_COL_CP_DEPOT, depot.getCP());
        contentValues.put(Constantes.CLE_COL_VILLE_DEPOT, depot.getVille());
        contentValues.put(Constantes.CLE_COL_TEL_DEPOT, depot.getTel());
        contentValues.put(Constantes.CLE_COL_FAX_DEPOT, depot.getFax());
        contentValues.put(Constantes.CLE_COL_STRUCTURE_DEPOT, depot.getStructure());
        contentValues.put(Constantes.CLE_COL_RESPONSABLE_DEPOT, depot.getResponsable());
        contentValues.put(Constantes.CLE_COL_PAD_IPP_DEPOT, depot.getPAD_IPP());
        contentValues.put(Constantes.CLE_COL_PAD_Patient_DEPOT, depot.getPAD_Patient());
        contentValues.put(Constantes.CLE_COL_HOR_OUV_DEPOT, depot.getHor_ouv());
        contentValues.put(Constantes.CLE_COL_R_ADRESSE1_DEPOT, depot.getR_adresse1());
        contentValues.put(Constantes.CLE_COL_R_ADRESSE2_DEPOT, depot.getR_adresse2());
        contentValues.put(Constantes.CLE_COL_R_CP_DEPOT, depot.getR_CP());
        contentValues.put(Constantes.CLE_COL_R_VILLE_DEPOT, depot.getR_Ville());
        contentValues.put(Constantes.CLE_COL_R_TEL_DEPOT, depot.getR_tel());
        contentValues.put(Constantes.CLE_COL_R_FAX_DEPOT, depot.getR_fax());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_DEPOT, depot.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_DEPOT, depot.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_DEPOT, depot.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_DIALYSE_FREQUENCE_DEPOT, depot.getDialyse_Frequence());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_ADR1_DEPOT, depot.getPAD_Vacances_Adr1());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_ADR2_DEPOT, depot.getPAD_Vacances_Adr2());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_CP_DEPOT, depot.getPAD_Vacances_CP());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_VILLE_DEPOT, depot.getPAD_Vacances_Ville());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_PAYS_DEPOT, depot.getPAD_Vacances_Pays());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_TEL_DEPOT, depot.getPAD_Vacances_Tél());
        contentValues.put(Constantes.CLE_COL_PAD_VACANCES_COMMENTAIRES_DEPOT, depot.getPAD_Vacances_Commentaires());
        contentValues.put(Constantes.CLE_COL_PAD_LOCALISATION_POCHES_DEPOT, depot.getPAD_Localisation_Poches());
        contentValues.put(Constantes.CLE_COL_PAD_PRECISION_LOCALISATION_DEPOT, depot.getPAD_Précision_Localisation());
        contentValues.put(Constantes.CLE_COL_PAD_COORDONNEES_GPS_DEPOT, depot.getPAD_Coordonnées_GPS());
        contentValues.put(Constantes.CLE_COL_PAD_VEHICULE_LIVRAISON_DEPOT, depot.getPAD_Vehicule_Livraison());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_SEMAINE_1_DEPOT, depot.getLivraison_Semaine_1());
        contentValues.put(Constantes.CLE_COL_PAD_EMAIL_DEPOT, depot.getPAD_Email());
        contentValues.put(Constantes.CLE_COL_PAD_PLAN_DEPOT, depot.getPAD_Plan());
        contentValues.put(Constantes.CLE_COL_PRATICIENT_PAR_DEFAUT_DEPOT, depot.getPraticient_Par_defaut());
        contentValues.put(Constantes.CLE_COL_PAD_LIEU_TRAITEMENT_DEPOT, depot.getPAD_Lieu_Traitement());
        contentValues.put(Constantes.CLE_COL_PAD_ESCALIER_DEPOT, depot.getPAD_escalier());
        contentValues.put(Constantes.CLE_COL_PAD_DIGICODE_DEPOT, depot.getPAD_digicode());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_JOUR_DEPOT, depot.getLivraison_Jour());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_SEMAINE_2_DEPOT, depot.getLivraison_Semaine_2());
        contentValues.put(Constantes.CLE_COL_TOURNEE_NOM_DEPOT, depot.getTournee_nom());
        contentValues.put(Constantes.CLE_COL_SECTION_ANALYTIQUE_DEPOT, depot.getSection_Analytique());
        contentValues.put(Constantes.CLE_COL_SYMBOLE_DEPOT, depot.getSymbole());
        contentValues.put(Constantes.CLE_COL_REF_DEPOT_PHI_DEPOT, depot.getRef_Depot_Phi());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_SEMAINE_3_DEPOT, depot.getLivraison_Semaine_3());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_SEMAINE_4_DEPOT, depot.getLivraison_Semaine_4());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_SEMAINE_5_DEPOT, depot.getLivraison_Semaine_5());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE_DEPOT, depot.getCommentaire_Commande());
        contentValues.put(Constantes.CLE_COL_CAHP_DEPOT, depot.getCAHP());
        contentValues.put(Constantes.CLE_COL_HORAIRE_LIVRAISON_DEPOT, depot.getHoraire_livraison());
        contentValues.put(Constantes.CLE_COL_ATIR_REFERENCE_DEPOT, depot.getATIR_Reference_Depot());
        contentValues.put(Constantes.CLE_COL_DM_LOCALISATION_DEPOT, depot.getDM_Localisation());
        contentValues.put(Constantes.CLE_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT, depot.getReference_Depot_Avant_PHI());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT, depot.getLivraison_Frequence_Type());
        contentValues.put(Constantes.CLE_COL_DOSSIER_DOCUMENT_DEPOT, depot.getDossier_document());
        contentValues.put(Constantes.CLE_COL_PROTOCOLESTD_DEPOT, depot.getProtocoleStd());
        contentValues.put(Constantes.CLE_COL_FINESSGEO_DEPOT, depot.getFInessGeo());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_DIRECTE_DEPOT, depot.isLivraison_Directe());
        contentValues.put(Constantes.CLE_COL_PAD_ACCES_CHARIOT_DEPOT, depot.isPAD_Acces_Chariot());
        contentValues.put(Constantes.CLE_COL_PAD_ACCES_ROLL_DEPOT, depot.isPAD_Acces_Roll());
        contentValues.put(Constantes.CLE_COL_PAD_ACCES_MANUELLE_DEPOT, depot.isPAD_Acces_Manuelle());
        contentValues.put(Constantes.CLE_COL_ARCHIVE_DEPOT, depot.isArchive());
        contentValues.put(Constantes.CLE_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT, depot.isPAD_Utiliser_Adresse_Vacances());
        contentValues.put(Constantes.CLE_COL_PAD_ASCENSEUR_DEPOT, depot.isPAD_Ascenceur());
        contentValues.put(Constantes.CLE_COL_SERVICE_EXTERNE_DEPOT, depot.isService_externe());
        contentValues.put(Constantes.CLE_COL_ACCUSE_RECEPTION_DEPOT, depot.isAccuse_Reception());
        contentValues.put(Constantes.CLE_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT, depot.isInventaire_fin_de_Mois());
        contentValues.put(Constantes.CLE_COL_RAZ_STOCK_INVENTAIRE_DEPOT, depot.isRAZ_Stock_Inventaire());
        contentValues.put(Constantes.CLE_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT, depot.getJours_de_réserve_par_livraison());
        contentValues.put(Constantes.CLE_COL_ID_UF_RATTACHEMENT_DEPOT, depot.getID_UF_Rattachement());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_PERIODE_DEPOT, depot.getLivraison_Periode());
        contentValues.put(Constantes.CLE_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT, depot.getPAD_Commentaire_Livraison());
        contentValues.put(Constantes.CLE_COL_STATUT_DEPOT, depot.getStatut());
        contentValues.put(Constantes.CLE_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT, depot.getPAD_ID_Lieu_Traitement());
        contentValues.put(Constantes.CLE_COL_PAD_ETAGE_DEPOT, depot.getPAD_etage());
        contentValues.put(Constantes.CLE_COL_TOURNEE_CODE_DEPOT, depot.getTournee_code());
        contentValues.put(Constantes.CLE_COL_PAD_ID_PRET_DEPOT, depot.getPAD_ID_Pret());
        contentValues.put(Constantes.CLE_COL_ETABLISSEMENT_UID_DEPOT, depot.getEtablissement_UID());
        contentValues.put(Constantes.CLE_COL_NOMBRE_POSTES_DEPOT, depot.getNombre_Postes());
        contentValues.put(Constantes.CLE_COL_LIVRAISON_NB_SEMAINES_DEPOT, depot.getLivraison_Nb_Semaines());
        contentValues.put(Constantes.CLE_COL_LATITUDE_DEPOT, depot.getLatitude());
        contentValues.put(Constantes.CLE_COL_LONGITUDE_DEPOT, depot.getLongitude());
        contentValues.put(Constantes.CLE_COL_ORDRE_DEPOT, depot.getOrdre());
        contentValues.put(Constantes.CLE_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT, depot.getJours_de_réserve_par_defaut());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_DEPOT, null, contentValues);

        depot.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static void insererBDDLocaleDepots(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Dépôt";
        final String erreurSynchronisationLibelle = "Dépots non synchronisés";

        if (!statutConnexion) {
            String activityName = context.getClass().getSimpleName();
            if(activityName.contentEquals("AuthentificationActivity"))
            {
                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
            }
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteDepots;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String erreur = "";
                                boolean etat = true;
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    erreur = response.getString("erreur");
                                    etat = false;
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        viderBasesDeDonnees(db);
                                        erreur = "Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    } else if (!erreur.equals("Aucun PH_Depots trouvé")) {
                                        erreur = "Erreur API Dépots";
                                    }
                                } else {
                                    viderTableDepot(db);

                                    JSONArray depotJSONArray = response.getJSONArray("PH_Depots");
                                    int compteurReussite = 0;

                                    for (int i = 0; i < depotJSONArray.length(); i++) {
                                        // Récupération du service courant
                                        JSONObject depotJSONObject = depotJSONArray.getJSONObject(i);

                                        Depot depot = new Depot(depotJSONObject);

                                        //gestion depot alcyons
                                        if(utilisateur.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot.getStructure().contentEquals("PAD"))
                                        {
                                            depot.setNom("XXX PAD");
                                            String[] tab_reference = depot.getDepot_Reference().split("-");
                                            String new_ref = tab_reference[tab_reference.length-1];
                                            //depot.setDepot_Reference("XXXX - PAD - "+new_ref);
                                            depot.setAdresse1("50 avenue du lac marion");
                                            depot.setCP("64200");
                                            depot.setVille("Biarritz");
                                            depot.setTel("0559225008");
                                        }

                                        // insertion du service en bdd
                                        long rowID = insererUnDepotEnBDD(db, depot);
                                        if (rowID != -1) {
                                            compteurReussite++;
                                        }
                                    }
                                    if (resultCount != compteurReussite) {
                                        erreur = String.valueOf(resultCount - compteurReussite) + " depots n'ont pas été insérées.";
                                        etat = false;
                                    }
                                }
                                String activityName = context.getClass().getSimpleName();
                                if(activityName.contentEquals("AuthentificationActivity"))
                                {
                                    ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, etat, erreur);
                                }
                                else if(activityName.contentEquals("ServiceConnexionDirecteActivity"))
                                {
                                    ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Dépôt volley", error.toString());
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
                            }

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

            requestQueue.add(obreq);
        }

    }

    public static List<Depot> getDepotsParType(SQLiteDatabase db, String depotStructure) {
        String critereRecherche = depotStructure;
        Cursor cursor = db.query(true, Constantes.TABLE_DEPOT, new String[]{"*"}, Constantes.CLE_COL_STRUCTURE_DEPOT + " LIKE ?", new String[]{critereRecherche}, Constantes.CLE_COL_NOM_DEPOT, null, null, null);

        List<Depot> depotList = new ArrayList<>();

        while (cursor.moveToNext()) {
            Depot depot = new Depot(cursor);
            if(!depot.isArchive())
                depotList.add(depot);
        }
        cursor.close();
        cursor = null;
        return depotList;
    }

    public static Depot getPUICourant(SQLiteDatabase db) {
        Depot depot = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_STRUCTURE_DEPOT + " LIKE ?", new String[]{"%PUI"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }

        cursor.close();
        cursor = null;
        return depot;
    }

    public Depot getDepotRec(SQLiteDatabase db) {
        Depot depot = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_DEPOT + " WHERE " + Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT + " LIKE ?", new String[]{"%REC"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            depot = new Depot(cursor);
        }

        cursor.close();
        cursor = null;
        return depot;
    }

    public ArrayList<CustomObject> getDepotLesPlusProche(double latitude, double longitude, List<Depot> listeDepots) {
        ArrayList<CustomObject> depotLesPlusProche = new ArrayList<>();

        for (int i = 0; i < listeDepots.size(); i++) {
            Depot depotCourant = listeDepots.get(i);
            if(!depotCourant.isArchive()){
                double eloignementDepotCourant = (6371 * acos(cos(toRadians(latitude)) * cos(toRadians(depotCourant.getLatitude())) * cos(toRadians(depotCourant.getLongitude()) - toRadians(longitude)) + sin(toRadians(latitude)) * sin(toRadians(depotCourant.getLatitude()))));
                if (eloignementDepotCourant <= 1) {
                    depotLesPlusProche.add(new CustomObject(depotCourant.getDepot_UID(), eloignementDepotCourant));
                }
            }
        }

        Collections.sort(depotLesPlusProche, new Comparator<CustomObject>() {
            @Override
            public int compare(CustomObject o1, CustomObject o2) {
                return o1.compareTo(o2);
            }
        });

        return depotLesPlusProche;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_DEPOT = "Depot";

        public static final String CLE_COL_DEPOT_REFERENCE_DEPOT = "Depot_Reference";
        public static final int NUM_COL_DEPOT_REFERENCE_DEPOT = 1;
        public static final String TYPE_COL_DEPOT_REFERENCE_DEPOT = "TEXT";
        public static final String CLE_COL_NOM_DEPOT = "Nom";
        public static final int NUM_COL_NOM_DEPOT = 2;
        public static final String TYPE_COL_NOM_DEPOT = "TEXT";
        public static final String CLE_COL_ADRESSE1_DEPOT = "Adresse1";
        public static final int NUM_COL_ADRESSE1_DEPOT = 3;
        public static final String TYPE_COL_ADRESSE1_DEPOT = "TEXT";
        public static final String CLE_COL_ADRESSE2_DEPOT = "Adresse2";
        public static final int NUM_COL_ADRESSE2_DEPOT = 4;
        public static final String TYPE_COL_ADRESSE2_DEPOT = "TEXT";
        public static final String CLE_COL_CP_DEPOT = "CP";
        public static final int NUM_COL_CP_DEPOT = 5;
        public static final String TYPE_COL_CP_DEPOT = "TEXT";
        public static final String CLE_COL_VILLE_DEPOT = "Ville";
        public static final int NUM_COL_VILLE_DEPOT = 6;
        public static final String TYPE_COL_VILLE_DEPOT = "TEXT";
        public static final String CLE_COL_TEL_DEPOT = "Tel";
        public static final int NUM_COL_TEL_DEPOT = 7;
        public static final String TYPE_COL_TEL_DEPOT = "TEXT";
        public static final String CLE_COL_FAX_DEPOT = "Fax";
        public static final int NUM_COL_FAX_DEPOT = 8;
        public static final String TYPE_COL_FAX_DEPOT = "TEXT";
        public static final String CLE_COL_STRUCTURE_DEPOT = "Structure";
        public static final int NUM_COL_STRUCTURE_DEPOT = 9;
        public static final String TYPE_COL_STRUCTURE_DEPOT = "TEXT";
        public static final String CLE_COL_RESPONSABLE_DEPOT = "Responsable";
        public static final int NUM_COL_RESPONSABLE_DEPOT = 10;
        public static final String TYPE_COL_RESPONSABLE_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_IPP_DEPOT = "PAD_IPP";
        public static final int NUM_COL_PAD_IPP_DEPOT = 11;
        public static final String TYPE_COL_PAD_IPP_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_Patient_DEPOT = "PAD_Patient";
        public static final int NUM_COL_PAD_Patient_DEPOT = 12;
        public static final String TYPE_COL_PAD_Patient_DEPOT = "TEXT";
        public static final String CLE_COL_HOR_OUV_DEPOT = "Hor_ouv";
        public static final int NUM_COL_HOR_OUV_DEPOT = 13;
        public static final String TYPE_COL_HOR_OUV_DEPOT = "TEXT";
        public static final String CLE_COL_R_ADRESSE1_DEPOT = "R_adresse1";
        public static final int NUM_COL_R_ADRESSE1_DEPOT = 14;
        public static final String TYPE_COL_R_ADRESSE1_DEPOT = "TEXT";
        public static final String CLE_COL_R_ADRESSE2_DEPOT = "R_adresse2";
        public static final int NUM_COL_R_ADRESSE2_DEPOT = 15;
        public static final String TYPE_COL_R_ADRESSE2_DEPOT = "TEXT";
        public static final String CLE_COL_R_CP_DEPOT = "R_CP";
        public static final int NUM_COL_R_CP_DEPOT = 16;
        public static final String TYPE_COL_R_CP_DEPOT = "TEXT";
        public static final String CLE_COL_R_VILLE_DEPOT = "R_Ville";
        public static final int NUM_COL_R_VILLE_DEPOT = 17;
        public static final String TYPE_COL_R_VILLE_DEPOT = "TEXT";
        public static final String CLE_COL_R_TEL_DEPOT = "R_tel";
        public static final int NUM_COL_R_TEL_DEPOT = 18;
        public static final String TYPE_COL_R_TEL_DEPOT = "TEXT";
        public static final String CLE_COL_R_FAX_DEPOT = "R_fax";
        public static final int NUM_COL_R_FAX_DEPOT = 19;
        public static final String TYPE_COL_R_FAX_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_DEPOT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_DEPOT = 20;
        public static final String TYPE_COL_SYS_DT_MAJ_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_DEPOT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_DEPOT = 21;
        public static final String TYPE_COL_SYS_HEURE_MAJ_DEPOT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_DEPOT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_DEPOT = 22;
        public static final String TYPE_COL_SYS_USER_MAJ_DEPOT = "TEXT";
        public static final String CLE_COL_DIALYSE_FREQUENCE_DEPOT = "Dialyse_Frequence";
        public static final int NUM_COL_DIALYSE_FREQUENCE_DEPOT = 23;
        public static final String TYPE_COL_DIALYSE_FREQUENCE_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_ADR1_DEPOT = "PAD_Vacances_Adr1";
        public static final int NUM_COL_PAD_VACANCES_ADR1_DEPOT = 24;
        public static final String TYPE_COL_PAD_VACANCES_ADR1_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_ADR2_DEPOT = "PAD_Vacances_Adr2";
        public static final int NUM_COL_PAD_VACANCES_ADR2_DEPOT = 25;
        public static final String TYPE_COL_PAD_VACANCES_ADR2_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_CP_DEPOT = "PAD_Vacances_CP";
        public static final int NUM_COL_PAD_VACANCES_CP_DEPOT = 26;
        public static final String TYPE_COL_PAD_VACANCES_CP_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_VILLE_DEPOT = "PAD_Vacances_Ville";
        public static final int NUM_COL_PAD_VACANCES_VILLE_DEPOT = 27;
        public static final String TYPE_COL_PAD_VACANCES_VILLE_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_PAYS_DEPOT = "PAD_Vacances_Pays";
        public static final int NUM_COL_PAD_VACANCES_PAYS_DEPOT = 28;
        public static final String TYPE_COL_PAD_VACANCES_PAYS_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_TEL_DEPOT = "PAD_Vacances_Tél";
        public static final int NUM_COL_PAD_VACANCES_TEL_DEPOT = 29;
        public static final String TYPE_COL_PAD_VACANCES_TEL_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VACANCES_COMMENTAIRES_DEPOT = "PAD_Vacances_Commentaires";
        public static final int NUM_COL_PAD_VACANCES_COMMENTAIRES_DEPOT = 30;
        public static final String TYPE_COL_PAD_VACANCES_COMMENTAIRES_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_LOCALISATION_POCHES_DEPOT = "PAD_Localisation_Poches";
        public static final int NUM_COL_PAD_LOCALISATION_POCHES_DEPOT = 31;
        public static final String TYPE_COL_PAD_LOCALISATION_POCHES_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_PRECISION_LOCALISATION_DEPOT = "PAD_Précision_Localisation";
        public static final int NUM_COL_PAD_PRECISION_LOCALISATION_DEPOT = 32;
        public static final String TYPE_COL_PAD_PRECISION_LOCALISATION_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_COORDONNEES_GPS_DEPOT = "PAD_Coordonnées_GPS";
        public static final int NUM_COL_PAD_COORDONNEES_GPS_DEPOT = 33;
        public static final String TYPE_COL_PAD_COORDONNEES_GPS_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_VEHICULE_LIVRAISON_DEPOT = "PAD_Vehicule_Livraison";
        public static final int NUM_COL_PAD_VEHICULE_LIVRAISON_DEPOT = 34;
        public static final String TYPE_COL_PAD_VEHICULE_LIVRAISON_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_SEMAINE_1_DEPOT = "Livraison_Semaine_1";
        public static final int NUM_COL_LIVRAISON_SEMAINE_1_DEPOT = 35;
        public static final String TYPE_COL_LIVRAISON_SEMAINE_1_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_EMAIL_DEPOT = "PAD_Email";
        public static final int NUM_COL_PAD_EMAIL_DEPOT = 36;
        public static final String TYPE_COL_PAD_EMAIL_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_PLAN_DEPOT = "PAD_Plan";
        public static final int NUM_COL_PAD_PLAN_DEPOT = 37;
        public static final String TYPE_COL_PAD_PLAN_DEPOT = "TEXT";
        public static final String CLE_COL_PRATICIENT_PAR_DEFAUT_DEPOT = "Praticient_Par_defaut";
        public static final int NUM_COL_PRATICIENT_PAR_DEFAUT_DEPOT = 38;
        public static final String TYPE_COL_PRATICIENT_PAR_DEFAUT_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_LIEU_TRAITEMENT_DEPOT = "PAD_Lieu_Traitement";
        public static final int NUM_COL_PAD_LIEU_TRAITEMENT_DEPOT = 39;
        public static final String TYPE_COL_PAD_LIEU_TRAITEMENT_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_ESCALIER_DEPOT = "PAD_escalier";
        public static final int NUM_COL_PAD_ESCALIER_DEPOT = 40;
        public static final String TYPE_COL_PAD_ESCALIER_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_DIGICODE_DEPOT = "PAD_digicode";
        public static final int NUM_COL_PAD_DIGICODE_DEPOT = 41;
        public static final String TYPE_COL_PAD_DIGICODE_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_JOUR_DEPOT = "Livraison_Jour";
        public static final int NUM_COL_LIVRAISON_JOUR_DEPOT = 42;
        public static final String TYPE_COL_LIVRAISON_JOUR_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_SEMAINE_2_DEPOT = "Livraison_Semaine_2";
        public static final int NUM_COL_LIVRAISON_SEMAINE_2_DEPOT = 43;
        public static final String TYPE_COL_LIVRAISON_SEMAINE_2_DEPOT = "TEXT";
        public static final String CLE_COL_TOURNEE_NOM_DEPOT = "Tournee_nom";
        public static final int NUM_COL_TOURNEE_NOM_DEPOT = 44;
        public static final String TYPE_COL_TOURNEE_NOM_DEPOT = "TEXT";
        public static final String CLE_COL_SECTION_ANALYTIQUE_DEPOT = "Section_Analytique";
        public static final int NUM_COL_SECTION_ANALYTIQUE_DEPOT = 45;
        public static final String TYPE_COL_SECTION_ANALYTIQUE_DEPOT = "TEXT";
        public static final String CLE_COL_SYMBOLE_DEPOT = "Symbole";
        public static final int NUM_COL_SYMBOLE_DEPOT = 46;
        public static final String TYPE_COL_SYMBOLE_DEPOT = "TEXT";
        public static final String CLE_COL_REF_DEPOT_PHI_DEPOT = "Ref_Depot_Phi";
        public static final int NUM_COL_REF_DEPOT_PHI_DEPOT = 47;
        public static final String TYPE_COL_REF_DEPOT_PHI_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_SEMAINE_3_DEPOT = "Livraison_Semaine_3";
        public static final int NUM_COL_LIVRAISON_SEMAINE_3_DEPOT = 48;
        public static final String TYPE_COL_LIVRAISON_SEMAINE_3_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_SEMAINE_4_DEPOT = "Livraison_Semaine_4";
        public static final int NUM_COL_LIVRAISON_SEMAINE_4_DEPOT = 49;
        public static final String TYPE_COL_LIVRAISON_SEMAINE_4_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_SEMAINE_5_DEPOT = "Livraison_Semaine_5";
        public static final int NUM_COL_LIVRAISON_SEMAINE_5_DEPOT = 50;
        public static final String TYPE_COL_LIVRAISON_SEMAINE_5_DEPOT = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_COMMANDE_DEPOT = "Commentaire_Commande";
        public static final int NUM_COL_COMMENTAIRE_COMMANDE_DEPOT = 51;
        public static final String TYPE_COL_COMMENTAIRE_COMMANDE_DEPOT = "TEXT";
        public static final String CLE_COL_CAHP_DEPOT = "CAHP";
        public static final int NUM_COL_CAHP_DEPOT = 52;
        public static final String TYPE_COL_CAHP_DEPOT = "TEXT";
        public static final String CLE_COL_HORAIRE_LIVRAISON_DEPOT = "Horaire_livraison";
        public static final int NUM_COL_HORAIRE_LIVRAISON_DEPOT = 53;
        public static final String TYPE_COL_HORAIRE_LIVRAISON_DEPOT = "TEXT";
        public static final String CLE_COL_ATIR_REFERENCE_DEPOT = "ATIR_Reference_Depot";
        public static final int NUM_COL_ATIR_REFERENCE_DEPOT = 54;
        public static final String TYPE_COL_ATIR_REFERENCE_DEPOT = "TEXT";
        public static final String CLE_COL_DM_LOCALISATION_DEPOT = "DM_Localisation";
        public static final int NUM_COL_DM_LOCALISATION_DEPOT = 55;
        public static final String TYPE_COL_DM_LOCALISATION_DEPOT = "TEXT";
        public static final String CLE_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT = "Reference_Depot_Avant_PHI";
        public static final int NUM_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT = 56;
        public static final String TYPE_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT = "Livraison_Frequence_Type";
        public static final int NUM_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT = 57;
        public static final String TYPE_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT = "TEXT";
        public static final String CLE_COL_DOSSIER_DOCUMENT_DEPOT = "dossier_document";
        public static final int NUM_COL_DOSSIER_DOCUMENT_DEPOT = 58;
        public static final String TYPE_COL_DOSSIER_DOCUMENT_DEPOT = "TEXT";
        public static final String CLE_COL_PROTOCOLESTD_DEPOT = "ProtocoleStd";
        public static final int NUM_COL_PROTOCOLESTD_DEPOT = 59;
        public static final String TYPE_COL_PROTOCOLESTD_DEPOT = "TEXT";
        public static final String CLE_COL_FINESSGEO_DEPOT = "FInessGeo";
        public static final int NUM_COL_FINESSGEO_DEPOT = 60;
        public static final String TYPE_COL_FINESSGEO_DEPOT = "TEXT";
        public static final String CLE_COL_LIVRAISON_DIRECTE_DEPOT = "Livraison_Directe";
        public static final int NUM_COL_LIVRAISON_DIRECTE_DEPOT = 61;
        public static final String TYPE_COL_LIVRAISON_DIRECTE_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ACCES_CHARIOT_DEPOT = "PAD_Acces_Chariot";
        public static final int NUM_COL_PAD_ACCES_CHARIOT_DEPOT = 62;
        public static final String TYPE_COL_PAD_ACCES_CHARIOT_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ACCES_ROLL_DEPOT = "PAD_Acces_Roll";
        public static final int NUM_COL_PAD_ACCES_ROLL_DEPOT = 63;
        public static final String TYPE_COL_PAD_ACCES_ROLL_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ACCES_MANUELLE_DEPOT = "PAD_Acces_Manuelle";
        public static final int NUM_COL_PAD_ACCES_MANUELLE_DEPOT = 64;
        public static final String TYPE_COL_PAD_ACCES_MANUELLE_DEPOT = "INTEGER";
        public static final String CLE_COL_ARCHIVE_DEPOT = "Archive";
        public static final int NUM_COL_ARCHIVE_DEPOT = 65;
        public static final String TYPE_COL_ARCHIVE_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT = "PAD_Utiliser_Adresse_Vacances";
        public static final int NUM_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT = 66;
        public static final String TYPE_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ASCENSEUR_DEPOT = "PAD_Ascenceur";
        public static final int NUM_COL_PAD_ASCENSEUR_DEPOT = 67;
        public static final String TYPE_COL_PAD_ASCENSEUR_DEPOT = "INTEGER";
        public static final String CLE_COL_SERVICE_EXTERNE_DEPOT = "Service_externe";
        public static final int NUM_COL_SERVICE_EXTERNE_DEPOT = 68;
        public static final String TYPE_COL_SERVICE_EXTERNE_DEPOT = "INTEGER";
        public static final String CLE_COL_ACCUSE_RECEPTION_DEPOT = "Accuse_Reception";
        public static final int NUM_COL_ACCUSE_RECEPTION_DEPOT = 69;
        public static final String TYPE_COL_ACCUSE_RECEPTION_DEPOT = "INTEGER";
        public static final String CLE_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT = "Inventaire_fin_de_Mois";
        public static final int NUM_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT = 70;
        public static final String TYPE_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT = "INTEGER";
        public static final String CLE_COL_RAZ_STOCK_INVENTAIRE_DEPOT = "RAZ_Stock_Inventaire";
        public static final int NUM_COL_RAZ_STOCK_INVENTAIRE_DEPOT = 71;
        public static final String TYPE_COL_RAZ_STOCK_INVENTAIRE_DEPOT = "INTEGER";
        public static final String CLE_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT = "Jours_de_réserve_par_livraison";
        public static final int NUM_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT = 72;
        public static final String TYPE_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT = "INTEGER";
        public static final String CLE_COL_ID_UF_RATTACHEMENT_DEPOT = "ID_UF_Rattachement";
        public static final int NUM_COL_ID_UF_RATTACHEMENT_DEPOT = 73;
        public static final String TYPE_COL_ID_UF_RATTACHEMENT_DEPOT = "INTEGER";
        public static final String CLE_COL_LIVRAISON_PERIODE_DEPOT = "Livraison_Periode";
        public static final int NUM_COL_LIVRAISON_PERIODE_DEPOT = 74;
        public static final String TYPE_COL_LIVRAISON_PERIODE_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT = "PAD_Commentaire_Livraison";
        public static final int NUM_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT = 75;
        public static final String TYPE_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT = "TEXT";
        public static final String CLE_COL_STATUT_DEPOT = "Statut";
        public static final int NUM_COL_STATUT_DEPOT = 76;
        public static final String TYPE_COL_STATUT_DEPOT = "TEXT";
        public static final String CLE_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT = "PAD_ID_Lieu_Traitement";
        public static final int NUM_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT = 77;
        public static final String TYPE_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ETAGE_DEPOT = "PAD_etage";
        public static final int NUM_COL_PAD_ETAGE_DEPOT = 78;
        public static final String TYPE_COL_PAD_ETAGE_DEPOT = "INTEGER";
        public static final String CLE_COL_TOURNEE_CODE_DEPOT = "Tournee_code";
        public static final int NUM_COL_TOURNEE_CODE_DEPOT = 79;
        public static final String TYPE_COL_TOURNEE_CODE_DEPOT = "INTEGER";
        public static final String CLE_COL_PAD_ID_PRET_DEPOT = "PAD_ID_Pret";
        public static final int NUM_COL_PAD_ID_PRET_DEPOT = 80;
        public static final String TYPE_COL_PAD_ID_PRET_DEPOT = "INTEGER";
        public static final String CLE_COL_ETABLISSEMENT_UID_DEPOT = "Etablissement_UID";
        public static final int NUM_COL_ETABLISSEMENT_UID_DEPOT = 81;
        public static final String TYPE_COL_ETABLISSEMENT_UID_DEPOT = "INTEGER";
        public static final String CLE_COL_NOMBRE_POSTES_DEPOT = "Nombre_Postes";
        public static final int NUM_COL_NOMBRE_POSTES_DEPOT = 82;
        public static final String TYPE_COL_NOMBRE_POSTES_DEPOT = "INTEGER";
        public static final String CLE_COL_LIVRAISON_NB_SEMAINES_DEPOT = "Livraison_Nb_Semaines";
        public static final int NUM_COL_LIVRAISON_NB_SEMAINES_DEPOT = 83;
        public static final String TYPE_COL_LIVRAISON_NB_SEMAINES_DEPOT = "INTEGER";
        public static final String CLE_COL_LATITUDE_DEPOT = "latitude";
        public static final int NUM_COL_LATITUDE_DEPOT = 84;
        public static final String TYPE_COL_LATITUDE_DEPOT = "REAL";
        public static final String CLE_COL_LONGITUDE_DEPOT = "longitude";
        public static final int NUM_COL_LONGITUDE_DEPOT = 85;
        public static final String TYPE_COL_LONGITUDE_DEPOT = "REAL";
        public static final String CLE_COL_ORDRE_DEPOT = "Ordre";
        public static final int NUM_COL_ORDRE_DEPOT = 86;
        public static final String TYPE_COL_ORDRE_DEPOT = "INTEGER";
        public static final String CLE_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT = "Jours_de_réserve_par_defaut";
        public static final int NUM_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT = 87;
        public static final String TYPE_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT = "INTEGER";
        public static final String CLE_COL_ID_DEPOT = "Depot_UID";
        public static final int NUM_COL_ID_DEPOT = 88;
        public static final String TYPE_COL_ID_DEPOT = "INTEGER";

        public static final String CREATION_TABLE_DEPOT = "CREATE TABLE "
                + Constantes.TABLE_DEPOT + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_DEPOT_REFERENCE_DEPOT + " " + Constantes.TYPE_COL_DEPOT_REFERENCE_DEPOT + ","
                + Constantes.CLE_COL_NOM_DEPOT + " " + Constantes.TYPE_COL_NOM_DEPOT + ","
                + Constantes.CLE_COL_ADRESSE1_DEPOT + " " + Constantes.TYPE_COL_ADRESSE1_DEPOT + ","
                + Constantes.CLE_COL_ADRESSE2_DEPOT + " " + Constantes.TYPE_COL_ADRESSE2_DEPOT + ","
                + Constantes.CLE_COL_CP_DEPOT + " " + Constantes.TYPE_COL_CP_DEPOT + ","
                + Constantes.CLE_COL_VILLE_DEPOT + " " + Constantes.TYPE_COL_VILLE_DEPOT + ","
                + Constantes.CLE_COL_TEL_DEPOT + " " + Constantes.TYPE_COL_TEL_DEPOT + ","
                + Constantes.CLE_COL_FAX_DEPOT + " " + Constantes.TYPE_COL_FAX_DEPOT + ","
                + Constantes.CLE_COL_STRUCTURE_DEPOT + " " + Constantes.TYPE_COL_STRUCTURE_DEPOT + ","
                + Constantes.CLE_COL_RESPONSABLE_DEPOT + " " + Constantes.TYPE_COL_RESPONSABLE_DEPOT + ","

                + Constantes.CLE_COL_PAD_IPP_DEPOT + " " + Constantes.TYPE_COL_PAD_IPP_DEPOT + ","
                + Constantes.CLE_COL_PAD_Patient_DEPOT + " " + Constantes.TYPE_COL_PAD_Patient_DEPOT + ","
                + Constantes.CLE_COL_HOR_OUV_DEPOT + " " + Constantes.TYPE_COL_HOR_OUV_DEPOT + ","
                + Constantes.CLE_COL_R_ADRESSE1_DEPOT + " " + Constantes.TYPE_COL_R_ADRESSE1_DEPOT + ","
                + Constantes.CLE_COL_R_ADRESSE2_DEPOT + " " + Constantes.TYPE_COL_R_ADRESSE2_DEPOT + ","
                + Constantes.CLE_COL_R_CP_DEPOT + " " + Constantes.TYPE_COL_R_CP_DEPOT + ","
                + Constantes.CLE_COL_R_VILLE_DEPOT + " " + Constantes.TYPE_COL_R_VILLE_DEPOT + ","
                + Constantes.CLE_COL_R_TEL_DEPOT + " " + Constantes.TYPE_COL_R_TEL_DEPOT + ","
                + Constantes.CLE_COL_R_FAX_DEPOT + " " + Constantes.TYPE_COL_R_FAX_DEPOT + ","
                + Constantes.CLE_COL_SYS_DT_MAJ_DEPOT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_DEPOT + ","

                + Constantes.CLE_COL_SYS_HEURE_MAJ_DEPOT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_DEPOT + ","
                + Constantes.CLE_COL_SYS_USER_MAJ_DEPOT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_DEPOT + ","
                + Constantes.CLE_COL_DIALYSE_FREQUENCE_DEPOT + " " + Constantes.TYPE_COL_DIALYSE_FREQUENCE_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_ADR1_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_ADR1_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_ADR2_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_ADR2_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_CP_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_CP_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_VILLE_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_VILLE_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_PAYS_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_PAYS_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_TEL_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_TEL_DEPOT + ","
                + Constantes.CLE_COL_PAD_VACANCES_COMMENTAIRES_DEPOT + " " + Constantes.TYPE_COL_PAD_VACANCES_COMMENTAIRES_DEPOT + ","

                + Constantes.CLE_COL_PAD_LOCALISATION_POCHES_DEPOT + " " + Constantes.TYPE_COL_PAD_LOCALISATION_POCHES_DEPOT + ","
                + Constantes.CLE_COL_PAD_PRECISION_LOCALISATION_DEPOT + " " + Constantes.TYPE_COL_PAD_PRECISION_LOCALISATION_DEPOT + ","
                + Constantes.CLE_COL_PAD_COORDONNEES_GPS_DEPOT + " " + Constantes.TYPE_COL_PAD_COORDONNEES_GPS_DEPOT + ","
                + Constantes.CLE_COL_PAD_VEHICULE_LIVRAISON_DEPOT + " " + Constantes.TYPE_COL_PAD_VEHICULE_LIVRAISON_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_SEMAINE_1_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_SEMAINE_1_DEPOT + ","
                + Constantes.CLE_COL_PAD_EMAIL_DEPOT + " " + Constantes.TYPE_COL_PAD_EMAIL_DEPOT + ","
                + Constantes.CLE_COL_PAD_PLAN_DEPOT + " " + Constantes.TYPE_COL_PAD_PLAN_DEPOT + ","
                + Constantes.CLE_COL_PRATICIENT_PAR_DEFAUT_DEPOT + " " + Constantes.TYPE_COL_PRATICIENT_PAR_DEFAUT_DEPOT + ","
                + Constantes.CLE_COL_PAD_LIEU_TRAITEMENT_DEPOT + " " + Constantes.TYPE_COL_PAD_LIEU_TRAITEMENT_DEPOT + ","
                + Constantes.CLE_COL_PAD_ESCALIER_DEPOT + " " + Constantes.TYPE_COL_PAD_ESCALIER_DEPOT + ","

                + Constantes.CLE_COL_PAD_DIGICODE_DEPOT + " " + Constantes.TYPE_COL_PAD_DIGICODE_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_JOUR_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_JOUR_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_SEMAINE_2_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_SEMAINE_2_DEPOT + ","
                + Constantes.CLE_COL_TOURNEE_NOM_DEPOT + " " + Constantes.TYPE_COL_TOURNEE_NOM_DEPOT + ","
                + Constantes.CLE_COL_SECTION_ANALYTIQUE_DEPOT + " " + Constantes.TYPE_COL_SECTION_ANALYTIQUE_DEPOT + ","
                + Constantes.CLE_COL_SYMBOLE_DEPOT + " " + Constantes.TYPE_COL_SYMBOLE_DEPOT + ","
                + Constantes.CLE_COL_REF_DEPOT_PHI_DEPOT + " " + Constantes.TYPE_COL_REF_DEPOT_PHI_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_SEMAINE_3_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_SEMAINE_3_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_SEMAINE_4_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_SEMAINE_4_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_SEMAINE_5_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_SEMAINE_5_DEPOT + ","

                + Constantes.CLE_COL_COMMENTAIRE_COMMANDE_DEPOT + " " + Constantes.TYPE_COL_COMMENTAIRE_COMMANDE_DEPOT + ","
                + Constantes.CLE_COL_CAHP_DEPOT + " " + Constantes.TYPE_COL_CAHP_DEPOT + ","
                + Constantes.CLE_COL_HORAIRE_LIVRAISON_DEPOT + " " + Constantes.TYPE_COL_HORAIRE_LIVRAISON_DEPOT + ","
                + Constantes.CLE_COL_ATIR_REFERENCE_DEPOT + " " + Constantes.TYPE_COL_ATIR_REFERENCE_DEPOT + ","
                + Constantes.CLE_COL_DM_LOCALISATION_DEPOT + " " + Constantes.TYPE_COL_DM_LOCALISATION_DEPOT + ","
                + Constantes.CLE_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT + " " + Constantes.TYPE_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT + ","
                + Constantes.CLE_COL_DOSSIER_DOCUMENT_DEPOT + " " + Constantes.TYPE_COL_DOSSIER_DOCUMENT_DEPOT + ","
                + Constantes.CLE_COL_PROTOCOLESTD_DEPOT + " " + Constantes.TYPE_COL_PROTOCOLESTD_DEPOT + ","
                + Constantes.CLE_COL_FINESSGEO_DEPOT + " " + Constantes.TYPE_COL_FINESSGEO_DEPOT + ","

                + Constantes.CLE_COL_LIVRAISON_DIRECTE_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_DIRECTE_DEPOT + ","
                + Constantes.CLE_COL_PAD_ACCES_CHARIOT_DEPOT + " " + Constantes.TYPE_COL_PAD_ACCES_CHARIOT_DEPOT + ","
                + Constantes.CLE_COL_PAD_ACCES_ROLL_DEPOT + " " + Constantes.TYPE_COL_PAD_ACCES_ROLL_DEPOT + ","
                + Constantes.CLE_COL_PAD_ACCES_MANUELLE_DEPOT + " " + Constantes.TYPE_COL_PAD_ACCES_MANUELLE_DEPOT + ","
                + Constantes.CLE_COL_ARCHIVE_DEPOT + " " + Constantes.TYPE_COL_ARCHIVE_DEPOT + ","
                + Constantes.CLE_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT + " " + Constantes.TYPE_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT + ","
                + Constantes.CLE_COL_PAD_ASCENSEUR_DEPOT + " " + Constantes.TYPE_COL_PAD_ASCENSEUR_DEPOT + ","
                + Constantes.CLE_COL_SERVICE_EXTERNE_DEPOT + " " + Constantes.TYPE_COL_SERVICE_EXTERNE_DEPOT + ","
                + Constantes.CLE_COL_ACCUSE_RECEPTION_DEPOT + " " + Constantes.TYPE_COL_ACCUSE_RECEPTION_DEPOT + ","
                + Constantes.CLE_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT + " " + Constantes.TYPE_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT + ","

                + Constantes.CLE_COL_RAZ_STOCK_INVENTAIRE_DEPOT + " " + Constantes.TYPE_COL_RAZ_STOCK_INVENTAIRE_DEPOT + ","
                + Constantes.CLE_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT + " " + Constantes.TYPE_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT + ","
                + Constantes.CLE_COL_ID_UF_RATTACHEMENT_DEPOT + " " + Constantes.TYPE_COL_ID_UF_RATTACHEMENT_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_PERIODE_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_PERIODE_DEPOT + ","
                + Constantes.CLE_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT + " " + Constantes.TYPE_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT + ","
                + Constantes.CLE_COL_STATUT_DEPOT + " " + Constantes.TYPE_COL_STATUT_DEPOT + ","
                + Constantes.CLE_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT + " " + Constantes.TYPE_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT + ","
                + Constantes.CLE_COL_PAD_ETAGE_DEPOT + " " + Constantes.TYPE_COL_PAD_ETAGE_DEPOT + ","
                + Constantes.CLE_COL_TOURNEE_CODE_DEPOT + " " + Constantes.TYPE_COL_TOURNEE_CODE_DEPOT + ","
                + Constantes.CLE_COL_PAD_ID_PRET_DEPOT + " " + Constantes.TYPE_COL_PAD_ID_PRET_DEPOT + ","
                + Constantes.CLE_COL_ETABLISSEMENT_UID_DEPOT + " " + Constantes.TYPE_COL_ETABLISSEMENT_UID_DEPOT + ","
                + Constantes.CLE_COL_NOMBRE_POSTES_DEPOT + " " + Constantes.TYPE_COL_NOMBRE_POSTES_DEPOT + ","
                + Constantes.CLE_COL_LIVRAISON_NB_SEMAINES_DEPOT + " " + Constantes.TYPE_COL_LIVRAISON_NB_SEMAINES_DEPOT + ","
                + Constantes.CLE_COL_LATITUDE_DEPOT + " " + Constantes.TYPE_COL_LATITUDE_DEPOT + ","
                + Constantes.CLE_COL_LONGITUDE_DEPOT + " " + Constantes.TYPE_COL_LONGITUDE_DEPOT + ","
                + Constantes.CLE_COL_ORDRE_DEPOT + " " + Constantes.TYPE_COL_ORDRE_DEPOT + ","
                + Constantes.CLE_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT + " " + Constantes.TYPE_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT + ","
                + Constantes.CLE_COL_ID_DEPOT + " " + Constantes.TYPE_COL_ID_DEPOT
                + ");";

    }

    // Class uniquement utile à la requete getDepotLesPlusProche
    public class CustomObject {

        private int key;
        private double value;

        public CustomObject(int key, double value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public double getValue() {
            return value;
        }

        public int compareTo(Object obj) {
            CustomObject customObject = (CustomObject) obj;

            if (this.getValue() == customObject.getValue()) {
                return 0;
            } else {
                return this.getValue() > customObject.getValue() ? 1 : -1;
            }
        }
    }
}
