package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationPleinVideActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.DemandeDotationGlobale.InformationDotationServiceActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DotationAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDemandeDotationGlobaleActivity extends ServiceAvecConnexionActivity {

    private static final String TAG = "ServiceDotationGlobale";

    // ═══════════════════════════════════════════
    // Données métier
    // ═══════════════════════════════════════════
    private ArrayList<Dotation> dotationsListe;
    private DotationAdapter dotationAdapter;
    private int nbPreparationSynchroniser;

    // ═══════════════════════════════════════════
    // UI
    // ═══════════════════════════════════════════
    private ListView dotationListView;
    private LinearLayout lancerScan;
    private TextView textLancerScan;
    private ImageView iconLancerScan;
    private LinearLayout layoutSynchronisation;
    private LinearLayout sousTitreDate;
    private TextView compteurPreparations;
    private TextView nbTotalPreparations;
    private TextView compteurLignes;

    // ═══════════════════════════════════════════
    // Cycle de vie
    // ═══════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_dotation_service);

        bindVues();
        demarrerAnimationScan();
        configurerClicListe();
        configurerBoutonScan();
        configurerBoutonRetour();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (statutConnexion && passageParOnCreate) {
            chargerDepuisApi();
        } else {
            rafraichirAdapter();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, null, null, "Intitulé dotation...");
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Traitement futur du retour scanner si nécessaire
    }

    // ═══════════════════════════════════════════
    // Initialisation UI
    // ═══════════════════════════════════════════
    private void bindVues() {
        dotationListView      = findViewById(R.id.listeView);
        lancerScan            = findViewById(R.id.lancerScan);
        textLancerScan        = findViewById(R.id.textLancerScan);
        iconLancerScan        = findViewById(R.id.iconLancerScan);
        sousTitreDate         = findViewById(R.id.sousTitreDate);
        layoutSynchronisation = findViewById(R.id.layoutSyncronisationPreparations);
        compteurPreparations  = findViewById(R.id.compteurEnregistrementCourant);
        nbTotalPreparations   = findViewById(R.id.nbTotalPreparations);
        compteurLignes        = findViewById(R.id.compteurEnregistrementPLCourant);
    }

    private void demarrerAnimationScan() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textLancerScan.startAnimation(anim);
        iconLancerScan.startAnimation(anim);
    }

    private void configurerClicListe() {
        dotationListView.setOnItemClickListener((parent, view, position, id) -> {
            Dotation dotationSelectionnee = dotationAdapter.listeDotation.get(position);
            PH_Preparation preparation = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(
                    db,
                    "Dotation Globale : " + dotationSelectionnee.getIntitule(),
                    dotationSelectionnee.getDateLivraison());

            boolean peutOuvrir = preparation == null
                    || preparation.getStatut().contentEquals("En instance")
                    || preparation.getStatut().contentEquals("En cours de régularisation");

            if (peutOuvrir) {
                startActivity(buildDetailIntent(dotationSelectionnee, preparation));
                finish();
            }
        });
    }

    private void configurerBoutonScan() {
        lancerScan.setOnClickListener(v -> {
            Intent scanIntent;
            Bundle bundle = super.getBundle();

            if (Build.MANUFACTURER.contains("Zebra Technologies")
                    || Build.MANUFACTURER.toLowerCase().contains("honeywell")) {
                scanIntent = new Intent(this, ScannerPreparationPleinVideActivity.class);
                bundle.putInt("scannerContexteInt", R.string.scannerContextePleinVide);
            } else {
                scanIntent = new Intent(this, BarcodeCaptureActivity.class);
            }

            bundle.putString("contexte", String.valueOf(R.string.scannerContextePleinVide));
            bundle.putBoolean("isBoutonSuppressionExistant", true);
            bundle.putBoolean("modeRafale", true);
            bundle.putStringArrayList("stringList", new ArrayList<>());
            bundle.putStringArrayList("detailDotPleinVide_AdressageList", new ArrayList<>());
            bundle.putString("dotationIntitule", "");
            scanIntent.putExtras(bundle);
            scanIntent.putExtra("designationArrayList", new LinkedHashMap<String, String>());

            startActivityForResult(scanIntent, CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION);
        });
    }

    private void configurerBoutonRetour() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                naviguerVersNavigation();
            }
        });
    }

    // ═══════════════════════════════════════════
    // Chargement des données
    // ═══════════════════════════════════════════
    private void chargerDepuisApi() {
        if (!swipeRefreshLayout.isRefreshing()) {
            afficherSpinner(this, LayoutInflater.from(this));
        }

        String url = ParametresServeurOpenHelper.getPartieCommuneUrls(db)
                + DBOpenHelper.Urls.uriRequeteDotationGlobaleCourant;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::traiterReponseApi,
                error -> {
                    Log.e(TAG, "Erreur Volley", error);
                    Alerte.afficherAlerte(this, "Erreur",
                            "Veuillez contacter la société Alcyons (erreur Volley : Dotation Service)",
                            "alerte");
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
                return headers;
            }
        };

        request.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(this).add(request);
        passageParOnCreate = false;
    }

    // ═══════════════════════════════════════════
    // Traitement réponse API
    // ═══════════════════════════════════════════
    private void traiterReponseApi(JSONObject response) {
        try {
            int nbResultat = response.getInt("resultCount");

            if (nbResultat == 0) {
                gererReponseVide(response);
            } else {
                traiterPreparations(response.getJSONArray("PH_Preparations"));
            }

            rafraichirAdapter();
        } catch (JSONException e) {
            Log.e(TAG, "Erreur parsing JSON", e);
        }
    }

    private void gererReponseVide(JSONObject response) throws JSONException {
        String erreur = response.getString("erreur");

        if (erreur.equals(getString(R.string.tokenInvalide))) {
            Alerte.afficherAlerte(this, "Alerte",
                    "Votre session a expirée, veuillez vous reconnecter.", "alerte");
            finishAffinity();
            startActivity(new Intent(this, AuthentificationActivity.class));
            return;
        }

        List<Dotation> liste = DotationOpenHelper.getDotationGlobale(db);
        if (liste.isEmpty()) {
            vide = true;
            nomServiceVide = "Dotation Service";
            naviguerVersNavigation();
        }
    }

    private void traiterPreparations(JSONArray preparationsArray) throws JSONException {
        List<Integer> uidsEnInstance = PH_PreparationOpenHelper.getUIDDotationGlobaleEnInstance(db);

        for (int i = 0; i < preparationsArray.length(); i++) {
            traiterUnePreparation(preparationsArray.getJSONObject(i), uidsEnInstance);
        }
    }

    private void traiterUnePreparation(JSONObject obj, List<Integer> uidsEnInstance)
            throws JSONException {

        String nomListe = obj.getString("Liste");
        int uid         = obj.getInt("UID");
        String statut   = obj.getString("Statut");
        String dateLivr = obj.getString("LivraisonPrevueDate");

        // Nettoyage des UIDs en instance
        int pos = uidsEnInstance.indexOf(uid);
        if (pos != -1 && !statut.contentEquals("En cours de préparation")) {
            uidsEnInstance.remove(pos);
        }

        // Upsert PH_Preparation
        PH_Preparation preparation = PH_PreparationOpenHelper
                .getDemandeDotationGlobaleEnInstance(db, nomListe, dateLivr);

        if (preparation == null) {
            preparation = new PH_Preparation(obj);
            PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, preparation);
        } else {
            preparation.setStatut(statut);
            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, preparation);
        }

        traiterLignes(obj.getJSONArray("ph_preparation_lignes"), preparation);
    }

    private void traiterLignes(JSONArray lignesArray, PH_Preparation preparation)
            throws JSONException {

        for (int j = 0; j < lignesArray.length(); j++) {
            JSONObject ligneObj = lignesArray.getJSONObject(j);
            int codeProduit     = ligneObj.getInt("produitID");

            PH_Preparation_Ligne ligne = PH_Preparation_LigneOpenHelper
                    .getPHPreparationLignesParPHPreparationAndProduit(db, preparation, codeProduit);

            if (ligne == null) {
                ligne = new PH_Preparation_Ligne(ligneObj, preparation.getUID());
                PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ligne);
            } else {
                ligne.setQte_APreparer(ligneObj.getInt("Qte_APreparer"));
                ligne.setQte_Demander(ligneObj.getInt("Qte_Demander"));
                ligne.setQte_RAL(ligneObj.getInt("Qte_RAL"));
                ligne.setQte_StockSaisie(ligneObj.getInt("Qte_StockSaisie"));
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
            }
        }
    }

    // ═══════════════════════════════════════════
    // Adapter
    // ═══════════════════════════════════════════
    private void rafraichirAdapter() {
        arreterSpinner();

        dotationsListe = DotationOpenHelper.getDotationGlobale(db);

        if (dotationsListe.isEmpty()) {
            vide = true;
            nomServiceVide = "Dotation Service";
            finish();
            return;
        }

        nbPreparationSynchroniser = 0;
        nbTotalPreparations.setText(String.valueOf(dotationsListe.size()));

        enrichirDatesLivraison();
        trierParDate();

        ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, false);

        dotationAdapter = new DotationAdapter(this, db, utilisateurConnecte);
        peuplerAdapter();

        dotationListView.setDivider(footer);
        dotationListView.setAdapter(dotationAdapter);
        layoutSynchronisation.setVisibility(View.GONE);
        sousTitreDate.setVisibility(View.VISIBLE);

        invalidateOptionsMenu();
    }

    private void enrichirDatesLivraison() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (Dotation dotation : dotationsListe) {
            String date = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());

            if (date == null || date.isEmpty()) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 1);
                date = dateFormat.format(cal.getTime());
            }

            dotation.setDateLivraison(date);

            PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(
                    db, "Dotation Globale : " + dotation.getIntitule(), date);

            majProgressPrincipal();
        }
    }

    private void trierParDate() {
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        dotationsListe.sort((o1, o2) -> {
            try {
                return Objects.requireNonNull(format.parse(o1.getDateLivraison()))
                        .compareTo(format.parse(o2.getDateLivraison()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void peuplerAdapter() {
        List<String> datesVues = new ArrayList<>();
        List<String> depotsVus = new ArrayList<>();

        for (Dotation dotation : dotationsListe) {
            boolean nouvelleDate = !datesVues.contains(dotation.getDateLivraison());
            boolean nouveauDepot = !depotsVus.contains(dotation.getRef_Depot());

            if (nouvelleDate || nouveauDepot) {
                datesVues.add(dotation.getDateLivraison());
                depotsVus.add(dotation.getRef_Depot());
                dotationAdapter.addSectionHeaderItem(dotation);
            }

            dotationAdapter.addItem(dotation);
        }
    }

    // ═══════════════════════════════════════════
    // Navigation
    // ═══════════════════════════════════════════
    private void naviguerVersNavigation() {
        Intent intent = new Intent(this, NavigationActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    @NonNull
    private Intent buildDetailIntent(Dotation dotation, PH_Preparation preparation) {
        Bundle bundle = super.getBundle();
        bundle.putInt("depotSelectionneID", dotation.getDepot_UID());
        bundle.putInt("dotationSelectionneID", dotation.getPhiMR4UUID());

        if (preparation != null) {
            bundle.putInt("phPreparationID", preparation.getUID());
        }

        Intent intent = new Intent(this, InformationDotationServiceActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    // ═══════════════════════════════════════════
    // Progression
    // ═══════════════════════════════════════════
    private void majProgressPrincipal() {
        nbPreparationSynchroniser++;
        compteurPreparations.setText(String.valueOf(nbPreparationSynchroniser));
    }

    private void majProgressSecondaire(int compteurCourant) {
        compteurLignes.setText(String.valueOf(compteurCourant));
    }
}