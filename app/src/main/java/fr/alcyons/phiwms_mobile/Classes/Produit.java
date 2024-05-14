package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

public class Produit implements Serializable, Comparable {
    public boolean selected;
    public boolean URGENCE;

    private int ID_produit;
    private String N_interne;
    private String Designation_ext;
    private String Categorie;
    private double Taux_de_TVA;
    private boolean Peremption;
    private boolean Marche_Obtenu;
    private boolean Ordonnance;
    private int Cond_achat;
    private double Cond_distrib;
    private double Prix_unitaire;
    private double Seuil_alerte;
    private double Qte_Reassort;
    private boolean Suivi_Lot;
    private int Valeur_stock_actuel;
    private String Zone_PUI_Defaut;
    private String Ref_fourni;
    private int Code_fourn;
    private String Fournisseur;
    private String Unite;
    private boolean Reassort;
    private double Stock_clot;
    private String Ville;
    private String Designation_interne;
    private String Type_erreur;
    private String A_corriger;
    private boolean Inclu_au_panel;
    private String Forme;
    private String Reassort_Statut;
    private boolean Sterile;
    private int Duree_peremption;
    private String Conservation;
    private String Mode_de_Distribution;
    private boolean Respect_Cond_Achat;
    private double Stock_Global;
    private double Valeur_Stock_Global;
    private double Cond_franco;
    private boolean Arret_Dis;
    private String Date_Arret_Dis;
    private String Ref_marche;
    private String Date_der_photo;
    private String Sterilisation_Mode;
    private String Statut;
    private String Secteur;
    private String Devise;
    private double Nouveau_PU;
    private String SYS_DT_MAJ;
    private String SYS_HEURE_MAJ;
    private String SYS_USER_MAJ;
    private String Panel;
    private String Type_franco;
    private double Cond_Achat_Gros_volume;
    private String UCD_Code;
    private double Couleur;
    private int RGB_Red;
    private int RGB_Green;
    private int RGB_Blue;
    private double Inventaire1_PUMP_HT;
    private String Code_CIP;
    private double PUMP_TTC_Exercice_Prec;
    private double Qte_inventaire_exercice_prec;
    private double Stock_Actuel;
    private boolean Gratuit;
    private boolean Arret_Commande;
    private String Date_arret_com;
    private boolean Prev_A_commander;
    private double Qte_a_commander;
    private String Commentaire;
    private double PUMP_TTC_derniere_cloture;
    private int Classe_numero;
    private boolean Inscrire_a_ordonnancier;
    private String Date_Creation_Produit;
    private int Nb_Ligne_Code_Barre;
    private String Histo_Prix_Unitaire;
    private String DCI;
    private String Commentaire_Commande;
    private String Code_LPP;
    private String GTIN;
    private String Photo;
    private double Poids;
    private double Volume;
    private String Emplacement_PUI_Defaut;
    private String Informations_importantes;
    private boolean Distribution_Nominative_Active;
    private String Documentation_Path;
    private String Reapprovisionnement_Classe;
    private String Zone_UF_Defaut;
    private String Emplacement_UF_Defaut;
    private String Hemadialyse_Reference;
    private String Medicament_Liste;
    private double Medicament_CTJ;
    private String Materiaux;
    private String Voie;
    private String XForme;
    private String Indication_therapeutique;
    private String Posologie;
    private String Contre_indications;
    private String Effets_indesirables;
    private double Conservation_temperature_min;
    private double Conservation_temperature_Max;
    private double Conservation_hydro_min;
    private double Conservation_hydro_max;
    private double Conservation_pression_min;
    private double Conservation_pression_max;
    private boolean Conservation_sec;
    private boolean Conservation_abri;
    private boolean Condition_Refus_Si_Endomage;
    private boolean Condition_Fragile;
    private boolean Condition_usage_unique;
    private boolean Condition_peremption;
    private boolean Tracabilite_ref;
    private boolean Tracabilite_SN;
    private String Contenant;
    private boolean NePasResteriliser;
    private boolean Risque_voir_notice;
    private boolean Risque_Voir_Recommandation;
    private String Risque_Substance_presence;
    private String Risque_Substance_absence;
    private boolean Risque_latex;
    private boolean Risque_PHT;
    private boolean Medicament_dotation_urgence;
    private String Documentation_Web_Path;
    private boolean Temperature_Refrigere;
    private boolean Medicament_Risque;
    private boolean Regle_Bon_Usage_Active;
    private boolean Temperature_Ambiante;
    private boolean Livret_Therapeutique;
    private String Moment_Injection;
    private String Comment_Injecte;
    private String Composition;
    private double UI_Conversion;
    private String Zone_PAD_Defaut;
    private String Emplacement_PAD_Defaut;
    private String UCD_NomCourt;
    private String PHIE_Synchro;
    private String codeInconnue;
    private boolean Suivi_Serialisation;
    private boolean Serialiser_Reception_Delivrance;
    private int phiwms_mobileUUID = -1;


    public Produit(int ID_produit, String Designation_ext, String Categorie, String Designation_interne, String SYS_USER_MAJ, String GTIN)
    {
        this.ID_produit = ID_produit;
        this.Designation_ext = Designation_ext;
        this.Categorie = Categorie;
        this.Designation_interne = Designation_interne;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.GTIN = GTIN;
    }

    public Produit(int ID_produit, String Designation_ext, String Categorie, String Designation_interne, String SYS_USER_MAJ, String GTIN, int classe_numero, String zone_PUI_Defaut, String zone_UF_Defaut, String emplacement_PUI_Defaut, String emplacement_UF_Defaut, String fournisseur)
    {
        this.ID_produit = ID_produit;
        this.Designation_ext = Designation_ext;
        this.Categorie = Categorie;
        this.Designation_interne = Designation_interne;
        this.SYS_USER_MAJ = SYS_USER_MAJ;
        this.GTIN = GTIN;
        this.Classe_numero = classe_numero;
        this.Zone_PUI_Defaut = zone_PUI_Defaut;
        this.Zone_UF_Defaut = zone_UF_Defaut;
        this.Emplacement_PUI_Defaut = emplacement_PUI_Defaut;
        this.Emplacement_UF_Defaut = emplacement_UF_Defaut;
        this.Fournisseur = fournisseur;
    }

