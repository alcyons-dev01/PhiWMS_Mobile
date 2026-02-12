package fr.alcyons.phiwms_mobile.DemandeDotationGlobale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DotationGlobaleAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceDemandeDotationGlobaleActivity;

public class InformationDotationServiceActivity extends ServiceAvecConnexionActivity {
    Dotation dotation;
    Depot depot;
    List<Dotation> dotationList;
    List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();
    Calendar calendar;
    DotationGlobaleAdapter dotationGlobaleAdapter;
    ListView demandePuiListeView;
    TextView nomDemandeTextView;
    int dotationId;
    int depotID;
    boolean vide;
    int phpreparationid;
    PH_Preparation ph_preparation_courante;
    EditText searchBar;
    ImageView removeSearch;
    TextView nbElementCommander_TV;
    TextView nbElementTotal_TV;
    LinearLayout layoutBoutonEnvoyer;
    TextView dateEnvoiListe_TV;
    AlertDialog alertDialog;
    int nbDetailDotAttendu = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dotation_service);

        // Récupération des intents
        depotID = intent.getIntExtra("depotSelectionneID", 0);
        dotationId = intent.getIntExtra("dotationSelectionneID", 0);
        phpreparationid = intent.getIntExtra("phPreparationID", 0);

        // Récupération des ressources
        depot = DepotOpenHelper.getDepotParID(db, depotID);
        dotation = DotationOpenHelper.getDotationByphiwms_mobileUUID(db, dotationId);
        nbDetailDotAttendu = Detail_DotOpenHelper.getNbDetailDot(db, dotation);
        ph_preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, phpreparationid);

        if(ph_preparation_courante == null)
        {
            ph_preparation_courante = CreationPhPreparation(dotation);
        }

        //Initialisation des objets graphiques
        nomDemandeTextView = findViewById(R.id.nomDemande);
        demandePuiListeView = findViewById(R.id.listeView);
        searchBar = findViewById(R.id.searchBarDemande);
        removeSearch = findViewById(R.id.removeSearchDemande);
        nbElementCommander_TV = findViewById(R.id.nbElementCommander);
        nbElementTotal_TV = findViewById(R.id.nbElementTotal);
        dateEnvoiListe_TV = findViewById(R.id.dateEnvoiListe);
        layoutBoutonEnvoyer = findViewById(R.id.layoutBoutonEnvoyer);

        //initialisation du boolean permettant de dire si il n'y a aucune dotation
        vide = false;

        nomDemandeTextView.setText(dotation.getIntitule().trim());

        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

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
                    for(int i = 0; i < dotationGlobaleAdapter.phPreparationLigneOriginalList.size(); i ++)
                    {
                        PH_Preparation_Ligne phPreparationLigne = dotationGlobaleAdapter.phPreparationLigneOriginalList.get(i);

                        if(phPreparationLigne.getProduitDesignation().toLowerCase().contains(recherche))
                        {
                            rechercheList.add(dotationGlobaleAdapter.phPreparationLigneOriginalList.get(i));
                        }
                    }
                    dotationGlobaleAdapter.phPreparationLigneList.clear();
                    dotationGlobaleAdapter.phPreparationLigneList.addAll(rechercheList);
                }
                else
                {
                    removeSearch.setVisibility(View.GONE);
                    if(dotationGlobaleAdapter != null)
                    {
                        if(dotationGlobaleAdapter.phPreparationLigneOriginalList != null)
                        {
                            dotationGlobaleAdapter.phPreparationLigneList.clear();
                            dotationGlobaleAdapter.phPreparationLigneList.addAll(dotationGlobaleAdapter.phPreparationLigneOriginalList);
                        }
                    }
                }
                dotationGlobaleAdapter.notifyDataSetChanged();
            }
        });

        removeSearch.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            searchBar.setText("");
            removeSearch.setVisibility(View.GONE);
            dotationGlobaleAdapter.phPreparationLigneList.clear();
            dotationGlobaleAdapter.phPreparationLigneList.addAll(dotationGlobaleAdapter.phPreparationLigneOriginalList);
            dotationGlobaleAdapter.notifyDataSetChanged();
        });

        if(ph_preparation_courante.getStatut().contentEquals("En instance") || ph_preparation_courante.getStatut().contentEquals("En cours de régularisation"))
        {
            layoutBoutonEnvoyer.setOnClickListener(view -> {
                boolean complet = true;

                for(PH_Preparation_Ligne ph_preparation_ligne : phPreparationLigneList)
                {
                    if(ph_preparation_ligne.getQte_StockSaisie() == -1)
                    {
                        complet = false;
                        break;
                    }
                }

                if(complet)
                {
                    afficherAlerteConfirmation(InformationDotationServiceActivity.this, InformationDotationServiceActivity.this.getLayoutInflater());
                }
                else
                {
                    afficherAlerte(InformationDotationServiceActivity.this, InformationDotationServiceActivity.this.getLayoutInflater());
                }
            });
        }
        else
        {
            layoutBoutonEnvoyer.setVisibility(View.GONE);
        }

        String livraisonPrevueDate = ph_preparation_courante.getLivraisonPrevueDate();
        String[] tabDate = livraisonPrevueDate.split("-");
        livraisonPrevueDate = tabDate[tabDate.length-1]+"/"+tabDate[1]+"/"+tabDate[0];
        dateEnvoiListe_TV.setText(livraisonPrevueDate);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(InformationDotationServiceActivity.this, ServiceDemandeDotationGlobaleActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                extras.putInt("serviceSelectionneID", serviceActuel.getId());
                intent.putExtras(extras);
                InformationDotationServiceActivity.this.startActivity(intent);
                InformationDotationServiceActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dotationList = new ArrayList<>();

                /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing() && !checkSpinner()) {
                afficherSpinner(InformationDotationServiceActivity.this, LayoutInflater.from(InformationDotationServiceActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(InformationDotationServiceActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePh_Preparation_Lignes + "ph_preparation/" + ph_preparation_courante.getUID();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String string = response.getString("erreur");
                                if (string.equals(getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(InformationDotationServiceActivity.this, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                    //DBOpenHelper.viderBasesDeDonnees(db);
                                    InformationDotationServiceActivity.this.finishAffinity();
                                    Intent intent = new Intent(InformationDotationServiceActivity.this, AuthentificationActivity.class);
                                    InformationDotationServiceActivity.this.startActivity(intent);
                                } else {
                                    Alerte.afficherAlerte(InformationDotationServiceActivity.this, "Alerte", "Aucune Dotation trouvée", "alerte");
                                }
                            } else if(nbResultat != nbDetailDotAttendu) {
                                onResume();
                            }else {
                                PH_Preparation_LigneOpenHelper.viderTablePH_Preparation_LignesParPreparation(db, ph_preparation_courante.getUID());
                                JSONArray ph_preparationligneJson = response.getJSONArray("PH_Preparation_Lignes");
                                for (int k = 0; k < ph_preparationligneJson.length(); k++) {
                                    JSONObject preparationligneJsonJSONObject = ph_preparationligneJson.getJSONObject(k);
                                    PH_Preparation_Ligne preparation_ligne = new PH_Preparation_Ligne(preparationligneJsonJSONObject);
                                    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, preparation_ligne);
                                    phPreparationLigneList.add(preparation_ligne);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        handler.sendMessage(handler.obtainMessage());
                    },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(InformationDotationServiceActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Dotation Service)", "alerte");
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
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
        } else {
            dotationList = DotationOpenHelper.getDotationByDepot(db, depotID);
            if (dotationList.isEmpty()) {
                connexionNecessaire();
                return;
            }
        }

//        phPreparationLigneList.sort(Comparator.comparing(PH_Preparation_Ligne::getProduitCategorie));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        dotationGlobaleAdapter = new DotationGlobaleAdapter(InformationDotationServiceActivity.this, phPreparationLigneList, db, utilisateurConnecte, dotation);
        demandePuiListeView.setAdapter(dotationGlobaleAdapter);
        demandePuiListeView.setItemsCanFocus(true);
        demandePuiListeView.setOnItemClickListener((adapterView, view, i, l) -> {
            PH_Preparation_Ligne preparation_ligne = dotationGlobaleAdapter.phPreparationLigneList.get(i);
            afficherNumberPicker(InformationDotationServiceActivity.this, InformationDotationServiceActivity.this.getLayoutInflater(), preparation_ligne);
        });

        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_croix, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuQuitter = menu.findItem(R.id.quitteMenu);
        menuQuitter.setOnMenuItemClickListener(item -> {
            Intent detailDotationIntent = new Intent(InformationDotationServiceActivity.this, ServiceDemandeDotationGlobaleActivity.class);
            Bundle detailDotationBundle = InformationDotationServiceActivity.this.getBundle();
            detailDotationIntent.putExtras(detailDotationBundle);
            InformationDotationServiceActivity.this.startActivity(detailDotationIntent);
            InformationDotationServiceActivity.this.finish();
            return true;
        });
        return true;
    }
    @SuppressLint("SetTextI18n")
    private void afficherNumberPicker(Context context, LayoutInflater inflater, final PH_Preparation_Ligne ph_preparation_ligne)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View layout = inflater.inflate(R.layout.alerte_demander, null);
        final int conditionnementCourant = (int)ph_preparation_ligne.getProduitCondDistrib();
        TextView designation_tv = layout.findViewById(R.id.np_designation);
        TextView reference_tv = layout.findViewById(R.id.np_reference);
        final EditText stock_et = layout.findViewById(R.id.np_qteStock);
        final EditText demande_et = layout.findViewById(R.id.np_qteDemande);
        final TextView conditionnement_tv = layout.findViewById(R.id.np_conditionnement);
        TextView qte_besoin_tv = layout.findViewById(R.id.np_qteBesoin);
        LinearLayout valider_ll = layout.findViewById(R.id.np_valider);
        LinearLayout layoutdotation_ll = layout.findViewById(R.id.layoutdotation);
        ImageView quitterModale_iv = layout.findViewById(R.id.np_quitterModale);
        LinearLayout validerstock_ll = layout.findViewById(R.id.np_valider_stock);
        LinearLayout layoutStock_ll = layout.findViewById(R.id.layoutStock);

        designation_tv.setText(ph_preparation_ligne.getProduitDesignation());
        reference_tv.setText(ph_preparation_ligne.getProduitReference());
        conditionnement_tv.setText("(x"+ (int) ph_preparation_ligne.getProduitCondDistrib() +")");
        qte_besoin_tv.setText(String.valueOf(ph_preparation_ligne.getQte_besoin()));

        stock_et.setOnKeyListener((view, i, keyEvent) -> {
            if(i == 66)
            {
                validerstock_ll.performClick();
            }
            return false;
        });
        demande_et.setOnKeyListener((view, i, keyEvent) -> {
            if(i == 66)
            {
                valider_ll.performClick();
            }
            return false;
        });
        builder.setView(layout);

        quitterModale_iv.setOnClickListener(view -> alertDialog.dismiss());

        validerstock_ll.setOnClickListener(view -> {
            String stock_string = stock_et.getText().toString().trim();

            if(!stock_string.contentEquals("") && !dotation.isCommandeAB())
            {
                int valueStock = Integer.parseInt(stock_et.getText().toString());
                ph_preparation_ligne.setQte_StockSaisie(valueStock);
                if(ph_preparation_ligne.getQte_besoin() < valueStock) {
                    ph_preparation_ligne.setQte_APreparer(0);
                    ph_preparation_ligne.setQte_Demander(0);
                    demande_et.setText(String.valueOf(0));
                }
                else
                {
                    int qterestante = ph_preparation_ligne.getQte_besoin() - valueStock;
                    int qteSaisieApreparer = (int) ph_preparation_ligne.Conditionnement_Calcul(qterestante, conditionnementCourant);
                    ph_preparation_ligne.setQte_APreparer(qteSaisieApreparer);
                    ph_preparation_ligne.setQte_Demander(qteSaisieApreparer);
                    demande_et.setText(String.valueOf(qteSaisieApreparer));
                }

                int stockValue = 0;
                if(!stock_string.contentEquals(""))
                    stockValue = Integer.parseInt(stock_string);

                ph_preparation_ligne.setQte_StockSaisie(stockValue);
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);

                layoutdotation_ll.setVisibility(View.VISIBLE);
                validerstock_ll.setVisibility(View.GONE);
                demande_et.requestFocus();
            }
            else if(dotation.isCommandeAB())
            {
                layoutdotation_ll.setVisibility(View.VISIBLE);
                validerstock_ll.setVisibility(View.GONE);
                demande_et.requestFocus();
            }
        });

        valider_ll.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(valider_ll.getWindowToken(), 0);
            String stock_string = stock_et.getText().toString().trim();
            int stockValue = 0;
            if(!stock_string.contentEquals(""))
                stockValue = Integer.parseInt(stock_string);

            String demande_string = demande_et.getText().toString().trim();
            int demandeValue = 0;
            if(!demande_string.contentEquals(""))
                demandeValue = Integer.parseInt(demande_string);

            ph_preparation_ligne.setQte_StockSaisie(stockValue);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat heureFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();

            String dateDuJour = dateFormat.format(date);
            String heureCourante = heureFormat.format(date);
            ph_preparation_ligne.setSYS_DT_MAJ(dateDuJour);
            ph_preparation_ligne.setSYS_HEURE_MAJ(heureCourante);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
            ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);

            int total = stockValue + demandeValue;
            if(ph_preparation_ligne.getQte_besoin() > total)
            {
                afficherMessageAlerte(context, inflater, ph_preparation_ligne, demandeValue);
            }
            else
            {
                demandeValue = (int) ph_preparation_ligne.Conditionnement_Calcul(demandeValue, (int)ph_preparation_ligne.getProduitCondDistrib());

                ph_preparation_ligne.setQte_APreparer(demandeValue);
                ph_preparation_ligne.setQte_Demander(demandeValue);
                ph_preparation_ligne.setSYS_DT_MAJ(dateDuJour);
                ph_preparation_ligne.setSYS_HEURE_MAJ(heureCourante);
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);
                dotationGlobaleAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        if(ph_preparation_ligne.getQte_StockSaisie() == -1 && !dotation.isCommandeAB())
        {
            stock_et.setText("");
            stock_et.requestFocus();
            InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        else
        {
            stock_et.setText(String.valueOf(ph_preparation_ligne.getQte_StockSaisie()));
            demande_et.setText(String.valueOf(ph_preparation_ligne.getQte_Demander()));
            validerstock_ll.performClick();
        }

        if(dotation.isCommandeAB())
            layoutStock_ll.setVisibility(View.GONE);

        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void afficherMessageAlerte(Context context, LayoutInflater inflater, final PH_Preparation_Ligne ph_preparation_ligne, final int quantite)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);
        TextView text_tv = layout.findViewById(R.id.messageFin);
        LinearLayout valider_ll = layout.findViewById(R.id.buttonOk);
        LinearLayout annuler_ll = layout.findViewById(R.id.buttonAnnuler);

        int quantiteBesoin = ph_preparation_ligne.getQte_besoin() - ph_preparation_ligne.getQte_StockSaisie();
        text_tv.setText("La quantité demandée ("+quantite+") est inférieur à la quantité de besoin ("+quantiteBesoin+")");
        builder.setView(layout);
        final AlertDialog alertDialogErreur = builder.create();
        valider_ll.setOnClickListener(view -> {
            mettreAJourQuantiteDemande(ph_preparation_ligne, quantite);
            alertDialogErreur.dismiss();
            alertDialog.dismiss();
        });

        annuler_ll.setOnClickListener(view -> alertDialogErreur.dismiss());

        alertDialogErreur.setCanceledOnTouchOutside(false);
        alertDialogErreur.setCancelable(false);
        alertDialogErreur.show();
    }

    private void mettreAJourQuantiteDemande(PH_Preparation_Ligne ph_preparation_ligne, int quantite)
    {
        ph_preparation_ligne.setQte_Demander(quantite);
        ph_preparation_ligne.setQte_APreparer(quantite);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat heureFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        String dateDuJour = dateFormat.format(date);
        String heureCourante = heureFormat.format(date);
        ph_preparation_ligne.setSYS_DT_MAJ(dateDuJour);
        ph_preparation_ligne.setSYS_HEURE_MAJ(heureCourante);
        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);
        dotationGlobaleAdapter.notifyDataSetChanged();
    }

    public void gestionCompteur()
    {
        int total_reference = PH_Preparation_LigneOpenHelper.getCountPHPreparationLignesParPHPreparation(db, ph_preparation_courante);
        int total_demande = PH_Preparation_LigneOpenHelper.getCountPHPreparationLignesDemandeParPHPreparation(db, ph_preparation_courante);

        nbElementCommander_TV.setText(String.valueOf(total_demande));
        nbElementTotal_TV.setText(String.valueOf(total_reference));
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_commentaire_dotation, null);

        LinearLayout zoneok = layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = layout.findViewById(R.id.buttonAnnuler);
        final EditText commentaireEdit = layout.findViewById(R.id.commentaire);
        @SuppressLint("UseSwitchCompatOrMaterialCode") final Switch demande_urgente_s = layout.findViewById(R.id.switch_demande_urgente);

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            String commentaire = commentaireEdit.getText().toString();

            enregistrerDemande(commentaire, demande_urgente_s.isChecked());
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    public void enregistrerDemande(String commentaire, boolean urgente)
    {
        ph_preparation_courante.setStatut("En cours de régularisation");
        ph_preparation_courante.setCommentaires(commentaire);
        ph_preparation_courante.setURGENT(urgente);
        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_courante);
        //Création de l'action utilisateur
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", ph_preparation_courante.getUID(), "", "Demander");

        List<PH_Preparation_Ligne> listePhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation_courante);

        for(PH_Preparation_Ligne ph_preparation_ligne : listePhPreparationLigne)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparation_ligne.get_UID(), "", 0, ph_preparation_ligne.getQte_APreparer(), ph_preparation_ligne.getProduitDesignation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
        }

        // Ajout du PH_Preparation au ElementASynchroniser
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_courante.getPhiMR4UUID(), ph_preparation_courante.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        // Tentative de lancer la sychronisation
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);
        }

        Toast.makeText(InformationDotationServiceActivity.this, "Enregistrement en cours", Toast.LENGTH_SHORT).show();
        Intent detailPleinVideIntent = new Intent(InformationDotationServiceActivity.this, ServiceDemandeDotationGlobaleActivity.class);
        Bundle detailPleinVideBundle = super.getBundle();
        detailPleinVideIntent.putExtras(detailPleinVideBundle);
        InformationDotationServiceActivity.this.startActivity(detailPleinVideIntent);
        InformationDotationServiceActivity.this.finish();
    }

    @SuppressLint("SetTextI18n")
    private void afficherAlerte(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte, null);
        TextView text_tv = layout.findViewById(R.id.messageFin);
        LinearLayout valider_ll = layout.findViewById(R.id.buttonOk);

        text_tv.setText("Veuillez saisir toutes les lignes avant de valider la demande.");
        builder.setView(layout);
        final AlertDialog alertDialogErreur = builder.create();
        valider_ll.setOnClickListener(view -> alertDialogErreur.dismiss());

        alertDialogErreur.setCanceledOnTouchOutside(false);
        alertDialogErreur.setCancelable(false);
        alertDialogErreur.show();
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
        int compteurDetailDot = 0;
        //nbTotalPreparationsLignes.setText(String.valueOf(listDetailDot.size()));
        for(Detail_Dot detail_dot : listDetailDot)
        {
            compteurDetailDot ++;
            CreatePhPreparationLigneDetail(detail_dot, dotation);
        }

        phpreparationid = phPreparationID;
        getIntent().putExtra("phPreparationID", phPreparationID);
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
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationDotationServiceActivity.this, db, utilisateurConnecte, false);
            }
        }
    }
}
