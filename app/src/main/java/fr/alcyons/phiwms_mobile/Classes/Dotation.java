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

/**
 * Created by jessica on 02/10/2017.
 */

public class Dotation implements Serializable, Comparable {

    private int _UID;
    private String Intitulé;
    private String Ref_Depot;
    private String Début;
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

    public Dotation(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Intitulé = OutilsGestionClasses.recupererString(jsonObject.getString("Intitulé"));
            this.Ref_Depot = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot"));
            this.Début = OutilsGestionClasses.recupererString(jsonObject.getString("Début"));
            this.Fin = OutilsGestionClasses.recupererString(jsonObject.getString("Fin"));
            this.Interrompu = OutilsGestionClasses.recupererBooleen(jsonObject, "Interrompu");
            this.NB_Semaine = jsonObject.getInt("NB_Semaine");
            this.Valorisation_TTC = jsonObject.getInt("Valorisation_TTC");
            this.Dotation_Std = OutilsGestionClasses.recupererString(jsonObject.getString("Dotation_Std"));
            this.Commentaire = OutilsGestionClasses.recupererString(jsonObject.getString("Commentaire"));
            this.depot_UID = jsonObject.getInt("depot_UID");
            this.nb_patients = jsonObject.getInt("nb_patients");
            this.Tournee_Reference = OutilsGestionClasses.recupererString(jsonObject.getString("Tournee_Reference"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.tech_UID = jsonObject.getInt("tech_UID");
            this.URGENCE = OutilsGestionClasses.recupererBooleen(jsonObject, "URGENCE");
            this.SECURISE = OutilsGestionClasses.recupererBooleen(jsonObject, "SECURISE");
            this.TauxStockIdeal = jsonObject.getInt("TauxStockIdeal");
            this.INSTALLATION = OutilsGestionClasses.recupererBooleen(jsonObject, "INSTALLATION");
            this.PLEINVIDE = OutilsGestionClasses.recupererBooleen(jsonObject, "PLEINVIDE");
            this.protocole_UID = jsonObject.getInt("protocole_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Dotation(Cursor cursor) {
        this._UID = cursor.getInt(DotationOpenHelper.Constantes.NUM_COL__UID_DOTATION);
        this.Intitulé = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_INTITULE_DOTATION);
        this.Ref_Depot = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_REF_DEPOT_DOTATION);
        this.Début = cursor.getString(DotationOpenHelper.Constantes.NUM_COL_DEBUT_DOTATION);
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
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getIntitulé() {
        return Intitulé;
    }

    public void setIntitulé(String intitulé) {
        Intitulé = intitulé;
    }

    public String getRef_Depot() {
        return Ref_Depot;
    }

    public void setRef_Depot(String ref_Depot) {
        Ref_Depot = ref_Depot;
    }

    public String getDébut() {
        return Début;
    }

    public void setDébut(String début) {
        Début = début;
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

    public int getphiwms_mobileUUID() {
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
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", _UID);
            jsonObject.put("Intitulé", Intitulé);
            jsonObject.put("Ref_Depot", Ref_Depot);
            jsonObject.put("Début", Début);
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
                ", Intitulé='" + Intitulé + '\'' +
                ", Ref_Depot='" + Ref_Depot + '\'' +
                ", Début='" + Début + '\'' +
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
                '}';
    }

    @Override
    public int compareTo(@NonNull Object obj) {
        Dotation dotation = (Dotation) obj;

        if (this.getphiwms_mobileUUID() == dotation.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.getphiwms_mobileUUID() > dotation.getphiwms_mobileUUID() ? 1 : -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Dotation) obj).getphiwms_mobileUUID() == this.getphiwms_mobileUUID()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Dotation)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }
}
