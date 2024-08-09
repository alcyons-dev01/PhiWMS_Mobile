package fr.alcyons.phiwms_mobile.Navigation;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.NotificationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PerimetreFonctionnel;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ListPerimetreAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.NavigationAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionListeServices;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotoApercu;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NavigationActivity extends ServiceAvecConnexionActivity {
    RequestQueue requestQueuePlanHabilitationUtilisateur;
    RequestQueue requestQueueIndicateur;
    String etablissement;
    List<String> channels = new ArrayList<>();
    ListView actionListView;
    NavigationAdapter navigationAdapter;
    ListPerimetreAdapter listPerimetreAdapter;
    Map<String, Integer> mapServiceIndicateur;
    PackageManager pm;
    List<Service> liste_service;
    LinearLayout layoutRecherche;
    ImageView closeRecherche;
    ImageView lancerScanService;
    EditText rechercheService;
    TextView versionApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        pm = NavigationActivity.this.getPackageManager();

        etablissement = Objects.requireNonNull(intent.getExtras()).getString("etablissement");
        if (etablissement == null) {
            etablissement = ParametresServeurOpenHelper.getEtablissementNom(db);
        }

        passageParOnCreate = true;

        //gestion du map pour les indicateurs
        mapServiceIndicateur = new LinkedHashMap<>();

        //gestion de la zone de recherche
        layoutRecherche = (LinearLayout) findViewById(R.id.layoutRecherche);
        closeRecherche = (ImageView) findViewById(R.id.closeRecherche);
        lancerScanService = (ImageView) findViewById(R.id.lancerScanService);
        rechercheService = (EditText) findViewById(R.id.rechercheService);

        //affichage version
        versionApplication = (TextView) findViewById(R.id.versionApplication);
        //String phrase_version = Calendar.getInstance().get(Calendar.YEAR)+" - v"+BuildConfig.VERSION_NAME+"."+BuildConfig.VERSION_CODE;

        String phrase_version = "";
        versionApplication.setText(phrase_version);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(NavigationActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                NavigationActivity.this.startActivity(intent);
                NavigationActivity.this.finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(NavigationActivity.this, db, utilisateurConnecte, true);
        }
        NotificationOpenHelper.supprimerNotificationVerification(db);
        if (vide != null) {
            if (vide) {
                afficherSnackBar(nomServiceVide);
                vide = false;
            }
        }

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {
            afficherSpinner(NavigationActivity.this, LayoutInflater.from(NavigationActivity.this));
            requestQueuePlanHabilitationUtilisateur = new Volley().newRequestQueue(NavigationActivity.this);

            final String urlCompletRequetePlanHabilitation = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePlanHabilitation + utilisateurConnecte.getPlanHabilitation()/* + DBOpenHelper.Urls.parametreToken + utilisateurConnecte.getToken()*/;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlCompletRequetePlanHabilitation, null, response -> {
                try {
                    int resultCount = response.getInt("resultCount");
                    if (resultCount == 0) {
                        String erreur = response.getString("erreur");
                        if (erreur.equals(NavigationActivity.this.getString(R.string.tokenInvalide))) {
                            Alerte.afficherAlerte(NavigationActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                            DBOpenHelper.viderBasesDeDonnees(db);
                            NavigationActivity.this.finishAffinity();
                            Intent intent = new Intent(NavigationActivity.this, AuthentificationActivity.class);
                            NavigationActivity.this.startActivity(intent);
                        } else if (erreur.equals(NavigationActivity.this.getString(R.string.tokenExpire))) {
                            Alerte.afficherAlerte(NavigationActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                            NavigationActivity.this.finishAffinity();
                            Intent intent = new Intent(NavigationActivity.this, AuthentificationActivity.class);
                            NavigationActivity.this.startActivity(intent);
                        } else if (!erreur.contentEquals("Aucun plan_habilitation trouvé")) {
                            Alerte.afficherAlerte(NavigationActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete recupererPlanHabilitationUtilisateur", "alerte");
                        } else {
                            utilisateurConnecte.setServicesHabilites(null);
                            UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                        }
                    } else {
                        passageParOnCreate = false;

                        // récupération des services auquels l'utilisateur connecté a accès
                        JSONArray planHabilitationJSONArray = response.getJSONArray("plan_habilitation");

                        // Creation de la liste des services auquels l'utilisateur connecté a accès
                        List<Service> serviceList = new ArrayList<>();

                        for (int i = 0; i < planHabilitationJSONArray.length(); i++) {
                            // Récupération du service courant
                            JSONObject planHabilitationJSONObject = planHabilitationJSONArray.getJSONObject(i);

                            // Récupération des attributs du service courant
                            int idServiceCourant = planHabilitationJSONObject.getInt("id");
                            String nomServiceCourant = planHabilitationJSONObject.getString("name");
                            int ordreServiceCourant = planHabilitationJSONObject.getInt("ordre");
                            int idPerimetreFonctionnelServiceCourant = planHabilitationJSONObject.getInt("perimetre_fonctionnel_id");
                            String nomPerimetreFonctionnelServiceCourant = planHabilitationJSONObject.getString("perimetre_fonctionnel");
                            String statutServiceCourant = planHabilitationJSONObject.getString("statut");
                            int indicateurServiceCourant = 0;
                            String descriptionServiceCourant = planHabilitationJSONObject.getString("description");
                            String videoServiceCourant = planHabilitationJSONObject.getString("video");
                            String whitePaperServiceCourant= planHabilitationJSONObject.getString("whitepaper");
                            int score = planHabilitationJSONObject.getInt("score");
                            String activiteMobile = planHabilitationJSONObject.getString("activiteMobile");
                            int phiwms_mobileuuid = 0;
                            Service serviceBDD = ServiceOpenHelper.getServiceByID(db, idServiceCourant);
                            if(serviceBDD != null)
                            {
                                phiwms_mobileuuid = serviceBDD.getPhiMR4UUID();
                            }

                            Service service = new Service(idServiceCourant, nomServiceCourant, ordreServiceCourant, idPerimetreFonctionnelServiceCourant, nomPerimetreFonctionnelServiceCourant, statutServiceCourant, indicateurServiceCourant, descriptionServiceCourant, videoServiceCourant, whitePaperServiceCourant, score, phiwms_mobileuuid, activiteMobile);
                            serviceList.add(service);
                        }
                        utilisateurConnecte.setServicesHabilites(serviceList);
                        UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                        if (!utilisateurConnecte.getServicesHabilites().isEmpty()) {
                            // Activer les indicateurs pour l'utilisateur
                            recupererIndicateurs();
                        }

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error Looper :", e);
                    Alerte.afficherAlerte(NavigationActivity.this, "Erreur synchronisation", String.valueOf(e.getMessage()), "alerte");

                }
            },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(NavigationActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons !\n Impossible d'atteindre le serveur", "alerte");
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=utf-8");
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    headers.put("UserIdentifiant", utilisateurConnecte.getIdentifiant());
                    headers.put("ApplicationName", "PhiWMS");
                    return headers;
                }
            };

            obreq.setRetryPolicy(retryPolicy);
            requestQueuePlanHabilitationUtilisateur.add(obreq);
        } else {
            afficherMenu();
        }

        //gestion du clic sur la toolbar
        if(toolbar != null)
        {
            toolbar.setOnClickListener(v -> {
                perimetreCourantTextView.setText("Périmètres");
                final List<PerimetreFonctionnel> list_perimetre = utilisateurConnecte.getAllPerimetres();
                listPerimetreAdapter = new ListPerimetreAdapter(NavigationActivity.this, list_perimetre, utilisateurConnecte);
                actionListView = (ListView) findViewById(R.id.listeView);
                actionListView.setAdapter(listPerimetreAdapter);
                actionListView.setDivider(footer);

                actionListView.setOnItemClickListener((parent, view, position, id) -> {
                    perimetreFonctionnelCourant = list_perimetre.get(position);
                    utilisateurConnecte.setLastPerimetre(perimetreFonctionnelCourant.getId());
                    UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                    perimetreCourantTextView.setText(perimetreFonctionnelCourant.getNom());
                    if(perimetreFonctionnelCourant != null)
                    {
                        Objects.requireNonNull(intent.getExtras()).putSerializable("PerimetreFonctionnelCourant", perimetreFonctionnelCourant);
                        liste_service = new ArrayList<>();
                        List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnelCourant);
                        liste_service.addAll(serviceList);
                        gestionIndicateur();
                        gestionAdapter();
                    }
                });
            });
        }

       invalidateOptionsMenu();
    }

    public void clickInformationService(Service service_courant)
    {
        Intent intentWebView = new Intent(NavigationActivity.this, WebViewServiceActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putSerializable("Service_Courant", service_courant); //!\ Il est nécessaire de transmettre cet élément pour gérer les services non disponible avec une seule activité
        intentWebView.putExtras(extras);

        // Appel de la prochaine activité
        NavigationActivity.this.startActivity(intentWebView);
    }

    private void afficherMenu() {
        // Si des notifications n'ont pas été lues

        if(navigationAdapter != null)
        {
            navigationAdapter.clear();
        }

        liste_service = new ArrayList<>();

        if(perimetreFonctionnelCourant != null)
        {
            List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnelCourant);
            perimetreCourantTextView.setText(perimetreFonctionnelCourant.getNom());
            liste_service.addAll(serviceList);
            gestionLancementService(perimetreFonctionnelCourant);
        }
        else
        {
            toolbar.performClick();
            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.deleteMenu).setVisible(false);
        menu.findItem(R.id.menuDatamatrix).setVisible(false);
        menu.findItem(R.id.menuRecherche).setVisible(true);
        menu.findItem(R.id.menuInformation).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            MenuItem item = menu.findItem(R.id.menuDatamatrix);
            item.setOnMenuItemClickListener(item1 -> {
                lancerScan();
                return true;
            });
        }

        MenuItem itemRecherche = menu.findItem(R.id.menuRecherche);
        itemRecherche.setOnMenuItemClickListener(item -> {
            rechercherService();
            return true;
        });

        MenuItem itemInformation = menu.findItem(R.id.menuInformation);
        itemInformation.setOnMenuItemClickListener(item -> {
            actionListView = (ListView) findViewById(R.id.listeView);
            if(!navigationAdapter.afficherInfo)
            {
                navigationAdapter = new NavigationAdapter(NavigationActivity.this, utilisateurConnecte, db, liste_service, mapServiceIndicateur, perimetreFonctionnelCourant.getNom(), true);
            }
            else
            {
                navigationAdapter = new NavigationAdapter(NavigationActivity.this, utilisateurConnecte, db, liste_service, mapServiceIndicateur, perimetreFonctionnelCourant.getNom(), false);
            }
            actionListView.setAdapter(navigationAdapter);
            actionListView.setDivider(footer);
            return true;
        });

        return true;
    }

    public void lancerScan() {
        Intent scanDocumentIntent;
        Bundle scanDocumentBundle = NavigationActivity.super.getBundle();
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        scanDocumentBundle.putBoolean("modeRafale", false);
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(NavigationActivity.this, ScannerSearchOnlyActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteService);
            scanDocumentBundle.putBoolean("activerTextSuppression", true);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un service");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(NavigationActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteService));
            }
            else
            {
                scanDocumentIntent = new Intent(NavigationActivity.this, ScannerSearchOnlyActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteService);
                scanDocumentBundle.putBoolean("activerTextSuppression", true);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un service");
            }
        }
        scanDocumentIntent.putExtras(scanDocumentBundle);
        NavigationActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_SERVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_SERVICE: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        Service serviceSelectionneScan = ServiceOpenHelper.getServiceByName(db, code);
                        if (serviceSelectionneScan == null) {
                            afficherSnackBar("NavigationService");
                        } else {
                            Class classe_demande = null;
                            try {
                                classe_demande = Class.forName("fr.alcyons.phiwms_mobile.Services."+serviceSelectionneScan.getActiviteMobile());
                            } catch (ClassNotFoundException ignored) {
                            }

                            if (classe_demande ==  null || classe_demande.getName().isEmpty() || classe_demande.getName().contentEquals("fr.alcyons.phiwms_mobile.ServiceEnCreationActivity")) {
                                afficherSnackBar("EnCoursDeDeveloppement");
                            } else {
                                Intent intentVersService = getIntentVersService(classe_demande, serviceSelectionneScan);

                                // Appel de la prochaine activité
                                NavigationActivity.this.startActivity(intentVersService);
                            }
                        }
                    }
                }
                break;
            }
            case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                if (data != null) {
                    String photoProduits = Objects.requireNonNull(data.getExtras()).getString("photoProduit");

                    Intent anotherIntent = new Intent(NavigationActivity.this, OutilsGestionPhotoApercu.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    anotherIntent.putExtra("image", photoProduits);
                    anotherIntent.putExtras(extras);
                    NavigationActivity.this.startActivity(anotherIntent);
                }
                break;
        }
    }

    @NonNull
    private Intent getIntentVersService(Class classe_demande, Service serviceSelectionneScan) {
        Intent intentVersService = new Intent(NavigationActivity.this, classe_demande);

        // Récupération des éléments à transmettre à la prochaine activité
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putInt("serviceSelectionneID", serviceSelectionneScan.getId());
        intentVersService.putExtras(extras);
        return intentVersService;
    }

    void gestionLancementService(PerimetreFonctionnel perimetreFonctionnel)
    {
        actionListView = (ListView) findViewById(R.id.listeView);
        navigationAdapter = new NavigationAdapter(NavigationActivity.this, utilisateurConnecte, db, liste_service, mapServiceIndicateur, perimetreFonctionnel.getNom(), false);
        actionListView.setAdapter(navigationAdapter);
        actionListView.setDivider(footer);


        actionListView.setOnItemClickListener((adapterView, view, i, l) -> {
            InputMethodManager imm = (InputMethodManager) NavigationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

            Service serviceSelectionne = navigationAdapter.getItem(i);
            TextView text_indicateur = view.findViewById(R.id.indicateurService);
            String indicateur = text_indicateur.getText().toString();
            if(!indicateur.contentEquals("0"))
            {
                if (serviceSelectionne != null) {

                    Class classe_demande = null;
                    try {
                        classe_demande = Class.forName("fr.alcyons.phiwms_mobile.Services."+serviceSelectionne.getActiviteMobile());
                    } catch (ClassNotFoundException ignored) {
                    }
                    if (classe_demande ==  null || classe_demande.getName().isEmpty() || classe_demande.getName().contentEquals("fr.alcyons.phiwms_mobile.ServiceEnCreationActivity")) {
                        afficherSnackBar("EnCoursDeDeveloppement");
                    } else {
                        serviceSelectionne.setScore(serviceSelectionne.getScore()+1);
                        ServiceOpenHelper.mettreAJourUnServiceEnBD(db, serviceSelectionne);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ServiceOpenHelper.Constantes.TABLE_SERVICE, serviceSelectionne.getId(), serviceSelectionne.getId(), DBOpenHelper.ActionsEAS.MAJ);
                        Intent intentVersService = getVersService(classe_demande, serviceSelectionne);

                        // Appel de la prochaine activité
                        NavigationActivity.this.startActivity(intentVersService);
                        NavigationActivity.this.finish();
                    }
                }
            }
            else
            {
                afficherSnackBar(serviceSelectionne.getNom());
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }

    @NonNull
    private Intent getVersService(Class classe_demande, Service serviceSelectionne) {
        Intent intentVersService = new Intent(NavigationActivity.this, classe_demande);

        // Récupération des éléments à transmettre à la prochaine activité
        Bundle extras = new Bundle();

        if (serviceActuel != null) {
            intentVersService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentVersService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentVersService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentVersService.putExtra("EXIT", true);
        }

        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putInt("serviceSelectionneID", serviceSelectionne.getId()); //!\ Il est nécessaire de transmettre cet élément pour gérer les services non disponible avec une seule activité
        intentVersService.putExtras(extras);
        return intentVersService;
    }

    public void recupererIndicateurs() {
        if (!statutConnexion) {
            Alerte.afficherAlerte(NavigationActivity.this, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }

        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteIndicateur + "bureau";
        new Volley();
        requestQueueIndicateur = Volley.newRequestQueue(NavigationActivity.this);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try {
                int resultCount = response.getInt("resultCount");
                if (resultCount == 0) {
                    String erreur = response.getString("erreur");
                    if (erreur.equals(NavigationActivity.this.getString(R.string.tokenInvalide))) {
                        DBOpenHelper.viderBasesDeDonnees(db);
                        NavigationActivity.this.finish();
                        Intent intent = new Intent(NavigationActivity.this, AuthentificationActivity.class);
                        NavigationActivity.this.startActivity(intent);
                    } else if (erreur.equals(NavigationActivity.this.getString(R.string.tokenExpire))) {
                        Alerte.afficherAlerte(NavigationActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                        NavigationActivity.this.finish();
                        Intent intent = new Intent(NavigationActivity.this, AuthentificationActivity.class);
                        NavigationActivity.this.startActivity(intent);
                    } else {
                        Alerte.afficherAlerte(NavigationActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete recupererIndicateurs", "alerte");
                    }
                } else {
                    JSONObject indicateurJSONObject = response.getJSONObject("Indicateurs");

                    int indicateurVerrouPharmacie = indicateurJSONObject.getInt("VerrouPharmacie");
                    serviceIndicateurNom.add("Verrou Pharmacie Préparation externe");
                    serviceIndicateurValeur.add(indicateurVerrouPharmacie);
                    mapServiceIndicateur.put("Verrou Pharmacie Préparation externe", indicateurVerrouPharmacie);

                    int indicateurQuarantaine = indicateurJSONObject.getInt("Quarantaine");
                    serviceIndicateurNom.add("Quarantaine");
                    serviceIndicateurValeur.add(indicateurQuarantaine);
                    mapServiceIndicateur.put("Quarantaine", indicateurQuarantaine);

                    int indicateurControleDesRetours = indicateurJSONObject.getInt("ControleDesRetours");
                    serviceIndicateurNom.add("Contrôles des retours");
                    serviceIndicateurValeur.add(indicateurControleDesRetours);
                    mapServiceIndicateur.put("Contrôles des retours", indicateurControleDesRetours);

                    int indicateurRetourPUI = indicateurJSONObject.getInt("RetourPUI");
                    serviceIndicateurNom.add("Retour PUI");
                    serviceIndicateurValeur.add(indicateurRetourPUI);
                    mapServiceIndicateur.put("Retour PUI", indicateurRetourPUI);

                    int indicateurRetourFrs = indicateurJSONObject.getInt("RetourFrs");
                    serviceIndicateurNom.add("Retour Fournisseur");
                    serviceIndicateurValeur.add(indicateurRetourFrs);
                    mapServiceIndicateur.put("Retour Fournisseur", indicateurRetourFrs);

                    int indicateurDestruction = indicateurJSONObject.getInt("Destruction");
                    serviceIndicateurNom.add("Destruction");
                    serviceIndicateurValeur.add(indicateurDestruction);
                    mapServiceIndicateur.put("Destruction", indicateurDestruction);

                    int indicateurPreparationUF = indicateurJSONObject.getInt("PreparationUF");
                    serviceIndicateurNom.add("Préparation UF");
                    serviceIndicateurValeur.add(indicateurPreparationUF);
                    mapServiceIndicateur.put("Préparation UF", indicateurPreparationUF);

                    int indicateurPreparationPAD = indicateurJSONObject.getInt("PreparationPAD");
                    serviceIndicateurNom.add("Préparation PAD");
                    serviceIndicateurValeur.add(indicateurPreparationPAD);
                    mapServiceIndicateur.put("Préparation PAD", indicateurPreparationPAD);

                    int indicateurLivraison = indicateurJSONObject.getInt("Livraison");
                    serviceIndicateurNom.add("Livraison");
                    serviceIndicateurValeur.add(indicateurLivraison);
                    mapServiceIndicateur.put("Livraison", indicateurLivraison);

                    int indicateurReceptionPUI = indicateurJSONObject.getInt("ReceptionPUI");
                    serviceIndicateurNom.add("Réception PUI");
                    serviceIndicateurValeur.add(indicateurReceptionPUI);
                    mapServiceIndicateur.put("Réception PUI", indicateurReceptionPUI);

                    int indicateurReceptionPAD = indicateurJSONObject.getInt("ReceptionPAD");
                    serviceIndicateurNom.add("Réception PAD");
                    serviceIndicateurValeur.add(indicateurReceptionPAD);
                    mapServiceIndicateur.put("Réception PAD", indicateurReceptionPAD);

                    int indicateurPharmacieInterne = indicateurJSONObject.getInt("VerrouPharmacieInterne");
                    serviceIndicateurNom.add("Verrou Pharmacie Préparation interne");
                    serviceIndicateurValeur.add(indicateurPharmacieInterne);
                    mapServiceIndicateur.put("Verrou Pharmacie Préparation interne", indicateurPharmacieInterne);

                    int indicateurDemandePleinVide = indicateurJSONObject.getInt("DemandePleinVide");
                    serviceIndicateurNom.add("Demande PleinVide");
                    serviceIndicateurValeur.add(indicateurDemandePleinVide);
                    mapServiceIndicateur.put("Demande PleinVide", indicateurDemandePleinVide);

                    for (String serviceCourant : serviceIndicateurNom) {
                        Service service = ServiceOpenHelper.getServiceByName(db, serviceCourant);
                        serviceList.add(service);
                    }
                    afficherMenu();
                    //activerNotifications();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error Looper :", e);
            }

        },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerte(NavigationActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP recupererIndicateurs", "alerte");
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
                return headers;
            }
        };

        requestQueueIndicateur.add(obreq);
    }

    public void activerNotifications() {
     /*   // Initialiser la liste des channels

        if (etablissement.length() > 0) {
            gestionnaireUtilisateur.mettreAJourUtilisateur(db, utilisateurConnecte);

            for (Service service : utilisateurConnecte.getServicesHabilites()) {

                String perimetreFonctionnelName = service.getNomPerimetrefonctionnel();
                String channel = "Channel_" + perimetreFonctionnelName.toLowerCase().replaceAll(" ", "") + "_" + etablissement;
                if (channels.indexOf(channel) == -1) {
                    channels.add(channel);
                }
            }
        }

        // Access the default SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NavigationActivity.this);

        // Récupération des identifiants
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String pubnubSubKey = preferences.getString(getString(R.string.pubnubSubscribeKey), "sub-c-76c656ba-6c93-11e7-85aa-0619f8945a4f");

        // Initialisation de l'objet pubnub
        PNConfiguration pnConfiguration = new PNConfiguration().setSubscribeKey(pubnubSubKey).setSecure(true);

        PubNub pubnub = new PubNub(pnConfiguration);

        pubnub.subscribe().channels(channels).execute();

        pubnub.addPushNotificationsOnChannels()
                .channels(channels)
                .deviceId(refreshedToken)
                .pushType(PNPushType.GCM)
                .async(new PNCallback<PNPushAddChannelResult>() {
                    @Override
                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                    }
                });*/
    }
    protected void rechercherService()
    {
        final InputMethodManager imm = (InputMethodManager) NavigationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        layoutRecherche.setVisibility(View.VISIBLE);
        rechercheService.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        rechercheService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                {
                    List<String> serviceNomSelectionne = new ArrayList<>();
                    liste_service = new ArrayList<>();
                    mapServiceIndicateur = new LinkedHashMap<>();

                    List<Service> serviceUtilisateur = utilisateurConnecte.getServicesHabilites();

                    for(Service serviceUtilisateurCourant : serviceUtilisateur) {
                        String serviceUtilisateurCourantNom = serviceUtilisateurCourant.getNom().toLowerCase();
                        serviceUtilisateurCourantNom = Normalizer.normalize(serviceUtilisateurCourantNom, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

                        if (serviceUtilisateurCourantNom.contains(s.toString().toLowerCase()))
                        {
                            if(!serviceNomSelectionne.contains(serviceUtilisateurCourantNom))
                            {
                                serviceNomSelectionne.add(serviceUtilisateurCourantNom);
                                liste_service.add(serviceUtilisateurCourant);
                                int positionService = -1;
                                int boucle = -1;
                                for (String service : serviceIndicateurNom) {
                                    boucle++;
                                    if (service.toLowerCase().trim().contentEquals(serviceUtilisateurCourant.getNom().toLowerCase().trim())) {
                                        positionService = boucle;
                                        break;
                                    }
                                }

                                if (positionService == -1) {
                                    mapServiceIndicateur.put(serviceUtilisateurCourant.getNom(), -1);
                                } else {
                                    mapServiceIndicateur.put(serviceUtilisateurCourant.getNom(), serviceIndicateurValeur.get(positionService));
                                }
                            }
                        }
                    }

                    actionListView = (ListView) findViewById(R.id.listeView);
                    navigationAdapter = new NavigationAdapter(NavigationActivity.this, utilisateurConnecte, db, liste_service, mapServiceIndicateur, perimetreFonctionnelCourant.getNom(), false);
                    actionListView.setAdapter(navigationAdapter);
                    actionListView.setDivider(footer);
                }
            }
        });

        closeRecherche.setOnClickListener(v -> {
            rechercheService.setText("");
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
            layoutRecherche.setVisibility(View.GONE);
            liste_service = new ArrayList<>();
            List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnelCourant);
            liste_service.addAll(serviceList);
            gestionIndicateur();
            gestionAdapter();

        });

        lancerScanService.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
            lancerScan();
        });

    }

    protected void gestionAdapter()
    {
        actionListView = (ListView) findViewById(R.id.listeView);
        navigationAdapter = new NavigationAdapter(NavigationActivity.this, utilisateurConnecte, db, liste_service, mapServiceIndicateur, perimetreFonctionnelCourant.getNom(), false);
        actionListView.setAdapter(navigationAdapter);
        actionListView.setDivider(footer);

        gestionLancementService(perimetreFonctionnelCourant);
    }

    protected void gestionIndicateur()
    {
        mapServiceIndicateur = new LinkedHashMap<>();

        for(int i =0; i < liste_service.size(); i++)
        {
            Service service_courant = liste_service.get(i);
            int positionService = -1;
            int boucle = -1;
            for(String service : serviceIndicateurNom)
            {
                boucle ++;
                if(service.toLowerCase().trim().contentEquals(service_courant.getNom().toLowerCase().trim()))
                {
                    positionService = boucle;
                    break;
                }
            }

            if(positionService == -1)
            {
                mapServiceIndicateur.put(service_courant.getNom(), -1);
            }
            else
            {
                mapServiceIndicateur.put(service_courant.getNom(), serviceIndicateurValeur.get(positionService));
            }
        }
    }
}


