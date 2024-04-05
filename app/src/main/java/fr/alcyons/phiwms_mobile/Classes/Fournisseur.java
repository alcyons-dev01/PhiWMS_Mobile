package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 19/06/2017.
 */

public class Fournisseur implements Serializable, Comparable {

    private String Numero;
    private String raisonSociale;
    private String Commande_adresse1;
    private String Commande_adresse2;
    private String Commande_CP;
    private String Commande_Ville;
    private String Commande_Pays;
    private String Commande_Telephone;
    private String Commande_Fax;
    private String Contact_civilite;
    private String Contact_prenom;
    private String Contact_nom;
    private String compte;
    private int _UID;
    private double En_cours;
    private double Seuil_encours;
    private double CA;
    private String ReferenceClient;
    private String Contact_Adresse1;
    private String Certification;
    private double Taux_Escompte1;
    private double Nbj_escompte1;
    private double Taux_escompte2;
    private double Nbj_escompte2;
    private String Groupe;
    private String ReferanceGroupe;
    private String ModeReglement;
    private String ConditionsReglement;
    private String Contact_Adresse2;
    private String Contact_Tel;
    private String Contact_Fax;
    private String Contact_CP;
    private String Contact_Ville;
    private double Seuil_pour_franco;
    private String Web;
    private String Commande_Email;
    private String Contact_Portable_com;
    private String Affectation;
    private String ST_Civilite;
    private String ST_Prénom;
    private String ST_Nom;
    private String ST_Adresse1_com;
    private String ST_Adresse2_Com;
    private String ST_CP_com;
    private String ST_Ville_com;
    private String ST_Tel_com;
    private String ST_Fax_com;
    private String St_Email_com;
    private String ST_Portable_com;
    private String _SYS_DT_MAJ;
    private String _SYS_HEURE_MAJ;
    private String _SYS_USER_MAJ;
    private String Type_franco;
    private Boolean SAP_Groupe;
    private String SAp_Compte;
    private int Delai_Livraison;
    private double Montant_Min;
    private String Distrib_Raison_Sociale;
    private Boolean Distributeur;
    private double Montant_Frais;
    private String Pharmaco_Vigilance_Tel;
    private Boolean Archive;
    private String Commentaire_Commande_Edition;
    private String Commentaire_Cde_Confidentiel;
    private Boolean LivraisonDomicileAutoriser;
    private String document_marche;
    private String document_developpementDurable;
    private String document_certification;
    private String developpementDurable;
    private String responsableAchat;
    private String LivraisonFrequence;
    private String LivraisonJourSemaine;
    private Boolean Fabricant;
    private String Contact_Email;
    private String Pharmaco_Vigilance_Email;
    private String Transport_Type;
    private Boolean import_transitaire;
    private String Affectation_Detaillee;
    private String Transitaire_Metropolitain;
    private String Transitaire_Local;
    private Boolean Import_DDP;
    private String Devise_Facturation;
    private int phiMR4UUID = -1;

    public Fournisseur(int _UID, String numero, String raisonSociale)
    {
        this._UID = _UID;
        this.Numero = numero;
        this.raisonSociale = raisonSociale;
    }

