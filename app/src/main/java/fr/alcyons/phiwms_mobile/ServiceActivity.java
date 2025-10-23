package fr.alcyons.phiwms_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PerimetreFonctionnelOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PerimetreFonctionnel;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.Interfaces.ConfirmationServiceListener;
import fr.alcyons.phiwms_mobile.Interfaces.RetourServiceListener;
import fr.alcyons.phiwms_mobile.Interfaces.SaisieTextListener;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ServiceActivityExpandableAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionListeServices;


public class ServiceActivity extends MenuActivity implements RetourServiceListener, ConfirmationServiceListener, SaisieTextListener {

    public LinearLayout boutonRetourListePerimetresFonctionnelsLinearLayout;
    // Le service selectionné
    public Service serviceActuel;
    ImageView fermer_menu;
    Map<PerimetreFonctionnel, List<Service>> ListIemParent;
    ExpandableListView expandablelistView;
    ServiceActivityExpandableAdapter navigationExpandableListAdapter;
    private boolean connexionDirecte;
    int  nbPerimetreFonctionnel;
    boolean doubleBackToExitPressedOnce = false;
    final Context context = this;
    public Toolbar toolbar;
    public TextView perimetreCourantTextView;
    public PerimetreFonctionnel perimetreFonctionnelCourant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_service);

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        perimetreCourantTextView = (TextView) findViewById(R.id.perimetreCourantTextView);

        if(utilisateurConnecte != null)
        {
            perimetreFonctionnelCourant = PerimetreFonctionnelOpenHelper.getUnPerimetreFonctionnelByID(db, utilisateurConnecte.getLastPerimetre());
            if(perimetreFonctionnelCourant == null)
            {
                perimetreFonctionnelCourant = utilisateurConnecte.recupererAccesDirectPerimetresFonctionnelHabilites();
                if(perimetreFonctionnelCourant != null)
                {
                    utilisateurConnecte.setLastPerimetre(perimetreFonctionnelCourant.getId());
                    UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                }
            }
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Récupération du service sélectionné
        serviceActuel = ServiceOpenHelper.getServiceByID(db, intent.getExtras().getInt("serviceSelectionneID"));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        if(utilisateurConnecte != null)
        {
            ((TextView) headerView.findViewById(R.id.identiteUtilisateur)).setText(utilisateurConnecte.getPrenom() + " " + utilisateurConnecte.getNom());

            // Récupération du Périmètre Fonctionnel concerné
            PerimetreFonctionnel perimetreFonctionnelConcerne = null;
            // boutonRetourListePerimetresFonctionnelsLinearLayout = (LinearLayout) findViewById(R.id.boutonRetourListePerimetresFonctionnels);
            if(serviceActuel != null)
            {
                perimetreFonctionnelConcerne = PerimetreFonctionnelOpenHelper.getUnPerimetreFonctionnelParNom(serviceActuel.getNomPerimetrefonctionnel(), db);

            }


            fermer_menu = (ImageView) headerView.findViewById(R.id.fermer_menu);
            fermer_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });


            // Récupération de la liste des périmètres fonctionnels de l'utilisateur
            List<PerimetreFonctionnel> ListePerimetreFonctionnel = null;


            ListIemParent = new LinkedHashMap<>();

            if(!connexionDirecte)
            {
                ListePerimetreFonctionnel = utilisateurConnecte.recupererListePerimetresFonctionnelHabilites();
                for(int i = 0; i < ListePerimetreFonctionnel.size(); i++) {
                    PerimetreFonctionnel perimetreFonctionnel = ListePerimetreFonctionnel.get(i);
                    if(!perimetreFonctionnel.getNom().contentEquals("Accès direct"))
                    {
                        List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnel);
                        ListIemParent.put(perimetreFonctionnel, serviceList);
                    }
                }
            }
            else
            {
                ListePerimetreFonctionnel = utilisateurConnecte.recupererPerimetreServiceParametreUtilisateur();
                for(int i = 0; i < ListePerimetreFonctionnel.size(); i++) {
                    PerimetreFonctionnel perimetreFonctionnel = ListePerimetreFonctionnel.get(i);
                    if(!perimetreFonctionnel.getNom().contentEquals("Accès direct"))
                    {
                        List<Service> serviceList = utilisateurConnecte.getServicesUtilisateurParPerimetreFonctionnel(perimetreFonctionnel);
                        ListIemParent.put(perimetreFonctionnel, serviceList);
                    }
                }
            }


            expandablelistView = (ExpandableListView) findViewById(R.id.expandableListView);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            expandablelistView.setIndicatorBounds(600, 0);
            expandablelistView.setChildDivider(getResources().getDrawable(R.drawable.grey_color_fonce, null));

            navigationExpandableListAdapter = new ServiceActivityExpandableAdapter(this, ListePerimetreFonctionnel, ListIemParent, serviceIndicateurNom);
            expandablelistView.setAdapter(navigationExpandableListAdapter);

            nbPerimetreFonctionnel = ListePerimetreFonctionnel.size();
            if (nbPerimetreFonctionnel > 0) {
                expandablelistView.expandGroup(nbPerimetreFonctionnel - 1);
            }



            expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Service  serviceSelectionne = (Service) navigationExpandableListAdapter.getChild(groupPosition, childPosition);

                    Service serviceCourant = ServiceOpenHelper.getServiceByID(db, serviceSelectionne.getId());
                    if (serviceSelectionne != null) {

                        Class classe_demande = null;
                        try {
                            classe_demande = Class.forName("fr.alcyons.phiwms_mobile.Services."+serviceSelectionne.getActiviteMobile());
                        } catch (ClassNotFoundException ignored) {
                        }
                        if (classe_demande ==  null || classe_demande.getName().isEmpty() || classe_demande.getName().contentEquals("fr.alcyons.phiwms_mobile.ServiceEnCreationActivity"))
                        {
                            afficherSnackBar("EnCoursDeDeveloppement");
                        }
                        else
                        {
                            //on gere le score du service
                            serviceSelectionne.setScore(serviceSelectionne.getScore()+1);
                            ServiceOpenHelper.mettreAJourUnServiceEnBD(db, serviceSelectionne);
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ServiceOpenHelper.Constantes.TABLE_SERVICE, serviceSelectionne.getPhiMR4UUID(), serviceSelectionne.getId(), DBOpenHelper.ActionsEAS.MAJ);

                            // Appeler l'activité correspondante au service sélectionné
                            Intent intentVersService = new Intent(context, classe_demande);

                            // Récupération des éléments à transmettre à la prochaine activité
                            Bundle extras = new Bundle();

                            if(serviceActuel != null)
                            {
                                intentVersService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intentVersService.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intentVersService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intentVersService.putExtra("EXIT", true);
                            }

                            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            extras.putInt("serviceSelectionneID", serviceSelectionne.getId()); //!\ Il est nécessaire de transmettre cet élément pour gérer les services non disponible avec une seule activité
                            intentVersService.putExtras(extras);

                            // Appel de la prochaine activité
                            context.startActivity(intentVersService);
                        }
                    }
                    return false;
                }
            });

            final SearchView barreDeRechercheSearchView = (SearchView) findViewById(R.id.barreDeRecherche);
            barreDeRechercheSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    navigationExpandableListAdapter.filter(query);
                    expandablelistView.setAdapter(navigationExpandableListAdapter);
                    expandAll();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String newText) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            navigationExpandableListAdapter.filter(newText);
                            expandablelistView.setAdapter(navigationExpandableListAdapter);
                            expandAll();
                        }
                    });
                    return false;
                }
            });

            barreDeRechercheSearchView.setQueryHint("Nom service");
            barreDeRechercheSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    barreDeRechercheSearchView.setQuery("", true);
                    collapseAll();
                    return false;
                }
            });

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setIcon(R.mipmap.ic_logo_phiwms);
            getSupportActionBar().setTitle("");

            if(serviceActuel != null)
            {
                getSupportActionBar().setIcon(R.drawable.ic_menu);
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alerte.afficherAlerteConfirmation(context, getLayoutInflater(), ServiceActivity.this.getBundle(),"Souhaitez-vous revenir à l'écran d'accueil des services ?", true, false, ((Activity) context));
                    }
                });

                perimetreCourantTextView.setText(serviceActuel.getNom());
            }
        }
    }

    @Override
    public void setContentView(int layoutDirection) {
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.contenuPage);
        viewGroup.removeAllViews();
        viewGroup.addView(View.inflate(this, layoutDirection, null));
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           /*if (doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = false;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Cliquez à nouveau pour quitter l'application", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> {
                if(doubleBackToExitPressedOnce)
                {
                    Intent intent = new Intent(context, NavigationActivity.class);
                    Bundle extras = new Bundle();
                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    intent.putExtras(extras);
                    context.startActivity(intent);
                }
            }, 250);
            Intent intent = new Intent(context, NavigationActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            intent.putExtras(extras);
            context.startActivity(intent);

        }
    }*/


    public Bundle getBundle() {
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        if(serviceActuel != null)
            extras.putInt("serviceSelectionneID", serviceActuel.getId());
        return extras;
    }


    public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void recupererIndicateurs() {


        if (!statutConnexion) {
            Alerte.afficherAlerte(ServiceActivity.this, "Alerte", "Veuillez contacter la société Alcyons ! \n Impossible de se connecter à la base de données.", "alerte");
            return;
        }
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteIndicateur + "bureau";
        RequestQueue requestQueue = new Volley().newRequestQueue(ServiceActivity.this);

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int resultCount = response.getInt("resultCount");
                    if (resultCount == 0) {
                        String erreur = response.getString("erreur");
                        if (erreur.equals(ServiceActivity.this.getString(R.string.tokenInvalide))) {
                            DBOpenHelper.viderBasesDeDonnees(db);
                            ServiceActivity.this.finish();
                            Intent intent = new Intent(ServiceActivity.this, AuthentificationActivity.class);
                            ServiceActivity.this.startActivity(intent);
                        } else if (erreur.equals(ServiceActivity.this.getString(R.string.tokenExpire))) {
                            Alerte.afficherAlerte(ServiceActivity.this, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                            ServiceActivity.this.finish();
                            Intent intent = new Intent(ServiceActivity.this, AuthentificationActivity.class);
                            ServiceActivity.this.startActivity(intent);
                        } else {
                            Alerte.afficherAlerte(ServiceActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete recupererIndicateurs", "alerte");
                        }
                    } else {
                        JSONObject indicateurJSONObject = response.getJSONObject("Indicateurs");

                        int indicateurVerrouPharmacie = indicateurJSONObject.getInt("VerrouPharmacie");
                        serviceIndicateurNom.add("Verrou Pharmacie");
                        serviceIndicateurValeur.add(indicateurVerrouPharmacie);

                        int indicateurQuarantaine = indicateurJSONObject.getInt("Quarantaine");
                        serviceIndicateurNom.add("Quarantaine");
                        serviceIndicateurValeur.add(indicateurQuarantaine);

                        int indicateurControleDesRetours = indicateurJSONObject.getInt("ControleDesRetours");
                        serviceIndicateurNom.add("Contrôle des retours");
                        serviceIndicateurValeur.add(indicateurControleDesRetours);

                        int indicateurRetourPUI = indicateurJSONObject.getInt("RetourPUI");
                        serviceIndicateurNom.add("Retour PUI");
                        serviceIndicateurValeur.add(indicateurRetourPUI);

                        int indicateurRetourFrs = indicateurJSONObject.getInt("RetourFrs");
                        serviceIndicateurNom.add("Retour Frs");
                        serviceIndicateurValeur.add(indicateurRetourFrs);

                        int indicateurDestruction = indicateurJSONObject.getInt("Destruction");
                        serviceIndicateurNom.add("Destruction");
                        serviceIndicateurValeur.add(indicateurDestruction);

                        int indicateurPreparationUF = indicateurJSONObject.getInt("PreparationUF");
                        serviceIndicateurNom.add("Préparation UF");
                        serviceIndicateurValeur.add(indicateurPreparationUF);

                        int indicateurPreparationPAD = indicateurJSONObject.getInt("PreparationPAD");
                        serviceIndicateurNom.add("Préparation PAD");
                        serviceIndicateurValeur.add(indicateurPreparationPAD);

                        int indicateurLivraison = indicateurJSONObject.getInt("Livraison");
                        serviceIndicateurNom.add("Livraison");
                        serviceIndicateurValeur.add(indicateurLivraison);

                        int indicateurReceptionPUI = indicateurJSONObject.getInt("ReceptionPUI");
                        serviceIndicateurNom.add("Réception PUI");
                        serviceIndicateurValeur.add(indicateurReceptionPUI);

                        for (String serviceCourant : serviceIndicateurNom) {
                            Service service = ServiceOpenHelper.getServiceByName(db, serviceCourant);
                            serviceList.add(service);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(ServiceActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP recupererIndicateurs", "alerte");
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

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(obreq);
    }

    private void expandAll() {
        for (int i = 0; i < nbPerimetreFonctionnel; i++) {
            expandablelistView.expandGroup(i);
        }
    }

    private void collapseAll() {
        for (int i = 0; i < nbPerimetreFonctionnel; i++) {
            expandablelistView.collapseGroup(i);
        }
    }

    @Override
    public void retourService(Bundle bundle) {
        Intent serviceHome_Intent = new Intent(ServiceActivity.this, NavigationActivity.class);
        Bundle serviceHome_Bundle = new Bundle();
        serviceHome_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        serviceHome_Intent.putExtras(serviceHome_Bundle);
        ServiceActivity.this.startActivity(serviceHome_Intent);
        ServiceActivity.this.finish();
    }

    @Override
    public void retourNavigation() {
        Intent serviceHome_Intent = new Intent(ServiceActivity.this, NavigationActivity.class);
        Bundle serviceHome_Bundle = new Bundle();
        serviceHome_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        serviceHome_Intent.putExtras(serviceHome_Bundle);
        ServiceActivity.this.startActivity(serviceHome_Intent);
        ServiceActivity.this.finish();
    }

    @Override
    public void confirmationService() {

    }

    @Override
    public void retourSaisieText(String text) {

    }
}
