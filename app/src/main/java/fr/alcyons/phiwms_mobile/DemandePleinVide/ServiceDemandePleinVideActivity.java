package fr.alcyons.phiwms_mobile.DemandePleinVide;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationPleinVideActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Demande_PleinVide;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PleinVideAdapter;
import fr.alcyons.phiwms_mobile.Navigation.WebViewServiceActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDemandePleinVideActivity extends ServiceAvecConnexionActivity {

    boolean passageOnResume = true;
    PackageManager pm;
    List<Dotation> liste_plein_vide;

    PleinVideAdapter pleinVideAdapter;
    ListView dotationListView;
    LinearLayout lancerScan;
    TextView textLancerScan;
    ImageView iconLancerScan;

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
        dotationListView = findViewById(R.id.listeView);
        lancerScan = findViewById(R.id.lancerScan);
        textLancerScan = findViewById(R.id.textLancerScan);
        iconLancerScan = findViewById(R.id.iconLancerScan);
        passageParOnCreate = true;

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textLancerScan.startAnimation(anim);
        iconLancerScan.startAnimation(anim);

        dotationListView.setOnItemClickListener((parent, view, position, id) -> {
            demandePleinVideList = new ArrayList<>();
            Dotation dotationCourante = pleinVideAdapter.listeDotation.get(position);
            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());

            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotationCourante.get_UID() + ":" + dotationCourante.getIntitule(), dateProchaineLivraison);
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
            demandePleinVide_Bundle.putSerializable("ListePleinVide", (Serializable) demandePleinVideList);

            Intent demandePleinVide_Intent = new Intent(ServiceDemandePleinVideActivity.this, DetailDotationPleinVideActivity.class);
            demandePleinVide_Intent.putExtras(demandePleinVide_Bundle);
            ServiceDemandePleinVideActivity.this.startActivity(demandePleinVide_Intent);
            ServiceDemandePleinVideActivity.this.finish();
        });


        lancerScan.setOnClickListener(v -> {
            List<String> detailDotPleinVide_AdressageList = new ArrayList<>();
            List<String> pleinVideAdressageScanneList = new ArrayList<>();
            HashMap<String, String> mapPleinVide = new LinkedHashMap<>();
            demandePleinVideList = new ArrayList<>();
            for(Dotation dotationCourante : liste_plein_vide)
            {
                String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotationCourante.get_UID() + ":" + dotationCourante.getIntitule(), dateProchaineLivraison);
                List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);

                for(PH_Preparation_Ligne ligne_courante : ListPhPreparationLigne)
                {
                    int codeProduitCourant = ligne_courante.getProduitID();

                    Detail_Dot detailDotCorrespondant = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, codeProduitCourant, dotationCourante.get_UID());

                    if(ligne_courante.getQte_APreparer() > 0)
                    {
                        if(detailDotCorrespondant != null) {
                            detailDotPleinVide_AdressageList.add(detailDotCorrespondant.getPleinVide_Adressage());

                            demandePleinVideList.add(new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDotCorrespondant));
                        }
                    }
                    assert detailDotCorrespondant != null;
                    mapPleinVide.put(detailDotCorrespondant.getPleinVide_Adressage(), detailDotCorrespondant.getDesignation());
                }
            }

            Intent detailDotationPleinVideIntent = new Intent(ServiceDemandePleinVideActivity.this, BarcodeCaptureActivity.class);
            Bundle detailDotationPleinVideBundle = ServiceDemandePleinVideActivity.super.getBundle();

            if(Build.MANUFACTURER.contains("Zebra Technologies"))
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
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (statutConnexion  && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceDemandePleinVideActivity.this, LayoutInflater.from(ServiceDemandePleinVideActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceDemandePleinVideActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePleinVideCourant;

            // Takes the response from the JSON request
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
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

                                passageParOnCreate = false;
                                liste_plein_vide = DotationOpenHelper.getAllDotationPleinVide(db);

                                List<String>listeDate = new ArrayList<>();

                                pleinVideAdapter = new PleinVideAdapter(ServiceDemandePleinVideActivity.this, db, utilisateurConnecte);

                                for (Dotation dotationCourante : liste_plein_vide) {
                                    String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                                    dotationCourante.setDateLivraison(dateProchaineLivraison);
                                }

                                liste_plein_vide.sort(Comparator.comparing(Dotation::getDateLivraison));

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

                                new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(ServiceDemandePleinVideActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Récupération date livraison)", "alerte");
                    }
            ) {
                /**
                 * Passing some request headers
                 */
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
        }
        else
        {
            passageParOnCreate = false;
            liste_plein_vide = DotationOpenHelper.getAllDotationPleinVide(db);

            List<String>listeDate = new ArrayList<>();

            pleinVideAdapter = new PleinVideAdapter(ServiceDemandePleinVideActivity.this, db, utilisateurConnecte);

            for (Dotation dotationCourante : liste_plein_vide) {
                String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                dotationCourante.setDateLivraison(dateProchaineLivraison);
            }

            liste_plein_vide.sort(Comparator.comparing(Dotation::getDateLivraison));

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
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServiceDemandePleinVideActivity.this.finish();
        } else {
            if (requestCode == CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION) {
                String PHITAGLOCALISATION = data.getStringExtra("code");
                List<String> stringList = Objects.requireNonNull(data.getExtras()).getStringArrayList("listeString");

                if (PHITAGLOCALISATION != null) {
                    String dotationId = PHITAGLOCALISATION.replace("PHITAGLOCALISATION_", "");

                    Dotation dotationCourante = DotationOpenHelper.getDotationPleinByStringId(db, dotationId);
                    demandePleinVideList = new ArrayList<>();

                    String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
                    PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotationCourante.get_UID() + ":" + dotationCourante.getIntitule(), dateProchaineLivraison);
                    List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);

                    for (PH_Preparation_Ligne ligne_courante : ListPhPreparationLigne) {
                        int codeProduitCourant = ligne_courante.getProduitID();

                        Detail_Dot detailDotCorrespondant = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, codeProduitCourant, dotationCourante.get_UID());

                        if (detailDotCorrespondant != null) {
                            demandePleinVideList.add(new Demande_PleinVide(dotationCourante.getDepot_UID(), detailDotCorrespondant));
                        }
                    }

                    Bundle demandePleinVide_Bundle = ServiceDemandePleinVideActivity.super.getBundle();
                    demandePleinVide_Bundle.putInt("depotUID_Selectionne", dotationCourante.getDepot_UID());
                    demandePleinVide_Bundle.putInt("Dotation_Selection_phiwms_mobileUUID", dotationCourante.get_UID());
                    demandePleinVide_Bundle.putInt("PreparationID", phPreparationCourante.getUID());
                    demandePleinVide_Bundle.putSerializable("ListePleinVide", (Serializable) demandePleinVideList);

                    Intent demandePleinVide_Intent = new Intent(ServiceDemandePleinVideActivity.this, DetailDotationPleinVideActivity.class);
                    demandePleinVide_Intent.putExtras(demandePleinVide_Bundle);
                    ServiceDemandePleinVideActivity.this.startActivity(demandePleinVide_Intent);
                    ServiceDemandePleinVideActivity.this.finish();
                } else if (stringList != null) {
                    if (demandePleinVideList == null) {
                        demandePleinVideList = new ArrayList<>();
                    }
                    boolean doitEtreAjouter = true;
                    boolean valider = false;
                    for (String pleinVideAdressage : stringList) {

                        Detail_Dot detailDotAjouter = null;
                        Detail_Dot detailDotSupprimer = null;

                        switch (pleinVideAdressage) {
                            case "PHITAGACTION_Supprimer":
                                doitEtreAjouter = false;
                                break;
                            case "PHITAGACTION_Valider":
                                valider = true;
                                break;
                            default:
                                Detail_Dot detail_dot = Detail_DotOpenHelper.getDetailDotPleinVideAdressage(db, pleinVideAdressage);
                                if (doitEtreAjouter) {
                                    detailDotAjouter = detail_dot;
                                } else {
                                    detailDotSupprimer = detail_dot;
                                }
                                doitEtreAjouter = true;
                                break;
                        }

                        if (valider) {
                            break;
                        } else if (detailDotAjouter != null) {
                            Dotation dotation = DotationOpenHelper.getDotationPleinByStringId(db, String.valueOf(detailDotAjouter.getDotation_UID()));
                            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
                            PH_Preparation ph_preparation = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotation.get_UID() + ":" + dotation.getIntitule(), dateProchaineLivraison);
                            if (!ph_preparation.getStatut().contentEquals("En cours de préparation")) {
                                PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, ph_preparation, detailDotAjouter.getCode_produit());
                                if (utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("AVODD")) {
                                    int quantite = detailDotAjouter.getQte() - detailDotAjouter.getStock_minimum();
                                    ph_preparation_ligne.setQte_APreparer(quantite);
                                    ph_preparation_ligne.setQte_Demander(quantite);
                                    ph_preparation_ligne.setQte_RAL(quantite);
                                } else {
                                    ph_preparation_ligne.setQte_APreparer(detailDotAjouter.getQte());
                                    ph_preparation_ligne.setQte_Demander(detailDotAjouter.getQte());
                                    ph_preparation_ligne.setQte_RAL(detailDotAjouter.getQte());
                                }

                                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                            }
                        } else if (detailDotSupprimer != null) {
                            Dotation dotation = DotationOpenHelper.getDotationPleinByStringId(db, String.valueOf(detailDotAjouter.getDotation_UID()));
                            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
                            PH_Preparation ph_preparation = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotation.get_UID() + ":" + dotation.getIntitule(), dateProchaineLivraison);
                            if (!ph_preparation.getStatut().contentEquals("En cours de préparation")) {
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
                } else {
                    onBackPressed();
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerte()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceDemandePleinVideActivity.this);
        View layout = ServiceDemandePleinVideActivity.this.getLayoutInflater().inflate(R.layout.alerte_confirmation, null);

        ImageView buttonOk = layout.findViewById(R.id.buttonOk);
        TextView messageFin = layout.findViewById(R.id.messageFin);
        TextView titre = layout.findViewById(R.id.titre);

        titre.setText("Conseil");
        messageFin.setText("Avant de commencer, réaliser un tour dans votre stock pour vérifier que vous disposez de toutes les étiquettes.");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(v -> {
            alertDialog.dismiss();
            passageOnResume = true;
            onResume();
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuInformation);
        item.setOnMenuItemClickListener(item1 -> {
            afficherInformationService();
            return true;
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
        Service serviceCourant = ServiceOpenHelper.getServiceByID(db, Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
        Intent intentWebView = new Intent(ServiceDemandePleinVideActivity.this, WebViewServiceActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putSerializable("Service_Courant", serviceCourant);
        intentWebView.putExtras(extras);

        // Appel de la prochaine activité
        ServiceDemandePleinVideActivity.this.startActivity(intentWebView);
    }
}
