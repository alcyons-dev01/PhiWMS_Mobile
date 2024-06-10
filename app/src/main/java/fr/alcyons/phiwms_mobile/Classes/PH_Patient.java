package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PatientOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;
public class PH_Patient implements Serializable, Comparable {
    private int _patientUID;
    private String Civilite;
    private String Nom_naissance;
    private String Prenom;
    private String Adresse1;
    private String Adresse2;
    private String CP;
    private String Ville;
    private String Tel1;
    private String IPP;
    private String Fax;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Adresse3;
    private String Tel_Professionnel;
    private String Tel2;
    private String Technique;
    private String IPP_Fac;
    private String IPP_DM;
    private String Nom_Marital;
    private String Date_Naissance;
    private String Matricule;
    private String Clef;
    private String Lieu_Naissance;
    private boolean Sexe_Masculin_Feminin;
    private String Fax_Professionnel;
    private String Profession;
    private String Nom_Usuel;
    private String Email;
    private String Ressource_Adr1;
    private String Ressource_Adr2;
    private String Ressource_CP;
    private String Ressource_Ville;
    private String Personne_Ressource;
    private String Ressource_Tel;
    private String Ressource_Fax;
    private String Centre_Hospitalier;
    private String Praticien;
    private String Motif_Suspension_Traitement;
    private String Detail_Etat_patient;
    private String Infirmier;
    private String Inf_Adr1;
    private String Inf_Adr2;
    private String Inf_CP;
    private String Inf_Ville;
    private String Inf_Fax;
    private String Inf_Tel;
    private String Inf_Email;
    private String Date_entree;
    private String Date_Debut_Traitement;
    private String Sous_Technique;
    private String Approvisionnement;
    private String Lieu_Traitement;
    private int ID_Lieu_Traitement;
    private boolean Ascenceur;
    private String Escalier;
    private int Etage;
    private String Dgicode;
    private String Date_Etat;
    private boolean Archive;
    private String Securite_Sociale;
    private String Autre_Tel;
    private String Autre_Nom;
    private String CPAM_Nom;
    private String CPAM_Adresse;
    private String CPAM_CP;
    private String CPAM_Ville;
    private int Poids;
    private String INSC;
    private String Sexe;
    private String Pharmacie;
    private String Traitement_Modalite;
    private String Photo_lien;
    private String Document_partage;
    private String discipline_Medicale;
    private int phiwms_mobileUUID = -1;

