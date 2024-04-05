package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Prescription_patientOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by olivier on 12/03/2018.
 */

public class Prescription_patient {

    int protocolePatient_UID;
    int Code_Produit;
    String Designation;
    int Cond;
    int Quantite;
    String Rf_Frs;
    String Categorie;
    boolean Livraison_Directe;
    String Date_Début;
    String Date_Fin;
    boolean L;
    boolean Ma;
    boolean Me;
    boolean J;
    boolean V;
    boolean S;
    boolean D;
    int PU_HT;
    int PU_TTC;
    int Montant_TTC_Ligne;
    int Montant_HT_Ligne;
    String Prescripteur_Abrev;
    String Prescripteur_Nom;
    int Nb_Semaine;
    String Voie;
    int _UID;
    int Frequence_Hebdomadaire;
    String document_partage;
    String SYS_USER_MAJ;
    String SYS_DT_MAJ;
    String SYS_HEURE_MAJ;
    private int phiMR4UUID = -1;


    public Prescription_patient(JSONObject jsonObject) {
        try {
            this.protocolePatient_UID = jsonObject.getInt("protocolePatient_UID");
            this.Code_Produit = jsonObject.getInt("Code_Produit");
            this.Designation = OutilsGestionClasses.recupererString(jsonObject.getString("Designation"));
            this.Cond = jsonObject.getInt("Cond");
            this.Quantite = jsonObject.getInt("Quantite");
            this.Rf_Frs = OutilsGestionClasses.recupererString(jsonObject.getString("Rf_Frs"));
            this.Categorie = OutilsGestionClasses.recupererString(jsonObject.getString("Categorie"));
            this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(jsonObject, "Livraison_Directe");
            this.Date_Début = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Début"));
            this.Date_Fin = OutilsGestionClasses.recupererString(jsonObject.getString("Date_Fin"));
            this.L = OutilsGestionClasses.recupererBooleen(jsonObject, "L");
            this.Ma = OutilsGestionClasses.recupererBooleen(jsonObject, "Ma");
            this.Me = OutilsGestionClasses.recupererBooleen(jsonObject, "Me");
            this.J = OutilsGestionClasses.recupererBooleen(jsonObject, "J");
            this.V = OutilsGestionClasses.recupererBooleen(jsonObject, "V");
            this.S = OutilsGestionClasses.recupererBooleen(jsonObject, "S");
            this.D = OutilsGestionClasses.recupererBooleen(jsonObject, "D");
            this.PU_HT = jsonObject.getInt("PU_HT");
            this.PU_TTC = jsonObject.getInt("PU_TTC");
            this.Montant_TTC_Ligne = jsonObject.getInt("Montant_TTC_Ligne");
            this.Montant_HT_Ligne = jsonObject.getInt("Montant_HT_Ligne");
            this.Prescripteur_Abrev = OutilsGestionClasses.recupererString(jsonObject.getString("Prescripteur_Abrev"));
            this.Prescripteur_Nom = OutilsGestionClasses.recupererString(jsonObject.getString("Prescripteur_Nom"));
            this.Nb_Semaine = jsonObject.getInt("Nb_Semaine");
            this.Voie = OutilsGestionClasses.recupererString(jsonObject.getString("Voie"));
            this._UID = jsonObject.getInt("_UID");
            this.Frequence_Hebdomadaire = jsonObject.getInt("Frequence_Hebdomadaire");
            this.document_partage = OutilsGestionClasses.recupererString(jsonObject.getString("document_partage"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Prescription_patient(Cursor cursor) {
        this.protocolePatient_UID = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_PROTOCOLEPATIENT_UID_PRESCRIPTION_PATIENT);
        this.Code_Produit = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_CODE_PRODUIT_PRESCRIPTION_PATIENT);
        this.Designation = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_DESIGNATION_PRESCRIPTION_PATIENT);
        this.Cond = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_COND_PRESCRIPTION_PATIENT);
        this.Quantite = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_QUANTITE_PRESCRIPTION_PATIENT);
        this.Rf_Frs = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_RF_FRS_PRESCRIPTION_PATIENT);
        this.Categorie = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_CATEGORIE_PRESCRIPTION_PATIENT);
        this.Livraison_Directe = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_LIVRAISON_DIRECTE_PRESCRIPTION_PATIENT);
        this.Date_Début = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_DATE_DEBUT_PRESCRIPTION_PATIENT);
        this.Date_Fin = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_DATE_FIN_PRESCRIPTION_PATIENT);
        this.L = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_L_PRESCRIPTION_PATIENT);
        this.Ma = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_MA_PRESCRIPTION_PATIENT);
        this.Me = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_ME_PRESCRIPTION_PATIENT);
        this.J = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_J_PRESCRIPTION_PATIENT);
        this.V = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_V_PRESCRIPTION_PATIENT);
        this.S = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_S_PRESCRIPTION_PATIENT);
        this.D = OutilsGestionClasses.recupererBooleen(cursor, Prescription_patientOpenHelper.Constantes.NUM_COL_D_PRESCRIPTION_PATIENT);
        this.PU_HT = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_PU_HT_PRESCRIPTION_PATIENT);
        this.PU_TTC = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_PU_TTC_PRESCRIPTION_PATIENT);
        this.Montant_TTC_Ligne = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_MONTANT_TTC_LIGNE_PRESCRIPTION_PATIENT);
        this.Montant_HT_Ligne = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_MONTANT_HT_LIGNE_PRESCRIPTION_PATIENT);
        this.Prescripteur_Abrev = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_PRESCRIPTEUR_ABREV_PRESCRIPTION_PATIENT);
        this.Prescripteur_Nom = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_PRESCRIPTEUR_NOM_PRESCRIPTION_PATIENT);
        this.Nb_Semaine = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_NB_SEMAINE_PRESCRIPTION_PATIENT);
        this.Voie = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_VOIE_PRESCRIPTION_PATIENT);
        this._UID = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL__UID_PRESCRIPTION_PATIENT);
        this.Frequence_Hebdomadaire = cursor.getInt(Prescription_patientOpenHelper.Constantes.NUM_COL_FREQUENCE_HEBDOMADAIRE_PRESCRIPTION_PATIENT);
        this.document_partage = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_DOCUMENT_PARTAGE_PRESCRIPTION_PATIENT);
        this.SYS_USER_MAJ = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PRESCRIPTION_PATIENT);
        this.SYS_DT_MAJ = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PRESCRIPTION_PATIENT);
        this.SYS_HEURE_MAJ = cursor.getString(Prescription_patientOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PRESCRIPTION_PATIENT);
    }

    public int getProtocolePatient_UID() {
        return protocolePatient_UID;
    }

    public void setProtocolePatient_UID(int protocolePatient_UID) {
        this.protocolePatient_UID = protocolePatient_UID;
    }

    public int getCode_Produit() {
        return Code_Produit;
    }

    public void setCode_Produit(int code_Produit) {
        Code_Produit = code_Produit;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }

    public int getCond() {
        return Cond;
    }

    public void setCond(int cond) {
        Cond = cond;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int quantite) {
        Quantite = quantite;
    }

    public String getRf_Frs() {
        return Rf_Frs;
    }

    public void setRf_Frs(String rf_Frs) {
        Rf_Frs = rf_Frs;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public boolean isLivraison_Directe() {
        return Livraison_Directe;
    }

    public void setLivraison_Directe(boolean livraison_Directe) {
        Livraison_Directe = livraison_Directe;
    }

    public String getDate_Début() {
        return Date_Début;
    }

    public void setDate_Début(String date_Début) {
        Date_Début = date_Début;
    }

    public String getDate_Fin() {
        return Date_Fin;
    }

    public void setDate_Fin(String date_Fin) {
        Date_Fin = date_Fin;
    }

    public boolean isL() {
        return L;
    }

    public void setL(boolean l) {
        L = l;
    }

    public boolean isMa() {
        return Ma;
    }

    public void setMa(boolean ma) {
        Ma = ma;
    }

    public boolean isMe() {
        return Me;
    }

    public void setMe(boolean me) {
        Me = me;
    }

    public boolean isJ() {
        return J;
    }

    public void setJ(boolean j) {
        J = j;
    }

    public boolean isV() {
        return V;
    }

    public void setV(boolean v) {
        V = v;
    }

    public boolean isS() {
        return S;
    }

    public void setS(boolean s) {
        S = s;
    }

    public boolean isD() {
        return D;
    }

    public void setD(boolean d) {
        D = d;
    }

    public int getPU_HT() {
        return PU_HT;
    }

    public void setPU_HT(int PU_HT) {
        this.PU_HT = PU_HT;
    }

    public int getPU_TTC() {
        return PU_TTC;
    }

    public void setPU_TTC(int PU_TTC) {
        this.PU_TTC = PU_TTC;
    }

    public int getMontant_TTC_Ligne() {
        return Montant_TTC_Ligne;
    }

    public void setMontant_TTC_Ligne(int montant_TTC_Ligne) {
        Montant_TTC_Ligne = montant_TTC_Ligne;
    }

    public int getMontant_HT_Ligne() {
        return Montant_HT_Ligne;
    }

    public void setMontant_HT_Ligne(int montant_HT_Ligne) {
        Montant_HT_Ligne = montant_HT_Ligne;
    }

    public String getPrescripteur_Abrev() {
        return Prescripteur_Abrev;
    }

    public void setPrescripteur_Abrev(String prescripteur_Abrev) {
        Prescripteur_Abrev = prescripteur_Abrev;
    }

    public String getPrescripteur_Nom() {
        return Prescripteur_Nom;
    }

    public void setPrescripteur_Nom(String prescripteur_Nom) {
        Prescripteur_Nom = prescripteur_Nom;
    }

    public int getNb_Semaine() {
        return Nb_Semaine;
    }

    public void setNb_Semaine(int nb_Semaine) {
        Nb_Semaine = nb_Semaine;
    }

    public String getVoie() {
        return Voie;
    }

    public void setVoie(String voie) {
        Voie = voie;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public int getFrequence_Hebdomadaire() {
        return Frequence_Hebdomadaire;
    }

    public void setFrequence_Hebdomadaire(int frequence_Hebdomadaire) {
        Frequence_Hebdomadaire = frequence_Hebdomadaire;
    }

    public String getDocument_partage() {
        return document_partage;
    }

    public void setDocument_partage(String document_partage) {
        this.document_partage = document_partage;
    }

    public String getSYS_USER_MAJ() {
        return SYS_USER_MAJ;
    }

    public void setSYS_USER_MAJ(String SYS_USER_MAJ) {
        this.SYS_USER_MAJ = SYS_USER_MAJ;
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

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("protocolePatient_UID ", protocolePatient_UID);
            jsonObject.put("Code_Produit ", Code_Produit);
            jsonObject.put("Designation ", Designation);
            jsonObject.put("Cond ", Cond);
            jsonObject.put("Quantite ", Quantite);
            jsonObject.put("Rf_Frs ", Rf_Frs);
            jsonObject.put("Categorie ", Categorie);
            jsonObject.put("Livraison_Directe ", Livraison_Directe);
            jsonObject.put("Date_Début ", Date_Début);
            jsonObject.put("Date_Fin ", Date_Fin);
            jsonObject.put("L ", L);
            jsonObject.put("Ma ", Ma);
            jsonObject.put("Me ", Me);
            jsonObject.put("J ", J);
            jsonObject.put("V ", V);
            jsonObject.put("S ", S);
            jsonObject.put("D ", D);
            jsonObject.put("PU_HT ", PU_HT);
            jsonObject.put("PU_TTC ", PU_TTC);
            jsonObject.put("Montant_TTC_Ligne ", Montant_TTC_Ligne);
            jsonObject.put("Montant_HT_Ligne ", Montant_HT_Ligne);
            jsonObject.put("Prescripteur_Abrev ", Prescripteur_Abrev);
            jsonObject.put("Prescripteur_Nom ", Prescripteur_Nom);
            jsonObject.put("Nb_Semaine ", Nb_Semaine);
            jsonObject.put("Voie ", Voie);
            jsonObject.put("_UID ", _UID);
            jsonObject.put("Frequence_Hebdomadaire ", Frequence_Hebdomadaire);
            jsonObject.put("document_partage ", document_partage);
            jsonObject.put("SYS_USER_MAJ ", SYS_USER_MAJ);
            jsonObject.put("SYS_DT_MAJ ", SYS_DT_MAJ);
            jsonObject.put("SYS_HEURE_MAJ ", SYS_HEURE_MAJ);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }
}
