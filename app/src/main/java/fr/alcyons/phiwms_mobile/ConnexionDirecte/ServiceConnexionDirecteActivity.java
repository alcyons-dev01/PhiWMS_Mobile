package fr.alcyons.phiwms_mobile.ConnexionDirecte;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tomer.fadingtextview.FadingTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PerimetreFonctionnel;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ConnexionDirecteExpandableAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionListeServices;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhraseSnackbar;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
public class ServiceConnexionDirecteActivity extends ServiceAvecConnexionActivity {

    public ExpandableListView expandablelistViewConnexionDirecte;
    Map<PerimetreFonctionnel, List<Service>> perimetreListe;
    List<PerimetreFonctionnel> ListePerimetreFonctionnel;
    ConnexionDirecteExpandableAdapter connexionDirecteExpandableAdapter;
    AlertDialog alertDialog;
    ProgressBar progressBar;
    int progressionSeekbar;
    Service serviceSelectionne;
    boolean premierPassage;
    FadingTextView fadingText;

    PackageManager pm;

    boolean snackBar;
    boolean enCoursDeDeveloppement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion_directe);

        // Initialisation de la listView
        expandablelistViewConnexionDirecte = (ExpandableListView) findViewById(R.id.expandablelistViewConnexionDirecte);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expandablelistViewConnexionDirecte.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));

        //Initialisation des listes utiles à l'expandable
        perimetreListe = new LinkedHashMap<>();
        ListePerimetreFonctionnel = new ArrayList<>();
        ListePerimetreFonctionnel = utilisateurConnecte.recupererListePerimetresFonctionnelHabilites();
        for(int i = 0; i < ListePerimetreFonctionnel.size(); i++) {
            PerimetreFonctionnel perimetreFonctionnel = ListePerimetreFonctionnel.get(i);
            List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnel);
            perimetreListe.put(perimetreFonctionnel, serviceList);
        }

        //Gestion de l'adapter
        connexionDirecteExpandableAdapter = new ConnexionDirecteExpandableAdapter(this, ListePerimetreFonctionnel, perimetreListe, serviceIndicateurNom);
        expandablelistViewConnexionDirecte.setAdapter(connexionDirecteExpandableAdapter);
        fadingText = (FadingTextView)findViewById(R.id.fadingText);

        //Gestion pour le click de l'adapter
        expandablelistViewConnexionDirecte.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                serviceSelectionne = (Service) connexionDirecteExpandableAdapter.getChild(groupPosition, childPosition);
                if(serviceSelectionne != null)
                {
                    synchronisation(serviceSelectionne);
                }
                return false;
            }
        });

        //on ouvre le scan lors du lancement de l'activité afin de scanné le service
        premierPassage = true;
        pm = ServiceConnexionDirecteActivity.this.getPackageManager();

        //Gestion des services en cours de développement
        enCoursDeDeveloppement = false;
        //récupération du boolean déclenchant une snackbar ou pas
        snackBar = intent.getBooleanExtra("snackBar", false);
        if(snackBar)
        {
            afficherSnackbarServiceDirect(intent.getExtras().getString("nomService"));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!snackBar)
        {
            /* Code nécessaire afin de réaliser une requête à l' API */
            if (haveNetworkConnection(ServiceConnexionDirecteActivity.this) && premierPassage) {
                afficherSpinner(ServiceConnexionDirecteActivity.this, LayoutInflater.from(ServiceConnexionDirecteActivity.this));

                RequestQueue requestQueuePlanHabilitationUtilisateur = Volley.newRequestQueue(ServiceConnexionDirecteActivity.this);

                String urlCompletRequetePlanHabilitation = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePlanHabilitation + String.valueOf(utilisateurConnecte.getPlanHabilitation())/* + DBOpenHelper.Urls.parametreToken + utilisateurConnecte.getToken()*/;

                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlCompletRequetePlanHabilitation, null, new Response.Listener<JSONObject>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int resultCount = response.getInt("resultCount");
                            if (resultCount == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(ServiceConnexionDirecteActivity.this.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(ServiceConnexionDirecteActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    ServiceConnexionDirecteActivity.this.finishAffinity();
                                    Intent intent = new Intent(ServiceConnexionDirecteActivity.this, AuthentificationActivity.class);
                                    ServiceConnexionDirecteActivity.this.startActivity(intent);
                                } else if (erreur.equals(ServiceConnexionDirecteActivity.this.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(ServiceConnexionDirecteActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    ServiceConnexionDirecteActivity.this.finishAffinity();
                                    Intent intent = new Intent(ServiceConnexionDirecteActivity.this, AuthentificationActivity.class);
                                    ServiceConnexionDirecteActivity.this.startActivity(intent);
                                } else if (!erreur.contentEquals("Aucun plan_habilitation trouvé")) {
                                    Alerte.afficherAlerte(ServiceConnexionDirecteActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete recupererPlanHabilitationUtilisateur", "alerte");
                                }
                                else
                                {
                                    utilisateurConnecte.setServicesHabilites(null);
                                    UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                                }
                            } else {
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

                                    Service service = new Service(idServiceCourant, nomServiceCourant, ordreServiceCourant, idPerimetreFonctionnelServiceCourant, nomPerimetreFonctionnelServiceCourant, statutServiceCourant, indicateurServiceCourant, descriptionServiceCourant, videoServiceCourant, whitePaperServiceCourant, score);
                                    serviceList.add(service);
                                }
                                utilisateurConnecte.setServicesHabilites(serviceList);
                                UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);


                                if (utilisateurConnecte.getServicesHabilites().size() > 0) {
                                    // Activer les indicateurs pour l'utilisateur
                                    recupererIndicateurs();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(ServiceConnexionDirecteActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP recupererPlanHabilitationUtilisateur", "alerte");
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", utilisateurConnecte.getToken());
                        return headers;
                    }
                };
                requestQueuePlanHabilitationUtilisateur.add(obreq);
                try {
                    Looper.loop();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                premierPassage = false;
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    lancerScan();
                }
            }
        }
    }

    private void lancerScan() {
        Bundle scanDocumentBundle = ServiceConnexionDirecteActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteService));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent = null;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteService);
            scanDocumentBundle.putBoolean("modeRafale", true);
            scanDocumentIntent = new Intent(ServiceConnexionDirecteActivity.this, ScannerSearchOnlyActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                scanDocumentBundle.putBoolean("modeRafale", false);
                scanDocumentIntent = new Intent(ServiceConnexionDirecteActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteService);
                scanDocumentBundle.putBoolean("modeRafale", true);
                scanDocumentIntent = new Intent(ServiceConnexionDirecteActivity.this, ScannerSearchOnlyActivity.class);
            }
        }



        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceConnexionDirecteActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_SERVICE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.prepareOptionsMenu(menu, connexionDirecteExpandableAdapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return false;
            }
        });

        MenuItem searchMenuItem = menu.findItem(R.id.rechercheMenu);
        searchMenuItem.setVisible(true);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                connexionDirecteExpandableAdapter.filter(query);
                //display the list
                expandablelistViewConnexionDirecte.setAdapter(connexionDirecteExpandableAdapter);
                //expand all Groups
                expandAll();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connexionDirecteExpandableAdapter.filter(newText);
                        //display the list
                        expandablelistViewConnexionDirecte.setAdapter(connexionDirecteExpandableAdapter);
                        //expand all Groups
                        expandAll();
                    }
                });
                return false;
            }
        });

        searchView.setQueryHint("Nom service");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setQuery("", true);
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                collapseAll();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                expandAll();
                return true;  // Return true to expand action view
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            menu.findItem(R.id.menuDatamatrix).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menuDatamatrix).setVisible(false);
        }

        return true;
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_SERVICE: {
                if (data != null) {
                    String code = data.getExtras().getString("code");
                    if (code != null) {
                        serviceSelectionne = ServiceOpenHelper.getServiceByName(db, code);
                        if (serviceSelectionne != null) {
                            synchronisation(serviceSelectionne);
                        }
                    }
                }
                break;
            }
        }
    }

    public void synchronisation(Service service) {
        String nomService = service.getNom();

        switch (nomService)
        {
            //Périmètre patient
            case "Commander":
                initialisationAlerte(3);
                break;
            case "Complément de commande":
                enCoursDeDeveloppement = true;
                break;
            case "Contacter la pharmacie":
                enCoursDeDeveloppement = true;
                break;

            //Périmètre préparateur :
            case "Préparation Nominative":
                enCoursDeDeveloppement = true;
                break;
            case "Délivrances Nominatives":
                enCoursDeDeveloppement = true;
                break;

            //Périmètre pharmacien :
            case "Verrou Pharmacie":
                initialisationAlerte(4);
                PH_PreparationOpenHelper.synchronisationPH_PreparationVerrouPharmacie(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Quarantaine":
                initialisationAlerte(4);
                RetourOpenHelper.insererRetourQuarantaine(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;

            //Périmètre Magasinier :
            case "Inventaire scanner":
                initialisationAlerte(3);
                break;
            case "Réception PUI":
                initialisationAlerte(4);
                CommandeOpenHelper.insererBDDLocaleCommandeReceptionPUI(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Contrôle des retours":
                initialisationAlerte(4);
                RetourOpenHelper.insererRetourControleDesRetour(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Retour PUI":
                initialisationAlerte(4);
                RetourOpenHelper.insererRetourRetourPUI(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Retour Frs":
                initialisationAlerte(4);
                RetourOpenHelper.insererRetourRetourFournisseur(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Destruction":
                initialisationAlerte(4);
                RetourOpenHelper.insererRetourDestruction(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Plan de Placements":
                initialisationAlerte(3);
                break;
            case "Identification Par Scan":
                initialisationAlerte(3);
                break;
            case "Zones et Emplacements":
                initialisationAlerte(3);
                break;
            case "Préparation UF":
                initialisationAlerte(4);
                PH_PreparationOpenHelper.synchronisationPH_PreparationUF(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Préparation PAD":
                initialisationAlerte(4);
                PH_PreparationOpenHelper.synchronisationPH_PreparationPAD(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;

            //Périmètre Chauffeurs :
            case "Livraison":
                initialisationAlerte(4);
                PH_PreparationOpenHelper.synchronisationPH_PreparationLivraison(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte.getId(), utilisateurConnecte, statutConnexion);
                break;
            case "Tournée journalière":
                enCoursDeDeveloppement = true;
                break;
            case "Installation à Domicile":
                enCoursDeDeveloppement = true;
                break;
            case "Demande PleinVide":
                initialisationAlerte(3);
                break;

            //Périmètre Infirmiers (NB : préparation UF et PAD définie dans le périmètre magasinier, demande PleinVide définie dans le périmètre Chauffeur) :
            case "Utiliser":
                initialisationAlerte(3);
                break;
            case "Demande Protocole PAD":
                initialisationAlerte(3);
                break;
            case "Demande Dotation PAD":
                initialisationAlerte(3);
                break;
            case "Demande Réassort":
                initialisationAlerte(3);
                break;
            case "Dotation Service":
                initialisationAlerte(3);
                break;
            case "Demande Particuliere":
                initialisationAlerte(3);
                break;
            case "Séances Préparer":
                enCoursDeDeveloppement = true;
                break;
            case "Feuilles de comptage":
                enCoursDeDeveloppement = true;
                break;
            case "Pharmacie Urgence":
                enCoursDeDeveloppement = true;
                break;
            case "Demande Dotation Urgence":
                enCoursDeDeveloppement = true;
                break;
            case "Retour Demandé":
                initialisationAlerte(3);
                break;
            case "Visite à domicile":
                enCoursDeDeveloppement = true;
                break;

            //Périmètre Commun
            case "Dictaphone":
                enCoursDeDeveloppement = true;
                break;
            case "Paramètres":
                initialisationAlerte(3);
                break;
            case "Médicament au Livret":
                initialisationAlerte(3);
                break;
            case "Dispositif au Livret":
                initialisationAlerte(3);
                break;
            case "Stock":
                initialisationAlerte(3);
                break;
            case "Notification":
                initialisationAlerte(3);
                break;
            case "Serialisation":
                initialisationAlerte(3);
                break;
            case "ConnexionDirecte":
                initialisationAlerte(3);
                break;
            case "Paramètres utilisateur":
                initialisationAlerte(3);
                break;

            //Spécifique ADH (Préparation UF&PAD défini en amont, ainsi que contrôle des retours scannés)
            case "Réception Scannée":
                initialisationAlerte(4);
                CommandeOpenHelper.insererBDDLocaleCommandeReceptionScannee(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            case "Réception PAD":
                initialisationAlerte(4);
                CommandeOpenHelper.insererBDDLocaleCommandeReceptionPAD(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
                break;
            default:
                initialisationAlerte(3);
                break;
        }

        if(!enCoursDeDeveloppement)
        {
            //synchronisation des éléments communs
            ElementASynchroniserOpenHelper.toutSynchroniser(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte, true);
            ServiceOpenHelper.insererBDDLocaleServicesEtPerimetresFonctionnelsphiwms_mobile(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
            DepotOpenHelper.insererBDDLocaleDepots(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
            ProduitOpenHelper.insererBDDLocaleProduits(ServiceConnexionDirecteActivity.this, db, utilisateurConnecte.getToken(), utilisateurConnecte, statutConnexion);
        }
        else
        {
            afficherSnackbarServiceDirect("EnCoursDeDeveloppement");
        }
    }

    public void initialisationAlerte(int max) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceConnexionDirecteActivity.this);
        LayoutInflater inflater = ServiceConnexionDirecteActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alerte_synchro_connexion_direct, null);
        progressBar = layout.findViewById(R.id.barDeProgression);

        progressBar.setMax(max);
        progressBar.setProgress(0);
        progressionSeekbar = 0;
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public void gestionProgressBar() {
        progressBar.setProgress(0);
        progressionSeekbar++;
        progressBar.setProgress(progressionSeekbar);
        if(progressBar.getMax() == progressBar.getProgress())
        {
            // Appeler l'activité correspondante au service sélectionné
            Intent intentVersService = new Intent(ServiceConnexionDirecteActivity.this, OutilsGestionListeServices.recupererActiviteCorrespondanteAUnService(serviceSelectionne));

            // Récupération des éléments à transmettre à la prochaine activité
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            extras.putInt("serviceSelectionneID", serviceSelectionne.getId()); //!\ Il est nécessaire de transmettre cet élément pour gérer les services non disponible avec une seule activité
            intentVersService.putExtras(extras);

            // Appel de la prochaine activité
            ServiceConnexionDirecteActivity.this.startActivity(intentVersService);
            ServiceConnexionDirecteActivity.this.finish();
            alertDialog.dismiss();
        }
    }

    private static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void expandAll() {
        for (int i = 0; i < ListePerimetreFonctionnel.size(); i++) {
            expandablelistViewConnexionDirecte.expandGroup(i);
        }
    }

    private void collapseAll() {
        for (int i = 0; i < ListePerimetreFonctionnel.size(); i++) {
            expandablelistViewConnexionDirecte.collapseGroup(i);
        }
    }

    private void afficherSnackbarServiceDirect(String nomService)
    {
        String erreur = OutilsGestionPhraseSnackbar.obtenirPhraseSnackbar(nomService);
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>"+erreur+"</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();

        if(enCoursDeDeveloppement)
            enCoursDeDeveloppement = false;
    }
}
