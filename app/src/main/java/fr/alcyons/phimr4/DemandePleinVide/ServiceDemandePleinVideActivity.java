package fr.alcyons.phimr4.DemandePleinVide;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerPreparationPleinVideActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.Classes.Demande_PleinVide;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Detail_Dot;
import fr.alcyons.phimr4.Classes.Dotation;
import fr.alcyons.phimr4.Classes.PH_Lot_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.ListViewAdapters.DotationAdapter;
import fr.alcyons.phimr4.ListViewAdapters.NewPleinVideAdapter;
import fr.alcyons.phimr4.ListViewAdapters.PH_Preparation_LivraisonAdapter;
import fr.alcyons.phimr4.Livraison.NewServiceLivraison;
import fr.alcyons.phimr4.Navigation.WebViewServiceActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.RetourPUI.ServiceRetourPUIActivity;
import fr.alcyons.phimr4.ServiceActivity;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 17/05/2018.
 */

public class ServiceDemandePleinVideActivity extends ServiceAvecConnexionActivity {

    boolean passageOnResume = true;
    PackageManager pm;

    List<Dotation> liste_plein_vide;
    DotationAdapter dotationAdapter;

    NewPleinVideAdapter pleinVideAdapter;
    ListView dotationListView;
    LinearLayout lancerScan;
    TextView textLancerScan;
    ImageView iconLancerScan;

    List<String> detailDotPleinVide_AdressageList;
    List<Detail_Dot> detailDotList;
    List<Demande_PleinVide> demandePleinVideList;

    Boolean passageParOnCreate;

