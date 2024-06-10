package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import androidx.annotation.RequiresApi;
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
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;

public class PH_PreparationOpenHelper extends DBOpenHelper {

    public PH_PreparationOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePH_Preparations(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_PREPARATION, null, null);
    }

    public static PH_Preparation getDemandeDemandeReassortEnInstance(SQLiteDatabase db, String Liste, String DateProchaineLivraison) {
        PH_Preparation ph_preparation = null;

        if(DateProchaineLivraison.contains("/"))
        {
            String tab[] = DateProchaineLivraison.split("/");
            String annee = tab[tab.length-1];
            String mois = tab[1];
            String jour = tab[0];
            DateProchaineLivraison = annee+"-"+mois+"-"+jour;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?  AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ? ", new String[]{Liste, "En instance", DateProchaineLivraison});
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            ph_preparation = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?" , new String[]{Liste, "En cours de régularisation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de préparation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de livraison", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }
        return ph_preparation;
    }

    public static long insererUnPH_PreparationEnBDD(SQLiteDatabase db, PH_Preparation ph_preparation) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID_PH_PREPARATION, ph_preparation.getUID());
        contentValues.put(Constantes.CLE_COL_SERVICE_PH_PREPARATION, ph_preparation.getService());
        contentValues.put(Constantes.CLE_COL_ERREUR_VALID_PH_PREPARATION, ph_preparation.getErreur_Valid());
        contentValues.put(Constantes.CLE_COL_PHIE_TAG_PH_PREPARATION, ph_preparation.getPHIE_Tag());
        contentValues.put(Constantes.CLE_COL_SAISIE_LE_PH_PREPARATION, ph_preparation.getSaisie_Le());
        contentValues.put(Constantes.CLE_COL_A_TEL_HEURE_PH_PREPARATION, ph_preparation.getA_tel_heure());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_PREPARATION, ph_preparation.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION, ph_preparation.getProduitDesignation());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDEE_PH_PREPARATION, ph_preparation.getQte_demandee());
        contentValues.put(Constantes.CLE_COL_LIVREE_PH_PREPARATION, ph_preparation.getLivree());
        contentValues.put(Constantes.CLE_COL_VALIDEE_PH_PREPARATION, ph_preparation.getValidee());
        contentValues.put(Constantes.CLE_COL_ORIGINE_PH_PREPARATION, ph_preparation.getOrigine());
        contentValues.put(Constantes.CLE_COL_LISTE_PH_PREPARATION, ph_preparation.getListe());
        contentValues.put(Constantes.CLE_COL_DEPOTDESTINATAIREID_PH_PREPARATION, ph_preparation.getDepotDestinataireID());
        contentValues.put(Constantes.CLE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION, ph_preparation.getDepotDestinataireReference());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION, ph_preparation.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION, ph_preparation.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION, ph_preparation.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION, ph_preparation.getPrescripteurReference());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTION_DATE_PH_PREPARATION, ph_preparation.getPrescription_date());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION, ph_preparation.getPrescripteurNom());
        contentValues.put(Constantes.CLE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION, ph_preparation.getDepotOrigineReference());
        contentValues.put(Constantes.CLE_COL_DEPOTORIGINEID_PH_PREPARATION, ph_preparation.getDepotOrigineID());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRES_PH_PREPARATION, ph_preparation.getCommentaires());
        contentValues.put(Constantes.CLE_COL_PREPARATIONDATE_PH_PREPARATION, ph_preparation.getPreparationDate());
        contentValues.put(Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION, ph_preparation.getLivraisonPrevueDate());
        contentValues.put(Constantes.CLE_COL_DN_GROUPE_PH_PREPARATION, ph_preparation.getDN_Groupe());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION, ph_preparation.getMontant_HT());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION, ph_preparation.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_POIDS_PH_PREPARATION, ph_preparation.getPoids());
        contentValues.put(Constantes.CLE_COL_COMMANDE_ID_PH_PREPARATION, ph_preparation.getCommande_ID());
        contentValues.put(Constantes.CLE_COL_PREPARATEUR_PH_PREPARATION, ph_preparation.getPreparateur());
        contentValues.put(Constantes.CLE_COL_STATUT_PH_PREPARATION, ph_preparation.getStatut());
        contentValues.put(Constantes.CLE_COL_PHIE_SYNCHRO_PH_PREPARATION, ph_preparation.getPHIE_SYNCHRO());
        contentValues.put(Constantes.CLE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION, ph_preparation.getReceptionUFNonComforme());
        contentValues.put(Constantes.CLE_COL_LIVRAISONDATE_PH_PREPARATION, ph_preparation.getLivraisonDate());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_PH_PREPARATION, ph_preparation.getFrequence());
        contentValues.put(Constantes.CLE_COL_PREVISIONDATEDEBUT_PH_PREPARATION, ph_preparation.getPrevisionDateDebut());
        contentValues.put(Constantes.CLE_COL_PREVISIONDATEFIN_PH_PREPARATION, ph_preparation.getPrevisionDateFin());
        contentValues.put(Constantes.CLE_COL_URGENT_PH_PREPARATION, ph_preparation.getURGENT());
        contentValues.put(Constantes.CLE_COL_MOTIF_PH_PREPARATION, ph_preparation.getMotif());
        contentValues.put(Constantes.CLE_COL_PREPARATEUR_USERID_PH_PREPARATION, ph_preparation.getPreparateur());
        contentValues.put(Constantes.CLE_COL_PHARMACIEN_USERID_PH_PREPARATION, ph_preparation.getPharmacien_userID());
        contentValues.put(Constantes.CLE_COL_VOLUME_PH_PREPARATION, ph_preparation.getVolume());
        contentValues.put(Constantes.CLE_COL_PALETTENB_PH_PREPARATION, ph_preparation.getPaletteNB());
        contentValues.put(Constantes.CLE_COL_CAISSENB_PH_PREPARATION, ph_preparation.getColisNB());
        contentValues.put(Constantes.CLE_COL_CONTENEUR_NB, ph_preparation.getConteneur_NB());
        contentValues.put(Constantes.CLE_COL_NUMERO_SCELLE, ph_preparation.getNumero_scelle());
        contentValues.put(Constantes.CLE_COL_LIVREUR_USERID, ph_preparation.getLivreur_userID());
        contentValues.put(Constantes.CLE_COL_SIGNATURE_LIVRAISON, ph_preparation.getSignature_Livraison());
        contentValues.put(Constantes.CLE_COL_TEMPS_PREPARATION, ph_preparation.getTempsPreparation());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_A, ph_preparation.getDelivranceValider_A());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_LE, ph_preparation.getDelivranceValider_Le());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_PAR, ph_preparation.getDelivranceValider_Par());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_PH_PREPARATION, null, contentValues);

        ph_preparation.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }

    public static long mettreAJourUnPHPreparation(SQLiteDatabase db, PH_Preparation ph_preparation) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_UID_PH_PREPARATION, ph_preparation.getUID());
        contentValues.put(Constantes.CLE_COL_SERVICE_PH_PREPARATION, ph_preparation.getService());
        contentValues.put(Constantes.CLE_COL_ERREUR_VALID_PH_PREPARATION, ph_preparation.getErreur_Valid());
        contentValues.put(Constantes.CLE_COL_PHIE_TAG_PH_PREPARATION, ph_preparation.getPHIE_Tag());
        contentValues.put(Constantes.CLE_COL_SAISIE_LE_PH_PREPARATION, ph_preparation.getSaisie_Le());
        contentValues.put(Constantes.CLE_COL_A_TEL_HEURE_PH_PREPARATION, ph_preparation.getA_tel_heure());
        contentValues.put(Constantes.CLE_COL_PRODUITID_PH_PREPARATION, ph_preparation.getProduitID());
        contentValues.put(Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION, ph_preparation.getProduitDesignation());
        contentValues.put(Constantes.CLE_COL_QTE_DEMANDEE_PH_PREPARATION, ph_preparation.getQte_demandee());
        contentValues.put(Constantes.CLE_COL_LIVREE_PH_PREPARATION, ph_preparation.getLivree());
        contentValues.put(Constantes.CLE_COL_VALIDEE_PH_PREPARATION, ph_preparation.getValidee());
        contentValues.put(Constantes.CLE_COL_ORIGINE_PH_PREPARATION, ph_preparation.getOrigine());
        contentValues.put(Constantes.CLE_COL_LISTE_PH_PREPARATION, ph_preparation.getListe());
        contentValues.put(Constantes.CLE_COL_DEPOTDESTINATAIREID_PH_PREPARATION, ph_preparation.getDepotDestinataireID());
        contentValues.put(Constantes.CLE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION, ph_preparation.getDepotDestinataireReference());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION, ph_preparation.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION, ph_preparation.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION, ph_preparation.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION, ph_preparation.getPrescripteurReference());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTION_DATE_PH_PREPARATION, ph_preparation.getPrescription_date());
        contentValues.put(Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION, ph_preparation.getPrescripteurNom());
        contentValues.put(Constantes.CLE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION, ph_preparation.getDepotOrigineReference());
        contentValues.put(Constantes.CLE_COL_DEPOTORIGINEID_PH_PREPARATION, ph_preparation.getDepotOrigineID());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRES_PH_PREPARATION, ph_preparation.getCommentaires());
        contentValues.put(Constantes.CLE_COL_PREPARATIONDATE_PH_PREPARATION, ph_preparation.getPreparationDate());
        contentValues.put(Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION, ph_preparation.getLivraisonPrevueDate());
        contentValues.put(Constantes.CLE_COL_DN_GROUPE_PH_PREPARATION, ph_preparation.getDN_Groupe());
        contentValues.put(Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION, ph_preparation.getMontant_HT());
        contentValues.put(Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION, ph_preparation.getMontant_TTC());
        contentValues.put(Constantes.CLE_COL_POIDS_PH_PREPARATION, ph_preparation.getPoids());
        contentValues.put(Constantes.CLE_COL_COMMANDE_ID_PH_PREPARATION, ph_preparation.getCommande_ID());
        contentValues.put(Constantes.CLE_COL_PREPARATEUR_PH_PREPARATION, ph_preparation.getPreparateur());
        contentValues.put(Constantes.CLE_COL_STATUT_PH_PREPARATION, ph_preparation.getStatut());
        contentValues.put(Constantes.CLE_COL_PHIE_SYNCHRO_PH_PREPARATION, ph_preparation.getPHIE_SYNCHRO());
        contentValues.put(Constantes.CLE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION, ph_preparation.getReceptionUFNonComforme());
        contentValues.put(Constantes.CLE_COL_LIVRAISONDATE_PH_PREPARATION, ph_preparation.getLivraisonDate());
        contentValues.put(Constantes.CLE_COL_FREQUENCE_PH_PREPARATION, ph_preparation.getFrequence());
        contentValues.put(Constantes.CLE_COL_PREVISIONDATEDEBUT_PH_PREPARATION, ph_preparation.getPrevisionDateDebut());
        contentValues.put(Constantes.CLE_COL_PREVISIONDATEFIN_PH_PREPARATION, ph_preparation.getPrevisionDateFin());
        contentValues.put(Constantes.CLE_COL_URGENT_PH_PREPARATION, ph_preparation.getURGENT());
        contentValues.put(Constantes.CLE_COL_MOTIF_PH_PREPARATION, ph_preparation.getMotif());
        contentValues.put(Constantes.CLE_COL_PREPARATEUR_USERID_PH_PREPARATION, ph_preparation.getPreparateur());
        contentValues.put(Constantes.CLE_COL_PHARMACIEN_USERID_PH_PREPARATION, ph_preparation.getPharmacien_userID());
        contentValues.put(Constantes.CLE_COL_VOLUME_PH_PREPARATION, ph_preparation.getVolume());
        contentValues.put(Constantes.CLE_COL_PALETTENB_PH_PREPARATION, ph_preparation.getPaletteNB());
        contentValues.put(Constantes.CLE_COL_CAISSENB_PH_PREPARATION, ph_preparation.getColisNB());
        contentValues.put(Constantes.CLE_COL_CONTENEUR_NB, ph_preparation.getConteneur_NB());
        contentValues.put(Constantes.CLE_COL_NUMERO_SCELLE, ph_preparation.getNumero_scelle());
        contentValues.put(Constantes.CLE_COL_LIVREUR_USERID, ph_preparation.getLivreur_userID());
        contentValues.put(Constantes.CLE_COL_SIGNATURE_LIVRAISON, ph_preparation.getSignature_Livraison());
        contentValues.put(Constantes.CLE_COL_TEMPS_PREPARATION, ph_preparation.getTempsPreparation());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_A, ph_preparation.getDelivranceValider_A());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_LE, ph_preparation.getDelivranceValider_Le());
        contentValues.put(Constantes.CLE_COL_DELIVRANCE_VALIDER_PAR, ph_preparation.getDelivranceValider_Par());
        contentValues.put(DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID, ph_preparation.getPhiMR4UUID());

        return db.update(Constantes.TABLE_PH_PREPARATION, contentValues, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=" + ph_preparation.getPhiMR4UUID(), null);
    }

    public static PH_Preparation getPH_PreparationByID(SQLiteDatabase db, int id) {
        PH_Preparation phPreparation = null;

        Cursor cursorA = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION + " WHERE " + Constantes.CLE_COL_UID_PH_PREPARATION + "=?", new String[]{String.valueOf(id)});

        if (cursorA.getCount() >= 1) {
            cursorA.moveToFirst();
            phPreparation = new PH_Preparation(cursorA);
        }
        cursorA.close();
        cursorA = null;
        return phPreparation;
    }

    public static PH_Preparation getPH_PreparationByNumeroCommande(SQLiteDatabase db, String commandeID) {
        PH_Preparation phPreparation = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION + " WHERE " + Constantes.CLE_COL_COMMANDE_ID_PH_PREPARATION + "=?", new String[]{commandeID});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phPreparation = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;
        return phPreparation;
    }

    public static PH_Preparation getPH_PreparationByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        PH_Preparation phPreparation = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION + " WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(id)});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phPreparation = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;
        return phPreparation;
    }

    public static List<PH_Preparation> getAllDelivrance(SQLiteDatabase db)
    {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (phPreparation.getStatut().startsWith("Préparée")) {
                    ph_preparationList.add(phPreparation);
                }
            }
        }
        cursor.close();
        cursor = null;


        return ph_preparationList;
    }

    public static PH_Preparation getDemandeDotationGlobaleEnInstance(SQLiteDatabase db, String Liste, String DateProchaineLivraison) {
        PH_Preparation ph_preparation = null;

        if(DateProchaineLivraison.contains("/"))
        {
            String tab[] = DateProchaineLivraison.split("/");
            String annee = tab[tab.length-1];
            String mois = tab[1];
            String jour = tab[0];
            DateProchaineLivraison = annee+"-"+mois+"-"+jour;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?  AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ? ", new String[]{Liste, "En instance", DateProchaineLivraison});
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            ph_preparation = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?" , new String[]{Liste, "En cours de régularisation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de préparation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de livraison", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }
        return ph_preparation;
    }

    public static ArrayList<Integer> getUIDDotationGlobaleEnInstance(SQLiteDatabase db) {
        ArrayList<Integer> listeUID = new ArrayList<>();

        PH_Preparation ph_preparation = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LISTE_PH_PREPARATION+" LIKE '%Dotation Globale :%'", new String[]{"En instance"});
        while (cursor.moveToNext()) {
            ph_preparation = new PH_Preparation(cursor);
            listeUID.add(ph_preparation.getUID());
        }

        cursor.close();
        cursor = null;

        cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?  AND "+Constantes.CLE_COL_LISTE_PH_PREPARATION+" LIKE '%Dotation Globale :%'", new String[]{"En cours de régularisation"});
        while (cursor.moveToNext()) {
            ph_preparation = new PH_Preparation(cursor);
            listeUID.add(ph_preparation.getUID());
        }
        cursor.close();
        cursor = null;

        return listeUID;
    }

    public static List<PH_Preparation> getAllPHPreparationVerrouPharmacie(SQLiteDatabase db) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (phPreparation.getStatut().equals("Verrouillée")) {
                    if (depot.getStructure().contains("PAD")) {
                        ph_preparationList.add(phPreparation);
                    } else if (depot.getStructure().contains("PUF")) {
                        if (!phPreparation.getListe().contains("nominative")) {
                            if (phPreparation.getValidee() == false) {
                                ph_preparationList.add(phPreparation);
                            }
                        }
                    }
                }
            }
        }
        cursor.close();
        cursor = null;


        return ph_preparationList;
    }

    public static List<PH_Preparation> getAllPHPreparationVerrouPharmacieInterne(SQLiteDatabase db) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (phPreparation.getStatut().equals("Préparée") || phPreparation.getStatut().equals("Préparée en partie")) {
                    ph_preparationList.add(phPreparation);
                }
            }
        }
        cursor.close();
        cursor = null;


        return ph_preparationList;
    }

    public static List<PH_Preparation> getAllLivraisonByDepotAndDate(SQLiteDatabase db, String depotReference, String dateLivraison) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION+"=? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"=?", new String[]{String.valueOf(depotReference), String.valueOf(dateLivraison)});

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);
            ph_preparationList.add(phPreparation);
        }
        cursor.close();
        cursor = null;
        return ph_preparationList;
    }
    public static PH_Preparation getDemandePleinVideEnInstance(SQLiteDatabase db, String Liste, String DateProchaineLivraison) {
        PH_Preparation ph_preparation = null;

        if(DateProchaineLivraison.contains("/"))
        {
            String tab[] = DateProchaineLivraison.split("/");
            DateProchaineLivraison = tab[tab.length-1]+"-"+tab[1]+"-"+tab[0];
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?  AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ? ", new String[]{Liste, "En instance", DateProchaineLivraison});
        if (cursor.getCount() >= 1) {
            cursor.moveToFirst();
            ph_preparation = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?" , new String[]{Liste, "En cours de régularisation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de préparation", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }

        if(ph_preparation == null)
        {
            cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_LISTE_PH_PREPARATION+ " = ? AND "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ? AND "+Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION+"= ?", new String[]{Liste, "En cours de livraison", DateProchaineLivraison});
            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ph_preparation = new PH_Preparation(cursor);
            }
            cursor.close();
            cursor = null;
        }
        return ph_preparation;
    }

    public static ArrayList<Integer> getUIDDemandePleinVideEnInstance(SQLiteDatabase db) {
        ArrayList<Integer> listeUID = new ArrayList<>();

        PH_Preparation ph_preparation = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?", new String[]{"En instance"});
        while (cursor.moveToNext()) {
            ph_preparation = new PH_Preparation(cursor);
            listeUID.add(ph_preparation.getUID());
        }

        cursor.close();
        cursor = null;

        cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION+" WHERE "+Constantes.CLE_COL_STATUT_PH_PREPARATION+"= ?", new String[]{"En cours de régularisation"});
        while (cursor.moveToNext()) {
            ph_preparation = new PH_Preparation(cursor);
            listeUID.add(ph_preparation.getUID());
        }
        cursor.close();
        cursor = null;

        return listeUID;
    }

    public static List<PH_Preparation> getAllPHPreparationLivraisons(SQLiteDatabase db, Boolean module_transport, int livreur_userID) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (!phPreparation.getListe().contains("nominative")) {
                    if (phPreparation.getStatut().contains("Préparé") && !module_transport) {
                        if (phPreparation.getCommande_ID() == 0) {
                            ph_preparationList.add(phPreparation);
                        }
                    }
                    //else if(phPreparation.getStatut().contains("Dé") && module_transport){
                    else if(phPreparation.getStatut().contains("Dé")){
                        if (phPreparation.getCommande_ID() == 0) {
                            if (!phPreparation.getLivree()) {
                                if(phPreparation.getLivreur_userID() == livreur_userID){
                                    ph_preparationList.add(phPreparation);
                                }
                            }
                        }
                    }
                }

            }
        }
        cursor.close();
        cursor = null;
        return ph_preparationList;
    }

    public static List<PH_Preparation> getAllPHPreparationPreparationPAD(SQLiteDatabase db) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (!phPreparation.getListe().contains("nominative")) {
                    if (phPreparation.getStatut().contains("A Préparer") || phPreparation.getStatut().contains("En Reliquat") || phPreparation.getStatut().contains("A préparer") || phPreparation.getStatut().contains("En reliquat")) {
                        if (phPreparation.getDepotDestinataireReference().contains("-PAD-") || phPreparation.getListe().contentEquals("ALCYONS_LISTE")) {
                            if (phPreparation.getCommande_ID() == 0) {
                                ph_preparationList.add(phPreparation);
                            }
                        }
                    }
                }
            }
        }
        cursor.close();
        cursor = null;

        return ph_preparationList;
    }

    public static PH_Preparation getPreparationEssaiAlcyons(SQLiteDatabase db)
    {
        PH_Preparation preparation_alcyons = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION + " WHERE " + Constantes.CLE_COL_LISTE_PH_PREPARATION + "=?", new String[]{"ALCYONS_LISTE"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            preparation_alcyons = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;

        return preparation_alcyons;
    }

    public static PH_Preparation getVerrouEssaiAlcyons(SQLiteDatabase db)
    {
        PH_Preparation preparation_alcyons = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION + " WHERE " + Constantes.CLE_COL_LISTE_PH_PREPARATION + "=?", new String[]{"ALCYONS_VERROU"});

        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            preparation_alcyons = new PH_Preparation(cursor);
        }
        cursor.close();
        cursor = null;

        return preparation_alcyons;
    }

    public static List<PH_Preparation> getAllPHPreparationPreparationPUF(SQLiteDatabase db) {
        List<PH_Preparation> ph_preparationList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.TABLE_PH_PREPARATION, null);

        while (cursor.moveToNext()) {
            PH_Preparation phPreparation = new PH_Preparation(cursor);

            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            if (depot != null) {
                if (!phPreparation.getListe().contains("nominative")) {
                    if (phPreparation.getStatut().contains("A Préparer") || phPreparation.getStatut().contains("En Reliquat") || phPreparation.getStatut().contains("A préparer") || phPreparation.getStatut().contains("En reliquat")) {
                        if (phPreparation.getDepotDestinataireReference().contains("-PUF-") || phPreparation.getListe().contentEquals("ALCYONS_LISTE")) {
                            if (phPreparation.getCommande_ID() == 0) {
                                ph_preparationList.add(phPreparation);
                            }
                        }
                    }
                }
            }
        }
        cursor.close();
        cursor = null;
        return ph_preparationList;
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        db.delete(Constantes.TABLE_PH_PREPARATION, Constantes.CLE_COL_LISTE_PH_PREPARATION + "=?", new String[]{"ALCYONS_VERROU"});
        return db.delete(Constantes.TABLE_PH_PREPARATION, Constantes.CLE_COL_LISTE_PH_PREPARATION + "=?", new String[]{"ALCYONS_LISTE"});
    }

    public static void synchronisationPH_PreparationVerrouPharmacie(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteVerrouPharmacie;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    viderBasesDeDonnees(db);
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocalePH_Preparations", "alerte");
                                }
                            } else {
                                PH_SerialisationOpenHelper.viderTablePH_Serialisation(db);
                                JSONArray SerialisationJSONArray = response.getJSONArray("Ph_Serialisation");
                                for (int s = 0; s < SerialisationJSONArray.length(); s++) {
                                    JSONObject serialisationJSONObject = SerialisationJSONArray.getJSONObject(s);
                                    PH_Serialisation serialisation = new PH_Serialisation(serialisationJSONObject);
                                    PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                                }

                                JSONArray phPreparationJSONArray = response.getJSONArray("PH_Preparations");
                                Stock_Lot_EmplacementLightOpenHelper.viderTableStock_Lot_Emplacements(db);
                                PH_PreparationOpenHelper.viderTablePH_Preparations(db);
                                PH_Preparation_LigneOpenHelper.viderTablePH_Preparation_Lignes(db);
                                for (int i = 0; i < phPreparationJSONArray.length(); i++) {
                                    JSONObject phPreparationJSONObject = phPreparationJSONArray.getJSONObject(i);
                                    PH_Preparation phPreparation = new PH_Preparation(phPreparationJSONObject);

                                    Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

                                    if (depot != null) {

                                        if (depot.getStructure().contains("PAD")) {
                                            if (phPreparation.getStatut().equals(context.getString(R.string.statutVerrouillée))) {
                                                long rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparation);
                                                if (rowID != -1) {
                                                    remplirTablesPHPreparationLigneEtStockLotEmplacement(phPreparationJSONObject, db);
                                                }
                                            }
                                        } else if (depot.getStructure().contains("PUF")) {
                                            if (!phPreparation.getListe().contains("nominative")) {
                                                if (!phPreparation.getValidee()) {
                                                    long rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparation);
                                                    if (rowID != -1) {
                                                        remplirTablesPHPreparationLigneEtStockLotEmplacement(phPreparationJSONObject, db);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            String activity_name = context.getClass().getSimpleName();
                            if(activity_name.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(true, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocalePH_Preparations", "alerte");
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

    public static void synchronisationPH_PreparationUF(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationPUF;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    viderBasesDeDonnees(db);
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocalePH_Preparations", "alerte");
                                }
                            } else {
                                JSONArray ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
                                for (PH_Preparation ph_preparation : getAllPHPreparationPreparationPUF(db)
                                        ) {
                                    for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation)
                                            ) {
                                        PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                                        Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                                        Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());

                                        if (depot != null && produit != null) {
                                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                                                    ) {
                                                Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                                            }
                                        }
                                    }
                                    supprimerUnPhPreparation(db, ph_preparation);
                                }


                                long rowID = 0;
                                for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
                                    JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
                                    PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                    rowID = insererUnPH_PreparationEnBDD(db, ph_preparation);
                                    if(ph_preparation.getUID() == 51570)
                                    {
                                        ph_preparation.setUID(ph_preparation.getUID());
                                    }
                                    if (rowID != -1) {
                                        JSONArray ph_preparationLigne_JSONArray = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                        for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                            JSONObject ph_preparationLigne_JSONObject = ph_preparationLigne_JSONArray.getJSONObject(k);
                                            rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLigne_JSONArray.getJSONObject(k)));
/*                                            if (rowID != -1) {
                                                JSONArray stockLotEmplacement_JSONArray = ph_preparationLigne_JSONObject.getJSONArray("ph_stock_lot_emplacements");

                                                for (int y = 0; y < stockLotEmplacement_JSONArray.length(); y++) {
                                                    Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(stockLotEmplacement_JSONArray.getJSONObject(y));
                                                    if (Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light.get_UID()) == null) {
                                                        if (stock_lot_emplacement_light.getQte() > 0) {
                                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light);
                                                        }
                                                    }
                                                }
                                            }*/
                                        }
                                    }

                                }
                            }
                            String activity_name = context.getClass().getSimpleName();
                            if(activity_name.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(true, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocalePH_Preparations", "alerte");
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

    public static void synchronisationPH_PreparationPAD(final Context context, final SQLiteDatabase db, final String token, final Utilisateur utilisateur, final boolean statutConnexion) {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePreparationPAD;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    viderBasesDeDonnees(db);
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete insererBDDLocalePH_Preparations", "alerte");
                                }
                            } else {
                                JSONArray ph_preparations_JSONArray = response.getJSONArray("PH_Preparations");
                                for (PH_Preparation ph_preparation : getAllPHPreparationPreparationPAD(db)
                                        ) {
                                    for (PH_Preparation_Ligne ph_preparation_ligne : PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation)
                                            ) {
                                        PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                                        Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                                        Depot depot = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotOrigineReference());

                                        if (depot != null && produit != null) {
                                            for (Stock_Lot_Emplacement_Light stockLotEmplacement : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)
                                                    ) {
                                                Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacement);
                                            }
                                        }
                                    }
                                    supprimerUnPhPreparation(db, ph_preparation);
                                }

                                long rowID = 0;
                                for (int i = 0; i < ph_preparations_JSONArray.length(); i++) {
                                    JSONObject ph_preparation_JSONObject = ph_preparations_JSONArray.getJSONObject(i);
                                    PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                    rowID = insererUnPH_PreparationEnBDD(db, ph_preparation);
                                    if (rowID != -1) {
                                        JSONArray ph_preparationLigne_JSONArray = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                        for (int k = 0; k < ph_preparationLigne_JSONArray.length(); k++) {
                                            JSONObject ph_preparationLigne_JSONObject = ph_preparationLigne_JSONArray.getJSONObject(k);
                                            rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLigne_JSONArray.getJSONObject(k)));
/*                                            if (rowID != -1) {
                                                JSONArray phStockLotEmplacement_JSONArray = ph_preparationLigne_JSONObject.getJSONArray("ph_stock_lot_emplacements");

                                                for (int y = 0; y < phStockLotEmplacement_JSONArray.length(); y++) {
                                                    Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(phStockLotEmplacement_JSONArray.getJSONObject(y));
                                                    if (Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light.get_UID()) == null) {
                                                        if (stock_lot_emplacement_light.getQte() > 0) {
                                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light);
                                                        }
                                                    }
                                                }
                                            }*/
                                        }
                                    }
                                }
                            }
                            String activity_name = context.getClass().getSimpleName();
                            if(activity_name.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(true, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocalePH_Preparations", "alerte");
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

    public static void synchronisationPH_PreparationLivraison(final Context context, final SQLiteDatabase db, final String token, final int utilisateurConnecteID, final Utilisateur utilisateur, final boolean statutConnexion)
    {
        if (!statutConnexion) {
            Alerte.afficherAlerte(context, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceLivraison;
        RequestQueue requestQueue = new Volley().newRequestQueue(context);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    viderBasesDeDonnees(db);
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ((Activity) context).finishAffinity();
                                    Intent intent = new Intent(context, AuthentificationActivity.class);
                                    context.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                    Alerte.afficherAlerte(context, "Aucune Livraison", "Vous n'avez aucune livraison de programmée.", "alerte");
                                }
                            } else {
                                JSONArray ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
                                for (PH_Preparation ph_preparation : getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), utilisateurConnecteID)
                                        ) {
                                    List<PH_Preparation_Ligne> ph_preparation_lignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);
                                    for (PH_Preparation_Ligne ph_preparation_ligne : ph_preparation_lignes
                                            ) {
                                        PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
                                    }
                                    supprimerUnPhPreparation(db, ph_preparation);
                                }
                                for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
                                    JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
                                    PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);
                                    long rowID = insererUnPH_PreparationEnBDD(db, ph_preparation);
                                    if (rowID != -1) {
                                        JSONArray ph_preparationLignesJson = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                        for (int k = 0; k < ph_preparationLignesJson.length(); k++) {
                                            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLignesJson.getJSONObject(k)));
                                        }
                                    }
                                }
                            }
                            String activity_name = context.getClass().getSimpleName();
                            if(activity_name.contentEquals("ServiceConnexionDirecteActivity"))
                            {
                                ((ServiceConnexionDirecteActivity) context).gestionProgressBar();
                            }
                            //((AuthentificationActivity) context).insertionDeTableEffectuee(true, "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP insererBDDLocalePH_Preparations", "alerte");
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

    public static void remplirTablesPHPreparationLigneEtStockLotEmplacement(JSONObject phPrep, SQLiteDatabase db) {
        try {
            JSONArray phPreparationLignesJSONArray = phPrep.getJSONArray("ph_preparation_lignes");
            for (int i = 0; i < phPreparationLignesJSONArray.length(); i++) {
                JSONObject phPreparationLignesJSONObject = phPreparationLignesJSONArray.getJSONObject(i);
                PH_Preparation_Ligne phPreparationLigne = new PH_Preparation_Ligne(phPreparationLignesJSONObject);
                long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, phPreparationLigne);

                if (rowID != -1) {

                    JSONArray phStockLotEmplacementJSONArray = phPreparationLignesJSONObject.getJSONArray("ph_stock_lot_emplacements");
                    for (int k = 0; k < phStockLotEmplacementJSONArray.length(); k++) {
                        JSONObject phStockLotEmplacementJSONObject = phStockLotEmplacementJSONArray.getJSONObject(k);
                        Stock_Lot_Emplacement_Light stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(phStockLotEmplacementJSONObject);
                        Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void supprimerUnPhPreparation(SQLiteDatabase db, PH_Preparation ph_preparation) {
        db.delete(Constantes.TABLE_PH_PREPARATION, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(ph_preparation.getPhiMR4UUID())});
    }

    public static final class Constantes implements BaseColumns {
        public static final String TABLE_PH_PREPARATION = "PH_Preparation";

        public static final String CLE_COL_SERVICE_PH_PREPARATION = "Service";
        public static final int NUM_COL_SERVICE_PH_PREPARATION = 1;
        public static final String TYPE_COL_SERVICE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_ERREUR_VALID_PH_PREPARATION = "Erreur_Valid";
        public static final int NUM_COL_ERREUR_VALID_PH_PREPARATION = 2;
        public static final String TYPE_COL_ERREUR_VALID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_PHIE_TAG_PH_PREPARATION = "PHIE_Tag";
        public static final int NUM_COL_PHIE_TAG_PH_PREPARATION = 3;
        public static final String TYPE_COL_PHIE_TAG_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_SAISIE_LE_PH_PREPARATION = "Saisie_Le";
        public static final int NUM_COL_SAISIE_LE_PH_PREPARATION = 4;
        public static final String TYPE_COL_SAISIE_LE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_A_TEL_HEURE_PH_PREPARATION = "A_tel_heure";
        public static final int NUM_COL_A_TEL_HEURE_PH_PREPARATION = 5;
        public static final String TYPE_COL_A_TEL_HEURE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PRODUITID_PH_PREPARATION = "produitID";
        public static final int NUM_COL_PRODUITID_PH_PREPARATION = 6;
        public static final String TYPE_COL_PRODUITID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_PRODUITDESIGNATION_PH_PREPARATION = "produitDesignation";
        public static final int NUM_COL_PRODUITDESIGNATION_PH_PREPARATION = 7;
        public static final String TYPE_COL_PRODUITDESIGNATION_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_QTE_DEMANDEE_PH_PREPARATION = "Qte_demandee";
        public static final int NUM_COL_QTE_DEMANDEE_PH_PREPARATION = 8;
        public static final String TYPE_COL_QTE_DEMANDEE_PH_PREPARATION = "REAL";
        public static final String CLE_COL_LIVREE_PH_PREPARATION = "Livree";
        public static final int NUM_COL_LIVREE_PH_PREPARATION = 9;
        public static final String TYPE_COL_LIVREE_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_VALIDEE_PH_PREPARATION = "Validee";
        public static final int NUM_COL_VALIDEE_PH_PREPARATION = 10;
        public static final String TYPE_COL_VALIDEE_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_ORIGINE_PH_PREPARATION = "Origine";
        public static final int NUM_COL_ORIGINE_PH_PREPARATION = 11;
        public static final String TYPE_COL_ORIGINE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_LISTE_PH_PREPARATION = "Liste";
        public static final int NUM_COL_LISTE_PH_PREPARATION = 12;
        public static final String TYPE_COL_LISTE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_DEPOTDESTINATAIREID_PH_PREPARATION = "depotDestinataireID";
        public static final int NUM_COL_DEPOTDESTINATAIREID_PH_PREPARATION = 13;
        public static final String TYPE_COL_DEPOTDESTINATAIREID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION = "depotDestinataireReference";
        public static final int NUM_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION = 14;
        public static final String TYPE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_PREPARATION = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_PREPARATION = 15;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_PREPARATION = 16;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_PREPARATION = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_PREPARATION = 17;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION = "PrescripteurReference";
        public static final int NUM_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION = 18;
        public static final String TYPE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PRESCRIPTION_DATE_PH_PREPARATION = "Prescription_date";
        public static final int NUM_COL_PRESCRIPTION_DATE_PH_PREPARATION = 19;
        public static final String TYPE_COL_PRESCRIPTION_DATE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION = "PrescripteurNom";
        public static final int NUM_COL_PRESCRIPTEURNOM_PH_PREPARATION = 20;
        public static final String TYPE_COL_PRESCRIPTEURNOM_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION = "depotOrigineReference";
        public static final int NUM_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION = 21;
        public static final String TYPE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_DEPOTORIGINEID_PH_PREPARATION = "depotOrigineID";
        public static final int NUM_COL_DEPOTORIGINEID_PH_PREPARATION = 22;
        public static final String TYPE_COL_DEPOTORIGINEID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_COMMENTAIRES_PH_PREPARATION = "Commentaires";
        public static final int NUM_COL_COMMENTAIRES_PH_PREPARATION = 23;
        public static final String TYPE_COL_COMMENTAIRES_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PREPARATIONDATE_PH_PREPARATION = "PreparationDate";
        public static final int NUM_COL_PREPARATIONDATE_PH_PREPARATION = 24;
        public static final String TYPE_COL_PREPARATIONDATE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION = "LivraisonPrevueDate";
        public static final int NUM_COL_LIVRAISONPREVUEDATE_PH_PREPARATION = 25;
        public static final String TYPE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_DN_GROUPE_PH_PREPARATION = "DN_Groupe";
        public static final int NUM_COL_DN_GROUPE_PH_PREPARATION = 26;
        public static final String TYPE_COL_DN_GROUPE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_MONTANT_HT_PH_PREPARATION = "Montant_HT";
        public static final int NUM_COL_MONTANT_HT_PH_PREPARATION = 27;
        public static final String TYPE_COL_MONTANT_HT_PH_PREPARATION = "REAL";
        public static final String CLE_COL_MONTANT_TTC_PH_PREPARATION = "Montant_TTC";
        public static final int NUM_COL_MONTANT_TTC_PH_PREPARATION = 28;
        public static final String TYPE_COL_MONTANT_TTC_PH_PREPARATION = "REAL";
        public static final String CLE_COL_POIDS_PH_PREPARATION = "Poids";
        public static final int NUM_COL_POIDS_PH_PREPARATION = 29;
        public static final String TYPE_COL_POIDS_PH_PREPARATION = "REAL";
        public static final String CLE_COL_COMMANDE_ID_PH_PREPARATION = "Commande_ID";
        public static final int NUM_COL_COMMANDE_ID_PH_PREPARATION = 30;
        public static final String TYPE_COL_COMMANDE_ID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_PREPARATEUR_PH_PREPARATION = "Preparateur";
        public static final int NUM_COL_PREPARATEUR_PH_PREPARATION = 31;
        public static final String TYPE_COL_PREPARATEUR_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_STATUT_PH_PREPARATION = "Statut";
        public static final int NUM_COL_STATUT_PH_PREPARATION = 32;
        public static final String TYPE_COL_STATUT_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PHIE_SYNCHRO_PH_PREPARATION = "PHIE_SYNCHRO";
        public static final int NUM_COL_PHIE_SYNCHRO_PH_PREPARATION = 33;
        public static final String TYPE_COL_PHIE_SYNCHRO_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION = "receptionUFNonComforme";
        public static final int NUM_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION = 34;
        public static final String TYPE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_LIVRAISONDATE_PH_PREPARATION = "livraisonDate";
        public static final int NUM_COL_LIVRAISONDATE_PH_PREPARATION = 35;
        public static final String TYPE_COL_LIVRAISONDATE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_FREQUENCE_PH_PREPARATION = "Frequence";
        public static final int NUM_COL_FREQUENCE_PH_PREPARATION = 36;
        public static final String TYPE_COL_FREQUENCE_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PREVISIONDATEDEBUT_PH_PREPARATION = "previsionDateDebut";
        public static final int NUM_COL_PREVISIONDATEDEBUT_PH_PREPARATION = 37;
        public static final String TYPE_COL_PREVISIONDATEDEBUT_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PREVISIONDATEFIN_PH_PREPARATION = "previsionDateFin";
        public static final int NUM_COL_PREVISIONDATEFIN_PH_PREPARATION = 38;
        public static final String TYPE_COL_PREVISIONDATEFIN_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_URGENT_PH_PREPARATION = "URGENT";
        public static final int NUM_COL_URGENT_PH_PREPARATION = 39;
        public static final String TYPE_COL_URGENT_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_MOTIF_PH_PREPARATION = "Motif";
        public static final int NUM_COL_MOTIF_PH_PREPARATION = 40;
        public static final String TYPE_COL_MOTIF_PH_PREPARATION = "TEXT";
        public static final String CLE_COL_PREPARATEUR_USERID_PH_PREPARATION = "preparateur_userID";
        public static final int NUM_COL_PREPARATEUR_USERID_PH_PREPARATION = 41;
        public static final String TYPE_COL_PREPARATEUR_USERID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_PHARMACIEN_USERID_PH_PREPARATION = "pharmacien_userID";
        public static final int NUM_COL_PHARMACIEN_USERID_PH_PREPARATION = 42;
        public static final String TYPE_COL_PHARMACIEN_USERID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_VOLUME_PH_PREPARATION = "Volume";
        public static final int NUM_COL_VOLUME_PH_PREPARATION = 43;
        public static final String TYPE_COL_VOLUME_PH_PREPARATION = "REAL";
        public static final String CLE_COL_PALETTENB_PH_PREPARATION = "PaletteNB";
        public static final int NUM_COL_PALETTENB_PH_PREPARATION = 44;
        public static final String TYPE_COL_PALETTENB_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_UID_PH_PREPARATION = "UID";
        public static final int NUM_COL_UID_PH_PREPARATION = 45;
        public static final String TYPE_COL_UID_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_CAISSENB_PH_PREPARATION = "CaisseNB";
        public static final int NUM_COL_CAISSENB_PH_PREPARATION = 46;
        public static final String TYPE_COL_CAISSENB_PH_PREPARATION = "INTEGER";
        public static final String CLE_COL_LIVREUR_USERID = "livreur_userID";
        public static final int NUM_COL_LIVREUR_USERID = 47;
        public static final String TYPE_COL_LIVREUR_USERID = "INTEGER";
        public static final String CLE_COL_SIGNATURE_LIVRAISON = "Signature_Livraison";
        public static final int NUM_COL_SIGNATURE_LIVRAISON = 48;
        public static final String TYPE_COL_SIGNATURE_LIVRAISON = "TEXT";
        public static final String CLE_COL_CONTENEUR_NB = "Conteneur_NB";
        public static final int NUM_COL_CONTENEUR_NB = 49;
        public static final String TYPE_COL_CONTENEUR_NB = "INTEGER";
        public static final String CLE_COL_NUMERO_SCELLE = "numero_scelle";
        public static final int NUM_COL_NUMERO_SCELLE = 50;
        public static final String TYPE_COL_NUMERO_SCELLE = "TEXT";
        public static final int NUM_COL_TEMPS_PREPARATION = 51;
        public static final String TYPE_COL_TEMPS_PREPARATION = "TEXT";
        public static final String CLE_COL_TEMPS_PREPARATION = "TempsPreparation";
        public static final int NUM_COL_DELIVRANCE_VALIDER_A = 52;
        public static final String TYPE_COL_DELIVRANCE_VALIDER_A = "INTEGER";
        public static final String CLE_COL_DELIVRANCE_VALIDER_A = "delivranceValider_A";
        public static final int NUM_COL_DELIVRANCE_VALIDER_LE = 53;
        public static final String TYPE_COL_DELIVRANCE_VALIDER_LE = "TEXT";
        public static final String CLE_COL_DELIVRANCE_VALIDER_LE = "delivranceValider_Le";
        public static final int NUM_COL_DELIVRANCE_VALIDER_PAR = 54;
        public static final String TYPE_COL_DELIVRANCE_VALIDER_PAR = "TEXT";
        public static final String CLE_COL_DELIVRANCE_VALIDER_PAR = "delivranceValider_Par";


        public static final String CREATION_TABLE_PH_PREPARATION = "CREATE TABLE " + Constantes.TABLE_PH_PREPARATION
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_SERVICE_PH_PREPARATION + " " + Constantes.TYPE_COL_SERVICE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_ERREUR_VALID_PH_PREPARATION + " " + Constantes.TYPE_COL_ERREUR_VALID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PHIE_TAG_PH_PREPARATION + " " + Constantes.TYPE_COL_PHIE_TAG_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_SAISIE_LE_PH_PREPARATION + " " + Constantes.TYPE_COL_SAISIE_LE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_A_TEL_HEURE_PH_PREPARATION + " " + Constantes.TYPE_COL_A_TEL_HEURE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PRODUITID_PH_PREPARATION + " " + Constantes.TYPE_COL_PRODUITID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PRODUITDESIGNATION_PH_PREPARATION + " " + Constantes.TYPE_COL_PRODUITDESIGNATION_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_QTE_DEMANDEE_PH_PREPARATION + " " + Constantes.TYPE_COL_QTE_DEMANDEE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_LIVREE_PH_PREPARATION + " " + Constantes.TYPE_COL_LIVREE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_VALIDEE_PH_PREPARATION + " " + Constantes.TYPE_COL_VALIDEE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_ORIGINE_PH_PREPARATION + " " + Constantes.TYPE_COL_ORIGINE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_LISTE_PH_PREPARATION + " " + Constantes.TYPE_COL_LISTE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_DEPOTDESTINATAIREID_PH_PREPARATION + " " + Constantes.TYPE_COL_DEPOTDESTINATAIREID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION + " " + Constantes.TYPE_COL_DEPOTDESTINATAIREREFERENCE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_PREPARATION + " " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PREPARATION + " " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_PREPARATION + " " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION + " " + Constantes.TYPE_COL_PRESCRIPTEURREFERENCE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PRESCRIPTION_DATE_PH_PREPARATION + " " + Constantes.TYPE_COL_PRESCRIPTION_DATE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PRESCRIPTEURNOM_PH_PREPARATION + " " + Constantes.TYPE_COL_PRESCRIPTEURNOM_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION + " " + Constantes.TYPE_COL_DEPOTORIGINEREFERENCE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_DEPOTORIGINEID_PH_PREPARATION + " " + Constantes.TYPE_COL_DEPOTORIGINEID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_COMMENTAIRES_PH_PREPARATION + " " + Constantes.TYPE_COL_COMMENTAIRES_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PREPARATIONDATE_PH_PREPARATION + " " + Constantes.TYPE_COL_PREPARATIONDATE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION + " " + Constantes.TYPE_COL_LIVRAISONPREVUEDATE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_DN_GROUPE_PH_PREPARATION + " " + Constantes.TYPE_COL_DN_GROUPE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_MONTANT_HT_PH_PREPARATION + " " + Constantes.TYPE_COL_MONTANT_HT_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_MONTANT_TTC_PH_PREPARATION + " " + Constantes.TYPE_COL_MONTANT_TTC_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_POIDS_PH_PREPARATION + " " + Constantes.TYPE_COL_POIDS_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_COMMANDE_ID_PH_PREPARATION + " " + Constantes.TYPE_COL_COMMANDE_ID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PREPARATEUR_PH_PREPARATION + " " + Constantes.TYPE_COL_PREPARATEUR_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_STATUT_PH_PREPARATION + " " + Constantes.TYPE_COL_STATUT_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PHIE_SYNCHRO_PH_PREPARATION + " " + Constantes.TYPE_COL_PHIE_SYNCHRO_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION + " " + Constantes.TYPE_COL_RECEPTIONUFNONCOMFORME_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_LIVRAISONDATE_PH_PREPARATION + " " + Constantes.TYPE_COL_LIVRAISONDATE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_FREQUENCE_PH_PREPARATION + " " + Constantes.TYPE_COL_FREQUENCE_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PREVISIONDATEDEBUT_PH_PREPARATION + " " + Constantes.TYPE_COL_PREVISIONDATEDEBUT_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PREVISIONDATEFIN_PH_PREPARATION + " " + Constantes.TYPE_COL_PREVISIONDATEFIN_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_URGENT_PH_PREPARATION + " " + Constantes.TYPE_COL_URGENT_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_MOTIF_PH_PREPARATION + " " + Constantes.TYPE_COL_MOTIF_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PREPARATEUR_USERID_PH_PREPARATION + " " + Constantes.TYPE_COL_PREPARATEUR_USERID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PHARMACIEN_USERID_PH_PREPARATION + " " + Constantes.TYPE_COL_PHARMACIEN_USERID_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_VOLUME_PH_PREPARATION + " " + Constantes.TYPE_COL_VOLUME_PH_PREPARATION + " ,"
                + Constantes.CLE_COL_PALETTENB_PH_PREPARATION + " " + Constantes.TYPE_COL_PALETTENB_PH_PREPARATION + ","
                + Constantes.CLE_COL_UID_PH_PREPARATION + " " + Constantes.TYPE_COL_UID_PH_PREPARATION + ","
                + Constantes.CLE_COL_CAISSENB_PH_PREPARATION + " " + Constantes.TYPE_COL_CAISSENB_PH_PREPARATION + ","
                + Constantes.CLE_COL_LIVREUR_USERID + " " + Constantes.TYPE_COL_LIVREUR_USERID + ","
                + Constantes.CLE_COL_SIGNATURE_LIVRAISON + " " + Constantes.TYPE_COL_SIGNATURE_LIVRAISON + ","
                + Constantes.CLE_COL_CONTENEUR_NB + " " + Constantes.TYPE_COL_CONTENEUR_NB + ","
                + Constantes.CLE_COL_NUMERO_SCELLE + " " + Constantes.TYPE_COL_NUMERO_SCELLE + ","
                + Constantes.CLE_COL_TEMPS_PREPARATION + " " + Constantes.TYPE_COL_TEMPS_PREPARATION + ","
                + Constantes.CLE_COL_DELIVRANCE_VALIDER_A + " " + Constantes.TYPE_COL_DELIVRANCE_VALIDER_A + ","
                + Constantes.CLE_COL_DELIVRANCE_VALIDER_LE + " " + Constantes.TYPE_COL_DELIVRANCE_VALIDER_LE + ","
                + Constantes.CLE_COL_DELIVRANCE_VALIDER_PAR + " " + Constantes.TYPE_COL_DELIVRANCE_VALIDER_PAR
                + ");";
    }
}
