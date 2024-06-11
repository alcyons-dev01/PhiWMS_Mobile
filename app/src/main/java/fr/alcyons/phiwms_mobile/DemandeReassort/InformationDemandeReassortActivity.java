package fr.alcyons.phiwms_mobile.DemandeReassort;

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

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reassort;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PhReassortAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class InformationDemandeReassortActivity  extends ServiceAvecConnexionActivity {
    PH_Reassort reassort;
    Depot depot;
    List<PH_Reassort> reassortList;
    List<PH_Preparation_Ligne> phPreparationLigneList = new ArrayList<>();
    Calendar calendar;
    PhReassortAdapter reassortAdapter;
    ListView demandePuiListeView;
    TextView nomDemandeTextView;
    int reassortid;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_dotation_service);

        // Récupération des intents
        depotID = intent.getIntExtra("depotSelectionneID", 0);
        reassortid = intent.getIntExtra("PH_ReassortSelectionneID", 0);
        phpreparationid = intent.getIntExtra("phPreparationID", 0);

        // Récupération des ressources
        depot = DepotOpenHelper.getDepotParID(db, depotID);
        reassort = PH_ReassortOpenHelper.getPH_ReassortByphiwms_mobileUUID(db, reassortid);
        ph_preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, phpreparationid);

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

        nomDemandeTextView.setText(reassort.getListe().trim());

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
                    for(int i = 0; i < reassortAdapter.phPreparationLigneOriginalList.size(); i ++)
                    {
                        PH_Preparation_Ligne phPreparationLigne = reassortAdapter.phPreparationLigneOriginalList.get(i);

                        if(phPreparationLigne.getProduitDesignation().toLowerCase().contains(recherche))
                        {
                            rechercheList.add(reassortAdapter.phPreparationLigneOriginalList.get(i));
                        }
                    }
                    reassortAdapter.phPreparationLigneList.clear();
                    reassortAdapter.phPreparationLigneList.addAll(rechercheList);
                }
                else
                {
                    removeSearch.setVisibility(View.GONE);
                    reassortAdapter.phPreparationLigneList.clear();
                    reassortAdapter.phPreparationLigneList.addAll(reassortAdapter.phPreparationLigneOriginalList);
                }
                reassortAdapter.notifyDataSetChanged();
            }
        });

        removeSearch.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            searchBar.setText("");
            removeSearch.setVisibility(View.GONE);
            reassortAdapter.phPreparationLigneList.clear();
            reassortAdapter.phPreparationLigneList.addAll(reassortAdapter.phPreparationLigneOriginalList);
            reassortAdapter.notifyDataSetChanged();
        });

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
                afficherAlerteConfirmation(InformationDemandeReassortActivity.this, InformationDemandeReassortActivity.this.getLayoutInflater());
            }
            else
            {
                afficherAlerte(InformationDemandeReassortActivity.this, InformationDemandeReassortActivity.this.getLayoutInflater());
            }
        });

        String livraisonPrevueDate = ph_preparation_courante.getLivraisonPrevueDate();
        String[] tabDate = livraisonPrevueDate.split("-");
        livraisonPrevueDate = tabDate[tabDate.length-1]+"/"+tabDate[1]+"/"+tabDate[0];
        dateEnvoiListe_TV.setText(livraisonPrevueDate);
    }

    @Override
    public void onResume() {
        super.onResume();
        reassortList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(InformationDemandeReassortActivity.this, LayoutInflater.from(InformationDemandeReassortActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(InformationDemandeReassortActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePh_Preparation_Lignes + "ph_preparation/" + ph_preparation_courante.getUID();


            // Takes the response from the JSON request
            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    response -> {
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
                                    Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Alerte", "Aucune Réassort Ligne trouvée", "alerte");
                                }
                            } else {
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
                        Alerte.afficherAlerte(InformationDemandeReassortActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Réassort ligne)", "alerte");
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
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            passageParOnCreate = false;
        } else {
            reassortList = PH_ReassortOpenHelper.getPH_Reassort(db);
            if (reassortList.isEmpty()) {
                connexionNecessaire();
                return;
            }
        }

        //tri de la liste
        phPreparationLigneList.sort(Comparator.comparing(PH_Preparation_Ligne::getProduitDesignation));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        reassortAdapter = new PhReassortAdapter(InformationDemandeReassortActivity.this, phPreparationLigneList, db, utilisateurConnecte);
        demandePuiListeView.setAdapter(reassortAdapter);
        demandePuiListeView.setItemsCanFocus(true);
        demandePuiListeView.setOnItemClickListener((adapterView, view, i, l) -> {
            PH_Preparation_Ligne preparation_ligne = reassortAdapter.phPreparationLigneList.get(i);
            afficherNumberPicker(InformationDemandeReassortActivity.this, InformationDemandeReassortActivity.this.getLayoutInflater(), preparation_ligne);
        });

        invalidateOptionsMenu();
        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
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
            Intent detailDotationIntent = new Intent(InformationDemandeReassortActivity.this, ListeReassortServiceActivity.class);
            Bundle detailDotationBundle = InformationDemandeReassortActivity.this.getBundle();
            detailDotationIntent.putExtras(detailDotationBundle);
            InformationDemandeReassortActivity.this.startActivity(detailDotationIntent);
            InformationDemandeReassortActivity.this.finish();
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

        designation_tv.setText(ph_preparation_ligne.getProduitDesignation());
        reference_tv.setText(ph_preparation_ligne.getProduitReference());
        conditionnement_tv.setText("(x"+ (int) ph_preparation_ligne.getProduitCondDistrib() +")");
        qte_besoin_tv.setText(String.valueOf(ph_preparation_ligne.getQte_besoin()));

        stock_et.setOnKeyListener((view, i, keyEvent) -> {
            //touche suivante sur le clavier android
            if(i == 66)
            {
                validerstock_ll.performClick();
            }
            return false;
        });
        demande_et.setOnKeyListener((view, i, keyEvent) -> {

            //touche suivante sur le clavier android
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

            if(!stock_string.contentEquals(""))
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
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, false);

                layoutdotation_ll.setVisibility(View.VISIBLE);
                validerstock_ll.setVisibility(View.GONE);
                demande_et.requestFocus();
            }
        });

        valider_ll.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(validerstock_ll.getWindowToken(), 0);
            String stock_string = stock_et.getText().toString().trim();
            int stockValue = 0;
            if(!stock_string.contentEquals(""))
                stockValue = Integer.parseInt(stock_string);

            String demande_string = demande_et.getText().toString().trim();
            int demandeValue = 0;
            if(!demande_string.contentEquals(""))
                demandeValue = Integer.parseInt(demande_string);

            ph_preparation_ligne.setQte_StockSaisie(stockValue);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
            ElementASynchroniserOpenHelper.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, false);

            int total = stockValue + demandeValue;
            if(ph_preparation_ligne.getQte_besoin() > total)
            {
                afficherMessageAlerte(context, inflater, ph_preparation_ligne, demandeValue);
            }
            else
            {
                ph_preparation_ligne.setQte_APreparer(demandeValue);
                ph_preparation_ligne.setQte_Demander(demandeValue);

                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                ElementASynchroniserOpenHelper.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, false);
                reassortAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        if(ph_preparation_ligne.getQte_StockSaisie() == -1)
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
            //layoutdotation_ll.setVisibility(View.VISIBLE);
            validerstock_ll.performClick();
        }
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

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        ElementASynchroniserOpenHelper.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, false);
        reassortAdapter.notifyDataSetChanged();
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
        ph_preparation_courante.setStatut("En attente");
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
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation_courante.getUID(), "", "Demande Plein Vide");

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
            ElementASynchroniserOpenHelper.toutSynchroniser(InformationDemandeReassortActivity.this, db, utilisateurConnecte, false);
        }

        Toast.makeText(InformationDemandeReassortActivity.this, "Enregistrement en cours", Toast.LENGTH_SHORT).show();
        Intent detailPleinVideIntent = new Intent(InformationDemandeReassortActivity.this, ListeReassortServiceActivity.class);
        Bundle detailPleinVideBundle = super.getBundle();
        detailPleinVideIntent.putExtras(detailPleinVideBundle);
        InformationDemandeReassortActivity.this.startActivity(detailPleinVideIntent);
        InformationDemandeReassortActivity.this.finish();
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
}
