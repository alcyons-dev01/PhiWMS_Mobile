package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Parametres_SerialisationOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by olivier on 26/02/2019.
 */

public class Parametres_Serialisation {

    private int ID;
    private String serveurAPI_host;
    private String serveurLDAP_host;
    private boolean communicationDiffere;
    private boolean dispenserReception;
    private boolean dispenserDelivrance;
    private boolean stockParNumeroDeSerie;
    private String serveurLDAP_port;
    private String serveurLDAP_login;
    private String serveurLDAP_password;
    private String serveurLDAP_nomDomaine;
    private String dossierVision;
    private String franceMVO_identifiant;
    private String franceMVO_mdp;
    private String franceMVO_tan;
    private boolean franceMVO_termesEtConditions;
    private boolean moduleVision;
    private int phiMR4UUID=-1;

    public Parametres_Serialisation(JSONObject jsonObject){
        try{
            ID=jsonObject.getInt("ID");
            serveurAPI_host= OutilsGestionClasses.recupererString(jsonObject.getString("serveurAPI_host"));
            serveurLDAP_host= OutilsGestionClasses.recupererString(jsonObject.getString("serveurLDAP_host"));
            communicationDiffere= OutilsGestionClasses.recupererBooleen(jsonObject,"communicationDiffere");
            dispenserReception= OutilsGestionClasses.recupererBooleen(jsonObject,"dispenserReception");
            dispenserDelivrance= OutilsGestionClasses.recupererBooleen(jsonObject,"dispenserDelivrance");
            stockParNumeroDeSerie= OutilsGestionClasses.recupererBooleen(jsonObject,"stockParNumeroDeSerie");
            serveurLDAP_port= OutilsGestionClasses.recupererString(jsonObject.getString("serveurLDAP_port"));
            serveurLDAP_login= OutilsGestionClasses.recupererString(jsonObject.getString("serveurLDAP_login"));
            serveurLDAP_password= OutilsGestionClasses.recupererString(jsonObject.getString("serveurLDAP_password"));
            serveurLDAP_nomDomaine= OutilsGestionClasses.recupererString(jsonObject.getString("serveurLDAP_nomDomaine"));
            dossierVision= OutilsGestionClasses.recupererString(jsonObject.getString("dossierVision"));
            franceMVO_identifiant= OutilsGestionClasses.recupererString(jsonObject.getString("franceMVO_identifiant"));
            franceMVO_mdp= OutilsGestionClasses.recupererString(jsonObject.getString("franceMVO_mdp"));
            franceMVO_tan= OutilsGestionClasses.recupererString(jsonObject.getString("franceMVO_tan"));
            franceMVO_termesEtConditions= OutilsGestionClasses.recupererBooleen(jsonObject,"franceMVO_termesEtConditions");
            moduleVision= OutilsGestionClasses.recupererBooleen(jsonObject,"moduleVision");
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public Parametres_Serialisation(Cursor cursor){
        ID=cursor.getInt(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_ID_PARAMETRES_SERIALISATION);
        serveurAPI_host=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURAPI_HOST_PARAMETRES_SERIALISATION);
        serveurLDAP_host=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURLDAP_HOST_PARAMETRES_SERIALISATION);
        communicationDiffere= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_COMMUNICATIONDIFFERE_PARAMETRES_SERIALISATION);
        dispenserReception= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_DISPENSERRECEPTION_PARAMETRES_SERIALISATION);
        dispenserDelivrance= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_DISPENSERDELIVRANCE_PARAMETRES_SERIALISATION);
        stockParNumeroDeSerie= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_STOCKPARNUMERODESERIE_PARAMETRES_SERIALISATION);
        serveurLDAP_port=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURLDAP_PORT_PARAMETRES_SERIALISATION);
        serveurLDAP_login=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURLDAP_LOGIN_PARAMETRES_SERIALISATION);
        serveurLDAP_password=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURLDAP_PASSWORD_PARAMETRES_SERIALISATION);
        serveurLDAP_nomDomaine=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_SERVEURLDAP_NOMDOMAINE_PARAMETRES_SERIALISATION);
        dossierVision=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_DOSSIERVISION_PARAMETRES_SERIALISATION);
        franceMVO_identifiant=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_FRANCEMVO_IDENTIFIANT_PARAMETRES_SERIALISATION);
        franceMVO_mdp=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_FRANCEMVO_MDP_PARAMETRES_SERIALISATION);
        franceMVO_tan=cursor.getString(Parametres_SerialisationOpenHelper.Constantes.NUM_COL_FRANCEMVO_TAN_PARAMETRES_SERIALISATION);
        franceMVO_termesEtConditions= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_FRANCEMVO_TERMESETCONDITIONS_PARAMETRES_SERIALISATION);
        moduleVision= OutilsGestionClasses.recupererBooleen(cursor, Parametres_SerialisationOpenHelper.Constantes.NUM_COL_MODULEVISION_PARAMETRES_SERIALISATION);
    }

    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("ID", ID);
            jsonObject.put("serveurAPI_host", serveurAPI_host);
            jsonObject.put("serveurLDAP_host", serveurLDAP_host);
            jsonObject.put("communicationDiffere", communicationDiffere);
            jsonObject.put("dispenserReception", dispenserReception);
            jsonObject.put("dispenserDelivrance", dispenserDelivrance);
            jsonObject.put("stockParNumeroDeSerie", stockParNumeroDeSerie);
            jsonObject.put("serveurLDAP_port", serveurLDAP_port);
            jsonObject.put("serveurLDAP_login", serveurLDAP_login);
            jsonObject.put("serveurLDAP_password", serveurLDAP_password);
            jsonObject.put("serveurLDAP_nomDomaine", serveurLDAP_nomDomaine);
            jsonObject.put("dossierVision", dossierVision);
            jsonObject.put("franceMVO_identifiant", franceMVO_identifiant);
            jsonObject.put("franceMVO_mdp", franceMVO_mdp);
            jsonObject.put("franceMVO_tan", franceMVO_tan);
            jsonObject.put("franceMVO_termesEtConditions", franceMVO_termesEtConditions);
            jsonObject.put("moduleVision", moduleVision);
        }catch(JSONException e){
            e.printStackTrace();jsonObject=null;
        }return jsonObject;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getServeurAPI_host() {
        return serveurAPI_host;
    }

    public void setServeurAPI_host(String serveurAPI_host) {
        this.serveurAPI_host = serveurAPI_host;
    }

    public String getServeurLDAP_host() {
        return serveurLDAP_host;
    }

    public void setServeurLDAP_host(String serveurLDAP_host) {
        this.serveurLDAP_host = serveurLDAP_host;
    }

    public boolean isCommunicationDiffere() {
        return communicationDiffere;
    }

    public void setCommunicationDiffere(boolean communicationDiffere) {
        this.communicationDiffere = communicationDiffere;
    }

    public boolean isDispenserReception() {
        return dispenserReception;
    }

    public void setDispenserReception(boolean dispenserReception) {
        this.dispenserReception = dispenserReception;
    }

    public boolean isDispenserDelivrance() {
        return dispenserDelivrance;
    }

    public void setDispenserDelivrance(boolean dispenserDelivrance) {
        this.dispenserDelivrance = dispenserDelivrance;
    }

    public boolean isStockParNumeroDeSerie() {
        return stockParNumeroDeSerie;
    }

    public void setStockParNumeroDeSerie(boolean stockParNumeroDeSerie) {
        this.stockParNumeroDeSerie = stockParNumeroDeSerie;
    }

    public String getServeurLDAP_port() {
        return serveurLDAP_port;
    }

    public void setServeurLDAP_port(String serveurLDAP_port) {
        this.serveurLDAP_port = serveurLDAP_port;
    }

    public String getServeurLDAP_login() {
        return serveurLDAP_login;
    }

    public void setServeurLDAP_login(String serveurLDAP_login) {
        this.serveurLDAP_login = serveurLDAP_login;
    }

    public String getServeurLDAP_password() {
        return serveurLDAP_password;
    }

    public void setServeurLDAP_password(String serveurLDAP_password) {
        this.serveurLDAP_password = serveurLDAP_password;
    }

    public String getServeurLDAP_nomDomaine() {
        return serveurLDAP_nomDomaine;
    }

    public void setServeurLDAP_nomDomaine(String serveurLDAP_nomDomaine) {
        this.serveurLDAP_nomDomaine = serveurLDAP_nomDomaine;
    }

    public String getDossierVision() {
        return dossierVision;
    }

    public void setDossierVision(String dossierVision) {
        this.dossierVision = dossierVision;
    }

    public String getFranceMVO_identifiant() {
        return franceMVO_identifiant;
    }

    public void setFranceMVO_identifiant(String franceMVO_identifiant) {
        this.franceMVO_identifiant = franceMVO_identifiant;
    }

    public String getFranceMVO_mdp() {
        return franceMVO_mdp;
    }

    public void setFranceMVO_mdp(String franceMVO_mdp) {
        this.franceMVO_mdp = franceMVO_mdp;
    }

    public String getFranceMVO_tan() {
        return franceMVO_tan;
    }

    public void setFranceMVO_tan(String franceMVO_tan) {
        this.franceMVO_tan = franceMVO_tan;
    }

    public boolean isFranceMVO_termesEtConditions() {
        return franceMVO_termesEtConditions;
    }

    public void setFranceMVO_termesEtConditions(boolean franceMVO_termesEtConditions) {
        this.franceMVO_termesEtConditions = franceMVO_termesEtConditions;
    }

    public boolean isModuleVision() {
        return moduleVision;
    }

    public void setModuleVision(boolean moduleVision) {
        this.moduleVision = moduleVision;
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }
}
