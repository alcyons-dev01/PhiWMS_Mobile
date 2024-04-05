package fr.alcyons.phiwms_mobile.Classes;

import android.database.Cursor;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;

import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses.recupererBooleen;

import fr.alcyons.phiwms_mobile.Outils.OutilsGestionClasses;

/**
 * Created by quentinlanusse on 12/04/2017.
 */

public class Utilisateur implements Serializable {

    private int id;
    private String identifiant;
    private String mdp;
    private String mail;
    private String nom;
    private String prenom;
    private boolean active;
    private int planHabilitation;
    private boolean bloque;
    private List<Service> servicesHabilites;
    private List<String> channels;
    private String token;
    private double longitude;
    private double latitude;
    private int depot_UID;
    private String Etablissement;

    private int EtablissementId;
    private int lastPerimetreId;
    private int phiwms_mobileUUID = -1;


    public Utilisateur(int id, String identifiant, String mdp, String mail, String nom, String prenom, boolean active, int planHabilitation, boolean bloque, int depot_UID, String token, String Etablissement, int perimetreFonctionnelId, int EtablissementId) {
        this.id = id;
        this.identifiant = identifiant;
        this.mdp = mdp;
        this.mail = mail;
        this.nom = nom;
        this.prenom = prenom;
        this.active = active;
        this.planHabilitation = planHabilitation;
        this.bloque = bloque;
        this.token = token;
        this.servicesHabilites = null;
        this.channels = null;
        this.depot_UID = depot_UID;
        this.Etablissement = Etablissement;
        this.lastPerimetreId = perimetreFonctionnelId;
        this.EtablissementId = EtablissementId;
    }

    public Utilisateur(int id, String identifiant, String mdp, String mail, String nom, String prenom, boolean active, int planHabilitation, boolean bloque, int depot_UID, String token, List<Service> servicesHabilites, List<String> channels, String Etablissement, int perimetreFonctionnelId, int EtablissementId) {
        this.id = id;
        this.identifiant = identifiant;
        this.mdp = mdp;
        this.mail = mail;
        this.nom = nom;
        this.prenom = prenom;
        this.active = active;
        this.planHabilitation = planHabilitation;
        this.bloque = bloque;
        this.token = token;
        this.servicesHabilites = servicesHabilites;
        this.channels = channels;
        this.depot_UID = depot_UID;
        this.Etablissement = Etablissement;
        this.lastPerimetreId = perimetreFonctionnelId;
        this.EtablissementId = EtablissementId;
    }

