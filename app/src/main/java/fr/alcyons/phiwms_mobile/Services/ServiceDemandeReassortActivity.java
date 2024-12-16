package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
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

            Intent listeReassortService_Intent = ServiceDemandeReassortActivity.this.getListeReassortServiceIntent(phPreparationCourante, PH_ReassortSelectionne);
            ServiceDemandeReassortActivity.this.startActivity(listeReassortService_Intent);
            ServiceDemandeReassortActivity.this.finish();
        });
    }

    @NonNull
    private Intent getListeReassortServiceIntent(PH_Preparation phPreparationCourante, PH_Reassort PH_ReassortSelectionne) {
        Bundle listeReassortService_Bundle = ServiceDemandeReassortActivity.super.getBundle();
        listeReassortService_Bundle.putInt("depotSelectionneID", phPreparationCourante.getDepotDestinataireID());
        listeReassortService_Bundle.putInt("PH_ReassortSelectionneID", PH_ReassortSelectionne.getPhiMR4UUID());
        listeReassortService_Bundle.putInt("phPreparationID", phPreparationCourante.getUID());

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
                                    Alerte.afficherAlerte(ServiceDemandeReassortActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    ServiceDemandeReassortActivity.this.finishAffinity();
                                    Intent intent = new Intent(ServiceDemandeReassortActivity.this, AuthentificationActivity.class);
                                    ServiceDemandeReassortActivity.this.startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(ServiceDemandeReassortActivity.this, NavigationActivity.class);
                                    Bundle extras = new Bundle();
                                    extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                                    intent.putExtras(extras);
                                    ServiceDemandeReassortActivity.this.startActivity(intent);
                                    ServiceDemandeReassortActivity.this.finish();
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

    private PH_Preparation CreationPhPreparation(PH_Reassort reassort)
    {
        Random phPreparationRandom = new Random();
        int phPreparationID = phPreparationRandom.nextInt();
        if (phPreparationID > 0) {
            phPreparationID = phPreparationID * -1;
        }

        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
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
        String Liste = "Réassort de service : " + reassort.getListe();
        Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, reassort.getDepot_Reference());
        int depotDestinataireID = depotDestinataire.getDepot_UID();
        String depotDestinataireReference = reassort.getDepot_Reference();
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
        String[] dateTab = EVENTOpenHelper.getDateProchaineLivraison(db, depotDestinataire.getDepot_UID()).split("/");
        String LivraisonPrevueDate = getLivraisonPrevueDate(dateTab);
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
        Boolean URGENT = false;
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
        int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

        List<PH_Reassort_Ligne> listReassortLigne = PH_Reassort_LigneOpenHelper.getAllPH_Reassort_LigneParPH_Reassort(db, reassort);

        for(PH_Reassort_Ligne reassort_ligne : listReassortLigne)
        {
            CreatePhPreparationLigneDetail(reassort_ligne, reassort);
        }

        return ph_preparation;
    }

    @NonNull
    private static String getLivraisonPrevueDate(String[] dateTab) {
        String LivraisonPrevueDate;
        if(dateTab.length > 1)
        {
            LivraisonPrevueDate = dateTab[dateTab.length-1]+"-"+ dateTab[1]+"-"+ dateTab[0];
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            LivraisonPrevueDate = dateFormat.format(tomorrow);
        }
        return LivraisonPrevueDate;
    }

    public void CreatePhPreparationLigneDetail(PH_Reassort_Ligne reassort_ligne, PH_Reassort reassort)
    {
        Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, reassort.getDepot_Reference());
        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, depotDestinataire.getDepot_UID());
        if(Objects.equals(dateProchaineLivraison, ""))
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            dateProchaineLivraison = dateFormat.format(tomorrow);
        }
        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(db, "Réassort de service : " + reassort.getListe(), dateProchaineLivraison);
        PH_Preparation_Ligne ph_preparation_ligneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, phPreparationCourante, reassort_ligne.getProduit_ID());

        if(ph_preparation_ligneCourant == null)
        {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, reassort_ligne.getProduit_ID());

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
                String produitReference = produitCorrespondant.getRef_fourni();
                String produitCategorie = produitCorrespondant.getCategorie();
                double produitCondDistrib = produitCorrespondant.getCond_distrib();
                Boolean Suivi_Par_Lot = produitCorrespondant.isSuivi_Lot();
                int Qte_APreparer = 0;
                int Qte_livrer = 0;
                Boolean Livrer = false;
                Boolean Valider = false;
                String ValidationDate = "";
                String ZoneDepot = "";
                int Qte_RAL = 0;
                String SYS_DT_MAJ = "";
                String SYS_HEURE_MAJ = "";
                String SYS_USER_MAJ = "";
                double produitPUHT = 0;
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
                int Qte_besoin = reassort_ligne.getQuantite();
                int Qte_StockSaisie = -1;
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

    private void gestionAdapter()
    {
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
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDemandeReassortEnInstance(db, "Réassort de service : " + reassortCourant.getListe(), dateProchaineLivraison);

            if(phPreparationCourante == null)
            {
                phPreparationCourante = CreationPhPreparation(reassortCourant);
            }

            phPreparationList.add(phPreparationCourante);
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

        if (reassortListe.isEmpty()) {
            vide = true;
            nomServiceVide = "PH_Reassort";
            ServiceDemandeReassortActivity.this.finish();
        } else {
            reassortListe.size();
        }

        invalidateOptionsMenu();
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
    }

}
