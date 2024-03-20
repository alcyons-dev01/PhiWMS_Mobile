package fr.alcyons.phimr4.DemandeReassort;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Reassort_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.PH_Reassort;
import fr.alcyons.phimr4.Classes.PH_Reassort_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_PUIAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

import static fr.alcyons.phimr4.AuthentificationActivity.hasPermissions;

/**
 * Created by olivier on 03/01/2018.
 */

public class InformationDemandeReassortActivity extends ServiceAvecConnexionActivity {

    Depot depot;
    PH_Reassort ph_reassort;

    List<Demande_PUI> demandePuiList;
    List<PH_Reassort_Ligne> phReassortLigneList;

    Calendar calendar;

    MenuItem action_demandeUrgenteMenuItem;

    TextView datePrevisionDuTextView;
    TextView datePrevisionAuTextView;
    TextView dateLivraisonProchaineTextView;
    TextView dateLivraisonSuivanteTextView;

    ListView phReassortLigneListView;
    Demande_PUIAdapter demandePuiAdapter;

    DatePickerDialog.OnDateSetListener datePrevisionDuDatePicker;
    DatePickerDialog.OnDateSetListener datePrevisionAuDatePicker;

    String commentaire;
    String depotAdresse;

    String dateInventaireString;
    String datePrevisionDuString;
    String datePrevisionAuString;
    String dateLivraisonProchaineString;
    String dateLivraisonSuivanteString;

    String datePrevisionDuMysqlString;
    String datePrevisionAuMysqlString;
    String dateLivraisonProchaineMysqlString;
    String dateLivraisonSuivanteMysqlString;

    int depotId;

