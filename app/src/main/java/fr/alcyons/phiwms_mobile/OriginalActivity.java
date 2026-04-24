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

import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Interfaces.DatabaseProvider;
import fr.alcyons.phiwms_mobile.Outils.Alerte;

public class OriginalActivity extends MainActivity implements DatabaseProvider {

    // Utilisateur connecté
    public Utilisateur utilisateurConnecte = null;
    public Drawable footer;

    // Base de données
    public SQLiteDatabase db;
    protected DBOpenHelper gestionnaireBDD;

    // Intent
    protected Intent intent;

    // --- Implementation de DatabaseProvider ---
    @Override
    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation de la BDD
        gestionnaireBDD = new DBOpenHelper(
                OriginalActivity.this,
                DBOpenHelper.Constantes.NOM_BDD,
                null,
                DBOpenHelper.Constantes.DATABASE_VERSION
        );
        db = gestionnaireBDD.openDB();
        gestionnaireBDD.onUpgrade(db, 0, 0);

        // Récupération de l'intent et du service courant
        intent = this.getIntent();
        String service = "";

        if (intent.hasExtra("ServiceCourant")) {
            service = Objects.requireNonNull(intent.getExtras()).getString("ServiceCourant");
        }

        // Récupération de l'utilisateur connecté
        utilisateurConnecte = UtilisateurOpenHelper.getUtilisateurByID(
                db,
                Objects.requireNonNull(intent.getExtras()).getInt("utilisateurConnecteID")
        );

        if (utilisateurConnecte == null) {
            assert service != null;
            if (!service.contentEquals("Authentification")) {
                Alerte.afficherAlerte(
                        OriginalActivity.this,
                        "Alerte",
                        "L'utilisateur connecté a été perdu, veuillez vous reconnecter",
                        "alerte"
                );
                Intent newIntent = new Intent(OriginalActivity.this, AuthentificationActivity.class);
                OriginalActivity.this.startActivity(newIntent);
                OriginalActivity.this.finish();
                return;
            }
        }

        assert service != null;
        if (!service.contentEquals("Authentification")) {
            initialiserGeolocalisation();
        }
    }

    // --- Géolocalisation extraite dans une méthode dédiée ---
    private void initialiserGeolocalisation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria critereRecherche = new Criteria();
        critereRecherche.setAccuracy(Criteria.ACCURACY_FINE);
        critereRecherche.setCostAllowed(false);

        String bestProvider = locationManager.getBestProvider(critereRecherche, true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (bestProvider != null) {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                utilisateurConnecte.setLocalisation(location);
            }
        }

        footer = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {}

            @Override
            public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {}

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {}

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
    }
}