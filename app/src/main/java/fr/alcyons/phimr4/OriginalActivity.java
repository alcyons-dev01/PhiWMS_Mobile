package fr.alcyons.phimr4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Commande_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.FactureOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Facture_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Histo_Mvt_StockOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Inventaire_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Mvt_StockOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_RetourMotifOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_UtiliserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PerimetreFonctionnelOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Photo_Stock_DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Photo_Stock_EtablissementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Prescription_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RRO_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SYS_Document_TypeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SYS_Mvt_Stock_TypeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_LotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.RetourFournisseur.DetailRetourFournisseurActivity;

public class OriginalActivity extends AppCompatActivity {
    // Création de l'utilisateur connecté
    public Utilisateur utilisateurConnecte = null;
    public Drawable footer;

    // base de donnée
    public SQLiteDatabase db;

    // Les gestionnaires de BDD
    protected DBOpenHelper gestionnaireBDD;
    protected UtilisateurOpenHelper gestionnaireUtilisateur;
    protected DepotOpenHelper gestionnaireDepot;
    protected ElementASynchroniserOpenHelper gestionnaireElementASynchroniser;
    protected EmplacementOpenHelper gestionnaireEmplacement;
    protected PerimetreFonctionnelOpenHelper gestionnairePerimetreFonctionnel;
    protected ProduitOpenHelper gestionnaireProduit;
    protected ServiceOpenHelper gestionnaireService;
    protected ZoneOpenHelper gestionnaireZone;
    protected CommandeOpenHelper gestionnaireCommande;
    protected InventaireOpenHelper gestionnaireInventaire;
    protected Inventaire_LigneOpenHelper gestionnaireInventaire_Ligne;
    protected FournisseurOpenHelper gestionnaireFournisseur;
    protected Commande_LigneOpenHelper gestionnaireCommande_Ligne;
    protected PH_PreparationOpenHelper gestionnairePH_Preparation;
    protected PH_Preparation_LigneOpenHelper gestionnairePH_Preparation_Ligne;
    protected Inventaire_Ligne_TempOpenHelper gestionnaireInventaire_Ligne_Temp;
    protected PreparationOpenHelper gestionnairePreparation;
    protected Preparation_LigneOpenHelper gestionnairePreparation_Ligne;
    protected RetourOpenHelper gestionnaireRetour;
    protected Retour_LigneOpenHelper gestionnaireRetour_Ligne;
    protected StockOpenHelper gestionnaireStock;
    protected Stock_LotOpenHelper gestionnaireStock_Lot;
    protected FactureOpenHelper gestionnaireFacture;
    protected Facture_LigneOpenHelper gestionnaireFacture_Ligne;
    protected Histo_Mvt_StockOpenHelper gestionnaireHisto_Mvt_Stock;
    protected PH_Mvt_StockOpenHelper gestionnairePH_Mvt_Stock;
    protected Photo_Stock_DepotOpenHelper gestionnairePhoto_Stock_Depot;
    protected Photo_Stock_EtablissementOpenHelper gestionnairePhoto_Stock_Etablissement;
    protected Stock_Lot_EmplacementLightOpenHelper gestionnaireStock_Lot_Emplacement;
    protected RRO_LigneOpenHelper gestionnaireRRO_Ligne;
    protected SYS_Document_TypeOpenHelper gestionnaireSYS_Document_Type;
    protected SYS_Mvt_Stock_TypeOpenHelper gestionnaireSYS_Mvt_Stock_Type;
    protected NotificationOpenHelper gestionnaireNotification;
    protected ParametresServeurOpenHelper gestionnaireParametresServeur;
    protected Composants_patientOpenHelper gestionnaireComposants_patient;
    protected Detail_DotOpenHelper gestionnaireDetail_Dot;
    protected Dotation_PatientOpenHelper gestionnaireDotationPatient;
    protected DotationOpenHelper gestionnaireDotation;
    protected PH_PatientOpenHelper gestionnairePH_Patient;
    protected PH_ReassortOpenHelper gestionnairePH_Reassort;
    protected PH_Reassort_LigneOpenHelper gestionnairePH_Reassort_Ligne;
    protected Protocoles_PatientsOpenHelper gestionnaireProtocoles_Patients;
    protected PH_ReliquatOpenHelper gestionnairePH_Reliquat;
    protected PH_RetourMotifOpenHelper gestionnairePH_RetourMotif;
    protected Prescription_patientOpenHelper gestionnairePrescription;
    protected PH_UtiliserOpenHelper gestionnairePH_Utiliser;
    protected PH_SerialisationOpenHelper gestionnaireSerialisation;
    protected PH_Lot_LigneOpenHelper gestionnairePH_Lot_Ligne;