    boolean demandeUrgente = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_demande_reassort);

        // Récupération des intents
        depotId = intent.getIntExtra("depotSelectionneID", 0);
        int PH_ReassortSelectionneID = intent.getIntExtra("PH_ReassortSelectionneID", 0);
        dateInventaireString = intent.getStringExtra("dateInventaire");
        dateLivraisonProchaineString = intent.getStringExtra("dateLivraisonProchaine");
        dateLivraisonSuivanteString = intent.getStringExtra("dateLivraisonSuivante");

        // Récupération des ressources
        depot = gestionnaireDepot.getDepotParID(db, depotId);
        ph_reassort = gestionnairePH_Reassort.getPH_ReassortByPhiMR4UUID(db, PH_ReassortSelectionneID);

        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        String format = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);

        // Si dans l'écran précédent initialisation de la date d'inventaire sinon date du jour
        if (dateInventaireString.contentEquals("00/00/0000")) {
            dateInventaireString = simpleDateFormat.format(calendar.getTime());
        }

        datePrevisionDuString = dateInventaireString;
        datePrevisionAuString = dateLivraisonSuivanteString;

        datePrevisionDuMysqlString = updateDateFormatMysql(datePrevisionDuString);
        datePrevisionAuMysqlString = updateDateFormatMysql(datePrevisionAuString);
        dateLivraisonProchaineMysqlString = updateDateFormatMysql(dateLivraisonProchaineString);
        dateLivraisonSuivanteMysqlString = updateDateFormatMysql(dateLivraisonSuivanteString);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.nomDemande)).setText(String.valueOf(ph_reassort.getListe()));
        // Récupération de la listView et initialisation de la liste contenant les Demande PUI
        phReassortLigneListView = ((ListView) findViewById(R.id.listeView));
    }

    @Override
    public void onResume() {
        super.onResume();

        phReassortLigneList = new ArrayList<>();

        if (OutilsGestionConnexionReseau.isServerAccessible(InformationDemandeReassortActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(InformationDemandeReassortActivity.this, "Veuillez patienter", "Synchronisation des lignes de reassorts en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(InformationDemandeReassortActivity.this);
            String urlRequete = null;
            try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePH_Reassort_Ligne + URLEncoder.encode(depot.getDepot_Reference(), "utf-8") + "/" + ph_reassort.getCode();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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
                                        Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        InformationDemandeReassortActivity.this.finishAffinity();
                                        Intent intent = new Intent(InformationDemandeReassortActivity.this, AuthentificationActivity.class);
                                        InformationDemandeReassortActivity.this.startActivity(intent);
                                    } else {
                                        Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Alerte", "Aucun Reassort de service trouvé", "alerte");
                                    }
                                } else {

                                    JSONArray PH_Reassort_LignesJson = response.getJSONArray("PH_Reassort_Ligne");
                                    for (int k = 0; k < PH_Reassort_LignesJson.length(); k++) {
                                        JSONObject PH_Reassort_LigneJson = PH_Reassort_LignesJson.getJSONObject(k);
                                        PH_Reassort_Ligne phReassortLigne = new PH_Reassort_Ligne(PH_Reassort_LigneJson);
                                        long rowID = gestionnairePH_Reassort_Ligne.insererPH_Reassort_LigneEnBDD(db, phReassortLigne);
                                        if (rowID != -1) {
                                            JSONArray ph_stock_destinataire = PH_Reassort_LigneJson.getJSONArray("ph_stock_destinataire");
                                            JSONArray ph_stock_pui = PH_Reassort_LigneJson.getJSONArray("ph_stock_pui");

                                            for (int y = 0; y < ph_stock_destinataire.length(); y++) {
                                                Stock stock = new Stock(ph_stock_destinataire.getJSONObject(y));
                                                rowID = gestionnaireStock.insererUnStockEnBDD(db, stock);
                                            }

                                            for (int y = 0; y < ph_stock_pui.length(); y++) {
                                                Stock stock = new Stock(ph_stock_pui.getJSONObject(y));
                                                rowID = gestionnaireStock.insererUnStockEnBDD(db, stock);
                                            }
                                        }
                                        phReassortLigneList.add(phReassortLigne);
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
                            Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Demande Réassort)", "alerte");
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
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
        } else {

            phReassortLigneList = PH_Reassort_LigneOpenHelper.getAllPH_Reassort_LigneParPH_Reassort(db, ph_reassort);
            if (phReassortLigneList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        demandePuiList = new ArrayList<>();

        // Récupération des ph_reassort_ligne du PH_Reassort
        for (PH_Reassort_Ligne ph_reassort_ligne : phReassortLigneList) {

            Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reassort_ligne.getProduit_ID());
            Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
            Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, ph_reassort.getDepot_Reference());

            Stock stockPUI = StockOpenHelper.getStockByProduitEtDepot(db, produit, depotPUI);
            Stock stockDestinataire = StockOpenHelper.getStockByProduitEtDepot(db, produit, depotDestinataire);

            double qteStockPui = 0;
            double qteStockDestinataire = 0;

            if (stockPUI != null)
                qteStockPui = stockPUI.getQuantite_Actuelle();
            if (stockDestinataire != null)
                qteStockDestinataire = stockDestinataire.getQuantite_Actuelle();

            Demande_PUI demande_pui = new Demande_PUI(ph_reassort_ligne, produit, qteStockPui, qteStockDestinataire);

            demandePuiList.add(demande_pui);
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        demandePuiAdapter = new Demande_PUIAdapter(InformationDemandeReassortActivity.this, demandePuiList);

        phReassortLigneListView.setAdapter(demandePuiAdapter);
        phReassortLigneListView.setItemsCanFocus(true);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_demande_action, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem action_informationsMenuItem = menu.findItem(R.id.action_informations);
        action_informationsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_informations();
                return true;
            }
        });

        MenuItem action_prochaineLivraisonMenuItem = menu.findItem(R.id.action_prochaineLivraison);
        action_prochaineLivraisonMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_prochaineLivraison();
                return true;
            }
        });

        action_demandeUrgenteMenuItem = menu.findItem(R.id.action_demandeUrgente);
        action_demandeUrgenteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_demandeUrgente();
                return true;
            }
        });

        MenuItem action_commentaireMenuItem = menu.findItem(R.id.action_commentaire);
        action_commentaireMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_commentaire();
                return true;
            }
        });

        MenuItem action_envoyerDemandeMenuItem = menu.findItem(R.id.action_envoyerDemande);
        action_envoyerDemandeMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_Action_envoyerDemande();
                return true;
            }
        });

        super.prepareOptionsMenu(menu, demandePuiAdapter, null, "Désignation, Catégorie...");

        return true;
    }

    public void onClick_Action_informations() {
        String depotNom = depot.getNom();
        String depotTelephone = depot.getTel();
        String depotFax = depot.getFax();

        // Récupération de l'adresse du dépot, attention dans le cas d'un PAD vérification de l'utilisation ou non de son adresse de vacance
        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            depotAdresse = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) {
                depotAdresse += depot.getPAD_Vacances_Adr2() + ", ";
            }
            depotAdresse += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            depotAdresse = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) {
                depotAdresse += depot.getAdresse2() + ", ";
            }
            depotAdresse += depot.getCP() + " " + depot.getVille();
        }

        Alerte.afficherAlerteInfoDepot(InformationDemandeReassortActivity.this, InformationDemandeReassortActivity.this.getLayoutInflater(), depotNom, depotAdresse, depotTelephone, depotFax);
    }

    public void onClick_Action_prochaineLivraison() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_prochaine_livraison, null);

        datePrevisionDuTextView = (TextView) view.findViewById(R.id.datePrevisionDu);
        datePrevisionAuTextView = (TextView) view.findViewById(R.id.datePrevisionAu);
        dateLivraisonProchaineTextView = (TextView) view.findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonSuivanteTextView = (TextView) view.findViewById(R.id.dateLivraisonSuivante);

        /*  Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        datePrevisionDuTextView.setText(datePrevisionDuString);
        datePrevisionDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datePrevisionDuTextView);
                datePrevisionDuString = datePrevisionDuTextView.getText().toString();
                datePrevisionDuMysqlString = updateDateFormatMysql(datePrevisionDuString);
            }

        };

        datePrevisionDuTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MAX à celle de datePrevisionAu
                String dateMaxString = datePrevisionAuTextView.getText().toString();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePrevisionDuPickerDialog = new DatePickerDialog(InformationDemandeReassortActivity.this, datePrevisionDuDatePicker, year, month, day);

                if (!dateMaxString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMax = new Date();
                    try {
                        dateMax = dateFormat.parse(dateMaxString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    datePrevisionDuPickerDialog.getDatePicker().setMaxDate(dateMax.getTime());
                }

                datePrevisionDuPickerDialog.show();

            }
        });

        datePrevisionAuTextView.setText(datePrevisionAuString);
        datePrevisionAuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(datePrevisionAuTextView);
                datePrevisionAuString = datePrevisionAuTextView.getText().toString();
                datePrevisionAuMysqlString = updateDateFormatMysql(datePrevisionAuString);
            }

        };

        datePrevisionAuTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MIN à la date du jour par défaut sinon à celle de dateLivraisonProchaine
                String dateMinString = dateLivraisonProchaineTextView.getText().toString();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePrevisionAuPickerDialog = new DatePickerDialog(InformationDemandeReassortActivity.this, datePrevisionAuDatePicker, year, month, day);
                datePrevisionAuPickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());

                if (!dateMinString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMin = new Date();
                    try {
                        dateMin = dateFormat.parse(dateMinString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    year = dateMin.getYear();
                    month = dateMin.getMonth();
                    day = dateMin.getDay();

                    // Create a new instance of DatePickerDialog and return it
                    datePrevisionAuPickerDialog = new DatePickerDialog(InformationDemandeReassortActivity.this, datePrevisionAuDatePicker, year, month, day);
                    datePrevisionAuPickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                datePrevisionAuPickerDialog.show();
            }
        });

        dateLivraisonProchaineTextView.setText(dateLivraisonProchaineString);
        dateLivraisonSuivanteTextView.setText(dateLivraisonSuivanteString);

        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void onClick_Action_demandeUrgente() {
        if (demandeUrgente) {
            action_demandeUrgenteMenuItem.setTitle("Demande Urgente");
            findViewById(R.id.isUrgent).setVisibility(View.GONE);
        } else {
            action_demandeUrgenteMenuItem.setTitle("Demande Non Urgente");
            findViewById(R.id.isUrgent).setVisibility(View.VISIBLE);
        }
        demandeUrgente = !demandeUrgente;
    }

    public void onClick_Action_commentaire() {
        commentaire = Alerte.afficherAlerteInfoCommantaire(this, commentaire);
    }

    public void onClick_Action_envoyerDemande() {
        boolean confirmer = Alerte.afficherAlerte(this, "Envoyer", "Êtes-vous sûr de vouloir envoyer cette demande ?", "OuiNon");
        if (confirmer) {
            int compteurReussite = 0;

            // Récupération du dépot PUI
            Depot depotPUI = DepotOpenHelper.getDepotPUI(db);

            Random phPreparationRandom = new Random();
            int phPreparationID = phPreparationRandom.nextInt();
            if (phPreparationID > 0) {
                phPreparationID = phPreparationID * -1;
            }

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
            String Liste = "Réassort de service : " + ph_reassort.getListe();
            int depotDestinataireID = depot.getDepot_UID();
            String depotDestinataireReference = depot.getDepot_Reference();
            String SYS_DT_MAJ = "";
            String SYS_HEURE_MAJ = "";
            String SYS_USER_MAJ = "";
            String PrescripteurReference = "";
            String Prescription_date = "";
            String PrescripteurNom = "";
            String depotOrigineReference = depotPUI.getDepot_Reference();
            int depotOrigineID = depotPUI.getDepot_UID();
            String Commentaires = commentaire;
            String PreparationDate = "";
            String LivraisonPrevueDate = dateLivraisonProchaineMysqlString;
            String DN_Groupe = "";
            double Montant_HT = 0;
            double Montant_TTC = 0;
            double Poids = 0;
            int Commande_ID = 0;
            String Preparateur = "";
            String Statut = "En attente";
            String PHIE_SYNCHRO = "";
            String receptionUFNonComforme = "";
            String livraisonDate = "";
            String Frequence = "";
            String previsionDateDebut = datePrevisionDuMysqlString;
            String previsionDateFin = datePrevisionAuMysqlString;
            Boolean URGENT = demandeUrgente;
            String Motif = "";
            int preparateur_userID = 0;
            int pharmacien_userID = 0;
            double Volume = 0;
            int PaletteNB = 0;
            int CaisseNB = 0;
            int Conteneur_NB = 0;
            String numero_scelle = "";

            // Création et insertion en base du PH_Preparation
            PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, Commentaires, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, Motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB, CaisseNB, Conteneur_NB, numero_scelle);
            int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

            int phPreparationLigneID = ph_preparation.getUID() + 1;

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation.getUID(), "", "Demande de réassort");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur


            // Parcours des demande_pui afin d'en faire des PH_Preparation_Ligne
            for (Demande_PUI demande_pui : demandePuiAdapter.demandePuiList) {

                phPreparationLigneID++;
                // Initialisation des données permettant de créer un PH_Préparation_Ligne
                int PreparationID = ph_preparation.getUID();
                int _UID = phPreparationLigneID;
                produitID = demande_pui.getCode_produit();
                produitDesignation = demande_pui.getDésignation();
                int Qte_APreparer = demande_pui.getA_preparer();
                int Qte_livrer = 0;
                Boolean Livrer = false;
                Boolean Valider = false;
                String ValidationDate = "";
                String produitReference = demande_pui.getRéférence();
                String ZoneDepot = "";
                String produitCategorie = demande_pui.getCatégorie();
                int Qte_RAL = Qte_APreparer;
                SYS_DT_MAJ = "";
                SYS_HEURE_MAJ = "";
                SYS_USER_MAJ = "";
                double produitCondDistrib = demande_pui.getConditionnement();
                double produitPUHT = 0;
                Boolean Suivi_Par_Lot = false;
                int patientID = 0;
                String PatientNom = "";
                PrescripteurNom = "";
                String prescripteurReference = "";
                int Ordre_Impression = 0;
                int Prescription_ID = 0;
                String LotNumero = "";
                String PeremptionDate = "";
                double produitPoids = 0;
                double produitTVA = 0;
                Montant_HT = 0;
                Montant_TTC = 0;
                double PoidsTotal = 0;
                String depot_Destinataire_Reference = depot.getDepot_Reference();
                String utilisation_Date_Prevue = "";
                int Qte_besoin = demande_pui.getBesoin();
                int Qte_StockSaisie = (int) demande_pui.getStock_destinataire();
                int Qte_Demander = Qte_APreparer;
                String EmplacementParDefaut = "";
                int Qte_preparer = Qte_APreparer;
                boolean accepter = true;

                // Création et insertion en base du PH_Preparation_Ligne
                PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter, ph_preparation.getUID());
                int ph_preparation_lignePHIMR4uid = (int) gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

                // Ajout du PH_Preparation_Ligne au ElementASynchroniser
                long rowId = gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_lignePHIMR4uid, ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                if (rowId != -1) {
                    compteurReussite++;
                    //gestion des actions lignes
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if(actionligneId > 0)
                        actionligneId= actionligneId*-1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparation_ligne.get_UID(), "", 0, (int)ph_preparation_ligne.getQte_APreparer(), ph_preparation_ligne.getProduitDesignation());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }
            }
            if (compteurReussite == demandePuiAdapter.demandePuiList.size()) {
                // Ajout du PH_Preparation au ElementASynchroniser
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

                // Tentative de lancer la sychronisation
                if (OutilsGestionConnexionReseau.isServerAccessible(InformationDemandeReassortActivity.this)) {
                    gestionnaireElementASynchroniser.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, true);
                }

                Toast.makeText(InformationDemandeReassortActivity.this, "Demande Réassort Service effectuée", Toast.LENGTH_SHORT).show();
                InformationDemandeReassortActivity.this.finish();
                return;
            } else {
                Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Alerte", "Une erreur est survenue", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                InformationDemandeReassortActivity.this.finish();
                return;
            }
        }
    }

    private String updateDateFormatMysql(String dateString) {
        Date date;
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateAAfficher = "";
        try {
            date = dateDecodeur.parse(dateString);
            dateAAfficher = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateAAfficher;
    }

    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(calendar.getTime()));
    }

    //clic sur l'adresse du dépôt ouvre Google Maps
    public void maps(View v) {
        String map = "https://www.google.com/maps/search/?api=1&query=" + depotAdresse;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(i);
    }

    //clic sur le numéro de téléphone ouvre le téléphone pour appeler le correspondant
    public void telephoner(View v) {
        // Demande des autorisations
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.CALL_PHONE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + depot.getTel()));
            startActivity(intent);
        }
    }
}
