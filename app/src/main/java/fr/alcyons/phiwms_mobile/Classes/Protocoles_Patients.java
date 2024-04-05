package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by jessica on 02/10/2017.
 */

public class Protocoles_Patients implements Serializable, Comparable {

    private int _UID;
    private String Protocole_Std;
    private String IPP;
    private String Rappel_traitement;
    private String Frequence;
    private String Depot_Reference;
    private String Debut_Date;
    private String Fin_Date;
    private boolean Interruption;
    private String Patient_Identite;
    private int Valorisation;
    private boolean Soins_A_Domicile;
    private String Dotation_Std;
    private int Depot_Code;
    private boolean Qte_par_session;
    private String Tournee_Protocole;
    private String Tournee_Dotation;
    private boolean Archiver;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private boolean Ordre_Prescription_ignorer;
    private String Serie_Lundi_1;
    private String Serie_Mardi_2;
    private String Serie_Mercredi_3;
    private String Serie_Jeudi_4;
    private String Serie_Vendredi_5;
    private String Serie_Samedi_6;
    private String Serie_Dimanche_7;
    private boolean Livraison_A_domicile;
    private int Patient_ID;
    private String Centre_Hospitalier_Rattachement;
    private String Nephrologue_Referent;
    private String Generaliste_Referent;
    private String IDE;
    private String Technique;
    private String Sous_Technique;
    private String Technique_Debut_Date;
    private String Interruption_Fin_Date;
    private String Interruption_Motif;
    private String Arret_Definitif_Date;
    private String Arret_Motif;
    private String Traitement_Modalite;
    private String Interruption_Debut_Date;
    private boolean Validation_Pharmaceutique;
    private String Validation_Pharmaceutique_Date;
    private String Validation_Pharmaceutique_Par;
    private String discipline_Medicale;
    private int phiwms_mobileUUID = -1;

