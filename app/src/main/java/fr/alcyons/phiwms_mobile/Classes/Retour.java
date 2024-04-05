package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 20/06/2017.
 */

public class Retour implements Serializable, Comparable {

    private int _UID;
    private String Numero;
    private String Ref_Depot_Origine;
    private int Code_Patient;
    private String Intitulé;
    private String Ref_Depot_Dest;
    private String Statut;
    private String Date_retour;
    private double Montant_TTC;
    private String Commentaire;
    private String Motif;
    private String Devise;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String En_Attente_de;
    private String Date_Reprise;
    private String Date_Validation;
    private String Provenance_Reference;
    private Boolean Avoir_Attendu;
    private String Nom_Chauffeur;
    private String Prenom_Chauffeur;
    private String Transporteur;
    private String Signature_Chauffeur;
    private int phiwms_mobileUUID = -1;

    public Retour(int _UID, String numero, String ref_Depot_Origine, int code_Patient, String intitulé, String ref_Depot_Dest, String statut, String date_retour, double montant_TTC, String commentaire, String motif, String devise, String SYS_DT_MAJ, String SYS_HEURE_MAJ, String SYS_USER_MAJ, String en_Attente_de, String date_Reprise, String date_Validation, String provenance_Reference, Boolean avoir_Attendu) {
        this._UID = _UID;
        this.Numero = numero;
        this.Ref_Depot_Origine = ref_Depot_Origine;
        this.Code_Patient = code_Patient;
        this.Intitulé = intitulé;
        this.Ref_Depot_Dest = ref_Depot_Dest;
        this.Statut = statut;
        this.Date_retour = date_retour;
        this.Montant_TTC = montant_TTC;
        this.Commentaire = commentaire;
        this.Motif = motif;
        this.Devise = devise;
        this.SYS_DT_MAJ = SYS_DT_MAJ;
        this.SYS_HEURE_MAJ = SYS_HEURE_MAJ;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.En_Attente_de = en_Attente_de;
        this.Date_Reprise = date_Reprise;
        this.Date_Validation = date_Validation;
        this.Provenance_Reference = provenance_Reference;
        this.Avoir_Attendu = avoir_Attendu;
    }

