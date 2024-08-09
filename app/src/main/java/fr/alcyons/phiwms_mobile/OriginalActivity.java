package fr.alcyons.phiwms_mobile;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
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

import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Commande_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PatientOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_UtiliserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.SYS_Document_TypeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.SYS_Mvt_Stock_TypeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_LotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;

public class OriginalActivity extends MainActivity {
    // Création de l'utilisateur connecté
    public Utilisateur utilisateurConnecte = null;
    public Drawable footer;

    // base de donnée
    public SQLiteDatabase db;

    // Les gestionnaires de BDD
    protected DBOpenHelper gestionnaireBDD;
    // Récupération de ce qui a été transmis
    protected Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Récupération de la BDD
        // Création ou récupération de la BDD si elle existe déjà
        gestionnaireBDD = new DBOpenHelper(OriginalActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBDD.openDB();

        gestionnaireBDD.onUpgrade(db, 0, 0);

        // Récupération des éléments transmis précédemment
        intent = this.getIntent();
        String service = "";
        // Récupération de l'utilisateur connecté
        if(intent.hasExtra("ServiceCourant"))
        {
            service = Objects.requireNonNull(intent.getExtras()).getString("ServiceCourant");
        }

        utilisateurConnecte = UtilisateurOpenHelper.getUtilisateurByID(db, Objects.requireNonNull(intent.getExtras()).getInt("utilisateurConnecteID"));
        if (utilisateurConnecte == null) {
            assert service != null;
            if (!service.contentEquals("Authentification")) {
                Alerte.afficherAlerte(OriginalActivity.this, "Alerte", "L'utilisateur connecté a été perdu, veuillez vous reconnecter", "alerte");
                Intent newIntent = new Intent(OriginalActivity.this, AuthentificationActivity.class);
                OriginalActivity.this.startActivity(newIntent);
                OriginalActivity.this.finish();
                return;
            }
        }

        assert service != null;
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
            if(bestProvider != null)
            {
                location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    utilisateurConnecte.setLocalisation(location);
                }
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
                    return PixelFormat.UNKNOWN;
                }
            };
        }
    }
}
