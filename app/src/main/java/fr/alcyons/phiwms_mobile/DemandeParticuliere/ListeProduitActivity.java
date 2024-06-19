package fr.alcyons.phiwms_mobile.DemandeParticuliere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Demande_MotifOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DemandeParticuliereAdapater;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeProduitActivity extends ServiceActivity implements DemandeParticuliereAdapater.ItemClickListener{
    Depot depot;
    List<Produit> produitList = new ArrayList<>();
    List<Integer> listeQuantiteProduit = new ArrayList<>();
    LinearLayout zoneSousSelection;
    TextView textCompteur;
    EditText searchBar;
    ImageView removeSearch;
    MenuItem menuQuitter;
    boolean sousSelection;
    ImageView iconEnvoi;
    LinearLayout lancerScan;
    TextView textLancerScan;
    ImageView iconLancerScan;

    DemandeParticuliereAdapater adapter;
    RecyclerView recyclerView;
    String motif = "";
    boolean viewscan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demandeparticuliereliste);

        // Récupération du dépot grace aux variables globales
        depot = DepotOpenHelper.getDepotParID(db, Objects.requireNonNull(intent.getExtras()).getInt("depotSelectionneID"));

        // Affichage des informations de base
        if(depot != null)
        {
            String nomDepot = getNomDepot();
            ((TextView) findViewById(R.id.nomDepot)).setText(nomDepot);
        }

        textCompteur = findViewById(R.id.textEnvoi);
        searchBar = findViewById(R.id.searchBarDemande);
        removeSearch = findViewById(R.id.removeSearchDemande);
        iconEnvoi = findViewById(R.id.iconEnvoi);
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

        // Récupération de tous les produit
        produitList = ProduitOpenHelper.getAllProduits(db);
        listeQuantiteProduit = new ArrayList<>();
        for(int i = 0; i < produitList.size(); i++)
        {
            listeQuantiteProduit.add(0);
        }

        // Récupération de la listView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DemandeParticuliereAdapater(this, produitList, listeQuantiteProduit);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        sousSelection = false;
        viewscan = false;
    }

    private String getNomDepot() {
        String nomDepot = depot.getNom();
        if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depot.getStructure().contentEquals("PAD"))
        {
            String[] tab_nom = depot.getNom().split(" ");
            String nom = tab_nom[0];
            if(nom.length() > 2)
            {
                nom = nom.substring(0, 3)+"...";
            }
            else
            {
                nom = nom +"...";
            }
            String prenom = tab_nom[1];
            if(prenom.length() > 2)
            {
                prenom = prenom.substring(0, 3)+"...";
            }
            else
            {
                prenom = prenom+"...";
            }
            nomDepot = nom+" "+prenom;
        }
        return nomDepot;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Affichage du nombre de produit
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(produitList.size()));

        searchBar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @SuppressLint("NotifyDataSetChanged")
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String recherche = String.valueOf(s).toLowerCase().trim();
                if(recherche.length() > 2)
                {
                    removeSearch.setVisibility(View.VISIBLE);
                    List<Produit> rechercheList = new ArrayList<>();
                    List<Integer> rechercheQuantite = new ArrayList<>();
                    for(int i = 0; i < adapter.produitsOriginal.size(); i ++)
                    {
                        Produit produit = adapter.produitsOriginal.get(i);

                        if(produit.getDesignation_interne().toLowerCase().contains(recherche) || produit.getRef_fourni().toLowerCase().contains(recherche) || produit.getCategorie().toLowerCase().contains(recherche) || produit.getFournisseur().toLowerCase().contains(recherche))
                        {
                            rechercheList.add(adapter.produitsOriginal.get(i));
                            rechercheQuantite.add(adapter.listQuantiteOriginal.get(i));
                        }
                    }
                    adapter.produits.clear();
                    adapter.produits.addAll(rechercheList);
                    adapter.listQuantite.clear();
                    adapter.listQuantite.addAll(rechercheQuantite);
                }
                else
                {
                    removeSearch.setVisibility(View.GONE);
                    adapter.produits.clear();
                    adapter.produits.addAll(adapter.produitsOriginal);
                    adapter.listQuantite.clear();
                    adapter.listQuantite.addAll(adapter.listQuantiteOriginal);
                }
                adapter.notifyDataSetChanged();
            }
        });

        removeSearch.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            searchBar.setText("");
            removeSearch.setVisibility(View.GONE);
            adapter.produits.clear();
            adapter.produits.addAll(adapter.produitsOriginal);
            adapter.listQuantite.clear();
            adapter.listQuantite.addAll(adapter.listQuantiteOriginal);
            adapter.notifyDataSetChanged();
        });

        zoneSousSelection = findViewById(R.id.layoutValider);
        zoneSousSelection.setOnClickListener(view -> {
            boolean vide = true;
            for(int quantite : listeQuantiteProduit)
            {
                if(quantite > 0)
                {
                    vide = false;
                    break;
                }
            }

            if(vide)
            {
                //afficher message erreur
                afficherAlerte(ListeProduitActivity.this, ListeProduitActivity.this.getLayoutInflater());
            }
            else
            {
                if(sousSelection)
                {
                    afficherAlerteConfirmation(ListeProduitActivity.this, ListeProduitActivity.this.getLayoutInflater());
                }
                else
                {
                    removeSearch.performClick();
                    iconEnvoi.setBackgroundTintList(ColorStateList.valueOf(ListeProduitActivity.this.getResources().getColor(R.color.vert, null)));
                    menuQuitter.setVisible(true);
                    sousSelection = true;
                    listeQuantiteProduit = new ArrayList<>();
                    listeQuantiteProduit.addAll(adapter.listQuantite);
                    ArrayList<Produit> sousSelectionProduit = new ArrayList<>();
                    ArrayList<Integer> sousSelectionQuantite = new ArrayList<>();

                    int position = 0;
                    for(Produit produit : adapter.produitsOriginal)
                    {
                        if(adapter.listQuantite.get(position) > 0)
                        {
                            sousSelectionProduit.add(produit);
                            sousSelectionQuantite.add(adapter.listQuantite.get(position) );
                        }

                        position ++;
                    }
                    adapter = new DemandeParticuliereAdapater(ListeProduitActivity.this, sousSelectionProduit,  sousSelectionQuantite);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
        lancerScan.setOnClickListener(v -> {
            PackageManager pm = ListeProduitActivity.this.getPackageManager();

            if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                // Si on passe pour la première fois, on lance l'activité de décodage.
                Intent newIntent = new Intent(ListeProduitActivity.this, ScannerSearchOnlyActivity.class);
                Bundle extras = ListeProduitActivity.this.getBundle();
                extras.putBoolean("isBoutonSuppressionExistant", true);
                newIntent.putExtras(extras);
                ListeProduitActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    // Si on passe pour la première fois, on lance l'activité de décodage.
                    Intent newIntent = new Intent(ListeProduitActivity.this, BarcodeCaptureActivity.class);
                    Bundle extras = ListeProduitActivity.this.getBundle();
                    extras.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(extras);
                    ListeProduitActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                }
                else
                {
                    // Si on passe pour la première fois, on lance l'activité de décodage.
                    Intent newIntent = new Intent(ListeProduitActivity.this, ScannerSearchOnlyActivity.class);
                    Bundle extras = ListeProduitActivity.this.getBundle();
                    extras.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(extras);
                    ListeProduitActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                }
            }
        });
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
        menuQuitter = menu.findItem(R.id.quitteMenu);
        menuQuitter.setVisible(false);

        menuQuitter.setOnMenuItemClickListener(item -> {
            sousSelection = false;
            menuQuitter.setVisible(false);
            iconEnvoi.setBackgroundTintList(ColorStateList.valueOf(ListeProduitActivity.this.getResources().getColor(R.color.bleu_clair_alcyons, null)));

            int positionAdapter = -1;
            for(Produit produitCourant : adapter.produits)
            {
                positionAdapter ++;

                int positionListe = -1;
                for(Produit produitListe : produitList)
                {
                    positionListe ++;

                    if(produitCourant.getDesignation_interne().toLowerCase().contains(produitListe.getDesignation_interne().toLowerCase()) && produitCourant.getRef_fourni().toLowerCase().contains(produitListe.getRef_fourni().toLowerCase()) && produitCourant.getCategorie().toLowerCase().contains(produitListe.getCategorie().toLowerCase()) && produitCourant.getFournisseur().toLowerCase().contains(produitListe.getFournisseur().toLowerCase()))
                    {
                        break;
                    }
                }

                listeQuantiteProduit.set(positionListe, adapter.listQuantite.get(positionAdapter));
            }


            adapter = new DemandeParticuliereAdapater(ListeProduitActivity.this, produitList,  listeQuantiteProduit);
            recyclerView.setAdapter(adapter);
            return true;
        });
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void afficherAlerte(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte, null);
        TextView text_tv = layout.findViewById(R.id.messageFin);
        LinearLayout valider_ll = layout.findViewById(R.id.buttonOk);

        text_tv.setText("Veuillez saisir au moins une ligne avant de valider la demande.");
        builder.setView(layout);
        final AlertDialog alertDialogErreur = builder.create();
        valider_ll.setOnClickListener(view -> alertDialogErreur.dismiss());

        alertDialogErreur.setCanceledOnTouchOutside(false);
        alertDialogErreur.setCancelable(false);
        alertDialogErreur.show();
    }

    @SuppressLint("SetTextI18n")
    public void gestionCompteur()
    {
        int compteur = 0;
        for(Integer courant : adapter.listQuantite)
        {
            if(courant > 0)
                compteur ++;
        }

        String text = "référence commandée";
        if(compteur > 1)
            text = "références commandées";

        textCompteur.setText(compteur+" "+text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)  {
            if (requestCode == CodesEchangesActivites.RETOUR_CODE_GS1) {
                Produit produit = null;
                removeSearch.performClick();
                String codeComplet = data.getStringExtra("code");
                if (codeComplet != null && !codeComplet.contentEquals("")) {
                    if(codeComplet.toLowerCase().startsWith("phitagtin:"))
                    {
                        String[] tabProduit = codeComplet.toLowerCase().split("phitagtin:");
                        if (tabProduit.length > 1) {
                            String gtin = tabProduit[tabProduit.length - 1];
                            produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                        }
                    }
                    else if(codeComplet.toLowerCase().startsWith("phitagref:"))
                    {
                        String[] tabProduit = codeComplet.toLowerCase().split("phitagref:");
                        if (tabProduit.length > 1) {
                            int id = Integer.parseInt(tabProduit[tabProduit.length - 1]);
                            produit = ProduitOpenHelper.getProduitByID(db, id);
                        }
                    }

                    if (produit != null) {
                        int position = -1;
                        for (Produit produitCourant : adapter.produits) {
                            position++;
                            if (produitCourant.getDesignation_interne().toLowerCase().contains(produit.getDesignation_interne().toLowerCase()) && produitCourant.getRef_fourni().toLowerCase().contains(produit.getRef_fourni().toLowerCase()) && produitCourant.getCategorie().toLowerCase().contains(produit.getCategorie().toLowerCase()) && produitCourant.getFournisseur().toLowerCase().contains(produit.getFournisseur().toLowerCase())) {
                                break;
                            }
                        }

                        if (position != -1) {
                            int positionListe = position;
                            if(positionListe + 3 <= adapter.produits.size() -1)
                                positionListe = positionListe + 3;
                            else if(positionListe + 2 <= adapter.produits.size() - 1)
                                positionListe = positionListe + 2;
                            else if(positionListe + 1 <= adapter.produits.size() - 1)
                                positionListe = positionListe + 1;

                            Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(positionListe);
                            int finalPosition = position;
                            viewscan = true;
                            new Handler().postDelayed(() -> Objects.requireNonNull(recyclerView.getLayoutManager().findViewByPosition(finalPosition)).performClick(), 250);
                        }
                    }

                }
            }
            invalidateOptionsMenu();
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        EditText edit = view.findViewById(R.id.qte_demander);
        edit.requestFocus();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void clickItem(View view)
    {
        if(viewscan)
        {
            viewscan = false;
            view.findViewById(R.id.linearLigneProduit).setBackground(ListeProduitActivity.this.getResources().getDrawable(R.drawable.background_plain_vert, null));
        }
        else
        {
            view.findViewById(R.id.qte_demander).requestFocus();
        }
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater)
    {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View layout = inflater.inflate(R.layout.alerte_demande_particuliere, null);

        //initi du tri
        Spinner spinner = layout.findViewById(R.id.spinnerMotif);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch urgent_s = layout.findViewById(R.id.switchUrgent);
        TextView textnbRef = layout.findViewById(R.id.nb_reference);
        ImageView quitterModale = layout.findViewById(R.id.quitterModale);
        LinearLayout validation_ll = layout.findViewById(R.id.linear_validation);
        EditText commentaire_et = layout.findViewById(R.id.commentaire);
        String text = "référence demandée";
        if(adapter.produits.size() > 1)
            text = "références demandées";
        textnbRef.setText(adapter.produits.size()+" "+text);

        List<String> listeMotifDemande = PH_Demande_MotifOpenHelper.getDemandeMotif(db);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listeMotifDemande);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                motif =spinner.getItemAtPosition(position).toString();
                if(!spinner.getItemAtPosition(position).toString().contentEquals("Sélectionnez un motif"))
                    validation_ll.setVisibility(View.VISIBLE);
                else
                    validation_ll.setVisibility(View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        validation_ll.setOnClickListener(view -> {
            String commentaire = commentaire_et.getText().toString();
            boolean urgent = urgent_s.isChecked();

            onClick_Action_envoyerDemande(motif, commentaire, urgent);
        });

        builder.setView(layout);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        quitterModale.setOnClickListener(view -> alertDialog.dismiss());

        alertDialog.show();
    }

    public void onClick_Action_envoyerDemande(String motif, String commentaire, boolean urgent) {

        boolean confirmer = Alerte.afficherAlerte(this, "Envoyer", "Êtes-vous sûr de vouloir envoyer cette demande ?", "OuiNon");
        if (confirmer) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String datePL = EVENTOpenHelper.getDateProchaineLivraison(db, depot.getDepot_UID());
            String livraisonDatePrevue;
            if(!datePL.contentEquals(""))
            {
                Date datelivraison = new Date(EVENTOpenHelper.getDateProchaineLivraison(db, depot.getDepot_UID()));
                livraisonDatePrevue = dateFormat.format(datelivraison);
            }
            else
            {
                livraisonDatePrevue = dateFormat.format(tomorrow);
            }

            if(urgent)
            {
                livraisonDatePrevue = dateFormat.format(tomorrow);
            }

            int compteurReussite = 0;

            // Récupération du dépot PUI
            Depot depotPUI = DepotOpenHelper.getDepotPUI(db);

            // Initialisation des données permettant de créer un PH_Préparation
            Random randomAction = new Random();
            int preparationId = randomAction.nextInt();
            if(preparationId > 0)
                preparationId= preparationId*-1;
            int UID = preparationId;
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
            String Liste = "Demande Particulière : " + motif;
            int depotDestinataireID = 0;
            String depotDestinataireReference = "";
            if(depotPUI != null)
            {
                depotDestinataireID = depot.getDepot_UID();
                depotDestinataireReference = depot.getDepot_Reference();
            }
            String SYS_DT_MAJ = "";
            String SYS_HEURE_MAJ = "";
            String SYS_USER_MAJ = "";
            String PrescripteurReference = "";
            String Prescription_date = "";
            String PrescripteurNom = "";
            assert depotPUI != null;
            String depotOrigineReference = depotPUI.getDepot_Reference();
            int depotOrigineID = depotPUI.getDepot_UID();
            String PreparationDate = "";
            String LivraisonPrevueDate = livraisonDatePrevue;
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
            String previsionDateDebut = livraisonDatePrevue;
            String previsionDateFin = livraisonDatePrevue;
            Boolean URGENT = urgent;
            int preparateur_userID = 0;
            int pharmacien_userID = 0;
            double Volume = 0;
            int PaletteNB = 0;
            int CaisseNB = 0;
            int Conteneur_NB = 0;
            String numero_scelle = "";

            // Pour enregistrer les dates en base de données nous avons besoin de les transformer au format yyyy-MM-dd
            @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFournie = dateDecodeur.parse(LivraisonPrevueDate);
                LivraisonPrevueDate = dateFormat.format(dateFournie);

                dateFournie = dateDecodeur.parse(previsionDateDebut);
                previsionDateDebut = dateFormat.format(dateFournie);

                dateFournie = dateDecodeur.parse(previsionDateFin);
                previsionDateFin = dateFormat.format(dateFournie);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Création et insertion en base du PH_Preparation
            PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, commentaire, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB, CaisseNB, Conteneur_NB, numero_scelle);
            int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation.getUID(), "", "Demande particulière");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur

            // Parcours des demande_pui afin d'en faire des PH_Preparation_Ligne
            int compteur = -1;
            for (Produit produit : adapter.produits)
            {
                compteur ++;
                // Initialisation des données permettant de créer un PH_Préparation_Ligne
                int PreparationID = ph_preparation.getUID();
                Random randomPreparationLigne = new Random();
                int preparationLigneId = randomPreparationLigne.nextInt();
                if(preparationLigneId > 0)
                    preparationLigneId= preparationLigneId*-1;
                int _UID = preparationLigneId;
                produitID = produit.getID_produit();
                produitDesignation = produit.getDesignation_interne();
                int Qte_APreparer = adapter.listQuantite.get(compteur);
                int Qte_livrer = 0;
                Boolean Livrer = false;
                Boolean Valider = false;
                String ValidationDate = "";
                String produitReference = produit.getRef_fourni();
                String ZoneDepot = "";
                String produitCategorie = produit.getCategorie();
                SYS_DT_MAJ = "";
                SYS_HEURE_MAJ = "";
                SYS_USER_MAJ = "";
                double produitCondDistrib = produit.getCond_distrib();
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
                int Qte_besoin = 0;
                int Qte_StockSaisie = 0;
                String EmplacementParDefaut = "";
                boolean accepter = true;

                // Création et insertion en base du PH_Preparation_Ligne
                PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_APreparer, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_APreparer, EmplacementParDefaut, Qte_APreparer, accepter, ph_preparation.getUID());
                int ph_preparation_lignePHIMR4uid = (int) PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparation_ligne);

                // Ajout du PH_Preparation_Ligne au ElementASynchroniser
                long rowId = ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_lignePHIMR4uid, ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                if (rowId != -1) {
                    compteurReussite++;
                    //gestion des actions lignes
                    ActionUtilisateur_Ligne actionUtilisateur_ligne = getActionUtilisateur_ligne(new_action_utilisateur, ph_preparation_ligne);
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }
            }
            if (compteurReussite == adapter.produits.size()) {
                // Ajout du PH_Preparation au ElementASynchroniser
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

                // Tentative de lancer la sychronisation
                if (statutConnexion) {
                    ElementASynchroniserOpenHelper.toutSynchroniser(ListeProduitActivity.this, db, utilisateurConnecte, true);
                }

                Toast.makeText(ListeProduitActivity.this, "Demande Particulière effectuée", Toast.LENGTH_SHORT).show();
                ListeProduitActivity.this.finish();
            } else {
                Alerte.afficherAlerte(ListeProduitActivity.this, "Alerte", "Une erreur est survenue", "alerte");
                ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                ListeProduitActivity.this.finish();
            }

        }

    }

    @NonNull
    private static ActionUtilisateur_Ligne getActionUtilisateur_ligne(ActionUtilisateur new_action_utilisateur, PH_Preparation_Ligne ph_preparation_ligne) {
        Random randomactionligne = new Random();
        int actionligneId = randomactionligne.nextInt();
        if(actionligneId > 0)
            actionligneId= actionligneId*-1;

        return new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", ph_preparation_ligne.get_UID(), "", 0, ph_preparation_ligne.getQte_APreparer(), ph_preparation_ligne.getProduitDesignation());
    }

}
