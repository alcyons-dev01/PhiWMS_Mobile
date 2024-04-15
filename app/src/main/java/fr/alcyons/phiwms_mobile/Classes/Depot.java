package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 21/04/2017.
 */

public class Depot implements Serializable, Comparable {
    private int Depot_UID;
    private String Depot_Reference;
    private String Nom;
    private String Adresse1;
    private String Adresse2;
    private String CP;
    private String Ville;
    private String Tel;
    private String Fax;
    private boolean Livraison_Directe;
    private String Structure;
    private String Responsable;
    private String PAD_IPP;
    private String PAD_Patient;
    private String Hor_ouv;
    private String R_adresse1;
    private String R_adresse2;
    private String R_CP;
    private String R_Ville;
    private String R_tel;
    private String R_fax;
    private boolean Archive;
    private int ID_UF_Rattachement;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Dialyse_Frequence;
    private String PAD_Vacances_Adr1;
    private String PAD_Vacances_Adr2;
    private String PAD_Vacances_CP;
    private String PAD_Vacances_Ville;
    private String PAD_Vacances_Pays;
    private String PAD_Vacances_Tél;
    private String PAD_Vacances_Commentaires;
    private String PAD_Localisation_Poches;
    private String PAD_Précision_Localisation;
    private String PAD_Coordonnées_GPS;
    private boolean PAD_Acces_Chariot;
    private boolean PAD_Acces_Roll;
    private boolean PAD_Acces_Manuelle;
    private String PAD_Vehicule_Livraison;
    private String Livraison_Semaine_1;
    private int Livraison_Periode;
    private String PAD_Commentaire_Livraison;
    private String PAD_Email;
    private String Statut;
    private String PAD_Plan;
    private String Praticient_Par_defaut;
    private String PAD_Lieu_Traitement;
    private int PAD_ID_Lieu_Traitement;
    private boolean PAD_Utiliser_Adresse_Vacances;
    private boolean PAD_Ascenceur;
    private int PAD_etage;
    private String PAD_escalier;
    private String PAD_digicode;
    private String Livraison_Jour;
    private String Livraison_Semaine_2;
    private String Tournee_nom;
    private int Tournee_code;
    private String Section_Analytique;
    private int PAD_ID_Pret;
    private String Symbole;
    private boolean Service_externe;
    private int Ordre;
    private boolean Accuse_Reception;
    private int Jours_de_réserve_par_defaut;
    private int Jours_de_réserve_par_livraison;
    private boolean Inventaire_fin_de_Mois;
    private boolean RAZ_Stock_Inventaire;
    private String Ref_Depot_Phi;
    private String Livraison_Semaine_3;
    private String Livraison_Semaine_4;
    private String Livraison_Semaine_5;
    private String Commentaire_Commande;
    private String CAHP;
    private String Horaire_livraison;
    private String ATIR_Reference_Depot;
    private String DM_Localisation;
    private String Reference_Depot_Avant_PHI;
    private String Livraison_Frequence_Type;
    private int Livraison_Nb_Semaines;
    private String dossier_document;
    private String ProtocoleStd;
    private double latitude;
    private double longitude;
    private int Etablissement_UID;
    private String FInessGeo;
    private int Nombre_Postes;
    private int phiwms_mobileUUID = -1;

