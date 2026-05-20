package fr.alcyons.phiwms_mobile.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.vision.L;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.DetailControleDesRetoursActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ControleDesRetoursAdapter;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceControleRetoursActivity extends ServiceAvecConnexionActivity
{
    private Context context = null;
    private List<Retour> retourList = null;
    private List<Retour> retourListBase = null;
    private List<String> listeDepotLivraison = null;
    private ListView retourListView = null;
    private ControleDesRetoursAdapter controleDesRetoursAdapter = null;
    private ArrayAdapter<String> autoCompleteAdapter = null;
    private AutoCompleteTextView autoComplete = null;
    private PackageManager pm = null;
    private boolean connexionDirecte = false;
    private ActivityResultLauncher<Intent> resultScanDocument = null;

    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);
        this.retourList = new ArrayList<>();
        this.retourListBase = new ArrayList<>();
        this.listeDepotLivraison = new ArrayList<>();
        this.context = ServiceControleRetoursActivity.this;
        this.pm = ServiceControleRetoursActivity.this.getPackageManager();

        // Initialisation de la liste et de l'action sur l'un de ses items
        this.retourListView = this.findViewById(R.id.listeView);
        this.retourListView.setOnItemClickListener((parent, view, position, id) -> {
            final Retour retourSelectionne = (Retour) this.controleDesRetoursAdapter.getItem(position);

            final Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
            assert null != retourSelectionne;
            serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

            final Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailControleDesRetoursActivity.class);
            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
            ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
            ServiceControleRetoursActivity.this.finish();
        });

        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db);

        this.resultScanDocument = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            final Intent data = result.getData();
            if (Activity.RESULT_OK == result.getResultCode())
            {
                if (null != data)
                {
                    final String code = Objects.requireNonNull(data.getExtras()).getString("code");
                    if (null != code)
                    {
                        final Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(this.db, code);
                        if (null == retourSelectionne)
                        {
                            this.afficherSnackBarControleDesRetours();
                            /* Code nécessaire à l'affichage de la liste */
                            this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                            this.retourListView.setAdapter(this.controleDesRetoursAdapter);

                            if (this.retourList.isEmpty())
                            {
                                MenuActivity.vide = Boolean.TRUE;
                                MenuActivity.nomServiceVide = "Contrôle des retours";
                                ServiceControleRetoursActivity.this.finish();
                            }
                            else { this.invalidateOptionsMenu(); }
                        }
                        else
                        {
                            final Bundle serviceControleRetours_Bundle = ServiceControleRetoursActivity.super.getBundle();
                            serviceControleRetours_Bundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            final Intent serviceControleRetours_Intent = new Intent(ServiceControleRetoursActivity.this, DetailControleDesRetoursActivity.class);
                            serviceControleRetours_Intent.putExtras(serviceControleRetours_Bundle);
                            ServiceControleRetoursActivity.this.startActivity(serviceControleRetours_Intent);
                            ServiceControleRetoursActivity.this.finish();
                        }
                    }
                    else
                    {
                        /* Code nécessaire à l'affichage de la liste */
                        this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                        this.retourListView.setAdapter(this.controleDesRetoursAdapter);

                        if (this.retourList.isEmpty())
                        {
                            MenuActivity.vide = Boolean.TRUE;
                            MenuActivity.nomServiceVide = "Contrôle des retours";
                            ServiceControleRetoursActivity.this.finish();
                        }
                        else { this.invalidateOptionsMenu(); }
                    }
                }
                else
                {
                    /* Code nécessaire à l'affichage de la liste */
                    this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                    this.retourListView.setAdapter(this.controleDesRetoursAdapter);

                    if (this.retourList.isEmpty())
                    {
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Contrôle des retours";
                        ServiceControleRetoursActivity.this.finish();
                    }
                    else { this.invalidateOptionsMenu(); }
                }
            }
        });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { ServiceControleRetoursActivity.this.retourNavigation(); }
        });
    }

    @Override public void onResume()
    {
        super.onResume();
        if (MenuActivity.statutConnexion && this.passageParOnCreate && !this.connexionDirecte)
        {
            if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceControleRetoursActivity.this, LayoutInflater.from(ServiceControleRetoursActivity.this));}

            final RequestQueue requestQueue = Volley.newRequestQueue(ServiceControleRetoursActivity.this);
            final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteControleRetours;

            final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            this.retourList = RetourOpenHelper.getRetoursByEnAttenteDe(this.db, this.getString(R.string.RepriseDemandee));
            this.retourListBase = new ArrayList<>(this.retourList);
            this.populateDepotList(this.retourListBase);
            if (this.retourList.isEmpty())
            {
                if(this.connexionDirecte)
                {
                    final Intent retourVersServiceConnexionDirectIntent = this.getRetourVersServiceConnexionDirectIntent();
                    ServiceControleRetoursActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceControleRetoursActivity.this.finish();
                }
                else
                {
                    this.connexionNecessaire();
                    return;
                }
            }
            else
            {
                this.passageParOnCreate = false;
                if(this.connexionDirecte)
                {
                    /* Code nécessaire à l'affichage de la liste */
                    this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                    this.retourListView.setAdapter(this.controleDesRetoursAdapter);
                    this.initializeAutoComplete();

                    if (this.retourList.isEmpty())
                    {
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Contrôle des retours";
                        ServiceControleRetoursActivity.this.finish();
                    }
                    else
                    {
                        this.invalidateOptionsMenu();
                        this.connexionDirecte = !this.connexionDirecte;
                    }
                }
            }
        }

        this.invalidateOptionsMenu();
    }

    private void populateDepotList(final List<Retour> retoursSource)
    {
        this.listeDepotLivraison.clear();
        this.listeDepotLivraison.add("Tous");
        for (final Retour retour : retoursSource)
        {
            final String depotOrigine = this.getDepotLivraison(retour);
            if (!this.listeDepotLivraison.contains(depotOrigine)) { this.listeDepotLivraison.add(depotOrigine); }
        }
    }

    private void initializeAutoComplete()
    {
        this.autoComplete = this.findViewById(R.id.listeFiltre);
        if (null == this.autoComplete) { return; }

        this.listeDepotLivraison.sort((a, b) -> {
            if (a.equals("Tous")) return -1;
            if (b.equals("Tous")) return 1;
            return a.compareTo(b);
        });

        this.autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, this.listeDepotLivraison);
        this.autoComplete.setAdapter(this.autoCompleteAdapter);
        this.autoComplete.setThreshold(100);
        if (!this.listeDepotLivraison.isEmpty()) { this.autoComplete.setText(this.listeDepotLivraison.get(0), false); }

        final int hauteurEcran = this.getResources().getDisplayMetrics().heightPixels;
        this.autoComplete.setDropDownHeight(hauteurEcran / 3);
        this.autoComplete.setDropDownBackgroundResource(android.R.color.white);
        this.autoComplete.post(() -> {
            final int dpToPx = (int) (12.0F * this.getResources().getDisplayMetrics().density);
            this.autoComplete.setDropDownWidth(this.findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx);
        });

        this.autoComplete.setOnClickListener(v -> this.autoComplete.showDropDown());
        this.findViewById(R.id.chevronFiltre).setOnClickListener(v -> this.autoComplete.showDropDown());
        this.autoComplete.setOnItemClickListener((parent, view, position, id) -> this.filterRetoursByDepot(position));
    }

    private void filterRetoursByDepot(final int position)
    {
        final String depotNom = this.listeDepotLivraison.get(position);
        this.autoComplete.setText(depotNom, false);
        this.autoComplete.dismissDropDown();

        this.retourList = new ArrayList<>();
        if (depotNom.contentEquals("Tous")) { this.retourList.addAll(this.retourListBase); }
        else
        {
            for (final Retour retourCourant : this.retourListBase)
            {
                if (this.getDepotLivraison(retourCourant).contentEquals(depotNom)) { this.retourList.add(retourCourant); }
            }
        }

        this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
        this.retourListView.setAdapter(this.controleDesRetoursAdapter);
        this.invalidateOptionsMenu();
    }

    private String getDepotLivraison(final Retour retour)
    {
        final String[] intitule_tab = retour.getIntitule().split(":");
        String depotOrigine = intitule_tab[0];
        if (retour.getRef_Depot_Origine().contains("-PAD-") && this.utilisateurConnecte.getIdentifiant().toLowerCase().contains("alcyons")) { depotOrigine = "XXX-PAD-XXX"; }
        return depotOrigine;
    }

    @NonNull private Intent getRetourVersServiceConnexionDirectIntent()
    {
        final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceControleRetoursActivity.this, ServiceConnexionDirecteActivity.class);
        final Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Quarantaine");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @NonNull private JsonObjectRequest getJsonObjectRequest(final String urlRequete)
    {
        return new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
            try
            {
                final int resultCount = response.getInt("resultCount");
                if (0 == resultCount)
                {
                    final String erreur = response.getString("erreur");
                    if (erreur.equals(this.context.getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(this.context, this.getLayoutInflater(), "Alerte", "Votre session est invalide, veuillez vous reconnecter.", false, true); }
                    else if (erreur.equals(this.context.getString(R.string.tokenExpire))) { Alerte.afficherAlerteInformation(this.context, this.getLayoutInflater(), "Alerte", "Votre session est expirée, veuillez vous reconnecter.", false, true); }
                    else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) { Alerte.afficherAlerteInformation(this.context, this.getLayoutInflater(), "Alerte", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", false, true); }
                    else
                    {
                        this.arreterSpinner();
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Contrôle des retours";
                        this.retourNavigation();
                    }
                }
                else
                {
                    final JSONArray retoursJSONArray = response.getJSONArray("PH_Retours");
                    this.viderTablesConcernees();
                    this.retourList.clear();
                    this.retourListBase.clear();
                    this.listeDepotLivraison.clear();
                    this.listeDepotLivraison.add("Tous");
                    long rowID;
                    for (int i = 0; i < retoursJSONArray.length(); i++)
                    {
                        final JSONObject retourJSONObject = retoursJSONArray.getJSONObject(i);
                        final Retour retour = new Retour(retourJSONObject);

                        if (retour.getEn_Attente_de().equals(this.getString(R.string.RepriseDemandee)))
                        {
                            rowID = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
                            if (-1L != rowID)
                            {
                                this.retourList.add(retour);
                                this.retourListBase.add(retour);
                                final String depotOrigine = this.getDepotLivraison(retour);
                                if (!this.listeDepotLivraison.contains(depotOrigine)) { this.listeDepotLivraison.add(depotOrigine); }

                                final JSONArray retourLignesJSONArray= retourJSONObject.getJSONArray("ph_retour_ligne");
                                for (int k = 0; k < retourLignesJSONArray.length(); k++) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, new Retour_Ligne(retourLignesJSONArray.getJSONObject(k))); }
                            }
                        }
                    }

                    final Retour retour_alcyons = RetourOpenHelper.getRetourEssaiAlcyons(this.db);
                    if(null != retour_alcyons)
                    {
                        this.retourList.add(retour_alcyons);
                        this.retourListBase.add(retour_alcyons);
                        final String depotOrigine = this.getDepotLivraison(retour_alcyons);
                        if (!this.listeDepotLivraison.contains(depotOrigine)) { this.listeDepotLivraison.add(depotOrigine); }
                    }

                    //lancerScan();
                    /* Code nécessaire à l'affichage de la liste */
                    this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                    this.retourListView.setAdapter(this.controleDesRetoursAdapter);
                    this.initializeAutoComplete();

                    if (this.retourList.isEmpty())
                    {
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Contrôle des retours";
                        ServiceControleRetoursActivity.this.finish();
                    }
                    else
                    {
                        this.onClickTriNumero();
                        this.invalidateOptionsMenu();
                    }
                    this.passageParOnCreate = false;
                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                }
            }
            catch (final Throwable t) { Log.e(L.TAG, "Error JSON", t); }
        }, error -> {
            // TODO: Handle error
            Log.e("Volley", "Error");
            Alerte.afficherAlerteInformation(this.context, this.getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", false, true);
        })
        {
            @Override public Map<String, String> getHeaders()
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceControleRetoursActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };
    }
    @Override public boolean onPrepareOptionsMenu(final Menu menu)
    {
        super.prepareOptionsMenu(menu, this.controleDesRetoursAdapter, null, "Produit, Intitulé, N°...");
        final MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            this.lancerScan();
            return true;
        });
        return true;
    }

    public void viderTablesConcernees()
    {
        for (final Retour retour : RetourOpenHelper.getRetoursByEnAttenteDe(this.db, this.getString(R.string.RepriseDemandee)))
        {
            if(!retour.getIntitule().contentEquals("Retour_ALCYONS"))
            {
                for (final Retour_Ligne retourLigne : Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, retour))
                {
                    Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne);
                    final Produit produit = ProduitOpenHelper.getProduitByID(this.db, retourLigne.getCode_produit());
                    final Depot depot = DepotOpenHelper.getDepotParReference(this.db, retour.getRef_Depot_Origine());

                    if(null != produit && null != depot)
                    {
                        for (final Stock_Lot_Emplacement_Light stockLotEmplacementLight : Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(this.db, produit, depot))
                        {
                            //Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
                        }
                    }
                }

                RetourOpenHelper.supprimerUnRetour(this.db, retour);
            }
        }
    }
    @Override public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }

    public void lancerScan()
    {
        final Bundle scanDocumentBundle = ServiceControleRetoursActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        scanDocumentBundle.putBoolean("modeRafale", false);

        final Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceControleRetoursActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }


        scanDocumentIntent.putExtras(scanDocumentBundle);
        this.resultScanDocument.launch(scanDocumentIntent);
    }
    private void onClickTriNumero()
    {
        this.retourList.sort(Comparator.comparing(Retour::getNumero));

        this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
        this.retourListView.setAdapter(this.controleDesRetoursAdapter);
    }

    public void onClickTriDate()
    {
        this.retourList.sort(Comparator.comparing(Retour::getDate_retour));

        this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
        this.retourListView.setAdapter(this.controleDesRetoursAdapter);
    }

    public void onClickTriDepot()
    {
        this.retourList.sort(Comparator.comparing(Retour::getRef_Depot_Origine));

        this.controleDesRetoursAdapter = new ControleDesRetoursAdapter(ServiceControleRetoursActivity.this, this.db, this.retourList, this.utilisateurConnecte);
        this.retourListView.setAdapter(this.controleDesRetoursAdapter);
    }

    private void afficherSnackBarControleDesRetours()
    {
        final Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), BaseTransientBottomBar.LENGTH_LONG);

         final Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(this.getResources().getColor(R.color.rouge2, null));
        final TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }
}
