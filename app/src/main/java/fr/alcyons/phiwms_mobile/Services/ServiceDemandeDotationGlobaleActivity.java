package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationPleinVideActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.DemandeDotationGlobale.InformationDotationServiceActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DotationAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDemandeDotationGlobaleActivity extends ServiceAvecConnexionActivity {

    List<Dotation> dotationList;
    List<PH_Preparation> phPreparationList;
    ListView dotationListView;
    DotationAdapter dotationAdapter;
    Calendar calendar;
    LinearLayout lancerScan;
    TextView textLancerScan;
    ImageView iconLancerScan;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_dotation_service);
        phPreparationList = new ArrayList<>();
        // Récupération du dépot sélectionné

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Dotation Globale");

        // Récupération et initialisation de l'action de la listView
        dotationListView = findViewById(R.id.listeView);
        lancerScan = findViewById(R.id.lancerScan);
        textLancerScan = findViewById(R.id.textLancerScan);
        iconLancerScan = findViewById(R.id.iconLancerScan);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textLancerScan.startAnimation(anim);
        iconLancerScan.startAnimation(anim);

        dotationListView.setOnItemClickListener((parent, view, position, id) -> {
            Dotation DotationSelectionne = dotationAdapter.listeDotation.get(position);
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(db, "Dotation Globale : " + DotationSelectionne.getIntitule(), DotationSelectionne.getDateLivraison());

            Intent listeDotationService_Intent = ServiceDemandeDotationGlobaleActivity.this.getListeDotationServiceIntent(DotationSelectionne, phPreparationCourante);
            ServiceDemandeDotationGlobaleActivity.this.startActivity(listeDotationService_Intent);
            ServiceDemandeDotationGlobaleActivity.this.finish();
        });

        /* Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        lancerScan.setOnClickListener(v -> {
            Intent detailDotationPleinVideIntent = new Intent(ServiceDemandeDotationGlobaleActivity.this, BarcodeCaptureActivity.class);
            Bundle detailDotationPleinVideBundle = ServiceDemandeDotationGlobaleActivity.super.getBundle();

            if(Build.MANUFACTURER.contains("Zebra Technologies"))
            {
                detailDotationPleinVideIntent = new Intent(ServiceDemandeDotationGlobaleActivity.this, ScannerPreparationPleinVideActivity.class);
                detailDotationPleinVideBundle.putInt("scannerContexteInt", R.string.scannerContextePleinVide);
            }

            HashMap<String, String> test  = new LinkedHashMap<>();
            ArrayList<String> stringList = new ArrayList<>();
            ArrayList<String> stringAdressageList = new ArrayList<>();
            detailDotationPleinVideBundle.putString("contexte", String.valueOf(R.string.scannerContextePleinVide));
            detailDotationPleinVideBundle.putBoolean("isBoutonSuppressionExistant", true);
            detailDotationPleinVideBundle.putBoolean("modeRafale", true);
            detailDotationPleinVideBundle.putStringArrayList("stringList", stringList);
            detailDotationPleinVideBundle.putStringArrayList("detailDotPleinVide_AdressageList", stringAdressageList);
            detailDotationPleinVideBundle.putString("dotationIntitule", "");
            detailDotationPleinVideIntent.putExtras(detailDotationPleinVideBundle);
            detailDotationPleinVideIntent.putExtra("designationArrayList", test);
            ServiceDemandeDotationGlobaleActivity.this.startActivityForResult(detailDotationPleinVideIntent, CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION);
        });
    }

    @NonNull
    private Intent getListeDotationServiceIntent(Dotation DotationSelectionne, PH_Preparation phPreparationCourante) {
        Bundle listeDotationService_Bundle = ServiceDemandeDotationGlobaleActivity.super.getBundle();
        listeDotationService_Bundle.putInt("depotSelectionneID", DotationSelectionne.getDepot_UID());
        listeDotationService_Bundle.putInt("dotationSelectionneID", DotationSelectionne.getPhiMR4UUID());
        listeDotationService_Bundle.putInt("phPreparationID", phPreparationCourante.getUID());

        Intent listeDotationService_Intent = new Intent(ServiceDemandeDotationGlobaleActivity.this, InformationDotationServiceActivity.class);
        listeDotationService_Intent.putExtras(listeDotationService_Bundle);
        return listeDotationService_Intent;
    }

    @Override
    public void onResume() {
        super.onResume();
        dotationList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceDemandeDotationGlobaleActivity.this, LayoutInflater.from(ServiceDemandeDotationGlobaleActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceDemandeDotationGlobaleActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDotationGlobaleCourant;

            // Takes the response from the JSON request
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String string = response.getString("erreur");
                                if (string.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(ServiceDemandeDotationGlobaleActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    ServiceDemandeDotationGlobaleActivity.this.finishAffinity();
                                    Intent intent = new Intent(ServiceDemandeDotationGlobaleActivity.this, AuthentificationActivity.class);
                                    ServiceDemandeDotationGlobaleActivity.this.startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(ServiceDemandeDotationGlobaleActivity.this, NavigationActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                    intent.putExtras(extras);
                                    ServiceDemandeDotationGlobaleActivity.this.startActivity(intent);
                                    ServiceDemandeDotationGlobaleActivity.this.finish();
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
                                    PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(db, nomListe, DateProchaineLivraison);

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
                        Alerte.afficherAlerte(ServiceDemandeDotationGlobaleActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Dotation Service)", "alerte");
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            passageParOnCreate = false;
        }
        else
        {
            gestionAdapter();
        }

    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, null, null, "Intitulé dotation...");
        return true;
    }


    private PH_Preparation CreationPhPreparation(Dotation dotation)
    {
        Random phPreparationRandom = new Random();
        int phPreparationID = phPreparationRandom.nextInt();
        if (phPreparationID > 0) {
            phPreparationID = phPreparationID * -1;
        }

        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
        //Création d'une PH_Preparation
        // Initialisation des données permettant de créer un PH_Préparation
        int UID = phPreparationID;
        String Service = "";
        Boolean Erreur_Valid = false;
        String PHIE_Tag = "";
        String Saisie_Le = "";
        String A_tel_heure = "";
        int produitID = 0;
        String produitDesignation = "";
        double Qte_demandee = 0;
        Boolean Livree = false;
        Boolean Validee = false;
        String Origine = "";
        String Liste = "Dotation Globale : " + dotation.getIntitule();
        int depotDestinataireID = dotation.getDepot_UID();
        String depotDestinataireReference = dotation.getRef_Depot();
        String SYS_DT_MAJ = "";
        String SYS_HEURE_MAJ = "";
        String SYS_USER_MAJ = "";
        String PrescripteurReference = "";
        String Prescription_date = "";
        String PrescripteurNom = "";
        String depotOrigineReference = "";
        int depotOrigineID = 0;
        if(depotPUI != null)
        {
            depotOrigineReference = depotPUI.getDepot_Reference();
            depotOrigineID = depotPUI.getDepot_UID();
        }

        String Commentaires = "";
        String PreparationDate = "";
        String[] dateTab = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID()).split("/");
        String LivraisonPrevueDate;
        if(dateTab.length > 1)
        {
            LivraisonPrevueDate = dateTab[dateTab.length-1]+"-"+dateTab[1]+"-"+dateTab[0];
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            LivraisonPrevueDate = dateFormat.format(tomorrow);
        }
        String DN_Groupe = "";
        double Montant_HT = 0;
        double Montant_TTC = 0;
        double Poids = 0;
        int Commande_ID = 0;
        String Preparateur = "";
        String Statut = "En instance";
        String PHIE_SYNCHRO = "";
        String receptionUFNonComforme = "";
        String livraisonDate = "";
        String Frequence = "";
        String previsionDateDebut = "";
        String previsionDateFin = "";
        Boolean URGENT = dotation.isURGENCE();
        String Motif = "";
        int preparateur_userID = 0;
        int pharmacien_userID = 0;
        double Volume = 0;
        int PaletteNB = 0;
        int CaisseNB = 0;
        int Conteneur_NB = 0;
        String numero_scelle = "";

        // Création et insertion en base du PH_Preparation
        PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, Commentaires, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, Motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB, CaisseNB,Conteneur_NB, numero_scelle);
        int ph_preparationphiwms_mobileuid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationphiwms_mobileuid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

        List<Detail_Dot> listDetailDot = Detail_DotOpenHelper.getAllDetailDotParDotation(db, dotation);

        for(Detail_Dot detail_dot : listDetailDot)
        {
            CreatePhPreparationLigneDetail(detail_dot, dotation);
        }

        return ph_preparation;
    }

    public void CreatePhPreparationLigneDetail(Detail_Dot detailDot, Dotation dotation)
    {
        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
        if(Objects.equals(dateProchaineLivraison, ""))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            dateProchaineLivraison = dateFormat.format(tomorrow);
        }
        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(db, "Dotation Globale : " + dotation.getIntitule(), dateProchaineLivraison);
        PH_Preparation_Ligne ph_preparation_ligneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, phPreparationCourante, detailDot.getCode_produit());

        if(ph_preparation_ligneCourant == null)
        {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, detailDot.getCode_produit());

            if(produitCorrespondant != null)
            {
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
                int Qte_APreparer = 0;
                int Qte_livrer = 0;
                Boolean Livrer = false;
                Boolean Valider = false;
                String ValidationDate = "";
                String produitReference = produitCorrespondant.getRef_fourni();
                String ZoneDepot = "";
                String produitCategorie = produitCorrespondant.getCategorie();
                int Qte_RAL = 0;
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
                int Qte_besoin = detailDot.getQte();
                int Qte_StockSaisie = -1;
                if(dotation.isCommandeAB())
                    Qte_StockSaisie = 0;
                int Qte_Demander = 0;
                String EmplacementParDefaut = "";
                int Qte_preparer = 0;
                boolean accepter = false;

                // Création et insertion en base du PH_Preparation_Ligne
                PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter, phPreparationCourante.getUID());
                PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CodesEchangesActivites.RESULT_PLEINVIDE_LOCALISATION) {
            }
        }
    }

    private void gestionAdapter()
    {
        List<String>listeDate = new ArrayList<>();
        ArrayList<Dotation> dotationsListe;
        dotationsListe = DotationOpenHelper.getDotationGlobale(db);
        /* Code nécessaire à l'affichage de la liste */
        dotationAdapter = new DotationAdapter(ServiceDemandeDotationGlobaleActivity.this, db, utilisateurConnecte);

        for (Dotation dotationCourante : dotationsListe) {
            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourante.getDepot_UID());
            if(Objects.equals(dateProchaineLivraison, ""))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                dateProchaineLivraison = dateFormat.format(tomorrow);
            }

            dotationCourante.setDateLivraison(dateProchaineLivraison);

            //on check la présence des préparations en base
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(db, "Dotation Globale : " + dotationCourante.getIntitule(), dateProchaineLivraison);

            if(phPreparationCourante == null)
            {
                phPreparationCourante = CreationPhPreparation(dotationCourante);
            }

            phPreparationList.add(phPreparationCourante);
        }
        ElementASynchroniserOpenHelper.toutSynchroniser(ServiceDemandeDotationGlobaleActivity.this, db, utilisateurConnecte, false);
        dotationsListe.sort(new Comparator<Dotation>() {
            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(Dotation o1, Dotation o2) {
                try {
                    return Objects.requireNonNull(f.parse(o1.getDateLivraison())).compareTo(f.parse(o2.getDateLivraison()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ArrayList<String> listeDepot = new ArrayList<>();
        for (Dotation dotationCourante : dotationsListe) {
            if (!listeDate.contains(dotationCourante.getDateLivraison()) || !listeDepot.contains(dotationCourante.getRef_Depot())) {
                listeDate.add(dotationCourante.getDateLivraison());
                listeDepot.add(dotationCourante.getRef_Depot());
                dotationAdapter.addSectionHeaderItem(dotationCourante);
            }

            dotationAdapter.addItem(dotationCourante);
        }

        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        dotationListView.setDivider(footer);
        dotationListView.setAdapter(dotationAdapter);

        if (dotationsListe.isEmpty()) {
            vide = true;
            nomServiceVide = "Dotation Service";
            ServiceDemandeDotationGlobaleActivity.this.finish();
        }

        invalidateOptionsMenu();
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }
}
