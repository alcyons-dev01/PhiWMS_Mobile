package fr.alcyons.phimr4.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phimr4.BaseDeDonnees.SYS_User_RulesOpenHelper;

import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererBooleen;
import static fr.alcyons.phimr4.Outils.OutilsGestionClasses.recupererString;

/**
 * Created by olivier on 25/04/2019.
 */

public class SYS_User_Rules {

    private String Nom;
    private String Profil;
    private String Initiale;
    private String X_MotPasse;
    private int User_UID;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Droits;
    private String Acces_Classification;
    private boolean Avaliser_Commande_Autoriser;
    private boolean Administrateur;
    private boolean Quarantaine_Autoriser;
    private String Perimetre_ParDefaut;
    private int planHabilitation;
    private boolean Modification_DPP_Autoriser;
    private boolean Regulation_Demande_Particuliere;
    private boolean Anonymiser;
    private String Bureau_ParDefaut;
    private String LocalisationParDefaut;
    private boolean Regulation_Automatique;
    private boolean Acces_Serialisation;
    private String Serialisation_identifiant;
    private String Serialisation_mdp;
    private String Serialisation_tan;
    private String Serialisation_clientLoginId;
    private boolean SerialisationTermsEtConditions;
    private int phiMR4UUID=-1;
    