    // Récupération de ce qui a été transmis
    protected Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupération de la BDD
        // Création ou récupération de la BDD si elle existe déjà
        gestionnaireBDD = new DBOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireUtilisateur = new UtilisateurOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireDepot = new DepotOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireElementASynchroniser = new ElementASynchroniserOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireEmplacement = new EmplacementOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePerimetreFonctionnel = new PerimetreFonctionnelOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireProduit = new ProduitOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireService = new ServiceOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireZone = new ZoneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireCommande = new CommandeOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireInventaire = new InventaireOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireInventaire_Ligne = new Inventaire_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireInventaire_Ligne_Temp = new Inventaire_Ligne_TempOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireFournisseur = new FournisseurOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireCommande_Ligne = new Commande_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Preparation = new PH_PreparationOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Preparation_Ligne = new PH_Preparation_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePreparation = new PreparationOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePreparation_Ligne = new Preparation_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireRetour = new RetourOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireRetour_Ligne = new Retour_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireStock = new StockOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireStock_Lot = new Stock_LotOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireFacture = new FactureOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireFacture_Ligne = new Facture_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireHisto_Mvt_Stock = new Histo_Mvt_StockOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Mvt_Stock = new PH_Mvt_StockOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePhoto_Stock_Depot = new Photo_Stock_DepotOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePhoto_Stock_Etablissement = new Photo_Stock_EtablissementOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireStock_Lot_Emplacement = new Stock_Lot_EmplacementLightOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireRRO_Ligne = new RRO_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireSYS_Document_Type = new SYS_Document_TypeOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireSYS_Mvt_Stock_Type = new SYS_Mvt_Stock_TypeOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireNotification = new NotificationOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireParametresServeur = new ParametresServeurOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireComposants_patient = new Composants_patientOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireDetail_Dot = new Detail_DotOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireDotationPatient = new Dotation_PatientOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireDotation = new DotationOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Patient = new PH_PatientOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Reassort = new PH_ReassortOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Reassort_Ligne = new PH_Reassort_LigneOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireProtocoles_Patients = new Protocoles_PatientsOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Reliquat = new PH_ReliquatOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_RetourMotif = new PH_RetourMotifOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePrescription = new Prescription_patientOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnairePH_Utiliser = new PH_UtiliserOpenHelper(OriginalActivity.this,DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        gestionnaireSerialisation = new PH_SerialisationOpenHelper(OriginalActivity.this,DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();

        gestionnaireBDD.onUpgrade(db, 0, 0);

        // Récupération des éléments transmis précédemment
        intent = this.getIntent();
        String service = "";
        // Récupération de l'utilisateur connecté
        if(intent.hasExtra("ServiceCourant"))
        {
            service = intent.getExtras().getString("ServiceCourant");
        }
        utilisateurConnecte = gestionnaireUtilisateur.getUtilisateurByID(db, intent.getExtras().getInt("utilisateurConnecteID"));
        if (utilisateurConnecte == null && !service.contentEquals("Authentification")) {
            Alerte.afficherAlerte(OriginalActivity.this, "Alerte", "L'utilisateur connecté a été perdu, veuillez vous reconnecter", "alerte");
            Intent newIntent = new Intent(OriginalActivity.this, AuthentificationActivity.class);
            OriginalActivity.this.startActivity(newIntent);
            OriginalActivity.this.finish();
            return;
        }

        //if (OutilsGestionConnexionReseau.isServerAccessible(OriginalActivity.this)) {
        //    gestionnaireElementASynchroniser.toutSynchroniser(OriginalActivity.this, db, utilisateurConnecte);
        //}

        if(!service.contentEquals("Authentification"))
        {
            // Récupération de la géolocalisation de l'utilisateur
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Criteria critereRecherche = new Criteria();

            // Pour indiquer la précision voulue
            // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
            critereRecherche.setAccuracy(Criteria.ACCURACY_FINE);

           // Est-ce que le fournisseur peut être payant ?
            critereRecherche.setCostAllowed(false);
            Location location;

            String bestProvider = locationManager.getBestProvider(critereRecherche, true);

            // bloc nécessaire afin qu'Android Studio permette l'utilisation de la géolocalisation
            if (ActivityCompat.checkSelfPermission(OriginalActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OriginalActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                utilisateurConnecte.setLocalisation(location);
            }


             footer = new Drawable() {
                @Override
                public void draw(@NonNull Canvas canvas) {
                }

                @Override
                public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
                }

                @Override
                public void setColorFilter(@Nullable ColorFilter colorFilter) {
                }

                @Override
                public int getOpacity() {
                    return 0;
                }
            };
        }
    }
}