    JSONArray phPreparationJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_demande_plein_vide);

        pm = ServiceDemandePleinVideActivity.this.getPackageManager();
        passageOnResume = false;
        liste_plein_vide = new ArrayList<>();
        demandePleinVideList = new ArrayList<>();
        dotationListView = (ListView) findViewById(R.id.listeView);
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);
        textLancerScan = (TextView) findViewById(R.id.textLancerScan);
        iconLancerScan = (ImageView) findViewById(R.id.iconLancerScan);
        passageParOnCreate = true;

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textLancerScan.startAnimation(anim);
        iconLancerScan.startAnimation(anim);

        dotationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                demandePleinVideList = new ArrayList<>();
                Dotation dotationCourante = pleinVideAdapter.listeDotation.get(position);
                String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());

                PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotationCourante.get_UID()) + ":" + dotationCourante.getIntitulé(), dateProchaineLivraison);
                List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);

                for(PH_Preparation_Ligne ligne_courante : ListPhPreparationLigne)
                {
                    int codeProduitCourant = ligne_courante.getProduitID();

                    Detail_Dot detailDotCorrespondant = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, codeProduitCourant, dotationCourante.get_UID());

                    if(detailDotCorrespondant != null)
                    {
                        demandePleinVideList.add(new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDotCorrespondant));
                    }
                }

                Bundle demandePleinVide_Bundle = ServiceDemandePleinVideActivity.super.getBundle();
                demandePleinVide_Bundle.putInt("depotUID_Selectionne", dotationCourante.getDepot_UID());
                demandePleinVide_Bundle.putInt("Dotation_Selection_PhiMR4UUID", dotationCourante.get_UID());
                demandePleinVide_Bundle.putInt("PreparationID", phPreparationCourante.getUID());
                demandePleinVide_Bundle.putSerializable("DemandePleinVide", (Serializable) demandePleinVideList);

                Intent demandePleinVide_Intent = new Intent(ServiceDemandePleinVideActivity.this, DetailDotationPleinVideActivity.class);
                demandePleinVide_Intent.putExtras(demandePleinVide_Bundle);
                ServiceDemandePleinVideActivity.this.startActivity(demandePleinVide_Intent);
                ServiceDemandePleinVideActivity.this.finish();
            }
        });


        lancerScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> detailDotPleinVide_AdressageList = new ArrayList<>();
                List<String> pleinVideAdressageScanneList = new ArrayList<>();
                List<String> designationListe = new ArrayList<>();
                HashMap<String, String> mapPleinVide = new LinkedHashMap<>();
                demandePleinVideList = new ArrayList<>();
                for(Dotation dotationCourante : liste_plein_vide)
                {
                    String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                    PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotationCourante.get_UID()) + ":" + dotationCourante.getIntitulé(), dateProchaineLivraison);
                    List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);

                    for(PH_Preparation_Ligne ligne_courante : ListPhPreparationLigne)
                    {
                        int codeProduitCourant = ligne_courante.getProduitID();

                        Detail_Dot detailDotCorrespondant = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, codeProduitCourant, dotationCourante.get_UID());

                        if(ligne_courante.getQte_APreparer() > 0)
                        {
                            if(detailDotCorrespondant != null) {
                                detailDotPleinVide_AdressageList.add(detailDotCorrespondant.getPleinVide_Adressage());
                                //pleinVideAdressageScanneList.add(detailDotCorrespondant.getPleinVide_Adressage());

                                demandePleinVideList.add(new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDotCorrespondant));
                            }
                        }
                        mapPleinVide.put(detailDotCorrespondant.getPleinVide_Adressage(), detailDotCorrespondant.getDesignation());
                    }
                }

                Intent detailDotationPleinVideIntent = new Intent(ServiceDemandePleinVideActivity.this, BarcodeCaptureActivity.class);
                Bundle detailDotationPleinVideBundle = ServiceDemandePleinVideActivity.super.getBundle();

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies"))
                {
                    detailDotationPleinVideIntent = new Intent(ServiceDemandePleinVideActivity.this, ScannerPreparationPleinVideActivity.class);
                    detailDotationPleinVideBundle.putInt("scannerContexteInt", R.string.scannerContextePleinVide);
                }

                detailDotationPleinVideBundle.putString("contexte", String.valueOf(R.string.scannerContextePleinVide));
                detailDotationPleinVideBundle.putBoolean("isBoutonSuppressionExistant", true);
                detailDotationPleinVideBundle.putBoolean("modeRafale", true);
                detailDotationPleinVideBundle.putStringArrayList("stringList", (ArrayList) pleinVideAdressageScanneList);
                detailDotationPleinVideBundle.putStringArrayList("detailDotPleinVide_AdressageList", (ArrayList) detailDotPleinVide_AdressageList);
                detailDotationPleinVideBundle.putString("dotationIntitule", "");
                detailDotationPleinVideIntent.putExtras(detailDotationPleinVideBundle);
                detailDotationPleinVideIntent.putExtra("designationArrayList", (HashMap) mapPleinVide);
                ServiceDemandePleinVideActivity.this.startActivityForResult(detailDotationPleinVideIntent, CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceDemandePleinVideActivity.this)  && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceDemandePleinVideActivity.this, "Veuillez patienter", "Récupération des préparations plein vide en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceDemandePleinVideActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePleinVideCourant;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String string = response.getString("erreur");
                                    if (string.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ServiceDemandePleinVideActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServiceDemandePleinVideActivity.this.finishAffinity();
                                        Intent intent = new Intent(ServiceDemandePleinVideActivity.this, AuthentificationActivity.class);
                                        ServiceDemandePleinVideActivity.this.startActivity(intent);
                                    }
                                } else {

                                    phPreparationJSONArray = response.getJSONArray("PH_Preparations");
                                    List<Integer> listeUIDPreparationEnInstance = PH_PreparationOpenHelper.getUIDDemandePleinVideEnInstance(db);
                                    //viderTablesConcernees();
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
                                        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, nomListe, DateProchaineLivraison);

                                        if(phPreparationCourante == null)
                                        {
                                            phPreparationCourante= new PH_Preparation(phPreparationJSONObject);

                                            long rowID = gestionnairePH_Preparation.insererUnPH_PreparationEnBDD(db, phPreparationCourante);
                                        }
                                        else
                                        {
                                            phPreparationCourante.setStatut(Statut);
                                            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationCourante);
                                        }

                                        String[] tabListe = phPreparationCourante.getListe().split(":");
                                        String[] tabId = tabListe[0].split("DPV");
                                        String idDotation = tabId[tabId.length-1];

                                        Dotation dotationCourante = DotationOpenHelper.getDotationPleinByStringId(db, idDotation);

                                        JSONArray PH_Preparation_LigneArray = phPreparationJSONObject.getJSONArray("ph_preparation_lignes");
                                        for(int j = 0; j < PH_Preparation_LigneArray.length(); j++)
                                        {
                                            JSONObject preparationLigneObject = PH_Preparation_LigneArray.getJSONObject(j);
                                            int codeProduit = preparationLigneObject.getInt("produitID");
                                            PH_Preparation_Ligne preparation_ligne = PH_Preparation_LigneOpenHelper.getPHPreparationLignesParPHPreparationAndProduit(db, phPreparationCourante, codeProduit);

                                            if(preparation_ligne == null)
                                            {
                                                preparation_ligne = new PH_Preparation_Ligne(preparationLigneObject, phPreparationCourante.getUID());
                                                gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, preparation_ligne);
                                            }
                                            else
                                            {
                                                preparation_ligne.setQte_APreparer(preparationLigneObject.getInt("Qte_APreparer"));
                                                preparation_ligne.setQte_Demander(preparationLigneObject.getInt("Qte_Demander"));
                                                preparation_ligne.setQte_RAL(preparationLigneObject.getInt("Qte_RAL"));
                                                preparation_ligne.setQte_StockSaisie(preparationLigneObject.getInt("Qte_StockSaisie"));
                                                gestionnairePH_Preparation_Ligne.mettreAJourUnPHPreparationLigne(db,preparation_ligne);
                                            }
                                            /*int nb = Detail_DotOpenHelper.getNbDetailDot(db, dotationCourante);
                                            Detail_Dot detailDot = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, preparation_ligne.getProduitID(), dotationCourante.get_UID());
                                            if(detailDot != null)
                                            {
                                                Demande_PleinVide demandePleinVideCourant = new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDot);
                                                demandePleinVideList.add(demandePleinVideCourant);
                                            }*/

                                        }
                                    }

                                    if(listeUIDPreparationEnInstance.size() > 0)
                                    {
                                        for(int courant : listeUIDPreparationEnInstance)
                                        {
                                            PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, courant);
                                            preparation.setStatut("En cours de préparation");
                                            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, preparation);
                                        }
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
                            Alerte.afficherAlerte(ServiceDemandePleinVideActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Récupération date livraison)", "alerte");
                        }
                    }
            ) {
                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                    headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        passageParOnCreate = false;
        liste_plein_vide = DotationOpenHelper.getAllDotationPleinVide(db);
        /*dotationAdapter = new DotationAdapter(ServiceDemandePleinVideActivity.this, liste_plein_vide, db);
        dotationListView.setAdapter(dotationAdapter);
        dotationListView.setDivider(footer);*/

        List<String>listeDate = new ArrayList<String>();

        pleinVideAdapter = new NewPleinVideAdapter(ServiceDemandePleinVideActivity.this, db, utilisateurConnecte);

        for (Dotation dotationCourante : liste_plein_vide) {
            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
            dotationCourante.setDateLivraison(dateProchaineLivraison);
        }

        Collections.sort(liste_plein_vide, new Comparator<Dotation>() {
            @Override
            public int compare(Dotation o1, Dotation o2) {
                return o1.getDateLivraison().compareTo(o2.getDateLivraison());
            }
        });

        for (Dotation dotationCourante : liste_plein_vide) {
            if (!listeDate.contains(dotationCourante.getDateLivraison())) {
                listeDate.add(dotationCourante.getDateLivraison());
                pleinVideAdapter.addSectionHeaderItem(dotationCourante);
            }

            pleinVideAdapter.addItem(dotationCourante);
        }

        dotationListView.setDivider(footer);
        dotationListView.setAdapter(pleinVideAdapter);



        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(liste_plein_vide.size()));
        passageOnResume = true;
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServiceDemandePleinVideActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION: {
                    String PHITAGLOCALISATION = data.getStringExtra("code");
                    List<String> stringList = data.getExtras().getStringArrayList("listeString");

                    if(PHITAGLOCALISATION != null)
                    {
                        String dotationId = PHITAGLOCALISATION.replace("PHITAGLOCALISATION_", "");

                        Dotation dotationCourante = gestionnaireDotation.getDotationPleinByStringId(db, dotationId);
                        demandePleinVideList = new ArrayList<>();

                        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotationCourante.get_UID()) + ":" + dotationCourante.getIntitulé(), dateProchaineLivraison);
                        List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);

                        for(PH_Preparation_Ligne ligne_courante : ListPhPreparationLigne)
                        {
                            int codeProduitCourant = ligne_courante.getProduitID();

                            Detail_Dot detailDotCorrespondant = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, codeProduitCourant, dotationCourante.get_UID());

                            if(detailDotCorrespondant != null)
                            {
                                demandePleinVideList.add(new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDotCorrespondant));
                            }
                        }

                        Bundle demandePleinVide_Bundle = ServiceDemandePleinVideActivity.super.getBundle();
                        demandePleinVide_Bundle.putInt("depotUID_Selectionne", dotationCourante.getDepot_UID());
                        demandePleinVide_Bundle.putInt("Dotation_Selection_PhiMR4UUID", dotationCourante.get_UID());
                        demandePleinVide_Bundle.putInt("PreparationID", phPreparationCourante.getUID());
                        demandePleinVide_Bundle.putSerializable("DemandePleinVide", (Serializable) demandePleinVideList);

                        Intent demandePleinVide_Intent = new Intent(ServiceDemandePleinVideActivity.this, DetailDotationPleinVideActivity.class);
                        demandePleinVide_Intent.putExtras(demandePleinVide_Bundle);
                        ServiceDemandePleinVideActivity.this.startActivity(demandePleinVide_Intent);
                        ServiceDemandePleinVideActivity.this.finish();
                    }
                    else if(stringList != null)
                    {
                        if(demandePleinVideList == null)
                        {
                            demandePleinVideList = new ArrayList<>();
                        }
                        List<Demande_PleinVide> demandePleinVideASupprimerList = new ArrayList<>();
                        boolean doitEtreAjouter = true;
                        boolean valider = false;
                        for (String pleinVideAdressage : stringList) {

                            Detail_Dot detailDotAjouter = null;
                            Detail_Dot detailDotSupprimer = null;

                            switch (pleinVideAdressage){
                                case "PHITAGACTION_Supprimer":
                                    doitEtreAjouter = false;
                                    break;
                                case "PHITAGACTION_Valider":
                                    valider = true;
                                    break;
                                default:
                                    Detail_Dot detail_dot = gestionnaireDetail_Dot.getDetailDotPleinVideAdressage(db, pleinVideAdressage);
                                    if(doitEtreAjouter) {
                                        detailDotAjouter = detail_dot;
                                    }
                                    else{
                                        detailDotSupprimer = detail_dot;
                                    }
                                    doitEtreAjouter = true;
                                    break;
                            }

                            if(valider){
                                break;
                            }
                            else if (detailDotAjouter != null) {
                                Dotation dotation = DotationOpenHelper.getDotationPleinByStringId(db, String.valueOf(detailDotAjouter.getDotation_UID()));
                                String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
                                PH_Preparation ph_preparation = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db,"Dotation PleinVide DPV" + String.valueOf(dotation.get_UID()) + ":" + dotation.getIntitulé(), dateProchaineLivraison);
                                if(!ph_preparation.getStatut().contentEquals("En cours de préparation"))
                                {
                                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, ph_preparation, detailDotAjouter.getCode_produit());
                                    if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("AVODD"))
                                    {
                                        int quantite = detailDotAjouter.getQte() - detailDotAjouter.getStock_minimum();
                                        ph_preparation_ligne.setQte_APreparer(quantite);
                                        ph_preparation_ligne.setQte_Demander(quantite);
                                        ph_preparation_ligne.setQte_RAL(quantite);
                                    }
                                    else
                                    {
                                        ph_preparation_ligne.setQte_APreparer(detailDotAjouter.getQte());
                                        ph_preparation_ligne.setQte_Demander(detailDotAjouter.getQte());
                                        ph_preparation_ligne.setQte_RAL(detailDotAjouter.getQte());
                                    }

                                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                                }
                            }
                            else if(detailDotSupprimer != null){
                                Dotation dotation = DotationOpenHelper.getDotationPleinByStringId(db, String.valueOf(detailDotAjouter.getDotation_UID()));
                                String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
                                PH_Preparation ph_preparation = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db,"Dotation PleinVide DPV" + String.valueOf(dotation.get_UID()) + ":" + dotation.getIntitulé(), dateProchaineLivraison);
                                if(!ph_preparation.getStatut().contentEquals("En cours de préparation"))
                                {
                                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, ph_preparation, detailDotSupprimer.getCode_produit());
                                    ph_preparation_ligne.setQte_APreparer(0);
                                    ph_preparation_ligne.setQte_Demander(0);
                                    ph_preparation_ligne.setQte_RAL(0);
                                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                                }
                            }
                        }

                        ElementASynchroniserOpenHelper.toutSynchroniser(ServiceDemandePleinVideActivity.this, db, utilisateurConnecte, false);

                        onResume();
                    }
                    else
                    {
                        onBackPressed();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public void afficherAlerte()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDemandePleinVideActivity.this);
        View layout = ServiceDemandePleinVideActivity.this.getLayoutInflater().inflate(R.layout.alerte_confirmation, null);

        ImageView buttonOk = (ImageView) layout.findViewById(R.id.buttonOk);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Conseil");
        messageFin.setText("Avant de commencer, réaliser un tour dans votre stock pour vérifier que vous disposez de toutes les étiquettes.");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                passageOnResume = true;
                onResume();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuInformation);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                afficherInformationService();
                return true;
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
        menu.findItem(R.id.menuInformation).setVisible(true);
        return true;
    }

    protected void afficherInformationService()
    {
        Service serviceCourant = ServiceOpenHelper.getServiceByID(db, intent.getExtras().getInt("serviceSelectionneID"));
        Intent intentWebView = new Intent(ServiceDemandePleinVideActivity.this, WebViewServiceActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putSerializable("Service_Courant", serviceCourant);
        intentWebView.putExtras(extras);

        // Appel de la prochaine activité
        ServiceDemandePleinVideActivity.this.startActivity(intentWebView);
    }

    public int CreatePhPreparationLigne(Detail_Dot detailDot, Dotation dotation, Depot depot)
    {
        int id = 0;

        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotation.get_UID()) + ":" + dotation.getIntitulé(), dateProchaineLivraison);
        PH_Preparation_Ligne ph_preparation_ligneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, phPreparationCourante, detailDot.getCode_produit());

        if(ph_preparation_ligneCourant == null)
        {
            Produit produitCorrespondant = gestionnaireProduit.getProduitByID(db, detailDot.getCode_produit());

            Random phPreparationRandom = new Random();
            int phPreparationLigneID = phPreparationRandom.nextInt();
            if (phPreparationLigneID > 0) {
                phPreparationLigneID = phPreparationLigneID * -1;
            }

            // Initialisation des données permettant de créer un PH_Préparation_Ligne
            int PreparationID = phPreparationCourante.getUID();
            int _UID = phPreparationLigneID;
            int produitID = produitCorrespondant.getID_produit();
            String produitDesignation = produitCorrespondant.getDesignation_interne();
            int Qte_APreparer = detailDot.getQte();
            int qteDemander = detailDot.getQte() - detailDot.getStock_minimum();
            int Qte_livrer = 0;
            Boolean Livrer = false;
            Boolean Valider = false;
            String ValidationDate = "";
            String produitReference = produitCorrespondant.getRef_fourni();
            String ZoneDepot = "";
            String produitCategorie = produitCorrespondant.getCategorie();
            int Qte_RAL = Qte_APreparer;
            String SYS_DT_MAJ = "";
            String SYS_HEURE_MAJ = "";
            String SYS_USER_MAJ = "";
            double produitCondDistrib = produitCorrespondant.getCond_distrib();
            double produitPUHT = 0;
            Boolean Suivi_Par_Lot = produitCorrespondant.isSuivi_Lot();
            int patientID = 0;
            String PatientNom = "";
            String PrescripteurNom = "";
            String prescripteurReference = "";
            int Ordre_Impression = 0;
            int Prescription_ID = 0;
            String LotNumero = "";
            String PeremptionDate = "0000-00-00";
            double produitPoids = 0;
            double produitTVA = 0;
            double Montant_HT = 0;
            double Montant_TTC = 0;
            double PoidsTotal = 0;
            String depot_Destinataire_Reference = "";
            String utilisation_Date_Prevue = "";
            int Qte_besoin = Qte_APreparer;
            int Qte_StockSaisie = Qte_APreparer;
            int Qte_Demander = qteDemander;
            String EmplacementParDefaut = "";
            int Qte_preparer = 0;
            boolean accepter = false;

            // Création et insertion en base du PH_Preparation_Ligne
            PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter, phPreparationCourante.getUID());
            id = (int) gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

            long rowId = gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
        }

        return id;
    }
}
