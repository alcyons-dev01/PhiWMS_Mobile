package fr.alcyons.phiwms_mobile.Services;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import fr.alcyons.phiwms_mobile.Destruction.DetailDestructionActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDestructionActivity extends ServiceAvecConnexionActivity
{
    // OTHERS
    private JSONArray retoursJson = null;
    private List<Retour> listeRetours = null;
    private PackageManager pm = null;
    private ActivityResultLauncher<Intent> resultScanDocument = null;
    private boolean connexionDirecte = false;

    // UI
    private RetourAdapter adapter = null;
    private ListView listViewRetours = null;


    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_liste_refresh);

        this.pm = ServiceDestructionActivity.this.getPackageManager();

        // Affichage des informations de base
        this.findViewById(R.id.triListe).setVisibility(View.GONE);

        // Gestion de la listView
        this.listViewRetours = this.findViewById(R.id.listeView);
        this.listViewRetours.setOnItemClickListener((parent, view, position, id) -> {
            final Retour retourSelectionne = (Retour) this.adapter.getItem(position);

            final Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
            assert null != retourSelectionne;

            final Bundle extras = ServiceDestructionActivity.super.getBundle();
            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());
            newIntent.putExtras(extras);
            ServiceDestructionActivity.this.startActivity(newIntent);
            ServiceDestructionActivity.this.finish();
        });

        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db).booleanValue();

        this.resultScanDocument = this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            final Intent data = result.getData();
            if (CodesEchangesActivites.RESULT_OK == result.getResultCode())
            {
                if (null != data)
                {
                    final String code = Objects.requireNonNull(data.getExtras()).getString("code");
                    if (null != code)
                    {
                        final Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(this.db, code);
                        if (null == retourSelectionne)
                        {
                            if (!code.contentEquals("")) { this.afficherSnackBarDestruction(); }

                            ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.listeRetours.size()));
                            this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.listeRetours, this.utilisateurConnecte);
                            this.listViewRetours.setDivider(this.footer);
                            this.listViewRetours.setAdapter(this.adapter);

                            if (this.listeRetours.isEmpty())
                            {
                                MenuActivity.vide = Boolean.TRUE;
                                MenuActivity.nomServiceVide = "Destruction";
                                ServiceDestructionActivity.this.finish();
                            }

                            this.invalidateOptionsMenu();
                        }
                        else
                        {
                            final Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
                            final Bundle extras = ServiceDestructionActivity.super.getBundle();
                            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());

                            newIntent.putExtras(extras);
                            ServiceDestructionActivity.this.startActivity(newIntent);
                            ServiceDestructionActivity.this.finish();
                        }

                    }
                    else
                    {
                        ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.listeRetours.size()));
                        ((TextView) this.findViewById(R.id.titre)).setText("Demandes de destruction");

                        this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.listeRetours, this.utilisateurConnecte);
                        this.listViewRetours.setDivider(this.footer);
                        this.listViewRetours.setAdapter(this.adapter);

                        if (this.listeRetours.isEmpty())
                        {
                            MenuActivity.vide = Boolean.TRUE;
                            MenuActivity.nomServiceVide = "Destruction";
                            ServiceDestructionActivity.this.finish();
                        }

                        this.invalidateOptionsMenu();
                    }
                }
                else
                {
                    ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.listeRetours.size()));
                    ((TextView) this.findViewById(R.id.titre)).setText("Demandes de destruction");

                    this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.listeRetours, this.utilisateurConnecte);
                    this.listViewRetours.setDivider(this.footer);
                    this.listViewRetours.setAdapter(this.adapter);

                    if (this.listeRetours.isEmpty())
                    {
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Destruction";
                        ServiceDestructionActivity.this.finish();
                    }

                    this.invalidateOptionsMenu();
                }
            }
        });

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override public void handleOnBackPressed()
            {
                final Intent intent = new Intent(ServiceDestructionActivity.this, NavigationActivity.class);
                final Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", ServiceDestructionActivity.this.utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceDestructionActivity.this.startActivity(intent);
                ServiceDestructionActivity.this.finish();
            }
        });
    }

    @Override public void onResume()
    {
        super.onResume();

        if (MainActivity.statutConnexion && this.passageParOnCreate && !this.connexionDirecte)
        {

            if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceDestructionActivity.this, LayoutInflater.from(ServiceDestructionActivity.this)); }

            this.listeRetours = new ArrayList<>();

            final RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceDestructionActivity.this);
            final String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(this.db) + DBOpenHelper.Urls.uriRequeteDestruction;
            final JsonObjectRequest obreq = this.getJsonObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);

        }
        else
        {
            this.listeRetours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.DestructionDemandee));
            if (this.listeRetours.isEmpty())
            {
                if(this.connexionDirecte)
                {
                    final Intent retourVersServiceConnexionDirectIntent = this.getRetourVersServiceConnexionDirectIntent();
                    ServiceDestructionActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceDestructionActivity.this.finish();
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
                    ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.listeRetours.size()));
                    ((TextView) this.findViewById(R.id.titre)).setText("Demandes de destruction");

                    this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.listeRetours, this.utilisateurConnecte);
                    this.listViewRetours.setDivider(this.footer);
                    this.listViewRetours.setAdapter(this.adapter);

                    if (this.listeRetours.isEmpty())
                    {
                        MenuActivity.vide = Boolean.TRUE;
                        MenuActivity.nomServiceVide = "Destruction";
                        ServiceDestructionActivity.this.finish();
                    }

                    this.connexionDirecte = !this.connexionDirecte;
                    this.invalidateOptionsMenu();
                }
            }
        }

        this.invalidateOptionsMenu();
    }

    @NonNull private Intent getRetourVersServiceConnexionDirectIntent()
    {
        final Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceDestructionActivity.this, ServiceConnexionDirecteActivity.class);
        final Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Destruction");
        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);

        return retourVersServiceConnexionDirectIntent;
    }
    @NonNull private JsonObjectRequest getJsonObjectRequest(final String urlRequete)
    {
        // Takes the response from the JSON request
        final JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
                    try
                    {
                        final int nbResultat = response.getInt("resultCount");
                        if (0 == nbResultat)
                        {
                            final String chaine = response.getString("erreur");
                            if (chaine.equals(this.getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, this.getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
                            else
                            {
                                this.arreterSpinner();
                                MenuActivity.vide = Boolean.TRUE;
                                MenuActivity.nomServiceVide = "Destruction";
                                this.retourNavigation();
                            }
                        }
                        else
                        {
                            this.viderTablesConcernees();

                            this.retoursJson = response.getJSONArray("PH_Retours");

                            for (int i = 0; i < this.retoursJson.length(); i++)
                            {
                                final JSONObject retourJson = this.retoursJson.getJSONObject(i);
                                final Retour retour = new Retour(retourJson);

                                if (retour.getEn_Attente_de().equals(this.getString(R.string.DestructionDemandee)) && retour.getStatut().equals(this.getString(R.string.statutEncours)))
                                {
                                    this.listeRetours.add(retour);
                                    final long rowID = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
                                    if (-1L != rowID)
                                    {
                                        final JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");
                                        for (int k = 0; k < retourLignesJson.length(); k++) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, new Retour_Ligne(retourLignesJson.getJSONObject(k))); }
                                    }
                                }
                            }

                            if (this.listeRetours.isEmpty())
                            {
                                MenuActivity.vide = Boolean.TRUE;
                                MenuActivity.nomServiceVide = "Destruction";
                                ServiceDestructionActivity.this.finish();
                            }
                            else
                            {
                                ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(this.listeRetours.size()));
                                ((TextView) this.findViewById(R.id.titre)).setText("Demandes de destruction");

                                this.adapter = new RetourAdapter(ServiceDestructionActivity.this, this.db, this.listeRetours, this.utilisateurConnecte);
                                this.listViewRetours.setDivider(this.footer);
                                this.listViewRetours.setAdapter(this.adapter);

                                this.passageParOnCreate = false;
                                this.invalidateOptionsMenu();
                            }

                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500L);
                        }
                    }
                    catch (final JSONException e) { e.printStackTrace(); }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, this.getLayoutInflater(),"Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Destruction)", false, true);
                }
        )
        {

            /**
             * Passing some request headers
             */
            @Override public Map<String, String> getHeaders()
            {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceDestructionActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override public int getCurrentTimeout() { return 50000; }
            @Override public int getCurrentRetryCount() { return 50000; }
            @Override public void retry(final VolleyError error) {}
        });

        return obreq;
    }

    public final void viderTablesConcernees()
    {
        for (final Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, this.getString(R.string.statutEncours), this.getString(R.string.DestructionDemandee)))
        {
            final List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, retour);
            for (final Retour_Ligne retourLigne : retourLignes) { Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne); }
            RetourOpenHelper.supprimerUnRetour(this.db, retour);
        }
    }

    @Override public boolean onPrepareOptionsMenu(final Menu menu)
    {
        super.prepareOptionsMenu(menu, this.adapter, null, "Produit, Intitulé, N°...");

        final MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> true);

        final MenuItem itemScan = menu.findItem(R.id.menuDatamatrix);
        itemScan.setOnMenuItemClickListener(menuItem -> {
            this.lancerScan();
            return true;
        });

        return true;
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);

        return true;
    }

    public void lancerScan()
    {
        final Bundle scanDocumentBundle = ServiceDestructionActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        final Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            scanDocumentIntent = new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { scanDocumentIntent = new Intent(ServiceDestructionActivity.this, BarcodeCaptureActivity.class); }
            else
            {
                scanDocumentIntent = new Intent(ServiceDestructionActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        this.resultScanDocument.launch(scanDocumentIntent);
    }
    private final void afficherSnackBarDestruction()
    {
        final Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), BaseTransientBottomBar.LENGTH_LONG);

        @SuppressLint("RestrictedApi")
        final Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(this.getResources().getColor(R.color.rouge2, null));
        final TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }
}
