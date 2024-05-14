package fr.alcyons.phiwms_mobile.BaseDeDonnees;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import fr.alcyons.phiwms_mobile.Classes.Fournisseur;

public class FournisseurOpenHelper extends DBOpenHelper {

    public FournisseurOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static long supprimerDonneesTest(SQLiteDatabase db)
    {
        return db.delete(Constantes.TABLE_FOURNISSEUR, Constantes.CLE_COL_RAISONSOCIALE_FOURNISSEUR + "=?", new String[]{"ALCYONS_Fournisseur"});
    }

    public static long insererUnFournisseurEnBDD(SQLiteDatabase db, Fournisseur fournisseur) {
        // Récupération des éléments du dépot
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constantes.CLE_COL_NUMERO_FOURNISSEUR, fournisseur.getNumero());
        contentValues.put(Constantes.CLE_COL_RAISONSOCIALE_FOURNISSEUR, fournisseur.getRaisonSociale());
        contentValues.put(Constantes.CLE_COL_COMMANDE_ADRESSE1_FOURNISSEUR, fournisseur.getCommande_adresse1());
        contentValues.put(Constantes.CLE_COL_COMMANDE_ADRESSE2_FOURNISSEUR, fournisseur.getCommande_adresse2());
        contentValues.put(Constantes.CLE_COL_COMMANDE_CP_FOURNISSEUR, fournisseur.getCommande_CP());
        contentValues.put(Constantes.CLE_COL_COMMANDE_VILLE_FOURNISSEUR, fournisseur.getCommande_Ville());
        contentValues.put(Constantes.CLE_COL_COMMANDE_PAYS_FOURNISSEUR, fournisseur.getCommande_Pays());
        contentValues.put(Constantes.CLE_COL_COMMANDE_TELEPHONE_FOURNISSEUR, fournisseur.getCommande_Telephone());
        contentValues.put(Constantes.CLE_COL_COMMANDE_FAX_FOURNISSEUR, fournisseur.getCommande_Fax());
        contentValues.put(Constantes.CLE_COL_CONTACT_CIVILITE_FOURNISSEUR, fournisseur.getContact_civilite());
        contentValues.put(Constantes.CLE_COL_CONTACT_PRENOM_FOURNISSEUR, fournisseur.getContact_prenom());
        contentValues.put(Constantes.CLE_COL_CONTACT_NOM_FOURNISSEUR, fournisseur.getContact_nom());
        contentValues.put(Constantes.CLE_COL_COMPTE_FOURNISSEUR, fournisseur.getCompte());
        contentValues.put(Constantes.CLE_COL__UID_FOURNISSEUR, fournisseur.get_UID());
        contentValues.put(Constantes.CLE_COL_EN_COURS_FOURNISSEUR, fournisseur.getEn_cours());
        contentValues.put(Constantes.CLE_COL_SEUIL_ENCOURS_FOURNISSEUR, fournisseur.getSeuil_encours());
        contentValues.put(Constantes.CLE_COL_CA_FOURNISSEUR, fournisseur.getCA());
        contentValues.put(Constantes.CLE_COL_REFERENCECLIENT_FOURNISSEUR, fournisseur.getReferenceClient());
        contentValues.put(Constantes.CLE_COL_CONTACT_ADRESSE1_FOURNISSEUR, fournisseur.getContact_Adresse1());
        contentValues.put(Constantes.CLE_COL_CERTIFICATION_FOURNISSEUR, fournisseur.getCertification());
        contentValues.put(Constantes.CLE_COL_TAUX_ESCOMPTE1_FOURNISSEUR, fournisseur.getTaux_Escompte1());
        contentValues.put(Constantes.CLE_COL_NBJ_ESCOMPTE1_FOURNISSEUR, fournisseur.getNbj_escompte1());
        contentValues.put(Constantes.CLE_COL_TAUX_ESCOMPTE2_FOURNISSEUR, fournisseur.getTaux_escompte2());
        contentValues.put(Constantes.CLE_COL_NBJ_ESCOMPTE2_FOURNISSEUR, fournisseur.getNbj_escompte2());
        contentValues.put(Constantes.CLE_COL_GROUPE_FOURNISSEUR, fournisseur.getGroupe());
        contentValues.put(Constantes.CLE_COL_REFERANCEGROUPE_FOURNISSEUR, fournisseur.getReferanceGroupe());
        contentValues.put(Constantes.CLE_COL_MODEREGLEMENT_FOURNISSEUR, fournisseur.getModeReglement());
        contentValues.put(Constantes.CLE_COL_CONDITIONSREGLEMENT_FOURNISSEUR, fournisseur.getConditionsReglement());
        contentValues.put(Constantes.CLE_COL_CONTACT_ADRESSE2_FOURNISSEUR, fournisseur.getContact_Adresse2());
        contentValues.put(Constantes.CLE_COL_CONTACT_TEL_FOURNISSEUR, fournisseur.getContact_Tel());
        contentValues.put(Constantes.CLE_COL_CONTACT_FAX_FOURNISSEUR, fournisseur.getContact_Fax());
        contentValues.put(Constantes.CLE_COL_CONTACT_CP_FOURNISSEUR, fournisseur.getContact_CP());
        contentValues.put(Constantes.CLE_COL_CONTACT_VILLE_FOURNISSEUR, fournisseur.getContact_Ville());
        contentValues.put(Constantes.CLE_COL_SEUIL_POUR_FRANCO_FOURNISSEUR, fournisseur.getSeuil_pour_franco());
        contentValues.put(Constantes.CLE_COL_WEB_FOURNISSEUR, fournisseur.getWeb());
        contentValues.put(Constantes.CLE_COL_COMMANDE_EMAIL_FOURNISSEUR, fournisseur.getCommande_Email());
        contentValues.put(Constantes.CLE_COL_CONTACT_PORTABLE_COM_FOURNISSEUR, fournisseur.getContact_Portable_com());
        contentValues.put(Constantes.CLE_COL_AFFECTATION_FOURNISSEUR, fournisseur.getAffectation());
        contentValues.put(Constantes.CLE_COL_ST_CIVILITE_FOURNISSEUR, fournisseur.getST_Civilite());
        contentValues.put(Constantes.CLE_COL_ST_PRENOM_FOURNISSEUR, fournisseur.getST_Prénom());
        contentValues.put(Constantes.CLE_COL_ST_NOM_FOURNISSEUR, fournisseur.getST_Nom());
        contentValues.put(Constantes.CLE_COL_ST_ADRESSE1_COM_FOURNISSEUR, fournisseur.getST_Adresse1_com());
        contentValues.put(Constantes.CLE_COL_ST_ADRESSE2_COM_FOURNISSEUR, fournisseur.getST_Adresse2_Com());
        contentValues.put(Constantes.CLE_COL_ST_CP_COM_FOURNISSEUR, fournisseur.getST_CP_com());
        contentValues.put(Constantes.CLE_COL_ST_VILLE_COM_FOURNISSEUR, fournisseur.getST_Ville_com());
        contentValues.put(Constantes.CLE_COL_ST_TEL_COM_FOURNISSEUR, fournisseur.getST_Tel_com());
        contentValues.put(Constantes.CLE_COL_ST_FAX_COM_FOURNISSEUR, fournisseur.getST_Fax_com());
        contentValues.put(Constantes.CLE_COL_ST_EMAIL_COM_FOURNISSEUR, fournisseur.getSt_Email_com());
        contentValues.put(Constantes.CLE_COL_ST_PORTABLE_COM_FOURNISSEUR, fournisseur.getST_Portable_com());
        contentValues.put(Constantes.CLE_COL__SYS_DT_MAJ_FOURNISSEUR, fournisseur.get_SYS_DT_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_HEURE_MAJ_FOURNISSEUR, fournisseur.get_SYS_HEURE_MAJ());
        contentValues.put(Constantes.CLE_COL__SYS_USER_MAJ_FOURNISSEUR, fournisseur.get_SYS_USER_MAJ());
        contentValues.put(Constantes.CLE_COL_TYPE_FRANCO_FOURNISSEUR, fournisseur.getType_franco());
        contentValues.put(Constantes.CLE_COL_SAP_GROUPE_FOURNISSEUR, fournisseur.getSAP_Groupe());
        contentValues.put(Constantes.CLE_COL_SAP_COMPTE_FOURNISSEUR, fournisseur.getSAp_Compte());
        contentValues.put(Constantes.CLE_COL_DELAI_LIVRAISON_FOURNISSEUR, fournisseur.getDelai_Livraison());
        contentValues.put(Constantes.CLE_COL_MONTANT_MIN_FOURNISSEUR, fournisseur.getMontant_Min());
        contentValues.put(Constantes.CLE_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR, fournisseur.getDistrib_Raison_Sociale());
        contentValues.put(Constantes.CLE_COL_DISTRIBUTEUR_FOURNISSEUR, fournisseur.getDistributeur());
        contentValues.put(Constantes.CLE_COL_MONTANT_FRAIS_FOURNISSEUR, fournisseur.getMontant_Frais());
        contentValues.put(Constantes.CLE_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR, fournisseur.getPharmaco_Vigilance_Tel());
        contentValues.put(Constantes.CLE_COL_ARCHIVE_FOURNISSEUR, fournisseur.getArchive());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR, fournisseur.getCommentaire_Commande_Edition());
        contentValues.put(Constantes.CLE_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR, fournisseur.getCommentaire_Cde_Confidentiel());
        contentValues.put(Constantes.CLE_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR, fournisseur.getLivraisonDomicileAutoriser());
        contentValues.put(Constantes.CLE_COL_DOCUMENT_MARCHE_FOURNISSEUR, fournisseur.getDocument_marche());
        contentValues.put(Constantes.CLE_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR, fournisseur.getDocument_developpementDurable());
        contentValues.put(Constantes.CLE_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR, fournisseur.getDocument_certification());
        contentValues.put(Constantes.CLE_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR, fournisseur.getDeveloppementDurable());
        contentValues.put(Constantes.CLE_COL_RESPONSABLEACHAT_FOURNISSEUR, fournisseur.getResponsableAchat());
        contentValues.put(Constantes.CLE_COL_LIVRAISONFREQUENCE_FOURNISSEUR, fournisseur.getLivraisonFrequence());
        contentValues.put(Constantes.CLE_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR, fournisseur.getLivraisonJourSemaine());
        contentValues.put(Constantes.CLE_COL_FABRICANT_FOURNISSEUR, fournisseur.getFabricant());
        contentValues.put(Constantes.CLE_COL_CONTACT_EMAIL_FOURNISSEUR, fournisseur.getContact_Email());
        contentValues.put(Constantes.CLE_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR, fournisseur.getPharmaco_Vigilance_Email());
        contentValues.put(Constantes.CLE_COL_TRANSPORT_TYPE_FOURNISSEUR, fournisseur.getTransport_Type());
        contentValues.put(Constantes.CLE_COL_IMPORT_TRANSITAIRE_FOURNISSEUR, fournisseur.getImport_transitaire());
        contentValues.put(Constantes.CLE_COL_AFFECTATION_DETAILLEE_FOURNISSEUR, fournisseur.getAffectation_Detaillee());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR, fournisseur.getTransitaire_Metropolitain());
        contentValues.put(Constantes.CLE_COL_TRANSITAIRE_LOCAL_FOURNISSEUR, fournisseur.getTransitaire_Local());
        contentValues.put(Constantes.CLE_COL_IMPORT_DDP_FOURNISSEUR, fournisseur.getImport_DDP());
        contentValues.put(Constantes.CLE_COL_DEVISE_FACTURATION_FOURNISSEUR, fournisseur.getDevise_Facturation());

        // Insertion du dépot en BDD
        long rowId = db.insert(Constantes.TABLE_FOURNISSEUR, null, contentValues);

        fournisseur.setphiwms_mobileUUID((int) rowId);

        return rowId;
    }


    public static class Constantes implements BaseColumns {
        public static final String TABLE_FOURNISSEUR = "Fournisseur";

        public static final String CLE_COL_RAISONSOCIALE_FOURNISSEUR = "raisonSociale";
        public static final int NUM_COL_RAISONSOCIALE_FOURNISSEUR = 1;
        public static final String TYPE_COL_RAISONSOCIALE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_ADRESSE1_FOURNISSEUR = "Commande_adresse1";
        public static final int NUM_COL_COMMANDE_ADRESSE1_FOURNISSEUR = 2;
        public static final String TYPE_COL_COMMANDE_ADRESSE1_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_ADRESSE2_FOURNISSEUR = "Commande_adresse2";
        public static final int NUM_COL_COMMANDE_ADRESSE2_FOURNISSEUR = 3;
        public static final String TYPE_COL_COMMANDE_ADRESSE2_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_CP_FOURNISSEUR = "Commande_CP";
        public static final int NUM_COL_COMMANDE_CP_FOURNISSEUR = 4;
        public static final String TYPE_COL_COMMANDE_CP_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_VILLE_FOURNISSEUR = "Commande_Ville";
        public static final int NUM_COL_COMMANDE_VILLE_FOURNISSEUR = 5;
        public static final String TYPE_COL_COMMANDE_VILLE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_PAYS_FOURNISSEUR = "Commande_Pays";
        public static final int NUM_COL_COMMANDE_PAYS_FOURNISSEUR = 6;
        public static final String TYPE_COL_COMMANDE_PAYS_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_TELEPHONE_FOURNISSEUR = "Commande_Telephone";
        public static final int NUM_COL_COMMANDE_TELEPHONE_FOURNISSEUR = 7;
        public static final String TYPE_COL_COMMANDE_TELEPHONE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_FAX_FOURNISSEUR = "Commande_Fax";
        public static final int NUM_COL_COMMANDE_FAX_FOURNISSEUR = 8;
        public static final String TYPE_COL_COMMANDE_FAX_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_CIVILITE_FOURNISSEUR = "Contact_civilite";
        public static final int NUM_COL_CONTACT_CIVILITE_FOURNISSEUR = 9;
        public static final String TYPE_COL_CONTACT_CIVILITE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_PRENOM_FOURNISSEUR = "Contact_prenom";
        public static final int NUM_COL_CONTACT_PRENOM_FOURNISSEUR = 10;
        public static final String TYPE_COL_CONTACT_PRENOM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_NOM_FOURNISSEUR = "Contact_nom";
        public static final int NUM_COL_CONTACT_NOM_FOURNISSEUR = 11;
        public static final String TYPE_COL_CONTACT_NOM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMPTE_FOURNISSEUR = "compte";
        public static final int NUM_COL_COMPTE_FOURNISSEUR = 12;
        public static final String TYPE_COL_COMPTE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL__UID_FOURNISSEUR = "_UID";
        public static final int NUM_COL__UID_FOURNISSEUR = 13;
        public static final String TYPE_COL__UID_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_EN_COURS_FOURNISSEUR = "En_cours";
        public static final int NUM_COL_EN_COURS_FOURNISSEUR = 14;
        public static final String TYPE_COL_EN_COURS_FOURNISSEUR = "REAL";
        public static final String CLE_COL_SEUIL_ENCOURS_FOURNISSEUR = "Seuil_encours";
        public static final int NUM_COL_SEUIL_ENCOURS_FOURNISSEUR = 15;
        public static final String TYPE_COL_SEUIL_ENCOURS_FOURNISSEUR = "REAL";
        public static final String CLE_COL_CA_FOURNISSEUR = "CA";
        public static final int NUM_COL_CA_FOURNISSEUR = 16;
        public static final String TYPE_COL_CA_FOURNISSEUR = "REAL";
        public static final String CLE_COL_REFERENCECLIENT_FOURNISSEUR = "ReferenceClient";
        public static final int NUM_COL_REFERENCECLIENT_FOURNISSEUR = 17;
        public static final String TYPE_COL_REFERENCECLIENT_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_ADRESSE1_FOURNISSEUR = "Contact_Adresse1";
        public static final int NUM_COL_CONTACT_ADRESSE1_FOURNISSEUR = 18;
        public static final String TYPE_COL_CONTACT_ADRESSE1_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CERTIFICATION_FOURNISSEUR = "Certification";
        public static final int NUM_COL_CERTIFICATION_FOURNISSEUR = 19;
        public static final String TYPE_COL_CERTIFICATION_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_TAUX_ESCOMPTE1_FOURNISSEUR = "Taux_Escompte1";
        public static final int NUM_COL_TAUX_ESCOMPTE1_FOURNISSEUR = 20;
        public static final String TYPE_COL_TAUX_ESCOMPTE1_FOURNISSEUR = "REAL";
        public static final String CLE_COL_NBJ_ESCOMPTE1_FOURNISSEUR = "Nbj_escompte1";
        public static final int NUM_COL_NBJ_ESCOMPTE1_FOURNISSEUR = 21;
        public static final String TYPE_COL_NBJ_ESCOMPTE1_FOURNISSEUR = "REAL";
        public static final String CLE_COL_TAUX_ESCOMPTE2_FOURNISSEUR = "Taux_escompte2";
        public static final int NUM_COL_TAUX_ESCOMPTE2_FOURNISSEUR = 22;
        public static final String TYPE_COL_TAUX_ESCOMPTE2_FOURNISSEUR = "REAL";
        public static final String CLE_COL_NBJ_ESCOMPTE2_FOURNISSEUR = "Nbj_escompte2";
        public static final int NUM_COL_NBJ_ESCOMPTE2_FOURNISSEUR = 23;
        public static final String TYPE_COL_NBJ_ESCOMPTE2_FOURNISSEUR = "REAL";
        public static final String CLE_COL_GROUPE_FOURNISSEUR = "Groupe";
        public static final int NUM_COL_GROUPE_FOURNISSEUR = 24;
        public static final String TYPE_COL_GROUPE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_REFERANCEGROUPE_FOURNISSEUR = "ReferanceGroupe";
        public static final int NUM_COL_REFERANCEGROUPE_FOURNISSEUR = 25;
        public static final String TYPE_COL_REFERANCEGROUPE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_MODEREGLEMENT_FOURNISSEUR = "ModeReglement";
        public static final int NUM_COL_MODEREGLEMENT_FOURNISSEUR = 26;
        public static final String TYPE_COL_MODEREGLEMENT_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONDITIONSREGLEMENT_FOURNISSEUR = "ConditionsReglement";
        public static final int NUM_COL_CONDITIONSREGLEMENT_FOURNISSEUR = 27;
        public static final String TYPE_COL_CONDITIONSREGLEMENT_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_ADRESSE2_FOURNISSEUR = "Contact_Adresse2";
        public static final int NUM_COL_CONTACT_ADRESSE2_FOURNISSEUR = 28;
        public static final String TYPE_COL_CONTACT_ADRESSE2_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_TEL_FOURNISSEUR = "Contact_Tel";
        public static final int NUM_COL_CONTACT_TEL_FOURNISSEUR = 29;
        public static final String TYPE_COL_CONTACT_TEL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_FAX_FOURNISSEUR = "Contact_Fax";
        public static final int NUM_COL_CONTACT_FAX_FOURNISSEUR = 30;
        public static final String TYPE_COL_CONTACT_FAX_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_CP_FOURNISSEUR = "Contact_CP";
        public static final int NUM_COL_CONTACT_CP_FOURNISSEUR = 31;
        public static final String TYPE_COL_CONTACT_CP_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_VILLE_FOURNISSEUR = "Contact_Ville";
        public static final int NUM_COL_CONTACT_VILLE_FOURNISSEUR = 32;
        public static final String TYPE_COL_CONTACT_VILLE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_SEUIL_POUR_FRANCO_FOURNISSEUR = "Seuil_pour_franco";
        public static final int NUM_COL_SEUIL_POUR_FRANCO_FOURNISSEUR = 33;
        public static final String TYPE_COL_SEUIL_POUR_FRANCO_FOURNISSEUR = "REAL";
        public static final String CLE_COL_WEB_FOURNISSEUR = "Web";
        public static final int NUM_COL_WEB_FOURNISSEUR = 34;
        public static final String TYPE_COL_WEB_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMANDE_EMAIL_FOURNISSEUR = "Commande_Email";
        public static final int NUM_COL_COMMANDE_EMAIL_FOURNISSEUR = 35;
        public static final String TYPE_COL_COMMANDE_EMAIL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_CONTACT_PORTABLE_COM_FOURNISSEUR = "Contact_Portable_com";
        public static final int NUM_COL_CONTACT_PORTABLE_COM_FOURNISSEUR = 36;
        public static final String TYPE_COL_CONTACT_PORTABLE_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_AFFECTATION_FOURNISSEUR = "Affectation";
        public static final int NUM_COL_AFFECTATION_FOURNISSEUR = 37;
        public static final String TYPE_COL_AFFECTATION_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_CIVILITE_FOURNISSEUR = "ST_Civilite";
        public static final int NUM_COL_ST_CIVILITE_FOURNISSEUR = 38;
        public static final String TYPE_COL_ST_CIVILITE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_PRENOM_FOURNISSEUR = "ST_Prénom";
        public static final int NUM_COL_ST_PRENOM_FOURNISSEUR = 39;
        public static final String TYPE_COL_ST_PRENOM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_NOM_FOURNISSEUR = "ST_Nom";
        public static final int NUM_COL_ST_NOM_FOURNISSEUR = 40;
        public static final String TYPE_COL_ST_NOM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_ADRESSE1_COM_FOURNISSEUR = "ST_Adresse1_com";
        public static final int NUM_COL_ST_ADRESSE1_COM_FOURNISSEUR = 41;
        public static final String TYPE_COL_ST_ADRESSE1_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_ADRESSE2_COM_FOURNISSEUR = "ST_Adresse2_Com";
        public static final int NUM_COL_ST_ADRESSE2_COM_FOURNISSEUR = 42;
        public static final String TYPE_COL_ST_ADRESSE2_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_CP_COM_FOURNISSEUR = "ST_CP_com";
        public static final int NUM_COL_ST_CP_COM_FOURNISSEUR = 43;
        public static final String TYPE_COL_ST_CP_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_VILLE_COM_FOURNISSEUR = "ST_Ville_com";
        public static final int NUM_COL_ST_VILLE_COM_FOURNISSEUR = 44;
        public static final String TYPE_COL_ST_VILLE_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_TEL_COM_FOURNISSEUR = "ST_Tel_com";
        public static final int NUM_COL_ST_TEL_COM_FOURNISSEUR = 45;
        public static final String TYPE_COL_ST_TEL_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_FAX_COM_FOURNISSEUR = "ST_Fax_com";
        public static final int NUM_COL_ST_FAX_COM_FOURNISSEUR = 46;
        public static final String TYPE_COL_ST_FAX_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_EMAIL_COM_FOURNISSEUR = "St_Email_com";
        public static final int NUM_COL_ST_EMAIL_COM_FOURNISSEUR = 47;
        public static final String TYPE_COL_ST_EMAIL_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ST_PORTABLE_COM_FOURNISSEUR = "ST_Portable_com";
        public static final int NUM_COL_ST_PORTABLE_COM_FOURNISSEUR = 48;
        public static final String TYPE_COL_ST_PORTABLE_COM_FOURNISSEUR = "TEXT";
        public static final String CLE_COL__SYS_DT_MAJ_FOURNISSEUR = "_SYS_DT_MAJ";
        public static final int NUM_COL__SYS_DT_MAJ_FOURNISSEUR = 49;
        public static final String TYPE_COL__SYS_DT_MAJ_FOURNISSEUR = "TEXT";
        public static final String CLE_COL__SYS_HEURE_MAJ_FOURNISSEUR = "_SYS_HEURE_MAJ";
        public static final int NUM_COL__SYS_HEURE_MAJ_FOURNISSEUR = 50;
        public static final String TYPE_COL__SYS_HEURE_MAJ_FOURNISSEUR = "TEXT";
        public static final String CLE_COL__SYS_USER_MAJ_FOURNISSEUR = "_SYS_USER_MAJ";
        public static final int NUM_COL__SYS_USER_MAJ_FOURNISSEUR = 51;
        public static final String TYPE_COL__SYS_USER_MAJ_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_TYPE_FRANCO_FOURNISSEUR = "Type_franco";
        public static final int NUM_COL_TYPE_FRANCO_FOURNISSEUR = 52;
        public static final String TYPE_COL_TYPE_FRANCO_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_SAP_GROUPE_FOURNISSEUR = "SAP_Groupe";
        public static final int NUM_COL_SAP_GROUPE_FOURNISSEUR = 53;
        public static final String TYPE_COL_SAP_GROUPE_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_SAP_COMPTE_FOURNISSEUR = "SAp_Compte";
        public static final int NUM_COL_SAP_COMPTE_FOURNISSEUR = 54;
        public static final String TYPE_COL_SAP_COMPTE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_DELAI_LIVRAISON_FOURNISSEUR = "Delai_Livraison";
        public static final int NUM_COL_DELAI_LIVRAISON_FOURNISSEUR = 55;
        public static final String TYPE_COL_DELAI_LIVRAISON_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_MONTANT_MIN_FOURNISSEUR = "Montant_Min";
        public static final int NUM_COL_MONTANT_MIN_FOURNISSEUR = 56;
        public static final String TYPE_COL_MONTANT_MIN_FOURNISSEUR = "REAL";
        public static final String CLE_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR = "Distrib_Raison_Sociale";
        public static final int NUM_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR = 57;
        public static final String TYPE_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_DISTRIBUTEUR_FOURNISSEUR = "Distributeur";
        public static final int NUM_COL_DISTRIBUTEUR_FOURNISSEUR = 58;
        public static final String TYPE_COL_DISTRIBUTEUR_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_MONTANT_FRAIS_FOURNISSEUR = "Montant_Frais";
        public static final int NUM_COL_MONTANT_FRAIS_FOURNISSEUR = 59;
        public static final String TYPE_COL_MONTANT_FRAIS_FOURNISSEUR = "REAL";
        public static final String CLE_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR = "Pharmaco_Vigilance_Tel";
        public static final int NUM_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR = 60;
        public static final String TYPE_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_ARCHIVE_FOURNISSEUR = "Archive";
        public static final int NUM_COL_ARCHIVE_FOURNISSEUR = 61;
        public static final String TYPE_COL_ARCHIVE_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR = "Commentaire_Commande_Edition";
        public static final int NUM_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR = 62;
        public static final String TYPE_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR = "Commentaire_Cde_Confidentiel";
        public static final int NUM_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR = 63;
        public static final String TYPE_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR = "LivraisonDomicileAutoriser";
        public static final int NUM_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR = 64;
        public static final String TYPE_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_DOCUMENT_MARCHE_FOURNISSEUR = "document_marche";
        public static final int NUM_COL_DOCUMENT_MARCHE_FOURNISSEUR = 65;
        public static final String TYPE_COL_DOCUMENT_MARCHE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR = "document_developpementDurable";
        public static final int NUM_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR = 66;
        public static final String TYPE_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR = "document_certification";
        public static final int NUM_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR = 67;
        public static final String TYPE_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR = "developpementDurable";
        public static final int NUM_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR = 68;
        public static final String TYPE_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_RESPONSABLEACHAT_FOURNISSEUR = "responsableAchat";
        public static final int NUM_COL_RESPONSABLEACHAT_FOURNISSEUR = 69;
        public static final String TYPE_COL_RESPONSABLEACHAT_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_LIVRAISONFREQUENCE_FOURNISSEUR = "LivraisonFrequence";
        public static final int NUM_COL_LIVRAISONFREQUENCE_FOURNISSEUR = 70;
        public static final String TYPE_COL_LIVRAISONFREQUENCE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR = "LivraisonJourSemaine";
        public static final int NUM_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR = 71;
        public static final String TYPE_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_FABRICANT_FOURNISSEUR = "Fabricant";
        public static final int NUM_COL_FABRICANT_FOURNISSEUR = 72;
        public static final String TYPE_COL_FABRICANT_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_CONTACT_EMAIL_FOURNISSEUR = "Contact_Email";
        public static final int NUM_COL_CONTACT_EMAIL_FOURNISSEUR = 73;
        public static final String TYPE_COL_CONTACT_EMAIL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR = "Pharmaco_Vigilance_Email";
        public static final int NUM_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR = 74;
        public static final String TYPE_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_TRANSPORT_TYPE_FOURNISSEUR = "Transport_Type";
        public static final int NUM_COL_TRANSPORT_TYPE_FOURNISSEUR = 75;
        public static final String TYPE_COL_TRANSPORT_TYPE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_IMPORT_TRANSITAIRE_FOURNISSEUR = "import_transitaire";
        public static final int NUM_COL_IMPORT_TRANSITAIRE_FOURNISSEUR = 76;
        public static final String TYPE_COL_IMPORT_TRANSITAIRE_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_AFFECTATION_DETAILLEE_FOURNISSEUR = "Affectation_Detaillee";
        public static final int NUM_COL_AFFECTATION_DETAILLEE_FOURNISSEUR = 77;
        public static final String TYPE_COL_AFFECTATION_DETAILLEE_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR = "Transitaire_Metropolitain";
        public static final int NUM_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR = 78;
        public static final String TYPE_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_TRANSITAIRE_LOCAL_FOURNISSEUR = "Transitaire_Local";
        public static final int NUM_COL_TRANSITAIRE_LOCAL_FOURNISSEUR = 79;
        public static final String TYPE_COL_TRANSITAIRE_LOCAL_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_IMPORT_DDP_FOURNISSEUR = "Import_DDP";
        public static final int NUM_COL_IMPORT_DDP_FOURNISSEUR = 80;
        public static final String TYPE_COL_IMPORT_DDP_FOURNISSEUR = "INTEGER";
        public static final String CLE_COL_DEVISE_FACTURATION_FOURNISSEUR = "Devise_Facturation";
        public static final int NUM_COL_DEVISE_FACTURATION_FOURNISSEUR = 81;
        public static final String TYPE_COL_DEVISE_FACTURATION_FOURNISSEUR = "TEXT";
        public static final String CLE_COL_NUMERO_FOURNISSEUR = "Numero";
        public static final int NUM_COL_NUMERO_FOURNISSEUR = 82;
        public static final String TYPE_COL_NUMERO_FOURNISSEUR = "TEXT";


        public static final String CREATION_TABLE_FOURNISSEUR = "CREATE TABLE " + Constantes.TABLE_FOURNISSEUR
                + "("
                + DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + " " + DBOpenHelper.Constantes.TYPE_COL_phiwms_mobileUUID + " PRIMARY KEY,"
                + Constantes.CLE_COL_RAISONSOCIALE_FOURNISSEUR + " " + Constantes.TYPE_COL_RAISONSOCIALE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_ADRESSE1_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_ADRESSE1_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_ADRESSE2_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_ADRESSE2_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_CP_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_CP_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_VILLE_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_VILLE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_PAYS_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_PAYS_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_TELEPHONE_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_TELEPHONE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_FAX_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_FAX_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_CIVILITE_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_CIVILITE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_PRENOM_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_PRENOM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_NOM_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_NOM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMPTE_FOURNISSEUR + " " + Constantes.TYPE_COL_COMPTE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL__UID_FOURNISSEUR + " " + Constantes.TYPE_COL__UID_FOURNISSEUR + ","
                + Constantes.CLE_COL_EN_COURS_FOURNISSEUR + " " + Constantes.TYPE_COL_EN_COURS_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_SEUIL_ENCOURS_FOURNISSEUR + " " + Constantes.TYPE_COL_SEUIL_ENCOURS_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CA_FOURNISSEUR + " " + Constantes.TYPE_COL_CA_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_REFERENCECLIENT_FOURNISSEUR + " " + Constantes.TYPE_COL_REFERENCECLIENT_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_ADRESSE1_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_ADRESSE1_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CERTIFICATION_FOURNISSEUR + " " + Constantes.TYPE_COL_CERTIFICATION_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TAUX_ESCOMPTE1_FOURNISSEUR + " " + Constantes.TYPE_COL_TAUX_ESCOMPTE1_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_NBJ_ESCOMPTE1_FOURNISSEUR + " " + Constantes.TYPE_COL_NBJ_ESCOMPTE1_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TAUX_ESCOMPTE2_FOURNISSEUR + " " + Constantes.TYPE_COL_TAUX_ESCOMPTE2_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_NBJ_ESCOMPTE2_FOURNISSEUR + " " + Constantes.TYPE_COL_NBJ_ESCOMPTE2_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_GROUPE_FOURNISSEUR + " " + Constantes.TYPE_COL_GROUPE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_REFERANCEGROUPE_FOURNISSEUR + " " + Constantes.TYPE_COL_REFERANCEGROUPE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_MODEREGLEMENT_FOURNISSEUR + " " + Constantes.TYPE_COL_MODEREGLEMENT_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONDITIONSREGLEMENT_FOURNISSEUR + " " + Constantes.TYPE_COL_CONDITIONSREGLEMENT_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_ADRESSE2_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_ADRESSE2_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_TEL_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_TEL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_FAX_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_FAX_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_CP_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_CP_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_VILLE_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_VILLE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_SEUIL_POUR_FRANCO_FOURNISSEUR + " " + Constantes.TYPE_COL_SEUIL_POUR_FRANCO_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_WEB_FOURNISSEUR + " " + Constantes.TYPE_COL_WEB_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMANDE_EMAIL_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMANDE_EMAIL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_PORTABLE_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_PORTABLE_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_AFFECTATION_FOURNISSEUR + " " + Constantes.TYPE_COL_AFFECTATION_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_CIVILITE_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_CIVILITE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_PRENOM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_PRENOM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_NOM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_NOM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_ADRESSE1_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_ADRESSE1_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_ADRESSE2_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_ADRESSE2_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_CP_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_CP_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_VILLE_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_VILLE_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_TEL_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_TEL_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_FAX_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_FAX_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_EMAIL_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_EMAIL_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ST_PORTABLE_COM_FOURNISSEUR + " " + Constantes.TYPE_COL_ST_PORTABLE_COM_FOURNISSEUR + " ,"
                + Constantes.CLE_COL__SYS_DT_MAJ_FOURNISSEUR + " " + Constantes.TYPE_COL__SYS_DT_MAJ_FOURNISSEUR + " ,"
                + Constantes.CLE_COL__SYS_HEURE_MAJ_FOURNISSEUR + " " + Constantes.TYPE_COL__SYS_HEURE_MAJ_FOURNISSEUR + " ,"
                + Constantes.CLE_COL__SYS_USER_MAJ_FOURNISSEUR + " " + Constantes.TYPE_COL__SYS_USER_MAJ_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TYPE_FRANCO_FOURNISSEUR + " " + Constantes.TYPE_COL_TYPE_FRANCO_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_SAP_GROUPE_FOURNISSEUR + " " + Constantes.TYPE_COL_SAP_GROUPE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_SAP_COMPTE_FOURNISSEUR + " " + Constantes.TYPE_COL_SAP_COMPTE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DELAI_LIVRAISON_FOURNISSEUR + " " + Constantes.TYPE_COL_DELAI_LIVRAISON_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_MONTANT_MIN_FOURNISSEUR + " " + Constantes.TYPE_COL_MONTANT_MIN_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR + " " + Constantes.TYPE_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DISTRIBUTEUR_FOURNISSEUR + " " + Constantes.TYPE_COL_DISTRIBUTEUR_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_MONTANT_FRAIS_FOURNISSEUR + " " + Constantes.TYPE_COL_MONTANT_FRAIS_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR + " " + Constantes.TYPE_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_ARCHIVE_FOURNISSEUR + " " + Constantes.TYPE_COL_ARCHIVE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR + " " + Constantes.TYPE_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR + " " + Constantes.TYPE_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DOCUMENT_MARCHE_FOURNISSEUR + " " + Constantes.TYPE_COL_DOCUMENT_MARCHE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR + " " + Constantes.TYPE_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR + " " + Constantes.TYPE_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR + " " + Constantes.TYPE_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_RESPONSABLEACHAT_FOURNISSEUR + " " + Constantes.TYPE_COL_RESPONSABLEACHAT_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_LIVRAISONFREQUENCE_FOURNISSEUR + " " + Constantes.TYPE_COL_LIVRAISONFREQUENCE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR + " " + Constantes.TYPE_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_FABRICANT_FOURNISSEUR + " " + Constantes.TYPE_COL_FABRICANT_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_CONTACT_EMAIL_FOURNISSEUR + " " + Constantes.TYPE_COL_CONTACT_EMAIL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR + " " + Constantes.TYPE_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TRANSPORT_TYPE_FOURNISSEUR + " " + Constantes.TYPE_COL_TRANSPORT_TYPE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_IMPORT_TRANSITAIRE_FOURNISSEUR + " " + Constantes.TYPE_COL_IMPORT_TRANSITAIRE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_AFFECTATION_DETAILLEE_FOURNISSEUR + " " + Constantes.TYPE_COL_AFFECTATION_DETAILLEE_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR + " " + Constantes.TYPE_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_TRANSITAIRE_LOCAL_FOURNISSEUR + " " + Constantes.TYPE_COL_TRANSITAIRE_LOCAL_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_IMPORT_DDP_FOURNISSEUR + " " + Constantes.TYPE_COL_IMPORT_DDP_FOURNISSEUR + " ,"
                + Constantes.CLE_COL_DEVISE_FACTURATION_FOURNISSEUR + " " + Constantes.TYPE_COL_DEVISE_FACTURATION_FOURNISSEUR + ","
                + Constantes.CLE_COL_NUMERO_FOURNISSEUR + " " + Constantes.TYPE_COL_NUMERO_FOURNISSEUR
                + ");";
    }
}