    public Fournisseur(String numero, String raisonSociale, String commande_adresse1, String commande_adresse2, String commande_CP, String commande_Ville, String commande_Pays, String commande_Telephone, String commande_Fax, String contact_civilite, String contact_prenom, String contact_nom, String compte, int _UID, double en_cours, double seuil_encours, double CA, String referenceClient, String contact_Adresse1, String certification, double taux_Escompte1, double nbj_escompte1, double taux_escompte2, double nbj_escompte2, String groupe, String referanceGroupe, String modeReglement, String conditionsReglement, String contact_Adresse2, String contact_Tel, String contact_Fax, String contact_CP, String contact_Ville, double seuil_pour_franco, String web, String commande_Email, String contact_Portable_com, String affectation, String ST_Civilite, String ST_Prénom, String ST_Nom, String ST_Adresse1_com, String ST_Adresse2_Com, String ST_CP_com, String ST_Ville_com, String ST_Tel_com, String ST_Fax_com, String st_Email_com, String ST_Portable_com, String _SYS_DT_MAJ, String _SYS_HEURE_MAJ, String _SYS_USER_MAJ, String type_franco, Boolean SAP_Groupe, String SAp_Compte, int delai_Livraison, double montant_Min, String distrib_Raison_Sociale, Boolean distributeur, double montant_Frais, String pharmaco_Vigilance_Tel, Boolean archive, String commentaire_Commande_Edition, String commentaire_Cde_Confidentiel, Boolean livraisonDomicileAutoriser, String document_marche, String document_developpementDurable, String document_certification, String developpementDurable, String responsableAchat, String livraisonFrequence, String livraisonJourSemaine, Boolean fabricant, String contact_Email, String pharmaco_Vigilance_Email, String transport_Type, Boolean import_transitaire, String affectation_Detaillee, String transitaire_Metropolitain, String transitaire_Local, Boolean import_DDP, String devise_Facturation) {
        this.Numero = numero;
        this.raisonSociale = raisonSociale;
        this.Commande_adresse1 = commande_adresse1;
        this.Commande_adresse2 = commande_adresse2;
        this.Commande_CP = commande_CP;
        this.Commande_Ville = commande_Ville;
        this.Commande_Pays = commande_Pays;
        this.Commande_Telephone = commande_Telephone;
        this.Commande_Fax = commande_Fax;
        this.Contact_civilite = contact_civilite;
        this.Contact_prenom = contact_prenom;
        this.Contact_nom = contact_nom;
        this.compte = compte;
        this._UID = _UID;
        this.En_cours = en_cours;
        this.Seuil_encours = seuil_encours;
        this.CA = CA;
        this.ReferenceClient = referenceClient;
        this.Contact_Adresse1 = contact_Adresse1;
        this.Certification = certification;
        this.Taux_Escompte1 = taux_Escompte1;
        this.Nbj_escompte1 = nbj_escompte1;
        this.Taux_escompte2 = taux_escompte2;
        this.Nbj_escompte2 = nbj_escompte2;
        this.Groupe = groupe;
        this.ReferanceGroupe = referanceGroupe;
        this.ModeReglement = modeReglement;
        this.ConditionsReglement = conditionsReglement;
        this.Contact_Adresse2 = contact_Adresse2;
        this.Contact_Tel = contact_Tel;
        this.Contact_Fax = contact_Fax;
        this.Contact_CP = contact_CP;
        this.Contact_Ville = contact_Ville;
        this.Seuil_pour_franco = seuil_pour_franco;
        this.Web = web;
        this.Commande_Email = commande_Email;
        this.Contact_Portable_com = contact_Portable_com;
        this.Affectation = affectation;
        this.ST_Civilite = ST_Civilite;
        this.ST_Prénom = ST_Prénom;
        this.ST_Nom = ST_Nom;
        this.ST_Adresse1_com = ST_Adresse1_com;
        this.ST_Adresse2_Com = ST_Adresse2_Com;
        this.ST_CP_com = ST_CP_com;
        this.ST_Ville_com = ST_Ville_com;
        this.ST_Tel_com = ST_Tel_com;
        this.ST_Fax_com = ST_Fax_com;
        this.St_Email_com = st_Email_com;
        this.ST_Portable_com = ST_Portable_com;
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
        this.Type_franco = type_franco;
        this.SAP_Groupe = SAP_Groupe;
        this.SAp_Compte = SAp_Compte;
        this.Delai_Livraison = delai_Livraison;
        this.Montant_Min = montant_Min;
        this.Distrib_Raison_Sociale = distrib_Raison_Sociale;
        this.Distributeur = distributeur;
        this.Montant_Frais = montant_Frais;
        this.Pharmaco_Vigilance_Tel = pharmaco_Vigilance_Tel;
        this.Archive = archive;
        this.Commentaire_Commande_Edition = commentaire_Commande_Edition;
        this.Commentaire_Cde_Confidentiel = commentaire_Cde_Confidentiel;
        this.LivraisonDomicileAutoriser = livraisonDomicileAutoriser;
        this.document_marche = document_marche;
        this.document_developpementDurable = document_developpementDurable;
        this.document_certification = document_certification;
        this.developpementDurable = developpementDurable;
        this.responsableAchat = responsableAchat;
        this.LivraisonFrequence = livraisonFrequence;
        this.LivraisonJourSemaine = livraisonJourSemaine;
        this.Fabricant = fabricant;
        this.Contact_Email = contact_Email;
        this.Pharmaco_Vigilance_Email = pharmaco_Vigilance_Email;
        this.Transport_Type = transport_Type;
        this.import_transitaire = import_transitaire;
        this.Affectation_Detaillee = affectation_Detaillee;
        this.Transitaire_Metropolitain = transitaire_Metropolitain;
        this.Transitaire_Local = transitaire_Local;
        this.Import_DDP = import_DDP;
        this.Devise_Facturation = devise_Facturation;
    }