   /* public Produit(JSONObject produitJson) {
        try {
            this.ID_produit = produitJson.getInt("ID_produit");
            this.N_interne = OutilsGestionClasses.recupererString(produitJson.getString("N_interne"));
            this.Designation_ext = OutilsGestionClasses.recupererString(produitJson.getString("Designation_ext"));
            this.Categorie = OutilsGestionClasses.recupererString(produitJson.getString("Categorie"));
            this.Taux_de_TVA = produitJson.getDouble("Taux_de_TVA");
            this.Peremption = OutilsGestionClasses.recupererBooleen(produitJson, "Peremption");
            this.Marche_Obtenu = OutilsGestionClasses.recupererBooleen(produitJson, "Marche_Obtenu");
            this.Ordonnance = OutilsGestionClasses.recupererBooleen(produitJson, "Ordonnance");
            this.Cond_achat = produitJson.getInt("Cond_achat");
            this.Cond_distrib = produitJson.getDouble("Cond_distrib");
            this.Prix_unitaire = produitJson.getDouble("Prix_unitaire");
            this.Seuil_alerte = produitJson.getDouble("Seuil_alerte");
            this.Qte_Reassort = produitJson.getDouble("Qte_Reassort");
            this.Suivi_Lot = OutilsGestionClasses.recupererBooleen(produitJson, "Suivi_Lot");
            this.Valeur_stock_actuel = produitJson.getInt("Valeur_stock_actuel");
            this.Zone_PUI_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_PUI_Defaut"));
            this.Ref_fourni = OutilsGestionClasses.recupererString(produitJson.getString("Ref_fourni"));
            this.Code_fourn = produitJson.getInt("Code_fourn");
            this.Fournisseur = OutilsGestionClasses.recupererString(produitJson.getString("Fournisseur"));
            this.Unite = OutilsGestionClasses.recupererString(produitJson.getString("Unite"));
            this.Reassort = OutilsGestionClasses.recupererBooleen(produitJson, "Reassort");
            this.Stock_clot = produitJson.getDouble("Stock_clot");
            this.Ville = OutilsGestionClasses.recupererString(produitJson.getString("Ville"));
            this.Designation_interne = OutilsGestionClasses.recupererString(produitJson.getString("Designation_interne"));
            this.Type_erreur = OutilsGestionClasses.recupererString(produitJson.getString("Type_erreur"));
            this.A_corriger = OutilsGestionClasses.recupererString(produitJson.getString("A_corriger"));
            this.Inclu_au_panel = OutilsGestionClasses.recupererBooleen(produitJson, "Inclu_au_panel");
            this.Forme = OutilsGestionClasses.recupererString(produitJson.getString("Forme"));
            this.Reassort_Statut = OutilsGestionClasses.recupererString(produitJson.getString("Reassort_Statut"));
            this.Sterile = OutilsGestionClasses.recupererBooleen(produitJson, "Sterile");
            this.Duree_peremption = produitJson.getInt("Duree_peremption");
            this.Conservation = OutilsGestionClasses.recupererString(produitJson.getString("Conservation"));
            this.Mode_de_Distribution = OutilsGestionClasses.recupererString(produitJson.getString("Mode_de_Distribution"));
            this.Respect_Cond_Achat = OutilsGestionClasses.recupererBooleen(produitJson, "Respect_Cond_Achat");
            this.Stock_Global = produitJson.getDouble("Stock_Global");
            this.Valeur_Stock_Global = produitJson.getDouble("Valeur_Stock_Global");
            this.Cond_franco = produitJson.getDouble("Cond_franco");
            this.Arret_Dis = OutilsGestionClasses.recupererBooleen(produitJson, "Arret_Dis");
            this.Date_Arret_Dis = OutilsGestionClasses.recupererString(produitJson.getString("Date_Arret_Dis"));
            this.Ref_marche = OutilsGestionClasses.recupererString(produitJson.getString("Ref_marche"));
            this.Date_der_photo = OutilsGestionClasses.recupererString(produitJson.getString("Date_der_photo"));
            this.Sterilisation_Mode = OutilsGestionClasses.recupererString(produitJson.getString("Sterilisation_Mode"));
            this.Statut = OutilsGestionClasses.recupererString(produitJson.getString("Statut"));
            this.Secteur = OutilsGestionClasses.recupererString(produitJson.getString("Secteur"));
            this.Devise = OutilsGestionClasses.recupererString(produitJson.getString("Devise"));
            this.Nouveau_PU = produitJson.getDouble("Nouveau_PU");
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_USER_MAJ"));
            this.Panel = OutilsGestionClasses.recupererString(produitJson.getString("Panel"));
            this.Type_franco = OutilsGestionClasses.recupererString(produitJson.getString("Type_franco"));
            this.Cond_Achat_Gros_volume = produitJson.getDouble("Cond_Achat_Gros_volume");
            this.UCD_Code = OutilsGestionClasses.recupererString(produitJson.getString("UCD_Code"));
            this.Couleur = produitJson.getDouble("Couleur");
            this.RGB_Red = produitJson.getInt("RGB_Red");
            this.RGB_Green = produitJson.getInt("RGB_Green");
            this.RGB_Blue = produitJson.getInt("RGB_Blue");
            this.Inventaire1_PUMP_HT = produitJson.getDouble("Inventaire1_PUMP_HT");
            this.Code_CIP = OutilsGestionClasses.recupererString(produitJson.getString("Code_CIP"));
            this.PUMP_TTC_Exercice_Prec = produitJson.getDouble("PUMP_TTC_Exercice_Prec");
            this.Qte_inventaire_exercice_prec = produitJson.getDouble("Qte_inventaire_exercice_prec");
            this.Stock_Actuel = produitJson.getDouble("Stock_Actuel");
            this.Gratuit = OutilsGestionClasses.recupererBooleen(produitJson, "Gratuit");
            this.Arret_Commande = OutilsGestionClasses.recupererBooleen(produitJson, "Arret_Commande");
            this.Date_arret_com = OutilsGestionClasses.recupererString(produitJson.getString("Date_arret_com"));
            this.Prev_A_commander = OutilsGestionClasses.recupererBooleen(produitJson, "Prev_A_commander");
            this.Qte_a_commander = produitJson.getDouble("Qte_a_commander");
            this.Commentaire = OutilsGestionClasses.recupererString(produitJson.getString("Commentaire"));
            this.PUMP_TTC_derniere_cloture = produitJson.getDouble("PUMP_TTC_derniere_cloture");
            this.Classe_numero = produitJson.getInt("Classe_numero");
            this.Inscrire_a_ordonnancier = OutilsGestionClasses.recupererBooleen(produitJson, "Inscrire_a_ordonnancier");
            this.Date_Creation_Produit = OutilsGestionClasses.recupererString(produitJson.getString("Date_Creation_Produit"));
            this.Nb_Ligne_Code_Barre = produitJson.getInt("Nb_Ligne_Code_Barre");
            //this.Histo_Prix_Unitaire = recupererString(produitJson.getString("Histo_Prix_Unitaire");
            this.DCI = OutilsGestionClasses.recupererString(produitJson.getString("DCI"));
            this.Commentaire_Commande = OutilsGestionClasses.recupererString(produitJson.getString("Commentaire_Commande"));
            this.Code_LPP = OutilsGestionClasses.recupererString(produitJson.getString("Code_LPP"));
            this.GTIN = OutilsGestionClasses.recupererString(produitJson.getString("GTIN"));
            this.Photo = OutilsGestionClasses.recupererString(produitJson.getString("Photo"));
            this.Poids = produitJson.getDouble("Poids");
            this.Volume = produitJson.getDouble("Volume");
            this.Emplacement_PUI_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_PUI_Defaut"));
            this.Informations_importantes = OutilsGestionClasses.recupererString(produitJson.getString("Informations_importantes"));
            this.Distribution_Nominative_Active = OutilsGestionClasses.recupererBooleen(produitJson, "Distribution_Nominative_Active");
            this.Documentation_Path = OutilsGestionClasses.recupererString(produitJson.getString("Documentation_Path"));
            this.Reapprovisionnement_Classe = OutilsGestionClasses.recupererString(produitJson.getString("Reapprovisionnement_Classe"));
            this.Zone_UF_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_UF_Defaut"));
            this.Emplacement_UF_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_UF_Defaut"));
            this.Hemadialyse_Reference = OutilsGestionClasses.recupererString(produitJson.getString("Hemadialyse_Reference"));
            this.Medicament_Liste = OutilsGestionClasses.recupererString(produitJson.getString("Medicament_Liste"));
            this.Medicament_CTJ = produitJson.getDouble("Medicament_CTJ");
            this.Materiaux = OutilsGestionClasses.recupererString(produitJson.getString("Materiaux"));
            this.Voie = OutilsGestionClasses.recupererString(produitJson.getString("Voie"));
            this.XForme = OutilsGestionClasses.recupererString(produitJson.getString("XForme"));
            this.Indication_therapeutique = OutilsGestionClasses.recupererString(produitJson.getString("Indication_therapeutique"));
            this.Posologie = OutilsGestionClasses.recupererString(produitJson.getString("Posologie"));
            this.Contre_indications = OutilsGestionClasses.recupererString(produitJson.getString("Contre_indications"));
            this.Effets_indesirables = OutilsGestionClasses.recupererString(produitJson.getString("Effets_indesirables"));
            this.Conservation_temperature_min = produitJson.getDouble("Conservation_temperature_min");
            this.Conservation_temperature_Max = produitJson.getDouble("Conservation_temperature_Max");
            this.Conservation_hydro_min = produitJson.getDouble("Conservation_hydro_min");
            this.Conservation_hydro_max = produitJson.getDouble("Conservation_hydro_max");
            this.Conservation_pression_min = produitJson.getDouble("Conservation_pression_min");
            this.Conservation_pression_max = produitJson.getDouble("Conservation_pression_max");
            this.Conservation_sec = OutilsGestionClasses.recupererBooleen(produitJson, "Conservation_sec");
            this.Conservation_abri = OutilsGestionClasses.recupererBooleen(produitJson, "Conservation_abri");
            this.Condition_Refus_Si_Endomage = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_Refus_Si_Endomage");
            this.Condition_Fragile = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_Fragile");
            this.Condition_usage_unique = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_usage_unique");
            this.Condition_peremption = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_peremption");
            this.Tracabilite_ref = OutilsGestionClasses.recupererBooleen(produitJson, "Tracabilite_ref");
            this.Tracabilite_SN = OutilsGestionClasses.recupererBooleen(produitJson, "Tracabilite_SN");
            this.Contenant = OutilsGestionClasses.recupererString(produitJson.getString("Contenant"));
            this.NePasResteriliser = OutilsGestionClasses.recupererBooleen(produitJson, "NePasResteriliser");
            this.Risque_voir_notice = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_voir_notice");
            this.Risque_Voir_Recommandation = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_Voir_Recommandation");
            this.Risque_Substance_presence = OutilsGestionClasses.recupererString(produitJson.getString("Risque_Substance_presence"));
            this.Risque_Substance_absence = OutilsGestionClasses.recupererString(produitJson.getString("Risque_Substance_absence"));
            this.Risque_latex = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_latex");
            this.Risque_PHT = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_PHT");
            this.Medicament_dotation_urgence = OutilsGestionClasses.recupererBooleen(produitJson, "Medicament_dotation_urgence");
            this.Documentation_Web_Path = OutilsGestionClasses.recupererString(produitJson.getString("Documentation_Web_Path"));
            this.Temperature_Refrigere = OutilsGestionClasses.recupererBooleen(produitJson, "Temperature_Refrigere");
            this.Medicament_Risque = OutilsGestionClasses.recupererBooleen(produitJson, "Medicament_Risque");
            this.Regle_Bon_Usage_Active = OutilsGestionClasses.recupererBooleen(produitJson, "Regle_Bon_Usage_Active");
            this.Temperature_Ambiante = OutilsGestionClasses.recupererBooleen(produitJson, "Temperature_Ambiante");
            this.Livret_Therapeutique = OutilsGestionClasses.recupererBooleen(produitJson, "Livret_Therapeutique");
            this.Moment_Injection = OutilsGestionClasses.recupererString(produitJson.getString("Moment_Injection"));
            this.Comment_Injecte = OutilsGestionClasses.recupererString(produitJson.getString("Comment_Injecte"));
            this.Composition = OutilsGestionClasses.recupererString(produitJson.getString("Composition"));
            this.UI_Conversion = produitJson.getDouble("UI_Conversion");
            this.Zone_PAD_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_PAD_Defaut"));
            this.Emplacement_PAD_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_PAD_Defaut"));
            this.UCD_NomCourt = OutilsGestionClasses.recupererString(produitJson.getString("UCD_NomCourt"));
            this.codeInconnue = OutilsGestionClasses.recupererString(produitJson.getString("CodeInconnue"));
            this.PHIE_Synchro = OutilsGestionClasses.recupererString(produitJson.getString("PHIE_Synchro"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


    public Produit(JSONObject produitJson) {
        try {
            this.ID_produit = produitJson.getInt("ID_produit");
            this.Designation_ext = OutilsGestionClasses.recupererString(produitJson.getString("Designation_ext"));
            this.Categorie = OutilsGestionClasses.recupererString(produitJson.getString("Categorie"));
            this.Taux_de_TVA = produitJson.getDouble("Taux_de_TVA");
            this.Peremption = OutilsGestionClasses.recupererBooleen(produitJson, "Peremption");
            this.Cond_achat = produitJson.getInt("Cond_achat");
            this.Cond_distrib = produitJson.getDouble("Cond_distrib");
            this.Prix_unitaire = produitJson.getDouble("Prix_unitaire");
            this.Suivi_Lot = OutilsGestionClasses.recupererBooleen(produitJson, "Suivi_Lot");
            this.Zone_PUI_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_PUI_Defaut"));
            this.Ref_fourni = OutilsGestionClasses.recupererString(produitJson.getString("Ref_fourni"));
            this.Code_fourn = produitJson.getInt("Code_fourn");
            this.Fournisseur = OutilsGestionClasses.recupererString(produitJson.getString("Fournisseur"));
            this.Unite = OutilsGestionClasses.recupererString(produitJson.getString("Unite"));
            this.Designation_interne = OutilsGestionClasses.recupererString(produitJson.getString("Designation_interne"));
            this.Forme = OutilsGestionClasses.recupererString(produitJson.getString("Forme"));
            this.Sterile = OutilsGestionClasses.recupererBooleen(produitJson, "Sterile");
            this.Conservation = OutilsGestionClasses.recupererString(produitJson.getString("Conservation"));
            this.Arret_Dis = OutilsGestionClasses.recupererBooleen(produitJson, "Arret_Dis");
            this.Sterilisation_Mode = OutilsGestionClasses.recupererString(produitJson.getString("Sterilisation_Mode"));
            this.Secteur = OutilsGestionClasses.recupererString(produitJson.getString("Secteur"));
            this.Devise = OutilsGestionClasses.recupererString(produitJson.getString("Devise"));
            this.SYS_DT_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_DT_MAJ"));
            this.SYS_HEURE_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_HEURE_MAJ"));
            this.SYS_USER_MAJ = OutilsGestionClasses.recupererString(produitJson.getString("SYS_USER_MAJ"));
            this.Cond_Achat_Gros_volume = produitJson.getDouble("Cond_Achat_Gros_volume");
            this.UCD_Code = OutilsGestionClasses.recupererString(produitJson.getString("UCD_Code"));
            this.Arret_Commande = OutilsGestionClasses.recupererBooleen(produitJson, "Arret_Commande");
            this.Commentaire = OutilsGestionClasses.recupererString(produitJson.getString("Commentaire"));
            this.Classe_numero = produitJson.getInt("Classe_numero");
            this.GTIN = OutilsGestionClasses.recupererString(produitJson.getString("GTIN"));
            this.Emplacement_PUI_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_PUI_Defaut"));
            this.Informations_importantes = OutilsGestionClasses.recupererString(produitJson.getString("Informations_importantes"));
            this.Zone_UF_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_UF_Defaut"));
            this.Emplacement_UF_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_UF_Defaut"));
            this.Materiaux = OutilsGestionClasses.recupererString(produitJson.getString("Materiaux"));
            this.Voie = OutilsGestionClasses.recupererString(produitJson.getString("Voie"));
            this.Indication_therapeutique = OutilsGestionClasses.recupererString(produitJson.getString("Indication_therapeutique"));
            this.Posologie = OutilsGestionClasses.recupererString(produitJson.getString("Posologie"));
            this.Contre_indications = OutilsGestionClasses.recupererString(produitJson.getString("Contre_indications"));
            this.Effets_indesirables = OutilsGestionClasses.recupererString(produitJson.getString("Effets_indesirables"));
            this.Conservation_temperature_min = produitJson.getDouble("Conservation_temperature_min");
            this.Conservation_temperature_Max = produitJson.getDouble("Conservation_temperature_Max");
            this.Conservation_sec = OutilsGestionClasses.recupererBooleen(produitJson, "Conservation_sec");
            this.Conservation_abri = OutilsGestionClasses.recupererBooleen(produitJson, "Conservation_abri");
            this.Condition_Fragile = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_Fragile");
            this.Condition_usage_unique = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_usage_unique");
            this.Condition_peremption = OutilsGestionClasses.recupererBooleen(produitJson, "Condition_peremption");
            this.Contenant = OutilsGestionClasses.recupererString(produitJson.getString("Contenant"));
            this.NePasResteriliser = OutilsGestionClasses.recupererBooleen(produitJson, "NePasResteriliser");
            this.Risque_Substance_presence = OutilsGestionClasses.recupererString(produitJson.getString("Risque_Substance_presence"));
            this.Risque_Substance_absence = OutilsGestionClasses.recupererString(produitJson.getString("Risque_Substance_absence"));
            this.Risque_latex = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_latex");
            this.Risque_PHT = OutilsGestionClasses.recupererBooleen(produitJson, "Risque_PHT");
            this.Medicament_dotation_urgence = OutilsGestionClasses.recupererBooleen(produitJson, "Medicament_dotation_urgence");
            this.Temperature_Refrigere = OutilsGestionClasses.recupererBooleen(produitJson, "Temperature_Refrigere");
            this.Medicament_Risque = OutilsGestionClasses.recupererBooleen(produitJson, "Medicament_Risque");
            this.Temperature_Ambiante = OutilsGestionClasses.recupererBooleen(produitJson, "Temperature_Ambiante");
            this.UI_Conversion = produitJson.getDouble("UI_Conversion");
            this.Zone_PAD_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Zone_PAD_Defaut"));
            this.Emplacement_PAD_Defaut = OutilsGestionClasses.recupererString(produitJson.getString("Emplacement_PAD_Defaut"));
            this.UCD_NomCourt = OutilsGestionClasses.recupererString(produitJson.getString("UCD_NomCourt"));
            this.codeInconnue = OutilsGestionClasses.recupererString(produitJson.getString("codeInconnue"));
            this.Suivi_Serialisation = OutilsGestionClasses.recupererBooleen(produitJson,"Suivi_Serialisation");
            this.Serialiser_Reception_Delivrance = OutilsGestionClasses.recupererBooleen(produitJson,"Serialiser_Reception_Delivrance");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


   /* public Produit(Cursor cursor) {
        this.ID_produit = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_ID_PRODUIT);
        this.N_interne = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_N_INTERNE_PRODUIT);
        this.Designation_ext = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DESIGNATION_EXT_PRODUIT);
        this.Categorie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CATEGORIE_PRODUIT);
        this.Taux_de_TVA = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_TAUX_DE_TVA_PRODUIT);
        this.Peremption = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_PEREMPTION_PRODUIT);
        this.Marche_Obtenu = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_MARCHE_OBTENU_PRODUIT);
        this.Ordonnance = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_ORDONNANCE_PRODUIT);
        this.Cond_achat = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_COND_ACHAT_PRODUIT);
        this.Cond_distrib = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COND_DISTRIB_PRODUIT);
        this.Prix_unitaire = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_PRIX_UNITAIRE_PRODUIT);
        this.Seuil_alerte = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_SEUIL_ALERTE_PRODUIT);
        this.Qte_Reassort = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_QTE_REASSORT_PRODUIT);
        this.Suivi_Lot = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_SUIVI_LOT_PRODUIT);
        this.Valeur_stock_actuel = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_VALEUR_STOCK_ACTUEL_PRODUIT);
        this.Zone_PUI_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_PUI_DEFAUT_PRODUIT);
        this.Ref_fourni = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_REF_FOURNI_PRODUIT);
        this.Code_fourn = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_CODE_FOURN_PRODUIT);
        this.Fournisseur = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_FOURNISSEUR_PRODUIT);
        this.Unite = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UNITE_PRODUIT);
        this.Reassort = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_REASSORT_STATUT_PRODUIT);
        this.Stock_clot = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_STOCK_CLOT_PRODUIT);
        this.Ville = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_VILLE_PRODUIT);
        this.Designation_interne = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DESIGNATION_INTERNE_PRODUIT);
        this.Type_erreur = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_TYPE_ERREUR_PRODUIT);
        this.A_corriger = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_A_CORRIGER_PRODUIT);
        this.Inclu_au_panel = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_INCLU_AU_PANEL_PRODUIT);
        this.Forme = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_FORME_PRODUIT);
        this.Reassort_Statut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_REASSORT_STATUT_PRODUIT);
        this.Sterile = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_STERILE_PRODUIT);
        this.Duree_peremption = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_DUREE_PEREMPTION_PRODUIT);
        this.Conservation = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_PRODUIT);
        this.Mode_de_Distribution = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_MODE_DE_DISTRIBUTION_PRODUIT);
        this.Respect_Cond_Achat = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RESPECT_COND_ACHAT_PRODUIT);
        this.Stock_Global = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_STOCK_GLOBAL_PRODUIT);
        this.Valeur_Stock_Global = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_VALEUR_STOCK_GLOBAL_PRODUIT);
        this.Cond_franco = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COND_FRANCO_PRODUIT);
        this.Arret_Dis = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_ARRET_DIS_PRODUIT);
        this.Date_Arret_Dis = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DATE_ARRET_DIS_PRODUIT);
        this.Ref_marche = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_REF_MARCHE_PRODUIT);
        this.Date_der_photo = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DATE_DER_PHOTO_PRODUIT);
        this.Sterilisation_Mode = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_STERILISATION_MODE_PRODUIT);
        this.Statut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_STATUT_PRODUIT);
        this.Secteur = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SECTEUR_PRODUIT);
        this.Devise = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DEVISE_PRODUIT);
        this.Nouveau_PU = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_NOUVEAU_PU_PRODUIT);
        this.SYS_DT_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PRODUIT);
        this.SYS_HEURE_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PRODUIT);
        this.SYS_USER_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PRODUIT);
        this.Panel = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_PANEL_PRODUIT);
        this.Type_franco = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_TYPE_FRANCO_PRODUIT);
        this.Cond_Achat_Gros_volume = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COND_ACHAT_GROS_VOLUME_PRODUIT);
        this.UCD_Code = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UCD_CODE_PRODUIT);
        this.Couleur = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COULEUR_PRODUIT);
        this.RGB_Red = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_RGB_RED_PRODUIT);
        this.RGB_Green = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_RGB_GREEN_PRODUIT);
        this.RGB_Blue = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_RGB_BLUE_PRODUIT);
        this.Inventaire1_PUMP_HT = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_INVENTAIRE1_PUMP_HT_PRODUIT);
        this.Code_CIP = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CODE_CIP_PRODUIT);
        this.PUMP_TTC_Exercice_Prec = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_PUMP_TTC_EXERCICE_PREC_PRODUIT);
        this.Qte_inventaire_exercice_prec = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_QTE_INVENTAIRE_EXERCICE_PREC_PRODUIT);
        this.Stock_Actuel = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_STOCK_ACTUEL_PRODUIT);
        this.Gratuit = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_GRATUIT_PRODUIT);
        this.Arret_Commande = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_ARRET_COMMANDE_PRODUIT);
        this.Date_arret_com = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DATE_ARRET_COM_PRODUIT);
        this.Prev_A_commander = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_PREV_A_COMMANDER_PRODUIT);
        this.Qte_a_commander = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_QTE_A_COMMANDER_PRODUIT);
        this.Commentaire = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_COMMENTAIRE_PRODUIT);
        this.PUMP_TTC_derniere_cloture = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_PUMP_TTC_DERNIERE_CLOTURE_PRODUIT);
        this.Classe_numero = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_CLASSE_NUMERO_PRODUIT);
        this.Inscrire_a_ordonnancier = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_INSCRIRE_A_ORDONNANCIER_PRODUIT);
        this.Date_Creation_Produit = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DATE_CREATION_PRODUIT);
        this.Nb_Ligne_Code_Barre = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_NB_LIGNE_CODE_BARRE_PRODUIT);
        this.Histo_Prix_Unitaire = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_HISTO_PRIX_UNITAIRE_PRODUIT);
        this.DCI = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DCI_PRODUIT);
        this.Commentaire_Commande = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_COMMENTAIRE_COMMANDE_PRODUIT);
        this.Code_LPP = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CODE_LPP_PRODUIT);
        this.GTIN = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_GTIN_PRODUIT);
        this.Photo = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DATE_DER_PHOTO_PRODUIT);
        this.Poids = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_POIDS_PRODUIT);
        this.Volume = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_VOLUME_PRODUIT);
        this.Emplacement_PUI_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT);
        this.Informations_importantes = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_INFORMATION_IMPORTANTES_PRODUIT);
        this.Distribution_Nominative_Active = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_DISTRIBUTION_NOMINATIVE_ACTIVE_PRODUIT);
        this.Documentation_Path = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DOCUMENTATION_PATH_PRODUIT);
        this.Reapprovisionnement_Classe = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_REAPPROVISIONNEMENT_CLASSE_PRODUIT);
        this.Zone_UF_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_UF_DEFAUT_PRODUIT);
        this.Emplacement_UF_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT);
        this.Hemadialyse_Reference = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_HEMADIALYSE_REFERENCE_PRODUIT);
        this.Medicament_Liste = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_LISTE_PRODUIT);
        this.Medicament_CTJ = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_CTJ_PRODUIT);
        this.Materiaux = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_MATERIAUX_PRODUIT);
        this.Voie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_VOIE_PRODUIT);
        this.XForme = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_XFORME_PRODUIT);
        this.Indication_therapeutique = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_INDICATION_THERAPEUTIQUE_PRODUIT);
        this.Posologie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_POSOLOGIE_PRODUIT);
        this.Contre_indications = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONTRE_INDICATIONS_PRODUIT);
        this.Effets_indesirables = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EFFETS_INDESIRABLES_PRODUIT);
        this.Conservation_temperature_min = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT);
        this.Conservation_temperature_Max = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT);
        this.Conservation_hydro_min = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_HYDRO_MIN_PRODUIT);
        this.Conservation_hydro_max = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_HYDRO_MAX_PRODUIT);
        this.Conservation_pression_min = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_PRESSION_MIN_PRODUIT);
        this.Conservation_pression_max = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_PRESSION_MAX_PRODUIT);
        this.Conservation_sec = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_SEC_PRODUIT);
        this.Conservation_abri = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_ABRI_PRODUIT);
        this.Condition_Refus_Si_Endomage = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_REFUS_SI_ENDOMAGE_PRODUIT);
        this.Condition_Fragile = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_FRAGILE_PRODUIT);
        this.Condition_usage_unique = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_USAGE_UNIQUE_PRODUIT);
        this.Condition_peremption = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_PEREMPTION_PRODUIT);
        this.Tracabilite_ref = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TRACABILITE_REF_PRODUIT);
        this.Tracabilite_SN = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TRACABILITE_SN_PRODUIT);
        this.Contenant = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONTENANT_PRODUIT);
        this.NePasResteriliser = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_NEPASRESTERILISER_PRODUIT);
        this.Risque_voir_notice = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_VOIR_NOTICE_PRODUIT);
        this.Risque_Voir_Recommandation = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_VOIR_RECOMMANDATION_PRODUIT);
        this.Risque_Substance_presence = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT);
        this.Risque_Substance_absence = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT);
        this.Risque_latex = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_LATEX_PRODUIT);
        this.Risque_PHT = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_PHT_PRODUIT);
        this.Medicament_dotation_urgence = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT);
        this.Documentation_Web_Path = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DOCUMENTATION_WEB_PATH_PRODUIT);
        this.Temperature_Refrigere = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TEMPERATURE_REFRIGERE_PRODUIT);
        this.Medicament_Risque = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_RISQUE_PRODUIT);
        this.Regle_Bon_Usage_Active = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_REGLE_BON_USAGE_ACTIVE_PRODUIT);
        this.Temperature_Ambiante = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TEMPERATURE_AMBIANTE_PRODUIT);
        this.Livret_Therapeutique = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_LIVRET_THERAPEUTIQUE_PRODUIT);
        this.Moment_Injection = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_MOMENT_INJECTION_PRODUIT);
        this.Comment_Injecte = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_COMMENT_INJECTE_PRODUIT);
        this.Composition = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_COMPOSITION_PRODUIT);
        this.UI_Conversion = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_UI_CONVERSION_PRODUIT);
        this.Zone_PAD_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_PAD_DEFAUT_PRODUIT);
        this.Emplacement_PAD_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT);
        this.UCD_NomCourt = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UCD_NOMCOURT_PRODUIT);
        this.PHIE_Synchro = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_PHIE_SYNCHRO_PRODUIT);
        this.codeInconnue = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CODE_INCONNU);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }*/

