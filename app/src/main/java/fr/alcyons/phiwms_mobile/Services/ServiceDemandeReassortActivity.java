package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.android.volley.RequestQueue;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.DemandeReassort.InformationDemandeReassortActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ReassortAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDemandeReassortActivity extends ServiceAvecConnexionActivity {
    List<PH_Reassort> phReassortList;
    ListView phReassortListView;
    LinearLayout lancerScan;
    TextView textLancerScan;
    ImageView iconLancerScan;
    LinearLayout layoutSyncronisationPreparations;
    LinearLayout sousTitreDate;
    TextView compteurEnregistrementCourant;
    TextView nbTotalPreparations;
    TextView compteurEnregistrementPLCourant;
    TextView nbTotalPreparationsLignes;
    int nbPreparationSynchroniser;
    ReassortAdapter reassortAdapter;
    List<PH_Preparation> phPreparationList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_dotation_service);

        phPreparationList = new ArrayList<>();

        ((TextView) findViewById(R.id.titre)).setText("Réassort de service");
        lancerScan = findViewById(R.id.lancerScan);
        textLancerScan = findViewById(R.id.textLancerScan);
        iconLancerScan = findViewById(R.id.iconLancerScan);
        sousTitreDate = findViewById(R.id.sousTitreDate);
        layoutSyncronisationPreparations = findViewById(R.id.layoutSyncronisationPreparations);
        compteurEnregistrementCourant = findViewById(R.id.compteurEnregistrementCourant);
        nbTotalPreparations = findViewById(R.id.nbTotalPreparations);
        compteurEnregistrementPLCourant = findViewById(R.id.compteurEnregistrementPLCourant);
        nbTotalPreparationsLignes = findViewById(R.id.nbTotalPreparationsLignes);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textLancerScan.startAnimation(anim);
        iconLancerScan.startAnimation(anim);

        // Récupération et initialisation de l'action de la listView
        phReassortListView = findViewById(R.id.listeView);
        phReassortListView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Reassort PH_ReassortSelectionne = reassortAdapter.listeReassort.get(position);
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(db, "Réassort de service : " + PH_ReassortSelectionne.getListe(), PH_ReassortSelectionne.getDateLivraison());

            if(phPreparationCourante.getStatut().contentEquals("En instance") || phPreparationCourante.getStatut().contentEquals("En cours de régularisation"))
            {
                Intent listeReassortService_Intent = ServiceDemandeReassortActivity.this.getListeReassortServiceIntent(phPreparationCourante, PH_ReassortSelectionne);
                ServiceDemandeReassortActivity.this.startActivity(listeReassortService_Intent);
                ServiceDemandeReassortActivity.this.finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceDemandeReassortActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceDemandeReassortActivity.this.startActivity(intent);
                ServiceDemandeReassortActivity.this.finish();
            }
        });
    }

    @NonNull
    private Intent getListeReassortServiceIntent(PH_Preparation phPreparationCourante, PH_Reassort PH_ReassortSelectionne) {
        Bundle listeReassortService_Bundle = ServiceDemandeReassortActivity.super.getBundle();
        listeReassortService_Bundle.putInt("PH_ReassortSelectionneID", PH_ReassortSelectionne.getPhiMR4UUID());
        if(phPreparationCourante != null)
        {
            listeReassortService_Bundle.putInt("depotSelectionneID", phPreparationCourante.getDepotDestinataireID());
            listeReassortService_Bundle.putInt("phPreparationID", phPreparationCourante.getUID());
        }

        Intent listeReassortService_Intent = new Intent(ServiceDemandeReassortActivity.this, InformationDemandeReassortActivity.class);
        listeReassortService_Intent.putExtras(listeReassortService_Bundle);
        return listeReassortService_Intent;
    }

    @Override
    public void onResume() {
        super.onResume();
        phReassortList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceDemandeReassortActivity.this, LayoutInflater.from(ServiceDemandeReassortActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceDemandeReassortActivity.this);

            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDemandeReassortCourant;
            // Takes the response from the JSON request
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String string = response.getString("erreur");
                                if (string.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(ServiceDemandeReassortActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    //DBOpenHelper.viderBasesDeDonnees(db);
                                    ServiceDemandeReassortActivity.this.finishAffinity();
                                    Intent intent = new Intent(ServiceDemandeReassortActivity.this, AuthentificationActivity.class);
                                    ServiceDemandeReassortActivity.this.startActivity(intent);
                                }
                                else
                                {
                                    List<PH_Reassort> listReassort = PH_ReassortOpenHelper.getPH_Reassort(db);
                                    if(listReassort.isEmpty())
                                    {
                                        vide = true;
                                        nomServiceVide = "PH_Reassort";
                                        Intent intent = new Intent(ServiceDemandeReassortActivity.this, NavigationActivity.class);
                                        Bundle extras = new Bundle();
                                        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                        intent.putExtras(extras);
                                        ServiceDemandeReassortActivity.this.startActivity(intent);
                                        ServiceDemandeReassortActivity.this.finish();
                                    }
                                    /*Intent intent = new Intent(ServiceDemandeReassortActivity.this, NavigationActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                    intent.putExtras(extras);
                                    ServiceDemandeReassortActivity.this.startActivity(intent);
                                    ServiceDemandeReassortActivity.this.finish();*/
                                }
                            } else {
                                JSONArray phPreparationJSONArray = response.getJSONArray("PH_Preparations");
                                List<Integer> listeUIDPreparationEnInstance = PH_PreparationOpenHelper.getUIDDotationGlobaleEnInstance(db);
                                for (int i = 0; i < phPreparationJSONArray.length(); i++) {
                                    JSONObject phPreparationJSONObject = phPreparationJSONArray.getJSONObject(i);

                                    String nomListe = phPreparationJSONObject.getString("Liste");
                                    int uid = phPreparationJSONObject.getInt("UID");
                                    int position = listeUIDPreparationEnInstance.indexOf(uid);
                                    String Statut = phPreparationJSONObject.getString("Statut");
                                    if(position != -1 && !Statut.contentEquals("En cours de préparation"))
                                    {
                                        listeUIDPreparationEnInstance.remove(position);
                                    }

                                    String DateProchaineLivraison = phPreparationJSONObject.getString("LivraisonPrevueDate");
                                    PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(db, nomListe, DateProchaineLivraison);

                                    if(phPreparationCourante == null)
                                    {
                                        phPreparationCourante= new PH_Preparation(phPreparationJSONObject);

                                        PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, phPreparationCourante);
                                    }
                                    else
                                    {
                                        phPreparationCourante.setStatut(Statut);
                                        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationCourante);
                                    }

                                    JSONArray PH_Preparation_LigneArray = phPreparationJSONObject.getJSONArray("ph_preparation_lignes");
                                    for(int j = 0; j < PH_Preparation_LigneArray.length(); j++)
                                    {
                                        JSONObject preparationLigneObject = PH_Preparation_LigneArray.getJSONObject(j);
                                        int codeProduit = preparationLigneObject.getInt("produitID");
                                        PH_Preparation_Ligne preparation_ligne = PH_Preparation_LigneOpenHelper.getPHPreparationLignesParPHPreparationAndProduit(db, phPreparationCourante, codeProduit);

                                        if(preparation_ligne == null)
                                        {
                                            preparation_ligne = new PH_Preparation_Ligne(preparationLigneObject, phPreparationCourante.getUID());
                                            PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, preparation_ligne);
                                        }
                                        else
                                        {
                                            preparation_ligne.setQte_APreparer(preparationLigneObject.getInt("Qte_APreparer"));
                                            preparation_ligne.setQte_Demander(preparationLigneObject.getInt("Qte_Demander"));
                                            preparation_ligne.setQte_RAL(preparationLigneObject.getInt("Qte_RAL"));
                                            preparation_ligne.setQte_StockSaisie(preparationLigneObject.getInt("Qte_StockSaisie"));
                                            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db,preparation_ligne);
                                        }
                                    }

                                }
                            }
                            gestionAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(ServiceDemandeReassortActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Demande Réassort)", "alerte");
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
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);

            passageParOnCreate = false;
        } else {
            phReassortList = PH_ReassortOpenHelper.getPH_Reassort(db);
            if (phReassortList.isEmpty()) {
                connexionNecessaire();
            }
            else
            {
                gestionAdapter();
            }
        }


    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //super.prepareOptionsMenu(menu, phReassortAdapter, null, "Intitulé réassort...");
        return true;
    }

    private void gestionAdapter()
    {
        arreterSpinner();
        List<String>listeDate = new ArrayList<>();
        List<PH_Reassort> reassortListe;
        reassortListe = PH_ReassortOpenHelper.getPH_Reassort(db);
        /* Code nécessaire à l'affichage de la liste */
        reassortAdapter = new ReassortAdapter(ServiceDemandeReassortActivity.this, db, utilisateurConnecte);

        for (PH_Reassort reassortCourant : reassortListe) {
            Depot depot = DepotOpenHelper.getDepotParReference(db, reassortCourant.getDepot_Reference());
            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, depot.getDepot_UID());
            if(Objects.equals(dateProchaineLivraison, ""))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                dateProchaineLivraison = dateFormat.format(tomorrow);
            }

            reassortCourant.setDateLivraison(dateProchaineLivraison);

            //on check la présence des préparations en base
            /*PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(db, "Réassort de service : " + reassortCourant.getListe(), dateProchaineLivraison);

            if(phPreparationCourante == null)
            {
                phPreparationCourante = CreationPhPreparation(reassortCourant);
            }

            phPreparationList.add(phPreparationCourante);*/
        }
        ElementASynchroniserOpenHelper.toutSynchroniser(ServiceDemandeReassortActivity.this, db, utilisateurConnecte, false);
        reassortListe.sort(new Comparator<PH_Reassort>() {
            @SuppressLint("SimpleDateFormat")
            final DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(PH_Reassort o1, PH_Reassort o2) {
                try {
                    return Objects.requireNonNull(f.parse(o1.getDateLivraison())).compareTo(f.parse(o2.getDateLivraison()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ArrayList<String> listeDepot = new ArrayList<>();
        for (PH_Reassort reassort : reassortListe) {
            if (!listeDate.contains(reassort.getDateLivraison()) || !listeDepot.contains(reassort.getDepot_Reference())) {
                listeDate.add(reassort.getDateLivraison());
                listeDepot.add(reassort.getDepot_Reference());
                reassortAdapter.addSectionHeaderItem(reassort);
            }

            reassortAdapter.addItem(reassort);
        }


        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        phReassortListView.setDivider(footer);
        phReassortListView.setAdapter(reassortAdapter);
        sousTitreDate.setVisibility(View.VISIBLE);
        layoutSyncronisationPreparations.setVisibility(View.GONE);

        if (reassortListe.isEmpty()) {
            vide = true;
            nomServiceVide = "PH_Reassort";
            Intent intent = new Intent(ServiceDemandeReassortActivity.this, NavigationActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            intent.putExtras(extras);
            ServiceDemandeReassortActivity.this.startActivity(intent);
            ServiceDemandeReassortActivity.this.finish();
        } else {
            reassortListe.size();
        }

        invalidateOptionsMenu();
       // new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }

}
