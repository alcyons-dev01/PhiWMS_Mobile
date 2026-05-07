package fr.alcyons.phiwms_mobile.RetourPUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceRetourPUIActivity;
import fr.alcyons.phiwms_mobile.Fragment.RechercheFragment;
import fr.alcyons.phiwms_mobile.Fragment.ScannerFragment;
import fr.alcyons.phiwms_mobile.Fragment.ScannerInputFragment;
import fr.alcyons.phiwms_mobile.Interfaces.RechercheAdjustable;
import fr.alcyons.phiwms_mobile.Interfaces.ScanDebounce;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeScanne;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import fr.alcyons.phiwms_mobile.RetourPUI.Fragment.ARetournerPUIFragment;

public class DetailRetourPUIActivity extends ServiceActivity implements ARetournerPUIFragment.OnElementSelectionneListener, RechercheFragment.OnElementRechercheListener, RechercheAdjustable {
    Retour retourSelectionne;
    List<Retour_Ligne> retourLigneList;
    Retour_Ligne_RetourPUIAdapter.Retour_LigneViewHolder viewHolderAModifier;
    ActivityResultLauncher<Intent> resultListeEmplacement;
    String commentaire;
    ARetournerPUIFragment aRetournerPUIFragment;
    boolean isACompterOpen = false;

    // UI elements
    View lancerScan;
    View lancerRecherche;
    View boutonAction;

    // Fragments
    androidx.fragment.app.Fragment scannerFragment;
    RechercheFragment rechercheFragment;

