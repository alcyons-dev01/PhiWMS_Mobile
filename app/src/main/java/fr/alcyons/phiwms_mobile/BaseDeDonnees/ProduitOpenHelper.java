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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
public class ProduitOpenHelper extends DBOpenHelper {

    private static final int MY_SOCKET_TIMEOUT_MS = 100;

    public ProduitOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static int getNbProduit(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT, null);
        int nbRetours = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbRetours;
    }

    public static int getNbProduitIdentifier(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT+" WHERE "+Constantes.CLE_COL_GTIN_PRODUIT+" != \"\" OR "+Constantes.CLE_COL_CODE_INCONNU+" != \"\"", null);
        int nbReferences = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbReferences;
    }


    public static int getNbProduitNonIdentifier(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT+" WHERE "+Constantes.CLE_COL_GTIN_PRODUIT+" = \"\" AND "+Constantes.CLE_COL_CODE_INCONNU+" = \"\"", null);
        int nbReferences = cursor.getCount();
        cursor.close();
        cursor = null;
        return nbReferences;
    }

    public static List<Produit> getAllMedicaments(final SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();

        Cursor cursor = db.query(true, Constantes.TABLE_PRODUIT, new String[]{"*"}, Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{String.valueOf(1)}, Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, null, null, null);

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getAllDispositifs(final SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();

        Cursor cursor = db.query(true, Constantes.TABLE_PRODUIT, new String[]{"*"}, Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{String.valueOf(2)}, Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, null, null, null);

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getAllProduits(final SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();

        Cursor cursor = db.query(true, Constantes.TABLE_PRODUIT+" WHERE "+Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT+" != 1", new String[]{"*"}, null, null, Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, null, null, null);

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<String> getAllFournisseurs(final SQLiteDatabase db, String classeNumeroProduit) {

        Cursor cursor = db.query(true, Constantes.TABLE_PRODUIT, new String[]{"*"}, Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{classeNumeroProduit}, Constantes.CLE_COL_FOURNISSEUR_PRODUIT, null, null, null);

        List<String> produitFournisseurList = new ArrayList<>();

        while (cursor.moveToNext()) {
            produitFournisseurList.add(cursor.getString(Constantes.NUM_COL_FOURNISSEUR_PRODUIT));
        }
        cursor.close();
        cursor = null;
        return produitFournisseurList;
    }

    public static List<String> getAllCategories(final SQLiteDatabase db, String classeNumeroProduit) {

        Cursor cursor = db.query(true, Constantes.TABLE_PRODUIT, new String[]{"*"}, Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{classeNumeroProduit}, Constantes.CLE_COL_CATEGORIE_PRODUIT, null, null, null);

        List<String> produitCategorieList = new ArrayList<>();

        while (cursor.moveToNext()) {
            produitCategorieList.add(cursor.getString(Constantes.NUM_COL_CATEGORIE_PRODUIT));
        }
        cursor.close();
        cursor = null;
        return produitCategorieList;
    }

    public static List<Produit> getMedicamentsParGTIN(SQLiteDatabase db, String produitGTIN) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitGTIN, String.valueOf(1)});

        if(cursor.getCount() < 1)
        {
            if(produitGTIN.length() == 14)
            {
                produitGTIN = "01"+produitGTIN;
            }
            else
            {
                produitGTIN = produitGTIN.substring(2);
            }
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitGTIN, String.valueOf(1)});
        }

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getDispositifsParGTIN(SQLiteDatabase db, String produitGTIN) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitGTIN, String.valueOf(2)});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getProduitByCodeInconnu(SQLiteDatabase db, String codeinconnu)
    {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_CODE_INCONNU + "=?", new String[]{codeinconnu});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static Produit getUnProduitByCodeInconnu(SQLiteDatabase db, String codeinconnu)
    {
        Produit produit = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_CODE_INCONNU + "=?", new String[]{codeinconnu});

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            produit = new Produit(cursor);
        }
        cursor.close();
        cursor = null;
        return produit;
    }

    public static List<Produit> getProduitsNonIdentifier(SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "= \"\" AND "+Constantes.CLE_COL_CODE_INCONNU+"= \"\" AND "+Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT+" != 1 ORDER BY "+Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, new String[]{});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getProduitsIdentifier(SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "!= \"\" OR "+Constantes.CLE_COL_CODE_INCONNU+"!= \"\" AND "+Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT+" != 1 ORDER BY "+Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, new String[]{});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }


    public static List<Produit> getProduitsParGTIN(SQLiteDatabase db, String produitGTIN) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "=? AND "+Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT+" != 1 ", new String[]{produitGTIN});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static Produit getUnProduitParGTIN(SQLiteDatabase db, String produitGTIN) {
        Produit produit = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_GTIN_PRODUIT + "=?", new String[]{produitGTIN});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            produit = new Produit(cursor);
        }
        cursor.close();
        cursor = null;

        return produit;
    }

    public static List<Produit> getProduitsParCodeInconnue(SQLiteDatabase db, String produitCodeInconnu) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_CODE_INCONNU + "=?", new String[]{produitCodeInconnu});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getMedicamentsParFournisseur(SQLiteDatabase db, String produitFournisseur) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_FOURNISSEUR_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitFournisseur, String.valueOf(1)});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getDispositifsParFournisseur(SQLiteDatabase db, String produitFournisseur) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_FOURNISSEUR_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitFournisseur, String.valueOf(2)});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getMedicamentsParCategorie(SQLiteDatabase db, String produitCategorie) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_CATEGORIE_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitCategorie, String.valueOf(1)});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getDispositifsParCategorie(SQLiteDatabase db, String produitCategorie) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_CATEGORIE_PRODUIT + "=? and " + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + "=?", new String[]{produitCategorie, String.valueOf(2)});

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            produitList.add(produit);
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static List<Produit> getProduitPAD(SQLiteDatabase db) {
        List<Produit> produitList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT, null);

        while (cursor.moveToNext()) {
            Produit produit = new Produit(cursor);
            if (produit.isArret_Dis() == false) {
                produitList.add(produit);
            }
        }
        cursor.close();
        cursor = null;
        return produitList;
    }

    public static Produit getProduitByID(SQLiteDatabase db, int id) {
        Produit produit = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + Constantes.CLE_COL_ID_PRODUIT + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            produit = new Produit(cursor);
        }
        cursor.close();
        cursor = null;

        return produit;
    }

    public static Produit getProduitByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        Produit produit = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PRODUIT + " WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            produit = new Produit(cursor);
        }

        cursor.close();
        cursor = null;
        return produit;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_PRODUIT, Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT + "=?", new String[]{"Traceur_Dispositif_ALCYONS"});
        return db.delete(Constantes.TABLE_PRODUIT, Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT + "=?", new String[]{"Traceur_Medicament_ALCYONS"});
    }

    public static void viderTableProduit(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PRODUIT, null, null);
    }

    public static long insererUnProduitEnBDD(SQLiteDatabase db, Produit produit) {
        // Récupération des éléments du produit
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PRODUIT, produit.getID_produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, produit.getDesignation_interne());
        contentValues.put(Constantes.CLE_COL_REF_FOURNI_PRODUIT, produit.getRef_fourni());
        contentValues.put(Constantes.CLE_COL_INFORMATION_IMPORTANTES_PRODUIT, produit.getInformations_importantes());
        contentValues.put(Constantes.CLE_COL_CONDITION_USAGE_UNIQUE_PRODUIT, produit.isCondition_usage_unique());
        contentValues.put(Constantes.CLE_COL_STERILE_PRODUIT, produit.isSterile());
        contentValues.put(Constantes.CLE_COL_STERILISATION_MODE_PRODUIT, produit.getSterilisation_Mode());
        contentValues.put(Constantes.CLE_COL_NEPASRESTERILISER_PRODUIT, produit.isNePasResteriliser());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_PRODUIT, produit.getCategorie());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_PRODUIT, produit.getFournisseur());
        contentValues.put(Constantes.CLE_COL_PRIX_UNITAIRE_PRODUIT, produit.getPrix_unitaire());
        contentValues.put(Constantes.CLE_COL_FORME_PRODUIT, produit.getForme());
        contentValues.put(Constantes.CLE_COL_CONTENANT_PRODUIT, produit.getContenant());
        contentValues.put(Constantes.CLE_COL_MATERIAUX_PRODUIT, produit.getMateriaux());
        contentValues.put(Constantes.CLE_COL_STATUT_PRODUIT, produit.getStatut());
        contentValues.put(Constantes.CLE_COL_SECTEUR_PRODUIT, produit.getSecteur());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_PRODUIT, produit.getCommentaire());
        contentValues.put(Constantes.CLE_COL_RISQUE_PHT_PRODUIT, produit.isRisque_PHT());
        contentValues.put(Constantes.CLE_COL_RISQUE_LATEX_PRODUIT, produit.isRisque_latex());
        contentValues.put(Constantes.CLE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT, produit.getRisque_Substance_presence());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRODUIT, produit.getConservation());
        contentValues.put(Constantes.CLE_COL_TEMPERATURE_REFRIGERE_PRODUIT, produit.isTemperature_Refrigere());
        contentValues.put(Constantes.CLE_COL_TEMPERATURE_AMBIANTE_PRODUIT, produit.isTemperature_Ambiante());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT, produit.getConservation_temperature_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT, produit.getConservation_temperature_Max());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_ABRI_PRODUIT, produit.isConservation_abri());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_SEC_PRODUIT, produit.isConservation_sec());
        contentValues.put(Constantes.CLE_COL_CONDITION_FRAGILE_PRODUIT, produit.isCondition_Fragile());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_RISQUE_PRODUIT, produit.isMedicament_Risque());
        contentValues.put(Constantes.CLE_COL_CONTRE_INDICATIONS_PRODUIT, produit.getContre_indications());
        contentValues.put(Constantes.CLE_COL_EFFETS_INDESIRABLES_PRODUIT, produit.getEffets_indesirables());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT, produit.isMedicament_dotation_urgence());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_LISTE_PRODUIT, produit.getMedicament_Liste());
        contentValues.put(Constantes.CLE_COL_POSOLOGIE_PRODUIT, produit.getPosologie());
        contentValues.put(Constantes.CLE_COL_VOIE_PRODUIT, produit.getVoie());
        contentValues.put(Constantes.CLE_COL_INDICATION_THERAPEUTIQUE_PRODUIT, produit.getIndication_therapeutique());
        contentValues.put(Constantes.CLE_COL_UI_CONVERSION_PRODUIT, produit.getUI_Conversion());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_CTJ_PRODUIT, produit.getMedicament_CTJ());
        contentValues.put(Constantes.CLE_COL_TAUX_DE_TVA_PRODUIT, produit.getTaux_de_TVA());
        contentValues.put(Constantes.CLE_COL_GTIN_PRODUIT, produit.getGTIN());
        contentValues.put(Constantes.CLE_COL_N_INTERNE_PRODUIT, produit.getN_interne());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_EXT_PRODUIT, produit.getDesignation_ext());
        contentValues.put(Constantes.CLE_COL_PEREMPTION_PRODUIT, produit.isPeremption());
        contentValues.put(Constantes.CLE_COL_MARCHE_OBTENU_PRODUIT, produit.isMarche_Obtenu());
        contentValues.put(Constantes.CLE_COL_ORDONNANCE_PRODUIT, produit.isOrdonnance());
        contentValues.put(Constantes.CLE_COL_SUIVI_LOT_PRODUIT, produit.isSuivi_Lot());
        contentValues.put(Constantes.CLE_COL_REASSORT_PRODUIT, produit.isReassort());
        contentValues.put(Constantes.CLE_COL_INCLU_AU_PANEL_PRODUIT, produit.isInclu_au_panel());
        contentValues.put(Constantes.CLE_COL_RESPECT_COND_ACHAT_PRODUIT, produit.isRespect_Cond_Achat());
        contentValues.put(Constantes.CLE_COL_ARRET_DIS_PRODUIT, produit.isArret_Dis());
        contentValues.put(Constantes.CLE_COL_GRATUIT_PRODUIT, produit.isGratuit());
        contentValues.put(Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT, produit.isArret_Commande());
        contentValues.put(Constantes.CLE_COL_PREV_A_COMMANDER_PRODUIT, produit.isPrev_A_commander());
        contentValues.put(Constantes.CLE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT, produit.isInscrire_a_ordonnancier());
        contentValues.put(Constantes.CLE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT, produit.isCondition_Refus_Si_Endomage());
        contentValues.put(Constantes.CLE_COL_CONDITION_PEREMPTION_PRODUIT, produit.isCondition_peremption());
        contentValues.put(Constantes.CLE_COL_TRACABILITE_REF_PRODUIT, produit.isTracabilite_ref());
        contentValues.put(Constantes.CLE_COL_TRACABILITE_SN_PRODUIT, produit.isTracabilite_SN());
        contentValues.put(Constantes.CLE_COL_RISQUE_VOIR_NOTICE_PRODUIT, produit.isRisque_voir_notice());
        contentValues.put(Constantes.CLE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT, produit.isRisque_Voir_Recommandation());
        contentValues.put(Constantes.CLE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT, produit.isDistribution_Nominative_Active());
        contentValues.put(Constantes.CLE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT, produit.isRegle_Bon_Usage_Active());
        contentValues.put(Constantes.CLE_COL_LIVRET_THERAPEUTIQUE_PRODUIT, produit.isLivret_Therapeutique());
        contentValues.put(Constantes.CLE_COL_DATE_CREATION_PRODUIT, produit.getDate_Creation_Produit());
        contentValues.put(Constantes.CLE_COL_DATE_ARRET_COM_PRODUIT, produit.getDate_arret_com());
        contentValues.put(Constantes.CLE_COL_DATE_ARRET_DIS_PRODUIT, produit.getDate_Arret_Dis());
        contentValues.put(Constantes.CLE_COL_DATE_DER_PHOTO_PRODUIT, produit.getDate_der_photo());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PRODUIT, produit.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PRODUIT, produit.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_ZONE_PUI_DEFAUT_PRODUIT, produit.getZone_PUI_Defaut());
        contentValues.put(Constantes.CLE_COL_UNITE_PRODUIT, produit.getUnite());
        contentValues.put(Constantes.CLE_COL_VILLE_PRODUIT, produit.getVille());
        contentValues.put(Constantes.CLE_COL_TYPE_ERREUR_PRODUIT, produit.getType_erreur());
        contentValues.put(Constantes.CLE_COL_A_CORRIGER_PRODUIT, produit.getA_corriger());
        contentValues.put(Constantes.CLE_COL_REASSORT_STATUT_PRODUIT, produit.getReassort_Statut());
        contentValues.put(Constantes.CLE_COL_MODE_DE_DISTRIBUTION_PRODUIT, produit.getMode_de_Distribution());
        contentValues.put(Constantes.CLE_COL_REF_MARCHE_PRODUIT, produit.getRef_marche());
        contentValues.put(Constantes.CLE_COL_DEVISE_PRODUIT, produit.getDevise());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PRODUIT, produit.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PANEL_PRODUIT, produit.getPanel());
        contentValues.put(Constantes.CLE_COL_TYPE_FRANCO_PRODUIT, produit.getType_franco());
        contentValues.put(Constantes.CLE_COL_UCD_CODE_PRODUIT, produit.getUCD_Code());
        contentValues.put(Constantes.CLE_COL_CODE_CIP_PRODUIT, produit.getCode_CIP());
        contentValues.put(Constantes.CLE_COL_HISTO_PRIX_UNITAIRE_PRODUIT, produit.getHisto_Prix_Unitaire());
        contentValues.put(Constantes.CLE_COL_DCI_PRODUIT, produit.getDCI());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE_PRODUIT, produit.getCommentaire_Commande());
        contentValues.put(Constantes.CLE_COL_CODE_LPP_PRODUIT, produit.getCode_LPP());
        contentValues.put(Constantes.CLE_COL_PHOTO_PRODUIT, produit.getPhoto());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT, produit.getEmplacement_PUI_Defaut());
        contentValues.put(Constantes.CLE_COL_DOCUMENTATION_PATH_PRODUIT, produit.getDocumentation_Path());
        contentValues.put(Constantes.CLE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT, produit.getReapprovisionnement_Classe());
        contentValues.put(Constantes.CLE_COL_ZONE_UF_DEFAUT_PRODUIT, produit.getZone_UF_Defaut());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT, produit.getEmplacement_UF_Defaut());
        contentValues.put(Constantes.CLE_COL_HEMADIALYSE_REFERENCE_PRODUIT, produit.getHemadialyse_Reference());
        contentValues.put(Constantes.CLE_COL_XFORME_PRODUIT, produit.getXForme());
        contentValues.put(Constantes.CLE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT, produit.getRisque_Substance_absence());
        contentValues.put(Constantes.CLE_COL_DOCUMENTATION_WEB_PATH_PRODUIT, produit.getDocumentation_Web_Path());
        contentValues.put(Constantes.CLE_COL_MOMENT_INJECTION_PRODUIT, produit.getMoment_Injection());
        contentValues.put(Constantes.CLE_COL_COMMENT_INJECTE_PRODUIT, produit.getComment_Injecte());
        contentValues.put(Constantes.CLE_COL_COMPOSITION_PRODUIT, produit.getComposition());
        contentValues.put(Constantes.CLE_COL_ZONE_PAD_DEFAUT_PRODUIT, produit.getZone_PAD_Defaut());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT, produit.getEmplacement_PAD_Defaut());
        contentValues.put(Constantes.CLE_COL_UCD_NOMCOURT_PRODUIT, produit.getUCD_NomCourt());
        contentValues.put(Constantes.CLE_COL_PHIE_SYNCHRO_PRODUIT, produit.getPHIE_Synchro());
        contentValues.put(Constantes.CLE_COL_CODE_FOURN_PRODUIT, produit.getCode_fourn());
        contentValues.put(Constantes.CLE_COL_DUREE_PEREMPTION_PRODUIT, produit.getDuree_peremption());
        contentValues.put(Constantes.CLE_COL_RGB_RED_PRODUIT, produit.getRGB_Red());
        contentValues.put(Constantes.CLE_COL_RGB_GREEN_PRODUIT, produit.getRGB_Green());
        contentValues.put(Constantes.CLE_COL_RGB_BLUE_PRODUIT, produit.getRGB_Blue());
        contentValues.put(Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT, produit.getClasse_numero());
        contentValues.put(Constantes.CLE_COL_NB_LIGNE_CODE_BARRE_PRODUIT, produit.getNb_Ligne_Code_Barre());
        contentValues.put(Constantes.CLE_COL_STOCK_GLOBAL_PRODUIT, produit.getStock_Global());
        contentValues.put(Constantes.CLE_COL_VALEUR_STOCK_GLOBAL_PRODUIT, produit.getValeur_Stock_Global());
        contentValues.put(Constantes.CLE_COL_COND_FRANCO_PRODUIT, produit.getCond_franco());
        contentValues.put(Constantes.CLE_COL_STOCK_CLOT_PRODUIT, produit.getStock_clot());
        contentValues.put(Constantes.CLE_COL_VALEUR_STOCK_ACTUEL_PRODUIT, produit.getValeur_stock_actuel());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_PRODUIT, produit.getCond_achat());
        contentValues.put(Constantes.CLE_COL_COND_DISTRIB_PRODUIT, produit.getCond_distrib());
        contentValues.put(Constantes.CLE_COL_SEUIL_ALERTE_PRODUIT, produit.getSeuil_alerte());
        contentValues.put(Constantes.CLE_COL_QTE_REASSORT_PRODUIT, produit.getQte_Reassort());
        contentValues.put(Constantes.CLE_COL_NOUVEAU_PU_PRODUIT, produit.getNouveau_PU());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT, produit.getCond_Achat_Gros_volume());
        contentValues.put(Constantes.CLE_COL_COULEUR_PRODUIT, produit.getCouleur());
        contentValues.put(Constantes.CLE_COL_INVENTAIRE1_PUMP_HT_PRODUIT, produit.getInventaire1_PUMP_HT());
        contentValues.put(Constantes.CLE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT, produit.getPUMP_TTC_Exercice_Prec());
        contentValues.put(Constantes.CLE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT, produit.getQte_inventaire_exercice_prec());
        contentValues.put(Constantes.CLE_COL_STOCK_ACTUEL_PRODUIT, produit.getStock_Actuel());
        contentValues.put(Constantes.CLE_COL_QTE_A_COMMANDER_PRODUIT, produit.getQte_a_commander());
        contentValues.put(Constantes.CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT, produit.getPUMP_TTC_derniere_cloture());
        contentValues.put(Constantes.CLE_COL_POIDS_PRODUIT, produit.getPoids());
        contentValues.put(Constantes.CLE_COL_VOLUME_PRODUIT, produit.getVolume());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_HYDRO_MIN_PRODUIT, produit.getConservation_hydro_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_HYDRO_MAX_PRODUIT, produit.getConservation_hydro_max());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRESSION_MIN_PRODUIT, produit.getConservation_pression_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRESSION_MAX_PRODUIT, produit.getConservation_pression_max());
        contentValues.put(Constantes.CLE_COL_CODE_INCONNU, produit.getCodeInconnue());
        contentValues.put(Constantes.CLE_COL_SUIVI_SERIALISATION, produit.isSuivi_Serialisation());
        contentValues.put(Constantes.CLE_COL_SERIALISER_RECEPTION_DELIVRANCE, produit.isSerialiser_Reception_Delivrance());

        long rowId = db.insert(Constantes.TABLE_PRODUIT, null, contentValues);

        produit.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static void insererBDDLocaleProduits(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        final String tableNom = "Produit";
        final String erreurSynchronisationLibelle = "Produits non synchronisés";

        if (!statutConnexion) {
            String activityName = context.getClass().getSimpleName();
            if(activityName.contentEquals("AuthentificationActivity"))
            {
                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
            }
        }
        else{
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + Urls.uriRequeteProduits;
            RequestQueue requestQueue = new Volley().newRequestQueue(context);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean etat = true;
                                String erreur = "";
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    erreur = response.getString("erreur");
                                    etat = false;
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        //viderBasesDeDonnees(db);
                                        erreur = " Votre identifiant de connexion est invalide, veuillez vous reconnecter.";
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        erreur = "Votre session de connexion est expirée, veuillez vous reconnecter.";
                                    } else if (!erreur.contentEquals("Aucun PH_Produit trouvé")) {
                                        erreur = "Erreur API Produits";
                                    }
                                } else {
                                    viderTableProduit(db);
                                    JSONArray produitJSONArray = response.getJSONArray("PH_Produits");
                                    int compteurReussite = 0;

                                    for (int i = 0; i < produitJSONArray.length(); i++) {
                                        // Récupération du service courant
                                        JSONObject produitJSONObject = produitJSONArray.getJSONObject(i);

                                        Produit produit = new Produit(produitJSONObject);

                                        // insertion du service en bdd
                                        long rowID = insererUnProduitEnBDD(db, produit);
                                        if (rowID != -1) {
                                            compteurReussite++;
                                        }
                                    }
                                    if (resultCount != compteurReussite) {
                                        erreur = String.valueOf(resultCount - compteurReussite) + " produits n'ont pas été insérés.";
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
                            Log.e("Produit volley", error.toString());
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("AuthentificationActivity"))
                            {
                                ((AuthentificationActivity) context).insertionDeTableEffectuee(tableNom, false, erreurSynchronisationLibelle);
                            }
                        }
                    }
            )

            {
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

    public static long mettreAJourProduit(SQLiteDatabase db, Produit produit) {
        // Récupération des éléments du produit
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_ID_PRODUIT, produit.getID_produit());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT, produit.getDesignation_interne());
        contentValues.put(Constantes.CLE_COL_REF_FOURNI_PRODUIT, produit.getRef_fourni());
        contentValues.put(Constantes.CLE_COL_INFORMATION_IMPORTANTES_PRODUIT, produit.getInformations_importantes());
        contentValues.put(Constantes.CLE_COL_CONDITION_USAGE_UNIQUE_PRODUIT, produit.isCondition_usage_unique());
        contentValues.put(Constantes.CLE_COL_STERILE_PRODUIT, produit.isSterile());
        contentValues.put(Constantes.CLE_COL_STERILISATION_MODE_PRODUIT, produit.getSterilisation_Mode());
        contentValues.put(Constantes.CLE_COL_NEPASRESTERILISER_PRODUIT, produit.isNePasResteriliser());
        contentValues.put(Constantes.CLE_COL_CATEGORIE_PRODUIT, produit.getCategorie());
        contentValues.put(Constantes.CLE_COL_FOURNISSEUR_PRODUIT, produit.getFournisseur());
        contentValues.put(Constantes.CLE_COL_PRIX_UNITAIRE_PRODUIT, produit.getPrix_unitaire());
        contentValues.put(Constantes.CLE_COL_FORME_PRODUIT, produit.getForme());
        contentValues.put(Constantes.CLE_COL_CONTENANT_PRODUIT, produit.getContenant());
        contentValues.put(Constantes.CLE_COL_MATERIAUX_PRODUIT, produit.getMateriaux());
        contentValues.put(Constantes.CLE_COL_STATUT_PRODUIT, produit.getStatut());
        contentValues.put(Constantes.CLE_COL_SECTEUR_PRODUIT, produit.getSecteur());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_PRODUIT, produit.getCommentaire());
        contentValues.put(Constantes.CLE_COL_RISQUE_PHT_PRODUIT, produit.isRisque_PHT());
        contentValues.put(Constantes.CLE_COL_RISQUE_LATEX_PRODUIT, produit.isRisque_latex());
        contentValues.put(Constantes.CLE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT, produit.getRisque_Substance_presence());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRODUIT, produit.getConservation());
        contentValues.put(Constantes.CLE_COL_TEMPERATURE_REFRIGERE_PRODUIT, produit.isTemperature_Refrigere());
        contentValues.put(Constantes.CLE_COL_TEMPERATURE_AMBIANTE_PRODUIT, produit.isTemperature_Ambiante());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT, produit.getConservation_temperature_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT, produit.getConservation_temperature_Max());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_ABRI_PRODUIT, produit.isConservation_abri());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_SEC_PRODUIT, produit.isConservation_sec());
        contentValues.put(Constantes.CLE_COL_CONDITION_FRAGILE_PRODUIT, produit.isCondition_Fragile());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_RISQUE_PRODUIT, produit.isMedicament_Risque());
        contentValues.put(Constantes.CLE_COL_CONTRE_INDICATIONS_PRODUIT, produit.getContre_indications());
        contentValues.put(Constantes.CLE_COL_EFFETS_INDESIRABLES_PRODUIT, produit.getEffets_indesirables());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT, produit.isMedicament_dotation_urgence());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_LISTE_PRODUIT, produit.getMedicament_Liste());
        contentValues.put(Constantes.CLE_COL_POSOLOGIE_PRODUIT, produit.getPosologie());
        contentValues.put(Constantes.CLE_COL_VOIE_PRODUIT, produit.getVoie());
        contentValues.put(Constantes.CLE_COL_INDICATION_THERAPEUTIQUE_PRODUIT, produit.getIndication_therapeutique());
        contentValues.put(Constantes.CLE_COL_UI_CONVERSION_PRODUIT, produit.getUI_Conversion());
        contentValues.put(Constantes.CLE_COL_MEDICAMENT_CTJ_PRODUIT, produit.getMedicament_CTJ());
        contentValues.put(Constantes.CLE_COL_TAUX_DE_TVA_PRODUIT, produit.getTaux_de_TVA());
        contentValues.put(Constantes.CLE_COL_GTIN_PRODUIT, produit.getGTIN());
        contentValues.put(Constantes.CLE_COL_N_INTERNE_PRODUIT, produit.getN_interne());
        contentValues.put(Constantes.CLE_COL_DESIGNATION_EXT_PRODUIT, produit.getDesignation_ext());
        contentValues.put(Constantes.CLE_COL_PEREMPTION_PRODUIT, produit.isPeremption());
        contentValues.put(Constantes.CLE_COL_MARCHE_OBTENU_PRODUIT, produit.isMarche_Obtenu());
        contentValues.put(Constantes.CLE_COL_ORDONNANCE_PRODUIT, produit.isOrdonnance());
        contentValues.put(Constantes.CLE_COL_SUIVI_LOT_PRODUIT, produit.isSuivi_Lot());
        contentValues.put(Constantes.CLE_COL_REASSORT_PRODUIT, produit.isReassort());
        contentValues.put(Constantes.CLE_COL_INCLU_AU_PANEL_PRODUIT, produit.isInclu_au_panel());
        contentValues.put(Constantes.CLE_COL_RESPECT_COND_ACHAT_PRODUIT, produit.isRespect_Cond_Achat());
        contentValues.put(Constantes.CLE_COL_ARRET_DIS_PRODUIT, produit.isArret_Dis());
        contentValues.put(Constantes.CLE_COL_GRATUIT_PRODUIT, produit.isGratuit());
        contentValues.put(Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT, produit.isArret_Commande());
        contentValues.put(Constantes.CLE_COL_PREV_A_COMMANDER_PRODUIT, produit.isPrev_A_commander());
        contentValues.put(Constantes.CLE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT, produit.isInscrire_a_ordonnancier());
        contentValues.put(Constantes.CLE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT, produit.isCondition_Refus_Si_Endomage());
        contentValues.put(Constantes.CLE_COL_CONDITION_PEREMPTION_PRODUIT, produit.isCondition_peremption());
        contentValues.put(Constantes.CLE_COL_TRACABILITE_REF_PRODUIT, produit.isTracabilite_ref());
        contentValues.put(Constantes.CLE_COL_TRACABILITE_SN_PRODUIT, produit.isTracabilite_SN());
        contentValues.put(Constantes.CLE_COL_RISQUE_VOIR_NOTICE_PRODUIT, produit.isRisque_voir_notice());
        contentValues.put(Constantes.CLE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT, produit.isRisque_Voir_Recommandation());
        contentValues.put(Constantes.CLE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT, produit.isDistribution_Nominative_Active());
        contentValues.put(Constantes.CLE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT, produit.isRegle_Bon_Usage_Active());
        contentValues.put(Constantes.CLE_COL_LIVRET_THERAPEUTIQUE_PRODUIT, produit.isLivret_Therapeutique());
        contentValues.put(Constantes.CLE_COL_DATE_CREATION_PRODUIT, produit.getDate_Creation_Produit());
        contentValues.put(Constantes.CLE_COL_DATE_ARRET_COM_PRODUIT, produit.getDate_arret_com());
        contentValues.put(Constantes.CLE_COL_DATE_ARRET_DIS_PRODUIT, produit.getDate_Arret_Dis());
        contentValues.put(Constantes.CLE_COL_DATE_DER_PHOTO_PRODUIT, produit.getDate_der_photo());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PRODUIT, produit.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PRODUIT, produit.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_ZONE_PUI_DEFAUT_PRODUIT, produit.getZone_PUI_Defaut());
        contentValues.put(Constantes.CLE_COL_UNITE_PRODUIT, produit.getUnite());
        contentValues.put(Constantes.CLE_COL_VILLE_PRODUIT, produit.getVille());
        contentValues.put(Constantes.CLE_COL_TYPE_ERREUR_PRODUIT, produit.getType_erreur());
        contentValues.put(Constantes.CLE_COL_A_CORRIGER_PRODUIT, produit.getA_corriger());
        contentValues.put(Constantes.CLE_COL_REASSORT_STATUT_PRODUIT, produit.getReassort_Statut());
        contentValues.put(Constantes.CLE_COL_MODE_DE_DISTRIBUTION_PRODUIT, produit.getMode_de_Distribution());
        contentValues.put(Constantes.CLE_COL_REF_MARCHE_PRODUIT, produit.getRef_marche());
        contentValues.put(Constantes.CLE_COL_DEVISE_PRODUIT, produit.getDevise());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PRODUIT, produit.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PANEL_PRODUIT, produit.getPanel());
        contentValues.put(Constantes.CLE_COL_TYPE_FRANCO_PRODUIT, produit.getType_franco());
        contentValues.put(Constantes.CLE_COL_UCD_CODE_PRODUIT, produit.getUCD_Code());
        contentValues.put(Constantes.CLE_COL_CODE_CIP_PRODUIT, produit.getCode_CIP());
        contentValues.put(Constantes.CLE_COL_HISTO_PRIX_UNITAIRE_PRODUIT, produit.getHisto_Prix_Unitaire());
        contentValues.put(Constantes.CLE_COL_DCI_PRODUIT, produit.getDCI());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE_PRODUIT, produit.getCommentaire_Commande());
        contentValues.put(Constantes.CLE_COL_CODE_LPP_PRODUIT, produit.getCode_LPP());
        contentValues.put(Constantes.CLE_COL_PHOTO_PRODUIT, produit.getPhoto());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT, produit.getEmplacement_PUI_Defaut());
        contentValues.put(Constantes.CLE_COL_DOCUMENTATION_PATH_PRODUIT, produit.getDocumentation_Path());
        contentValues.put(Constantes.CLE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT, produit.getReapprovisionnement_Classe());
        contentValues.put(Constantes.CLE_COL_ZONE_UF_DEFAUT_PRODUIT, produit.getZone_UF_Defaut());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT, produit.getEmplacement_UF_Defaut());
        contentValues.put(Constantes.CLE_COL_HEMADIALYSE_REFERENCE_PRODUIT, produit.getHemadialyse_Reference());
        contentValues.put(Constantes.CLE_COL_XFORME_PRODUIT, produit.getXForme());
        contentValues.put(Constantes.CLE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT, produit.getRisque_Substance_absence());
        contentValues.put(Constantes.CLE_COL_DOCUMENTATION_WEB_PATH_PRODUIT, produit.getDocumentation_Web_Path());
        contentValues.put(Constantes.CLE_COL_MOMENT_INJECTION_PRODUIT, produit.getMoment_Injection());
        contentValues.put(Constantes.CLE_COL_COMMENT_INJECTE_PRODUIT, produit.getComment_Injecte());
        contentValues.put(Constantes.CLE_COL_COMPOSITION_PRODUIT, produit.getComposition());
        contentValues.put(Constantes.CLE_COL_ZONE_PAD_DEFAUT_PRODUIT, produit.getZone_PAD_Defaut());
        contentValues.put(Constantes.CLE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT, produit.getEmplacement_PAD_Defaut());
        contentValues.put(Constantes.CLE_COL_UCD_NOMCOURT_PRODUIT, produit.getUCD_NomCourt());
        contentValues.put(Constantes.CLE_COL_PHIE_SYNCHRO_PRODUIT, produit.getPHIE_Synchro());
        contentValues.put(Constantes.CLE_COL_CODE_FOURN_PRODUIT, produit.getCode_fourn());
        contentValues.put(Constantes.CLE_COL_DUREE_PEREMPTION_PRODUIT, produit.getDuree_peremption());
        contentValues.put(Constantes.CLE_COL_RGB_RED_PRODUIT, produit.getRGB_Red());
        contentValues.put(Constantes.CLE_COL_RGB_GREEN_PRODUIT, produit.getRGB_Green());
        contentValues.put(Constantes.CLE_COL_RGB_BLUE_PRODUIT, produit.getRGB_Blue());
        contentValues.put(Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT, produit.getClasse_numero());
        contentValues.put(Constantes.CLE_COL_NB_LIGNE_CODE_BARRE_PRODUIT, produit.getNb_Ligne_Code_Barre());
        contentValues.put(Constantes.CLE_COL_STOCK_GLOBAL_PRODUIT, produit.getStock_Global());
        contentValues.put(Constantes.CLE_COL_VALEUR_STOCK_GLOBAL_PRODUIT, produit.getValeur_Stock_Global());
        contentValues.put(Constantes.CLE_COL_COND_FRANCO_PRODUIT, produit.getCond_franco());
        contentValues.put(Constantes.CLE_COL_STOCK_CLOT_PRODUIT, produit.getStock_clot());
        contentValues.put(Constantes.CLE_COL_VALEUR_STOCK_ACTUEL_PRODUIT, produit.getValeur_stock_actuel());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_PRODUIT, produit.getCond_achat());
        contentValues.put(Constantes.CLE_COL_COND_DISTRIB_PRODUIT, produit.getCond_distrib());
        contentValues.put(Constantes.CLE_COL_SEUIL_ALERTE_PRODUIT, produit.getSeuil_alerte());
        contentValues.put(Constantes.CLE_COL_QTE_REASSORT_PRODUIT, produit.getQte_Reassort());
        contentValues.put(Constantes.CLE_COL_NOUVEAU_PU_PRODUIT, produit.getNouveau_PU());
        contentValues.put(Constantes.CLE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT, produit.getCond_Achat_Gros_volume());
        contentValues.put(Constantes.CLE_COL_COULEUR_PRODUIT, produit.getCouleur());
        contentValues.put(Constantes.CLE_COL_INVENTAIRE1_PUMP_HT_PRODUIT, produit.getInventaire1_PUMP_HT());
        contentValues.put(Constantes.CLE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT, produit.getPUMP_TTC_Exercice_Prec());
        contentValues.put(Constantes.CLE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT, produit.getQte_inventaire_exercice_prec());
        contentValues.put(Constantes.CLE_COL_STOCK_ACTUEL_PRODUIT, produit.getStock_Actuel());
        contentValues.put(Constantes.CLE_COL_QTE_A_COMMANDER_PRODUIT, produit.getQte_a_commander());
        contentValues.put(Constantes.CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT, produit.getPUMP_TTC_derniere_cloture());
        contentValues.put(Constantes.CLE_COL_POIDS_PRODUIT, produit.getPoids());
        contentValues.put(Constantes.CLE_COL_VOLUME_PRODUIT, produit.getVolume());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_HYDRO_MIN_PRODUIT, produit.getConservation_hydro_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_HYDRO_MAX_PRODUIT, produit.getConservation_hydro_max());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRESSION_MIN_PRODUIT, produit.getConservation_pression_min());
        contentValues.put(Constantes.CLE_COL_CONSERVATION_PRESSION_MAX_PRODUIT, produit.getConservation_pression_max());
        contentValues.put(Constantes.CLE_COL_CODE_INCONNU, produit.getCodeInconnue());
        contentValues.put(Constantes.CLE_COL_SUIVI_SERIALISATION, produit.isSuivi_Serialisation());
        contentValues.put(Constantes.CLE_COL_SERIALISER_RECEPTION_DELIVRANCE, produit.isSerialiser_Reception_Delivrance());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, produit.getPhiMR4UUID());

        long rowId = db.update(Constantes.TABLE_PRODUIT, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + produit.getPhiMR4UUID(), null);
        return rowId;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PRODUIT = "Produit";


        public static final String CLE_COL_DESIGNATION_INTERNE_PRODUIT = "Designation_interne";
        public static final int NUM_COL_DESIGNATION_INTERNE_PRODUIT = 1;
        public static final String TYPE_COL_DESIGNATION_INTERNE_PRODUIT = "TEXT";
        public static final String CLE_COL_REF_FOURNI_PRODUIT = "Ref_fourni";
        public static final int NUM_COL_REF_FOURNI_PRODUIT = 2;
        public static final String TYPE_COL_REF_FOURNI_PRODUIT = "TEXT";
        public static final String CLE_COL_INFORMATION_IMPORTANTES_PRODUIT = "Informations_importantes";
        public static final int NUM_COL_INFORMATION_IMPORTANTES_PRODUIT = 3;
        public static final String TYPE_COL_INFORMATION_IMPORTANTES_PRODUIT = "TEXT";
        public static final String CLE_COL_CONDITION_USAGE_UNIQUE_PRODUIT = "Condition_usage_unique";
        public static final int NUM_COL_CONDITION_USAGE_UNIQUE_PRODUIT = 4;
        public static final String TYPE_COL_CONDITION_USAGE_UNIQUE_PRODUIT = "INTEGER";
        public static final String CLE_COL_STERILE_PRODUIT = "Sterile";
        public static final int NUM_COL_STERILE_PRODUIT = 5;
        public static final String TYPE_COL_STERILE_PRODUIT = "INTEGER";
        public static final String CLE_COL_STERILISATION_MODE_PRODUIT = "Sterilisation_Mode";
        public static final int NUM_COL_STERILISATION_MODE_PRODUIT = 6;
        public static final String TYPE_COL_STERILISATION_MODE_PRODUIT = "TEXT";
        public static final String CLE_COL_NEPASRESTERILISER_PRODUIT = "NePasResteriliser";
        public static final int NUM_COL_NEPASRESTERILISER_PRODUIT = 7;
        public static final String TYPE_COL_NEPASRESTERILISER_PRODUIT = "INTEGER";
        public static final String CLE_COL_CATEGORIE_PRODUIT = "Categorie";
        public static final int NUM_COL_CATEGORIE_PRODUIT = 8;
        public static final String TYPE_COL_CATEGORIE_PRODUIT = "TEXT";
        public static final String CLE_COL_FOURNISSEUR_PRODUIT = "Fournisseur";
        public static final int NUM_COL_FOURNISSEUR_PRODUIT = 9;
        public static final String TYPE_COL_FOURNISSEUR_PRODUIT = "TEXT";
        public static final String CLE_COL_PRIX_UNITAIRE_PRODUIT = "Prix_unitaire";
        public static final int NUM_COL_PRIX_UNITAIRE_PRODUIT = 10;
        public static final String TYPE_COL_PRIX_UNITAIRE_PRODUIT = "REAL";

        public static final String CLE_COL_FORME_PRODUIT = "Forme";
        public static final int NUM_COL_FORME_PRODUIT = 11;
        public static final String TYPE_COL_FORME_PRODUIT = "TEXT";
        public static final String CLE_COL_CONTENANT_PRODUIT = "Contenant";
        public static final int NUM_COL_CONTENANT_PRODUIT = 12;
        public static final String TYPE_COL_CONTENANT_PRODUIT = "TEXT";
        public static final String CLE_COL_MATERIAUX_PRODUIT = "Materiaux";
        public static final int NUM_COL_MATERIAUX_PRODUIT = 13;
        public static final String TYPE_COL_MATERIAUX_PRODUIT = "TEXT";
        public static final String CLE_COL_STATUT_PRODUIT = "Statut";
        public static final int NUM_COL_STATUT_PRODUIT = 14;
        public static final String TYPE_COL_STATUT_PRODUIT = "TEXT";
        public static final String CLE_COL_SECTEUR_PRODUIT = "Secteur";
        public static final int NUM_COL_SECTEUR_PRODUIT = 15;
        public static final String TYPE_COL_SECTEUR_PRODUIT = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_PRODUIT = "Commentaire";
        public static final int NUM_COL_COMMENTAIRE_PRODUIT = 16;
        public static final String TYPE_COL_COMMENTAIRE_PRODUIT = "TEXT";
        public static final String CLE_COL_RISQUE_PHT_PRODUIT = "Risque_PHT";
        public static final int NUM_COL_RISQUE_PHT_PRODUIT = 17;
        public static final String TYPE_COL_RISQUE_PHT_PRODUIT = "INTEGER";
        public static final String CLE_COL_RISQUE_LATEX_PRODUIT = "Risque_latex";
        public static final int NUM_COL_RISQUE_LATEX_PRODUIT = 18;
        public static final String TYPE_COL_RISQUE_LATEX_PRODUIT = "INTEGER";
        public static final String CLE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT = "Risque_Substance_presence";
        public static final int NUM_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT = 19;
        public static final String TYPE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT = "TEXT";
        public static final String CLE_COL_CONSERVATION_PRODUIT = "Conservation";
        public static final int NUM_COL_CONSERVATION_PRODUIT = 20;
        public static final String TYPE_COL_CONSERVATION_PRODUIT = "TEXT";

        public static final String CLE_COL_TEMPERATURE_REFRIGERE_PRODUIT = "Temperature_Refrigere";
        public static final int NUM_COL_TEMPERATURE_REFRIGERE_PRODUIT = 21;
        public static final String TYPE_COL_TEMPERATURE_REFRIGERE_PRODUIT = "INTEGER";
        public static final String CLE_COL_TEMPERATURE_AMBIANTE_PRODUIT = "Temperature_Ambiante";
        public static final int NUM_COL_TEMPERATURE_AMBIANTE_PRODUIT = 22;
        public static final String TYPE_COL_TEMPERATURE_AMBIANTE_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT = "Conservation_temperature_min";
        public static final int NUM_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT = 23;
        public static final String TYPE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT = "REAL";
        public static final String CLE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT = "Conservation_temperature_Max";
        public static final int NUM_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT = 24;
        public static final String TYPE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT = "REAL";
        public static final String CLE_COL_CONSERVATION_ABRI_PRODUIT = "Conservation_abri";
        public static final int NUM_COL_CONSERVATION_ABRI_PRODUIT = 25;
        public static final String TYPE_COL_CONSERVATION_ABRI_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONSERVATION_SEC_PRODUIT = "Conservation_sec";
        public static final int NUM_COL_CONSERVATION_SEC_PRODUIT = 26;
        public static final String TYPE_COL_CONSERVATION_SEC_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONDITION_FRAGILE_PRODUIT = "Condition_Fragile";
        public static final int NUM_COL_CONDITION_FRAGILE_PRODUIT = 27;
        public static final String TYPE_COL_CONDITION_FRAGILE_PRODUIT = "INTEGER";
        public static final String CLE_COL_MEDICAMENT_RISQUE_PRODUIT = "Medicament_Risque";
        public static final int NUM_COL_MEDICAMENT_RISQUE_PRODUIT = 28;
        public static final String TYPE_COL_MEDICAMENT_RISQUE_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONTRE_INDICATIONS_PRODUIT = "Contre_indications";
        public static final int NUM_COL_CONTRE_INDICATIONS_PRODUIT = 29;
        public static final String TYPE_COL_CONTRE_INDICATIONS_PRODUIT = "TEXT";
        public static final String CLE_COL_EFFETS_INDESIRABLES_PRODUIT = "Effets_indesirables";
        public static final int NUM_COL_EFFETS_INDESIRABLES_PRODUIT = 30;
        public static final String TYPE_COL_EFFETS_INDESIRABLES_PRODUIT = "TEXT";

        public static final String CLE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT = "Medicament_dotation_urgence";
        public static final int NUM_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT = 31;
        public static final String TYPE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT = "INTEGER";
        public static final String CLE_COL_MEDICAMENT_LISTE_PRODUIT = "Medicament_Liste";
        public static final int NUM_COL_MEDICAMENT_LISTE_PRODUIT = 32;
        public static final String TYPE_COL_MEDICAMENT_LISTE_PRODUIT = "TEXT";
        public static final String CLE_COL_POSOLOGIE_PRODUIT = "Posologie";
        public static final int NUM_COL_POSOLOGIE_PRODUIT = 33;
        public static final String TYPE_COL_POSOLOGIE_PRODUIT = "TEXT";
        public static final String CLE_COL_VOIE_PRODUIT = "Voie";
        public static final int NUM_COL_VOIE_PRODUIT = 34;
        public static final String TYPE_COL_VOIE_PRODUIT = "TEXT";
        public static final String CLE_COL_INDICATION_THERAPEUTIQUE_PRODUIT = "Indication_therapeutique";
        public static final int NUM_COL_INDICATION_THERAPEUTIQUE_PRODUIT = 35;
        public static final String TYPE_COL_INDICATION_THERAPEUTIQUE_PRODUIT = "TEXT";
        public static final String CLE_COL_UI_CONVERSION_PRODUIT = "UI_Conversion";
        public static final int NUM_COL_UI_CONVERSION_PRODUIT = 36;
        public static final String TYPE_COL_UI_CONVERSION_PRODUIT = "REAL";
        public static final String CLE_COL_MEDICAMENT_CTJ_PRODUIT = "Medicament_CTJ";
        public static final int NUM_COL_MEDICAMENT_CTJ_PRODUIT = 37;
        public static final String TYPE_COL_MEDICAMENT_CTJ_PRODUIT = "REAL";
        public static final String CLE_COL_TAUX_DE_TVA_PRODUIT = "Taux_de_TVA";
        public static final int NUM_COL_TAUX_DE_TVA_PRODUIT = 38;
        public static final String TYPE_COL_TAUX_DE_TVA_PRODUIT = "REAL";
        public static final String CLE_COL_GTIN_PRODUIT = "GTIN";
        public static final int NUM_COL_GTIN_PRODUIT = 39;
        public static final String TYPE_COL_GTIN_PRODUIT = "TEXT";
        public static final String CLE_COL_N_INTERNE_PRODUIT = "N_interne";
        public static final int NUM_COL_N_INTERNE_PRODUIT = 40;
        public static final String TYPE_COL_N_INTERNE_PRODUIT = "TEXT";

        public static final String CLE_COL_DESIGNATION_EXT_PRODUIT = "Designation_ext";
        public static final int NUM_COL_DESIGNATION_EXT_PRODUIT = 41;
        public static final String TYPE_COL_DESIGNATION_EXT_PRODUIT = "TEXT";
        public static final String CLE_COL_PEREMPTION_PRODUIT = "Peremption";
        public static final int NUM_COL_PEREMPTION_PRODUIT = 42;
        public static final String TYPE_COL_PEREMPTION_PRODUIT = "INTEGER";
        public static final String CLE_COL_MARCHE_OBTENU_PRODUIT = "Marche_Obtenu";
        public static final int NUM_COL_MARCHE_OBTENU_PRODUIT = 43;
        public static final String TYPE_COL_MARCHE_OBTENU_PRODUIT = "INTEGER";
        public static final String CLE_COL_ORDONNANCE_PRODUIT = "Ordonnance";
        public static final int NUM_COL_ORDONNANCE_PRODUIT = 44;
        public static final String TYPE_COL_ORDONNANCE_PRODUIT = "INTEGER";
        public static final String CLE_COL_SUIVI_LOT_PRODUIT = "Suivi_Lot";
        public static final int NUM_COL_SUIVI_LOT_PRODUIT = 45;
        public static final String TYPE_COL_SUIVI_LOT_PRODUIT = "INTEGER";
        public static final String CLE_COL_REASSORT_PRODUIT = "Reassort";
        public static final int NUM_COL_REASSORT_PRODUIT = 46;
        public static final String TYPE_COL_REASSORT_PRODUIT = "INTEGER";
        public static final String CLE_COL_INCLU_AU_PANEL_PRODUIT = "Inclu_au_panel";
        public static final int NUM_COL_INCLU_AU_PANEL_PRODUIT = 47;
        public static final String TYPE_COL_INCLU_AU_PANEL_PRODUIT = "INTEGER";
        public static final String CLE_COL_RESPECT_COND_ACHAT_PRODUIT = "Respect_Cond_Achat";
        public static final int NUM_COL_RESPECT_COND_ACHAT_PRODUIT = 48;
        public static final String TYPE_COL_RESPECT_COND_ACHAT_PRODUIT = "INTEGER";
        public static final String CLE_COL_ARRET_DIS_PRODUIT = "Arret_Dis";
        public static final int NUM_COL_ARRET_DIS_PRODUIT = 49;
        public static final String TYPE_COL_ARRET_DIS_PRODUIT = "INTEGER";
        public static final String CLE_COL_GRATUIT_PRODUIT = "Gratuit";
        public static final int NUM_COL_GRATUIT_PRODUIT = 50;
        public static final String TYPE_COL_GRATUIT_PRODUIT = "INTEGER";

        public static final String CLE_COL_ARRET_COMMANDE_PRODUIT = "Arret_Commande";
        public static final int NUM_COL_ARRET_COMMANDE_PRODUIT = 51;
        public static final String TYPE_COL_ARRET_COMMANDE_PRODUIT = "INTEGER";
        public static final String CLE_COL_PREV_A_COMMANDER_PRODUIT = "Prev_A_commander";
        public static final int NUM_COL_PREV_A_COMMANDER_PRODUIT = 52;
        public static final String TYPE_COL_PREV_A_COMMANDER_PRODUIT = "INTEGER";
        public static final String CLE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT = "Inscrire_a_ordonnancier";
        public static final int NUM_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT = 53;
        public static final String TYPE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT = "Condition_Refus_Si_Endomage";
        public static final int NUM_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT = 54;
        public static final String TYPE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT = "INTEGER";
        public static final String CLE_COL_CONDITION_PEREMPTION_PRODUIT = "Condition_peremption";
        public static final int NUM_COL_CONDITION_PEREMPTION_PRODUIT = 55;
        public static final String TYPE_COL_CONDITION_PEREMPTION_PRODUIT = "INTEGER";
        public static final String CLE_COL_TRACABILITE_REF_PRODUIT = "Tracabilite_ref";
        public static final int NUM_COL_TRACABILITE_REF_PRODUIT = 56;
        public static final String TYPE_COL_TRACABILITE_REF_PRODUIT = "INTEGER";
        public static final String CLE_COL_TRACABILITE_SN_PRODUIT = "Tracabilite_SN";
        public static final int NUM_COL_TRACABILITE_SN_PRODUIT = 57;
        public static final String TYPE_COL_TRACABILITE_SN_PRODUIT = "INTEGER";
        public static final String CLE_COL_RISQUE_VOIR_NOTICE_PRODUIT = "Risque_voir_notice";
        public static final int NUM_COL_RISQUE_VOIR_NOTICE_PRODUIT = 58;
        public static final String TYPE_COL_RISQUE_VOIR_NOTICE_PRODUIT = "INTEGER";
        public static final String CLE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT = "Risque_Voir_Recommandation";
        public static final int NUM_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT = 59;
        public static final String TYPE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT = "INTEGER";
        public static final String CLE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT = "Distribution_Nominative_Active";
        public static final int NUM_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT = 60;
        public static final String TYPE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT = "INTEGER";

        public static final String CLE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT = "Regle_Bon_Usage_Active";
        public static final int NUM_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT = 61;
        public static final String TYPE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT = "INTEGER";
        public static final String CLE_COL_LIVRET_THERAPEUTIQUE_PRODUIT = "Livret_Therapeutique";
        public static final int NUM_COL_LIVRET_THERAPEUTIQUE_PRODUIT = 62;
        public static final String TYPE_COL_LIVRET_THERAPEUTIQUE_PRODUIT = "INTEGER";
        public static final String CLE_COL_DATE_CREATION_PRODUIT = "Date_Creation_Produit";
        public static final int NUM_COL_DATE_CREATION_PRODUIT = 63;
        public static final String TYPE_COL_DATE_CREATION_PRODUIT = "TEXT";
        public static final String CLE_COL_DATE_ARRET_COM_PRODUIT = "Date_arret_com";
        public static final int NUM_COL_DATE_ARRET_COM_PRODUIT = 64;
        public static final String TYPE_COL_DATE_ARRET_COM_PRODUIT = "TEXT";
        public static final String CLE_COL_DATE_ARRET_DIS_PRODUIT = "Date_Arret_Dis";
        public static final int NUM_COL_DATE_ARRET_DIS_PRODUIT = 65;
        public static final String TYPE_COL_DATE_ARRET_DIS_PRODUIT = "TEXT";
        public static final String CLE_COL_DATE_DER_PHOTO_PRODUIT = "Date_der_photo";
        public static final int NUM_COL_DATE_DER_PHOTO_PRODUIT = 66;
        public static final String TYPE_COL_DATE_DER_PHOTO_PRODUIT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PRODUIT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PRODUIT = 67;
        public static final String TYPE_COL_SYS_DT_MAJ_PRODUIT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PRODUIT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PRODUIT = 68;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PRODUIT = "TEXT";
        public static final String CLE_COL_ZONE_PUI_DEFAUT_PRODUIT = "Zone_PUI_Defaut";
        public static final int NUM_COL_ZONE_PUI_DEFAUT_PRODUIT = 69;
        public static final String TYPE_COL_ZONE_PUI_DEFAUT_PRODUIT = "TEXT";
        public static final String CLE_COL_UNITE_PRODUIT = "Unite";
        public static final int NUM_COL_UNITE_PRODUIT = 70;
        public static final String TYPE_COL_UNITE_PRODUIT = "TEXT";

        public static final String CLE_COL_VILLE_PRODUIT = "Ville";
        public static final int NUM_COL_VILLE_PRODUIT = 71;
        public static final String TYPE_COL_VILLE_PRODUIT = "TEXT";
        public static final String CLE_COL_TYPE_ERREUR_PRODUIT = "Type_erreur";
        public static final int NUM_COL_TYPE_ERREUR_PRODUIT = 72;
        public static final String TYPE_COL_TYPE_ERREUR_PRODUIT = "TEXT";
        public static final String CLE_COL_A_CORRIGER_PRODUIT = "A_corriger";
        public static final int NUM_COL_A_CORRIGER_PRODUIT = 73;
        public static final String TYPE_COL_A_CORRIGER_PRODUIT = "TEXT";
        public static final String CLE_COL_REASSORT_STATUT_PRODUIT = "Reassort_Statut";
        public static final int NUM_COL_REASSORT_STATUT_PRODUIT = 74;
        public static final String TYPE_COL_REASSORT_STATUT_PRODUIT = "TEXT";
        public static final String CLE_COL_MODE_DE_DISTRIBUTION_PRODUIT = "Mode_de_Distribution";
        public static final int NUM_COL_MODE_DE_DISTRIBUTION_PRODUIT = 75;
        public static final String TYPE_COL_MODE_DE_DISTRIBUTION_PRODUIT = "TEXT";
        public static final String CLE_COL_REF_MARCHE_PRODUIT = "Ref_marche";
        public static final int NUM_COL_REF_MARCHE_PRODUIT = 76;
        public static final String TYPE_COL_REF_MARCHE_PRODUIT = "TEXT";
        public static final String CLE_COL_DEVISE_PRODUIT = "Devise";
        public static final int NUM_COL_DEVISE_PRODUIT = 77;
        public static final String TYPE_COL_DEVISE_PRODUIT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PRODUIT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PRODUIT = 78;
        public static final String TYPE_COL_SYS_USER_MAJ_PRODUIT = "TEXT";
        public static final String CLE_COL_PANEL_PRODUIT = "Panel";
        public static final int NUM_COL_PANEL_PRODUIT = 79;
        public static final String TYPE_COL_PANEL_PRODUIT = "TEXT";
        public static final String CLE_COL_TYPE_FRANCO_PRODUIT = "Type_franco";
        public static final int NUM_COL_TYPE_FRANCO_PRODUIT = 80;
        public static final String TYPE_COL_TYPE_FRANCO_PRODUIT = "TEXT";

        public static final String CLE_COL_UCD_CODE_PRODUIT = "UCD_Code";
        public static final int NUM_COL_UCD_CODE_PRODUIT = 81;
        public static final String TYPE_COL_UCD_CODE_PRODUIT = "TEXT";
        public static final String CLE_COL_CODE_CIP_PRODUIT = "Code_CIP";
        public static final int NUM_COL_CODE_CIP_PRODUIT = 82;
        public static final String TYPE_COL_CODE_CIP_PRODUIT = "TEXT";
        public static final String CLE_COL_HISTO_PRIX_UNITAIRE_PRODUIT = "Histo_Prix_Unitaire";
        public static final int NUM_COL_HISTO_PRIX_UNITAIRE_PRODUIT = 83;
        public static final String TYPE_COL_HISTO_PRIX_UNITAIRE_PRODUIT = "TEXT";
        public static final String CLE_COL_DCI_PRODUIT = "DCI";
        public static final int NUM_COL_DCI_PRODUIT = 84;
        public static final String TYPE_COL_DCI_PRODUIT = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_COMMANDE_PRODUIT = "Commentaire_Commande";
        public static final int NUM_COL_COMMENTAIRE_COMMANDE_PRODUIT = 85;
        public static final String TYPE_COL_COMMENTAIRE_COMMANDE_PRODUIT = "TEXT";
        public static final String CLE_COL_CODE_LPP_PRODUIT = "Code_LPP";
        public static final int NUM_COL_CODE_LPP_PRODUIT = 86;
        public static final String TYPE_COL_CODE_LPP_PRODUIT = "TEXT";
        public static final String CLE_COL_PHOTO_PRODUIT = "Photo";
        public static final int NUM_COL_PHOTO_PRODUIT = 87;
        public static final String TYPE_COL_PHOTO_PRODUIT = "TEXT";
        public static final String CLE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT = "Emplacement_PUI_Defaut";
        public static final int NUM_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT = 88;
        public static final String TYPE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT = "TEXT";
        public static final String CLE_COL_DOCUMENTATION_PATH_PRODUIT = "Documentation_Path";
        public static final int NUM_COL_DOCUMENTATION_PATH_PRODUIT = 89;
        public static final String TYPE_COL_DOCUMENTATION_PATH_PRODUIT = "TEXT";
        public static final String CLE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT = "Reapprovisionnement_Classe";
        public static final int NUM_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT = 90;
        public static final String TYPE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT = "TEXT";

        public static final String CLE_COL_ZONE_UF_DEFAUT_PRODUIT = "Zone_UF_Defaut";
        public static final int NUM_COL_ZONE_UF_DEFAUT_PRODUIT = 91;
        public static final String TYPE_COL_ZONE_UF_DEFAUT_PRODUIT = "TEXT";
        public static final String CLE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT = "Emplacement_UF_Defaut";
        public static final int NUM_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT = 92;
        public static final String TYPE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT = "TEXT";
        public static final String CLE_COL_HEMADIALYSE_REFERENCE_PRODUIT = "Hemadialyse_Reference";
        public static final int NUM_COL_HEMADIALYSE_REFERENCE_PRODUIT = 93;
        public static final String TYPE_COL_HEMADIALYSE_REFERENCE_PRODUIT = "TEXT";
        public static final String CLE_COL_XFORME_PRODUIT = "XForme";
        public static final int NUM_COL_XFORME_PRODUIT = 94;
        public static final String TYPE_COL_XFORME_PRODUIT = "TEXT";
        public static final String CLE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT = "Risque_Substance_absence";
        public static final int NUM_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT = 95;
        public static final String TYPE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT = "TEXT";
        public static final String CLE_COL_DOCUMENTATION_WEB_PATH_PRODUIT = "Documentation_Web_Path";
        public static final int NUM_COL_DOCUMENTATION_WEB_PATH_PRODUIT = 96;
        public static final String TYPE_COL_DOCUMENTATION_WEB_PATH_PRODUIT = "TEXT";
        public static final String CLE_COL_MOMENT_INJECTION_PRODUIT = "Moment_Injection";
        public static final int NUM_COL_MOMENT_INJECTION_PRODUIT = 97;
        public static final String TYPE_COL_MOMENT_INJECTION_PRODUIT = "TEXT";
        public static final String CLE_COL_COMMENT_INJECTE_PRODUIT = "Comment_Injecte";
        public static final int NUM_COL_COMMENT_INJECTE_PRODUIT = 98;
        public static final String TYPE_COL_COMMENT_INJECTE_PRODUIT = "TEXT";
        public static final String CLE_COL_COMPOSITION_PRODUIT = "Composition";
        public static final int NUM_COL_COMPOSITION_PRODUIT = 99;
        public static final String TYPE_COL_COMPOSITION_PRODUIT = "TEXT";
        public static final String CLE_COL_ZONE_PAD_DEFAUT_PRODUIT = "Zone_PAD_Defaut";
        public static final int NUM_COL_ZONE_PAD_DEFAUT_PRODUIT = 100;
        public static final String TYPE_COL_ZONE_PAD_DEFAUT_PRODUIT = "TEXT";

        public static final String CLE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT = "Emplacement_PAD_Defaut";
        public static final int NUM_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT = 101;
        public static final String TYPE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT = "TEXT";
        public static final String CLE_COL_UCD_NOMCOURT_PRODUIT = "UCD_NomCourt";
        public static final int NUM_COL_UCD_NOMCOURT_PRODUIT = 102;
        public static final String TYPE_COL_UCD_NOMCOURT_PRODUIT = "TEXT";
        public static final String CLE_COL_PHIE_SYNCHRO_PRODUIT = "PHIE_Synchro";
        public static final int NUM_COL_PHIE_SYNCHRO_PRODUIT = 103;
        public static final String TYPE_COL_PHIE_SYNCHRO_PRODUIT = "TEXT";
        public static final String CLE_COL_CODE_FOURN_PRODUIT = "Code_fourn";
        public static final int NUM_COL_CODE_FOURN_PRODUIT = 104;
        public static final String TYPE_COL_CODE_FOURN_PRODUIT = "INTEGER";
        public static final String CLE_COL_DUREE_PEREMPTION_PRODUIT = "Duree_peremption";
        public static final int NUM_COL_DUREE_PEREMPTION_PRODUIT = 105;
        public static final String TYPE_COL_DUREE_PEREMPTION_PRODUIT = "INTEGER";
        public static final String CLE_COL_RGB_RED_PRODUIT = "RGB_Red";
        public static final int NUM_COL_RGB_RED_PRODUIT = 106;
        public static final String TYPE_COL_RGB_RED_PRODUIT = "INTEGER";
        public static final String CLE_COL_RGB_GREEN_PRODUIT = "RGB_Green";
        public static final int NUM_COL_RGB_GREEN_PRODUIT = 107;
        public static final String TYPE_COL_RGB_GREEN_PRODUIT = "INTEGER";
        public static final String CLE_COL_RGB_BLUE_PRODUIT = "RGB_Blue";
        public static final int NUM_COL_RGB_BLUE_PRODUIT = 108;
        public static final String TYPE_COL_RGB_BLUE_PRODUIT = "INTEGER";
        public static final String CLE_COL_CLASSE_NUMERO_PRODUIT = "Classe_numero";
        public static final int NUM_COL_CLASSE_NUMERO_PRODUIT = 109;
        public static final String TYPE_COL_CLASSE_NUMERO_PRODUIT = "INTEGER";
        public static final String CLE_COL_NB_LIGNE_CODE_BARRE_PRODUIT = "Nb_Ligne_Code_Barre";
        public static final int NUM_COL_NB_LIGNE_CODE_BARRE_PRODUIT = 110;
        public static final String TYPE_COL_NB_LIGNE_CODE_BARRE_PRODUIT = "INTEGER";

        public static final String CLE_COL_STOCK_GLOBAL_PRODUIT = "Stock_Global";
        public static final int NUM_COL_STOCK_GLOBAL_PRODUIT = 111;
        public static final String TYPE_COL_STOCK_GLOBAL_PRODUIT = "REAL";
        public static final String CLE_COL_VALEUR_STOCK_GLOBAL_PRODUIT = "Valeur_Stock_Global";
        public static final int NUM_COL_VALEUR_STOCK_GLOBAL_PRODUIT = 112;
        public static final String TYPE_COL_VALEUR_STOCK_GLOBAL_PRODUIT = "REAL";
        public static final String CLE_COL_COND_FRANCO_PRODUIT = "Cond_franco";
        public static final int NUM_COL_COND_FRANCO_PRODUIT = 113;
        public static final String TYPE_COL_COND_FRANCO_PRODUIT = "REAL";
        public static final String CLE_COL_STOCK_CLOT_PRODUIT = "Stock_clot";
        public static final int NUM_COL_STOCK_CLOT_PRODUIT = 114;
        public static final String TYPE_COL_STOCK_CLOT_PRODUIT = "REAL";
        public static final String CLE_COL_VALEUR_STOCK_ACTUEL_PRODUIT = "Valeur_stock_actuel";
        public static final int NUM_COL_VALEUR_STOCK_ACTUEL_PRODUIT = 115;
        public static final String TYPE_COL_VALEUR_STOCK_ACTUEL_PRODUIT = "INTEGER";
        public static final String CLE_COL_COND_ACHAT_PRODUIT = "Cond_achat";
        public static final int NUM_COL_COND_ACHAT_PRODUIT = 116;
        public static final String TYPE_COL_COND_ACHAT_PRODUIT = "REAL";
        public static final String CLE_COL_COND_DISTRIB_PRODUIT = "Cond_distrib";
        public static final int NUM_COL_COND_DISTRIB_PRODUIT = 117;
        public static final String TYPE_COL_COND_DISTRIB_PRODUIT = "REAL";
        public static final String CLE_COL_SEUIL_ALERTE_PRODUIT = "Seuil_alerte";
        public static final int NUM_COL_SEUIL_ALERTE_PRODUIT = 118;
        public static final String TYPE_COL_SEUIL_ALERTE_PRODUIT = "REAL";
        public static final String CLE_COL_QTE_REASSORT_PRODUIT = "Qte_Reassort";
        public static final int NUM_COL_QTE_REASSORT_PRODUIT = 119;
        public static final String TYPE_COL_QTE_REASSORT_PRODUIT = "REAL";
        public static final String CLE_COL_NOUVEAU_PU_PRODUIT = "Nouveau_PU";
        public static final int NUM_COL_NOUVEAU_PU_PRODUIT = 120;
        public static final String TYPE_COL_NOUVEAU_PU_PRODUIT = "REAL";

        public static final String CLE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT = "Cond_Achat_Gros_volume";
        public static final int NUM_COL_COND_ACHAT_GROS_VOLUME_PRODUIT = 121;
        public static final String TYPE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT = "REAL";
        public static final String CLE_COL_COULEUR_PRODUIT = "Couleur";
        public static final int NUM_COL_COULEUR_PRODUIT = 122;
        public static final String TYPE_COL_COULEUR_PRODUIT = "REAL";
        public static final String CLE_COL_INVENTAIRE1_PUMP_HT_PRODUIT = "Inventaire1_PUMP_HT";
        public static final int NUM_COL_INVENTAIRE1_PUMP_HT_PRODUIT = 123;
        public static final String TYPE_COL_INVENTAIRE1_PUMP_HT_PRODUIT = "REAL";
        public static final String CLE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT = "PUMP_TTC_Exercice_Prec";
        public static final int NUM_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT = 124;
        public static final String TYPE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT = "REAL";
        public static final String CLE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT = "Qte_inventaire_exercice_prec";
        public static final int NUM_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT = 125;
        public static final String TYPE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT = "REAL";
        public static final String CLE_COL_STOCK_ACTUEL_PRODUIT = "Stock_Actuel";
        public static final int NUM_COL_STOCK_ACTUEL_PRODUIT = 126;
        public static final String TYPE_COL_STOCK_ACTUEL_PRODUIT = "REAL";
        public static final String CLE_COL_QTE_A_COMMANDER_PRODUIT = "Qte_a_commander";
        public static final int NUM_COL_QTE_A_COMMANDER_PRODUIT = 127;
        public static final String TYPE_COL_QTE_A_COMMANDER_PRODUIT = "REAL";
        public static final String CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT = "PUMP_TTC_derniere_cloture";
        public static final int NUM_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT = 128;
        public static final String TYPE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT = "REAL";
        public static final String CLE_COL_POIDS_PRODUIT = "Poids";
        public static final int NUM_COL_POIDS_PRODUIT = 129;
        public static final String TYPE_COL_POIDS_PRODUIT = "REAL";
        public static final String CLE_COL_VOLUME_PRODUIT = "Volume";
        public static final int NUM_COL_VOLUME_PRODUIT = 130;
        public static final String TYPE_COL_VOLUME_PRODUIT = "REAL";

        public static final String CLE_COL_CONSERVATION_HYDRO_MIN_PRODUIT = "Conservation_hydro_min";
        public static final int NUM_COL_CONSERVATION_HYDRO_MIN_PRODUIT = 131;
        public static final String TYPE_COL_CONSERVATION_HYDRO_MIN_PRODUIT = "REAL";
        public static final String CLE_COL_CONSERVATION_HYDRO_MAX_PRODUIT = "Conservation_hydro_max";
        public static final int NUM_COL_CONSERVATION_HYDRO_MAX_PRODUIT = 132;
        public static final String TYPE_COL_CONSERVATION_HYDRO_MAX_PRODUIT = "REAL";
        public static final String CLE_COL_CONSERVATION_PRESSION_MIN_PRODUIT = "Conservation_pression_min";
        public static final int NUM_COL_CONSERVATION_PRESSION_MIN_PRODUIT = 133;
        public static final String TYPE_COL_CONSERVATION_PRESSION_MIN_PRODUIT = "REAL";
        public static final String CLE_COL_CONSERVATION_PRESSION_MAX_PRODUIT = "Conservation_pression_max";
        public static final int NUM_COL_CONSERVATION_PRESSION_MAX_PRODUIT = 134;
        public static final String TYPE_COL_CONSERVATION_PRESSION_MAX_PRODUIT = "REAL";
        public static final String CLE_COL_ID_PRODUIT = "ID_produit";
        public static final int NUM_COL_ID_PRODUIT = 135;
        public static final String TYPE_COL_ID_PRODUIT = "INTEGER";

        public static final String CLE_COL_CODE_INCONNU = "CodeInconnue";
        public static final int NUM_COL_CODE_INCONNU = 136;
        public static final String TYPE_COL_CODE_INCONNU = "TEXT";

            public static final String CLE_COL_SUIVI_SERIALISATION = "Suivi_Serialisation";
        public static final int NUM_COL_SUIVI_SERIALISATION = 137;
        public static final String TYPE_COL_SUIVI_SERIALISATION = "INTEGER";

        public static final String CLE_COL_SERIALISER_RECEPTION_DELIVRANCE = "Serialiser_Reception_Delivrance";
        public static final int NUM_COL_SERIALISER_RECEPTION_DELIVRANCE = 138;
        public static final String TYPE_COL_SERIALISER_RECEPTION_DELIVRANCE = "INTEGER";


        public static final String CREATION_TABLE_PRODUIT = "CREATE TABLE "
                + Constantes.TABLE_PRODUIT + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_DESIGNATION_INTERNE_PRODUIT + " " + Constantes.TYPE_COL_DESIGNATION_INTERNE_PRODUIT + ","
                + Constantes.CLE_COL_REF_FOURNI_PRODUIT + " " + Constantes.TYPE_COL_REF_FOURNI_PRODUIT + ","
                + Constantes.CLE_COL_INFORMATION_IMPORTANTES_PRODUIT + " " + Constantes.TYPE_COL_INFORMATION_IMPORTANTES_PRODUIT + ","
                + Constantes.CLE_COL_CONDITION_USAGE_UNIQUE_PRODUIT + " " + Constantes.TYPE_COL_CONDITION_USAGE_UNIQUE_PRODUIT + ","
                + Constantes.CLE_COL_STERILE_PRODUIT + " " + Constantes.TYPE_COL_STERILE_PRODUIT + ","
                + Constantes.CLE_COL_STERILISATION_MODE_PRODUIT + " " + Constantes.TYPE_COL_STERILISATION_MODE_PRODUIT + ","
                + Constantes.CLE_COL_NEPASRESTERILISER_PRODUIT + " " + Constantes.TYPE_COL_NEPASRESTERILISER_PRODUIT + ","
                + Constantes.CLE_COL_CATEGORIE_PRODUIT + " " + Constantes.TYPE_COL_CATEGORIE_PRODUIT + ","
                + Constantes.CLE_COL_FOURNISSEUR_PRODUIT + " " + Constantes.TYPE_COL_FOURNISSEUR_PRODUIT + ","
                + Constantes.CLE_COL_PRIX_UNITAIRE_PRODUIT + " " + Constantes.TYPE_COL_PRIX_UNITAIRE_PRODUIT + ","
                + Constantes.CLE_COL_FORME_PRODUIT + " " + Constantes.TYPE_COL_FORME_PRODUIT + ","
                + Constantes.CLE_COL_CONTENANT_PRODUIT + " " + Constantes.TYPE_COL_CONTENANT_PRODUIT + ","
                + Constantes.CLE_COL_MATERIAUX_PRODUIT + " " + Constantes.TYPE_COL_MATERIAUX_PRODUIT + ","
                + Constantes.CLE_COL_STATUT_PRODUIT + " " + Constantes.TYPE_COL_STATUT_PRODUIT + ","
                + Constantes.CLE_COL_SECTEUR_PRODUIT + " " + Constantes.TYPE_COL_SECTEUR_PRODUIT + ","
                + Constantes.CLE_COL_COMMENTAIRE_PRODUIT + " " + Constantes.TYPE_COL_COMMENTAIRE_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_PHT_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_PHT_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_LATEX_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_LATEX_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_PRODUIT + ","
                + Constantes.CLE_COL_TEMPERATURE_REFRIGERE_PRODUIT + " " + Constantes.TYPE_COL_TEMPERATURE_REFRIGERE_PRODUIT + ","
                + Constantes.CLE_COL_TEMPERATURE_AMBIANTE_PRODUIT + " " + Constantes.TYPE_COL_TEMPERATURE_AMBIANTE_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_ABRI_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_ABRI_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_SEC_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_SEC_PRODUIT + ","
                + Constantes.CLE_COL_CONDITION_FRAGILE_PRODUIT + " " + Constantes.TYPE_COL_CONDITION_FRAGILE_PRODUIT + ","
                + Constantes.CLE_COL_MEDICAMENT_RISQUE_PRODUIT + " " + Constantes.TYPE_COL_MEDICAMENT_RISQUE_PRODUIT + ","
                + Constantes.CLE_COL_CONTRE_INDICATIONS_PRODUIT + " " + Constantes.TYPE_COL_CONTRE_INDICATIONS_PRODUIT + ","
                + Constantes.CLE_COL_EFFETS_INDESIRABLES_PRODUIT + " " + Constantes.TYPE_COL_EFFETS_INDESIRABLES_PRODUIT + ","
                + Constantes.CLE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT + " " + Constantes.TYPE_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT + ","
                + Constantes.CLE_COL_MEDICAMENT_LISTE_PRODUIT + " " + Constantes.TYPE_COL_MEDICAMENT_LISTE_PRODUIT + ","
                + Constantes.CLE_COL_POSOLOGIE_PRODUIT + " " + Constantes.TYPE_COL_POSOLOGIE_PRODUIT + ","
                + Constantes.CLE_COL_VOIE_PRODUIT + " " + Constantes.TYPE_COL_VOIE_PRODUIT + ","
                + Constantes.CLE_COL_INDICATION_THERAPEUTIQUE_PRODUIT + " " + Constantes.TYPE_COL_INDICATION_THERAPEUTIQUE_PRODUIT + ","
                + Constantes.CLE_COL_UI_CONVERSION_PRODUIT + " " + Constantes.TYPE_COL_UI_CONVERSION_PRODUIT + ","
                + Constantes.CLE_COL_MEDICAMENT_CTJ_PRODUIT + " " + Constantes.TYPE_COL_MEDICAMENT_CTJ_PRODUIT + ","
                + Constantes.CLE_COL_TAUX_DE_TVA_PRODUIT + " " + Constantes.TYPE_COL_TAUX_DE_TVA_PRODUIT + ","
                + Constantes.CLE_COL_GTIN_PRODUIT + " " + Constantes.TYPE_COL_GTIN_PRODUIT + ","
                + Constantes.CLE_COL_N_INTERNE_PRODUIT + " " + Constantes.TYPE_COL_N_INTERNE_PRODUIT + ","
                + Constantes.CLE_COL_DESIGNATION_EXT_PRODUIT + " " + Constantes.TYPE_COL_DESIGNATION_EXT_PRODUIT + ","
                + Constantes.CLE_COL_PEREMPTION_PRODUIT + " " + Constantes.TYPE_COL_PEREMPTION_PRODUIT + ","
                + Constantes.CLE_COL_MARCHE_OBTENU_PRODUIT + " " + Constantes.TYPE_COL_MARCHE_OBTENU_PRODUIT + ","
                + Constantes.CLE_COL_ORDONNANCE_PRODUIT + " " + Constantes.TYPE_COL_ORDONNANCE_PRODUIT + ","
                + Constantes.CLE_COL_SUIVI_LOT_PRODUIT + " " + Constantes.TYPE_COL_SUIVI_LOT_PRODUIT + ","
                + Constantes.CLE_COL_REASSORT_PRODUIT + " " + Constantes.TYPE_COL_REASSORT_PRODUIT + ","
                + Constantes.CLE_COL_INCLU_AU_PANEL_PRODUIT + " " + Constantes.TYPE_COL_INCLU_AU_PANEL_PRODUIT + ","
                + Constantes.CLE_COL_RESPECT_COND_ACHAT_PRODUIT + " " + Constantes.TYPE_COL_RESPECT_COND_ACHAT_PRODUIT + ","
                + Constantes.CLE_COL_ARRET_DIS_PRODUIT + " " + Constantes.TYPE_COL_ARRET_DIS_PRODUIT + ","
                + Constantes.CLE_COL_GRATUIT_PRODUIT + " " + Constantes.TYPE_COL_GRATUIT_PRODUIT + ","
                + Constantes.CLE_COL_ARRET_COMMANDE_PRODUIT + " " + Constantes.TYPE_COL_ARRET_COMMANDE_PRODUIT + ","
                + Constantes.CLE_COL_PREV_A_COMMANDER_PRODUIT + " " + Constantes.TYPE_COL_PREV_A_COMMANDER_PRODUIT + ","
                + Constantes.CLE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT + " " + Constantes.TYPE_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT + ","
                + Constantes.CLE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT + " " + Constantes.TYPE_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT + ","
                + Constantes.CLE_COL_CONDITION_PEREMPTION_PRODUIT + " " + Constantes.TYPE_COL_CONDITION_PEREMPTION_PRODUIT + ","
                + Constantes.CLE_COL_TRACABILITE_REF_PRODUIT + " " + Constantes.TYPE_COL_TRACABILITE_REF_PRODUIT + ","
                + Constantes.CLE_COL_TRACABILITE_SN_PRODUIT + " " + Constantes.TYPE_COL_TRACABILITE_SN_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_VOIR_NOTICE_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_VOIR_NOTICE_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT + ","
                + Constantes.CLE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT + " " + Constantes.TYPE_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT + ","
                + Constantes.CLE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT + " " + Constantes.TYPE_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT + ","
                + Constantes.CLE_COL_LIVRET_THERAPEUTIQUE_PRODUIT + " " + Constantes.TYPE_COL_LIVRET_THERAPEUTIQUE_PRODUIT + ","
                + Constantes.CLE_COL_DATE_CREATION_PRODUIT + " " + Constantes.TYPE_COL_DATE_CREATION_PRODUIT + ","
                + Constantes.CLE_COL_DATE_ARRET_COM_PRODUIT + " " + Constantes.TYPE_COL_DATE_ARRET_COM_PRODUIT + ","
                + Constantes.CLE_COL_DATE_ARRET_DIS_PRODUIT + " " + Constantes.TYPE_COL_DATE_ARRET_DIS_PRODUIT + ","
                + Constantes.CLE_COL_DATE_DER_PHOTO_PRODUIT + " " + Constantes.TYPE_COL_DATE_DER_PHOTO_PRODUIT + ","
                + Constantes.CLE_COL_SYS_DT_MAJ_PRODUIT + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PRODUIT + ","
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PRODUIT + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PRODUIT + ","
                + Constantes.CLE_COL_ZONE_PUI_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_ZONE_PUI_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_UNITE_PRODUIT + " " + Constantes.TYPE_COL_UNITE_PRODUIT + ","
                + Constantes.CLE_COL_VILLE_PRODUIT + " " + Constantes.TYPE_COL_VILLE_PRODUIT + ","
                + Constantes.CLE_COL_TYPE_ERREUR_PRODUIT + " " + Constantes.TYPE_COL_TYPE_ERREUR_PRODUIT + ","
                + Constantes.CLE_COL_A_CORRIGER_PRODUIT + " " + Constantes.TYPE_COL_A_CORRIGER_PRODUIT + ","
                + Constantes.CLE_COL_REASSORT_STATUT_PRODUIT + " " + Constantes.TYPE_COL_REASSORT_STATUT_PRODUIT + ","
                + Constantes.CLE_COL_MODE_DE_DISTRIBUTION_PRODUIT + " " + Constantes.TYPE_COL_MODE_DE_DISTRIBUTION_PRODUIT + ","
                + Constantes.CLE_COL_REF_MARCHE_PRODUIT + " " + Constantes.TYPE_COL_REF_MARCHE_PRODUIT + ","
                + Constantes.CLE_COL_DEVISE_PRODUIT + " " + Constantes.TYPE_COL_DEVISE_PRODUIT + ","
                + Constantes.CLE_COL_SYS_USER_MAJ_PRODUIT + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PRODUIT + ","
                + Constantes.CLE_COL_PANEL_PRODUIT + " " + Constantes.TYPE_COL_PANEL_PRODUIT + ","
                + Constantes.CLE_COL_TYPE_FRANCO_PRODUIT + " " + Constantes.TYPE_COL_TYPE_FRANCO_PRODUIT + ","
                + Constantes.CLE_COL_UCD_CODE_PRODUIT + " " + Constantes.TYPE_COL_UCD_CODE_PRODUIT + ","
                + Constantes.CLE_COL_CODE_CIP_PRODUIT + " " + Constantes.TYPE_COL_CODE_CIP_PRODUIT + ","
                + Constantes.CLE_COL_HISTO_PRIX_UNITAIRE_PRODUIT + " " + Constantes.TYPE_COL_HISTO_PRIX_UNITAIRE_PRODUIT + ","
                + Constantes.CLE_COL_DCI_PRODUIT + " " + Constantes.TYPE_COL_DCI_PRODUIT + ","
                + Constantes.CLE_COL_COMMENTAIRE_COMMANDE_PRODUIT + " " + Constantes.TYPE_COL_COMMENTAIRE_COMMANDE_PRODUIT + ","
                + Constantes.CLE_COL_CODE_LPP_PRODUIT + " " + Constantes.TYPE_COL_CODE_LPP_PRODUIT + ","
                + Constantes.CLE_COL_PHOTO_PRODUIT + " " + Constantes.TYPE_COL_PHOTO_PRODUIT + ","
                + Constantes.CLE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_DOCUMENTATION_PATH_PRODUIT + " " + Constantes.TYPE_COL_DOCUMENTATION_PATH_PRODUIT + ","
                + Constantes.CLE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT + " " + Constantes.TYPE_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT + ","
                + Constantes.CLE_COL_ZONE_UF_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_ZONE_UF_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_HEMADIALYSE_REFERENCE_PRODUIT + " " + Constantes.TYPE_COL_HEMADIALYSE_REFERENCE_PRODUIT + ","
                + Constantes.CLE_COL_XFORME_PRODUIT + " " + Constantes.TYPE_COL_XFORME_PRODUIT + ","
                + Constantes.CLE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT + " " + Constantes.TYPE_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT + ","
                + Constantes.CLE_COL_DOCUMENTATION_WEB_PATH_PRODUIT + " " + Constantes.TYPE_COL_DOCUMENTATION_WEB_PATH_PRODUIT + ","
                + Constantes.CLE_COL_MOMENT_INJECTION_PRODUIT + " " + Constantes.TYPE_COL_MOMENT_INJECTION_PRODUIT + ","
                + Constantes.CLE_COL_COMMENT_INJECTE_PRODUIT + " " + Constantes.TYPE_COL_COMMENT_INJECTE_PRODUIT + ","
                + Constantes.CLE_COL_COMPOSITION_PRODUIT + " " + Constantes.TYPE_COL_COMPOSITION_PRODUIT + ","
                + Constantes.CLE_COL_ZONE_PAD_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_ZONE_PAD_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT + " " + Constantes.TYPE_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT + ","
                + Constantes.CLE_COL_UCD_NOMCOURT_PRODUIT + " " + Constantes.TYPE_COL_UCD_NOMCOURT_PRODUIT + ","
                + Constantes.CLE_COL_PHIE_SYNCHRO_PRODUIT + " " + Constantes.TYPE_COL_PHIE_SYNCHRO_PRODUIT + ","
                + Constantes.CLE_COL_CODE_FOURN_PRODUIT + " " + Constantes.TYPE_COL_CODE_FOURN_PRODUIT + ","
                + Constantes.CLE_COL_DUREE_PEREMPTION_PRODUIT + " " + Constantes.TYPE_COL_DUREE_PEREMPTION_PRODUIT + ","
                + Constantes.CLE_COL_RGB_RED_PRODUIT + " " + Constantes.TYPE_COL_RGB_RED_PRODUIT + ","
                + Constantes.CLE_COL_RGB_GREEN_PRODUIT + " " + Constantes.TYPE_COL_RGB_GREEN_PRODUIT + ","
                + Constantes.CLE_COL_RGB_BLUE_PRODUIT + " " + Constantes.TYPE_COL_RGB_BLUE_PRODUIT + ","
                + Constantes.CLE_COL_CLASSE_NUMERO_PRODUIT + " " + Constantes.TYPE_COL_CLASSE_NUMERO_PRODUIT + ","
                + Constantes.CLE_COL_NB_LIGNE_CODE_BARRE_PRODUIT + " " + Constantes.TYPE_COL_NB_LIGNE_CODE_BARRE_PRODUIT + ","
                + Constantes.CLE_COL_STOCK_GLOBAL_PRODUIT + " " + Constantes.TYPE_COL_STOCK_GLOBAL_PRODUIT + ","
                + Constantes.CLE_COL_VALEUR_STOCK_GLOBAL_PRODUIT + " " + Constantes.TYPE_COL_VALEUR_STOCK_GLOBAL_PRODUIT + ","
                + Constantes.CLE_COL_COND_FRANCO_PRODUIT + " " + Constantes.TYPE_COL_COND_FRANCO_PRODUIT + ","
                + Constantes.CLE_COL_STOCK_CLOT_PRODUIT + " " + Constantes.TYPE_COL_STOCK_CLOT_PRODUIT + ","
                + Constantes.CLE_COL_VALEUR_STOCK_ACTUEL_PRODUIT + " " + Constantes.TYPE_COL_VALEUR_STOCK_ACTUEL_PRODUIT + ","
                + Constantes.CLE_COL_COND_ACHAT_PRODUIT + " " + Constantes.TYPE_COL_COND_ACHAT_PRODUIT + ","
                + Constantes.CLE_COL_COND_DISTRIB_PRODUIT + " " + Constantes.TYPE_COL_COND_DISTRIB_PRODUIT + ","
                + Constantes.CLE_COL_SEUIL_ALERTE_PRODUIT + " " + Constantes.TYPE_COL_SEUIL_ALERTE_PRODUIT + ","
                + Constantes.CLE_COL_QTE_REASSORT_PRODUIT + " " + Constantes.TYPE_COL_QTE_REASSORT_PRODUIT + ","
                + Constantes.CLE_COL_NOUVEAU_PU_PRODUIT + " " + Constantes.TYPE_COL_NOUVEAU_PU_PRODUIT + ","
                + Constantes.CLE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT + " " + Constantes.TYPE_COL_COND_ACHAT_GROS_VOLUME_PRODUIT + ","
                + Constantes.CLE_COL_COULEUR_PRODUIT + " " + Constantes.TYPE_COL_COULEUR_PRODUIT + ","
                + Constantes.CLE_COL_INVENTAIRE1_PUMP_HT_PRODUIT + " " + Constantes.TYPE_COL_INVENTAIRE1_PUMP_HT_PRODUIT + ","
                + Constantes.CLE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT + " " + Constantes.TYPE_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT + ","
                + Constantes.CLE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT + " " + Constantes.TYPE_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT + ","
                + Constantes.CLE_COL_STOCK_ACTUEL_PRODUIT + " " + Constantes.TYPE_COL_STOCK_ACTUEL_PRODUIT + ","
                + Constantes.CLE_COL_QTE_A_COMMANDER_PRODUIT + " " + Constantes.TYPE_COL_QTE_A_COMMANDER_PRODUIT + ","
                + Constantes.CLE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT + " " + Constantes.TYPE_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT + ","
                + Constantes.CLE_COL_POIDS_PRODUIT + " " + Constantes.TYPE_COL_POIDS_PRODUIT + ","
                + Constantes.CLE_COL_VOLUME_PRODUIT + " " + Constantes.TYPE_COL_VOLUME_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_HYDRO_MIN_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_HYDRO_MIN_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_HYDRO_MAX_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_HYDRO_MAX_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_PRESSION_MIN_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_PRESSION_MIN_PRODUIT + ","
                + Constantes.CLE_COL_CONSERVATION_PRESSION_MAX_PRODUIT + " " + Constantes.TYPE_COL_CONSERVATION_PRESSION_MAX_PRODUIT + ","
                + Constantes.CLE_COL_ID_PRODUIT + " " + Constantes.TYPE_COL_ID_PRODUIT + ","
                + Constantes.CLE_COL_CODE_INCONNU + " " + Constantes.TYPE_COL_CODE_INCONNU+ ","
                + Constantes.CLE_COL_SUIVI_SERIALISATION + " " + Constantes.TYPE_COL_SUIVI_SERIALISATION + ","
                + Constantes.CLE_COL_SERIALISER_RECEPTION_DELIVRANCE + " " + Constantes.TYPE_COL_SERIALISER_RECEPTION_DELIVRANCE
                + ");";
    }
}
