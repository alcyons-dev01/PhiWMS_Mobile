package fr.alcyons.phiwms_mobile.ParametresUtilisateur;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Fournisseur;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 20/06/2019.
 */

public class ServiceParametreUtilisateurActivity extends ServiceActivity {

    Switch authentificationForte;
    Switch connexionDirecte;
    Switch modeTrace;
    Spinner choixTriListePreparation;
    Spinner choixTriListeRéception;
    Spinner choixTriListeReliquat;
    Spinner choixTriListeRetour;
    Spinner choixTriListeRetourLigne;
    Button jeuEssaiAlcyons;
    LinearLayout afficherInformationAuthentificationForte;
    public Boolean authentificationForteBool;
    public Boolean connexionDirecteBool;
    public Boolean traceMode;
    public String choixTriPreparation;
    public String choixTriReception;
    public String choixTriReliquat;
    public String choixTriRetour;
    public String choixTriRetourLigne;
    public SQLiteDatabase db;
    public DBOpenHelper gestionnaireBDD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_parametres_utilisateur);


        gestionnaireBDD = new DBOpenHelper(ServiceParametreUtilisateurActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();
        authentificationForte = (Switch) findViewById(R.id.authentificationForte);
        modeTrace = (Switch) findViewById(R.id.modeTrace);
        jeuEssaiAlcyons = (Button) findViewById(R.id.jeuEssaiAlcyons);
        authentificationForte = (Switch) findViewById(R.id.authentificationForte);
        connexionDirecte = (Switch) findViewById(R.id.connexionDirecte);
        choixTriListePreparation = (Spinner) findViewById(R.id.choixTriListePreparation);
        choixTriListeRéception = (Spinner) findViewById(R.id.choixTriListeRéception);
        choixTriListeReliquat = (Spinner) findViewById(R.id.choixTriListeReliquat);
        choixTriListeRetour = (Spinner) findViewById(R.id.choixTriListeRetour);
        choixTriListeRetourLigne = (Spinner) findViewById(R.id.choixTriListeRetourLigne);
        afficherInformationAuthentificationForte = (LinearLayout) findViewById(R.id.afficherInformationAuthentificationForte);
        afficherInformationAuthentificationForte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerte.afficherAlerte(ServiceParametreUtilisateurActivity.this, "Information", "Si vous activez l'authentification forte, une notification et un email contenant un code de vérification vous sera envoyé. Il vous suffira de saisir le code dans la zone prévue à cet effet pour vous connecter.", "alerte");
            }
        });

        //on affiche le bouton pour le jeu d'essai que si on est alcyons
        if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
        {
            modeTrace.setVisibility(View.VISIBLE);
            jeuEssaiAlcyons.setVisibility(View.VISIBLE);
            jeuEssaiAlcyons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    creationJeuEssai();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int nbUtilisateur = ParametreUtilisateurOpenHelper.getNbUtilisateur(db);
        if (nbUtilisateur == 0) {
            ParametreUtilisateurOpenHelper.insererParametreUtilisateurEnBDD(db, 0, false, false, false, "Designation", "Numéro de commande", "Categorie", "Numéro de retour", "Designation");
        }

        choixTriPreparation = ParametreUtilisateurOpenHelper.getChoixTriPreparation(db);
        if(choixTriPreparation == null)
        {
            choixTriPreparation = "Designation";
            ParametreUtilisateurOpenHelper.mettreAJourTriPreparation(db, 0, "Designation");
        }

        choixTriReception = ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        if(choixTriReception == null)
        {
            choixTriReception = "Numéro de commande";
            ParametreUtilisateurOpenHelper.mettreAJourTriReception(db, 0, "Numéro de commande");
        }

        choixTriReliquat = ParametreUtilisateurOpenHelper.getChoixTriReception(db);
        if(choixTriReliquat == null)
        {
            choixTriReliquat = "Categorie";
            ParametreUtilisateurOpenHelper.mettreAJourTriReliquat(db, 0, "Categorie");
        }

        choixTriRetour = ParametreUtilisateurOpenHelper.getChoixTriRetour(db);
        if(choixTriRetour == null)
        {
            choixTriRetour = "Numéro de retour";
            ParametreUtilisateurOpenHelper.mettreAJourTriRetour(db, 0, "Numéro de retour");
        }

        choixTriRetourLigne = ParametreUtilisateurOpenHelper.getChoixTriRetourLigne(db);
        if(choixTriRetourLigne == null)
        {
            choixTriRetourLigne = "Designation";
            ParametreUtilisateurOpenHelper.mettreAJourTriRetourLigne(db, 0, "Designation");
        }


        //Affichage de l'authentification forte
        Boolean Authentificationactiver = ParametreUtilisateurOpenHelper.getAuthentificationForte(db);
        if (Authentificationactiver) {
            authentificationForte.setChecked(true);
            authentificationForteBool = true;

        } else {
            authentificationForte.setChecked(false);
            authentificationForteBool = false;
        }

        //Affichage de la connexion direct
        Boolean ConnexionDirectactiver = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
        if (ConnexionDirectactiver) {
            connexionDirecte.setChecked(true);
            connexionDirecteBool = true;

        } else {
            connexionDirecte.setChecked(false);
            connexionDirecteBool = false;
        }

        //Affichage du mode trace
        Boolean ModeTraceActiver = ParametreUtilisateurOpenHelper.getModeTrace(db);
        if (ModeTraceActiver) {
            modeTrace.setChecked(true);
            traceMode = true;

        } else {
            modeTrace.setChecked(false);
            traceMode = false;
        }

        authentificationForte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (authentificationForte.isChecked()) {
                    authentificationForte.setChecked(true);
                    authentificationForteBool = true;

                } else {
                    authentificationForte.setActivated(false);
                    authentificationForteBool = false;
                }

                MajParametreUtilisateur();
            }
        });

        connexionDirecte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(connexionDirecte.isChecked())
                {
                    connexionDirecte.setChecked(true);
                    connexionDirecteBool = true;
                }
                else
                {
                    connexionDirecte.setChecked(false);
                    connexionDirecteBool = false;
                }

                MajParametreUtilisateur();
            }
        });

        modeTrace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(modeTrace.isChecked())
                {
                    modeTrace.setChecked(true);
                    traceMode = true;
                }
                else
                {
                    modeTrace.setChecked(false);
                    traceMode = false;
                }

                MajParametreUtilisateur();
            }
        });

        //gestion du choix du tri des préparations
        ArrayAdapter<CharSequence> adapterPreparation = ArrayAdapter.createFromResource(this, R.array.option_tri_preparation, android.R.layout.simple_spinner_item);
        adapterPreparation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choixTriListePreparation.setAdapter(adapterPreparation);
        if (choixTriPreparation != null) {
            int spinnerPosition = adapterPreparation.getPosition(choixTriPreparation);
            choixTriListePreparation.setSelection(spinnerPosition);
        }
        choixTriListePreparation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                choixTriPreparation = arg0.getItemAtPosition(position).toString();
                MajParametreUtilisateur();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        //gestion du tri des réceptions
        ArrayAdapter<CharSequence> adapterReception = ArrayAdapter.createFromResource(this, R.array.option_tri_reception, android.R.layout.simple_spinner_item);
        adapterReception.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choixTriListeRéception.setAdapter(adapterReception);
        if (choixTriReception != null) {
            int spinnerPosition = adapterReception.getPosition(choixTriReception);
            choixTriListeRéception.setSelection(spinnerPosition);
        }
        choixTriListeRéception.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                choixTriReception = arg0.getItemAtPosition(position).toString();
                MajParametreUtilisateur();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //gestion du tri des reliquat
        ArrayAdapter<CharSequence> adapterReceptionDetail = ArrayAdapter.createFromResource(this, R.array.option_tri_reception_detail, android.R.layout.simple_spinner_item);
        adapterReceptionDetail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choixTriListeReliquat.setAdapter(adapterReceptionDetail);
        if (choixTriReliquat != null) {
            int spinnerPosition = adapterReceptionDetail.getPosition(choixTriReliquat);
            choixTriListeReliquat.setSelection(spinnerPosition);
        }
        choixTriListeReliquat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                choixTriReliquat = arg0.getItemAtPosition(position).toString();
                MajParametreUtilisateur();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //gestion du tri des retour
        ArrayAdapter<CharSequence> adapterRetour = ArrayAdapter.createFromResource(this, R.array.option_tri_retour, android.R.layout.simple_spinner_item);
        adapterRetour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choixTriListeRetour.setAdapter(adapterRetour);
        if (choixTriRetour != null) {
            int spinnerPosition = adapterRetour.getPosition(choixTriRetour);
            choixTriListeRetour.setSelection(spinnerPosition);
        }
        choixTriListeRetour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                choixTriRetour = arg0.getItemAtPosition(position).toString();
                MajParametreUtilisateur();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //gestion du tri des retourLigne
        ArrayAdapter<CharSequence> adapterRetourLigne = ArrayAdapter.createFromResource(this, R.array.option_tri_retour_ligne, android.R.layout.simple_spinner_item);
        adapterRetourLigne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choixTriListeRetourLigne.setAdapter(adapterRetourLigne);
        if (choixTriRetourLigne != null) {
            int spinnerPosition = adapterRetour.getPosition(choixTriRetourLigne);
            choixTriListeRetourLigne.setSelection(spinnerPosition);
        }
        choixTriListeRetourLigne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                choixTriRetourLigne = arg0.getItemAtPosition(position).toString();
                MajParametreUtilisateur();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void MajParametreUtilisateur()
    {
        ParametreUtilisateurOpenHelper.mettreAJourParametre(db, 0, authentificationForteBool, connexionDirecteBool, traceMode, choixTriPreparation, choixTriReception, choixTriReliquat, choixTriRetour, choixTriRetourLigne);
    }

    private void creationJeuEssai()
    {
        //suppression du jeu d'essai précédent
        DepotOpenHelper.supprimerDonneesTest(db);
        ZoneOpenHelper.supprimerDonneesTest(db);
        EmplacementOpenHelper.supprimerDonneesTest(db);
        ProduitOpenHelper.supprimerDonneesTest(db);
        FournisseurOpenHelper.supprimerDonneesTest(db);
        PH_PreparationOpenHelper.supprimerDonneesTest(db);
        PH_Preparation_LigneOpenHelper.supprimerDonneesTest(db);
        RetourOpenHelper.supprimerDonneesTest(db);
        Retour_LigneOpenHelper.supprimerDonneesTest(db);
        CommandeOpenHelper.supprimerDonneesTest(db);
        PH_ReliquatOpenHelper.supprimerDonneesTest(db);

        //Création de deux dépôts, un pui un puf
            Random random_depot = new Random();
            int Depot_UID = random_depot.nextInt();
            if(Depot_UID > 0)
                Depot_UID = Depot_UID*-1;


            Depot depot_essai_puf = new Depot(Depot_UID, "DEPOT_PUF_ALCYONS_ESSAI", "ALCYONS_PUF", "50 avenue du lac Marion", "64200", "Biarritz", "0555", "PUF");
            depot_essai_puf.setEtablissement_UID(117);
            long uid_depot_puf = DepotOpenHelper.insererUnDepotEnBDD(db, depot_essai_puf);

            Depot_UID = random_depot.nextInt();
            if(Depot_UID > 0)
                Depot_UID = Depot_UID*-1;

            Depot depot_essai_pui = new Depot(Depot_UID, "DEPOT_PUI_ALCYONS_ESSAI", "ALCYONS", "50 avenue du lac Marion", "64200", "Biarritz", "0555", "PUI");
            depot_essai_pui.setEtablissement_UID(117);
            long uid_depot_pui = DepotOpenHelper.insererUnDepotEnBDD(db, depot_essai_pui);

        //Création de la zone et de l'emplacement pour le depot puf
            Random random_zone = new Random();
            int zone_id = random_zone.nextInt();
            if(zone_id > 0)
                zone_id = zone_id*-1;

            Depot_Zone zone_essai_puf = new Depot_Zone(zone_id, "ZONE_UF_ALCYONS_ESSAI",0, 0, "", depot_essai_puf.getDepot_UID(), "", depot_essai_puf.getDepot_Reference(), "");
            long uid_zone_puf = ZoneOpenHelper.insererUnDepotZoneEnBDD(db, zone_essai_puf);

            //Création de l'emplacement
            Random random_emplacement = new Random();
            int emplacement_id = random_emplacement.nextInt();
            if(emplacement_id > 0)
                emplacement_id = emplacement_id*-1;

            Depot_Emplacement emplacement_essai_puf = new Depot_Emplacement("EMPLACEMENT_UF_ALCYONS_ESSAI", "", "", "", "", zone_essai_puf.getZoneID(), depot_essai_puf.getDepot_UID(), depot_essai_puf.getDepot_Reference(), "");
            emplacement_essai_puf.set_UID(emplacement_id);
            long uid_emplacement_puf = EmplacementOpenHelper.insererUnDepotEmplacementEnBDD(db, emplacement_essai_puf);

        //Création de la zone et de l'emplacement pour le depot pui
            zone_id = random_zone.nextInt();
            if(zone_id > 0)
                zone_id = zone_id*-1;

            Depot_Zone zone_essai_pui = new Depot_Zone(zone_id, "ZONE_PUI_ALCYONS_ESSAI",0, 0, "", depot_essai_pui.getDepot_UID(), "", depot_essai_pui.getDepot_Reference(), "");
            long uid_zone_pui = ZoneOpenHelper.insererUnDepotZoneEnBDD(db, zone_essai_pui);


            //Création de l'emplacement
            emplacement_id = random_emplacement.nextInt();
            if(emplacement_id > 0)
                emplacement_id = emplacement_id*-1;
            Depot_Emplacement emplacement_essai_pui = new Depot_Emplacement("EMPLACEMENT_PUI_ALCYONS_ESSAI", "", "", "", "", zone_essai_pui.getZoneID(), depot_essai_pui.getDepot_UID(), depot_essai_pui.getDepot_Reference(), "");
            emplacement_essai_pui.set_UID(emplacement_id);
            long uid_emplacement_pui = EmplacementOpenHelper.insererUnDepotEmplacementEnBDD(db, emplacement_essai_pui);

        //Creation du fournisseur ALCYONS
            Random random_fournisseur = new Random();
            int fournisseur_id = random_fournisseur.nextInt();
            if(fournisseur_id > 0)
                fournisseur_id = fournisseur_id*-1;

            Fournisseur fournisseur_essai = new Fournisseur(fournisseur_id, "ALCYONS_Four_01", "ALCYONS_Fournisseur");
            long uid_fournisseur = FournisseurOpenHelper.insererUnFournisseurEnBDD(db, fournisseur_essai);

        //Création du produit
            Random random_produit = new Random();
            int produit_id = random_produit.nextInt();
            if(produit_id > 0)
                produit_id = produit_id*-1;

            Produit produit_essai_medicament = new Produit(produit_id, "Traceur_Medicament_ALCYONS", "ESSAI_ALCYONS", "Traceur_Medicament_ALCYONS", "ALCYONS", "", 1, zone_essai_pui.getZoneName(), zone_essai_puf.getZoneName(), emplacement_essai_pui.getAdressage(), emplacement_essai_puf.getAdressage(), fournisseur_essai.getRaisonSociale());
            produit_essai_medicament.setCond_achat(5);
            produit_essai_medicament.setCond_distrib(5);
            produit_essai_medicament.setRef_fourni("Trac_Med_ALCYONS");
            produit_essai_medicament.setCodeInconnue("Code_Trac_Med_ALCYONS");
            long uid_produit_medicament = ProduitOpenHelper.insererUnProduitEnBDD(db, produit_essai_medicament);

            produit_id = random_produit.nextInt();
            if(produit_id > 0)
                produit_id = produit_id*-1;

            Produit produit_essai_dm = new Produit(produit_id, "Traceur_Dispositif_ALCYONS", "ESSAI_ALCYONS", "Traceur_Dispositif_ALCYONS", "ALCYONS", "", 2, zone_essai_pui.getZoneName(), zone_essai_puf.getZoneName(), emplacement_essai_pui.getAdressage(), emplacement_essai_puf.getAdressage(), fournisseur_essai.getRaisonSociale());
            produit_essai_dm.setCond_achat(2);
            produit_essai_dm.setCond_distrib(2);
            produit_essai_dm.setRef_fourni("Trac_DM_ALCYONS");
            produit_essai_dm.setCodeInconnue("Code_Trac_Dm_ALCYONS");

            long uid_produit_dm = ProduitOpenHelper.insererUnProduitEnBDD(db, produit_essai_dm);

        //Création d'une ph_préparation
            //gestion de la date
            SimpleDateFormat format_date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format_heure = new SimpleDateFormat("HH:mm:ss");

            String heure = format_heure.format(new Date());
            String date = format_date.format(new Date());
            Random random_preparation = new Random();
            int preparation_id = random_preparation.nextInt();
            if(preparation_id > 0)
                preparation_id = preparation_id*-1;

            PH_Preparation preparation_essai = new PH_Preparation(preparation_id, "PHITAG_ALCYONS", date, heure, 10, false, false, "", "ALCYONS_LISTE", depot_essai_puf.getDepot_UID(), depot_essai_puf.getDepot_Reference(), date, heure, utilisateurConnecte.getIdentifiant(), date, depot_essai_pui.getDepot_Reference(), depot_essai_pui.getDepot_UID(), "", date, date, 0, 0, 0, 0, utilisateurConnecte.getIdentifiant(), "A Préparer", date, date, date, false, utilisateurConnecte.getId(), utilisateurConnecte.getId(), 0, 0, 0,0, "");
            long uid_preparation = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, preparation_essai);

        //Création d'un ph_preparation_ligne
            Random random_preparation_ligne = new Random();
            int preparation_ligne_id = random_preparation_ligne.nextInt();
            if(preparation_ligne_id > 0)
                preparation_ligne_id = preparation_ligne_id*-1;

            PH_Preparation_Ligne ph_preparation_ligne_essai = new PH_Preparation_Ligne(preparation_essai.getUID(), preparation_ligne_id, produit_essai_medicament.getID_produit(), produit_essai_medicament.getDesignation_interne(), 20, 0, produit_essai_medicament.getRef_fourni(), zone_essai_pui.getZoneName(), produit_essai_medicament.getCategorie(), 20, (int)produit_essai_medicament.getCond_distrib(), true, "", depot_essai_puf.getDepot_Reference(), 20, 20, 0);
            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne_essai);

            preparation_ligne_id = random_preparation_ligne.nextInt();
            if(preparation_ligne_id > 0)
                preparation_ligne_id = preparation_ligne_id*-1;

            PH_Preparation_Ligne ph_preparation_ligne_essai_dm = new PH_Preparation_Ligne(preparation_essai.getUID(), preparation_ligne_id, produit_essai_dm.getID_produit(), produit_essai_dm.getDesignation_interne(), 40, 0, produit_essai_medicament.getRef_fourni(), zone_essai_pui.getZoneName(), produit_essai_dm.getCategorie(), 40, (int)produit_essai_dm.getCond_distrib(), true, "", depot_essai_puf.getDepot_Reference(), 40, 40, 0);
            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne_essai_dm);

        //Création d'un retour
            Random random_retour = new Random();
            int retour_id = random_retour.nextInt();
            if(retour_id > 0)
                retour_id = retour_id*-1;

            Retour retour_essai = new Retour(retour_id, "ALCYON_Retour", depot_essai_puf.getDepot_Reference(), 0, "Retour_ALCYONS", depot_essai_pui.getDepot_Reference(), "Encours", date, 0, "", "Cassé", "", date, heure, utilisateurConnecte.getIdentifiant(), "Reprise demandée", date, date, depot_essai_puf.getDepot_Reference(), false);
            long uid_retour = RetourOpenHelper.insererUnRetourEnBDD(db, retour_essai);

        //Création d'un retour_ligne
            Random random_retour_ligne = new Random();
            int retour_ligne_id = random_retour_ligne.nextInt();
            if(retour_ligne_id > 0)
                retour_ligne_id = retour_ligne_id * -1;

            Retour_Ligne retour_ligne = new Retour_Ligne(retour_ligne_id, retour_essai.get_UID(), 20, produit_essai_dm.getID_produit(), produit_essai_dm.getRef_fourni(), produit_essai_dm.getFournisseur(), 20, 20, produit_essai_dm.getDesignation_interne(), 10,32);
            long uid_retour_ligne = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retour_ligne);

        //Création d'un verrou
            Random random_verrou_pharmacie = new Random();
            int verrou_id = random_verrou_pharmacie.nextInt();
            if(verrou_id > 0)
                verrou_id = verrou_id*-1;

            PH_Preparation verrou_essai = new PH_Preparation(verrou_id, "PHITAG_ALCYONS_VERROU", date, heure, 0, false, false, "", "ALCYONS_VERROU", depot_essai_puf.getDepot_UID(), depot_essai_puf.getDepot_Reference(), date, heure, utilisateurConnecte.getIdentifiant(), date, depot_essai_pui.getDepot_Reference(), depot_essai_pui.getDepot_UID(), "", date, date, 0, 0, 0, 0, utilisateurConnecte.getIdentifiant(), "Verrouillée", date, date, date, false, utilisateurConnecte.getId(), utilisateurConnecte.getId(), 0, 0, 0,0, "");
            long uid_verrou = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, verrou_essai);

            Random random_verrou_ligne = new Random();
            int verrou_ligne_id = random_verrou_ligne.nextInt();
            if(verrou_ligne_id > 0)
                verrou_ligne_id = verrou_ligne_id*-1;

            PH_Preparation_Ligne verrou_ligne_essai = new PH_Preparation_Ligne(verrou_essai.getUID(), verrou_ligne_id, produit_essai_medicament.getID_produit(), produit_essai_medicament.getDesignation_interne(), 20, 0, produit_essai_medicament.getRef_fourni(), zone_essai_pui.getZoneName(), produit_essai_medicament.getCategorie(), 20, (int)produit_essai_medicament.getCond_distrib(), true, "", depot_essai_puf.getDepot_Reference(), 20, 20, 20);
            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, verrou_ligne_essai);

            verrou_ligne_id = random_verrou_ligne.nextInt();
            if(verrou_ligne_id > 0)
                verrou_ligne_id = verrou_ligne_id*-1;

            PH_Preparation_Ligne verrou_ligne_essai_dm = new PH_Preparation_Ligne(verrou_essai.getUID(), verrou_ligne_id, produit_essai_dm.getID_produit(), produit_essai_dm.getDesignation_interne(), 40, 0, produit_essai_medicament.getRef_fourni(), zone_essai_pui.getZoneName(), produit_essai_dm.getCategorie(), 40, (int)produit_essai_dm.getCond_distrib(), true, "", depot_essai_puf.getDepot_Reference(), 40, 40, 20);
            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, verrou_ligne_essai_dm);

        //Création d'une quarantaine
            Random random_quarantaine = new Random();
            int quarantaine_id = random_quarantaine.nextInt();
            if(quarantaine_id > 0)
                quarantaine_id = quarantaine_id*-1;

            Retour quarantaine_essai = new Retour(quarantaine_id, "ALCYONS_Quarantaine", depot_essai_puf.getDepot_Reference(), 0, "Quarantaine_ALCYONS", depot_essai_pui.getDepot_Reference(), "Encours", date, 0, "", "Cassé", "", date, heure, utilisateurConnecte.getIdentifiant(), "Mise en quarantaine", date, date, depot_essai_puf.getDepot_Reference(), false);
            long uid_quarantaine = RetourOpenHelper.insererUnRetourEnBDD(db, quarantaine_essai);

            Random random_quarantaine_ligne = new Random();
            int quarantaine_ligne_id = random_quarantaine_ligne.nextInt();
            if(quarantaine_ligne_id > 0)
                quarantaine_ligne_id = quarantaine_ligne_id * -1;

            Retour_Ligne quarantaine_ligne = new Retour_Ligne(quarantaine_ligne_id, quarantaine_essai.get_UID(), 20, produit_essai_dm.getID_produit(), produit_essai_dm.getRef_fourni(), produit_essai_dm.getFournisseur(), 20, 20, produit_essai_dm.getDesignation_interne(), 10,32);
            quarantaine_ligne.setQte_Retourner(25);
            quarantaine_ligne.setLot("LOT_ALCYONS_ESSAI");
            quarantaine_ligne.setLot_Retourner("LOT_ALCYONS_ESSAI");
            quarantaine_ligne.setPeremptionDate(date);
            long uid_quarantaine_ligne = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, quarantaine_ligne);

        //Création d'une récéption PAD
            Random random_recepetion_PAD = new Random();
            int reception_pad_id = random_recepetion_PAD.nextInt();
            if(reception_pad_id > 0)
                reception_pad_id = reception_pad_id*-1;

            Commande commande_pad_essai = new Commande(reception_pad_id, "RECALCYONS01", fournisseur_essai.get_UID(), "", 0, 0, 0, date, date, fournisseur_essai.getRaisonSociale(), "L", 0, depot_essai_puf.getDepot_Reference(), depot_essai_puf.getDepot_UID(), "PUI-PAD", date, heure, utilisateurConnecte.getIdentifiant(), 3, false, "ALCYONSESSAI"+date, "ALCYONS001", 1850, "ALCYONS", "IPPALCYONS", date, "BLALCYONSESSAI", 5, 0, 20, "AL", utilisateurConnecte.getId(), date);
            CommandeOpenHelper.insererUneCommandeEnBDD(db, commande_pad_essai);

            Random random_commande_ligne = new Random();
            int reception_pad_ligne_id = random_commande_ligne.nextInt();
            if(reception_pad_ligne_id > 0)
                reception_pad_ligne_id = reception_pad_ligne_id*-1;

            PH_Reliquat reliquat_essai = new PH_Reliquat(reception_pad_ligne_id, produit_essai_medicament.getID_produit(), produit_essai_medicament.getRef_fourni(), 20, produit_essai_medicament.getDesignation_interne(), "UNITE", fournisseur_essai.getRaisonSociale(), fournisseur_essai.get_UID(), 25, 0, 25, false, date, commande_pad_essai.getNumero(), date, produit_essai_medicament.getCond_achat(), (int)produit_essai_medicament.getCond_distrib(), reception_pad_ligne_id, 25, "EUR", depot_essai_puf.getDepot_Reference(), date, heure, "IPPALCYONS", "ALCYONS");
            PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, reliquat_essai);

        //on change le text du bouton, on l'affiche en vert et on ne peut plu cliquer dessus
            jeuEssaiAlcyons.setBackgroundResource(R.color.vert3);
            jeuEssaiAlcyons.setText("Jeu d'essai ALCYONS créé");
            jeuEssaiAlcyons.setTextColor(getResources().getColor(R.color.blanc));
            jeuEssaiAlcyons.setClickable(false);
    }
}