    public Retour(int _UID, String numero, int code_Patient, String intitulé, String ref_Depot_Dest, String statut, String date_retour, double montant_TTC, String commentaire, String ref_Depot_Origine, String motif, String en_Attente_de, String date_Reprise, String provenance_Reference) {
        this._UID = _UID;
        this.Numero = numero;
        this.Code_Patient = code_Patient;
        this.Intitulé = intitulé;
        this.Ref_Depot_Dest = ref_Depot_Dest;
        this.Statut = statut;
        this.Date_retour = date_retour;
        this.Montant_TTC = montant_TTC;
        this.Commentaire = commentaire;
        this.Ref_Depot_Origine = ref_Depot_Origine;
        this.Motif = motif;
        this.En_Attente_de = en_Attente_de;
        this.Date_Reprise = date_Reprise;
        this.Provenance_Reference = provenance_Reference;
    }

/*    public Retour(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Numero = recupererString(jsonObject.getString("Numero"));
            this.Ref_Depot_Origine = recupererString(jsonObject.getString("Ref_Depot_Origine"));
            this.Code_Patient = jsonObject.getInt("Code_Patient");
            this.Intitulé = recupererString(jsonObject.getString("Intitulé"));
            this.Ref_Depot_Dest = recupererString(jsonObject.getString("Ref_Depot_Dest"));
            this.Statut = recupererString(jsonObject.getString("Statut"));
            this.Date_retour = recupererString(jsonObject.getString("Date_retour"));
            this.Montant_TTC = jsonObject.getDouble("Montant_TTC");
            this.Commentaire = recupererString(jsonObject.getString("Commentaire"));
            this.Motif = recupererString(jsonObject.getString("Motif"));
            this.Devise = recupererString(jsonObject.getString("Devise"));
            this.SYS_DT_MAJ = recupererString(jsonObject.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = recupererString(jsonObject.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = recupererString(jsonObject.getString("SYS_USER_MAJ"));
            this.En_Attente_de = recupererString(jsonObject.getString("En_Attente_de"));
            this.Date_Reprise = recupererString(jsonObject.getString("Date_Reprise"));
            this.Date_Validation = recupererString(jsonObject.getString("Date_Validation"));
            this.Provenance_Reference = recupererString(jsonObject.getString("Provenance_Reference"));
            this.Avoir_Attendu = recupererBooleen(jsonObject, "Avoir_Attendu");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


    public Retour(JSONObject jsonObject) {
        try {
            this._UID = jsonObject.getInt("_UID");
            this.Numero = OutilsGestionClasses.recupererString(jsonObject.getString("Numero"));
            this.Ref_Depot_Origine = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot_Origine"));
            this.Intitulé = OutilsGestionClasses.recupererString(jsonObject.getString("Intitulé"));
            this.Statut = OutilsGestionClasses.recupererString(jsonObject.getString("Statut"));
            this.Ref_Depot_Dest = OutilsGestionClasses.recupererString(jsonObject.getString("Ref_Depot_Dest"));
            this.Date_retour = OutilsGestionClasses.recupererString(jsonObject.getString("Date_retour"));
            this.Commentaire = OutilsGestionClasses.recupererString(jsonObject.getString("Commentaire"));
            this.Motif = OutilsGestionClasses.recupererString(jsonObject.getString("Motif"));
            this.En_Attente_de = OutilsGestionClasses.recupererString(jsonObject.getString("En_Attente_de"));
            this.Avoir_Attendu = OutilsGestionClasses.recupererBooleen(jsonObject, "Avoir_Attendu");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*    public Retour(Cursor cursor) {
        this._UID = cursor.getInt(RetourOpenHelper.Constantes.NUM_COL__UID_RETOUR);
        this.Numero = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_NUMERO_RETOUR);
        this.Ref_Depot_Origine = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_REF_DEPOT_ORIGINE_RETOUR);
        this.Code_Patient = cursor.getInt(RetourOpenHelper.Constantes.NUM_COL_CODE_PATIENT_RETOUR);
        this.Intitulé = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_INTITULE_RETOUR);
        this.Ref_Depot_Dest = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_REF_DEPOT_DEST_RETOUR);
        this.Statut = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_STATUT_RETOUR);
        this.Date_retour = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DATE_RETOUR_RETOUR);
        this.Montant_TTC = cursor.getDouble(RetourOpenHelper.Constantes.NUM_COL_MONTANT_TTC_RETOUR);
        this.Commentaire = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_COMMENTAIRE_RETOUR);
        this.Motif = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_MOTIF_RETOUR);
        this.Devise = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DEVISE_RETOUR);
        this.SYS_DT_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_RETOUR);
        this.SYS_HEURE_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_RETOUR);
        this.SYS_USER_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_RETOUR);
        this.En_Attente_de = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_EN_ATTENTE_DE_RETOUR);
        this.Date_Reprise = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DATE_REPRISE_RETOUR);
        this.Date_Validation = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DATE_VALIDATION_RETOUR);
        this.Provenance_Reference = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_PROVENANCE_REFERENCE_RETOUR);
        this.Avoir_Attendu = recupererBooleen(cursor, RetourOpenHelper.Constantes.NUM_COL_AVOIR_ATTENDU_RETOUR);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Retour(Cursor cursor) {
        this._UID = cursor.getInt(RetourOpenHelper.Constantes.NUM_COL__UID_RETOUR);
        this.Numero = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_NUMERO_RETOUR);
        this.Ref_Depot_Origine = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_REF_DEPOT_ORIGINE_RETOUR);
        this.Intitulé = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_INTITULE_RETOUR);
        this.Statut = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_STATUT_RETOUR);
        this.Date_retour = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DATE_RETOUR_RETOUR);
        this.Commentaire = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_COMMENTAIRE_RETOUR);
        this.Motif = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_MOTIF_RETOUR);
        this.Ref_Depot_Dest = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_REF_DEPOT_DEST_RETOUR);
        this.SYS_DT_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_RETOUR);
        this.SYS_HEURE_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_RETOUR);
        this.SYS_USER_MAJ = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_RETOUR);
        this.En_Attente_de = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_EN_ATTENTE_DE_RETOUR);
        this.Date_Reprise = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_DATE_REPRISE_RETOUR);
        this.Provenance_Reference = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_PROVENANCE_REFERENCE_RETOUR);
        this.Avoir_Attendu = OutilsGestionClasses.recupererBooleen(cursor, RetourOpenHelper.Constantes.NUM_COL_AVOIR_ATTENDU_RETOUR);
        this.Nom_Chauffeur = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_NOM_CHAUFFEUR_RETOUR);
        this.Prenom_Chauffeur = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_PRENOM_CHAUFFEUR_RETOUR);
        this.Transporteur = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_TRANSPORTEUR_RETOUR);
        this.Signature_Chauffeur = cursor.getString(RetourOpenHelper.Constantes.NUM_COL_SIGNATURE_CHAUFFEUR);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }


    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getRef_Depot_Origine() {
        return Ref_Depot_Origine;
    }

    public void setRef_Depot_Origine(String ref_Depot_Origine) {
        Ref_Depot_Origine = ref_Depot_Origine;
    }

    public int getCode_Patient() {
        return Code_Patient;
    }

    public void setCode_Patient(int code_Patient) {
        Code_Patient = code_Patient;
    }

    public String getIntitulé() {
        return Intitulé;
    }

    public void setIntitulé(String intitulé) {
        Intitulé = intitulé;
    }

    public String getRef_Depot_Dest() {
        return Ref_Depot_Dest;
    }

    public void setRef_Depot_Dest(String ref_Depot_Dest) {
        Ref_Depot_Dest = ref_Depot_Dest;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public String getDate_retour() {
        return Date_retour;
    }

    public void setDate_retour(String date_retour) {
        Date_retour = date_retour;
    }

    public double getMontant_TTC() {
        return Montant_TTC;
    }

    public void setMontant_TTC(double montant_TTC) {
        Montant_TTC = montant_TTC;
    }

    public String getCommentaire() {
        return Commentaire;
    }

    public void setCommentaire(String commentaire) {
        Commentaire = commentaire;
    }

    public String getMotif() {
        return Motif;
    }

    public void setMotif(String motif) {
        Motif = motif;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
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

    public String getEn_Attente_de() {
        return En_Attente_de;
    }

    public void setEn_Attente_de(String en_Attente_de) {
        En_Attente_de = en_Attente_de;
    }

    public String getDate_Reprise() {
        return Date_Reprise;
    }

    public void setDate_Reprise(String date_Reprise) {
        Date_Reprise = date_Reprise;
    }

    public String getDate_Validation() {
        return Date_Validation;
    }

    public void setDate_Validation(String date_Validation) {
        Date_Validation = date_Validation;
    }

    public String getProvenance_Reference() {
        return Provenance_Reference;
    }

    public void setProvenance_Reference(String provenance_Reference) {
        Provenance_Reference = provenance_Reference;
    }

    public Boolean getAvoir_Attendu() {
        return Avoir_Attendu;
    }

    public void setAvoir_Attendu(Boolean avoir_Attendu) {
        Avoir_Attendu = avoir_Attendu;
    }

    public String getNom_Chauffeur() {
        return Nom_Chauffeur;
    }

    public void setNom_Chauffeur(String nom_Chauffeur) {
        Nom_Chauffeur = nom_Chauffeur;
    }

    public String getPrenom_Chauffeur() {
        return Prenom_Chauffeur;
    }

    public void setPrenom_Chauffeur(String prenom_Chauffeur) {
        Prenom_Chauffeur = prenom_Chauffeur;
    }

    public String getTransporteur() {
        return Transporteur;
    }

    public void setTransporteur(String transporteur) {
        Transporteur = transporteur;
    }

    public String getSignature_Chauffeur() {
        return Signature_Chauffeur;
    }

    public void setSignature_Chauffeur(String signature_Chauffeur) {
        Signature_Chauffeur = signature_Chauffeur;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_UID", this.get_UID());
            jsonObject.put("Numero", this.getNumero());
            jsonObject.put("Ref_Depot_Origine", this.getRef_Depot_Origine());
            jsonObject.put("Code_Patient", this.getCode_Patient());
            jsonObject.put("Intitulé", this.getIntitulé());
            jsonObject.put("Ref_Depot_Dest", this.getRef_Depot_Dest());
            jsonObject.put("Statut", this.getStatut());
            jsonObject.put("Date_retour", this.getDate_retour());
            jsonObject.put("Montant_TTC", this.getMontant_TTC());
            jsonObject.put("Commentaire", this.getCommentaire());
            jsonObject.put("Motif", this.getMotif());
            jsonObject.put("Devise", this.getDevise());
            jsonObject.put("SYS_DT_MAJ", this.getSYS_DT_MAJ());
            jsonObject.put("SYS_HEURE_MAJ", this.getSYS_HEURE_MAJ());
            jsonObject.put("SYS_USER_MAJ", this.getSYS_USER_MAJ());
            jsonObject.put("En_Attente_de", this.getEn_Attente_de());
            jsonObject.put("Date_Reprise", this.getDate_Reprise());
            jsonObject.put("Date_Validation", this.getDate_Validation());
            jsonObject.put("Provenance_Reference", this.getProvenance_Reference());
            jsonObject.put("Avoir_Attendu", this.getAvoir_Attendu());
            jsonObject.put("Nom_Chauffeur", this.getNom_Chauffeur());
            jsonObject.put("Prenom_Chauffeur", this.getPrenom_Chauffeur());
            jsonObject.put("Transporteur", this.getTransporteur());
            jsonObject.put("Signature_Chauffeur", this.getSignature_Chauffeur());
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = null;
        }
        return jsonObject;
    }


    public String getChaineFiltreQuarantaine(SQLiteDatabase db) {
        String filter = this.toString() + " ";

        List<Retour_Ligne> listeRetourLigne = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, this);
        for (int i = 0; i < listeRetourLigne.size(); i++) {
            Retour_Ligne retourLigneCourant = listeRetourLigne.get(i);
            filter += retourLigneCourant.getProduit_Designation();
        }

        return filter;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Retour)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Retour retour = (Retour) obj;

        if (this.getphiwms_mobileUUID() == retour.getphiwms_mobileUUID()) {
            return 0;
        } else {
            return this.get_UID() > retour.get_UID() ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return this.getNumero();
    }
}