    public Protocoles_Patients(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Protocole_Std = OutilsGestionClasses.recupererString(jsonObject.getString("Protocole_Std"));
            this.IPP = OutilsGestionClasses.recupererString(jsonObject.getString("IPP"));
            this.Rappel_traitement = OutilsGestionClasses.recupererString(jsonObject.getString("Rappel_traitement"));
            this.Frequence = OutilsGestionClasses.recupererString(jsonObject.getString("Frequence"));
            this.Depot_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Depot_Reference"));
            this.Debut_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Debut_Date"));
            this.Fin_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Fin_Date"));
            this.Interruption = OutilsGestionClasses.recupererBooleen(jsonObject, "Interruption");
            this.Patient_Identite = OutilsGestionClasses.recupererString(jsonObject.getString("Patient_Identite"));
            this.Valorisation = jsonObject.getInt("Valorisation");
            this.Soins_A_Domicile = OutilsGestionClasses.recupererBooleen(jsonObject, "Soins_A_Domicile");
            this.Dotation_Std = OutilsGestionClasses.recupererString(jsonObject.getString("Dotation_Std"));
            this.Depot_Code = jsonObject.getInt("Depot_Code");
            this.Qte_par_session = OutilsGestionClasses.recupererBooleen(jsonObject, "Qte_par_session");
            this.Tournee_Protocole = OutilsGestionClasses.recupererString(jsonObject.getString("Tournee_Protocole"));
            this.Tournee_Dotation = OutilsGestionClasses.recupererString(jsonObject.getString("Tournee_Dotation"));
            this.Archiver = OutilsGestionClasses.recupererBooleen(jsonObject, "Archiver");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.Ordre_Prescription_ignorer = OutilsGestionClasses.recupererBooleen(jsonObject, "Ordre_Prescription_ignorer");
            this.Serie_Lundi_1 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Lundi_1"));
            this.Serie_Mardi_2 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Mardi_2"));
            this.Serie_Mercredi_3 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Mercredi_3"));
            this.Serie_Jeudi_4 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Jeudi_4"));
            this.Serie_Vendredi_5 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Vendredi_5"));
            this.Serie_Samedi_6 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Samedi_6"));
            this.Serie_Dimanche_7 = OutilsGestionClasses.recupererString(jsonObject.getString("Serie_Dimanche_7"));
            this.Livraison_A_domicile = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_A_domicile");
            this.Patient_ID = jsonObject.getInt("Patient_ID");
            this.Centre_Hospitalier_Rattachement = OutilsGestionClasses.recupererString(jsonObject.getString("Centre_Hospitalier_Rattachement"));
            this.Nephrologue_Referent = OutilsGestionClasses.recupererString(jsonObject.getString("Nephrologue_Referent"));
            this.Generaliste_Referent = OutilsGestionClasses.recupererString(jsonObject.getString("Generaliste_Referent"));
            this.IDE = OutilsGestionClasses.recupererString(jsonObject.getString("IDE"));
            this.Technique = OutilsGestionClasses.recupererString(jsonObject.getString("Technique"));
            this.Sous_Technique = OutilsGestionClasses.recupererString(jsonObject.getString("Sous_Technique"));
            this.Technique_Debut_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Technique_Debut_Date"));
            this.Interruption_Fin_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Interruption_Fin_Date"));
            this.Interruption_Motif = OutilsGestionClasses.recupererString(jsonObject.getString("Interruption_Motif"));
            this.Arret_Definitif_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Arret_Definitif_Date"));
            this.Arret_Motif = OutilsGestionClasses.recupererString(jsonObject.getString("Arret_Motif"));
            this.Traitement_Modalite = OutilsGestionClasses.recupererString(jsonObject.getString("Traitement_Modalite"));
            this.Interruption_Debut_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Interruption_Debut_Date"));
            this.Validation_Pharmaceutique = OutilsGestionClasses.recupererBooleen(jsonObject, "Validation_Pharmaceutique");
            this.Validation_Pharmaceutique_Date = OutilsGestionClasses.recupererString(jsonObject.getString("Validation_Pharmaceutique_Date"));
            this.Validation_Pharmaceutique_Par = OutilsGestionClasses.recupererString(jsonObject.getString("Validation_Pharmaceutique_Par"));
            this.discipline_Medicale = OutilsGestionClasses.recupererString(jsonObject.getString("discipline_Medicale"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Protocoles_Patients(Cursor cursor) {
        this._UID = cursor.getInt(Protocoles_PatientsOpenHelper.Constantes.NUM_COL__UID_PROTOCOLES_PATIENTS);
        this.Protocole_Std = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_PROTOCOLE_STD_PROTOCOLES_PATIENTS);
        this.IPP = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_IPP_PROTOCOLES_PATIENTS);
        this.Rappel_traitement = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_RAPPEL_TRAITEMENT_PROTOCOLES_PATIENTS);
        this.Frequence = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_FREQUENCE_PROTOCOLES_PATIENTS);
        this.Depot_Reference = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_DEPOT_REFERENCE_PROTOCOLES_PATIENTS);
        this.Debut_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_DEBUT_DATE_PROTOCOLES_PATIENTS);
        this.Fin_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_FIN_DATE_PROTOCOLES_PATIENTS);
        this.Interruption = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_INTERRUPTION_PROTOCOLES_PATIENTS);
        this.Patient_Identite = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_PATIENT_IDENTITE_PROTOCOLES_PATIENTS);
        this.Valorisation = cursor.getInt(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_VALORISATION_PROTOCOLES_PATIENTS);
        this.Soins_A_Domicile = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SOINS_A_DOMICILE_PROTOCOLES_PATIENTS);
        this.Dotation_Std = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_DOTATION_STD_PROTOCOLES_PATIENTS);
        this.Depot_Code = cursor.getInt(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_DEPOT_CODE_PROTOCOLES_PATIENTS);
        this.Qte_par_session = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_QTE_PAR_SESSION_PROTOCOLES_PATIENTS);
        this.Tournee_Protocole = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_TOURNEE_PROTOCOLE_PROTOCOLES_PATIENTS);
        this.Tournee_Dotation = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_TOURNEE_DOTATION_PROTOCOLES_PATIENTS);
        this.Archiver = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_ARCHIVER_PROTOCOLES_PATIENTS);
        this.SYS_DT_MAJ = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PROTOCOLES_PATIENTS);
        this.SYS_HEURE_MAJ = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PROTOCOLES_PATIENTS);
        this.SYS_USER_MAJ = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PROTOCOLES_PATIENTS);
        this.Ordre_Prescription_ignorer = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_ORDRE_PRESCRIPTION_IGNORER_PROTOCOLES_PATIENTS);
        this.Serie_Lundi_1 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_LUNDI_1_PROTOCOLES_PATIENTS);
        this.Serie_Mardi_2 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_MARDI_2_PROTOCOLES_PATIENTS);
        this.Serie_Mercredi_3 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_MERCREDI_3_PROTOCOLES_PATIENTS);
        this.Serie_Jeudi_4 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_JEUDI_4_PROTOCOLES_PATIENTS);
        this.Serie_Vendredi_5 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_VENDREDI_5_PROTOCOLES_PATIENTS);
        this.Serie_Samedi_6 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_SAMEDI_6_PROTOCOLES_PATIENTS);
        this.Serie_Dimanche_7 = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SERIE_DIMANCHE_7_PROTOCOLES_PATIENTS);
        this.Livraison_A_domicile = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_LIVRAISON_A_DOMICILE_PROTOCOLES_PATIENTS);
        this.Patient_ID = cursor.getInt(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_PATIENT_ID_PROTOCOLES_PATIENTS);
        this.Centre_Hospitalier_Rattachement = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_CENTRE_HOSPITALIER_RATTACHEMENT_PROTOCOLES_PATIENTS);
        this.Nephrologue_Referent = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_NEPHROLOGUE_REFERENT_PROTOCOLES_PATIENTS);
        this.Generaliste_Referent = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_GENERALISTE_REFERENT_PROTOCOLES_PATIENTS);
        this.IDE = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_IDE_PROTOCOLES_PATIENTS);
        this.Technique = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_TECHNIQUE_PROTOCOLES_PATIENTS);
        this.Sous_Technique = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_SOUS_TECHNIQUE_PROTOCOLES_PATIENTS);
        this.Technique_Debut_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_TECHNIQUE_DEBUT_DATE_PROTOCOLES_PATIENTS);
        this.Interruption_Fin_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_INTERRUPTION_FIN_DATE_PROTOCOLES_PATIENTS);
        this.Interruption_Motif = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_INTERRUPTION_MOTIF_PROTOCOLES_PATIENTS);
        this.Arret_Definitif_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_ARRET_DEFINITIF_DATE_PROTOCOLES_PATIENTS);
        this.Arret_Motif = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_ARRET_MOTIF_PROTOCOLES_PATIENTS);
        this.Traitement_Modalite = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_TRAITEMENT_MODALITE_PROTOCOLES_PATIENTS);
        this.Interruption_Debut_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_INTERRUPTION_DEBUT_DATE_PROTOCOLES_PATIENTS);
        this.Validation_Pharmaceutique = OutilsGestionClasses.recupererBooleen(cursor, Protocoles_PatientsOpenHelper.Constantes.NUM_COL_VALIDATION_PHARMACEUTIQUE_PROTOCOLES_PATIENTS);
        this.Validation_Pharmaceutique_Date = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_VALIDATION_PHARMACEUTIQUE_DATE_PROTOCOLES_PATIENTS);
        this.Validation_Pharmaceutique_Par = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_VALIDATION_PHARMACEUTIQUE_PAR_PROTOCOLES_PATIENTS);
        this.discipline_Medicale = cursor.getString(Protocoles_PatientsOpenHelper.Constantes.NUM_COL_DISCIPLINE_MEDICALE_PROTOCOLES_PATIENTS);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);

    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getProtocole_Std() {
        return Protocole_Std;
    }

    public void setProtocole_Std(String protocole_Std) {
        Protocole_Std = protocole_Std;
    }

    public String getIPP() {
        return IPP;
    }

    public void setIPP(String IPP) {
        this.IPP = IPP;
    }

    public String getRappel_traitement() {
        return Rappel_traitement;
    }

    public void setRappel_traitement(String rappel_traitement) {
        Rappel_traitement = rappel_traitement;
    }

    public String getFrequence() {
        return Frequence;
    }

    public void setFrequence(String frequence) {
        Frequence = frequence;
    }

    public String getDepot_Reference() {
        return Depot_Reference;
    }

    public void setDepot_Reference(String depot_Reference) {
        Depot_Reference = depot_Reference;
    }

    public String getDebut_Date() {
        return Debut_Date;
    }

    public void setDebut_Date(String debut_Date) {
        Debut_Date = debut_Date;
    }

    public String getFin_Date() {
        return Fin_Date;
    }

    public void setFin_Date(String fin_Date) {
        Fin_Date = fin_Date;
    }

    public boolean isInterruption() {
        return Interruption;
    }

    public void setInterruption(boolean interruption) {
        Interruption = interruption;
    }

    public String getPatient_Identite() {
        return Patient_Identite;
    }

    public void setPatient_Identite(String patient_Identite) {
        Patient_Identite = patient_Identite;
    }

    public int getValorisation() {
        return Valorisation;
    }

    public void setValorisation(int valorisation) {
        Valorisation = valorisation;
    }

    public boolean isSoins_A_Domicile() {
        return Soins_A_Domicile;
    }

    public void setSoins_A_Domicile(boolean soins_A_Domicile) {
        Soins_A_Domicile = soins_A_Domicile;
    }

    public String getDotation_Std() {
        return Dotation_Std;
    }

    public void setDotation_Std(String dotation_Std) {
        Dotation_Std = dotation_Std;
    }

    public int getDepot_Code() {
        return Depot_Code;
    }

    public void setDepot_Code(int depot_Code) {
        Depot_Code = depot_Code;
    }

    public boolean isQte_par_session() {
        return Qte_par_session;
    }

    public void setQte_par_session(boolean qte_par_session) {
        Qte_par_session = qte_par_session;
    }

    public String getTournee_Protocole() {
        return Tournee_Protocole;
    }

    public void setTournee_Protocole(String tournee_Protocole) {
        Tournee_Protocole = tournee_Protocole;
    }

    public String getTournee_Dotation() {
        return Tournee_Dotation;
    }

    public void setTournee_Dotation(String tournee_Dotation) {
        Tournee_Dotation = tournee_Dotation;
    }

    public boolean isArchiver() {
        return Archiver;
    }

    public void setArchiver(boolean archiver) {
        Archiver = archiver;
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

    public boolean isOrdre_Prescription_ignorer() {
        return Ordre_Prescription_ignorer;
    }

    public void setOrdre_Prescription_ignorer(boolean ordre_Prescription_ignorer) {
        Ordre_Prescription_ignorer = ordre_Prescription_ignorer;
    }

    public String getSerie_Lundi_1() {
        return Serie_Lundi_1;
    }

    public void setSerie_Lundi_1(String serie_Lundi_1) {
        Serie_Lundi_1 = serie_Lundi_1;
    }

    public String getSerie_Mardi_2() {
        return Serie_Mardi_2;
    }

    public void setSerie_Mardi_2(String serie_Mardi_2) {
        Serie_Mardi_2 = serie_Mardi_2;
    }

    public String getSerie_Mercredi_3() {
        return Serie_Mercredi_3;
    }

    public void setSerie_Mercredi_3(String serie_Mercredi_3) {
        Serie_Mercredi_3 = serie_Mercredi_3;
    }

    public String getSerie_Jeudi_4() {
        return Serie_Jeudi_4;
    }

    public void setSerie_Jeudi_4(String serie_Jeudi_4) {
        Serie_Jeudi_4 = serie_Jeudi_4;
    }

    public String getSerie_Vendredi_5() {
        return Serie_Vendredi_5;
    }

    public void setSerie_Vendredi_5(String serie_Vendredi_5) {
        Serie_Vendredi_5 = serie_Vendredi_5;
    }

    public String getSerie_Samedi_6() {
        return Serie_Samedi_6;
    }

    public void setSerie_Samedi_6(String serie_Samedi_6) {
        Serie_Samedi_6 = serie_Samedi_6;
    }

    public String getSerie_Dimanche_7() {
        return Serie_Dimanche_7;
    }

    public void setSerie_Dimanche_7(String serie_Dimanche_7) {
        Serie_Dimanche_7 = serie_Dimanche_7;
    }

    public boolean isLivraison_A_domicile() {
        return Livraison_A_domicile;
    }

    public void setLivraison_A_domicile(boolean livraison_A_domicile) {
        Livraison_A_domicile = livraison_A_domicile;
    }

    public int getPatient_ID() {
        return Patient_ID;
    }

    public void setPatient_ID(int patient_ID) {
        Patient_ID = patient_ID;
    }

    public String getCentre_Hospitalier_Rattachement() {
        return Centre_Hospitalier_Rattachement;
    }

    public void setCentre_Hospitalier_Rattachement(String centre_Hospitalier_Rattachement) {
        Centre_Hospitalier_Rattachement = centre_Hospitalier_Rattachement;
    }

    public String getNephrologue_Referent() {
        return Nephrologue_Referent;
    }

    public void setNephrologue_Referent(String nephrologue_Referent) {
        Nephrologue_Referent = nephrologue_Referent;
    }

    public String getGeneraliste_Referent() {
        return Generaliste_Referent;
    }

    public void setGeneraliste_Referent(String generaliste_Referent) {
        Generaliste_Referent = generaliste_Referent;
    }

    public String getIDE() {
        return IDE;
    }

    public void setIDE(String IDE) {
        this.IDE = IDE;
    }

    public String getTechnique() {
        return Technique;
    }

    public void setTechnique(String technique) {
        Technique = technique;
    }

    public String getSous_Technique() {
        return Sous_Technique;
    }

    public void setSous_Technique(String sous_Technique) {
        Sous_Technique = sous_Technique;
    }

    public String getTechnique_Debut_Date() {
        return Technique_Debut_Date;
    }

    public void setTechnique_Debut_Date(String technique_Debut_Date) {
        Technique_Debut_Date = technique_Debut_Date;
    }

    public String getInterruption_Fin_Date() {
        return Interruption_Fin_Date;
    }

    public void setInterruption_Fin_Date(String interruption_Fin_Date) {
        Interruption_Fin_Date = interruption_Fin_Date;
    }

    public String getInterruption_Motif() {
        return Interruption_Motif;
    }

    public void setInterruption_Motif(String interruption_Motif) {
        Interruption_Motif = interruption_Motif;
    }

    public String getArret_Definitif_Date() {
        return Arret_Definitif_Date;
    }

    public void setArret_Definitif_Date(String arret_Definitif_Date) {
        Arret_Definitif_Date = arret_Definitif_Date;
    }

    public String getArret_Motif() {
        return Arret_Motif;
    }

    public void setArret_Motif(String arret_Motif) {
        Arret_Motif = arret_Motif;
    }

    public String getTraitement_Modalite() {
        return Traitement_Modalite;
    }

    public void setTraitement_Modalite(String traitement_Modalite) {
        Traitement_Modalite = traitement_Modalite;
    }

    public String getInterruption_Debut_Date() {
        return Interruption_Debut_Date;
    }

    public void setInterruption_Debut_Date(String interruption_Debut_Date) {
        Interruption_Debut_Date = interruption_Debut_Date;
    }

    public boolean isValidation_Pharmaceutique() {
        return Validation_Pharmaceutique;
    }

    public void setValidation_Pharmaceutique(boolean validation_Pharmaceutique) {
        Validation_Pharmaceutique = validation_Pharmaceutique;
    }

    public String getValidation_Pharmaceutique_Date() {
        return Validation_Pharmaceutique_Date;
    }

    public void setValidation_Pharmaceutique_Date(String validation_Pharmaceutique_Date) {
        Validation_Pharmaceutique_Date = validation_Pharmaceutique_Date;
    }

    public String getValidation_Pharmaceutique_Par() {
        return Validation_Pharmaceutique_Par;
    }

    public void setValidation_Pharmaceutique_Par(String validation_Pharmaceutique_Par) {
        Validation_Pharmaceutique_Par = validation_Pharmaceutique_Par;
    }

    public String getDiscipline_Medicale() {
        return discipline_Medicale;
    }

    public void setDiscipline_Medicale(String discipline_Medicale) {
        this.discipline_Medicale = discipline_Medicale;
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("Protocole_Std", Protocole_Std);
            jsonObject.put("IPP", IPP);
            jsonObject.put("Rappel_traitement", Rappel_traitement);
            jsonObject.put("Frequence", Frequence);
            jsonObject.put("Depot_Reference", Depot_Reference);
            jsonObject.put("Debut_Date", Debut_Date);
            jsonObject.put("Fin_Date", Fin_Date);
            jsonObject.put("Interruption", Interruption);
            jsonObject.put("Patient_Identite", Patient_Identite);
            jsonObject.put("Valorisation", Valorisation);
            jsonObject.put("Soins_A_Domicile", Soins_A_Domicile);
            jsonObject.put("Dotation_Std", Dotation_Std);
            jsonObject.put("Depot_Code", Depot_Code);
            jsonObject.put("Qte_par_session", Qte_par_session);
            jsonObject.put("Tournee_Protocole", Tournee_Protocole);
            jsonObject.put("Tournee_Dotation", Tournee_Dotation);
            jsonObject.put("Archiver", Archiver);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Ordre_Prescription_ignorer", Ordre_Prescription_ignorer);
            jsonObject.put("Serie_Lundi_1", Serie_Lundi_1);
            jsonObject.put("Serie_Mardi_2", Serie_Mardi_2);
            jsonObject.put("Serie_Mercredi_3", Serie_Mercredi_3);
            jsonObject.put("Serie_Jeudi_4", Serie_Jeudi_4);
            jsonObject.put("Serie_Vendredi_5", Serie_Vendredi_5);
            jsonObject.put("Serie_Samedi_6", Serie_Samedi_6);
            jsonObject.put("Serie_Dimanche_7", Serie_Dimanche_7);
            jsonObject.put("Livraison_A_domicile", Livraison_A_domicile);
            jsonObject.put("Patient_ID", Patient_ID);
            jsonObject.put("Centre_Hospitalier_Rattachement", Centre_Hospitalier_Rattachement);
            jsonObject.put("Nephrologue_Referent", Nephrologue_Referent);
            jsonObject.put("Generaliste_Referent", Generaliste_Referent);
            jsonObject.put("IDE", IDE);
            jsonObject.put("Technique", Technique);
            jsonObject.put("Sous_Technique", Sous_Technique);
            jsonObject.put("Technique_Debut_Date", Technique_Debut_Date);
            jsonObject.put("Interruption_Fin_Date", Interruption_Fin_Date);
            jsonObject.put("Interruption_Motif", Interruption_Motif);
            jsonObject.put("Arret_Definitif_Date", Arret_Definitif_Date);
            jsonObject.put("Arret_Motif", Arret_Motif);
            jsonObject.put("Traitement_Modalite", Traitement_Modalite);
            jsonObject.put("Interruption_Debut_Date", Interruption_Debut_Date);
            jsonObject.put("Validation_Pharmaceutique", Validation_Pharmaceutique);
            jsonObject.put("Validation_Pharmaceutique_Date", Validation_Pharmaceutique_Date);
            jsonObject.put("Validation_Pharmaceutique_Par", Validation_Pharmaceutique_Par);
            jsonObject.put("discipline_Medicale", discipline_Medicale);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "Protocoles_Patients{" +
                "_UID=" + _UID +
                ", Protocole_Std='" + Protocole_Std + '\'' +
                ", IPP='" + IPP + '\'' +
                ", Rappel_traitement='" + Rappel_traitement + '\'' +
                ", Frequence='" + Frequence + '\'' +
                ", Depot_Reference='" + Depot_Reference + '\'' +
                ", Debut_Date='" + Debut_Date + '\'' +
                ", Fin_Date='" + Fin_Date + '\'' +
                ", Interruption=" + Interruption +
                ", Patient_Identite='" + Patient_Identite + '\'' +
                ", Valorisation=" + Valorisation +
                ", Soins_A_Domicile=" + Soins_A_Domicile +
                ", Dotation_Std='" + Dotation_Std + '\'' +
                ", Depot_Code=" + Depot_Code +
                ", Qte_par_session=" + Qte_par_session +
                ", Tournee_Protocole='" + Tournee_Protocole + '\'' +
                ", Tournee_Dotation='" + Tournee_Dotation + '\'' +
                ", Archiver=" + Archiver +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", Ordre_Prescription_ignorer=" + Ordre_Prescription_ignorer +
                ", Serie_Lundi_1='" + Serie_Lundi_1 + '\'' +
                ", Serie_Mardi_2='" + Serie_Mardi_2 + '\'' +
                ", Serie_Mercredi_3='" + Serie_Mercredi_3 + '\'' +
                ", Serie_Jeudi_4='" + Serie_Jeudi_4 + '\'' +
                ", Serie_Vendredi_5='" + Serie_Vendredi_5 + '\'' +
                ", Serie_Samedi_6='" + Serie_Samedi_6 + '\'' +
                ", Serie_Dimanche_7='" + Serie_Dimanche_7 + '\'' +
                ", Livraison_A_domicile=" + Livraison_A_domicile +
                ", Patient_ID=" + Patient_ID +
                ", Centre_Hospitalier_Rattachement='" + Centre_Hospitalier_Rattachement + '\'' +
                ", Nephrologue_Referent='" + Nephrologue_Referent + '\'' +
                ", Generaliste_Referent='" + Generaliste_Referent + '\'' +
                ", IDE='" + IDE + '\'' +
                ", Technique='" + Technique + '\'' +
                ", Sous_Technique='" + Sous_Technique + '\'' +
                ", Technique_Debut_Date='" + Technique_Debut_Date + '\'' +
                ", Interruption_Fin_Date='" + Interruption_Fin_Date + '\'' +
                ", Interruption_Motif='" + Interruption_Motif + '\'' +
                ", Arret_Definitif_Date='" + Arret_Definitif_Date + '\'' +
                ", Arret_Motif='" + Arret_Motif + '\'' +
                ", Traitement_Modalite='" + Traitement_Modalite + '\'' +
                ", Interruption_Debut_Date='" + Interruption_Debut_Date + '\'' +
                ", Validation_Pharmaceutique=" + Validation_Pharmaceutique +
                ", Validation_Pharmaceutique_Date='" + Validation_Pharmaceutique_Date + '\'' +
                ", Validation_Pharmaceutique_Par='" + Validation_Pharmaceutique_Par + '\'' +
                ", discipline_Medicale='" + discipline_Medicale + '\'' +
                ", phiwms_mobileUUID=" + phiwms_mobileUUID +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Protocoles_Patients protocoles_patients = (Protocoles_Patients) obj;

        if (this.getphiwms_mobileUUID() == protocoles_patients.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getphiwms_mobileUUID() > protocoles_patients.getphiwms_mobileUUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Protocoles_Patients) obj).getphiwms_mobileUUID() == this.getphiwms_mobileUUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Protocoles_Patients)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