    public Fournisseur(JSONObject fournisseurJson) {
        try {
            this.Numero = OutilsGestionClasses.recupererString(fournisseurJson.getString("Numero"));
            this.raisonSociale = OutilsGestionClasses.recupererString(fournisseurJson.getString("raisonSociale"));
            this.Commande_adresse1 = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_adresse1"));
            this.Commande_adresse2 = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_adresse2"));
            this.Commande_CP = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_CP"));
            this.Commande_Ville = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_Ville"));
            this.Commande_Pays = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_Pays"));
            this.Commande_Telephone = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_Telephone"));
            this.Commande_Fax = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_Fax"));
            this.Contact_civilite = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_civilite"));
            this.Contact_prenom = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_prenom"));
            this.Contact_nom = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_nom"));
            this.compte = OutilsGestionClasses.recupererString(fournisseurJson.getString("compte"));
            this._UID = fournisseurJson.getInt("_UID");
            this.En_cours = fournisseurJson.getDouble("En_cours");
            this.Seuil_encours = fournisseurJson.getDouble("Seuil_encours");
            this.CA = fournisseurJson.getDouble("CA");
            this.ReferenceClient = OutilsGestionClasses.recupererString(fournisseurJson.getString("ReferenceClient"));
            this.Contact_Adresse1 = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Adresse1"));
            this.Certification = OutilsGestionClasses.recupererString(fournisseurJson.getString("Certification"));
            this.Taux_Escompte1 = fournisseurJson.getDouble("Taux_Escompte1");
            this.Nbj_escompte1 = fournisseurJson.getDouble("Nbj_escompte1");
            this.Taux_escompte2 = fournisseurJson.getDouble("Taux_escompte2");
            this.Nbj_escompte2 = fournisseurJson.getDouble("Nbj_escompte2");
            this.Groupe = OutilsGestionClasses.recupererString(fournisseurJson.getString("Groupe"));
            this.ReferanceGroupe = OutilsGestionClasses.recupererString(fournisseurJson.getString("ReferanceGroupe"));
            this.ModeReglement = OutilsGestionClasses.recupererString(fournisseurJson.getString("ModeReglement"));
            this.ConditionsReglement = OutilsGestionClasses.recupererString(fournisseurJson.getString("ConditionsReglement"));
            this.Contact_Adresse2 = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Adresse2"));
            this.Contact_Tel = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Tel"));
            this.Contact_Fax = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Fax"));
            this.Contact_CP = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_CP"));
            this.Contact_Ville = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Ville"));
            this.Seuil_pour_franco = fournisseurJson.getDouble("Seuil_pour_franco");
            this.Web = OutilsGestionClasses.recupererString(fournisseurJson.getString("Web"));
            this.Commande_Email = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commande_Email"));
            this.Contact_Portable_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Portable_com"));
            this.Affectation = OutilsGestionClasses.recupererString(fournisseurJson.getString("Affectation"));
            this.ST_Civilite = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Civilite"));
            this.ST_Prénom = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Prénom"));
            this.ST_Nom = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Nom"));
            this.ST_Adresse1_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Adresse1_com"));
            this.ST_Adresse2_Com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Adresse2_Com"));
            this.ST_CP_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_CP_com"));
            this.ST_Ville_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Ville_com"));
            this.ST_Tel_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Tel_com"));
            this.ST_Fax_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Fax_com"));
            this.St_Email_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("St_Email_com"));
            this.ST_Portable_com = OutilsGestionClasses.recupererString(fournisseurJson.getString("ST_Portable_com"));
            this._SYS_DT_MAJ = OutilsGestionClasses.recupererString(fournisseurJson.getString("_SYS_DT_MAJ"));
            this._SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(fournisseurJson.getString("_SYS_HEURE_MAJ"));
            this._SYS_USER_MAJ = OutilsGestionClasses.recupererString(fournisseurJson.getString("_SYS_USER_MAJ"));
            this.Type_franco = OutilsGestionClasses.recupererString(fournisseurJson.getString("Type_franco"));
            this.SAP_Groupe = OutilsGestionClasses.recupererBooleen(fournisseurJson, "SAP_Groupe");
            this.SAp_Compte = OutilsGestionClasses.recupererString(fournisseurJson.getString("SAp_Compte"));
            this.Delai_Livraison = fournisseurJson.getInt("Delai_Livraison");
            this.Montant_Min = fournisseurJson.getDouble("Montant_Min");
            this.Distrib_Raison_Sociale = OutilsGestionClasses.recupererString(fournisseurJson.getString("Distrib_Raison_Sociale"));
            this.Distributeur = OutilsGestionClasses.recupererBooleen(fournisseurJson, "Distributeur");
            this.Montant_Frais = fournisseurJson.getDouble("Montant_Frais");
            this.Pharmaco_Vigilance_Tel = OutilsGestionClasses.recupererString(fournisseurJson.getString("Pharmaco_Vigilance_Tel"));
            this.Archive = OutilsGestionClasses.recupererBooleen(fournisseurJson, "Archive");
            this.Commentaire_Commande_Edition = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commentaire_Commande_Edition"));
            this.Commentaire_Cde_Confidentiel = OutilsGestionClasses.recupererString(fournisseurJson.getString("Commentaire_Cde_Confidentiel"));
            this.LivraisonDomicileAutoriser = OutilsGestionClasses.recupererBooleen(fournisseurJson, "LivraisonDomicileAutoriser");
            this.document_marche = OutilsGestionClasses.recupererString(fournisseurJson.getString("document_marche"));
            this.document_developpementDurable = OutilsGestionClasses.recupererString(fournisseurJson.getString("document_developpementDurable"));
            this.document_certification = OutilsGestionClasses.recupererString(fournisseurJson.getString("document_certification"));
            this.developpementDurable = OutilsGestionClasses.recupererString(fournisseurJson.getString("developpementDurable"));
            this.responsableAchat = OutilsGestionClasses.recupererString(fournisseurJson.getString("responsableAchat"));
            this.LivraisonFrequence = OutilsGestionClasses.recupererString(fournisseurJson.getString("LivraisonFrequence"));
            this.LivraisonJourSemaine = OutilsGestionClasses.recupererString(fournisseurJson.getString("LivraisonJourSemaine"));
            this.Fabricant = OutilsGestionClasses.recupererBooleen(fournisseurJson, "Fabricant");
            this.Contact_Email = OutilsGestionClasses.recupererString(fournisseurJson.getString("Contact_Email"));
            this.Pharmaco_Vigilance_Email = OutilsGestionClasses.recupererString(fournisseurJson.getString("Pharmaco_Vigilance_Email"));
            this.Transport_Type = OutilsGestionClasses.recupererString(fournisseurJson.getString("Transport_Type"));
            this.import_transitaire = OutilsGestionClasses.recupererBooleen(fournisseurJson, "import_transitaire");
            this.Affectation_Detaillee = OutilsGestionClasses.recupererString(fournisseurJson.getString("Affectation_Detaillee"));
            this.Transitaire_Metropolitain = OutilsGestionClasses.recupererString(fournisseurJson.getString("Transitaire_Metropolitain"));
            this.Transitaire_Local = OutilsGestionClasses.recupererString(fournisseurJson.getString("Transitaire_Local"));
            this.Import_DDP = OutilsGestionClasses.recupererBooleen(fournisseurJson, "Import_DDP");
            this.Devise_Facturation = OutilsGestionClasses.recupererString(fournisseurJson.getString("Devise_Facturation"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Fournisseur(Cursor cursorFournisseur) {
        this.Numero = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_NUMERO_FOURNISSEUR);
        this.raisonSociale = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_RAISONSOCIALE_FOURNISSEUR);
        this.Commande_adresse1 = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_ADRESSE1_FOURNISSEUR);
        this.Commande_adresse2 = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_ADRESSE2_FOURNISSEUR);
        this.Commande_CP = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_CP_FOURNISSEUR);
        this.Commande_Ville = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_VILLE_FOURNISSEUR);
        this.Commande_Pays = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_PAYS_FOURNISSEUR);
        this.Commande_Telephone = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_TELEPHONE_FOURNISSEUR);
        this.Commande_Fax = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_FAX_FOURNISSEUR);
        this.Contact_civilite = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_CIVILITE_FOURNISSEUR);
        this.Contact_prenom = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_PRENOM_FOURNISSEUR);
        this.Contact_nom = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_NOM_FOURNISSEUR);
        this.compte = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMPTE_FOURNISSEUR);
        this._UID = cursorFournisseur.getInt(FournisseurOpenHelper.Constantes.NUM_COL__UID_FOURNISSEUR);
        this.En_cours = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_EN_COURS_FOURNISSEUR);
        this.Seuil_encours = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_SEUIL_ENCOURS_FOURNISSEUR);
        this.CA = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_CA_FOURNISSEUR);
        this.ReferenceClient = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_REFERENCECLIENT_FOURNISSEUR);
        this.Contact_Adresse1 = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_ADRESSE1_FOURNISSEUR);
        this.Certification = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CERTIFICATION_FOURNISSEUR);
        this.Taux_Escompte1 = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_TAUX_ESCOMPTE1_FOURNISSEUR);
        this.Nbj_escompte1 = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_NBJ_ESCOMPTE1_FOURNISSEUR);
        this.Taux_escompte2 = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_TAUX_ESCOMPTE2_FOURNISSEUR);
        this.Nbj_escompte2 = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_NBJ_ESCOMPTE2_FOURNISSEUR);
        this.Groupe = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_GROUPE_FOURNISSEUR);
        this.ReferanceGroupe = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_REFERANCEGROUPE_FOURNISSEUR);
        this.ModeReglement = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_MODEREGLEMENT_FOURNISSEUR);
        this.ConditionsReglement = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONDITIONSREGLEMENT_FOURNISSEUR);
        this.Contact_Adresse2 = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_ADRESSE2_FOURNISSEUR);
        this.Contact_Tel = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_TEL_FOURNISSEUR);
        this.Contact_Fax = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_FAX_FOURNISSEUR);
        this.Contact_CP = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_CP_FOURNISSEUR);
        this.Contact_Ville = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_VILLE_FOURNISSEUR);
        this.Seuil_pour_franco = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_SEUIL_POUR_FRANCO_FOURNISSEUR);
        this.Web = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_WEB_FOURNISSEUR);
        this.Commande_Email = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMANDE_EMAIL_FOURNISSEUR);
        this.Contact_Portable_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_PORTABLE_COM_FOURNISSEUR);
        this.Affectation = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_AFFECTATION_FOURNISSEUR);
        this.ST_Civilite = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_CIVILITE_FOURNISSEUR);
        this.ST_Prénom = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_PRENOM_FOURNISSEUR);
        this.ST_Nom = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_NOM_FOURNISSEUR);
        this.ST_Adresse1_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_ADRESSE1_COM_FOURNISSEUR);
        this.ST_Adresse2_Com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_ADRESSE2_COM_FOURNISSEUR);
        this.ST_CP_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_CP_COM_FOURNISSEUR);
        this.ST_Ville_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_VILLE_COM_FOURNISSEUR);
        this.ST_Tel_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_TEL_COM_FOURNISSEUR);
        this.ST_Fax_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_FAX_COM_FOURNISSEUR);
        this.St_Email_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_EMAIL_COM_FOURNISSEUR);
        this.ST_Portable_com = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_ST_PORTABLE_COM_FOURNISSEUR);
        this._SYS_DT_MAJ = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL__SYS_DT_MAJ_FOURNISSEUR);
        this._SYS_HEURE_MAJ = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL__SYS_HEURE_MAJ_FOURNISSEUR);
        this._SYS_USER_MAJ = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL__SYS_USER_MAJ_FOURNISSEUR);
        this.Type_franco = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_TYPE_FRANCO_FOURNISSEUR);
        this.SAP_Groupe = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_SAP_GROUPE_FOURNISSEUR);
        this.SAp_Compte = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_SAP_COMPTE_FOURNISSEUR);
        this.Delai_Livraison = cursorFournisseur.getInt(FournisseurOpenHelper.Constantes.NUM_COL_DELAI_LIVRAISON_FOURNISSEUR);
        this.Montant_Min = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_MONTANT_MIN_FOURNISSEUR);
        this.Distrib_Raison_Sociale = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DISTRIB_RAISON_SOCIALE_FOURNISSEUR);
        this.Distributeur = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_DISTRIBUTEUR_FOURNISSEUR);
        this.Montant_Frais = cursorFournisseur.getDouble(FournisseurOpenHelper.Constantes.NUM_COL_MONTANT_FRAIS_FOURNISSEUR);
        this.Pharmaco_Vigilance_Tel = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_PHARMACO_VIGILANCE_TEL_FOURNISSEUR);
        this.Archive = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_ARCHIVE_FOURNISSEUR);
        this.Commentaire_Commande_Edition = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMENTAIRE_COMMANDE_EDITION_FOURNISSEUR);
        this.Commentaire_Cde_Confidentiel = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_COMMENTAIRE_CDE_CONFIDENTIEL_FOURNISSEUR);
        this.LivraisonDomicileAutoriser = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_LIVRAISONDOMICILEAUTORISER_FOURNISSEUR);
        this.document_marche = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DOCUMENT_MARCHE_FOURNISSEUR);
        this.document_developpementDurable = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DOCUMENT_DEVELOPPEMENTDURABLE_FOURNISSEUR);
        this.document_certification = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DOCUMENT_CERTIFICATION_FOURNISSEUR);
        this.developpementDurable = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DEVELOPPEMENTDURABLE_FOURNISSEUR);
        this.responsableAchat = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_RESPONSABLEACHAT_FOURNISSEUR);
        this.LivraisonFrequence = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_LIVRAISONFREQUENCE_FOURNISSEUR);
        this.LivraisonJourSemaine = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_LIVRAISONJOURSEMAINE_FOURNISSEUR);
        this.Fabricant = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_FABRICANT_FOURNISSEUR);
        this.Contact_Email = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_CONTACT_EMAIL_FOURNISSEUR);
        this.Pharmaco_Vigilance_Email = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_PHARMACO_VIGILANCE_EMAIL_FOURNISSEUR);
        this.Transport_Type = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_TRANSPORT_TYPE_FOURNISSEUR);
        this.import_transitaire = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_IMPORT_TRANSITAIRE_FOURNISSEUR);
        this.Affectation_Detaillee = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_AFFECTATION_DETAILLEE_FOURNISSEUR);
        this.Transitaire_Metropolitain = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_TRANSITAIRE_METROPOLITAIN_FOURNISSEUR);
        this.Transitaire_Local = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_TRANSITAIRE_LOCAL_FOURNISSEUR);
        this.Import_DDP = OutilsGestionClasses.recupererBooleen(cursorFournisseur, FournisseurOpenHelper.Constantes.NUM_COL_IMPORT_DDP_FOURNISSEUR);
        this.Devise_Facturation = cursorFournisseur.getString(FournisseurOpenHelper.Constantes.NUM_COL_DEVISE_FACTURATION_FOURNISSEUR);
        this.phiMR4UUID = cursorFournisseur.getInt(DBOpenHelper.Constantes.NUM_COL_PHIMR4UUID);
    }

    public int getPhiMR4UUID() {
        return phiMR4UUID;
    }

    public void setPhiMR4UUID(int phiMR4UUID) {
        this.phiMR4UUID = phiMR4UUID;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getCommande_adresse1() {
        return Commande_adresse1;
    }

    public void setCommande_adresse1(String commande_adresse1) {
        Commande_adresse1 = commande_adresse1;
    }

    public String getCommande_adresse2() {
        return Commande_adresse2;
    }

    public void setCommande_adresse2(String commande_adresse2) {
        Commande_adresse2 = commande_adresse2;
    }

    public String getCommande_CP() {
        return Commande_CP;
    }

    public void setCommande_CP(String commande_CP) {
        Commande_CP = commande_CP;
    }

    public String getCommande_Ville() {
        return Commande_Ville;
    }

    public void setCommande_Ville(String commande_Ville) {
        Commande_Ville = commande_Ville;
    }

    public String getCommande_Pays() {
        return Commande_Pays;
    }

    public void setCommande_Pays(String commande_Pays) {
        Commande_Pays = commande_Pays;
    }

    public String getCommande_Telephone() {
        return Commande_Telephone;
    }

    public void setCommande_Telephone(String commande_Telephone) {
        Commande_Telephone = commande_Telephone;
    }

    public String getCommande_Fax() {
        return Commande_Fax;
    }

    public void setCommande_Fax(String commande_Fax) {
        Commande_Fax = commande_Fax;
    }

    public String getContact_civilite() {
        return Contact_civilite;
    }

    public void setContact_civilite(String contact_civilite) {
        Contact_civilite = contact_civilite;
    }

    public String getContact_prenom() {
        return Contact_prenom;
    }

    public void setContact_prenom(String contact_prenom) {
        Contact_prenom = contact_prenom;
    }

    public String getContact_nom() {
        return Contact_nom;
    }

    public void setContact_nom(String contact_nom) {
        Contact_nom = contact_nom;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public int get_UID() {
        return _UID;
    }

    public void set_UID(int _UID) {
        this._UID = _UID;
    }

    public double getEn_cours() {
        return En_cours;
    }

    public void setEn_cours(double en_cours) {
        En_cours = en_cours;
    }

    public double getSeuil_encours() {
        return Seuil_encours;
    }

    public void setSeuil_encours(double seuil_encours) {
        Seuil_encours = seuil_encours;
    }

    public double getCA() {
        return CA;
    }

    public void setCA(double CA) {
        this.CA = CA;
    }

    public String getReferenceClient() {
        return ReferenceClient;
    }

    public void setReferenceClient(String referenceClient) {
        ReferenceClient = referenceClient;
    }

    public String getContact_Adresse1() {
        return Contact_Adresse1;
    }

    public void setContact_Adresse1(String contact_Adresse1) {
        Contact_Adresse1 = contact_Adresse1;
    }

    public String getCertification() {
        return Certification;
    }

    public void setCertification(String certification) {
        Certification = certification;
    }

    public double getTaux_Escompte1() {
        return Taux_Escompte1;
    }

    public void setTaux_Escompte1(double taux_Escompte1) {
        Taux_Escompte1 = taux_Escompte1;
    }

    public double getNbj_escompte1() {
        return Nbj_escompte1;
    }

    public void setNbj_escompte1(double nbj_escompte1) {
        Nbj_escompte1 = nbj_escompte1;
    }

    public double getTaux_escompte2() {
        return Taux_escompte2;
    }

    public void setTaux_escompte2(double taux_escompte2) {
        Taux_escompte2 = taux_escompte2;
    }

    public double getNbj_escompte2() {
        return Nbj_escompte2;
    }

    public void setNbj_escompte2(double nbj_escompte2) {
        Nbj_escompte2 = nbj_escompte2;
    }

    public String getGroupe() {
        return Groupe;
    }

    public void setGroupe(String groupe) {
        Groupe = groupe;
    }

    public String getReferanceGroupe() {
        return ReferanceGroupe;
    }

    public void setReferanceGroupe(String referanceGroupe) {
        ReferanceGroupe = referanceGroupe;
    }

    public String getModeReglement() {
        return ModeReglement;
    }

    public void setModeReglement(String modeReglement) {
        ModeReglement = modeReglement;
    }

    public String getConditionsReglement() {
        return ConditionsReglement;
    }

    public void setConditionsReglement(String conditionsReglement) {
        ConditionsReglement = conditionsReglement;
    }

    public String getContact_Adresse2() {
        return Contact_Adresse2;
    }

    public void setContact_Adresse2(String contact_Adresse2) {
        Contact_Adresse2 = contact_Adresse2;
    }

    public String getContact_Tel() {
        return Contact_Tel;
    }

    public void setContact_Tel(String contact_Tel) {
        Contact_Tel = contact_Tel;
    }

    public String getContact_Fax() {
        return Contact_Fax;
    }

    public void setContact_Fax(String contact_Fax) {
        Contact_Fax = contact_Fax;
    }

    public String getContact_CP() {
        return Contact_CP;
    }

    public void setContact_CP(String contact_CP) {
        Contact_CP = contact_CP;
    }

    public String getContact_Ville() {
        return Contact_Ville;
    }

    public void setContact_Ville(String contact_Ville) {
        Contact_Ville = contact_Ville;
    }

    public double getSeuil_pour_franco() {
        return Seuil_pour_franco;
    }

    public void setSeuil_pour_franco(double seuil_pour_franco) {
        Seuil_pour_franco = seuil_pour_franco;
    }

    public String getWeb() {
        return Web;
    }

    public void setWeb(String web) {
        Web = web;
    }

    public String getCommande_Email() {
        return Commande_Email;
    }

    public void setCommande_Email(String commande_Email) {
        Commande_Email = commande_Email;
    }

    public String getContact_Portable_com() {
        return Contact_Portable_com;
    }

    public void setContact_Portable_com(String contact_Portable_com) {
        Contact_Portable_com = contact_Portable_com;
    }

    public String getAffectation() {
        return Affectation;
    }

    public void setAffectation(String affectation) {
        Affectation = affectation;
    }

    public String getST_Civilite() {
        return ST_Civilite;
    }

    public void setST_Civilite(String ST_Civilite) {
        this.ST_Civilite = ST_Civilite;
    }

    public String getST_Prénom() {
        return ST_Prénom;
    }

    public void setST_Prénom(String ST_Prénom) {
        this.ST_Prénom = ST_Prénom;
    }

    public String getST_Nom() {
        return ST_Nom;
    }

    public void setST_Nom(String ST_Nom) {
        this.ST_Nom = ST_Nom;
    }

    public String getST_Adresse1_com() {
        return ST_Adresse1_com;
    }

    public void setST_Adresse1_com(String ST_Adresse1_com) {
        this.ST_Adresse1_com = ST_Adresse1_com;
    }

    public String getST_Adresse2_Com() {
        return ST_Adresse2_Com;
    }

    public void setST_Adresse2_Com(String ST_Adresse2_Com) {
        this.ST_Adresse2_Com = ST_Adresse2_Com;
    }

    public String getST_CP_com() {
        return ST_CP_com;
    }

    public void setST_CP_com(String ST_CP_com) {
        this.ST_CP_com = ST_CP_com;
    }

    public String getST_Ville_com() {
        return ST_Ville_com;
    }

    public void setST_Ville_com(String ST_Ville_com) {
        this.ST_Ville_com = ST_Ville_com;
    }

    public String getST_Tel_com() {
        return ST_Tel_com;
    }

    public void setST_Tel_com(String ST_Tel_com) {
        this.ST_Tel_com = ST_Tel_com;
    }

    public String getST_Fax_com() {
        return ST_Fax_com;
    }

    public void setST_Fax_com(String ST_Fax_com) {
        this.ST_Fax_com = ST_Fax_com;
    }

    public String getSt_Email_com() {
        return St_Email_com;
    }

    public void setSt_Email_com(String st_Email_com) {
        St_Email_com = st_Email_com;
    }

    public String getST_Portable_com() {
        return ST_Portable_com;
    }

    public void setST_Portable_com(String ST_Portable_com) {
        this.ST_Portable_com = ST_Portable_com;
    }

    public String get_SYS_DT_MAJ() {
        return _SYS_DT_MAJ;
    }

    public void set_SYS_DT_MAJ(String _SYS_DT_MAJ) {
        this._SYS_DT_MAJ = _SYS_DT_MAJ;
    }

    public String get_SYS_HEURE_MAJ() {
        return _SYS_HEURE_MAJ;
    }

    public void set_SYS_HEURE_MAJ(String _SYS_HEURE_MAJ) {
        this._SYS_HEURE_MAJ = _SYS_HEURE_MAJ;
    }

    public String get_SYS_USER_MAJ() {
        return _SYS_USER_MAJ;
    }

    public void set_SYS_USER_MAJ(String _SYS_USER_MAJ) {
        this._SYS_USER_MAJ = _SYS_USER_MAJ;
    }

    public String getType_franco() {
        return Type_franco;
    }

    public void setType_franco(String type_franco) {
        Type_franco = type_franco;
    }

    public Boolean getSAP_Groupe() {
        return SAP_Groupe;
    }

    public void setSAP_Groupe(Boolean SAP_Groupe) {
        this.SAP_Groupe = SAP_Groupe;
    }

    public String getSAp_Compte() {
        return SAp_Compte;
    }

    public void setSAp_Compte(String SAp_Compte) {
        this.SAp_Compte = SAp_Compte;
    }

    public int getDelai_Livraison() {
        return Delai_Livraison;
    }

    public void setDelai_Livraison(int delai_Livraison) {
        Delai_Livraison = delai_Livraison;
    }

    public double getMontant_Min() {
        return Montant_Min;
    }

    public void setMontant_Min(double montant_Min) {
        Montant_Min = montant_Min;
    }

    public String getDistrib_Raison_Sociale() {
        return Distrib_Raison_Sociale;
    }

    public void setDistrib_Raison_Sociale(String distrib_Raison_Sociale) {
        Distrib_Raison_Sociale = distrib_Raison_Sociale;
    }

    public Boolean getDistributeur() {
        return Distributeur;
    }

    public void setDistributeur(Boolean distributeur) {
        Distributeur = distributeur;
    }

    public double getMontant_Frais() {
        return Montant_Frais;
    }

    public void setMontant_Frais(double montant_Frais) {
        Montant_Frais = montant_Frais;
    }

    public String getPharmaco_Vigilance_Tel() {
        return Pharmaco_Vigilance_Tel;
    }

    public void setPharmaco_Vigilance_Tel(String pharmaco_Vigilance_Tel) {
        Pharmaco_Vigilance_Tel = pharmaco_Vigilance_Tel;
    }

    public Boolean getArchive() {
        return Archive;
    }

    public void setArchive(Boolean archive) {
        Archive = archive;
    }

    public String getCommentaire_Commande_Edition() {
        return Commentaire_Commande_Edition;
    }

    public void setCommentaire_Commande_Edition(String commentaire_Commande_Edition) {
        Commentaire_Commande_Edition = commentaire_Commande_Edition;
    }

    public String getCommentaire_Cde_Confidentiel() {
        return Commentaire_Cde_Confidentiel;
    }

    public void setCommentaire_Cde_Confidentiel(String commentaire_Cde_Confidentiel) {
        Commentaire_Cde_Confidentiel = commentaire_Cde_Confidentiel;
    }

    public Boolean getLivraisonDomicileAutoriser() {
        return LivraisonDomicileAutoriser;
    }

    public void setLivraisonDomicileAutoriser(Boolean livraisonDomicileAutoriser) {
        LivraisonDomicileAutoriser = livraisonDomicileAutoriser;
    }

    public String getDocument_marche() {
        return document_marche;
    }

    public void setDocument_marche(String document_marche) {
        this.document_marche = document_marche;
    }

    public String getDocument_developpementDurable() {
        return document_developpementDurable;
    }

    public void setDocument_developpementDurable(String document_developpementDurable) {
        this.document_developpementDurable = document_developpementDurable;
    }

    public String getDocument_certification() {
        return document_certification;
    }

    public void setDocument_certification(String document_certification) {
        this.document_certification = document_certification;
    }

    public String getDeveloppementDurable() {
        return developpementDurable;
    }

    public void setDeveloppementDurable(String developpementDurable) {
        this.developpementDurable = developpementDurable;
    }

    public String getResponsableAchat() {
        return responsableAchat;
    }

    public void setResponsableAchat(String responsableAchat) {
        this.responsableAchat = responsableAchat;
    }

    public String getLivraisonFrequence() {
        return LivraisonFrequence;
    }

    public void setLivraisonFrequence(String livraisonFrequence) {
        LivraisonFrequence = livraisonFrequence;
    }

    public String getLivraisonJourSemaine() {
        return LivraisonJourSemaine;
    }

    public void setLivraisonJourSemaine(String livraisonJourSemaine) {
        LivraisonJourSemaine = livraisonJourSemaine;
    }

    public Boolean getFabricant() {
        return Fabricant;
    }

    public void setFabricant(Boolean fabricant) {
        Fabricant = fabricant;
    }

    public String getContact_Email() {
        return Contact_Email;
    }

    public void setContact_Email(String contact_Email) {
        Contact_Email = contact_Email;
    }

    public String getPharmaco_Vigilance_Email() {
        return Pharmaco_Vigilance_Email;
    }

    public void setPharmaco_Vigilance_Email(String pharmaco_Vigilance_Email) {
        Pharmaco_Vigilance_Email = pharmaco_Vigilance_Email;
    }

    public String getTransport_Type() {
        return Transport_Type;
    }

    public void setTransport_Type(String transport_Type) {
        Transport_Type = transport_Type;
    }

    public Boolean getImport_transitaire() {
        return import_transitaire;
    }

    public void setImport_transitaire(Boolean import_transitaire) {
        this.import_transitaire = import_transitaire;
    }

    public String getAffectation_Detaillee() {
        return Affectation_Detaillee;
    }

    public void setAffectation_Detaillee(String affectation_Detaillee) {
        Affectation_Detaillee = affectation_Detaillee;
    }

    public String getTransitaire_Metropolitain() {
        return Transitaire_Metropolitain;
    }

    public void setTransitaire_Metropolitain(String transitaire_Metropolitain) {
        Transitaire_Metropolitain = transitaire_Metropolitain;
    }

    public String getTransitaire_Local() {
        return Transitaire_Local;
    }

    public void setTransitaire_Local(String transitaire_Local) {
        Transitaire_Local = transitaire_Local;
    }

    public Boolean getImport_DDP() {
        return Import_DDP;
    }

    public void setImport_DDP(Boolean import_DDP) {
        Import_DDP = import_DDP;
    }

    public String getDevise_Facturation() {
        return Devise_Facturation;
    }

    public void setDevise_Facturation(String devise_Facturation) {
        Devise_Facturation = devise_Facturation;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Fournisseur)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }

    @Override
    public int compareTo(Object obj) {
        Fournisseur fournisseur = (Fournisseur) obj;

        if (this.getPhiMR4UUID() == fournisseur.getPhiMR4UUID()) {
            return 0;
        } else {
            return this.get_UID() > fournisseur.get_UID() ? 1 : -1;
        }
    }
}
