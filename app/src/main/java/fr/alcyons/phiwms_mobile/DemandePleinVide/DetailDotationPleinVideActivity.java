package fr.alcyons.phiwms_mobile.DemandePleinVide;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationPleinVideActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Demande_PleinVide;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.ElementASynchroniser;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DetailPleinVideAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ReceptionPAD.DetailReceptionPadActivity;
import fr.alcyons.phiwms_mobile.ReceptionPAD.ServiceReceptionPadActivity;
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity;
import fr.alcyons.phiwms_mobile.RetourPUI.ServiceRetourPUIActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 31/01/2018.
 */

public class DetailDotationPleinVideActivity extends ServiceAvecConnexionActivity {
    Depot depot;
    Dotation dotation;
    List<Detail_Dot> detailDotList;
    Intent DetailDotationPleinVideIntent;
    List<Demande_PleinVide> demandePleinVideList = new ArrayList<>();
    DetailPleinVideAdapter detailPleinVideAdapter;
    ListView demandePleinVideListView;
    String datePrevisionDu;
    String datePrevisionAu;
    String dateLivraisonProchaine;
    String dateLivraisonSuivante;
    PackageManager pm;

    ImageView removeSearch;
    EditText searchBar;
    LinearLayout layoutValider;

    LinearLayout layoutTextAEnvoyer;
    LinearLayout layoutBoutonEnvoyer;
    PH_Preparation preparationCourante;
    List<PH_Preparation_Ligne> preparation_ligneList;
    boolean quantiteDotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dotation_pleinvide);
        // Récupération du dépot en fonction de la variable globale
        depot = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));
        dotation = DotationOpenHelper.getDotationPleinByStringId(db, String.valueOf(intent.getExtras().getInt("Dotation_Selection_PhiMR4UUID")));
        demandePleinVideList = (List<Demande_PleinVide>) intent.getExtras().getSerializable("DemandePleinVide");
        preparationCourante = PH_PreparationOpenHelper.getPH_PreparationByID(db, intent.getExtras().getInt("PreparationID"));
        preparation_ligneList = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationOrderDesignation(db, preparationCourante);
        quantiteDotation = intent.getExtras().getBoolean("quantiteDotation");

        int nbACommander = 0;
        for(PH_Preparation_Ligne preparation_ligne : preparation_ligneList)
        {
            if(preparation_ligne.getQte_APreparer() > 0)
            {
                nbACommander ++;
            }
        }

        if(preparationCourante.getStatut().contentEquals("En cours de préparation"))
        {
            ((LinearLayout) findViewById(R.id.layoutBoutonEnvoyer)).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(nbACommander));

        demandePleinVideListView = (ListView) findViewById(R.id.listeView);
        layoutValider = (LinearLayout) findViewById(R.id.layoutValider);
        layoutTextAEnvoyer = (LinearLayout) findViewById(R.id.layoutTextAEnvoyer);
        searchBar = (EditText)findViewById(R.id.searchBarPleinVide);
        removeSearch = (ImageView) findViewById(R.id.removeSearch);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        layoutTextAEnvoyer.startAnimation(anim);
        layoutValider.startAnimation(anim);
        searchBar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String recherche = String.valueOf(s).toLowerCase();
                if(s.length() > 0)
                {
                    removeSearch.setVisibility(View.VISIBLE);
                    List<PH_Preparation_Ligne> rechercheList = new ArrayList<>();
                    for(int i = 0; i < detailPleinVideAdapter.ph_preparation_ligneListOriginal.size(); i ++)
                    {
                        PH_Preparation_Ligne phPreparationLigne = (PH_Preparation_Ligne) detailPleinVideAdapter.ph_preparation_ligneListOriginal.get(i);

                        if(phPreparationLigne.getProduitDesignation().toLowerCase().contains(recherche))
                        {
                            rechercheList.add(detailPleinVideAdapter.ph_preparation_ligneListOriginal.get(i));
                        }
                    }
                    detailPleinVideAdapter.ph_preparation_ligneList.clear();
                    for (PH_Preparation_Ligne ph_preparation_ligne : rechercheList) {
                        detailPleinVideAdapter.ph_preparation_ligneList.add(ph_preparation_ligne);
                    }
                }
                else
                {
                    removeSearch.setVisibility(View.GONE);
                    detailPleinVideAdapter.ph_preparation_ligneList.clear();
                    detailPleinVideAdapter.ph_preparation_ligneList.addAll(detailPleinVideAdapter.ph_preparation_ligneListOriginal);
                }
                detailPleinVideAdapter.notifyDataSetChanged();
            }
        });

        removeSearch.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            searchBar.setText("");
            removeSearch.setVisibility(View.GONE);
            detailPleinVideAdapter.ph_preparation_ligneList.clear();
            detailPleinVideAdapter.ph_preparation_ligneList.addAll(detailPleinVideAdapter.ph_preparation_ligneListOriginal);
            detailPleinVideAdapter.notifyDataSetChanged();
        });

        layoutValider.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClick_Action_envoyerDemande();
            }
        });
        //gestion du packageManager
        pm = DetailDotationPleinVideActivity.this.getPackageManager();

        //affichage nom liste choisi
        String[] tabIntitule = dotation.getIntitule().split("- PLEINVIDE");
        ((TextView) findViewById(R.id.nomListe)).setText(tabIntitule[0]);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Affichage du nombre d'inventaire_ligne
        detailDotList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion  && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(DetailDotationPleinVideActivity.this, LayoutInflater.from(DetailDotationPleinVideActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DetailDotationPleinVideActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteEvent + depot.getDepot_UID();

            // Takes the response from the JSON request
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String string = response.getString("erreur");
                                if (string.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(DetailDotationPleinVideActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                    DBOpenHelper.viderBasesDeDonnees(db);
                                    DetailDotationPleinVideActivity.this.finishAffinity();
                                    Intent intent = new Intent(DetailDotationPleinVideActivity.this, AuthentificationActivity.class);
                                    DetailDotationPleinVideActivity.this.startActivity(intent);
                                } else {
                                    Alerte.afficherAlerte(DetailDotationPleinVideActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur requete : Demande Particulière récupération stock))", "alerte");
                                }
                            } else {
                                // Date de prochaine livraison
                                JSONObject eventJSON = response.getJSONObject("EVENTS");
                                dateLivraisonProchaine = eventJSON.getString("prochaine_livraison");
                                dateLivraisonSuivante = eventJSON.getString("livraison_suivante");

                                if(!dateLivraisonProchaine.contentEquals(""))
                                {
                                    String dateProchaineLivraison = dateLivraisonProchaine;
                                    Date date = null;
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        date = format.parse(dateProchaineLivraison);
                                        Calendar calendarCourant = Calendar.getInstance();
                                        calendarCourant.setTime(date);
                                        calendarCourant.add(Calendar.DAY_OF_WEEK, - 2);
                                        int numeroJour = calendarCourant.get(Calendar.DAY_OF_WEEK);
                                        if(numeroJour == Calendar.SUNDAY)
                                        {
                                            calendarCourant.add(Calendar.DAY_OF_WEEK, -2);
                                        }
                                        else if(numeroJour == Calendar.SATURDAY)
                                        {
                                            calendarCourant.add(Calendar.DAY_OF_WEEK, -1);
                                        }

                                        date = calendarCourant.getTime();
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }

                                    String dayOfTheWeek = "";
                                    String day          = "";
                                    String monthString  = "";
                                    String monthNumber  = "";
                                    String year         = "";

                                    if(date != null)
                                    {
                                        dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date); // Thursday
                                        day          = (String) android.text.format.DateFormat.format("dd",   date); // 20
                                        monthString  = (String) android.text.format.DateFormat.format("MMM",  date); // Jun
                                        year         = (String) android.text.format.DateFormat.format("yyyy", date); // 2013
                                    }
                                    ((TextView) findViewById(R.id.dateEnvoiListe)).setText(" avant "+dayOfTheWeek.substring(0,3).toUpperCase()+" "+day+" "+monthString.toUpperCase()+" "+year);
                                }

                                // Récupération du " Calendar " du téléphone
                                Calendar calendar = Calendar.getInstance();

                                // Initialisation dateInventaire à la date du jour
                                String format = "yyyy-MM-dd"; //In which you need put here
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
                                datePrevisionDu = simpleDateFormat.format(calendar.getTime());
                                datePrevisionAu = dateLivraisonSuivante;
                            }
                            gestionAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(DetailDotationPleinVideActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Récupération date livraison)", "alerte");
                    }
            ) {
                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
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
            gestionAdapter();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_croix, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuQuitter = menu.findItem(R.id.quitteMenu);
        menuQuitter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent detailPleinVideIntent = new Intent(DetailDotationPleinVideActivity.this, ServiceDemandePleinVideActivity.class);
                Bundle detailPleinVideBundle = DetailDotationPleinVideActivity.this.getBundle();
                detailPleinVideIntent.putExtras(detailPleinVideBundle);
                DetailDotationPleinVideActivity.this.startActivity(detailPleinVideIntent);
                DetailDotationPleinVideActivity.this.finish();
                return true;
            }
        });

        //super.prepareOptionsMenu(menu, demandePleinVideAdapter, null, "Désignation produit...");

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent detailPleinVideIntent = new Intent(DetailDotationPleinVideActivity.this, ServiceDemandePleinVideActivity.class);
        Bundle detailPleinVideBundle = super.getBundle();
        detailPleinVideIntent.putExtras(detailPleinVideBundle);
        DetailDotationPleinVideActivity.this.startActivity(detailPleinVideIntent);
        DetailDotationPleinVideActivity.this.finish();
    }

    public void onClick_Action_envoyerDemande() {
        boolean valide = false;
        for(PH_Preparation_Ligne preparation_ligne : detailPleinVideAdapter.ph_preparation_ligneList)
        {
            if(preparation_ligne.getQte_APreparer() > 0)
            {
                valide = true;
                break;
            }
        }
        if (valide) {
            afficherAlerteConfirmation(DetailDotationPleinVideActivity.this, DetailDotationPleinVideActivity.this.getLayoutInflater());
        } else {
            Alerte.afficherAlerte(DetailDotationPleinVideActivity.this, "Alerte", "Veuillez scanner au moins une référence.", "alerte");
        }
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_commentaire_plein_vide, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        final EditText commentaireEdit = (EditText) layout.findViewById(R.id.commentaire);
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String commentaire = commentaireEdit.getText().toString();
                if(commentaire == null)
                    commentaire = "";
                enregistrerDemande(commentaire);
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void enregistrerDemande(String commentaire)
    {
        int compteurReussite = 0;
        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
        PH_Preparation ph_preparation = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotation.get_UID()) + ":" + dotation.getIntitule(), dateProchaineLivraison);
        ph_preparation.setStatut("En cours de régularisation");
        ph_preparation.setCommentaires(commentaire);
        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation);
        //Création de l'action utilisateur
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation.getUID(), "", "Demande Plein Vide");

        List<PH_Preparation_Ligne> listePhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);

        for(PH_Preparation_Ligne ph_preparation_ligne : listePhPreparationLigne)
        {
            long rowId = ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

            compteurReussite ++;
            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparation_ligne.get_UID(), "", 0, (int)ph_preparation_ligne.getQte_APreparer(), ph_preparation_ligne.getProduitDesignation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
        }

        // Ajout du PH_Preparation au ElementASynchroniser
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation.getPhiMR4UUID(), ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        // Tentative de lancer la sychronisation
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailDotationPleinVideActivity.this, db, utilisateurConnecte, false);
        }

        Toast.makeText(DetailDotationPleinVideActivity.this, "Enregistrement en cours", Toast.LENGTH_SHORT).show();
        Intent detailPleinVideIntent = new Intent(DetailDotationPleinVideActivity.this, ServiceDemandePleinVideActivity.class);
        Bundle detailPleinVideBundle = super.getBundle();
        detailPleinVideIntent.putExtras(detailPleinVideBundle);
        DetailDotationPleinVideActivity.this.startActivity(detailPleinVideIntent);
        DetailDotationPleinVideActivity.this.finish();
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteDetailPleinVide(Context context, LayoutInflater inflater, final PH_Preparation_Ligne preparation_ligne, final int compteur, final Detail_Dot detail_dot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerteconfirmationpleinvide, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView designationProduit = (TextView) layout.findViewById(R.id.designationProduit);
        TextView nbSuppression = (TextView) layout.findViewById(R.id.nbSuppression);
        TextView nbAjout = (TextView) layout.findViewById(R.id.nbAjout);
        ImageView quitteModale = (ImageView) layout.findViewById(R.id.quitteModale);

        Produit produit = ProduitOpenHelper.getProduitByID(db, detail_dot.getCode_produit());

        designationProduit.setText(detail_dot.getDesignation());

        if(quantiteDotation)
        {
            nbSuppression.setText(detail_dot.getQte() +" "+produit.getUnite());
            nbAjout.setText(detail_dot.getQte() +" "+produit.getUnite());
        }
        else
        {
            int quantite = detail_dot.getQte() - detail_dot.getStock_minimum();
            nbSuppression.setText(quantite +" "+produit.getUnite());
            nbAjout.setText(quantite +" "+produit.getUnite());
        }
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        quitteModale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quantiteDotation)
                {
                    preparation_ligne.setQte_APreparer(preparation_ligne.getQte_APreparer()+detail_dot.getQte());
                    preparation_ligne.setQte_Demander(preparation_ligne.getQte_Demander()+detail_dot.getQte());
                    preparation_ligne.setQte_RAL(preparation_ligne.getQte_RAL()+detail_dot.getQte());
                }
                else
                {
                    int quantite = detail_dot.getQte() - detail_dot.getStock_minimum();
                    preparation_ligne.setQte_APreparer(preparation_ligne.getQte_APreparer()+quantite);
                    preparation_ligne.setQte_Demander(preparation_ligne.getQte_Demander()+quantite);
                    preparation_ligne.setQte_RAL(preparation_ligne.getQte_RAL()+quantite);
                }
                alertDialog.dismiss();
                MAJPreparationLigne(preparation_ligne, compteur);
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preparation_ligne.getQte_APreparer() >= 0)
                {
                    if(quantiteDotation)
                    {
                        preparation_ligne.setQte_APreparer(preparation_ligne.getQte_APreparer()-detail_dot.getQte());
                        preparation_ligne.setQte_Demander(preparation_ligne.getQte_Demander()-detail_dot.getQte());
                        preparation_ligne.setQte_RAL(preparation_ligne.getQte_RAL()-detail_dot.getQte());
                    }
                    else
                    {
                        int quantite = detail_dot.getQte() - detail_dot.getStock_minimum();
                        preparation_ligne.setQte_APreparer(preparation_ligne.getQte_APreparer()-quantite);
                        preparation_ligne.setQte_Demander(preparation_ligne.getQte_Demander()-quantite);
                        preparation_ligne.setQte_RAL(preparation_ligne.getQte_RAL()-quantite);
                    }
                }

                alertDialog.dismiss();
                MAJPreparationLigne(preparation_ligne, compteur);
            }
        });
    }

    public void MAJPreparationLigne(PH_Preparation_Ligne preparation_ligne, int compteur)
    {
        if(preparation_ligne.getQte_APreparer() == 0)
            compteur --;
        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne.getPhiMR4UUID(), preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        ElementASynchroniserOpenHelper.toutSynchroniser(DetailDotationPleinVideActivity.this, db, utilisateurConnecte, false);

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(compteur));
        detailPleinVideAdapter.notifyDataSetChanged();

        if(!searchBar.getText().toString().contentEquals(""))
            removeSearch.performClick();
    }

    private void gestionAdapter()
    {
        detailDotList = Detail_DotOpenHelper.getAllDetailDotParDotation(db, dotation);
        detailPleinVideAdapter = new DetailPleinVideAdapter(DetailDotationPleinVideActivity.this, db, preparation_ligneList, dotation.get_UID());
        demandePleinVideListView.setAdapter(detailPleinVideAdapter);
        demandePleinVideListView.setDivider(footer);

        demandePleinVideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!preparationCourante.getStatut().contentEquals("En cours de préparation"))
                {
                    int compteur = Integer.parseInt(String.valueOf(((TextView) findViewById(R.id.nbElementInAdapter)).getText()));
                    PH_Preparation_Ligne preparation_ligne = detailPleinVideAdapter.ph_preparation_ligneList.get(position);

                    if(preparation_ligne.getQte_APreparer() == 0)
                    {
                        Detail_Dot detail_dot = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, preparation_ligne.getProduitID(), dotation.get_UID());

                        if(quantiteDotation)
                        {
                            preparation_ligne.setQte_APreparer(detail_dot.getQte());
                            preparation_ligne.setQte_Demander(detail_dot.getQte());
                            preparation_ligne.setQte_RAL(detail_dot.getQte());
                        }
                        else
                        {
                            int quantite = detail_dot.getQte() - detail_dot.getStock_minimum();
                            preparation_ligne.setQte_APreparer(quantite);
                            preparation_ligne.setQte_Demander(quantite);
                            preparation_ligne.setQte_RAL(quantite);
                            preparation_ligne.setQte_StockSaisie(quantite);
                        }

                        compteur ++;
                        MAJPreparationLigne(preparation_ligne, compteur);
                    }
                    else
                    {
                        Detail_Dot detail_dot = Detail_DotOpenHelper.getDetailDotByProduitAndDotation(db, preparation_ligne.getProduitID(), dotation.get_UID());
                        afficherAlerteDetailPleinVide(DetailDotationPleinVideActivity.this, DetailDotationPleinVideActivity.this.getLayoutInflater(), preparation_ligne, compteur, detail_dot);
                    }
                }
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);

    }
}