    // State
    boolean isScannerOpen = false;
    boolean isSearchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_pui);

        // Récupération du Retour grâce à la variable globale
        retourSelectionne = RetourOpenHelper.getRetourByID(db, Objects.requireNonNull(intent.getExtras()).getInt("retourSelectionneID"));

        // Affichage des informations de base
        ((TextView) findViewById(R.id.numero)).setText("Numéro de retour : " + retourSelectionne.getNumero());


        retourLigneList = new ArrayList<>();

        resultListeEmplacement = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS) {
                        onResume();
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Alerte.afficherAlerteConfirmation(DetailRetourPUIActivity.this, getLayoutInflater(), getBundle(), "Voulez-vous quitter le détail du retour PUI ?", true, false, DetailRetourPUIActivity.this);
            }
        });

        setupEventListeners();
        this.lancerScan.performClick();
    }

    private void setupEventListeners() {
        // Scanner button listener
        lancerScan = findViewById(R.id.lancerScan);
        if (lancerScan != null) {
            lancerScan.setOnClickListener(v -> {
                if (isScannerOpen) {
                    closeScanner();
                } else {
                    closeOpenedFragments();
                    openScanner();
                }
            });
        }

        // Search button listener
        lancerRecherche = findViewById(R.id.lancerRecherhe);
        if (lancerRecherche != null) {
            lancerRecherche.setOnClickListener(v -> {
                if (isSearchOpen) {
                    closeSearch();
                } else {
                    closeOpenedFragments();
                    showSearchInput();
                }
            });
        }

        // Action button listener
        boutonAction = findViewById(R.id.boutonAction);
        if (boutonAction != null) {
            boutonAction.setOnClickListener(v -> {
                Alerte.afficherAlerteSaisieText(DetailRetourPUIActivity.this, getLayoutInflater(), "Validation retour PUI", "Souhaitez-vous valider le retour PUI ?", "Ajouter un commentaire...");
            });
        }

        // "À Retourner" list button listener
        View aRetournerPUI_LL = findViewById(R.id.aRetournerPUI_LL);
        if (aRetournerPUI_LL != null) {
            aRetournerPUI_LL.setOnClickListener(v -> {
                if (isACompterOpen) {
                    closeACompter();
                } else {
                    closeOpenedFragments();
                    openACompter();
                }
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération des retour_ligne si nécessaire
        retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne);

        //gestion des retours lignes
        for(Retour_Ligne retourLigneTemp : retourLigneList)
        {
            List<Retour_Ligne> retourLigneRetournee = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, retourLigneTemp.getCode_produit());

            if(retourLigneRetournee.isEmpty())
            {
                Produit produitCourant  = ProduitOpenHelper.getProduitByID(db, retourLigneTemp.getCode_produit());
                if(!produitCourant.getEmplacement_PUI_Defaut().contentEquals("") && produitCourant.getEmplacement_PUI_Defaut() != null)
                {
                    creationRetourLigne(retourLigneTemp, produitCourant);
                }
            }
        }

        // Mise à jour de la liste dans le fragment si elle est ouverte
        if (aRetournerPUIFragment != null) {
            aRetournerPUIFragment.updateList((ArrayList<Retour_Ligne>) retourLigneList, retourSelectionne);
        }

        // Mise à jour du compteur
        TextView nbRefTV = findViewById(R.id.nbReferenceARetournerPUI_TV);
        if (nbRefTV != null) {
            nbRefTV.setText(String.valueOf(retourLigneList.size()));
        }
    }

    @Override
    public void retourService(Bundle bundle)
    {
        Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ServiceRetourPUIActivity.class);
        Bundle detailRetourPUIBundle = super.getBundle();
        detailRetourPUIIntent.putExtras(detailRetourPUIBundle);
        DetailRetourPUIActivity.this.startActivity(detailRetourPUIIntent);
        DetailRetourPUIActivity.this.finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSaveCircle);
        item.setOnMenuItemClickListener(item1 -> {
            // On vérifie que chaque élément a au moins un emplacement de retour
            boolean retourPuiValide = true;
            for (Retour_Ligne retour_ligneTemp : retourLigneList)
            {
                List<Retour_Ligne> retourLigneCourant = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, retour_ligneTemp.getCode_produit());
                int qteRetournerTemp = 0;

                for(Retour_Ligne ligneNegTemp : retourLigneCourant)
                {
                    qteRetournerTemp += (int) ligneNegTemp.getQte_Retourner();
                }

                if(qteRetournerTemp != retour_ligneTemp.getQte_avant_retour())
                {
                    retourPuiValide = false;
                    break;
                }
            }

            if (!retourPuiValide) {
                Alerte.afficherAlerteConfirmation(DetailRetourPUIActivity.this, getLayoutInflater(), getBundle(), "Tous les éléments n'ont pas été retourné entièrement.\nSouhaitez-vous continuer ?", false, true, DetailRetourPUIActivity.this);
            }
            else
            {
                Alerte.afficherAlerteSaisieText(DetailRetourPUIActivity.this, getLayoutInflater(), "Commentaire", "Souhaitez-vous valider le retour PUI ?", "Ajouter un commentaire...");
            }

            return true;
        });
        return true;
    }

    @Override
    public void retourSaisieText(String text)
    {
        commentaire = text;
        validerRetourPUI();
    }

    @Override
    public void confirmationService() {
        Alerte.afficherAlerteSaisieText(DetailRetourPUIActivity.this, getLayoutInflater(), "Commentaire", "Veuillez saisir un commentaire pour ce retour PUI :", "Saisir un commentaire...");
    }

    private void validerRetourPUI()
    {
        //on supprime les retours ligne de base
        for(Retour_Ligne retour_ligneTemp : retourLigneList)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retour_ligneTemp);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.getPhiMR4UUID(), retour_ligneTemp.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.SUPPR);
        }

        //on recupère les retours ligne negatif
        List<Retour_Ligne> listRetourLigneNegatif = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retourSelectionne);

        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", retourSelectionne.get_UID(), "", "Retour PUI");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

        for (Retour_Ligne retour_ligneTemp : listRetourLigneNegatif) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.getPhiMR4UUID(), retour_ligneTemp.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retour_ligneTemp.get_UID(), "", 0, (int)retour_ligneTemp.getQte_Retourner(), retour_ligneTemp.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);

            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
        }

        // Si tout est ok mise à jour du Retour
        String intitule = retourSelectionne.getIntitule();
        intitule = intitule.replace(getString(R.string.RetourPUIDemande), getString(R.string.RetourPUIEffectue));
        retourSelectionne.setIntitule(intitule.trim());
        retourSelectionne.setEn_Attente_de(getString(R.string.RetourPUIEffectue));
        retourSelectionne.setCommentaire(commentaire);
        long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);
        if (rowID != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        }

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        ElementASynchroniserOpenHelper.toutSynchroniser(DetailRetourPUIActivity.this, db, utilisateurConnecte, true);
        Toast.makeText(DetailRetourPUIActivity.this, "Retour PUI effectué", Toast.LENGTH_SHORT).show();
        retourService(getBundle());
    }

    private void creationRetourLigne(Retour_Ligne retourLigneBase, Produit produit)
    {
        Random random = new Random();
        int retourLigneId = random.nextInt();
        if(retourLigneId > 0)
            retourLigneId = retourLigneId*-1;

        Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneBase);
        retourLigneCourant.set_UID(retourLigneId);
        retourLigneCourant.setRetourPUI_Zone(produit.getZone_PUI_Defaut());
        retourLigneCourant.setRetourPUI_Emplacement(produit.getEmplacement_PUI_Defaut());
        retourLigneCourant.setQte_Retourner(retourLigneBase.getQte_Retourner());

        long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
    }

    @Override
    public void onElementSelectionne(Retour_Ligne retourLigne)
    {
        Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ListeEmplacementRetourPUIActivity.class);
        Bundle detailRetourPUIBundle = DetailRetourPUIActivity.super.getBundle();
        detailRetourPUIBundle.putInt("produitID", retourLigne.getCode_produit());
        detailRetourPUIBundle.putSerializable("retourLigne", retourLigne);
        detailRetourPUIBundle.putInt("depotID", DepotOpenHelper.getDepotParReference(db, retourSelectionne.getRef_Depot_Dest()).getDepot_UID());
        detailRetourPUIIntent.putExtras(detailRetourPUIBundle);

        resultListeEmplacement.launch(detailRetourPUIIntent);
    }

    private void openACompter()
    {
        View container = findViewById(R.id.referenceARetournerPUIContainer);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) container.getLayoutParams();
        params.height = android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
        params.weight = 0f;
        container.setLayoutParams(params);
        container.setVisibility(View.VISIBLE);

        aRetournerPUIFragment = ARetournerPUIFragment.newInstance((ArrayList<Retour_Ligne>) retourLigneList, retourSelectionne);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.referenceARetournerPUIContainer, aRetournerPUIFragment)
                .commit();

        isACompterOpen = true;

        // Scroll to the container
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> {
            scrollView.smoothScrollTo(0, container.getTop());
        });
    }

    private void closeACompter()
    {
        View container = findViewById(R.id.referenceARetournerPUIContainer);
        container.setVisibility(View.GONE);
        android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) container.getLayoutParams();
        params.height = 0;
        params.weight = 0f;
        container.setLayoutParams(params);
        if (aRetournerPUIFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(aRetournerPUIFragment)
                    .commit();
            aRetournerPUIFragment = null;
        }
        isACompterOpen = false;
    }

    @Override
    public void onElementRechercher(int element) {
        scrollToItemOrDisplayAlert(element);
    }

    @Override
    public void ajusterHauteurRecherche(int hauteur) {
        View rechercheContainer = findViewById(R.id.rechercheContainer);
        if (rechercheContainer != null) {
            rechercheContainer.getLayoutParams().height = hauteur == 0 ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT;
            rechercheContainer.requestLayout();
        }
    }

    private void scrollToItemOrDisplayAlert(int idProduit) {
        int position = -1;
        for (int i = 0; i < retourLigneList.size(); i++) {
            if (retourLigneList.get(i).getCode_produit() == idProduit) {
                position = i;
                break;
            }
        }

        if (position >= 0) {
            if (!isACompterOpen) {
                closeOpenedFragments();
                openACompter();
            }
            if (aRetournerPUIFragment != null) {
                aRetournerPUIFragment.scrollToPosition(position);
            }
        } else {
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Produit non trouvé", "Ce produit n'est pas dans la liste de retour PUI", false, false);
        }
    }

    private void closeOpenedFragments() {
        if (isScannerOpen) closeScanner();
        if (isSearchOpen) closeSearch();
        if (isACompterOpen) closeACompter();
    }

    private void openScanner() {
        View scannerContainer = findViewById(R.id.scannerContainer);
        if (scannerContainer != null) {
            // Set height to 300dp
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) scannerContainer.getLayoutParams();
            params.height = (int) (300 * getResources().getDisplayMetrics().density);
            params.weight = 0f;
            scannerContainer.setLayoutParams(params);
            scannerContainer.setVisibility(View.VISIBLE);
            // Animate translationY
            scannerContainer.setTranslationY(-getResources().getDisplayMetrics().heightPixels);
            scannerContainer.animate().translationY(0f).setDuration(300).start();

            scannerFragment = createScannerFragment();
            setupScannerFragmentCallbacks(scannerFragment);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.scannerContainer, scannerFragment)
                    .commit();
        }
        isScannerOpen = true;
    }

    private void closeScanner() {
        View scannerContainer = findViewById(R.id.scannerContainer);
        if (scannerContainer != null) {
            scannerContainer.animate().translationY(-scannerContainer.getHeight()).setDuration(300).withEndAction(() -> {
                scannerContainer.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) scannerContainer.getLayoutParams();
                params.height = 0;
                params.weight = 0f;
                scannerContainer.setLayoutParams(params);
                if (scannerFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(scannerFragment)
                            .commit();
                    scannerFragment = null;
                }
            }).start();
        }
        isScannerOpen = false;
    }

    private androidx.fragment.app.Fragment createScannerFragment() {
        if (hasCamera()) {
            return new ScannerFragment();
        } else {
            return new ScannerInputFragment();
        }
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY);
    }

    private void setupScannerFragmentCallbacks(androidx.fragment.app.Fragment fragment) {
        try {
            if (fragment instanceof ScannerInputFragment) {
                java.lang.reflect.Field onCodeScannedField = fragment.getClass().getDeclaredField("onCodeScanned");
                onCodeScannedField.setAccessible(true);
                onCodeScannedField.set(fragment, (kotlin.jvm.functions.Function1<String, kotlin.Unit>) code -> {
                    handleScannedCode(code);
                    return kotlin.Unit.INSTANCE;
                });

                java.lang.reflect.Field onCloseRequestedField = fragment.getClass().getDeclaredField("onCloseRequested");
                onCloseRequestedField.setAccessible(true);
                onCloseRequestedField.set(fragment, (kotlin.jvm.functions.Function0<kotlin.Unit>) () -> {
                    closeScanner();
                    return kotlin.Unit.INSTANCE;
                });
            } else if (fragment instanceof ScannerFragment) {
                java.lang.reflect.Field onCodeScannedField = fragment.getClass().getDeclaredField("onCodeScanned");
                onCodeScannedField.setAccessible(true);
                onCodeScannedField.set(fragment, (kotlin.jvm.functions.Function1<String, kotlin.Unit>) code -> {
                    handleScannedCode(code);
                    return kotlin.Unit.INSTANCE;
                });

                java.lang.reflect.Field onCloseRequestedField = fragment.getClass().getDeclaredField("onCloseRequested");
                onCloseRequestedField.setAccessible(true);
                onCloseRequestedField.set(fragment, (kotlin.jvm.functions.Function0<kotlin.Unit>) () -> {
                    closeScanner();
                    return kotlin.Unit.INSTANCE;
                });
            }
            if (fragment instanceof ScanDebounce) {
                ((ScanDebounce) fragment).setScanDebounce(750L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleScannedCode(String scannedCode) {
        java.util.HashMap<String, String> resultDecoupage = GestionCodeScanne.decoupageCode(scannedCode);
        String codeIdentification = resultDecoupage.get("code");
        if (codeIdentification != null && !codeIdentification.isEmpty()) {
            List<Produit> produits = ProduitOpenHelper.getProduitsByIdentification(db, codeIdentification);
            if (!produits.isEmpty()) {
                int produitId = produits.get(0).getID_produit();
                scrollToItemOrDisplayAlert(produitId);
            } else {
                Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Produit non trouvé", "Aucun produit trouvé pour le code scanné: " + codeIdentification, false, false);
            }
        } else {
            Alerte.afficherAlerteInformation(this, getLayoutInflater(), "Code non reconnu", "Le code scanné n'a pas pu être analysé: " + scannedCode, false, false);
        }
    }

    private void showSearchInput() {
        isSearchOpen = true;
        View textChercherTV = findViewById(R.id.textChercher_TV);
        View searchInputET = findViewById(R.id.searchInput_ET);
        View effacerRechercheIV = findViewById(R.id.effacerRecherche_IV);
        View chevronRecherche = findViewById(R.id.chevronRecherche);

        if (textChercherTV != null) textChercherTV.setVisibility(View.GONE);
        if (searchInputET != null) {
            searchInputET.setVisibility(View.VISIBLE);
            ((android.widget.EditText) searchInputET).requestFocus();
            ((android.widget.EditText) searchInputET).addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(android.text.Editable s) {
                    String query = s.toString().trim();
                    if (!query.isEmpty()) {
                        openSearch();
                        if (rechercheFragment != null) {
                            rechercheFragment.lancerRecherche(query, "retourPUI", String.valueOf(retourSelectionne.get_UID()));
                        }
                    } else {
                        if (rechercheFragment != null) {
                            rechercheFragment.viderListe();
                        }
                    }
                }
            });
        }
        if (effacerRechercheIV != null) {
            effacerRechercheIV.setVisibility(View.VISIBLE);
            effacerRechercheIV.setOnClickListener(v -> closeSearch());
        }
        if (chevronRecherche != null) chevronRecherche.setVisibility(View.GONE);

        // Show keyboard
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && searchInputET != null) {
            imm.showSoftInput(searchInputET, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void openSearch() {
        View rechercheContainer = findViewById(R.id.rechercheContainer);
        if (rechercheContainer != null) {
            rechercheContainer.setVisibility(View.VISIBLE);
            rechercheFragment = new RechercheFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rechercheContainer, rechercheFragment)
                    .commitNow();
        }
        isSearchOpen = true;
    }

    private void closeSearch() {
        hideSearchInput();
        View rechercheContainer = findViewById(R.id.rechercheContainer);
        if (rechercheContainer != null) {
            rechercheContainer.setVisibility(View.GONE);
            if (rechercheFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(rechercheFragment)
                        .commit();
                rechercheFragment = null;
            }
        }
        isSearchOpen = false;
    }

    private void hideSearchInput() {
        View textChercherTV = findViewById(R.id.textChercher_TV);
        View searchInputET = findViewById(R.id.searchInput_ET);
        View effacerRechercheIV = findViewById(R.id.effacerRecherche_IV);
        View chevronRecherche = findViewById(R.id.chevronRecherche);

        if (textChercherTV != null) textChercherTV.setVisibility(View.VISIBLE);
        if (searchInputET != null) {
            searchInputET.setVisibility(View.GONE);
            ((android.widget.EditText) searchInputET).setText("");
        }
        if (effacerRechercheIV != null) effacerRechercheIV.setVisibility(View.GONE);
        if (chevronRecherche != null) chevronRecherche.setVisibility(View.VISIBLE);

        // Hide keyboard
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && searchInputET != null) {
            imm.hideSoftInputFromWindow(searchInputET.getWindowToken(), 0);
        }
    }
}