    public Utilisateur(Cursor cursor) {
        this.id = cursor.getInt(UtilisateurOpenHelper.Constantes.NUM_COL_ID_UTILISATEUR);
        this.identifiant = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_IDENTIFIANT_UTILISATEUR);
        this.mdp = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_MDP_UTILISATEUR);
        this.mail = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_MAIL_UTILISATEUR);
        this.nom = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_NOM_UTILISATEUR);
        this.prenom = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_PRENOM_UTILISATEUR);
        this.active = OutilsGestionClasses.recupererBooleen(cursor, UtilisateurOpenHelper.Constantes.NUM_COL_ACTIVE_UTILISATEUR);
        this.planHabilitation = cursor.getInt(UtilisateurOpenHelper.Constantes.NUM_COL_PLAN_HABILITATION_UTILISATEUR);
        this.bloque = OutilsGestionClasses.recupererBooleen(cursor, UtilisateurOpenHelper.Constantes.NUM_COL_BLOQUE_UTILISATEUR);
        this.depot_UID = cursor.getInt(UtilisateurOpenHelper.Constantes.NUM_COL_DEPOT_UID_UTILISATEUR);
        this.token = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_TOKEN_UTILISATEUR);
        this.Etablissement = cursor.getString(UtilisateurOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_UTILISATEUR);
        this.lastPerimetreId = cursor.getInt(UtilisateurOpenHelper.Constantes.NUM_COL_LAST_PERIMETRE);
        this.EtablissementId = cursor.getInt(UtilisateurOpenHelper.Constantes.NUM_COL_ETABLISSEMENT_ID_UTILISATEUR);
        this.phiwms_mobileUUID = cursor.getInt(DBOpenHelper.Constantes.NUM_COL_phiwms_mobileUUID);
    }

    public Utilisateur(JSONObject utilisateurJson) {
        try {
            // Récupération des attributs de l'utilisateur
            this.id = utilisateurJson.getInt("id");
            this.identifiant = utilisateurJson.getString("identifiant");
            this.mdp = utilisateurJson.getString("mdp");
            this.mail = utilisateurJson.getString("mail");
            this.nom = utilisateurJson.getString("nom");
            this.prenom = utilisateurJson.getString("prenom");
            this.active = OutilsGestionClasses.recupererBooleen(utilisateurJson, "active");
            this.planHabilitation = utilisateurJson.getInt("planHabilitation");
            this.bloque = OutilsGestionClasses.recupererBooleen(utilisateurJson, "bloque");
            this.depot_UID = utilisateurJson.getInt("Depot_UID");
            this.Etablissement = utilisateurJson.getString("etablissement");
            this.EtablissementId = utilisateurJson.getInt("Etablissement_UID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public int getphiwms_mobileUUID() {
        return phiwms_mobileUUID;
    }

    public void setphiwms_mobileUUID(int phiwms_mobileUUID) {
        this.phiwms_mobileUUID = phiwms_mobileUUID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLocalisation(Location localisation) {
        this.latitude = localisation.getLatitude();
        this.longitude = localisation.getLongitude();
    }

    public void setLocalisation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPlanHabilitation() {
        return planHabilitation;
    }

    public void setPlanHabilitation(int planHabilitation) {
        this.planHabilitation = planHabilitation;
    }

    public boolean isBloque() {
        return bloque;
    }

    public void setBloque(boolean bloque) {
        this.bloque = bloque;
    }

    public int getDepot_UID() {
        return this.depot_UID;
    }

    public void setDepot_UID(int depot_UID) {
        this.depot_UID = depot_UID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEtablissement() {
        return Etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.Etablissement = etablissement;
    }

    public int getEtablissementId() {
        return EtablissementId;
    }

    public void setEtablissementId(int etablissementId) {
        this.EtablissementId = etablissementId;
    }
    public int getLastPerimetre() {
        return lastPerimetreId;
    }

    public void setLastPerimetre(int perimetreId) {
        this.lastPerimetreId = perimetreId;
    }

    public List<Service> getServicesHabilites() {
        return servicesHabilites;
    }

    public void setServicesHabilites(List<Service> servicesHabilites) {
        this.servicesHabilites = servicesHabilites;
    }

    public List<PerimetreFonctionnel> recupererListePerimetresFonctionnelHabilites() {
        List<PerimetreFonctionnel> listePerimetresFonctionnels = new ArrayList<>();
        if(this.getServicesHabilites() != null)
        {
            for (Service serviceCourant : this.getServicesHabilites()) {
                PerimetreFonctionnel perimetreFonctionDuServiceCourant = new PerimetreFonctionnel(serviceCourant.getIdPerimetreFonctionnel(), serviceCourant.getNomPerimetrefonctionnel());
                if (listePerimetresFonctionnels.contains(perimetreFonctionDuServiceCourant) != true) {
                    if(!perimetreFonctionDuServiceCourant.getNom().contentEquals("Accès direct"))
                    {
                        listePerimetresFonctionnels.add(perimetreFonctionDuServiceCourant);
                    }
                }
            }
        }

        return listePerimetresFonctionnels;
    }

    public List<PerimetreFonctionnel> getAllPerimetres() {
        List<PerimetreFonctionnel> listePerimetresFonctionnels = new ArrayList<>();
        if(this.getServicesHabilites() != null)
        {
            for (Service serviceCourant : this.getServicesHabilites()) {
                PerimetreFonctionnel perimetreFonctionDuServiceCourant = new PerimetreFonctionnel(serviceCourant.getIdPerimetreFonctionnel(), serviceCourant.getNomPerimetrefonctionnel());
                if (!listePerimetresFonctionnels.contains(perimetreFonctionDuServiceCourant))
                {
                    listePerimetresFonctionnels.add(perimetreFonctionDuServiceCourant);
                }
            }
        }

        return listePerimetresFonctionnels;
    }

    public PerimetreFonctionnel recupererAccesDirectPerimetresFonctionnelHabilites() {
        PerimetreFonctionnel perimetreFonctionnel = null;
        if(this.getServicesHabilites() != null)
        {
            for (Service serviceCourant : this.getServicesHabilites()) {
                PerimetreFonctionnel perimetreFonctionDuServiceCourant = new PerimetreFonctionnel(serviceCourant.getIdPerimetreFonctionnel(), serviceCourant.getNomPerimetrefonctionnel());
                if(perimetreFonctionDuServiceCourant.getNom().contentEquals("Accès direct"))
                {
                    perimetreFonctionnel = perimetreFonctionDuServiceCourant;
                    break;
                }
            }
        }

        return perimetreFonctionnel;
    }

    public List<PerimetreFonctionnel> recupererPerimetreServiceParametreUtilisateur()
    {
        List<PerimetreFonctionnel> listePerimetresFonctionnels = new ArrayList<>();
        if(this.getServicesHabilites() != null)
        {
            for (Service serviceCourant : this.getServicesHabilites()) {
                if(serviceCourant.getNom().contentEquals("Paramètres utilisateur"))
                {
                    PerimetreFonctionnel perimetreFonctionDuServiceCourant = new PerimetreFonctionnel(serviceCourant.getIdPerimetreFonctionnel(), serviceCourant.getNomPerimetrefonctionnel());
                    if (listePerimetresFonctionnels.contains(perimetreFonctionDuServiceCourant) != true) {
                        listePerimetresFonctionnels.add(perimetreFonctionDuServiceCourant);
                    }
                }
            }
        }

        return listePerimetresFonctionnels;
    }

    @Override
    public boolean equals(Object obj) {
        boolean valeurARetourner = false;
        if (obj == this) {
            valeurARetourner = true;
        }

        if (!(obj instanceof Utilisateur)) {
            valeurARetourner = false;
        }
        return valeurARetourner;

    }


    public List<Service> getServicesUtilisateurParPerimetreFonctionnel(PerimetreFonctionnel perimetreFonctionnel) {
        List<Service> listeARetourner = new ArrayList<>();
        for (Service serviceCourant : this.getServicesHabilites()
                ) {
            if (serviceCourant.getNomPerimetrefonctionnel().equals(perimetreFonctionnel.getNom())) {
                listeARetourner.add(serviceCourant);
            }
        }
        return listeARetourner;
    }

    public List<Service> getServicesParametreUtilisateur(PerimetreFonctionnel perimetreFonctionnel) {
        List<Service> listeARetourner = new ArrayList<>();
        for (Service serviceCourant : this.getServicesHabilites())
        {
            if(serviceCourant.getNom().contentEquals("Paramètres utilisateur"))
            {
                if (serviceCourant.getNomPerimetrefonctionnel().equals(perimetreFonctionnel.getNom()))
                {
                    listeARetourner.add(serviceCourant);
                }
            }
        }
        return listeARetourner;
    }
}