    public SYS_User_Rules(JSONObject jsonObject){
        try{
            Nom=recupererString(jsonObject.getString("Nom"));
            Profil=recupererString(jsonObject.getString("Profil"));
            Initiale=recupererString(jsonObject.getString("Initiale"));
            X_MotPasse=recupererString(jsonObject.getString("X_MotPasse"));
            User_UID=jsonObject.getInt("User_UID");
            SYS_DT_MAJ=recupererString(jsonObject.getString("SYS_DT_MAJ"));
            SYS_HEURE_MAJ=recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            SYS_USER_MAJ=recupererString(jsonObject.getString("SYS_USER_MAJ"));
            Droits=recupererString(jsonObject.getString("Droits"));
            Acces_Classification=recupererString(jsonObject.getString("Acces_Classification"));
            Avaliser_Commande_Autoriser=recupererBooleen(jsonObject,"Avaliser_Commande_Autoriser");
            Administrateur=recupererBooleen(jsonObject,"Administrateur");
            Quarantaine_Autoriser=recupererBooleen(jsonObject,"Quarantaine_Autoriser");
            Perimetre_ParDefaut=recupererString(jsonObject.getString("Perimetre_ParDefaut"));
            planHabilitation=jsonObject.getInt("planHabilitation");
            Modification_DPP_Autoriser=recupererBooleen(jsonObject,"Modification_DPP_Autoriser");
            Regulation_Demande_Particuliere=recupererBooleen(jsonObject,"Regulation_Demande_Particuliere");
            Anonymiser=recupererBooleen(jsonObject,"Anonymiser");
            Bureau_ParDefaut=recupererString(jsonObject.getString("Bureau_ParDefaut"));
            LocalisationParDefaut=recupererString(jsonObject.getString("LocalisationParDefaut"));
            Regulation_Automatique=recupererBooleen(jsonObject,"Regulation_Automatique");
            Acces_Serialisation=recupererBooleen(jsonObject,"Acces_Serialisation");
            Serialisation_identifiant=recupererString(jsonObject.getString("Serialisation_identifiant"));
            Serialisation_mdp=recupererString(jsonObject.getString("Serialisation_mdp"));
            Serialisation_tan=recupererString(jsonObject.getString("Serialisation_tan"));
            Serialisation_clientLoginId=recupererString(jsonObject.getString("Serialisation_clientLoginId"));
            SerialisationTermsEtConditions=recupererBooleen(jsonObject,"SerialisationTermsEtConditions");
        }catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public SYS_User_Rules(Cursor cursor){
        Nom=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_NOM_SYS_USER_RULES);
        Profil=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_PROFIL_SYS_USER_RULES);
        Initiale=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_INITIALE_SYS_USER_RULES);
        X_MotPasse=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_X_MOTPASSE_SYS_USER_RULES);
        User_UID=cursor.getInt(SYS_User_RulesOpenHelper.Constantes.NUM_COL_USER_UID_SYS_USER_RULES);
        SYS_DT_MAJ=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_SYS_USER_RULES);
        SYS_HEURE_MAJ=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_SYS_USER_RULES);
        SYS_USER_MAJ=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_SYS_USER_RULES);
        Droits=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_DROITS_SYS_USER_RULES);
        Acces_Classification=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_ACCES_CLASSIFICATION_SYS_USER_RULES);
        Avaliser_Commande_Autoriser=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_AVALISER_COMMANDE_AUTORISER_SYS_USER_RULES);
        Administrateur=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_ADMINISTRATEUR_SYS_USER_RULES);
        Quarantaine_Autoriser=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_QUARANTAINE_AUTORISER_SYS_USER_RULES);
        Perimetre_ParDefaut=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_PERIMETRE_PARDEFAUT_SYS_USER_RULES);
        planHabilitation=cursor.getInt(SYS_User_RulesOpenHelper.Constantes.NUM_COL_PLANHABILITATION_SYS_USER_RULES);
        Modification_DPP_Autoriser=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_MODIFICATION_DPP_AUTORISER_SYS_USER_RULES);
        Regulation_Demande_Particuliere=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_REGULATION_DEMANDE_PARTICULIERE_SYS_USER_RULES);
        Anonymiser=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_ANONYMISER_SYS_USER_RULES);
        Bureau_ParDefaut=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_BUREAU_PARDEFAUT_SYS_USER_RULES);
        LocalisationParDefaut=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_LOCALISATIONPARDEFAUT_SYS_USER_RULES);
        Regulation_Automatique=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_REGULATION_AUTOMATIQUE_SYS_USER_RULES);
        Acces_Serialisation=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_ACCES_SERIALISATION_SYS_USER_RULES);
        Serialisation_identifiant=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SERIALISATION_IDENTIFIANT_SYS_USER_RULES);
        Serialisation_mdp=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SERIALISATION_MDP_SYS_USER_RULES);
        Serialisation_tan=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SERIALISATION_TAN_SYS_USER_RULES);
        Serialisation_clientLoginId=cursor.getString(SYS_User_RulesOpenHelper.Constantes.NUM_COL_SERIALISATION_CLIENTLOGINID_SYS_USER_RULES);
        SerialisationTermsEtConditions=recupererBooleen(cursor, SYS_User_RulesOpenHelper.Constantes.NUM_COL_SERIALISATIONTERMSETCONDITIONS_SYS_USER_RULES);
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getProfil() {
        return Profil;
    }

    public void setProfil(String profil) {
        Profil = profil;
    }

    public String getInitiale() {
        return Initiale;
    }

    public void setInitiale(String initiale) {
        Initiale = initiale;
    }

    public String getX_MotPasse() {
        return X_MotPasse;
    }

    public void setX_MotPasse(String x_MotPasse) {
        X_MotPasse = x_MotPasse;
    }

    public int getUser_UID() {
        return User_UID;
    }

    public void setUser_UID(int user_UID) {
        User_UID = user_UID;
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

    public String getDroits() {
        return Droits;
    }

    public void setDroits(String droits) {
        Droits = droits;
    }

    public String getAcces_Classification() {
        return Acces_Classification;
    }

    public void setAcces_Classification(String acces_Classification) {
        Acces_Classification = acces_Classification;
    }

    public boolean isAvaliser_Commande_Autoriser() {
        return Avaliser_Commande_Autoriser;
    }

    public void setAvaliser_Commande_Autoriser(boolean avaliser_Commande_Autoriser) {
        Avaliser_Commande_Autoriser = avaliser_Commande_Autoriser;
    }

    public boolean isAdministrateur() {
        return Administrateur;
    }

    public void setAdministrateur(boolean administrateur) {
        Administrateur = administrateur;
    }

    public boolean isQuarantaine_Autoriser() {
        return Quarantaine_Autoriser;
    }

    public void setQuarantaine_Autoriser(boolean quarantaine_Autoriser) {
        Quarantaine_Autoriser = quarantaine_Autoriser;
    }

    public String getPerimetre_ParDefaut() {
        return Perimetre_ParDefaut;
    }

    public void setPerimetre_ParDefaut(String perimetre_ParDefaut) {
        Perimetre_ParDefaut = perimetre_ParDefaut;
    }

    public int getPlanHabilitation() {
        return planHabilitation;
    }

    public void setPlanHabilitation(int planHabilitation) {
        this.planHabilitation = planHabilitation;
    }

    public boolean isModification_DPP_Autoriser() {
        return Modification_DPP_Autoriser;
    }

    public void setModification_DPP_Autoriser(boolean modification_DPP_Autoriser) {
        Modification_DPP_Autoriser = modification_DPP_Autoriser;
    }

    public boolean isRegulation_Demande_Particuliere() {
        return Regulation_Demande_Particuliere;
    }

    public void setRegulation_Demande_Particuliere(boolean regulation_Demande_Particuliere) {
        Regulation_Demande_Particuliere = regulation_Demande_Particuliere;
    }

    public boolean isAnonymiser() {
        return Anonymiser;
    }

    public void setAnonymiser(boolean anonymiser) {
        Anonymiser = anonymiser;
    }

    public String getBureau_ParDefaut() {
        return Bureau_ParDefaut;
    }

    public void setBureau_ParDefaut(String bureau_ParDefaut) {
        Bureau_ParDefaut = bureau_ParDefaut;
    }

    public String getLocalisationParDefaut() {
        return LocalisationParDefaut;
    }

    public void setLocalisationParDefaut(String localisationParDefaut) {
        LocalisationParDefaut = localisationParDefaut;
    }

    public boolean isRegulation_Automatique() {
        return Regulation_Automatique;
    }

    public void setRegulation_Automatique(boolean regulation_Automatique) {
        Regulation_Automatique = regulation_Automatique;
    }

    public boolean isAcces_Serialisation() {
        return Acces_Serialisation;
    }

    public void setAcces_Serialisation(boolean acces_Serialisation) {
        Acces_Serialisation = acces_Serialisation;
    }

    public String getSerialisation_identifiant() {
        return Serialisation_identifiant;
    }

    public void setSerialisation_identifiant(String serialisation_identifiant) {
        Serialisation_identifiant = serialisation_identifiant;
    }

    public String getSerialisation_mdp() {
        return Serialisation_mdp;
    }

    public void setSerialisation_mdp(String serialisation_mdp) {
        Serialisation_mdp = serialisation_mdp;
    }

    public String getSerialisation_tan() {
        return Serialisation_tan;
    }

    public void setSerialisation_tan(String serialisation_tan) {
        Serialisation_tan = serialisation_tan;
    }

    public String getSerialisation_clientLoginId() {
        return Serialisation_clientLoginId;
    }

    public void setSerialisation_clientLoginId(String serialisation_clientLoginId) {
        Serialisation_clientLoginId = serialisation_clientLoginId;
    }

    public boolean isSerialisationTermsEtConditions() {
        return SerialisationTermsEtConditions;
    }

    public void setSerialisationTermsEtConditions(boolean serialisationTermsEtConditions) {
        SerialisationTermsEtConditions = serialisationTermsEtConditions;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("Nom", Nom);
            jsonObject.put("Profil", Profil);
            jsonObject.put("Initiale", Initiale);
            jsonObject.put("X_MotPasse", X_MotPasse);
            jsonObject.put("User_UID", User_UID);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("Droits", Droits);
            jsonObject.put("Acces_Classification", Acces_Classification);
            jsonObject.put("Avaliser_Commande_Autoriser", Avaliser_Commande_Autoriser);
            jsonObject.put("Administrateur", Administrateur);
            jsonObject.put("Quarantaine_Autoriser", Quarantaine_Autoriser);
            jsonObject.put("Perimetre_ParDefaut", Perimetre_ParDefaut);
            jsonObject.put("planHabilitation", planHabilitation);
            jsonObject.put("Modification_DPP_Autoriser", Modification_DPP_Autoriser);
            jsonObject.put("Regulation_Demande_Particuliere", Regulation_Demande_Particuliere);
            jsonObject.put("Anonymiser", Anonymiser);
            jsonObject.put("Bureau_ParDefaut", Bureau_ParDefaut);
            jsonObject.put("LocalisationParDefaut", LocalisationParDefaut);
            jsonObject.put("Regulation_Automatique", Regulation_Automatique);
            jsonObject.put("Acces_Serialisation", Acces_Serialisation);
            jsonObject.put("Serialisation_identifiant", Serialisation_identifiant);
            jsonObject.put("Serialisation_mdp", Serialisation_mdp);
            jsonObject.put("Serialisation_tan", Serialisation_tan);
            jsonObject.put("Serialisation_clientLoginId", Serialisation_clientLoginId);
            jsonObject.put("SerialisationTermsEtConditions", SerialisationTermsEtConditions);
        }
        catch(JSONException e){
            e.printStackTrace();
            jsonObject=null;
        }
        return jsonObject;
    }
}
