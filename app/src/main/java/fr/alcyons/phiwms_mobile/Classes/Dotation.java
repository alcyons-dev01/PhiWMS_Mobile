package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Dotation implements Serializable, Comparable {

    private int _UID;
    private String Intitule;
    private String Ref_Depot;
    private String Debut;
    private String Fin;
    private boolean Interrompu;
    private int NB_Semaine;
    private int Valorisation_TTC;
    private String Dotation_Std;
    private String Commentaire;
    private int depot_UID;
    private int nb_patients;
    private String Tournee_Reference;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private int tech_UID;
    private boolean URGENCE;
    private boolean SECURISE;
    private int TauxStockIdeal;
    private boolean INSTALLATION;
    private boolean PLEINVIDE;
    private int protocole_UID;
    private int phiwms_mobileUUID = -1;

    private String dateLivraison;

    private boolean commandeAB;

    public Dotation(JSONObject jsonObject) {
        this._UID = jsonObject.optInt("_UID");
        this.Intitule = jsonObject.optString("Intitulé");
        this.Ref_Depot = jsonObject.optString("Ref_Depot");
        this.Debut = jsonObject.optString("Début");
        this.Fin = jsonObject.optString("Fin");
        this.Interrompu = recupererBooleen(jsonObject,"Interrompu");
        this.NB_Semaine = jsonObject.optInt("NB_Semaine");
        this.Valorisation_TTC = jsonObject.optInt("Valorisation_TTC");
        this.Dotation_Std = jsonObject.optString("Dotation_Std");
        this.Commentaire = jsonObject.optString("Commentaire");
        this.depot_UID = jsonObject.optInt("depot_UID");
        this.nb_patients = jsonObject.optInt("nb_patients");
        this.Tournee_Reference = jsonObject.optString("Tournee_Reference");
        this.SYS_DT_MAJ = jsonObject.optString("SYS_DT_MAJ");
        this.SYS_HEURE_MAJ = jsonObject.optString("SYS_HEURE_MAJ");
        this.SYS_USER_MAJ = jsonObject.optString("SYS_USER_MAJ");
        this.tech_UID = jsonObject.optInt("tech_UID");
        this.URGENCE = recupererBooleen(jsonObject,"URGENCE");
        this.SECURISE = recupererBooleen(jsonObject,"SECURISE");
        this.TauxStockIdeal = jsonObject.optInt("TauxStockIdeal");
        this.INSTALLATION = recupererBooleen(jsonObject,"INSTALLATION");
        this.PLEINVIDE = recupererBooleen(jsonObject, "PLEINVIDE");
        this.commandeAB = recupererBooleen(jsonObject, "Commande_AB");
        this.protocole_UID = jsonObject.optInt("protocole_UID");
    }

    public Dotation(Cursor cursor) {
        this._UID = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL__UID_DOTATION);
        this.Intitule = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_INTITULE_DOTATION);
        this.Ref_Depot = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_REF_DEPOT_DOTATION);
        this.Debut = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_DEBUT_DOTATION);
        this.Fin = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_FIN_DOTATION);
        this.Interrompu = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_INTERROMPU_DOTATION);
        this.NB_Semaine = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_NB_SEMAINE_DOTATION);
        this.Valorisation_TTC = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_VALORISATION_TTC_DOTATION);
        this.Dotation_Std = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_DOTATION_STD_DOTATION);
        this.Commentaire = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_COMMENTAIRE_DOTATION);
        this.depot_UID = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_DEPOT_UID_DOTATION);
        this.nb_patients = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_NB_PATIENTS_DOTATION);
        this.Tournee_Reference = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_TOURNEE_REFERENCE_DOTATION);
        this.SYS_DT_MAJ = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_DOTATION);
        this.SYS_HEURE_MAJ = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_DOTATION);
        this.SYS_USER_MAJ = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_DOTATION);
        this.tech_UID = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_TECH_UID_DOTATION);
        this.URGENCE = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_URGENCE_DOTATION);
        this.SECURISE = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_SECURISE_DOTATION);
        this.TauxStockIdeal = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_TAUXSTOCKIDEAL_DOTATION);
        this.INSTALLATION = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_INSTALLATION_DOTATION);
        this.PLEINVIDE = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_PLEINVIDE_DOTATION);
        this.protocole_UID = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL_PROTOCOLE_UID_DOTATION);
        this.commandeAB = OutilsGestionClasses.recupererBooleen(cursor, DotationOpenHelper.Constantes.NUM_COL_COMMANDEAB_DOTATION);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getIntitule() {
        return Intitule;
    }

    public void setIntitule(String intitule) {
        Intitule = intitule;
    }

    public String getRef_Depot() {
        return Ref_Depot;
    }

    public void setRef_Depot(String ref_Depot) {
        Ref_Depot = ref_Depot;
    }

    public String getDebut() {
        return Debut;
    }

    public void setDebut(String debut) {
        Debut = debut;
    }

    public String getFin() {
        return Fin;
    }

    public void setFin(String fin) {
        Fin = fin;
    }

    public boolean isInterrompu() {
        return Interrompu;
    }

    public void setInterrompu(boolean interrompu) {
        Interrompu = interrompu;
    }

    public int getNB_Semaine() {
        return NB_Semaine;
    }

    public void setNB_Semaine(int NB_Semaine) {
        this.NB_Semaine = NB_Semaine;
    }

    public int getValorisation_TTC() {
        return Valorisation_TTC;
    }

    public void setValorisation_TTC(int valorisation_TTC) {
        Valorisation_TTC = valorisation_TTC;
    }

    public String getDotation_Std() {
        return Dotation_Std;
    }

    public void setDotation_Std(String dotation_Std) {
        Dotation_Std = dotation_Std;
    }

    public String getCommentaire() {
        return Commentaire;
    }

    public void setCommentaire(String commentaire) {
        Commentaire = commentaire;
    }

    public int getDepot_UID() {
        return depot_UID;
    }

    public void setDepot_UID(int depot_UID) {
        this.depot_UID = depot_UID;
    }

    public int getNb_patients() {
        return nb_patients;
    }

    public void setNb_patients(int nb_patients) {
        this.nb_patients = nb_patients;
    }

    public String getTournee_Reference() {
        return Tournee_Reference;
    }

    public void setTournee_Reference(String tournee_Reference) {
        Tournee_Reference = tournee_Reference;
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

    public int getTech_UID() {
        return tech_UID;
    }

    public void setTech_UID(int tech_UID) {
        this.tech_UID = tech_UID;
    }

    public boolean isURGENCE() {
        return URGENCE;
    }

    public void setURGENCE(boolean URGENCE) {
        this.URGENCE = URGENCE;
    }

    public boolean isSECURISE() {
        return SECURISE;
    }

    public void setSECURISE(boolean SECURISE) {
        this.SECURISE = SECURISE;
    }

    public int getTauxStockIdeal() {
        return TauxStockIdeal;
    }

    public void setTauxStockIdeal(int tauxStockIdeal) {
        TauxStockIdeal = tauxStockIdeal;
    }

    public boolean isINSTALLATION() {
        return INSTALLATION;
    }

    public void setINSTALLATION(boolean INSTALLATION) {
        this.INSTALLATION = INSTALLATION;
    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public boolean isPLEINVIDE() {
        return PLEINVIDE;
    }

    public void setPLEINVIDE(boolean PLEINVIDE) {
        this.PLEINVIDE = PLEINVIDE;
    }

    public int getProtocole_UID() {
        return protocole_UID;
    }

    public void setProtocole_UID(int protocole_UID) {
        this.protocole_UID = protocole_UID;
    }

    public void setDateLivraison(String dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getDateLivraison() {
        return dateLivraison;
    }

    public boolean isCommandeAB() {
        return commandeAB;
    }

    public void setCommandeAB(boolean commandeAB) {
        this.commandeAB = commandeAB;
    }
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("Intitule", Intitule);
            jsonObject.put("Ref_Depot", Ref_Depot);
            jsonObject.put("Debut", Debut);
            jsonObject.put("Fin", Fin);
            jsonObject.put("Interrompu", Interrompu);
            jsonObject.put("NB_Semaine", NB_Semaine);
            jsonObject.put("Valorisation_TTC", Valorisation_TTC);
            jsonObject.put("Dotation_Std", Dotation_Std);
            jsonObject.put("Commentaire", Commentaire);
            jsonObject.put("depot_UID", depot_UID);
            jsonObject.put("nb_patients", nb_patients);
            jsonObject.put("Tournee_Reference", Tournee_Reference);
            jsonObject.put("SYS_DT_MAJ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ", SYS_HEURE_MAJ);
            jsonObject.put("SYS_USER_MAJ", SYS_USER_MAJ);
            jsonObject.put("tech_UID", tech_UID);
            jsonObject.put("URGENCE", URGENCE);
            jsonObject.put("SECURISE", SECURISE);
            jsonObject.put("TauxStockIdeal", TauxStockIdeal);
            jsonObject.put("INSTALLATION", INSTALLATION);
            jsonObject.put("PLEINVIDE", PLEINVIDE);
            jsonObject.put("protocole_UID", protocole_UID);
            jsonObject.put("commandeAB", commandeAB);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "Dotation{" +
                "_UID=" + _UID +
                ", Intitule='" + Intitule + '\'' +
                ", Ref_Depot='" + Ref_Depot + '\'' +
                ", Debut='" + Debut + '\'' +
                ", Fin='" + Fin + '\'' +
                ", Interrompu=" + Interrompu +
                ", NB_Semaine=" + NB_Semaine +
                ", Valorisation_TTC=" + Valorisation_TTC +
                ", Dotation_Std='" + Dotation_Std + '\'' +
                ", Commentaire='" + Commentaire + '\'' +
                ", depot_UID=" + depot_UID +
                ", nb_patients=" + nb_patients +
                ", Tournee_Reference='" + Tournee_Reference + '\'' +
                ", SYS_DT_MAJ='" + SYS_DT_MAJ + '\'' +
                ", SYS_HEURE_MAJ='" + SYS_HEURE_MAJ + '\'' +
                ", SYS_USER_MAJ='" + SYS_USER_MAJ + '\'' +
                ", tech_UID=" + tech_UID +
                ", URGENCE=" + URGENCE +
                ", SECURISE=" + SECURISE +
                ", TauxStockIdeal=" + TauxStockIdeal +
                ", INSTALLATION=" + INSTALLATION +
                ", PLEINVIDE=" + PLEINVIDE +
                ", protocole_UID=" + protocole_UID +
                ", phiwms_mobileUUID=" + phiwms_mobileUUID +
                ", commandeAB=" + commandeAB +
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Dotation dotation = (Dotation) obj;

        if (this.getPhiMR4UUID() == dotation.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.getPhiMR4UUID() > dotation.getPhiMR4UUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Dotation) obj).getPhiMR4UUID() == this.getPhiMR4UUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Dotation)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
