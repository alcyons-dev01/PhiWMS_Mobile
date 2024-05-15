package fr.alcyons.phiwms_mobile.DepotSelecteur;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DepotAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GPSTracker;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhraseSnackbar;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
public class DepotSelecteurSimpleActivity extends ServiceActivity {
    List<Depot> depotList;
    ListView depotListView;
    DepotAdapter adapter;
    FloatingActionButton boutonGeolocalisation;
    boolean listeReduite;
    boolean vide;
    protected DepotOpenHelper gestionnaireDepot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot_selecteur_simple);
        gestionnaireDepot = new DepotOpenHelper(DepotSelecteurSimpleActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);

        vide = intent.getBooleanExtra("vide", false);
        if(vide)
        {
            afficherSnackBarVide(Objects.requireNonNull(intent.getExtras()).getString("service"));
        }

        String depotType = Objects.requireNonNull(getIntent().getExtras()).getString("depotType");

        depotListView = (ListView) findViewById(R.id.listeView);

        depotListView.setOnItemClickListener((parent, view, position, id) -> {
            Depot depotSelectionne = (Depot) adapter.getItem(position);

            Intent resultIntent = new Intent();
            Bundle extras = new Bundle();
            assert depotSelectionne != null;
            extras.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
            resultIntent.putExtras(extras);
            setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
            DepotSelecteurSimpleActivity.this.finish();
        });

        //Récupération du booleen pour savoir si on est dans le service Plein Vide
        listeReduite = intent.getBooleanExtra("listeReduite", false);

        // Récupération de tous les dépots de type depotType
        depotList = new ArrayList<>();

        //soit on récupère les dépôt par depottype, soit on récupère la liste des dépôts qui contienne du pleinvide
        if (listeReduite) {
            List<String> depotReferenceList;
            depotReferenceList = intent.getStringArrayListExtra("depotReferenceList");
            //on récupère la liste des depot
            assert depotReferenceList != null;
            for (String depotReference : depotReferenceList) {
                Depot depot = DepotOpenHelper.getDepotParReference(db, depotReference);
                depotList.add(depot);
            }
        } else {
            depotList = DepotOpenHelper.getDepotsParType(db, depotType);
        }

        depotList.sort(Comparator.comparing(Depot::getNom));

        // Affichage du nombre de dépot
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(depotList.size()));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new DepotAdapter(DepotSelecteurSimpleActivity.this, depotList, utilisateurConnecte);
        depotListView.setAdapter(adapter);

        boutonGeolocalisation = (FloatingActionButton) findViewById(R.id.boutonGeolocalisation);
        boutonGeolocalisation.setOnClickListener(view -> {

            double latitude = 0;
            double longitude = 0;

            // create class object
            GPSTracker gps = new GPSTracker(DepotSelecteurSimpleActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                Toast.makeText(getApplicationContext(), "Votre localisation est - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                gps.showSettingsAlert();
            }

            ArrayList<DepotOpenHelper.CustomObject> depotLesPlusProche = gestionnaireDepot.getDepotLesPlusProche(latitude, longitude, depotList);
            if (latitude == 0 && longitude == 0) {
                Alerte.afficherAlerte(DepotSelecteurSimpleActivity.this, "Alerte", "Nous n'avons pas réussi à vous géolocaliser. Veuillez sélectionner manuellement votre dépot.", "alerte");
            } else if (depotLesPlusProche.isEmpty()) {
                Alerte.afficherAlerte(DepotSelecteurSimpleActivity.this, "Alerte", "Aucun dépot n'est situé à moins de 1km. Veuillez sélectionner manuellement votre dépot.", "alerte");
            } else if (depotLesPlusProche.size() == 1) {
                Depot depotLePlusProche = DepotOpenHelper.getDepotParID(db, depotLesPlusProche.get(0).getKey());
                boolean choixUtilisateur = Alerte.afficherAlerte(DepotSelecteurSimpleActivity.this, "Alerte", "Vous allez être redirigé vers le dépôt " + depotLePlusProche.getNom() + ". Voulez vous continuer ?", "OuiNon");

                if (choixUtilisateur) {
                    Intent resultIntent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putInt("depotUID_Selectionne", depotLePlusProche.getDepot_UID());
                    resultIntent.putExtras(extras);
                    setResult(CodesEchangesActivites.RESULT_SELECTION_DEPOT, resultIntent);
                    finish();
                }
            } else {
                Alerte.afficherAlerte(DepotSelecteurSimpleActivity.this, "Alerte", "Plusieurs dépots se situent à moins de 1km. Veuillez sélectionner manuellement votre dépot.", "alerte");

                depotList = new ArrayList<>();

                for (DepotOpenHelper.CustomObject customObject : depotLesPlusProche) {
                    Depot depotLePlusProche = DepotOpenHelper.getDepotParID(db, customObject.getKey());
                    depotList.add(depotLePlusProche);
                }

                depotList.sort(Comparator.comparing(Depot::getNom));

                // Affichage du nombre de dépot
                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(depotList.size()));

                // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
                adapter = new DepotAdapter(DepotSelecteurSimpleActivity.this, depotList, utilisateurConnecte);
                depotListView.setAdapter(adapter);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(DepotSelecteurSimpleActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                DepotSelecteurSimpleActivity.this.startActivity(intent);
                DepotSelecteurSimpleActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom dépot...");
        return true;
    }


    public void afficherSnackBarVide(String service) {
        String phrase = OutilsGestionPhraseSnackbar.obtenirPhraseSnackbar(service);
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>"+phrase+"</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
