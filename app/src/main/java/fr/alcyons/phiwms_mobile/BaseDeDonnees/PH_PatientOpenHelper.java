package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phiwms_mobile.Classes.PH_Patient;
public class PH_PatientOpenHelper extends DBOpenHelper {

    public PH_PatientOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void viderTablePH_Patient(SQLiteDatabase db) {
        db.delete(Constantes.TABLE_PH_PATIENT, null, null);
    }

    public static long insererPH_PatientEnBDD(SQLiteDatabase db, PH_Patient objet) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL__PATIENTUID_PH_PATIENT, objet.get_patientUID());
        contentValues.put(Constantes.CLE_COL_CIVILITE_PH_PATIENT, objet.getCivilite());
        contentValues.put(Constantes.CLE_COL_NOM_NAISSANCE_PH_PATIENT, objet.getNom_naissance());
        contentValues.put(Constantes.CLE_COL_PRENOM_PH_PATIENT, objet.getPrenom());
        contentValues.put(Constantes.CLE_COL_ADRESSE1_PH_PATIENT, objet.getAdresse1());
        contentValues.put(Constantes.CLE_COL_ADRESSE2_PH_PATIENT, objet.getAdresse2());
        contentValues.put(Constantes.CLE_COL_CP_PH_PATIENT, objet.getCP());
        contentValues.put(Constantes.CLE_COL_VILLE_PH_PATIENT, objet.getVille());
        contentValues.put(Constantes.CLE_COL_TEL1_PH_PATIENT, objet.getTel1());
        contentValues.put(Constantes.CLE_COL_IPP_PH_PATIENT, objet.getIPP());
        contentValues.put(Constantes.CLE_COL_FAX_PH_PATIENT, objet.getFax());
        contentValues.put(Constantes.CLE_COL_SYS_DT_MAJ_PH_PATIENT, objet.getSYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PATIENT, objet.getSYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL_SYS_USER_MAJ_PH_PATIENT, objet.getSYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_ADRESSE3_PH_PATIENT, objet.getAdresse3());
        contentValues.put(Constantes.CLE_COL_TEL_PROFESSIONNEL_PH_PATIENT, objet.getTel_Professionnel());
        contentValues.put(Constantes.CLE_COL_TEL2_PH_PATIENT, objet.getTel2());
        contentValues.put(Constantes.CLE_COL_TECHNIQUE_PH_PATIENT, objet.getTechnique());
        contentValues.put(Constantes.CLE_COL_IPP_FAC_PH_PATIENT, objet.getIPP_Fac());
        contentValues.put(Constantes.CLE_COL_IPP_DM_PH_PATIENT, objet.getIPP_DM());
        contentValues.put(Constantes.CLE_COL_NOM_MARITAL_PH_PATIENT, objet.getNom_Marital());
        contentValues.put(Constantes.CLE_COL_DATE_NAISSANCE_PH_PATIENT, objet.getDate_Naissance());
        contentValues.put(Constantes.CLE_COL_MATRICULE_PH_PATIENT, objet.getMatricule());
        contentValues.put(Constantes.CLE_COL_CLEF_PH_PATIENT, objet.getClef());
        contentValues.put(Constantes.CLE_COL_LIEU_NAISSANCE_PH_PATIENT, objet.getLieu_Naissance());
        contentValues.put(Constantes.CLE_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT, objet.isSexe_Masculin_Feminin());
        contentValues.put(Constantes.CLE_COL_FAX_PROFESSIONNEL_PH_PATIENT, objet.getFax_Professionnel());
        contentValues.put(Constantes.CLE_COL_PROFESSION_PH_PATIENT, objet.getProfession());
        contentValues.put(Constantes.CLE_COL_NOM_USUEL_PH_PATIENT, objet.getNom_Usuel());
        contentValues.put(Constantes.CLE_COL_EMAIL_PH_PATIENT, objet.getEmail());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_ADR1_PH_PATIENT, objet.getRessource_Adr1());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_ADR2_PH_PATIENT, objet.getRessource_Adr2());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_CP_PH_PATIENT, objet.getRessource_CP());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_VILLE_PH_PATIENT, objet.getRessource_Ville());
        contentValues.put(Constantes.CLE_COL_PERSONNE_RESSOURCE_PH_PATIENT, objet.getPersonne_Ressource());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_TEL_PH_PATIENT, objet.getRessource_Tel());
        contentValues.put(Constantes.CLE_COL_RESSOURCE_FAX_PH_PATIENT, objet.getRessource_Fax());
        contentValues.put(Constantes.CLE_COL_CENTRE_HOSPITALIER_PH_PATIENT, objet.getCentre_Hospitalier());
        contentValues.put(Constantes.CLE_COL_PRATICIEN_PH_PATIENT, objet.getPraticien());
        contentValues.put(Constantes.CLE_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT, objet.getMotif_Suspension_Traitement());
        contentValues.put(Constantes.CLE_COL_DETAIL_ETAT_PATIENT_PH_PATIENT, objet.getDetail_Etat_patient());
        contentValues.put(Constantes.CLE_COL_INFIRMIER_PH_PATIENT, objet.getInfirmier());
        contentValues.put(Constantes.CLE_COL_INF_ADR1_PH_PATIENT, objet.getInf_Adr1());
        contentValues.put(Constantes.CLE_COL_INF_ADR2_PH_PATIENT, objet.getInf_Adr2());
        contentValues.put(Constantes.CLE_COL_INF_CP_PH_PATIENT, objet.getInf_CP());
        contentValues.put(Constantes.CLE_COL_INF_VILLE_PH_PATIENT, objet.getInf_Ville());
        contentValues.put(Constantes.CLE_COL_INF_FAX_PH_PATIENT, objet.getInf_Fax());
        contentValues.put(Constantes.CLE_COL_INF_TEL_PH_PATIENT, objet.getInf_Tel());
        contentValues.put(Constantes.CLE_COL_INF_EMAIL_PH_PATIENT, objet.getInf_Email());
        contentValues.put(Constantes.CLE_COL_DATE_ENTREE_PH_PATIENT, objet.getDate_entree());
        contentValues.put(Constantes.CLE_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT, objet.getDate_Debut_Traitement());
        contentValues.put(Constantes.CLE_COL_SOUS_TECHNIQUE_PH_PATIENT, objet.getSous_Technique());
        contentValues.put(Constantes.CLE_COL_APPROVISIONNEMENT_PH_PATIENT, objet.getApprovisionnement());
        contentValues.put(Constantes.CLE_COL_LIEU_TRAITEMENT_PH_PATIENT, objet.getLieu_Traitement());
        contentValues.put(Constantes.CLE_COL_ID_LIEU_TRAITEMENT_PH_PATIENT, objet.getID_Lieu_Traitement());
        contentValues.put(Constantes.CLE_COL_ASCENCEUR_PH_PATIENT, objet.isAscenceur());
        contentValues.put(Constantes.CLE_COL_ESCALIER_PH_PATIENT, objet.getEscalier());
        contentValues.put(Constantes.CLE_COL_ETAGE_PH_PATIENT, objet.getEtage());
        contentValues.put(Constantes.CLE_COL_DGICODE_PH_PATIENT, objet.getDgicode());
        contentValues.put(Constantes.CLE_COL_DATE_ETAT_PH_PATIENT, objet.getDate_Etat());
        contentValues.put(Constantes.CLE_COL_ARCHIVE_PH_PATIENT, objet.isArchive());
        contentValues.put(Constantes.CLE_COL_SECURITE_SOCIALE_PH_PATIENT, objet.getSecurite_Sociale());
        contentValues.put(Constantes.CLE_COL_AUTRE_TEL_PH_PATIENT, objet.getAutre_Tel());
        contentValues.put(Constantes.CLE_COL_AUTRE_NOM_PH_PATIENT, objet.getAutre_Nom());
        contentValues.put(Constantes.CLE_COL_CPAM_NOM_PH_PATIENT, objet.getCPAM_Nom());
        contentValues.put(Constantes.CLE_COL_CPAM_ADRESSE_PH_PATIENT, objet.getCPAM_Adresse());
        contentValues.put(Constantes.CLE_COL_CPAM_CP_PH_PATIENT, objet.getCPAM_CP());
        contentValues.put(Constantes.CLE_COL_CPAM_VILLE_PH_PATIENT, objet.getCPAM_Ville());
        contentValues.put(Constantes.CLE_COL_POIDS_PH_PATIENT, objet.getPoids());
        contentValues.put(Constantes.CLE_COL_INSC_PH_PATIENT, objet.getINSC());
        contentValues.put(Constantes.CLE_COL_SEXE_PH_PATIENT, objet.getSexe());
        contentValues.put(Constantes.CLE_COL_PHARMACIE_PH_PATIENT, objet.getPharmacie());
        contentValues.put(Constantes.CLE_COL_TRAITEMENT_MODALITE_PH_PATIENT, objet.getTraitement_Modalite());
        contentValues.put(Constantes.CLE_COL_PHOTO_LIEN_PH_PATIENT, objet.getPhoto_lien());
        contentValues.put(Constantes.CLE_COL_DOCUMENT_PARTAGE_PH_PATIENT, objet.getDocument_partage());
        contentValues.put(Constantes.CLE_COL_DISCIPLINE_MEDICALE_PH_PATIENT, objet.getDiscipline_Medicale());
        long rowID = db.insert(Constantes.TABLE_PH_PATIENT, null, contentValues);
        objet.setphiwms_mobileUUID((int) rowID);
        return rowID;
    }

    public static PH_Patient getPH_PatientByphiwms_mobileUUID(SQLiteDatabase db, int id) {
        PH_Patient phPatient = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_PATIENT + " WHERE " + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=? ",
                new String[]{String.valueOf(id)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phPatient = new PH_Patient(cursor);
        }
        cursor.close();
        cursor = null;
        return phPatient;
    }

    public static PH_Patient getPH_PAtientByIPP(SQLiteDatabase db, String ipp) {
        PH_Patient phPatient = null;
        Cursor cursor = db.rawQuery(" SELECT * FROM " + Constantes.TABLE_PH_PATIENT + " WHERE " + Constantes.CLE_COL_IPP_PH_PATIENT + "=? ",
                new String[]{String.valueOf(ipp)});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            phPatient = new PH_Patient(cursor);
        }
        cursor.close();
        cursor = null;
        return phPatient;
    }

    public static class Constantes implements BaseColumns {
        public static final String TABLE_PH_PATIENT = "PH_Patient";
        public static final String CLE_COL__PATIENTUID_PH_PATIENT = "_patientUID";
        public static final int NUM_COL__PATIENTUID_PH_PATIENT = 1;
        public static final String TYPE_COL__PATIENTUID_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_CIVILITE_PH_PATIENT = "Civilité";
        public static final int NUM_COL_CIVILITE_PH_PATIENT = 2;
        public static final String TYPE_COL_CIVILITE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_NOM_NAISSANCE_PH_PATIENT = "Nom_naissance";
        public static final int NUM_COL_NOM_NAISSANCE_PH_PATIENT = 3;
        public static final String TYPE_COL_NOM_NAISSANCE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PRENOM_PH_PATIENT = "Prénom";
        public static final int NUM_COL_PRENOM_PH_PATIENT = 4;
        public static final String TYPE_COL_PRENOM_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ADRESSE1_PH_PATIENT = "Adresse1";
        public static final int NUM_COL_ADRESSE1_PH_PATIENT = 5;
        public static final String TYPE_COL_ADRESSE1_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ADRESSE2_PH_PATIENT = "Adresse2";
        public static final int NUM_COL_ADRESSE2_PH_PATIENT = 6;
        public static final String TYPE_COL_ADRESSE2_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CP_PH_PATIENT = "CP";
        public static final int NUM_COL_CP_PH_PATIENT = 7;
        public static final String TYPE_COL_CP_PH_PATIENT = "TEXT";
        public static final String CLE_COL_VILLE_PH_PATIENT = "Ville";
        public static final int NUM_COL_VILLE_PH_PATIENT = 8;
        public static final String TYPE_COL_VILLE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_TEL1_PH_PATIENT = "Tel1";
        public static final int NUM_COL_TEL1_PH_PATIENT = 9;
        public static final String TYPE_COL_TEL1_PH_PATIENT = "TEXT";
        public static final String CLE_COL_IPP_PH_PATIENT = "IPP";
        public static final int NUM_COL_IPP_PH_PATIENT = 10;
        public static final String TYPE_COL_IPP_PH_PATIENT = "TEXT";
        public static final String CLE_COL_FAX_PH_PATIENT = "Fax";
        public static final int NUM_COL_FAX_PH_PATIENT = 11;
        public static final String TYPE_COL_FAX_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_DT_MAJ_PH_PATIENT = "SYS_DT_MAJ";
        public static final int NUM_COL_SYS_DT_MAJ_PH_PATIENT = 12;
        public static final String TYPE_COL_SYS_DT_MAJ_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_HEURE_MAJ_PH_PATIENT = "SYS_HEURE_MAJ";
        public static final int NUM_COL_SYS_HEURE_MAJ_PH_PATIENT = 13;
        public static final String TYPE_COL_SYS_HEURE_MAJ_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SYS_USER_MAJ_PH_PATIENT = "SYS_USER_MAJ";
        public static final int NUM_COL_SYS_USER_MAJ_PH_PATIENT = 14;
        public static final String TYPE_COL_SYS_USER_MAJ_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ADRESSE3_PH_PATIENT = "Adresse3";
        public static final int NUM_COL_ADRESSE3_PH_PATIENT = 15;
        public static final String TYPE_COL_ADRESSE3_PH_PATIENT = "TEXT";
        public static final String CLE_COL_TEL_PROFESSIONNEL_PH_PATIENT = "Tel_Professionnel";
        public static final int NUM_COL_TEL_PROFESSIONNEL_PH_PATIENT = 16;
        public static final String TYPE_COL_TEL_PROFESSIONNEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_TEL2_PH_PATIENT = "Tel2";
        public static final int NUM_COL_TEL2_PH_PATIENT = 17;
        public static final String TYPE_COL_TEL2_PH_PATIENT = "TEXT";
        public static final String CLE_COL_TECHNIQUE_PH_PATIENT = "Technique";
        public static final int NUM_COL_TECHNIQUE_PH_PATIENT = 18;
        public static final String TYPE_COL_TECHNIQUE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_IPP_FAC_PH_PATIENT = "IPP_Fac";
        public static final int NUM_COL_IPP_FAC_PH_PATIENT = 19;
        public static final String TYPE_COL_IPP_FAC_PH_PATIENT = "TEXT";
        public static final String CLE_COL_IPP_DM_PH_PATIENT = "IPP_DM";
        public static final int NUM_COL_IPP_DM_PH_PATIENT = 20;
        public static final String TYPE_COL_IPP_DM_PH_PATIENT = "TEXT";
        public static final String CLE_COL_NOM_MARITAL_PH_PATIENT = "Nom_Marital";
        public static final int NUM_COL_NOM_MARITAL_PH_PATIENT = 21;
        public static final String TYPE_COL_NOM_MARITAL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DATE_NAISSANCE_PH_PATIENT = "Date_Naissance";
        public static final int NUM_COL_DATE_NAISSANCE_PH_PATIENT = 22;
        public static final String TYPE_COL_DATE_NAISSANCE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_MATRICULE_PH_PATIENT = "Matricule";
        public static final int NUM_COL_MATRICULE_PH_PATIENT = 23;
        public static final String TYPE_COL_MATRICULE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CLEF_PH_PATIENT = "Clef";
        public static final int NUM_COL_CLEF_PH_PATIENT = 24;
        public static final String TYPE_COL_CLEF_PH_PATIENT = "TEXT";
        public static final String CLE_COL_LIEU_NAISSANCE_PH_PATIENT = "Lieu_Naissance";
        public static final int NUM_COL_LIEU_NAISSANCE_PH_PATIENT = 25;
        public static final String TYPE_COL_LIEU_NAISSANCE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT = "Sexe_Masculin_Feminin";
        public static final int NUM_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT = 26;
        public static final String TYPE_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_FAX_PROFESSIONNEL_PH_PATIENT = "Fax_Professionnel";
        public static final int NUM_COL_FAX_PROFESSIONNEL_PH_PATIENT = 27;
        public static final String TYPE_COL_FAX_PROFESSIONNEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PROFESSION_PH_PATIENT = "Profession";
        public static final int NUM_COL_PROFESSION_PH_PATIENT = 28;
        public static final String TYPE_COL_PROFESSION_PH_PATIENT = "TEXT";
        public static final String CLE_COL_NOM_USUEL_PH_PATIENT = "Nom_Usuel";
        public static final int NUM_COL_NOM_USUEL_PH_PATIENT = 29;
        public static final String TYPE_COL_NOM_USUEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_EMAIL_PH_PATIENT = "Email";
        public static final int NUM_COL_EMAIL_PH_PATIENT = 30;
        public static final String TYPE_COL_EMAIL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_ADR1_PH_PATIENT = "Ressource_Adr1";
        public static final int NUM_COL_RESSOURCE_ADR1_PH_PATIENT = 31;
        public static final String TYPE_COL_RESSOURCE_ADR1_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_ADR2_PH_PATIENT = "Ressource_Adr2";
        public static final int NUM_COL_RESSOURCE_ADR2_PH_PATIENT = 32;
        public static final String TYPE_COL_RESSOURCE_ADR2_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_CP_PH_PATIENT = "Ressource_CP";
        public static final int NUM_COL_RESSOURCE_CP_PH_PATIENT = 33;
        public static final String TYPE_COL_RESSOURCE_CP_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_VILLE_PH_PATIENT = "Ressource_Ville";
        public static final int NUM_COL_RESSOURCE_VILLE_PH_PATIENT = 34;
        public static final String TYPE_COL_RESSOURCE_VILLE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PERSONNE_RESSOURCE_PH_PATIENT = "Personne_Ressource";
        public static final int NUM_COL_PERSONNE_RESSOURCE_PH_PATIENT = 35;
        public static final String TYPE_COL_PERSONNE_RESSOURCE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_TEL_PH_PATIENT = "Ressource_Tél";
        public static final int NUM_COL_RESSOURCE_TEL_PH_PATIENT = 36;
        public static final String TYPE_COL_RESSOURCE_TEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_RESSOURCE_FAX_PH_PATIENT = "Ressource_Fax";
        public static final int NUM_COL_RESSOURCE_FAX_PH_PATIENT = 37;
        public static final String TYPE_COL_RESSOURCE_FAX_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CENTRE_HOSPITALIER_PH_PATIENT = "Centre_Hospitalier";
        public static final int NUM_COL_CENTRE_HOSPITALIER_PH_PATIENT = 38;
        public static final String TYPE_COL_CENTRE_HOSPITALIER_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PRATICIEN_PH_PATIENT = "Praticien";
        public static final int NUM_COL_PRATICIEN_PH_PATIENT = 39;
        public static final String TYPE_COL_PRATICIEN_PH_PATIENT = "TEXT";
        public static final String CLE_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT = "Motif_Suspension_Traitement";
        public static final int NUM_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT = 40;
        public static final String TYPE_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DETAIL_ETAT_PATIENT_PH_PATIENT = "Detail_Etat_patient";
        public static final int NUM_COL_DETAIL_ETAT_PATIENT_PH_PATIENT = 41;
        public static final String TYPE_COL_DETAIL_ETAT_PATIENT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INFIRMIER_PH_PATIENT = "Infirmier";
        public static final int NUM_COL_INFIRMIER_PH_PATIENT = 42;
        public static final String TYPE_COL_INFIRMIER_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_ADR1_PH_PATIENT = "Inf_Adr1";
        public static final int NUM_COL_INF_ADR1_PH_PATIENT = 43;
        public static final String TYPE_COL_INF_ADR1_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_ADR2_PH_PATIENT = "Inf_Adr2";
        public static final int NUM_COL_INF_ADR2_PH_PATIENT = 44;
        public static final String TYPE_COL_INF_ADR2_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_CP_PH_PATIENT = "Inf_CP";
        public static final int NUM_COL_INF_CP_PH_PATIENT = 45;
        public static final String TYPE_COL_INF_CP_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_VILLE_PH_PATIENT = "Inf_Ville";
        public static final int NUM_COL_INF_VILLE_PH_PATIENT = 46;
        public static final String TYPE_COL_INF_VILLE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_FAX_PH_PATIENT = "Inf_Fax";
        public static final int NUM_COL_INF_FAX_PH_PATIENT = 47;
        public static final String TYPE_COL_INF_FAX_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_TEL_PH_PATIENT = "Inf_Tél";
        public static final int NUM_COL_INF_TEL_PH_PATIENT = 48;
        public static final String TYPE_COL_INF_TEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_INF_EMAIL_PH_PATIENT = "Inf_Email";
        public static final int NUM_COL_INF_EMAIL_PH_PATIENT = 49;
        public static final String TYPE_COL_INF_EMAIL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DATE_ENTREE_PH_PATIENT = "Date_entree";
        public static final int NUM_COL_DATE_ENTREE_PH_PATIENT = 50;
        public static final String TYPE_COL_DATE_ENTREE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT = "Date_Debut_Traitement";
        public static final int NUM_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT = 51;
        public static final String TYPE_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SOUS_TECHNIQUE_PH_PATIENT = "Sous_Technique";
        public static final int NUM_COL_SOUS_TECHNIQUE_PH_PATIENT = 52;
        public static final String TYPE_COL_SOUS_TECHNIQUE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_APPROVISIONNEMENT_PH_PATIENT = "Approvisionnement";
        public static final int NUM_COL_APPROVISIONNEMENT_PH_PATIENT = 53;
        public static final String TYPE_COL_APPROVISIONNEMENT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_LIEU_TRAITEMENT_PH_PATIENT = "Lieu_Traitement";
        public static final int NUM_COL_LIEU_TRAITEMENT_PH_PATIENT = 54;
        public static final String TYPE_COL_LIEU_TRAITEMENT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ID_LIEU_TRAITEMENT_PH_PATIENT = "ID_Lieu_Traitement";
        public static final int NUM_COL_ID_LIEU_TRAITEMENT_PH_PATIENT = 55;
        public static final String TYPE_COL_ID_LIEU_TRAITEMENT_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_ASCENCEUR_PH_PATIENT = "Ascenceur";
        public static final int NUM_COL_ASCENCEUR_PH_PATIENT = 56;
        public static final String TYPE_COL_ASCENCEUR_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_ESCALIER_PH_PATIENT = "Escalier";
        public static final int NUM_COL_ESCALIER_PH_PATIENT = 57;
        public static final String TYPE_COL_ESCALIER_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ETAGE_PH_PATIENT = "Etage";
        public static final int NUM_COL_ETAGE_PH_PATIENT = 58;
        public static final String TYPE_COL_ETAGE_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_DGICODE_PH_PATIENT = "Dgicode";
        public static final int NUM_COL_DGICODE_PH_PATIENT = 59;
        public static final String TYPE_COL_DGICODE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DATE_ETAT_PH_PATIENT = "Date_Etat";
        public static final int NUM_COL_DATE_ETAT_PH_PATIENT = 60;
        public static final String TYPE_COL_DATE_ETAT_PH_PATIENT = "TEXT";
        public static final String CLE_COL_ARCHIVE_PH_PATIENT = "Archive";
        public static final int NUM_COL_ARCHIVE_PH_PATIENT = 61;
        public static final String TYPE_COL_ARCHIVE_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_SECURITE_SOCIALE_PH_PATIENT = "Securite_Sociale";
        public static final int NUM_COL_SECURITE_SOCIALE_PH_PATIENT = 62;
        public static final String TYPE_COL_SECURITE_SOCIALE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_AUTRE_TEL_PH_PATIENT = "Autre_Tel";
        public static final int NUM_COL_AUTRE_TEL_PH_PATIENT = 63;
        public static final String TYPE_COL_AUTRE_TEL_PH_PATIENT = "TEXT";
        public static final String CLE_COL_AUTRE_NOM_PH_PATIENT = "Autre_Nom";
        public static final int NUM_COL_AUTRE_NOM_PH_PATIENT = 64;
        public static final String TYPE_COL_AUTRE_NOM_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CPAM_NOM_PH_PATIENT = "CPAM_Nom";
        public static final int NUM_COL_CPAM_NOM_PH_PATIENT = 65;
        public static final String TYPE_COL_CPAM_NOM_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CPAM_ADRESSE_PH_PATIENT = "CPAM_Adresse";
        public static final int NUM_COL_CPAM_ADRESSE_PH_PATIENT = 66;
        public static final String TYPE_COL_CPAM_ADRESSE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CPAM_CP_PH_PATIENT = "CPAM_CP";
        public static final int NUM_COL_CPAM_CP_PH_PATIENT = 67;
        public static final String TYPE_COL_CPAM_CP_PH_PATIENT = "TEXT";
        public static final String CLE_COL_CPAM_VILLE_PH_PATIENT = "CPAM_Ville";
        public static final int NUM_COL_CPAM_VILLE_PH_PATIENT = 68;
        public static final String TYPE_COL_CPAM_VILLE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_POIDS_PH_PATIENT = "Poids";
        public static final int NUM_COL_POIDS_PH_PATIENT = 69;
        public static final String TYPE_COL_POIDS_PH_PATIENT = "INTEGER";
        public static final String CLE_COL_INSC_PH_PATIENT = "INSC";
        public static final int NUM_COL_INSC_PH_PATIENT = 70;
        public static final String TYPE_COL_INSC_PH_PATIENT = "TEXT";
        public static final String CLE_COL_SEXE_PH_PATIENT = "Sexe";
        public static final int NUM_COL_SEXE_PH_PATIENT = 71;
        public static final String TYPE_COL_SEXE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PHARMACIE_PH_PATIENT = "Pharmacie";
        public static final int NUM_COL_PHARMACIE_PH_PATIENT = 72;
        public static final String TYPE_COL_PHARMACIE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_TRAITEMENT_MODALITE_PH_PATIENT = "Traitement_Modalite";
        public static final int NUM_COL_TRAITEMENT_MODALITE_PH_PATIENT = 73;
        public static final String TYPE_COL_TRAITEMENT_MODALITE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_PHOTO_LIEN_PH_PATIENT = "Photo_lien";
        public static final int NUM_COL_PHOTO_LIEN_PH_PATIENT = 75;
        public static final String TYPE_COL_PHOTO_LIEN_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DOCUMENT_PARTAGE_PH_PATIENT = "Document_partage";
        public static final int NUM_COL_DOCUMENT_PARTAGE_PH_PATIENT = 76;
        public static final String TYPE_COL_DOCUMENT_PARTAGE_PH_PATIENT = "TEXT";
        public static final String CLE_COL_DISCIPLINE_MEDICALE_PH_PATIENT = "discipline_Medicale";
        public static final int NUM_COL_DISCIPLINE_MEDICALE_PH_PATIENT = 77;
        public static final String TYPE_COL_DISCIPLINE_MEDICALE_PH_PATIENT = "TEXT";

        public static final String CREATION_TABLE_PH_PATIENT = " CREATE TABLE       " + Constantes.TABLE_PH_PATIENT
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + "    PRIMARY KEY,"
                + Constantes.CLE_COL__PATIENTUID_PH_PATIENT + "  " + Constantes.TYPE_COL__PATIENTUID_PH_PATIENT + " , "
                + Constantes.CLE_COL_CIVILITE_PH_PATIENT + "  " + Constantes.TYPE_COL_CIVILITE_PH_PATIENT + " , "
                + Constantes.CLE_COL_NOM_NAISSANCE_PH_PATIENT + "  " + Constantes.TYPE_COL_NOM_NAISSANCE_PH_PATIENT + " , "
                + Constantes.CLE_COL_PRENOM_PH_PATIENT + "  " + Constantes.TYPE_COL_PRENOM_PH_PATIENT + " , "
                + Constantes.CLE_COL_ADRESSE1_PH_PATIENT + "  " + Constantes.TYPE_COL_ADRESSE1_PH_PATIENT + " , "
                + Constantes.CLE_COL_ADRESSE2_PH_PATIENT + "  " + Constantes.TYPE_COL_ADRESSE2_PH_PATIENT + " , "
                + Constantes.CLE_COL_CP_PH_PATIENT + "  " + Constantes.TYPE_COL_CP_PH_PATIENT + " , "
                + Constantes.CLE_COL_VILLE_PH_PATIENT + "  " + Constantes.TYPE_COL_VILLE_PH_PATIENT + " , "
                + Constantes.CLE_COL_TEL1_PH_PATIENT + "  " + Constantes.TYPE_COL_TEL1_PH_PATIENT + " , "
                + Constantes.CLE_COL_IPP_PH_PATIENT + "  " + Constantes.TYPE_COL_IPP_PH_PATIENT + " , "
                + Constantes.CLE_COL_FAX_PH_PATIENT + "  " + Constantes.TYPE_COL_FAX_PH_PATIENT + " , "
                + Constantes.CLE_COL_SYS_DT_MAJ_PH_PATIENT + "  " + Constantes.TYPE_COL_SYS_DT_MAJ_PH_PATIENT + " , "
                + Constantes.CLE_COL_SYS_HEURE_MAJ_PH_PATIENT + "  " + Constantes.TYPE_COL_SYS_HEURE_MAJ_PH_PATIENT + " , "
                + Constantes.CLE_COL_SYS_USER_MAJ_PH_PATIENT + "  " + Constantes.TYPE_COL_SYS_USER_MAJ_PH_PATIENT + " , "
                + Constantes.CLE_COL_ADRESSE3_PH_PATIENT + "  " + Constantes.TYPE_COL_ADRESSE3_PH_PATIENT + " , "
                + Constantes.CLE_COL_TEL_PROFESSIONNEL_PH_PATIENT + "  " + Constantes.TYPE_COL_TEL_PROFESSIONNEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_TEL2_PH_PATIENT + "  " + Constantes.TYPE_COL_TEL2_PH_PATIENT + " , "
                + Constantes.CLE_COL_TECHNIQUE_PH_PATIENT + "  " + Constantes.TYPE_COL_TECHNIQUE_PH_PATIENT + " , "
                + Constantes.CLE_COL_IPP_FAC_PH_PATIENT + "  " + Constantes.TYPE_COL_IPP_FAC_PH_PATIENT + " , "
                + Constantes.CLE_COL_IPP_DM_PH_PATIENT + "  " + Constantes.TYPE_COL_IPP_DM_PH_PATIENT + " , "
                + Constantes.CLE_COL_NOM_MARITAL_PH_PATIENT + "  " + Constantes.TYPE_COL_NOM_MARITAL_PH_PATIENT + " , "
                + Constantes.CLE_COL_DATE_NAISSANCE_PH_PATIENT + "  " + Constantes.TYPE_COL_DATE_NAISSANCE_PH_PATIENT + " , "
                + Constantes.CLE_COL_MATRICULE_PH_PATIENT + "  " + Constantes.TYPE_COL_MATRICULE_PH_PATIENT + " , "
                + Constantes.CLE_COL_CLEF_PH_PATIENT + "  " + Constantes.TYPE_COL_CLEF_PH_PATIENT + " , "
                + Constantes.CLE_COL_LIEU_NAISSANCE_PH_PATIENT + "  " + Constantes.TYPE_COL_LIEU_NAISSANCE_PH_PATIENT + " , "
                + Constantes.CLE_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT + "  " + Constantes.TYPE_COL_SEXE_MASCULIN_FEMININ_PH_PATIENT + " , "
                + Constantes.CLE_COL_FAX_PROFESSIONNEL_PH_PATIENT + "  " + Constantes.TYPE_COL_FAX_PROFESSIONNEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_PROFESSION_PH_PATIENT + "  " + Constantes.TYPE_COL_PROFESSION_PH_PATIENT + " , "
                + Constantes.CLE_COL_NOM_USUEL_PH_PATIENT + "  " + Constantes.TYPE_COL_NOM_USUEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_EMAIL_PH_PATIENT + "  " + Constantes.TYPE_COL_EMAIL_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_ADR1_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_ADR1_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_ADR2_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_ADR2_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_CP_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_CP_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_VILLE_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_VILLE_PH_PATIENT + " , "
                + Constantes.CLE_COL_PERSONNE_RESSOURCE_PH_PATIENT + "  " + Constantes.TYPE_COL_PERSONNE_RESSOURCE_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_TEL_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_TEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_RESSOURCE_FAX_PH_PATIENT + "  " + Constantes.TYPE_COL_RESSOURCE_FAX_PH_PATIENT + " , "
                + Constantes.CLE_COL_CENTRE_HOSPITALIER_PH_PATIENT + "  " + Constantes.TYPE_COL_CENTRE_HOSPITALIER_PH_PATIENT + " , "
                + Constantes.CLE_COL_PRATICIEN_PH_PATIENT + "  " + Constantes.TYPE_COL_PRATICIEN_PH_PATIENT + " , "
                + Constantes.CLE_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT + "  " + Constantes.TYPE_COL_MOTIF_SUSPENSION_TRAITEMENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_DETAIL_ETAT_PATIENT_PH_PATIENT + "  " + Constantes.TYPE_COL_DETAIL_ETAT_PATIENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_INFIRMIER_PH_PATIENT + "  " + Constantes.TYPE_COL_INFIRMIER_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_ADR1_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_ADR1_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_ADR2_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_ADR2_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_CP_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_CP_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_VILLE_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_VILLE_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_FAX_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_FAX_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_TEL_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_TEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_INF_EMAIL_PH_PATIENT + "  " + Constantes.TYPE_COL_INF_EMAIL_PH_PATIENT + " , "
                + Constantes.CLE_COL_DATE_ENTREE_PH_PATIENT + "  " + Constantes.TYPE_COL_DATE_ENTREE_PH_PATIENT + " , "
                + Constantes.CLE_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT + "  " + Constantes.TYPE_COL_DATE_DEBUT_TRAITEMENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_SOUS_TECHNIQUE_PH_PATIENT + "  " + Constantes.TYPE_COL_SOUS_TECHNIQUE_PH_PATIENT + " , "
                + Constantes.CLE_COL_APPROVISIONNEMENT_PH_PATIENT + "  " + Constantes.TYPE_COL_APPROVISIONNEMENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_LIEU_TRAITEMENT_PH_PATIENT + "  " + Constantes.TYPE_COL_LIEU_TRAITEMENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_ID_LIEU_TRAITEMENT_PH_PATIENT + "  " + Constantes.TYPE_COL_ID_LIEU_TRAITEMENT_PH_PATIENT + " , "
                + Constantes.CLE_COL_ASCENCEUR_PH_PATIENT + "  " + Constantes.TYPE_COL_ASCENCEUR_PH_PATIENT + " , "
                + Constantes.CLE_COL_ESCALIER_PH_PATIENT + "  " + Constantes.TYPE_COL_ESCALIER_PH_PATIENT + " , "
                + Constantes.CLE_COL_ETAGE_PH_PATIENT + "  " + Constantes.TYPE_COL_ETAGE_PH_PATIENT + " , "
                + Constantes.CLE_COL_DGICODE_PH_PATIENT + "  " + Constantes.TYPE_COL_DGICODE_PH_PATIENT + " , "
                + Constantes.CLE_COL_DATE_ETAT_PH_PATIENT + "  " + Constantes.TYPE_COL_DATE_ETAT_PH_PATIENT + " , "
                + Constantes.CLE_COL_ARCHIVE_PH_PATIENT + "  " + Constantes.TYPE_COL_ARCHIVE_PH_PATIENT + " , "
                + Constantes.CLE_COL_SECURITE_SOCIALE_PH_PATIENT + "  " + Constantes.TYPE_COL_SECURITE_SOCIALE_PH_PATIENT + " , "
                + Constantes.CLE_COL_AUTRE_TEL_PH_PATIENT + "  " + Constantes.TYPE_COL_AUTRE_TEL_PH_PATIENT + " , "
                + Constantes.CLE_COL_AUTRE_NOM_PH_PATIENT + "  " + Constantes.TYPE_COL_AUTRE_NOM_PH_PATIENT + " , "
                + Constantes.CLE_COL_CPAM_NOM_PH_PATIENT + "  " + Constantes.TYPE_COL_CPAM_NOM_PH_PATIENT + " , "
                + Constantes.CLE_COL_CPAM_ADRESSE_PH_PATIENT + "  " + Constantes.TYPE_COL_CPAM_ADRESSE_PH_PATIENT + " , "
                + Constantes.CLE_COL_CPAM_CP_PH_PATIENT + "  " + Constantes.TYPE_COL_CPAM_CP_PH_PATIENT + " , "
                + Constantes.CLE_COL_CPAM_VILLE_PH_PATIENT + "  " + Constantes.TYPE_COL_CPAM_VILLE_PH_PATIENT + " , "
                + Constantes.CLE_COL_POIDS_PH_PATIENT + "  " + Constantes.TYPE_COL_POIDS_PH_PATIENT + " , "
                + Constantes.CLE_COL_INSC_PH_PATIENT + "  " + Constantes.TYPE_COL_INSC_PH_PATIENT + " , "
                + Constantes.CLE_COL_SEXE_PH_PATIENT + "  " + Constantes.TYPE_COL_SEXE_PH_PATIENT + " , "
                + Constantes.CLE_COL_PHARMACIE_PH_PATIENT + "  " + Constantes.TYPE_COL_PHARMACIE_PH_PATIENT + " , "
                + Constantes.CLE_COL_TRAITEMENT_MODALITE_PH_PATIENT + "  " + Constantes.TYPE_COL_TRAITEMENT_MODALITE_PH_PATIENT + " , "
                + Constantes.CLE_COL_PHOTO_LIEN_PH_PATIENT + "  " + Constantes.TYPE_COL_PHOTO_LIEN_PH_PATIENT + " , "
                + Constantes.CLE_COL_DOCUMENT_PARTAGE_PH_PATIENT + "  " + Constantes.TYPE_COL_DOCUMENT_PARTAGE_PH_PATIENT + " , "
                + Constantes.CLE_COL_DISCIPLINE_MEDICALE_PH_PATIENT + "  " + Constantes.TYPE_COL_DISCIPLINE_MEDICALE_PH_PATIENT
                + " ); ";

    }
}
