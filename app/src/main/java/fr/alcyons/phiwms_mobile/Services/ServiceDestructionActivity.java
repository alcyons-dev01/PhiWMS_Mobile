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
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceDestructionActivity extends ServiceAvecConnexionActivity
{
    // OTHERS
    JSONArray retoursJson;
    List<Retour> listeRetours;
    PackageManager pm;
    ActivityResultLauncher<Intent> resultScanDocument;
    boolean connexionDirecte;

    // UI
    RetourAdapter adapter;
    ListView listViewRetours;


    @SuppressLint("SetTextI18n")
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        this.pm = ServiceDestructionActivity.this.getPackageManager();

        // Affichage des informations de base
        this.findViewById(R.id.triListe).setVisibility(ListView.GONE);

        // Gestion de la listView
        this.listViewRetours = this.findViewById(R.id.listeView);
        this.listViewRetours.setOnItemClickListener((parent, view, position, id) -> {
            Retour retourSelectionne = (Retour) this.adapter.getItem(position);

            Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
            assert retourSelectionne != null;

            Bundle extras = ServiceDestructionActivity.super.getBundle();
            extras.putInt("retourSelectionneID", retourSelectionne.get_UID());
            newIntent.putExtras(extras);
            ServiceDestructionActivity.this.startActivity(newIntent);
            ServiceDestructionActivity.this.finish();
        });

        this.connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(this.db);

        this.resultScanDocument = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RESULT_OK)
                    {
                        if (data != null)
                        {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null)
                            {
                                Retour retourSelectionne = RetourOpenHelper.getRetourByNumero(this.db, code);
                                if (retourSelectionne == null)
                                {
                                    if (!code.contentEquals("")) { this.afficherSnackBarDestruction(); }

                                    ((TextView) this.findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listeRetours.size()));
                                    adapter = new RetourAdapter(ServiceDestructionActivity.this, db, listeRetours, utilisateurConnecte);
                                    listViewRetours.setDivider(footer);
                                    listViewRetours.setAdapter(adapter);

                                    if (this.listeRetours.isEmpty())
                                    {
                                        ServiceDestructionActivity.vide = true;
                                        ServiceDestructionActivity.nomServiceVide = "Destruction";
                                        ServiceDestructionActivity.this.finish();
                                    }

                                    this.invalidateOptionsMenu();
                                }
                                else
                                {
                                    Intent newIntent = new Intent(ServiceDestructionActivity.this, DetailDestructionActivity.class);
                                    Bundle extras = ServiceDestructionActivity.super.getBundle();
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
                                    ServiceDestructionActivity.vide = true;
                                    ServiceDestructionActivity.nomServiceVide = "Destruction";
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
                                ServiceDestructionActivity.vide = true;
                                ServiceDestructionActivity.nomServiceVide = "Destruction";
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
                Intent intent = new Intent(ServiceDestructionActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceDestructionActivity.this.startActivity(intent);
                ServiceDestructionActivity.this.finish();
            }
        });
    }

    @Override public void onResume()
    {
        super.onResume();

        if (ServiceDestructionActivity.statutConnexion && this.passageParOnCreate && !this.connexionDirecte)
        {

            if (!this.swipeRefreshLayout.isRefreshing()) { this.afficherSpinner(ServiceDestructionActivity.this, LayoutInflater.from(ServiceDestructionActivity.this)); }

            this.listeRetours = new ArrayList<>();

            RequestQueue requestQueueDestructionUtilisateur = Volley.newRequestQueue(ServiceDestructionActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDestruction;
            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueueDestructionUtilisateur.add(obreq);

        }
        else
        {
            this.listeRetours = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, getString(R.string.statutEncours), getString(R.string.DestructionDemandee));
            if (this.listeRetours.isEmpty())
            {
                if(this.connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
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
                        ServiceDestructionActivity.vide = true;
                        ServiceDestructionActivity.nomServiceVide = "Destruction";
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
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceDestructionActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", this.utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Destruction");
        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);

        return retourVersServiceConnexionDirectIntent;
    }
    @NonNull private JsonObjectRequest getJsonObjectRequest(String urlRequete)
    {
        // Takes the response from the JSON request
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
                    try
                    {
                        int nbResultat = response.getInt("resultCount");
                        if (nbResultat == 0)
                        {
                            String string = response.getString("erreur");
                            if (string.equals(getString(R.string.tokenInvalide))) { Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, getLayoutInflater(), "Alerte", "Votre session a expirée, veuillez vous reconnecter.", false, true); }
                            else
                            {
                                this.arreterSpinner();
                                ServiceDestructionActivity.vide = true;
                                ServiceDestructionActivity.nomServiceVide = "Destruction";
                                this.retourNavigation();
                            }
                        }
                        else
                        {
                            this.viderTablesConcernees();

                            this.retoursJson = response.getJSONArray("PH_Retours");

                            for (int i = 0; i < this.retoursJson.length(); i++)
                            {
                                JSONObject retourJson = this.retoursJson.getJSONObject(i);
                                Retour retour = new Retour(retourJson);

                                if (retour.getEn_Attente_de().equals(getString(R.string.DestructionDemandee)) && retour.getStatut().equals(getString(R.string.statutEncours)))
                                {
                                    this.listeRetours.add(retour);
                                    long rowID = RetourOpenHelper.insererUnRetourEnBDD(this.db, retour);
                                    if (rowID != -1)
                                    {
                                        JSONArray retourLignesJson = retourJson.getJSONArray("ph_retour_ligne");
                                        for (int k = 0; k < retourLignesJson.length(); k++) { Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, new Retour_Ligne(retourLignesJson.getJSONObject(k))); }
                                    }
                                }
                            }

                            if (this.listeRetours.isEmpty())
                            {
                                ServiceDestructionActivity.vide = true;
                                ServiceDestructionActivity.nomServiceVide = "Destruction";
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

                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    }
                    catch (JSONException e) { e.printStackTrace(); }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ServiceDestructionActivity.this, getLayoutInflater(),"Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Destruction)", false, true);
                }
        )
        {

            /**
             * Passing some request headers
             */
            @Override public Map<String, String> getHeaders()
            {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", ServiceDestructionActivity.this.utilisateurConnecte.getToken());
                return headers;
            }
        };

        obreq.setRetryPolicy(new RetryPolicy() {
            @Override public int getCurrentTimeout() { return 50000; }
            @Override public int getCurrentRetryCount() { return 50000; }
            @Override public void retry(VolleyError error) {}
        });

        return obreq;
    }

    public void viderTablesConcernees()
    {
        for (Retour retour : RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(this.db, getString(R.string.statutEncours), getString(R.string.DestructionDemandee)))
        {
            List<Retour_Ligne> retourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, retour);
            for (Retour_Ligne retourLigne : retourLignes) { Retour_LigneOpenHelper.supprimerUnRetourLigne(this.db, retourLigne); }
            RetourOpenHelper.supprimerUnRetour(this.db, retour);
        }
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.prepareOptionsMenu(menu, this.adapter, null, "Produit, Intitulé, N°...");

        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> true);

        MenuItem itemScan = menu.findItem(R.id.menuDatamatrix);
        itemScan.setOnMenuItemClickListener(menuItem -> {
            this.lancerScan();
            return true;
        });

        return true;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);

        return true;
    }

    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceDestructionActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent scanDocumentIntent;
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
    public void afficherSnackBarDestruction()
    {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