    public PH_Patient(Cursor cursor) {
        this._patientUID = cursor.getInt(PH_PatientOpenHelper.Constantes.NUM_COL__PATIENTUID_PH_PATIENT);
        this.Civilite = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CIVILITE_PH_PATIENT);
        this.Nom_naissance = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_NOM_NAISSANCE_PH_PATIENT);
        this.Prenom = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PRENOM_PH_PATIENT);
        this.Adresse1 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_ADRESSE1_PH_PATIENT);
        this.Adresse2 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_ADRESSE2_PH_PATIENT);
        this.CP = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CP_PH_PATIENT);
        this.Ville = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_VILLE_PH_PATIENT);
        this.Tel1 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_TEL1_PH_PATIENT);
        this.IPP = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_IPP_PH_PATIENT);
        this.Fax = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_FAX_PH_PATIENT);
        this.SYS_DT_MAJ = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PH_PATIENT);
        this.SYS_HEURE_MAJ = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PH_PATIENT);
        this.SYS_USER_MAJ = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PH_PATIENT);
        this.Adresse3 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_ADRESSE3_PH_PATIENT);
        this.Tel_Professionnel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_TEL_PROFESSIONNEL_PH_PATIENT);
        this.Tel2 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_TEL2_PH_PATIENT);
        this.Technique = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_TECHNIQUE_PH_PATIENT);
        this.IPP_Fac = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_IPP_FAC_PH_PATIENT);
        this.IPP_DM = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_IPP_DM_PH_PATIENT);
        this.Nom_Marital = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_NOM_MARITAL_PH_PATIENT);
        this.Date_Naissance = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DATE_NAISSANCE_PH_PATIENT);
        this.Matricule = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_MATRICULE_PH_PATIENT);
        this.Clef = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CLEF_PH_PATIENT);
        this.Lieu_Naissance = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_LIEU_NAISSANCE_PH_PATIENT);
        this.Sexe_Masculin_Feminin = OutilsGestionClasses.recupererBooleen(cursor, PH_PatientOpenHelper.Constantes.NUM_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT);
        this.Fax_Professionnel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_FAX_PROFESSIONNEL_PH_PATIENT);
        this.Profession = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PROFESSION_PH_PATIENT);
        this.Nom_Usuel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_NOM_USUEL_PH_PATIENT);
        this.Email = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_EMAIL_PH_PATIENT);
        this.Ressource_Adr1 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_ADR1_PH_PATIENT);
        this.Ressource_Adr2 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_ADR2_PH_PATIENT);
        this.Ressource_CP = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_CP_PH_PATIENT);
        this.Ressource_Ville = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_VILLE_PH_PATIENT);
        this.Personne_Ressource = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PERSONNE_RESSOURCE_PH_PATIENT);
        this.Ressource_Tel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_TEL_PH_PATIENT);
        this.Ressource_Fax = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_RESSOURCE_FAX_PH_PATIENT);
        this.Centre_Hospitalier = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CENTRE_HOSPITALIER_PH_PATIENT);
        this.Praticien = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PRATICIEN_PH_PATIENT);
        this.Motif_Suspension_Traitement = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT);
        this.Detail_Etat_patient = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DETAIL_ETAT_PATIENT_PH_PATIENT);
        this.Infirmier = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INFIRMIER_PH_PATIENT);
        this.Inf_Adr1 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_ADR1_PH_PATIENT);
        this.Inf_Adr2 = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_ADR2_PH_PATIENT);
        this.Inf_CP = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_CP_PH_PATIENT);
        this.Inf_Ville = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_VILLE_PH_PATIENT);
        this.Inf_Fax = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_FAX_PH_PATIENT);
        this.Inf_Tel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_TEL_PH_PATIENT);
        this.Inf_Email = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INF_EMAIL_PH_PATIENT);
        this.Date_entree = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DATE_ENTREE_PH_PATIENT);
        this.Date_Debut_Traitement = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT);
        this.Sous_Technique = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SOUS_TECHNIQUE_PH_PATIENT);
        this.Approvisionnement = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_APPROVISIONNEMENT_PH_PATIENT);
        this.Lieu_Traitement = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_LIEU_TRAITEMENT_PH_PATIENT);
        this.ID_Lieu_Traitement = cursor.getInt(PH_PatientOpenHelper.Constantes.NUM_COL_ID_LIEU_TRAITEMENT_PH_PATIENT);
        this.Ascenceur = OutilsGestionClasses.recupererBooleen(cursor, PH_PatientOpenHelper.Constantes.NUM_COL_ASCENCEUR_PH_PATIENT);
        this.Escalier = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_ESCALIER_PH_PATIENT);
        this.Etage = cursor.getInt(PH_PatientOpenHelper.Constantes.NUM_COL_ETAGE_PH_PATIENT);
        this.Dgicode = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DGICODE_PH_PATIENT);
        this.Date_Etat = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DATE_ETAT_PH_PATIENT);
        this.Archive = OutilsGestionClasses.recupererBooleen(cursor, PH_PatientOpenHelper.Constantes.NUM_COL_ARCHIVE_PH_PATIENT);
        this.Securite_Sociale = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SECURITE_SOCIALE_PH_PATIENT);
        this.Autre_Tel = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_AUTRE_TEL_PH_PATIENT);
        this.Autre_Nom = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_AUTRE_NOM_PH_PATIENT);
        this.CPAM_Nom = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CPAM_NOM_PH_PATIENT);
        this.CPAM_Adresse = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CPAM_ADRESSE_PH_PATIENT);
        this.CPAM_CP = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CPAM_CP_PH_PATIENT);
        this.CPAM_Ville = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_CPAM_VILLE_PH_PATIENT);
        this.Poids = cursor.getInt(PH_PatientOpenHelper.Constantes.NUM_COL_POIDS_PH_PATIENT);
        this.INSC = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_INSC_PH_PATIENT);
        this.Sexe = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_SEXE_PH_PATIENT);
        this.Pharmacie = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PHARMACIE_PH_PATIENT);
        this.Traitement_Modalite = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_TRAITEMENT_MODALITE_PH_PATIENT);
        this.Photo_lien = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_PHOTO_LIEN_PH_PATIENT);
        this.Document_partage = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DOCUMENT_PARTAGE_PH_PATIENT);
        // this.discipline_Medicale = cursor.getString(PH_PatientOpenHelper.Constantes.NUM_COL_DISCIPLINE_MEDICALE_PH_PATIENT);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int get_patientUID() {
        return _patientUID;
    }

    public void set_patientUID(int _patientUID) {
        this._patientUID = _patientUID;
    }

    public String getCivilite() {
        return Civilite;
    }

    public void setCivilite(String civilite) {
        Civilite = civilite;
    }

    public String getNom_naissance() {
        return Nom_naissance;
    }

    public void setNom_naissance(String nom_naissance) {
        Nom_naissance = nom_naissance;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
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

    public String getTel1() {
        return Tel1;
    }

    public void setTel1(String tel1) {
        Tel1 = tel1;
    }

    public String getIPP() {
        return IPP;
    }

    public void setIPP(String IPP) {
        this.IPP = IPP;
    }

    public String getFax() {
        return Fax;
    }

    public void setFax(String fax) {
        Fax = fax;
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

    public String getAdresse3() {
        return Adresse3;
    }

    public void setAdresse3(String adresse3) {
        Adresse3 = adresse3;
    }

    public String getTel_Professionnel() {
        return Tel_Professionnel;
    }

    public void setTel_Professionnel(String tel_Professionnel) {
        Tel_Professionnel = tel_Professionnel;
    }

    public String getTel2() {
        return Tel2;
    }

    public void setTel2(String tel2) {
        Tel2 = tel2;
    }

    public String getTechnique() {
        return Technique;
    }

    public void setTechnique(String technique) {
        Technique = technique;
    }

    public String getIPP_Fac() {
        return IPP_Fac;
    }

    public void setIPP_Fac(String IPP_Fac) {
        this.IPP_Fac = IPP_Fac;
    }

    public String getIPP_DM() {
        return IPP_DM;
    }

    public void setIPP_DM(String IPP_DM) {
        this.IPP_DM = IPP_DM;
    }

    public String getNom_Marital() {
        return Nom_Marital;
    }

    public void setNom_Marital(String nom_Marital) {
        Nom_Marital = nom_Marital;
    }

    public String getDate_Naissance() {
        return Date_Naissance;
    }

    public void setDate_Naissance(String date_Naissance) {
        Date_Naissance = date_Naissance;
    }

    public String getMatricule() {
        return Matricule;
    }

    public void setMatricule(String matricule) {
        Matricule = matricule;
    }

    public String getClef() {
        return Clef;
    }

    public void setClef(String clef) {
        Clef = clef;
    }

    public String getLieu_Naissance() {
        return Lieu_Naissance;
    }

    public void setLieu_Naissance(String lieu_Naissance) {
        Lieu_Naissance = lieu_Naissance;
    }

    public boolean isSexe_Masculin_Feminin() {
        return Sexe_Masculin_Feminin;
    }

    public void setSexe_Masculin_Feminin(boolean sexe_Masculin_Feminin) {
        Sexe_Masculin_Feminin = sexe_Masculin_Feminin;
    }

    public String getFax_Professionnel() {
        return Fax_Professionnel;
    }

    public void setFax_Professionnel(String fax_Professionnel) {
        Fax_Professionnel = fax_Professionnel;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String getNom_Usuel() {
        return Nom_Usuel;
    }

    public void setNom_Usuel(String nom_Usuel) {
        Nom_Usuel = nom_Usuel;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRessource_Adr1() {
        return Ressource_Adr1;
    }

    public void setRessource_Adr1(String ressource_Adr1) {
        Ressource_Adr1 = ressource_Adr1;
    }

    public String getRessource_Adr2() {
        return Ressource_Adr2;
    }

    public void setRessource_Adr2(String ressource_Adr2) {
        Ressource_Adr2 = ressource_Adr2;
    }

    public String getRessource_CP() {
        return Ressource_CP;
    }

    public void setRessource_CP(String ressource_CP) {
        Ressource_CP = ressource_CP;
    }

    public String getRessource_Ville() {
        return Ressource_Ville;
    }

    public void setRessource_Ville(String ressource_Ville) {
        Ressource_Ville = ressource_Ville;
    }

    public String getPersonne_Ressource() {
        return Personne_Ressource;
    }

    public void setPersonne_Ressource(String personne_Ressource) {
        Personne_Ressource = personne_Ressource;
    }

    public String getRessource_Tel() {
        return Ressource_Tel;
    }

    public void setRessource_Tel(String ressource_Tel) {
        Ressource_Tel = ressource_Tel;
    }

    public String getRessource_Fax() {
        return Ressource_Fax;
    }

    public void setRessource_Fax(String ressource_Fax) {
        Ressource_Fax = ressource_Fax;
    }

    public String getCentre_Hospitalier() {
        return Centre_Hospitalier;
    }

    public void setCentre_Hospitalier(String centre_Hospitalier) {
        Centre_Hospitalier = centre_Hospitalier;
    }

    public String getPraticien() {
        return Praticien;
    }

    public void setPraticien(String praticien) {
        Praticien = praticien;
    }

    public String getMotif_Suspension_Traitement() {
        return Motif_Suspension_Traitement;
    }

    public void setMotif_Suspension_Traitement(String motif_Suspension_Traitement) {
        Motif_Suspension_Traitement = motif_Suspension_Traitement;
    }

    public String getDetail_Etat_patient() {
        return Detail_Etat_patient;
    }

    public void setDetail_Etat_patient(String detail_Etat_patient) {
        Detail_Etat_patient = detail_Etat_patient;
    }

    public String getInfirmier() {
        return Infirmier;
    }

    public void setInfirmier(String infirmier) {
        Infirmier = infirmier;
    }

    public String getInf_Adr1() {
        return Inf_Adr1;
    }

    public void setInf_Adr1(String inf_Adr1) {
        Inf_Adr1 = inf_Adr1;
    }

    public String getInf_Adr2() {
        return Inf_Adr2;
    }

    public void setInf_Adr2(String inf_Adr2) {
        Inf_Adr2 = inf_Adr2;
    }

    public String getInf_CP() {
        return Inf_CP;
    }

    public void setInf_CP(String inf_CP) {
        Inf_CP = inf_CP;
    }

    public String getInf_Ville() {
        return Inf_Ville;
    }

    public void setInf_Ville(String inf_Ville) {
        Inf_Ville = inf_Ville;
    }

    public String getInf_Fax() {
        return Inf_Fax;
    }

    public void setInf_Fax(String inf_Fax) {
        Inf_Fax = inf_Fax;
    }

    public String getInf_Tel() {
        return Inf_Tel;
    }

    public void setInf_Tel(String inf_Tel) {
        Inf_Tel = inf_Tel;
    }

    public String getInf_Email() {
        return Inf_Email;
    }

    public void setInf_Email(String inf_Email) {
        Inf_Email = inf_Email;
    }

    public String getDate_entree() {
        return Date_entree;
    }

    public void setDate_entree(String date_entree) {
        Date_entree = date_entree;
    }

    public String getDate_Debut_Traitement() {
        return Date_Debut_Traitement;
    }

    public void setDate_Debut_Traitement(String date_Debut_Traitement) {
        Date_Debut_Traitement = date_Debut_Traitement;
    }

    public String getSous_Technique() {
        return Sous_Technique;
    }

    public void setSous_Technique(String sous_Technique) {
        Sous_Technique = sous_Technique;
    }

    public String getApprovisionnement() {
        return Approvisionnement;
    }

    public void setApprovisionnement(String approvisionnement) {
        Approvisionnement = approvisionnement;
    }

    public String getLieu_Traitement() {
        return Lieu_Traitement;
    }

    public void setLieu_Traitement(String lieu_Traitement) {
        Lieu_Traitement = lieu_Traitement;
    }

    public int getID_Lieu_Traitement() {
        return ID_Lieu_Traitement;
    }

    public void setID_Lieu_Traitement(int ID_Lieu_Traitement) {
        this.ID_Lieu_Traitement = ID_Lieu_Traitement;
    }

    public boolean isAscenceur() {
        return Ascenceur;
    }

    public void setAscenceur(boolean ascenceur) {
        Ascenceur = ascenceur;
    }

    public String getEscalier() {
        return Escalier;
    }

    public void setEscalier(String escalier) {
        Escalier = escalier;
    }

    public int getEtage() {
        return Etage;
    }

    public void setEtage(int etage) {
        Etage = etage;
    }

    public String getDgicode() {
        return Dgicode;
    }

    public void setDgicode(String dgicode) {
        Dgicode = dgicode;
    }

    public String getDate_Etat() {
        return Date_Etat;
    }

    public void setDate_Etat(String date_Etat) {
        Date_Etat = date_Etat;
    }

    public boolean isArchive() {
        return Archive;
    }

    public void setArchive(boolean archive) {
        Archive = archive;
    }

    public String getSecurite_Sociale() {
        return Securite_Sociale;
    }

    public void setSecurite_Sociale(String securite_Sociale) {
        Securite_Sociale = securite_Sociale;
    }

    public String getAutre_Tel() {
        return Autre_Tel;
    }

    public void setAutre_Tel(String autre_Tel) {
        Autre_Tel = autre_Tel;
    }

    public String getAutre_Nom() {
        return Autre_Nom;
    }

    public void setAutre_Nom(String autre_Nom) {
        Autre_Nom = autre_Nom;
    }

    public String getCPAM_Nom() {
        return CPAM_Nom;
    }

    public void setCPAM_Nom(String CPAM_Nom) {
        this.CPAM_Nom = CPAM_Nom;
    }

    public String getCPAM_Adresse() {
        return CPAM_Adresse;
    }

    public void setCPAM_Adresse(String CPAM_Adresse) {
        this.CPAM_Adresse = CPAM_Adresse;
    }

    public String getCPAM_CP() {
        return CPAM_CP;
    }

    public void setCPAM_CP(String CPAM_CP) {
        this.CPAM_CP = CPAM_CP;
    }

    public String getCPAM_Ville() {
        return CPAM_Ville;
    }

    public void setCPAM_Ville(String CPAM_Ville) {
        this.CPAM_Ville = CPAM_Ville;
    }

    public int getPoids() {
        return Poids;
    }

    public void setPoids(int poids) {
        Poids = poids;
    }

    public String getINSC() {
        return INSC;
    }

    public void setINSC(String INSC) {
        this.INSC = INSC;
    }

    public String getSexe() {
        return Sexe;
    }

    public void setSexe(String sexe) {
        Sexe = sexe;
    }

    public String getPharmacie() {
        return Pharmacie;
    }

    public void setPharmacie(String pharmacie) {
        Pharmacie = pharmacie;
    }

    public String getTraitement_Modalite() {
        return Traitement_Modalite;
    }

    public void setTraitement_Modalite(String traitement_Modalite) {
        Traitement_Modalite = traitement_Modalite;
    }

    public String getPhoto_lien() {
        return Photo_lien;
    }

    public void setPhoto_lien(String photo_lien) {
        Photo_lien = photo_lien;
    }

    public String getDocument_partage() {
        return Document_partage;
    }

    public void setDocument_partage(String document_partage) {
        Document_partage = document_partage;
    }

    public String getDiscipline_Medicale() {
        return discipline_Medicale;
    }

    public void setDiscipline_Medicale(String discipline_Medicale) {
        this.discipline_Medicale = discipline_Medicale;
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_patientUID", _patientUID);
            jsonObject.put("Civilite", Civilite);
            jsonObject.put("Nom_naissance", Nom_naissance);
            jsonObject.put("Prenom", Prenom);
            jsonObject.put("Adresse1", Adresse1);
            jsonObject.put("Adresse2", Adresse2);
            jsonObject.put("CP", CP);
            jsonObject.put("Ville", Ville);
            jsonObject.put("Tel1", Tel1);
            jsonObject.put("IPP", IPP);
            jsonObject.put("Fax", Fax);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Adresse3", Adresse3);
            jsonObject.put("Tel_Professionnel", Tel_Professionnel);
            jsonObject.put("Tel2", Tel2);
            jsonObject.put("Technique", Technique);
            jsonObject.put("IPP_Fac", IPP_Fac);
            jsonObject.put("IPP_DM", IPP_DM);
            jsonObject.put("Nom_Marital", Nom_Marital);
            jsonObject.put("Date_Naissance", Date_Naissance);
            jsonObject.put("Matricule", Matricule);
            jsonObject.put("Clef", Clef);
            jsonObject.put("Lieu_Naissance", Lieu_Naissance);
            jsonObject.put("Sexe_Masculin_Feminin", Sexe_Masculin_Feminin);
            jsonObject.put("Fax_Professionnel", Fax_Professionnel);
            jsonObject.put("Profession", Profession);
            jsonObject.put("Nom_Usuel", Nom_Usuel);
            jsonObject.put("Email", Email);
            jsonObject.put("Ressource_Adr1", Ressource_Adr1);
            jsonObject.put("Ressource_Adr2", Ressource_Adr2);
            jsonObject.put("Ressource_CP", Ressource_CP);
            jsonObject.put("Ressource_Ville", Ressource_Ville);
            jsonObject.put("Personne_Ressource", Personne_Ressource);
            jsonObject.put("Ressource_Tel", Ressource_Tel);
            jsonObject.put("Ressource_Fax", Ressource_Fax);
            jsonObject.put("Centre_Hospitalier", Centre_Hospitalier);
            jsonObject.put("Praticien", Praticien);
            jsonObject.put("Motif_Suspension_Traitement", Motif_Suspension_Traitement);
            jsonObject.put("Detail_Etat_patient", Detail_Etat_patient);
            jsonObject.put("Infirmier", Infirmier);
            jsonObject.put("Inf_Adr1", Inf_Adr1);
            jsonObject.put("Inf_Adr2", Inf_Adr2);
            jsonObject.put("Inf_CP", Inf_CP);
            jsonObject.put("Inf_Ville", Inf_Ville);
            jsonObject.put("Inf_Fax", Inf_Fax);
            jsonObject.put("Inf_Tel", Inf_Tel);
            jsonObject.put("Inf_Email", Inf_Email);
            jsonObject.put("Date_entree", Date_entree);
            jsonObject.put("Date_Debut_Traitement", Date_Debut_Traitement);
            jsonObject.put("Sous_Technique", Sous_Technique);
            jsonObject.put("Approvisionnement", Approvisionnement);
            jsonObject.put("Lieu_Traitement", Lieu_Traitement);
            jsonObject.put("ID_Lieu_Traitement", ID_Lieu_Traitement);
            jsonObject.put("Ascenceur", Ascenceur);
            jsonObject.put("Escalier", Escalier);
            jsonObject.put("Etage", Etage);
            jsonObject.put("Dgicode", Dgicode);
            jsonObject.put("Date_Etat", Date_Etat);
            jsonObject.put("Archive", Archive);
            jsonObject.put("Securite_Sociale", Securite_Sociale);
            jsonObject.put("Autre_Tel", Autre_Tel);
            jsonObject.put("Autre_Nom", Autre_Nom);
            jsonObject.put("CPAM_Nom", CPAM_Nom);
            jsonObject.put("CPAM_Adresse", CPAM_Adresse);
            jsonObject.put("CPAM_CP", CPAM_CP);
            jsonObject.put("CPAM_Ville", CPAM_Ville);
            jsonObject.put("Poids", Poids);
            jsonObject.put("INSC", INSC);
            jsonObject.put("Sexe", Sexe);
            jsonObject.put("Pharmacie", Pharmacie);
            jsonObject.put("Traitement_Modalite", Traitement_Modalite);
            jsonObject.put("Photo_lien", Photo_lien);
            jsonObject.put("Document_partage", Document_partage);
            jsonObject.put("discipline_Medicale", discipline_Medicale);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "PH_Patient{" +
                "_patientUID=" + _patientUID +
                ", Civilite='" + Civilite + '\'' +
                ", Nom_naissance='" + Nom_naissance + '\'' +
                ", Prenom='" + Prenom + '\'' +
                ", Adresse1='" + Adresse1 + '\'' +
                ", Adresse2='" + Adresse2 + '\'' +
                ", CP='" + CP + '\'' +
                ", Ville='" + Ville + '\'' +
                ", Tel1='" + Tel1 + '\'' +
                ", IPP='" + IPP + '\'' +
                ", Fax='" + Fax + '\'' +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", Adresse3='" + Adresse3 + '\'' +
                ", Tel_Professionnel='" + Tel_Professionnel + '\'' +
                ", Tel2='" + Tel2 + '\'' +
                ", Technique='" + Technique + '\'' +
                ", IPP_Fac='" + IPP_Fac + '\'' +
                ", IPP_DM='" + IPP_DM + '\'' +
                ", Nom_Marital='" + Nom_Marital + '\'' +
                ", Date_Naissance='" + Date_Naissance + '\'' +
                ", Matricule='" + Matricule + '\'' +
                ", Clef='" + Clef + '\'' +
                ", Lieu_Naissance='" + Lieu_Naissance + '\'' +
                ", Sexe_Masculin_Feminin=" + Sexe_Masculin_Feminin +
                ", Fax_Professionnel='" + Fax_Professionnel + '\'' +
                ", Profession='" + Profession + '\'' +
                ", Nom_Usuel='" + Nom_Usuel + '\'' +
                ", Email='" + Email + '\'' +
                ", Ressource_Adr1='" + Ressource_Adr1 + '\'' +
                ", Ressource_Adr2='" + Ressource_Adr2 + '\'' +
                ", Ressource_CP='" + Ressource_CP + '\'' +
                ", Ressource_Ville='" + Ressource_Ville + '\'' +
                ", Personne_Ressource='" + Personne_Ressource + '\'' +
                ", Ressource_Tel='" + Ressource_Tel + '\'' +
                ", Ressource_Fax='" + Ressource_Fax + '\'' +
                ", Centre_Hospitalier='" + Centre_Hospitalier + '\'' +
                ", Praticien='" + Praticien + '\'' +
                ", Motif_Suspension_Traitement='" + Motif_Suspension_Traitement + '\'' +
                ", Detail_Etat_patient='" + Detail_Etat_patient + '\'' +
                ", Infirmier='" + Infirmier + '\'' +
                ", Inf_Adr1='" + Inf_Adr1 + '\'' +
                ", Inf_Adr2='" + Inf_Adr2 + '\'' +
                ", Inf_CP='" + Inf_CP + '\'' +
                ", Inf_Ville='" + Inf_Ville + '\'' +
                ", Inf_Fax='" + Inf_Fax + '\'' +
                ", Inf_Tel='" + Inf_Tel + '\'' +
                ", Inf_Email='" + Inf_Email + '\'' +
                ", Date_entree='" + Date_entree + '\'' +
                ", Date_Debut_Traitement='" + Date_Debut_Traitement + '\'' +
                ", Sous_Technique='" + Sous_Technique + '\'' +
                ", Approvisionnement='" + Approvisionnement + '\'' +
                ", Lieu_Traitement='" + Lieu_Traitement + '\'' +
                ", ID_Lieu_Traitement=" + ID_Lieu_Traitement +
                ", Ascenceur=" + Ascenceur +
                ", Escalier='" + Escalier + '\'' +
                ", Etage=" + Etage +
                ", Dgicode='" + Dgicode + '\'' +
                ", Date_Etat='" + Date_Etat + '\'' +
                ", Archive=" + Archive +
                ", Securite_Sociale='" + Securite_Sociale + '\'' +
                ", Autre_Tel='" + Autre_Tel + '\'' +
                ", Autre_Nom='" + Autre_Nom + '\'' +
                ", CPAM_Nom='" + CPAM_Nom + '\'' +
                ", CPAM_Adresse='" + CPAM_Adresse + '\'' +
                ", CPAM_CP='" + CPAM_CP + '\'' +
                ", CPAM_Ville='" + CPAM_Ville + '\'' +
                ", Poids=" + Poids +
                ", INSC='" + INSC + '\'' +
                ", Sexe='" + Sexe + '\'' +
                ", Pharmacie='" + Pharmacie + '\'' +
                ", Traitement_Modalite='" + Traitement_Modalite + '\'' +
                ", Photo_lien='" + Photo_lien + '\'' +
                ", Document_partage='" + Document_partage + '\'' +
                ", discipline_Medicale='" + discipline_Medicale + '\'' +
                ", phiwms_mobileUUID=" + phiwms_mobileUUID +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        PH_Patient ph_patient = (PH_Patient) obj;

        if (this.getPhiMR4UUID() == ph_patient.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > ph_patient.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((PH_Patient) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof PH_Patient)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
