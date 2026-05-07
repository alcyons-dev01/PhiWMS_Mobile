package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourPUI.DetailRetourPUIActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceRetourPUIActivity extends ServiceAvecConnexionActivity
{
    private List<Retour> retourList = null;
    private ListView retourListView = null ;
    private RetourPUIAdapter adapter = null ;
    private PackageManager pm = null ;
    private JSONArray retourJSONArray = null ;
    private NavigationActivity navigationActivity = null ;
    private Context context = null ;
    private boolean connexionDirecte = false ;
    private ActivityResultLauncher<Intent> resultScanDocument = null ;

    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);

        // Modification du titre
        this.pm = ServiceRetourPUIActivity.this.getPackageManager();
        // Gestion de la listView
        this.retourListView = (ListView) findViewById(R.id.listeView);
        this.retourListView.setOnItemClickListener((parent, view, position, id) -> {
            final Retour retourSelectionne = (Retour) this.adapter.getItem(position);

            final Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
            final Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
            assert null != retourSelectionne;
            serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

            serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
            ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
            ServiceRetourPUIActivity.this.finish();
        });

        this.navigationActivity = new NavigationActivity();
        this.context = this.navigationActivity;

        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db).booleanValue();

        this.resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    final Intent data = result.getData();
                    if (CodesEchangesActivites.RESULT_OK == result.getResultCode())
                    {
                        if (null != data)
                        {
                            final String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (null != code) {
                                final Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(this.db, code);
                                if (null == retourSelectionne)
                                {
                                    if (!code.contentEquals("")) {this.afficherSnackBarRetourPUI();}
                                    /* Code nécessaire à l'affichage de la liste */
                                    this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                                    this.retourListView.setAdapter(this.adapter);

                                    if (this.retourList.isEmpty())
                                    {
                                        MenuActivity.vide = Boolean.TRUE;
                                        MenuActivity.nomServiceVide = "Retour PUI";
                                        ServiceRetourPUIActivity.this.finish();
                                    }

                                    this.invalidateOptionsMenu();
                                }
                                else
                                {
                                    final Intent serviceRetourPuiIntent = new Intent(ServiceRetourPUIActivity.this, DetailRetourPUIActivity.class);
                                    final Bundle serviceRetourPuiBundle = ServiceRetourPUIActivity.super.getBundle();
                                    serviceRetourPuiBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    serviceRetourPuiIntent.putExtras(serviceRetourPuiBundle);
                                    ServiceRetourPUIActivity.this.startActivity(serviceRetourPuiIntent);
                                    ServiceRetourPUIActivity.this.finish();
                                }
                            }
                            else
                            {
                                this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                                this.retourListView.setAdapter(this.adapter);

                                if (this.retourList.isEmpty()) {

                                    MenuActivity.vide = Boolean.TRUE;
                                    MenuActivity.nomServiceVide = "Retour PUI";
                                    ServiceRetourPUIActivity.this.finish();
                                }

                                this.invalidateOptionsMenu();
                            }
                        }
                        else
                        {
                            /* Code nécessaire à l'affichage de la liste */
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.retourList.size()));
                            this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                            this.retourListView.setAdapter(this.adapter);

                            if (this.retourList.isEmpty())
                            {
                                MenuActivity.vide = Boolean.TRUE;
                                MenuActivity.nomServiceVide = "Retour PUI";
                                ServiceRetourPUIActivity.this.finish();
                            }

                            this.invalidateOptionsMenu();
                        }
                    }
                });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed()
            {
                final Intent onBackIntent = new Intent(ServiceRetourPUIActivity.this, NavigationActivity.class);
                final Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", ServiceRetourPUIActivity.this.utilisateurConnecte.getId());
                onBackIntent.putExtras(extras);
                ServiceRetourPUIActivity.this.startActivity(onBackIntent);
                ServiceRetourPUIActivity.this.finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override public void onResume()
    {
        super.onResume();
        this.retourList = new ArrayList<>();

        if (MainActivity.statutConnexion && this.passageParOnCreate && !this.connexionDirecte) {

            if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceRetourPUIActivity.this, LayoutInflater.from(ServiceRetourPUIActivity.this)); }

            final RequestQueue requestQueueRetourPUI = Volley.newRequestQueue(ServiceRetourPUIActivity.this);
            final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteRetourPUI;

            final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
            requestQueueRetourPUI.add(obreq);
        }
        else
        {
            this.retourList = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande));
            if (this.retourList.isEmpty())
            {
                if(this.connexionDirecte)
                {
                    final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceRetourPUIActivity.this, ServiceConnexionDirecteActivity.class);
                    final Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Retour PUI");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
                    ServiceRetourPUIActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceRetourPUIActivity.this.finish();
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
                    this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                    this.retourListView.setAdapter(this.adapter);

                    if (this.retourList.isEmpty()) {
                        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Retour PUI";
                        ServiceRetourPUIActivity.this.finish();
                    }

                    this.invalidateOptionsMenu();
                    this.connexionDirecte = !this.connexionDirecte;
                }
            }

            this.invalidateOptionsMenu();
            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
        }
    }

    @NonNull private JsonObjectRequest getJsonObjectRequest(final String urlRequete)
    {
        final JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null, response -> {
            try
            {
                final int resultCount = response.getInt("resultCount");
                if (0 == resultCount)
                {
                    final String erreur = response.getString("erreur");
                    if (erreur.equals(getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
                    else if (erreur.equals(getString(R.string.tokenExpire))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter", false, true); }
                    else if (!erreur.equals(getString(R.string.aucunRetour))) { Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete service retour pui", false, true); }
                    else
                    {
                        this.arreterSpinner();
                        Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Alerte", "Aucun Retour PUI à traiter", false, true);
                    }
                }
                else
                {
                    this.viderTablesConcernees();

                    this.retourJSONArray = response.getJSONArray("PH_Retours");

                    for (int i = 0; i < this.retourJSONArray.length(); i++)
                    {
                        final JSONObject retourJSONObject = this.retourJSONArray.getJSONObject(i);
                        final Retour retour = new Retour(retourJSONObject);

                        if (retour.getEn_Attente_de().equals(getString(R.string.RetourPUIDemande)) && retour.getStatut().equals(getString(R.string.statutEncours)))
                        {
                            this.retourList.add(retour);
                            final long rowID = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
                            if (rowID != -1L)
                            {
                                final JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");
                                for (int k = 0; k < retourLigneJSONArray.length(); k++)
                                {
                                    final JSONObject retourLigneJSONObject = retourLigneJSONArray.getJSONObject(k);
                                    final Retour_Ligne retourLigne = new Retour_Ligne(retourLigneJSONObject);
                                    Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, retourLigne);
                                }
                            }
                        }
                    }

                    if (this.retourList.isEmpty())
                    {
                        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                        MenuActivity.vide = true;
                        MenuActivity.nomServiceVide = "Retour PUI";
                        ServiceRetourPUIActivity.this.finish();
                    }
                    else
                    {
                        this.adapter = new RetourPUIAdapter(ServiceRetourPUIActivity.this, this.db, this.retourList, this.utilisateurConnecte);
                        this.retourListView.setAdapter(this.adapter);
                        new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                        this.passageParOnCreate = false;
                    }

                    this.invalidateOptionsMenu();
                }
            } catch (final JSONException e) { e.printStackTrace(); }
        },
        error -> {
            Log.e("Volley", "Error");
            Alerte.afficherAlerteInformation(ServiceRetourPUIActivity.this, getLayoutInflater(), "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service retour pui", false, true);
        })
        {
            @Override public Map<String, String> getHeaders() throws AuthFailureError
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceRetourPUIActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(this.retryPolicy);
        return obreq;
    }

    public void viderTablesConcernees() {
        for (final Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, getString(R.string.statutEncours), getString(R.string.RetourPUIDemande)))
        {
            final List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(this.db, retour);
            for (final Retour_Ligne retourLigne : retourLigneList) { Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne); }
            RetourOpenHelper.supprimerUnRetour(this.db, retour);
        }
    }

    public void lancerScan()
    {
        final Bundle scanDocumentBundle = ServiceRetourPUIActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase(Locale.ROOT).contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceRetourPUIActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        this.resultScanDocument.launch(scanDocumentIntent);
    }

    private void afficherSnackBarRetourPUI()
    {
        Snackbar snackbar = null;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        final TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }
}