    public Depot(int Depot_UID, String Depot_Reference, String Nom, String Adresse1, String CP, String Ville, String Tel, String Structure)
    {
        this.Depot_UID = Depot_UID;
        this.Depot_Reference = Depot_Reference;
        this.Nom = Nom;
        this.Adresse1 = Adresse1;
        this.CP = CP;
        this.Ville = Ville;
        this.Tel = Tel;
        this.Structure = Structure;
    }
/*    public Depot(JSONObject depotJson) {
        try {
            this.Depot_UID = depotJson.getInt("Depot_UID");
            this.Depot_Reference = OutilsGestionClasses.recupererString(depotJson.getString("Depot_Reference"));
            this.Nom = OutilsGestionClasses.recupererString(depotJson.getString("Nom"));
            this.Adresse1 = OutilsGestionClasses.recupererString(depotJson.getString("Adresse1"));
            this.Adresse2 = OutilsGestionClasses.recupererString(depotJson.getString("Adresse2"));
            this.CP = OutilsGestionClasses.recupererString(depotJson.getString("CP"));
            this.Ville = OutilsGestionClasses.recupererString(depotJson.getString("Ville"));
            this.Tel = OutilsGestionClasses.recupererString(depotJson.getString("Tel"));
            this.Fax = OutilsGestionClasses.recupererString(depotJson.getString("Fax"));
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(depotJson, "Livraison_Directe");
            this.Structure = OutilsGestionClasses.recupererString(depotJson.getString("Structure"));
            this.Responsable = OutilsGestionClasses.recupererString(depotJson.getString("Responsable"));
            this.PAD_IPP = OutilsGestionClasses.recupererString(depotJson.getString("PAD_IPP"));
            this.PAD_Patient = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Patient"));
            this.Hor_ouv = OutilsGestionClasses.recupererString(depotJson.getString("Hor_ouv"));
            this.R_adresse1 = OutilsGestionClasses.recupererString(depotJson.getString("R_adresse1"));
            this.R_adresse2 = OutilsGestionClasses.recupererString(depotJson.getString("R_adresse2"));
            this.R_CP = OutilsGestionClasses.recupererString(depotJson.getString("R_CP"));
            this.R_Ville = OutilsGestionClasses.recupererString(depotJson.getString("R_Ville"));
            this.R_tel = OutilsGestionClasses.recupererString(depotJson.getString("R_tel"));
            this.R_fax = OutilsGestionClasses.recupererString(depotJson.getString("R_fax"));
            this.Archive = OutilsGestionClasses.recupererBooleen(depotJson, "Archive");
            this.ID_UF_Rattachement = depotJson.getInt("ID_UF_Rattachement");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_USER_MAJ"));
            this.Dialyse_Frequence = OutilsGestionClasses.recupererString(depotJson.getString("Dialyse_Frequence"));
            this.PAD_Vacances_Adr1 = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Adr1"));
            this.PAD_Vacances_Adr2 = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Adr2"));
            this.PAD_Vacances_CP = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_CP"));
            this.PAD_Vacances_Ville = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Ville"));
            this.PAD_Vacances_Pays = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Pays"));
            this.PAD_Vacances_Tél = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Tél"));
            this.PAD_Vacances_Commentaires = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Commentaires"));
            this.PAD_Localisation_Poches = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Localisation_Poches"));
            this.PAD_Précision_Localisation = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Précision_Localisation"));
            this.PAD_Coordonnées_GPS = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Coordonnées_GPS"));
            this.PAD_Acces_Chariot = OutilsGestionClasses.recupererBooleen(depotJson, "PAD_Acces_Chariot");
            this.PAD_Acces_Roll = OutilsGestionClasses.recupererBooleen(depotJson, "PAD_Acces_Roll");
            this.PAD_Acces_Manuelle = OutilsGestionClasses.recupererBooleen(depotJson, "PAD_Acces_Manuelle");
            this.PAD_Vehicule_Livraison = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vehicule_Livraison"));
            this.Livraison_Semaine_1 = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Semaine_1"));
            this.Livraison_Periode = depotJson.getInt("Livraison_Periode");
            this.PAD_Commentaire_Livraison = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Commentaire_Livraison"));
            this.PAD_Email = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Email"));
            this.Statut = OutilsGestionClasses.recupererString(depotJson.getString("Statut"));
            //this.PAD_Plan  = recupererString(depotJson.getString("PAD_Plan");
            this.Praticient_Par_defaut = OutilsGestionClasses.recupererString(depotJson.getString("Praticient_Par_defaut"));
            this.PAD_Lieu_Traitement = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Lieu_Traitement"));
            this.PAD_ID_Lieu_Traitement = depotJson.getInt("PAD_ID_Lieu_Traitement");
            this.PAD_Utiliser_Adresse_Vacances = OutilsGestionClasses.recupererBooleen(depotJson, "PAD_Utiliser_Adresse_Vacances");
            this.PAD_Ascenceur = OutilsGestionClasses.recupererBooleen(depotJson, "PAD_Ascenceur");
            this.PAD_etage = depotJson.getInt("PAD_etage");
            this.PAD_escalier = OutilsGestionClasses.recupererString(depotJson.getString("PAD_escalier"));
            this.PAD_digicode = OutilsGestionClasses.recupererString(depotJson.getString("PAD_digicode"));
            this.Livraison_Jour = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Jour"));
            this.Livraison_Semaine_2 = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Semaine_2"));
            this.Tournee_nom = OutilsGestionClasses.recupererString(depotJson.getString("Tournee_nom"));
            this.Tournee_code = depotJson.getInt("Tournee_code");
            this.Section_Analytique = OutilsGestionClasses.recupererString(depotJson.getString("Section_Analytique"));
            this.PAD_ID_Pret = depotJson.getInt("PAD_ID_Pret");
            //Symbole  = recupererString(depotJson.getString("Symbole");
            this.Service_externe = OutilsGestionClasses.recupererBooleen(depotJson, "Service_externe");
            this.Ordre = depotJson.getInt("Ordre");
            this.Accuse_Reception = OutilsGestionClasses.recupererBooleen(depotJson, "Accuse_Reception");
            this.Jours_de_réserve_par_defaut = depotJson.getInt("Jours_de_réserve_par_defaut");
            this.Jours_de_réserve_par_livraison = depotJson.getInt("Jours_de_réserve_par_livraison");
            this.Inventaire_fin_de_Mois = OutilsGestionClasses.recupererBooleen(depotJson, "Inventaire_fin_de_Mois");
            this.RAZ_Stock_Inventaire = OutilsGestionClasses.recupererBooleen(depotJson, "RAZ_Stock_Inventaire");
            this.Ref_Depot_Phi = OutilsGestionClasses.recupererString(depotJson.getString("Ref_Depot_Phi"));
            this.Livraison_Semaine_3 = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Semaine_3"));
            this.Livraison_Semaine_4 = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Semaine_4"));
            this.Livraison_Semaine_5 = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Semaine_5"));
            this.Commentaire_Commande = OutilsGestionClasses.recupererString(depotJson.getString("Commentaire_Commande"));
            this.CAHP = OutilsGestionClasses.recupererString(depotJson.getString("CAHP"));
            this.Horaire_livraison = OutilsGestionClasses.recupererString(depotJson.getString("Horaire_livraison"));
            this.ATIR_Reference_Depot = OutilsGestionClasses.recupererString(depotJson.getString("ATIR_Reference_Depot"));
            //this.DM_Localisation  = recupererString(depotJson.getString("DM_Localisation");
            this.Reference_Depot_Avant_PHI = OutilsGestionClasses.recupererString(depotJson.getString("Reference_Depot_Avant_PHI"));
            this.Livraison_Frequence_Type = OutilsGestionClasses.recupererString(depotJson.getString("Livraison_Frequence_Type"));
            this.Livraison_Nb_Semaines = depotJson.getInt("Livraison_Nb_Semaines");
            this.dossier_document = OutilsGestionClasses.recupererString(depotJson.getString("dossier_document"));
            this.ProtocoleStd = OutilsGestionClasses.recupererString(depotJson.getString("ProtocoleStd"));
            this.latitude = depotJson.getDouble("latitude");
            this.longitude = depotJson.getDouble("longitude");
            this.Etablissement_UID = depotJson.getInt("Etablissement_UID");
            this.FInessGeo = OutilsGestionClasses.recupererString(depotJson.getString("FInessGeo"));
            this.Nombre_Postes = depotJson.getInt("Nombre_Postes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


    public Depot(JSONObject depotJson) {
        try {
            this.Depot_UID = depotJson.getInt("Depot_UID");
            this.Depot_Reference = OutilsGestionClasses.recupererString(depotJson.getString("Depot_Reference"));
            this.Nom = OutilsGestionClasses.recupererString(depotJson.getString("Nom"));
            this.Adresse1 = OutilsGestionClasses.recupererString(depotJson.getString("Adresse1"));
            this.Adresse2 = OutilsGestionClasses.recupererString(depotJson.getString("Adresse2"));
            this.CP = OutilsGestionClasses.recupererString(depotJson.getString("CP"));
            this.Ville = OutilsGestionClasses.recupererString(depotJson.getString("Ville"));
            this.Tel = OutilsGestionClasses.recupererString(depotJson.getString("Tel"));
            this.Fax = OutilsGestionClasses.recupererString(depotJson.getString("Fax"));
            this.Structure = OutilsGestionClasses.recupererString(depotJson.getString("Structure"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(depotJson.getString("SYS_USER_MAJ"));
            this.PAD_Vacances_Adr1 = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Adr1"));
            this.PAD_Vacances_Adr2 = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Adr2"));
            this.PAD_Vacances_CP = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_CP"));
            this.PAD_Vacances_Ville = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Vacances_Ville"));
            this.latitude = depotJson.getDouble("latitude");
            this.longitude = depotJson.getDouble("longitude");
            this.Jours_de_réserve_par_livraison = depotJson.getInt("Jours_de_réserve_par_livraison");
            this.PAD_IPP = depotJson.getString("PAD_IPP");
            this.PAD_Patient = depotJson.getString("PAD_Patient");
            this.PAD_Lieu_Traitement = OutilsGestionClasses.recupererString(depotJson.getString("PAD_Lieu_Traitement"));
            this.Etablissement_UID = depotJson.getInt("Etablissement_UID");
            this.Archive = OutilsGestionClasses.recupererBooleen(depotJson, "Archive");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



 /*   public Depot(Cursor cursor) {
        this.Depot_UID = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ID_DEPOT);
        this.Depot_Reference = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_DEPOT);
        this.Nom = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_NOM_DEPOT);
        this.Adresse1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_ADRESSE1_DEPOT);
        this.Adresse2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_ADRESSE2_DEPOT);
        this.CP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_CP_DEPOT);
        this.Ville = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_VILLE_DEPOT);
        this.Tel = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_TEL_DEPOT);
        this.Fax = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_FAX_DEPOT);
        this.Livraison_Directe = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_DEPOT));
        this.Structure = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_STRUCTURE_DEPOT);
        this.Responsable = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_RESPONSABLE_DEPOT);
        this.PAD_IPP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_IPP_DEPOT);
        this.PAD_Patient = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_Patient_DEPOT);
        this.Hor_ouv = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_HOR_OUV_DEPOT);
        this.R_adresse1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_ADRESSE1_DEPOT);
        this.R_adresse2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_ADRESSE2_DEPOT);
        this.R_CP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_CP_DEPOT);
        this.R_Ville = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_VILLE_DEPOT);
        this.R_tel = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_TEL_DEPOT);
        this.R_fax = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_R_FAX_DEPOT);
        this.Archive = intToBoolean(DepotOpenHelper.Constantes.NUM_COL_ARCHIVE_DEPOT);
        this.ID_UF_Rattachement = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ID_UF_RATTACHEMENT_DEPOT);
        this.SYS_DT_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_DEPOT);
        this.SYS_HEURE_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_DEPOT);
        this.SYS_USER_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_DEPOT);
        this.Dialyse_Frequence = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_DIALYSE_FREQUENCE_DEPOT);
        this.PAD_Vacances_Adr1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_ADR1_DEPOT);
        this.PAD_Vacances_Adr2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_ADR2_DEPOT);
        this.PAD_Vacances_CP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_CP_DEPOT);
        this.PAD_Vacances_Ville = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_VILLE_DEPOT);
        this.PAD_Vacances_Pays = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_PAYS_DEPOT);
        this.PAD_Vacances_Tél = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_TEL_DEPOT);
        this.PAD_Vacances_Commentaires = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_COMMENTAIRES_DEPOT);
        this.PAD_Localisation_Poches = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_LOCALISATION_POCHES_DEPOT);
        this.PAD_Précision_Localisation = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_PRECISION_LOCALISATION_DEPOT);
        this.PAD_Coordonnées_GPS = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_COORDONNEES_GPS_DEPOT);
        this.PAD_Acces_Chariot = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ACCES_CHARIOT_DEPOT));
        this.PAD_Acces_Roll = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ACCES_ROLL_DEPOT));
        this.PAD_Acces_Manuelle = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ACCES_MANUELLE_DEPOT));
        this.PAD_Vehicule_Livraison = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VEHICULE_LIVRAISON_DEPOT);
        this.Livraison_Semaine_1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_SEMAINE_1_DEPOT);
        this.Livraison_Periode = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_PERIODE_DEPOT);
        this.PAD_Commentaire_Livraison = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_COMMENTAIRE_LIVRAISON_DEPOT);
        this.PAD_Email = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_EMAIL_DEPOT);
        this.Statut = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_STATUT_DEPOT);
        this.PAD_Plan = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_PLAN_DEPOT);
        this.Praticient_Par_defaut = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PRATICIENT_PAR_DEFAUT_DEPOT);
        this.PAD_Lieu_Traitement = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_LIEU_TRAITEMENT_DEPOT);
        this.PAD_ID_Lieu_Traitement = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ID_LIEU_TRAITEMENT_DEPOT);
        this.PAD_Utiliser_Adresse_Vacances = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_UTILISER_ADRESSE_VACANCES_DEPOT));
        this.PAD_Ascenceur = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ASCENSEUR_DEPOT));
        this.PAD_etage = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ETAGE_DEPOT);
        this.PAD_escalier = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_ESCALIER_DEPOT);
        this.PAD_digicode = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_DIGICODE_DEPOT);
        this.Livraison_Jour = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_JOUR_DEPOT);
        this.Livraison_Semaine_2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_SEMAINE_2_DEPOT);
        this.Tournee_nom = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_TOURNEE_NOM_DEPOT);
        this.Tournee_code = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_TOURNEE_CODE_DEPOT);
        this.Section_Analytique = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SECTION_ANALYTIQUE_DEPOT);
        this.PAD_ID_Pret = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_PAD_ID_PRET_DEPOT);
        this.Symbole = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYMBOLE_DEPOT);
        this.Service_externe = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_SERVICE_EXTERNE_DEPOT));
        this.Ordre = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ORDRE_DEPOT);
        this.Accuse_Reception = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ACCUSE_RECEPTION_DEPOT));
        this.Jours_de_réserve_par_defaut = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_JOURS_DE_RESERVE_PAR_DEFAUT_DEPOT);
        this.Jours_de_réserve_par_livraison = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT);
        this.Inventaire_fin_de_Mois = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_INVENTAIRE_FIN_DE_MOIS_DEPOT));
        this.RAZ_Stock_Inventaire = intToBoolean(cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_RAZ_STOCK_INVENTAIRE_DEPOT));
        this.Ref_Depot_Phi = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_REF_DEPOT_PHI_DEPOT);
        this.Livraison_Semaine_3 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_SEMAINE_3_DEPOT);
        this.Livraison_Semaine_4 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_SEMAINE_4_DEPOT);
        this.Livraison_Semaine_5 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_SEMAINE_5_DEPOT);
        this.Commentaire_Commande = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_COMMENTAIRE_COMMANDE_DEPOT);
        this.CAHP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_CAHP_DEPOT);
        this.Horaire_livraison = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_HORAIRE_LIVRAISON_DEPOT);
        this.ATIR_Reference_Depot = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_ATIR_REFERENCE_DEPOT);
        this.DM_Localisation = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_DM_LOCALISATION_DEPOT);
        this.Reference_Depot_Avant_PHI = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_REFERENCE_DEPOT_AVANT_PHI_DEPOT);
        this.Livraison_Frequence_Type = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_FREQUENCE_TYPE_DEPOT);
        this.Livraison_Nb_Semaines = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_LIVRAISON_NB_SEMAINES_DEPOT);
        this.dossier_document = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_DOSSIER_DOCUMENT_DEPOT);
        this.ProtocoleStd = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PROTOCOLESTD_DEPOT);
        this.latitude = cursor.getDouble(DepotOpenHelper.Constantes.NUM_COL_LATITUDE_DEPOT);
        this.longitude = cursor.getDouble(DepotOpenHelper.Constantes.NUM_COL_LONGITUDE_DEPOT);
        this.Etablissement_UID = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_UID_DEPOT);
        this.FInessGeo = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_FINESSGEO_DEPOT);
        this.Nombre_Postes = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_NOMBRE_POSTES_DEPOT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Depot(Cursor cursor) {
        this.Depot_UID = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ID_DEPOT);
        this.Depot_Reference = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_DEPOT);
        this.Nom = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_NOM_DEPOT);
        this.Adresse1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_ADRESSE1_DEPOT);
        this.Adresse2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_ADRESSE2_DEPOT);
        this.CP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_CP_DEPOT);
        this.Ville = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_VILLE_DEPOT);
        this.Tel = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_TEL_DEPOT);
        this.Fax = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_FAX_DEPOT);
        this.Structure = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_STRUCTURE_DEPOT);
        this.SYS_DT_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_DEPOT);
        this.SYS_HEURE_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_DEPOT);
        this.SYS_USER_MAJ = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_DEPOT);
        this.PAD_Vacances_Adr1 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_ADR1_DEPOT);
        this.PAD_Vacances_Adr2 = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_ADR2_DEPOT);
        this.PAD_Vacances_CP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_CP_DEPOT);
        this.PAD_Vacances_Ville = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_VACANCES_VILLE_DEPOT);
        this.latitude = cursor.getDouble(DepotOpenHelper.Constantes.NUM_COL_LATITUDE_DEPOT);
        this.longitude = cursor.getDouble(DepotOpenHelper.Constantes.NUM_COL_LONGITUDE_DEPOT);
        this.Jours_de_réserve_par_livraison = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_JOURS_DE_RESERVE_PAR_LIVRAISON_DEPOT);
        this.PAD_Lieu_Traitement = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_LIEU_TRAITEMENT_DEPOT);
        this.Etablissement_UID = cursor.getInt(DepotOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_UID_DEPOT);
        this.PAD_IPP = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_IPP_DEPOT);
        this.PAD_Patient = cursor.getString(DepotOpenHelper.Constantes.NUM_COL_PAD_Patient_DEPOT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
        this.Archive = OutilsGestionClasses.recupererBooleen(cursor, DepotOpenHelper.Constantes.NUM_COL_ARCHIVE_DEPOT);
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public boolean intToBoolean(int x) {
        return x == 1;
    }

    public int getDepot_UID() {
        return Depot_UID;
    }

    public void setDepot_UID(int depot_UID) {
        Depot_UID = depot_UID;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getAdresse1() {
        return Adresse1;
    }

    public void setAdresse1(String adresse1) {
        Adresse1 = adresse1;
    }

    public String getAdresse2() {
        return Adresse2;
    }

    public void setAdresse2(String adresse2) {
        Adresse2 = adresse2;
    }

    public String getCP() {
        return CP;
    }

    public void setCP(String CP) {
        this.CP = CP;
    }

    public String getVille() {
        return Ville;
    }

    public void setVille(String ville) {
        Ville = ville;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String fax) {
        Fax = fax;
    }

    public boolean isLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
    }

    public String getStructure() {
        return Structure;
    }

    public void setStructure(String structure) {
        Structure = structure;
    }

    public String getResponsable() {
        return Responsable;
    }

    public void setResponsable(String responsable) {
        Responsable = responsable;
    }

    public String getPAD_IPP() {
        return PAD_IPP;
    }

    public void setPAD_IPP(String PAD_IPP) {
        this.PAD_IPP = PAD_IPP;
    }

    public String getPAD_Patient() {
        return PAD_Patient;
    }

    public void setPAD_Patient(String PAD_Patient) {
        this.PAD_Patient = PAD_Patient;
    }

    public String getHor_ouv() {
        return Hor_ouv;
    }

    public void setHor_ouv(String hor_ouv) {
        Hor_ouv = hor_ouv;
    }

    public String getR_adresse1() {
        return R_adresse1;
    }

    public void setR_adresse1(String r_adresse1) {
        R_adresse1 = r_adresse1;
    }

    public String getR_adresse2() {
        return R_adresse2;
    }

    public void setR_adresse2(String r_adresse2) {
        R_adresse2 = r_adresse2;
    }

    public String getR_CP() {
        return R_CP;
    }

    public void setR_CP(String r_CP) {
        R_CP = r_CP;
    }

    public String getR_Ville() {
        return R_Ville;
    }

    public void setR_Ville(String r_Ville) {
        R_Ville = r_Ville;
    }

    public String getR_tel() {
        return R_tel;
    }

    public void setR_tel(String r_tel) {
        R_tel = r_tel;
    }

    public String getR_fax() {
        return R_fax;
    }

    public void setR_fax(String r_fax) {
        R_fax = r_fax;
    }

    public boolean isArchive() {
        return Archive;
    }

    public void setArchive(boolean archive) {
        Archive = archive;
    }

    public int getID_UF_Rattachement() {
        return ID_UF_Rattachement;
    }

    public void setID_UF_Rattachement(int ID_UF_Rattachement) {
        this.ID_UF_Rattachement = ID_UF_Rattachement;
    }

    public String getSYS_DT_MAJ() {
        return SYS_DT_MAJ;
    }

    public void setSYS_DT_MAJ(String SYS_DT_MAJ) {
        this.SYS_DT_MAJ = SYS_DT_MAJ;
    }

    public String getSYS_HEURE_MAJ() {
        return SYS_HEURE_MAJ;
    }

    public void setSYS_HEURE_MAJ(String SYS_HEURE_MAJ) {
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
    }

    public String getDialyse_Frequence() {
        return Dialyse_Frequence;
    }

    public void setDialyse_Frequence(String dialyse_Frequence) {
        Dialyse_Frequence = dialyse_Frequence;
    }

    public String getPAD_Vacances_Adr1() {
        return PAD_Vacances_Adr1;
    }

    public void setPAD_Vacances_Adr1(String PAD_Vacances_Adr1) {
        this.PAD_Vacances_Adr1 = PAD_Vacances_Adr1;
    }

    public String getPAD_Vacances_Adr2() {
        return PAD_Vacances_Adr2;
    }

    public void setPAD_Vacances_Adr2(String PAD_Vacances_Adr2) {
        this.PAD_Vacances_Adr2 = PAD_Vacances_Adr2;
    }

    public String getPAD_Vacances_CP() {
        return PAD_Vacances_CP;
    }

    public void setPAD_Vacances_CP(String PAD_Vacances_CP) {
        this.PAD_Vacances_CP = PAD_Vacances_CP;
    }

    public String getPAD_Vacances_Ville() {
        return PAD_Vacances_Ville;
    }

    public void setPAD_Vacances_Ville(String PAD_Vacances_Ville) {
        this.PAD_Vacances_Ville = PAD_Vacances_Ville;
    }

    public String getPAD_Vacances_Pays() {
        return PAD_Vacances_Pays;
    }

    public void setPAD_Vacances_Pays(String PAD_Vacances_Pays) {
        this.PAD_Vacances_Pays = PAD_Vacances_Pays;
    }

    public String getPAD_Vacances_Tél() {
        return PAD_Vacances_Tél;
    }

    public void setPAD_Vacances_Tél(String PAD_Vacances_Tél) {
        this.PAD_Vacances_Tél = PAD_Vacances_Tél;
    }

    public String getPAD_Vacances_Commentaires() {
        return PAD_Vacances_Commentaires;
    }

    public void setPAD_Vacances_Commentaires(String PAD_Vacances_Commentaires) {
        this.PAD_Vacances_Commentaires = PAD_Vacances_Commentaires;
    }

    public String getPAD_Localisation_Poches() {
        return PAD_Localisation_Poches;
    }

    public void setPAD_Localisation_Poches(String PAD_Localisation_Poches) {
        this.PAD_Localisation_Poches = PAD_Localisation_Poches;
    }

    public String getPAD_Précision_Localisation() {
        return PAD_Précision_Localisation;
    }

    public void setPAD_Précision_Localisation(String PAD_Précision_Localisation) {
        this.PAD_Précision_Localisation = PAD_Précision_Localisation;
    }

    public String getPAD_Coordonnées_GPS() {
        return PAD_Coordonnées_GPS;
    }

    public void setPAD_Coordonnées_GPS(String PAD_Coordonnées_GPS) {
        this.PAD_Coordonnées_GPS = PAD_Coordonnées_GPS;
    }

    public boolean isPAD_Acces_Chariot() {
        return PAD_Acces_Chariot;
    }

    public void setPAD_Acces_Chariot(boolean PAD_Acces_Chariot) {
        this.PAD_Acces_Chariot = PAD_Acces_Chariot;
    }

    public boolean isPAD_Acces_Roll() {
        return PAD_Acces_Roll;
    }

    public void setPAD_Acces_Roll(boolean PAD_Acces_Roll) {
        this.PAD_Acces_Roll = PAD_Acces_Roll;
    }

    public boolean isPAD_Acces_Manuelle() {
        return PAD_Acces_Manuelle;
    }

    public void setPAD_Acces_Manuelle(boolean PAD_Acces_Manuelle) {
        this.PAD_Acces_Manuelle = PAD_Acces_Manuelle;
    }

    public String getPAD_Vehicule_Livraison() {
        return PAD_Vehicule_Livraison;
    }

    public void setPAD_Vehicule_Livraison(String PAD_Vehicule_Livraison) {
        this.PAD_Vehicule_Livraison = PAD_Vehicule_Livraison;
    }

    public String getLivraison_Semaine_1() {
        return Livraison_Semaine_1;
    }

    public void setLivraison_Semaine_1(String livraison_Semaine_1) {
        Livraison_Semaine_1 = livraison_Semaine_1;
    }

    public int getLivraison_Periode() {
        return Livraison_Periode;
    }

    public void setLivraison_Periode(int livraison_Periode) {
        Livraison_Periode = livraison_Periode;
    }

    public String getPAD_Commentaire_Livraison() {
        return PAD_Commentaire_Livraison;
    }

    public void setPAD_Commentaire_Livraison(String PAD_Commentaire_Livraison) {
        this.PAD_Commentaire_Livraison = PAD_Commentaire_Livraison;
    }

    public String getPAD_Email() {
        return PAD_Email;
    }

    public void setPAD_Email(String PAD_Email) {
        this.PAD_Email = PAD_Email;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public String getPAD_Plan() {
        return PAD_Plan;
    }

    public void setPAD_Plan(String PAD_Plan) {
        this.PAD_Plan = PAD_Plan;
    }

    public String getPraticient_Par_defaut() {
        return Praticient_Par_defaut;
    }

    public void setPraticient_Par_defaut(String praticient_Par_defaut) {
        Praticient_Par_defaut = praticient_Par_defaut;
    }

    public String getPAD_Lieu_Traitement() {
        return PAD_Lieu_Traitement;
    }

    public void setPAD_Lieu_Traitement(String PAD_Lieu_Traitement) {
        this.PAD_Lieu_Traitement = PAD_Lieu_Traitement;
    }

    public int getPAD_ID_Lieu_Traitement() {
        return PAD_ID_Lieu_Traitement;
    }

    public void setPAD_ID_Lieu_Traitement(int PAD_ID_Lieu_Traitement) {
        this.PAD_ID_Lieu_Traitement = PAD_ID_Lieu_Traitement;
    }

    public boolean isPAD_Utiliser_Adresse_Vacances() {
        return PAD_Utiliser_Adresse_Vacances;
    }

    public void setPAD_Utiliser_Adresse_Vacances(boolean PAD_Utiliser_Adresse_Vacances) {
        this.PAD_Utiliser_Adresse_Vacances = PAD_Utiliser_Adresse_Vacances;
    }

    public boolean isPAD_Ascenceur() {
        return PAD_Ascenceur;
    }

    public void setPAD_Ascenceur(boolean PAD_Ascenceur) {
        this.PAD_Ascenceur = PAD_Ascenceur;
    }

    public int getPAD_etage() {
        return PAD_etage;
    }

    public void setPAD_etage(int PAD_etage) {
        this.PAD_etage = PAD_etage;
    }

    public String getPAD_escalier() {
        return PAD_escalier;
    }

    public void setPAD_escalier(String PAD_escalier) {
        this.PAD_escalier = PAD_escalier;
    }

    public String getPAD_digicode() {
        return PAD_digicode;
    }

    public void setPAD_digicode(String PAD_digicode) {
        this.PAD_digicode = PAD_digicode;
    }

    public String getLivraison_Jour() {
        return Livraison_Jour;
    }

    public void setLivraison_Jour(String livraison_Jour) {
        Livraison_Jour = livraison_Jour;
    }

    public String getLivraison_Semaine_2() {
        return Livraison_Semaine_2;
    }

    public void setLivraison_Semaine_2(String livraison_Semaine_2) {
        Livraison_Semaine_2 = livraison_Semaine_2;
    }

    public String getTournee_nom() {
        return Tournee_nom;
    }

    public void setTournee_nom(String tournee_nom) {
        Tournee_nom = tournee_nom;
    }

    public int getTournee_code() {
        return Tournee_code;
    }

    public void setTournee_code(int tournee_code) {
        Tournee_code = tournee_code;
    }

    public String getSection_Analytique() {
        return Section_Analytique;
    }

    public void setSection_Analytique(String section_Analytique) {
        Section_Analytique = section_Analytique;
    }

    public int getPAD_ID_Pret() {
        return PAD_ID_Pret;
    }

    public void setPAD_ID_Pret(int PAD_ID_Pret) {
        this.PAD_ID_Pret = PAD_ID_Pret;
    }

    public String getSymbole() {
        return Symbole;
    }

    public void setSymbole(String symbole) {
        Symbole = symbole;
    }

    public boolean isService_externe() {
        return Service_externe;
    }

    public void setService_externe(boolean service_externe) {
        Service_externe = service_externe;
    }

    public int getOrdre() {
        return Ordre;
    }

    public void setOrdre(int ordre) {
        Ordre = ordre;
    }

    public boolean isAccuse_Reception() {
        return Accuse_Reception;
    }

    public void setAccuse_Reception(boolean accuse_Reception) {
        Accuse_Reception = accuse_Reception;
    }

    public int getJours_de_réserve_par_defaut() {
        return Jours_de_réserve_par_defaut;
    }

    public void setJours_de_réserve_par_defaut(int jours_de_réserve_par_defaut) {
        Jours_de_réserve_par_defaut = jours_de_réserve_par_defaut;
    }

    public int getJours_de_réserve_par_livraison() {
        return Jours_de_réserve_par_livraison;
    }

    public void setJours_de_réserve_par_livraison(int jours_de_réserve_par_livraison) {
        Jours_de_réserve_par_livraison = jours_de_réserve_par_livraison;
    }

    public boolean isInventaire_fin_de_Mois() {
        return Inventaire_fin_de_Mois;
    }

    public void setInventaire_fin_de_Mois(boolean inventaire_fin_de_Mois) {
        Inventaire_fin_de_Mois = inventaire_fin_de_Mois;
    }

    public boolean isRAZ_Stock_Inventaire() {
        return RAZ_Stock_Inventaire;
    }

    public void setRAZ_Stock_Inventaire(boolean RAZ_Stock_Inventaire) {
        this.RAZ_Stock_Inventaire = RAZ_Stock_Inventaire;
    }

    public String getRef_Depot_Phi() {
        return Ref_Depot_Phi;
    }

    public void setRef_Depot_Phi(String ref_Depot_Phi) {
        Ref_Depot_Phi = ref_Depot_Phi;
    }

    public String getLivraison_Semaine_3() {
        return Livraison_Semaine_3;
    }

    public void setLivraison_Semaine_3(String livraison_Semaine_3) {
        Livraison_Semaine_3 = livraison_Semaine_3;
    }

    public String getLivraison_Semaine_4() {
        return Livraison_Semaine_4;
    }

    public void setLivraison_Semaine_4(String livraison_Semaine_4) {
        Livraison_Semaine_4 = livraison_Semaine_4;
    }

    public String getLivraison_Semaine_5() {
        return Livraison_Semaine_5;
    }

    public void setLivraison_Semaine_5(String livraison_Semaine_5) {
        Livraison_Semaine_5 = livraison_Semaine_5;
    }

    public String getCommentaire_Commande() {
        return Commentaire_Commande;
    }

    public void setCommentaire_Commande(String commentaire_Commande) {
        Commentaire_Commande = commentaire_Commande;
    }

    public String getCAHP() {
        return CAHP;
    }

    public void setCAHP(String CAHP) {
        this.CAHP = CAHP;
    }

    public String getHoraire_livraison() {
        return Horaire_livraison;
    }

    public void setHoraire_livraison(String horaire_livraison) {
        Horaire_livraison = horaire_livraison;
    }

    public String getATIR_Reference_Depot() {
        return ATIR_Reference_Depot;
    }

    public void setATIR_Reference_Depot(String ATIR_Reference_Depot) {
        this.ATIR_Reference_Depot = ATIR_Reference_Depot;
    }

    public String getDM_Localisation() {
        return DM_Localisation;
    }

    public void setDM_Localisation(String DM_Localisation) {
        this.DM_Localisation = DM_Localisation;
    }

    public String getReference_Depot_Avant_PHI() {
        return Reference_Depot_Avant_PHI;
    }

    public void setReference_Depot_Avant_PHI(String reference_Depot_Avant_PHI) {
        Reference_Depot_Avant_PHI = reference_Depot_Avant_PHI;
    }

    public String getLivraison_Frequence_Type() {
        return Livraison_Frequence_Type;
    }

    public void setLivraison_Frequence_Type(String livraison_Frequence_Type) {
        Livraison_Frequence_Type = livraison_Frequence_Type;
    }

    public int getLivraison_Nb_Semaines() {
        return Livraison_Nb_Semaines;
    }

    public void setLivraison_Nb_Semaines(int livraison_Nb_Semaines) {
        Livraison_Nb_Semaines = livraison_Nb_Semaines;
    }

    public String getDossier_document() {
        return dossier_document;
    }

    public void setDossier_document(String dossier_document) {
        this.dossier_document = dossier_document;
    }

    public String getProtocoleStd() {
        return ProtocoleStd;
    }

    public void setProtocoleStd(String protocoleStd) {
        ProtocoleStd = protocoleStd;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getEtablissement_UID() {
        return Etablissement_UID;
    }

    public void setEtablissement_UID(int etablissement_UID) {
        Etablissement_UID = etablissement_UID;
    }

    public String getFInessGeo() {
        return FInessGeo;
    }

    public void setFInessGeo(String FInessGeo) {
        this.FInessGeo = FInessGeo;
    }

    public int getNombre_Postes() {
        return Nombre_Postes;
    }

    public void setNombre_Postes(int nombre_Postes) {
        Nombre_Postes = nombre_Postes;
    }

    public String toString() {
        return this.getNom();
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Depot)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Depot depot = (Depot) obj;

        if (this.getPhiMR4UUID() == depot.getPhiMR4UUID()) {
            return 0;
        } else {
            return -1;
        }
    }
}