    public Produit(Cursor cursor) {
        this.ID_produit = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_ID_PRODUIT);
        this.Designation_ext = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DESIGNATION_EXT_PRODUIT);
        this.Categorie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CATEGORIE_PRODUIT);
        this.Peremption = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_PEREMPTION_PRODUIT);
        this.Cond_achat = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_COND_ACHAT_PRODUIT);
        this.Cond_distrib = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COND_DISTRIB_PRODUIT);
        this.Prix_unitaire = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_PRIX_UNITAIRE_PRODUIT);
        this.Suivi_Lot = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_SUIVI_LOT_PRODUIT);
        this.Zone_PUI_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_PUI_DEFAUT_PRODUIT);
        this.Ref_fourni = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_REF_FOURNI_PRODUIT);
        this.Code_fourn = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_CODE_FOURN_PRODUIT);
        this.Fournisseur = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_FOURNISSEUR_PRODUIT);
        this.Unite = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UNITE_PRODUIT);
        this.Designation_interne = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DESIGNATION_INTERNE_PRODUIT);
        this.Forme = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_FORME_PRODUIT);
        this.Sterile = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_STERILE_PRODUIT);
        this.Conservation = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_PRODUIT);
        this.Arret_Dis = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_ARRET_DIS_PRODUIT);
        this.Sterilisation_Mode = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_STERILISATION_MODE_PRODUIT);
        this.Secteur = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SECTEUR_PRODUIT);
        this.Devise = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_DEVISE_PRODUIT);
        this.SYS_DT_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_DT_MAJ_PRODUIT);
        this.SYS_HEURE_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_HEURE_MAJ_PRODUIT);
        this.SYS_USER_MAJ = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_SYS_USER_MAJ_PRODUIT);
        this.Cond_Achat_Gros_volume = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_COND_ACHAT_GROS_VOLUME_PRODUIT);
        this.UCD_Code = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UCD_CODE_PRODUIT);
        this.Arret_Commande = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_ARRET_COMMANDE_PRODUIT);
        this.Commentaire = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_COMMENTAIRE_PRODUIT);
        this.Classe_numero = cursor.getInt(ProduitOpenHelper.Constantes.NUM_COL_CLASSE_NUMERO_PRODUIT);
        this.GTIN = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_GTIN_PRODUIT);
        this.Emplacement_PUI_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PUI_DEFAUT_PRODUIT);
        this.Informations_importantes = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_INFORMATION_IMPORTANTES_PRODUIT);
        this.Zone_UF_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_UF_DEFAUT_PRODUIT);
        this.Emplacement_UF_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_UF_DEFAUT_PRODUIT);
        this.Materiaux = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_MATERIAUX_PRODUIT);
        this.Voie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_VOIE_PRODUIT);
        this.Indication_therapeutique = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_INDICATION_THERAPEUTIQUE_PRODUIT);
        this.Posologie = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_POSOLOGIE_PRODUIT);
        this.Contre_indications = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONTRE_INDICATIONS_PRODUIT);
        this.Effets_indesirables = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EFFETS_INDESIRABLES_PRODUIT);
        this.Conservation_temperature_min = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_TEMPERATURE_MIN_PRODUIT);
        this.Conservation_temperature_Max = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_TEMPERATURE_MAX_PRODUIT);
        this.Conservation_sec = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_SEC_PRODUIT);
        this.Conservation_abri = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONSERVATION_ABRI_PRODUIT);
        this.Condition_Fragile = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_FRAGILE_PRODUIT);
        this.Condition_usage_unique = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_USAGE_UNIQUE_PRODUIT);
        this.Condition_peremption = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_CONDITION_PEREMPTION_PRODUIT);
        this.Contenant = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CONTENANT_PRODUIT);
        this.NePasResteriliser = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_NEPASRESTERILISER_PRODUIT);
        this.Risque_Substance_presence = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_RISQUE_SUBSTANCE_PRESENCE_PRODUIT);
        this.Risque_Substance_absence = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_RISQUE_SUBSTANCE_ABSENCE_PRODUIT);
        this.Risque_latex = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_LATEX_PRODUIT);
        this.Risque_PHT = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_RISQUE_PHT_PRODUIT);
        this.Medicament_dotation_urgence = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_DOTATION_URGENCE_PRODUIT);
        this.Temperature_Refrigere = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TEMPERATURE_REFRIGERE_PRODUIT);
        this.Medicament_Risque = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_MEDICAMENT_RISQUE_PRODUIT);
        this.Temperature_Ambiante = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_TEMPERATURE_AMBIANTE_PRODUIT);
        this.UI_Conversion = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_UI_CONVERSION_PRODUIT);
        this.Zone_PAD_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_ZONE_PAD_DEFAUT_PRODUIT);
        this.Emplacement_PAD_Defaut = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_EMPLACEMENT_PAD_DEFAUT_PRODUIT);
        this.UCD_NomCourt = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_UCD_NOMCOURT_PRODUIT);
        this.codeInconnue = cursor.getString(ProduitOpenHelper.Constantes.NUM_COL_CODE_INCONNU);
        this.Taux_de_TVA = cursor.getDouble(ProduitOpenHelper.Constantes.NUM_COL_TAUX_DE_TVA_PRODUIT);
        this.Suivi_Serialisation = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_SUIVI_SERIALISATION);
        this.Serialiser_Reception_Delivrance = OutilsGestionClasses.recupererBooleen(cursor, ProduitOpenHelper.Constantes.NUM_COL_SERIALISER_RECEPTION_DELIVRANCE);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);

    }

    public int getPhiMR4UUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public int getID_produit() {
        return ID_produit;
    }

    public void setID_produit(int ID_produit) {
        this.ID_produit = ID_produit;
    }

    public String getN_interne() {
        return N_interne;
    }

    public void setN_interne(String n_interne) {
        N_interne = n_interne;
    }

    public String getDesignation_ext() {
        return Designation_ext;
    }

    public void setDesignation_ext(String designation_ext) {
        Designation_ext = designation_ext;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
    }

    public double getTaux_de_TVA() {
        return Taux_de_TVA;
    }

    public void setTaux_de_TVA(double taux_de_TVA) {
        Taux_de_TVA = taux_de_TVA;
    }

    public boolean isPeremption() {
        return Peremption;
    }

    public void setPeremption(boolean peremption) {
        Peremption = peremption;
    }

    public boolean isMarche_Obtenu() {
        return Marche_Obtenu;
    }

    public void setMarche_Obtenu(boolean marche_Obtenu) {
        Marche_Obtenu = marche_Obtenu;
    }

    public boolean isOrdonnance() {
        return Ordonnance;
    }

    public void setOrdonnance(boolean ordonnance) {
        Ordonnance = ordonnance;
    }

    public int getCond_achat() {
        return Cond_achat;
    }

    public void setCond_achat(int cond_achat) {
        Cond_achat = cond_achat;
    }

    public double getCond_distrib() {
        return Cond_distrib;
    }

    public void setCond_distrib(double cond_distrib) {
        Cond_distrib = cond_distrib;
    }

    public double getPrix_unitaire() {
        return Prix_unitaire;
    }

    public void setPrix_unitaire(double prix_unitaire) {
        Prix_unitaire = prix_unitaire;
    }

    public double getSeuil_alerte() {
        return Seuil_alerte;
    }

    public void setSeuil_alerte(double seuil_alerte) {
        Seuil_alerte = seuil_alerte;
    }

    public double getQte_Reassort() {
        return Qte_Reassort;
    }

    public void setQte_Reassort(double qte_Reassort) {
        Qte_Reassort = qte_Reassort;
    }

    public boolean isSuivi_Lot() {
        return Suivi_Lot;
    }

    public void setSuivi_Lot(boolean suivi_Lot) {
        Suivi_Lot = suivi_Lot;
    }

    public int getValeur_stock_actuel() {
        return Valeur_stock_actuel;
    }

    public void setValeur_stock_actuel(int valeur_stock_actuel) {
        Valeur_stock_actuel = valeur_stock_actuel;
    }

    public String getZone_PUI_Defaut() {
        return Zone_PUI_Defaut;
    }

    public void setZone_PUI_Defaut(String zone_PUI_Defaut) {
        Zone_PUI_Defaut = zone_PUI_Defaut;
    }

    public String getRef_fourni() {
        return Ref_fourni;
    }

    public void setRef_fourni(String ref_fourni) {
        Ref_fourni = ref_fourni;
    }

    public int getCode_fourn() {
        return Code_fourn;
    }

    public void setCode_fourn(int code_fourn) {
        Code_fourn = code_fourn;
    }

    public String getFournisseur() {
        return Fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        Fournisseur = fournisseur;
    }

    public String getUnite() {
        return Unite;
    }

    public void setUnite(String unite) {
        Unite = unite;
    }

    public boolean isReassort() {
        return Reassort;
    }

    public void setReassort(boolean reassort) {
        Reassort = reassort;
    }

    public double getStock_clot() {
        return Stock_clot;
    }

    public void setStock_clot(double stock_clot) {
        Stock_clot = stock_clot;
    }

    public String getVille() {
        return Ville;
    }

    public void setVille(String ville) {
        Ville = ville;
    }

    public String getDesignation_interne() {
        return Designation_interne;
    }

    public void setDesignation_interne(String designation_interne) {
        Designation_interne = designation_interne;
    }

    public String getType_erreur() {
        return Type_erreur;
    }

    public void setType_erreur(String type_erreur) {
        Type_erreur = type_erreur;
    }

    public String getA_corriger() {
        return A_corriger;
    }

    public void setA_corriger(String a_corriger) {
        A_corriger = a_corriger;
    }

    public boolean isInclu_au_panel() {
        return Inclu_au_panel;
    }

    public void setInclu_au_panel(boolean inclu_au_panel) {
        Inclu_au_panel = inclu_au_panel;
    }

    public String getForme() {
        return Forme;
    }

    public void setForme(String forme) {
        Forme = forme;
    }

    public String getReassort_Statut() {
        return Reassort_Statut;
    }

    public void setReassort_Statut(String reassort_Statut) {
        Reassort_Statut = reassort_Statut;
    }

    public boolean isSterile() {
        return Sterile;
    }

    public void setSterile(boolean sterile) {
        Sterile = sterile;
    }

    public int getDuree_peremption() {
        return Duree_peremption;
    }

    public void setDuree_peremption(int duree_peremption) {
        Duree_peremption = duree_peremption;
    }

    public String getConservation() {
        return Conservation;
    }

    public void setConservation(String conservation) {
        Conservation = conservation;
    }

    public String getMode_de_Distribution() {
        return Mode_de_Distribution;
    }

    public void setMode_de_Distribution(String mode_de_Distribution) {
        Mode_de_Distribution = mode_de_Distribution;
    }

    public boolean isRespect_Cond_Achat() {
        return Respect_Cond_Achat;
    }

    public void setRespect_Cond_Achat(boolean respect_Cond_Achat) {
        Respect_Cond_Achat = respect_Cond_Achat;
    }

    public double getStock_Global() {
        return Stock_Global;
    }

    public void setStock_Global(double stock_Global) {
        Stock_Global = stock_Global;
    }

    public double getValeur_Stock_Global() {
        return Valeur_Stock_Global;
    }

    public void setValeur_Stock_Global(double valeur_Stock_Global) {
        Valeur_Stock_Global = valeur_Stock_Global;
    }

    public double getCond_franco() {
        return Cond_franco;
    }

    public void setCond_franco(double cond_franco) {
        Cond_franco = cond_franco;
    }

    public boolean isArret_Dis() {
        return Arret_Dis;
    }

    public void setArret_Dis(boolean arret_Dis) {
        Arret_Dis = arret_Dis;
    }

    public String getDate_Arret_Dis() {
        return Date_Arret_Dis;
    }

    public void setDate_Arret_Dis(String date_Arret_Dis) {
        Date_Arret_Dis = date_Arret_Dis;
    }

    public String getRef_marche() {
        return Ref_marche;
    }

    public void setRef_marche(String ref_marche) {
        Ref_marche = ref_marche;
    }

    public String getDate_der_photo() {
        return Date_der_photo;
    }

    public void setDate_der_photo(String date_der_photo) {
        Date_der_photo = date_der_photo;
    }

    public String getSterilisation_Mode() {
        return Sterilisation_Mode;
    }

    public void setSterilisation_Mode(String sterilisation_Mode) {
        Sterilisation_Mode = sterilisation_Mode;
    }

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    public String getSecteur() {
        return Secteur;
    }

    public void setSecteur(String secteur) {
        Secteur = secteur;
    }

    public String getDevise() {
        return Devise;
    }

    public void setDevise(String devise) {
        Devise = devise;
    }

    public double getNouveau_PU() {
        return Nouveau_PU;
    }

    public void setNouveau_PU(double nouveau_PU) {
        Nouveau_PU = nouveau_PU;
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

    public String getPanel() {
        return Panel;
    }

    public void setPanel(String panel) {
        Panel = panel;
    }

    public String getType_franco() {
        return Type_franco;
    }

    public void setType_franco(String type_franco) {
        Type_franco = type_franco;
    }

    public double getCond_Achat_Gros_volume() {
        return Cond_Achat_Gros_volume;
    }

    public void setCond_Achat_Gros_volume(double cond_Achat_Gros_volume) {
        Cond_Achat_Gros_volume = cond_Achat_Gros_volume;
    }

    public String getUCD_Code() {
        return UCD_Code;
    }

    public void setUCD_Code(String UCD_Code) {
        this.UCD_Code = UCD_Code;
    }

    public double getCouleur() {
        return Couleur;
    }

    public void setCouleur(double couleur) {
        Couleur = couleur;
    }

    public int getRGB_Red() {
        return RGB_Red;
    }

    public void setRGB_Red(int RGB_Red) {
        this.RGB_Red = RGB_Red;
    }

    public int getRGB_Green() {
        return RGB_Green;
    }

    public void setRGB_Green(int RGB_Green) {
        this.RGB_Green = RGB_Green;
    }

    public int getRGB_Blue() {
        return RGB_Blue;
    }

    public void setRGB_Blue(int RGB_Blue) {
        this.RGB_Blue = RGB_Blue;
    }

    public double getInventaire1_PUMP_HT() {
        return Inventaire1_PUMP_HT;
    }

    public void setInventaire1_PUMP_HT(double inventaire1_PUMP_HT) {
        Inventaire1_PUMP_HT = inventaire1_PUMP_HT;
    }

    public String getCode_CIP() {
        return Code_CIP;
    }

    public void setCode_CIP(String code_CIP) {
        Code_CIP = code_CIP;
    }

    public double getPUMP_TTC_Exercice_Prec() {
        return PUMP_TTC_Exercice_Prec;
    }

    public void setPUMP_TTC_Exercice_Prec(double PUMP_TTC_Exercice_Prec) {
        this.PUMP_TTC_Exercice_Prec = PUMP_TTC_Exercice_Prec;
    }

    public double getQte_inventaire_exercice_prec() {
        return Qte_inventaire_exercice_prec;
    }

    public void setQte_inventaire_exercice_prec(double qte_inventaire_exercice_prec) {
        Qte_inventaire_exercice_prec = qte_inventaire_exercice_prec;
    }

    public double getStock_Actuel() {
        return Stock_Actuel;
    }

    public void setStock_Actuel(double stock_Actuel) {
        Stock_Actuel = stock_Actuel;
    }

    public boolean isGratuit() {
        return Gratuit;
    }

    public void setGratuit(boolean gratuit) {
        Gratuit = gratuit;
    }

    public boolean isArret_Commande() {
        return Arret_Commande;
    }

    public void setArret_Commande(boolean arret_Commande) {
        Arret_Commande = arret_Commande;
    }

    public String getDate_arret_com() {
        return Date_arret_com;
    }

    public void setDate_arret_com(String date_arret_com) {
        Date_arret_com = date_arret_com;
    }

    public boolean isPrev_A_commander() {
        return Prev_A_commander;
    }

    public void setPrev_A_commander(boolean prev_A_commander) {
        Prev_A_commander = prev_A_commander;
    }

    public double getQte_a_commander() {
        return Qte_a_commander;
    }

    public void setQte_a_commander(double qte_A_commander) {
        Qte_a_commander = qte_A_commander;
    }

    public String getCommentaire() {
        return Commentaire;
    }

    public void setCommentaire(String commentaire) {
        Commentaire = commentaire;
    }

    public double getPUMP_TTC_derniere_cloture() {
        return PUMP_TTC_derniere_cloture;
    }

    public void setPUMP_TTC_derniere_cloture(double PUMP_TTC_derniere_cloture) {
        this.PUMP_TTC_derniere_cloture = PUMP_TTC_derniere_cloture;
    }

    public int getClasse_numero() {
        return Classe_numero;
    }

    public void setClasse_numero(int classe_numero) {
        Classe_numero = classe_numero;
    }

    public boolean isInscrire_a_ordonnancier() {
        return Inscrire_a_ordonnancier;
    }

    public void setInscrire_a_ordonnancier(boolean inscrire_a_ordonnancier) {
        Inscrire_a_ordonnancier = inscrire_a_ordonnancier;
    }

    public String getDate_Creation_Produit() {
        return Date_Creation_Produit;
    }

    public void setDate_Creation_Produit(String date_Creation_Produit) {
        Date_Creation_Produit = date_Creation_Produit;
    }

    public int getNb_Ligne_Code_Barre() {
        return Nb_Ligne_Code_Barre;
    }

    public void setNb_Ligne_Code_Barre(int nb_Ligne_Code_Barre) {
        Nb_Ligne_Code_Barre = nb_Ligne_Code_Barre;
    }

    public String getHisto_Prix_Unitaire() {
        return Histo_Prix_Unitaire;
    }

    public void setHisto_Prix_Unitaire(String histo_Prix_Unitaire) {
        Histo_Prix_Unitaire = histo_Prix_Unitaire;
    }

    public String getDCI() {
        return DCI;
    }

    public void setDCI(String DCI) {
        this.DCI = DCI;
    }

    public String getCommentaire_Commande() {
        return Commentaire_Commande;
    }

    public void setCommentaire_Commande(String commentaire_Commande) {
        Commentaire_Commande = commentaire_Commande;
    }

    public String getCode_LPP() {
        return Code_LPP;
    }

    public void setCode_LPP(String code_LPP) {
        Code_LPP = code_LPP;
    }

    public String getGTIN() {
        return GTIN;
    }

    public void setGTIN(String GTIN) {
        this.GTIN = GTIN;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public double getPoids() {
        return Poids;
    }

    public void setPoids(double poids) {
        Poids = poids;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getEmplacement_PUI_Defaut() {
        return Emplacement_PUI_Defaut;
    }

    public void setEmplacement_PUI_Defaut(String emplacement_PUI_Defaut) {
        Emplacement_PUI_Defaut = emplacement_PUI_Defaut;
    }

    public String getInformations_importantes() {
        return Informations_importantes;
    }

    public void setInformations_importantes(String informations_importantes) {
        Informations_importantes = informations_importantes;
    }

    public boolean isDistribution_Nominative_Active() {
        return Distribution_Nominative_Active;
    }

    public void setDistribution_Nominative_Active(boolean distribution_Nominative_Active) {
        Distribution_Nominative_Active = distribution_Nominative_Active;
    }

    public String getDocumentation_Path() {
        return Documentation_Path;
    }

    public void setDocumentation_Path(String documentation_Path) {
        Documentation_Path = documentation_Path;
    }

    public String getReapprovisionnement_Classe() {
        return Reapprovisionnement_Classe;
    }

    public void setReapprovisionnement_Classe(String reapprovisionnement_Classe) {
        Reapprovisionnement_Classe = reapprovisionnement_Classe;
    }

    public String getZone_UF_Defaut() {
        return Zone_UF_Defaut;
    }

    public void setZone_UF_Defaut(String zone_UF_Defaut) {
        Zone_UF_Defaut = zone_UF_Defaut;
    }

    public String getEmplacement_UF_Defaut() {
        return Emplacement_UF_Defaut;
    }

    public void setEmplacement_UF_Defaut(String emplacement_UF_Defaut) {
        Emplacement_UF_Defaut = emplacement_UF_Defaut;
    }

    public String getHemadialyse_Reference() {
        return Hemadialyse_Reference;
    }

    public void setHemadialyse_Reference(String hemadialyse_Reference) {
        Hemadialyse_Reference = hemadialyse_Reference;
    }

    public String getMedicament_Liste() {
        return Medicament_Liste;
    }

    public void setMedicament_Liste(String medicament_Liste) {
        Medicament_Liste = medicament_Liste;
    }

    public double getMedicament_CTJ() {
        return Medicament_CTJ;
    }

    public void setMedicament_CTJ(double medicament_CTJ) {
        Medicament_CTJ = medicament_CTJ;
    }

    public String getMateriaux() {
        return Materiaux;
    }

    public void setMateriaux(String materiaux) {
        Materiaux = materiaux;
    }

    public String getVoie() {
        return Voie;
    }

    public void setVoie(String voie) {
        Voie = voie;
    }

    public String getXForme() {
        return XForme;
    }

    public void setXForme(String XForme) {
        this.XForme = XForme;
    }

    public String getIndication_therapeutique() {
        return Indication_therapeutique;
    }

    public void setIndication_therapeutique(String indication_therapeutique) {
        Indication_therapeutique = indication_therapeutique;
    }

    public String getPosologie() {
        return Posologie;
    }

    public void setPosologie(String posologie) {
        Posologie = posologie;
    }

    public String getContre_indications() {
        return Contre_indications;
    }

    public void setContre_indications(String contre_indications) {
        Contre_indications = contre_indications;
    }

    public String getEffets_indesirables() {
        return Effets_indesirables;
    }

    public void setEffets_indesirables(String effets_indesirables) {
        Effets_indesirables = effets_indesirables;
    }

    public double getConservation_temperature_min() {
        return Conservation_temperature_min;
    }

    public void setConservation_temperature_min(double conservation_temperature_min) {
        Conservation_temperature_min = conservation_temperature_min;
    }

    public double getConservation_temperature_Max() {
        return Conservation_temperature_Max;
    }

    public void setConservation_temperature_Max(double conservation_temperature_Max) {
        Conservation_temperature_Max = conservation_temperature_Max;
    }

    public double getConservation_hydro_min() {
        return Conservation_hydro_min;
    }

    public void setConservation_hydro_min(double conservation_hydro_min) {
        Conservation_hydro_min = conservation_hydro_min;
    }

    public double getConservation_hydro_max() {
        return Conservation_hydro_max;
    }

    public void setConservation_hydro_max(double conservation_hydro_max) {
        Conservation_hydro_max = conservation_hydro_max;
    }

    public double getConservation_pression_min() {
        return Conservation_pression_min;
    }

    public void setConservation_pression_min(double conservation_pression_min) {
        Conservation_pression_min = conservation_pression_min;
    }

    public double getConservation_pression_max() {
        return Conservation_pression_max;
    }

    public void setConservation_pression_max(double conservation_pression_max) {
        Conservation_pression_max = conservation_pression_max;
    }

    public boolean isConservation_sec() {
        return Conservation_sec;
    }

    public void setConservation_sec(boolean conservation_sec) {
        Conservation_sec = conservation_sec;
    }

    public boolean isConservation_abri() {
        return Conservation_abri;
    }

    public void setConservation_abri(boolean conservation_abri) {
        Conservation_abri = conservation_abri;
    }

    public boolean isCondition_Refus_Si_Endomage() {
        return Condition_Refus_Si_Endomage;
    }

    public void setCondition_Refus_Si_Endomage(boolean condition_Refus_Si_Endomage) {
        Condition_Refus_Si_Endomage = condition_Refus_Si_Endomage;
    }

    public boolean isCondition_Fragile() {
        return Condition_Fragile;
    }

    public void setCondition_Fragile(boolean condition_Fragile) {
        Condition_Fragile = condition_Fragile;
    }

    public boolean isCondition_usage_unique() {
        return Condition_usage_unique;
    }

    public void setCondition_usage_unique(boolean condition_usage_unique) {
        Condition_usage_unique = condition_usage_unique;
    }

    public boolean isCondition_peremption() {
        return Condition_peremption;
    }

    public void setCondition_peremption(boolean condition_peremption) {
        Condition_peremption = condition_peremption;
    }

    public boolean isTracabilite_ref() {
        return Tracabilite_ref;
    }

    public void setTracabilite_ref(boolean tracabilite_ref) {
        Tracabilite_ref = tracabilite_ref;
    }

    public boolean isTracabilite_SN() {
        return Tracabilite_SN;
    }

    public void setTracabilite_SN(boolean tracabilite_SN) {
        Tracabilite_SN = tracabilite_SN;
    }

    public String getContenant() {
        return Contenant;
    }

    public void setContenant(String contenant) {
        Contenant = contenant;
    }

    public boolean isNePasResteriliser() {
        return NePasResteriliser;
    }

    public void setNePasResteriliser(boolean nePasResteriliser) {
        NePasResteriliser = nePasResteriliser;
    }

    public boolean isRisque_voir_notice() {
        return Risque_voir_notice;
    }

    public void setRisque_voir_notice(boolean risque_voir_notice) {
        Risque_voir_notice = risque_voir_notice;
    }

    public boolean isRisque_Voir_Recommandation() {
        return Risque_Voir_Recommandation;
    }

    public void setRisque_Voir_Recommandation(boolean risque_Voir_Recommandation) {
        Risque_Voir_Recommandation = risque_Voir_Recommandation;
    }

    public String getRisque_Substance_presence() {
        return Risque_Substance_presence;
    }

    public void setRisque_Substance_presence(String risque_Substance_presence) {
        Risque_Substance_presence = risque_Substance_presence;
    }

    public String getRisque_Substance_absence() {
        return Risque_Substance_absence;
    }

    public void setRisque_Substance_absence(String risque_Substance_absence) {
        Risque_Substance_absence = risque_Substance_absence;
    }

    public boolean isRisque_latex() {
        return Risque_latex;
    }

    public void setRisque_latex(boolean risque_latex) {
        Risque_latex = risque_latex;
    }

    public boolean isRisque_PHT() {
        return Risque_PHT;
    }

    public void setRisque_PHT(boolean risque_PHT) {
        Risque_PHT = risque_PHT;
    }

    public boolean isMedicament_dotation_urgence() {
        return Medicament_dotation_urgence;
    }

    public void setMedicament_dotation_urgence(boolean medicament_dotation_urgence) {
        Medicament_dotation_urgence = medicament_dotation_urgence;
    }

    public String getDocumentation_Web_Path() {
        return Documentation_Web_Path;
    }

    public void setDocumentation_Web_Path(String documentation_Web_Path) {
        Documentation_Web_Path = documentation_Web_Path;
    }

    public boolean isTemperature_Refrigere() {
        return Temperature_Refrigere;
    }

    public void setTemperature_Refrigere(boolean temperature_Refrigere) {
        Temperature_Refrigere = temperature_Refrigere;
    }

    public boolean isMedicament_Risque() {
        return Medicament_Risque;
    }

    public void setMedicament_Risque(boolean medicament_Risque) {
        Medicament_Risque = medicament_Risque;
    }

    public boolean isRegle_Bon_Usage_Active() {
        return Regle_Bon_Usage_Active;
    }

    public void setRegle_Bon_Usage_Active(boolean regle_Bon_Usage_Active) {
        Regle_Bon_Usage_Active = regle_Bon_Usage_Active;
    }

    public boolean isTemperature_Ambiante() {
        return Temperature_Ambiante;
    }

    public void setTemperature_Ambiante(boolean temperature_Ambiante) {
        Temperature_Ambiante = temperature_Ambiante;
    }

    public boolean isLivret_Therapeutique() {
        return Livret_Therapeutique;
    }

    public void setLivret_Therapeutique(boolean livret_Therapeutique) {
        Livret_Therapeutique = livret_Therapeutique;
    }

    public String getMoment_Injection() {
        return Moment_Injection;
    }

    public void setMoment_Injection(String moment_Injection) {
        Moment_Injection = moment_Injection;
    }

    public String getComment_Injecte() {
        return Comment_Injecte;
    }

    public void setComment_Injecte(String comment_Injecte) {
        Comment_Injecte = comment_Injecte;
    }

    public String getComposition() {
        return Composition;
    }

    public void setComposition(String composition) {
        Composition = composition;
    }

    public double getUI_Conversion() {
        return UI_Conversion;
    }

    public void setUI_Conversion(double UI_Conversion) {
        this.UI_Conversion = UI_Conversion;
    }

    public String getZone_PAD_Defaut() {
        return Zone_PAD_Defaut;
    }

    public void setZone_PAD_Defaut(String zone_PAD_Defaut) {
        Zone_PAD_Defaut = zone_PAD_Defaut;
    }

    public String getEmplacement_PAD_Defaut() {
        return Emplacement_PAD_Defaut;
    }

    public void setEmplacement_PAD_Defaut(String emplacement_PAD_Defaut) {
        Emplacement_PAD_Defaut = emplacement_PAD_Defaut;
    }

    public String getUCD_NomCourt() {
        return UCD_NomCourt;
    }

    public void setUCD_NomCourt(String UCD_NomCourt) {
        this.UCD_NomCourt = UCD_NomCourt;
    }

    public String getPHIE_Synchro() {
        return PHIE_Synchro;
    }

    public void setPHIE_Synchro(String PHIE_Synchro) {
        this.PHIE_Synchro = PHIE_Synchro;
    }

    public String getCodeInconnue() {
        return codeInconnue;
    }

    public void setCodeInconnue(String codeInconnue) {
        this.codeInconnue = codeInconnue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setURGENCE(boolean URGENCE) {
        this.URGENCE = URGENCE;
    }

    public boolean isSuivi_Serialisation() {
        return Suivi_Serialisation;
    }

    public void setSuivi_Serialisation(boolean suivi_Serialisation) {
        Suivi_Serialisation = suivi_Serialisation;
    }

    public boolean isSerialiser_Reception_Delivrance() {
        return Serialiser_Reception_Delivrance;
    }

    public void setSerialiser_Reception_Delivrance(boolean serialiser_Reception_Delivrance) {
        Serialiser_Reception_Delivrance = serialiser_Reception_Delivrance;
    }

    public JSONObject toJson() {
        JSONObject produitJson = new JSONObject();

        try {
            produitJson.put("ID_produit", this.ID_produit);
            produitJson.put("N_interne", this.N_interne);
            produitJson.put("Designation_ext", this.Designation_ext);
            produitJson.put("Categorie", this.Categorie);
            produitJson.put("Taux_de_TVA", this.Taux_de_TVA);
            produitJson.put("Peremption", this.Peremption);
            produitJson.put("Marche_Obtenu", this.Marche_Obtenu);
            produitJson.put("Ordonnance", this.Ordonnance);
            produitJson.put("Cond_achat", this.Cond_achat);
            produitJson.put("Cond_distrib", this.Cond_distrib);
            produitJson.put("Prix_unitaire", this.Prix_unitaire);
            produitJson.put("Seuil_alerte", this.Seuil_alerte);
            produitJson.put("Qte_Reassort", this.Qte_Reassort);
            produitJson.put("Suivi_Lot", this.Suivi_Lot);
            produitJson.put("Valeur_stock_actuel", this.Valeur_stock_actuel);
            produitJson.put("Zone_PUI_Defaut", this.Zone_PUI_Defaut);
            produitJson.put("Ref_fourni", this.Ref_fourni);
            produitJson.put("Code_fourn", this.Code_fourn);
            produitJson.put("Fournisseur", this.Fournisseur);
            produitJson.put("Unite", this.Unite);
            produitJson.put("Reassort", this.Reassort);
            produitJson.put("Stock_clot", this.Stock_clot);
            produitJson.put("Ville", this.Ville);
            produitJson.put("Designation_interne", this.Designation_interne);
            produitJson.put("Type_erreur", this.Type_erreur);
            produitJson.put("A_corriger", this.A_corriger);
            produitJson.put("Inclu_au_panel", this.Inclu_au_panel);
            produitJson.put("Forme", this.Forme);
            produitJson.put("Reassort_Statut", this.Reassort_Statut);
            produitJson.put("Sterile", this.Sterile);
            produitJson.put("Duree_peremption", this.Duree_peremption);
            produitJson.put("Conservation", this.Conservation);
            produitJson.put("Mode_de_Distribution", this.Mode_de_Distribution);
            produitJson.put("Respect_Cond_Achat", this.Respect_Cond_Achat);
            produitJson.put("Stock_Global", this.Stock_Global);
            produitJson.put("Valeur_Stock_Global", this.Valeur_Stock_Global);
            produitJson.put("Cond_franco", this.Cond_franco);
            produitJson.put("Arret_Dis", this.Arret_Dis);
            produitJson.put("Date_Arret_Dis", this.Date_Arret_Dis);
            produitJson.put("Ref_marche", this.Ref_marche);
            produitJson.put("Date_der_photo", this.Date_der_photo);
            produitJson.put("Sterilisation_Mode", this.Sterilisation_Mode);
            produitJson.put("Statut", this.Statut);
            produitJson.put("Secteur", this.Secteur);
            produitJson.put("Devise", this.Devise);
            produitJson.put("Nouveau_PU", this.Nouveau_PU);
            produitJson.put("SYS_DT_MAJ", this.SYS_DT_MAJ);
            produitJson.put("SYS_HEURE_MAJ", this.SYS_HEURE_MAJ);
            produitJson.put("SYS_USER_MAJ", this.SYS_USER_MAJ);
            produitJson.put("Panel", this.Panel);
            produitJson.put("Type_franco", this.Type_franco);
            produitJson.put("Cond_Achat_Gros_volume", this.Cond_Achat_Gros_volume);
            produitJson.put("UCD_Code", this.UCD_Code);
            produitJson.put("Couleur", this.Couleur);
            produitJson.put("RGB_Red", this.RGB_Red);
            produitJson.put("RGB_Green", this.RGB_Green);
            produitJson.put("RGB_Blue", this.RGB_Blue);
            produitJson.put("Inventaire1_PUMP_HT", this.Inventaire1_PUMP_HT);
            produitJson.put("Code_CIP", this.Code_CIP);
            produitJson.put("PUMP_TTC_Exercice_Prec", this.PUMP_TTC_Exercice_Prec);
            produitJson.put("Qte_inventaire_exercice_prec", this.Qte_inventaire_exercice_prec);
            produitJson.put("Stock_Actuel", this.Stock_Actuel);
            produitJson.put("Gratuit", this.Gratuit);
            produitJson.put("Arret_Commande", this.Arret_Commande);
            produitJson.put("Date_arret_com", this.Date_arret_com);
            produitJson.put("Prev_A_commander", this.Prev_A_commander);
            produitJson.put("Qte_a_commander", this.Qte_a_commander);
            produitJson.put("Commentaire", this.Commentaire);
            produitJson.put("PUMP_TTC_derniere_cloture", this.PUMP_TTC_derniere_cloture);
            produitJson.put("Classe_numero", this.Classe_numero);
            produitJson.put("Inscrire_a_ordonnancier", this.Inscrire_a_ordonnancier);
            produitJson.put("Date_Creation_Produit", this.Date_Creation_Produit);
            produitJson.put("Nb_Ligne_Code_Barre", this.Nb_Ligne_Code_Barre);
            produitJson.put("Histo_Prix_Unitaire", this.Histo_Prix_Unitaire);
            produitJson.put("DCI", this.DCI);
            produitJson.put("Commentaire_Commande", this.Commentaire_Commande);
            produitJson.put("Code_LPP", this.Code_LPP);
            produitJson.put("GTIN", this.GTIN);
            produitJson.put("Photo", this.Photo);
            produitJson.put("Poids", this.Poids);
            produitJson.put("Volume", this.Volume);
            produitJson.put("Emplacement_PUI_Defaut", this.Emplacement_PUI_Defaut);
            produitJson.put("Informations_importantes", this.Informations_importantes);
            produitJson.put("Distribution_Nominative_Active", this.Distribution_Nominative_Active);
            produitJson.put("Documentation_Path", this.Documentation_Path);
            produitJson.put("Reapprovisionnement_Classe", this.Reapprovisionnement_Classe);
            produitJson.put("Zone_UF_Defaut", this.Zone_UF_Defaut);
            produitJson.put("Emplacement_UF_Defaut", this.Emplacement_UF_Defaut);
            produitJson.put("Hemadialyse_Reference", this.Hemadialyse_Reference);
            produitJson.put("Medicament_Liste", this.Medicament_Liste);
            produitJson.put("Medicament_CTJ", this.Medicament_CTJ);
            produitJson.put("Materiaux", this.Materiaux);
            produitJson.put("Voie", this.Voie);
            produitJson.put("XForme", this.XForme);
            produitJson.put("Indication_therapeutique", this.Indication_therapeutique);
            produitJson.put("Posologie", this.Posologie);
            produitJson.put("Contre_indications", this.Contre_indications);
            produitJson.put("Effets_indesirables", this.Effets_indesirables);
            produitJson.put("Conservation_temperature_min", this.Conservation_temperature_min);
            produitJson.put("Conservation_temperature_Max", this.Conservation_temperature_Max);
            produitJson.put("Conservation_hydro_min", this.Conservation_hydro_min);
            produitJson.put("Conservation_hydro_max", this.Conservation_hydro_max);
            produitJson.put("Conservation_pression_min", this.Conservation_pression_min);
            produitJson.put("Conservation_pression_max", this.Conservation_pression_max);
            produitJson.put("Conservation_sec", this.Conservation_sec);
            produitJson.put("Conservation_abri", this.Conservation_abri);
            produitJson.put("Condition_Refus_Si_Endomage", this.Condition_Refus_Si_Endomage);
            produitJson.put("Condition_Fragile", this.Condition_Fragile);
            produitJson.put("Condition_usage_unique", this.Condition_usage_unique);
            produitJson.put("Condition_peremption", this.Condition_peremption);
            produitJson.put("Tracabilite_ref", this.Tracabilite_ref);
            produitJson.put("Tracabilite_SN", this.Tracabilite_SN);
            produitJson.put("Contenant", this.Contenant);
            produitJson.put("NePasResteriliser", this.NePasResteriliser);
            produitJson.put("Risque_voir_notice", this.Risque_voir_notice);
            produitJson.put("Risque_Voir_Recommandation", this.Risque_Voir_Recommandation);
            produitJson.put("Risque_Substance_presence", this.Risque_Substance_presence);
            produitJson.put("Risque_Substance_absence", this.Risque_Substance_absence);
            produitJson.put("Risque_latex", this.Risque_latex);
            produitJson.put("Risque_PHT", this.Risque_PHT);
            produitJson.put("Medicament_dotation_urgence", this.Medicament_dotation_urgence);
            produitJson.put("Documentation_Web_Path", this.Documentation_Web_Path);
            produitJson.put("Temperature_Refrigere", this.Temperature_Refrigere);
            produitJson.put("Medicament_Risque", this.Medicament_Risque);
            produitJson.put("Regle_Bon_Usage_Active", this.Regle_Bon_Usage_Active);
            produitJson.put("Temperature_Ambiante", this.Temperature_Ambiante);
            produitJson.put("Livret_Therapeutique", this.Livret_Therapeutique);
            produitJson.put("Moment_Injection", this.Moment_Injection);
            produitJson.put("Comment_Injecte", this.Comment_Injecte);
            produitJson.put("Composition", this.Composition);
            produitJson.put("UI_Conversion", this.UI_Conversion);
            produitJson.put("Zone_PAD_Defaut", this.Zone_PAD_Defaut);
            produitJson.put("Emplacement_PAD_Defaut", this.Emplacement_PAD_Defaut);
            produitJson.put("UCD_NomCourt", this.UCD_NomCourt);
            produitJson.put("CodeInconnue", this.codeInconnue);
            produitJson.put("Suivi_Serialisation", this.Suivi_Serialisation);
            produitJson.put("Serialiser_Reception_Delivrance", this.Serialiser_Reception_Delivrance);
            produitJson.put("PHIE_Synchro", this.PHIE_Synchro);
        } catch (JSONException e) {
            e.printStackTrace();
            produitJson = null;
        }

        return produitJson;
    }

    @Override
    public String toString() {
        return getDesignation_interne();
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (((Produit) obj).getID_produit() == this.getID_produit()) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Produit)) {
            valeurARetourner = false;
        }
        return valeurARetourner;
    }


    @Override
    public int compareTo(Object obj) {
        Produit produit = (Produit) obj;

        if (this.getID_produit() == produit.getID_produit()) {
            return 0;
        } else {
            return this.getID_produit() > produit.getID_produit() ? 1 : -1;
        }
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isURGENCE() {
        return URGENCE;
    }
}
