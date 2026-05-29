package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;
import fr.alcyons.phiwms_mobile.DemandeReassort.InformationDemandeReassortActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ReassortAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDemandeReassortActivity extends ServiceAvecConnexionActivity {

    private static final String TAG = "ServiceDemandeReassort";

    // ═══════════════════════════════════════════
    // Données métier
    // ═══════════════════════════════════════════
    private List<PH_Reassort> phReassortList;
    private ReassortAdapter reassortAdapter;

    // ═══════════════════════════════════════════
    // UI
    // ═══════════════════════════════════════════
    private ListView phReassortListView;
    private TextView textLancerScan;
    private ImageView iconLancerScan;
    private LinearLayout layoutSynchronisation;
    private LinearLayout sousTitreDate;

    // ═══════════════════════════════════════════
    // Cycle de vie
    // ═══════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_dotation_service);

        bindVues();
        demarrerAnimationScan();
        configurerTitre();
        configurerClicListe();
        configurerBoutonRetour();
    }

    @Override
    public void onResume() {
        super.onResume();
        phReassortList = new ArrayList<>();

        if (statutConnexion && passageParOnCreate) {
            chargerDepuisApi();
        } else {
            chargerDepuisBase();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    // ═══════════════════════════════════════════
    // Initialisation UI
    // ═══════════════════════════════════════════
    private void bindVues() {
        textLancerScan        = findViewById(R.id.textLancerScan);
        iconLancerScan        = findViewById(R.id.iconLancerScan);
        sousTitreDate         = findViewById(R.id.sousTitreDate);
        layoutSynchronisation = findViewById(R.id.layoutSyncronisationPreparations);
        phReassortListView    = findViewById(R.id.listeView);
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

    private void configurerTitre() {
        ((TextView) findViewById(R.id.titre)).setText("Demande Plan de soin");
    }

    private void configurerClicListe() {
        phReassortListView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Reassort reassortSelectionne = reassortAdapter.listeReassort.get(position);
            PH_Preparation preparation = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(
                    db,
                    "Réassort de service : " + reassortSelectionne.getListe(),
                    reassortSelectionne.getDateLivraison());

            boolean peutOuvrir = preparation == null
                    || preparation.getStatut().contentEquals("En instance")
                    || preparation.getStatut().contentEquals("En cours de régularisation");

            if (peutOuvrir) {
                startActivity(buildDetailIntent(preparation, reassortSelectionne));
                finish();
            }
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
                + DBOpenHelper.Urls.uriRequeteDemandeReassortCourant;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::traiterReponseApi,
                error -> {
                    Log.e(TAG, "Erreur Volley", error);
                    Alerte.afficherAlerte(this, "Erreur",
                            "Veuillez contacter la société Alcyons (erreur Volley : Demande Réassort)",
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

    private void chargerDepuisBase() {
        phReassortList = PH_ReassortOpenHelper.getPH_Reassort(db);
        if (phReassortList.isEmpty()) {
            connexionNecessaire();
        } else {
            rafraichirAdapter();
        }
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
                // ✅ Précharger toutes les préparations existantes en Map avant la boucle
                Map<String, PH_Preparation> preparationsExistantes =
                        PH_PreparationOpenHelper.getAllPreparationsEnMap(db);

                traiterPreparations(response.getJSONArray("PH_Preparations"), preparationsExistantes);
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

        List<PH_Reassort> liste = PH_ReassortOpenHelper.getPH_Reassort(db);
        if (liste.isEmpty()) {
            vide = true;
            nomServiceVide = "PH_Reassort";
            naviguerVersNavigation();
        }
    }

    private void traiterPreparations(JSONArray preparationsArray,
                                     Map<String, PH_Preparation> preparationsExistantes)
            throws JSONException {

        // ✅ Charger les UIDs en instance une seule fois avant la boucle
        List<Integer> uidsEnInstance =
                PH_PreparationOpenHelper.getUIDDotationGlobaleEnInstance(db);

        for (int i = 0; i < preparationsArray.length(); i++) {
            traiterUnePreparation(
                    preparationsArray.getJSONObject(i),
                    uidsEnInstance,
                    preparationsExistantes);
        }
    }

    private void traiterUnePreparation(JSONObject obj,
                                       List<Integer> uidsEnInstance,
                                       Map<String, PH_Preparation> preparationsExistantes)
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

        // ✅ Utiliser la Map préchargée au lieu d'une requête BDD par itération
        String clePreparation = nomListe + "_" + dateLivr;
        PH_Preparation preparation = preparationsExistantes.get(clePreparation);

        if (preparation == null) {
            preparation = new PH_Preparation(obj);
            PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, preparation);
            // Ajouter à la Map pour les éventuels doublons dans le JSON
            preparationsExistantes.put(clePreparation, preparation);
        } else {
            preparation.setStatut(statut);
            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, preparation);
        }

        traiterLignes(obj.getJSONArray("ph_preparation_lignes"), preparation);
    }

    private void traiterLignes(JSONArray lignesArray, PH_Preparation preparation)
            throws JSONException {

        // ✅ Précharger toutes les lignes de cette préparation en Map avant la boucle
        Map<Integer, PH_Preparation_Ligne> lignesExistantes =
                PH_Preparation_LigneOpenHelper.getLignesParPreparationEnMap(db, preparation);

        for (int j = 0; j < lignesArray.length(); j++) {
            JSONObject ligneObj = lignesArray.getJSONObject(j);
            int codeProduit     = ligneObj.getInt("produitID");

            // ✅ Utiliser la Map au lieu d'une requête BDD par ligne
            PH_Preparation_Ligne ligne = lignesExistantes.get(codeProduit);

            if (ligne == null) {
                ligne = new PH_Preparation_Ligne(ligneObj, preparation.getUID());
                PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ligne);
                lignesExistantes.put(codeProduit, ligne);
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

        // ✅ Opérations BDD + calculs sur thread background
        new Thread(() -> {

            List<PH_Reassort> reassortListe = PH_ReassortOpenHelper.getPH_Reassort(db);

            if (reassortListe.isEmpty()) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    vide = true;
                    nomServiceVide = "PH_Reassort";
                    naviguerVersNavigation();
                });
                return;
            }

            enrichirDatesLivraison(reassortListe);
            trierParDate(reassortListe);

            // ✅ Retour sur thread UI uniquement pour l'adapter
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                ElementASynchroniserOpenHelper.toutSynchroniser(
                        this, db, utilisateurConnecte, false);

                reassortAdapter = new ReassortAdapter(this, db, utilisateurConnecte);
                peuplerAdapter(reassortListe);

                phReassortListView.setDivider(footer);
                phReassortListView.setAdapter(reassortAdapter);
                sousTitreDate.setVisibility(View.VISIBLE);
                layoutSynchronisation.setVisibility(View.GONE);
                invalidateOptionsMenu();
            });

        }).start();
    }

    private void enrichirDatesLivraison(List<PH_Reassort> liste) {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // ✅ Charger tous les dépôts en une seule requête et indexer par référence
        List<Depot> tousLesDepots = DepotOpenHelper.getDepotsParType(db, "%");
        Map<String, Depot> depotParReference = new HashMap<>();
        for (Depot depot : tousLesDepots) {
            depotParReference.put(depot.getDepot_Reference(), depot);
        }

        for (PH_Reassort reassort : liste) {
            Depot depot = depotParReference.get(reassort.getDepot_Reference());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            String date = dateFormat.format(cal.getTime());
            if (depot != null)
            {
                date = EVENTOpenHelper.getDateProchaineLivraison(db, depot.getDepot_UID());
                if (date == null || date.isEmpty()) {
                    date = dateFormat.format(cal.getTime());
                }
            }

            reassort.setDateLivraison(date);
        }
    }

    private void trierParDate(List<PH_Reassort> liste) {
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        liste.sort((o1, o2) -> {
            try {
                return Objects.requireNonNull(format.parse(o1.getDateLivraison()))
                        .compareTo(format.parse(o2.getDateLivraison()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void peuplerAdapter(List<PH_Reassort> liste) {
        // ✅ Set à la place de List pour contains() en O(1)
        Set<String> datesVues = new HashSet<>();
        Set<String> depotsVus = new HashSet<>();

        for (PH_Reassort reassort : liste) {
            boolean nouvelleDate = !datesVues.contains(reassort.getDateLivraison());
            boolean nouveauDepot = !depotsVus.contains(reassort.getDepot_Reference());

            if (nouvelleDate || nouveauDepot) {
                datesVues.add(reassort.getDateLivraison());
                depotsVus.add(reassort.getDepot_Reference());
                reassortAdapter.addSectionHeaderItem(reassort);
            }

            reassortAdapter.addItem(reassort);
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
    private Intent buildDetailIntent(PH_Preparation preparation, PH_Reassort reassort) {
        Bundle bundle = super.getBundle();
        bundle.putInt("PH_ReassortSelectionneID", reassort.getPhiMR4UUID());

        if (preparation != null) {
            bundle.putInt("depotSelectionneID", preparation.getDepotDestinataireID());
            bundle.putInt("phPreparationID", preparation.getUID());
        }

        Intent intent = new Intent(this, InformationDemandeReassortActivity.class);
        intent.putExtras(bundle);
        return intent;
    }
}