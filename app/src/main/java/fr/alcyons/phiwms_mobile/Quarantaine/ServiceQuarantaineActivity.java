package fr.alcyons.phiwms_mobile.Quarantaine;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.SYS_User_RulesOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.SYS_User_Rules;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ServiceControleRetoursActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourAdapter;

import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 16/04/2024.
 */

public class ServiceQuarantaineActivity extends ServiceAvecConnexionActivity {
    Retour retourSelectionne;
    PackageManager pm;
    List<Retour> retourList;
    ListView retourListView;
    RetourAdapter retourAdapter;
    JSONArray retourJSONArray;
    Context context;
    boolean connexionDirecte;
    ActivityResultLauncher<Intent> resultScanDocument;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        context = ServiceQuarantaineActivity.this;
        ((TextView) findViewById(R.id.titre)).setText("Demandes de quarantaine");
        pm = ServiceQuarantaineActivity.this.getPackageManager();
        SYS_User_Rules sys_user_rules = SYS_User_RulesOpenHelper.getSYS_User_RulesByUser(db, utilisateurConnecte.getId());

        if(!sys_user_rules.isQuarantaine_Autoriser())
        {
            vide = true;
            nomServiceVide = "QuarantaineNonAcces";
            ServiceQuarantaineActivity.this.finish();
        }

        // Gestion de la listView
        retourListView = findViewById(R.id.listeView);
        retourListView.setOnItemClickListener((parent, view, position, id) -> {
            retourSelectionne = (Retour) retourAdapter.getItem(position);

            Intent serviceQuarantaineIntent = new Intent(ServiceQuarantaineActivity.this, DetailQuarantaineActivity.class);
            Bundle serviceQuarantaineBundle = ServiceQuarantaineActivity.super.getBundle();
            serviceQuarantaineBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

            serviceQuarantaineIntent.putExtras(serviceQuarantaineBundle);
            ServiceQuarantaineActivity.this.startActivity(serviceQuarantaineIntent);
            ServiceQuarantaineActivity.this.finish();
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        resultScanDocument = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RETOUR_DOCUMENT) {
                        if (data != null) {
                            String code = Objects.requireNonNull(data.getExtras()).getString("code");
                            if (code != null) {
                                retourSelectionne = RetourOpenHelper.getRetourByNumero(db, code);
                                if (retourSelectionne == null) {
                                    if (!code.contentEquals("")) {
                                        afficherSnackBarPreparationQuarantaine();
                                    }

                                    //on récuère la quarantaine du jeu d'essai si elle existe
                                    Retour retour_alcyons = RetourOpenHelper.getQuarantaineEssai(db);
                                    if (retour_alcyons != null)
                                        retourList.add(retour_alcyons);

                                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                                    retourList.sort(Comparator.comparing(Retour::getDate_retour));
                                    retourAdapter = new RetourAdapter(ServiceQuarantaineActivity.this, db, retourList);
                                    retourListView.setDivider(footer);

                                    retourListView.setAdapter(retourAdapter);
                                    if (retourList.isEmpty()) {
                                        vide = true;
                                        nomServiceVide = "Quarantaine";
                                        ServiceQuarantaineActivity.this.finish();
                                    }
                                } else {
                                    Intent serviceQuarantaineIntent = new Intent(ServiceQuarantaineActivity.this, DetailQuarantaineActivity.class);
                                    Bundle serviceQuarantaineBundle = ServiceQuarantaineActivity.super.getBundle();
                                    serviceQuarantaineBundle.putInt("retourSelectionneID", retourSelectionne.get_UID());

                                    serviceQuarantaineIntent.putExtras(serviceQuarantaineBundle);
                                    ServiceQuarantaineActivity.this.startActivity(serviceQuarantaineIntent);
                                    ServiceQuarantaineActivity.this.finish();
                                }
                            } else {
                                //on récuère la quarantaine du jeu d'essai si elle existe
                                Retour retour_alcyons = RetourOpenHelper.getQuarantaineEssai(db);
                                if (retour_alcyons != null)
                                    retourList.add(retour_alcyons);

                                ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                                retourList.sort(Comparator.comparing(Retour::getDate_retour));
                                retourAdapter = new RetourAdapter(ServiceQuarantaineActivity.this, db, retourList);
                                retourListView.setDivider(footer);

                                retourListView.setAdapter(retourAdapter);
                                if (retourList.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Quarantaine";
                                    ServiceQuarantaineActivity.this.finish();
                                }
                            }
                        } else {
                            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                            retourList.sort(Comparator.comparing(Retour::getDate_retour));
                            retourAdapter = new RetourAdapter(ServiceQuarantaineActivity.this, db, retourList);
                            retourListView.setDivider(footer);

                            retourListView.setAdapter(retourAdapter);
                            if (retourList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Quarantaine";
                                ServiceQuarantaineActivity.this.finish();
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        retourList = new ArrayList<>();

        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceQuarantaineActivity.this) && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceQuarantaineActivity.this, LayoutInflater.from(ServiceQuarantaineActivity.this));
            }

            RequestQueue requestQueueQuarantaineUtilisateur = Volley.newRequestQueue(ServiceQuarantaineActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteQuarantaine;

            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueQuarantaineUtilisateur.add(obreq);
            try {
                Looper.loop();
            } catch (Throwable e) {
                Log.e(TAG, "Error looper :", e);
            }

            if (retourList.isEmpty()) {
                vide = true;
                nomServiceVide = "Quarantaine";
                ServiceQuarantaineActivity.this.finish();
            }
            else
            {
                if(passageParOnCreate)
                {
                    Retour retour_alcyons = RetourOpenHelper.getQuarantaineEssai(db);
                    if(retour_alcyons != null)
                        retourList.add(retour_alcyons);

                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(retourList.size()));
                    retourList.sort(Comparator.comparing(Retour::getDate_retour));
                    retourAdapter = new RetourAdapter(ServiceQuarantaineActivity.this, db, retourList);
                    retourListView.setDivider(footer);

                    retourListView.setAdapter(retourAdapter);
                }

                passageParOnCreate = false;
            }
            arreterSpinner();
        } else {
            retourList = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.MiseEnQuarantaine));

            if (retourList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceQuarantaineActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceQuarantaineActivity.this.finish();
                }
                else
                {
                    connexionNecessaire();
                    return;
                }
            }
            else if (retourSelectionne != null) {
                if (retourSelectionne.getStatut().equals(getString(R.string.statutValide)) && retourSelectionne.getEn_Attente_de().equals(getString(R.string.Quarantaine))) {
                    retourList.remove(retourSelectionne);
                }
            }
            else
            {
                passageParOnCreate = false;
                if(connexionDirecte)
                {
                    lancerScan();
                    connexionDirecte = !connexionDirecte;
                }
            }
        }

        invalidateOptionsMenu();
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceQuarantaineActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Quarantaine");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @NonNull
    private JsonObjectRequest getObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                DBOpenHelper.viderBasesDeDonnees(db);
                                ServiceQuarantaineActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                ServiceQuarantaineActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Quarantaine", "alerte");
                            }
                        } else {
                            retourJSONArray = response.getJSONArray("PH_Retours");
                            viderTablesConcernees();
                            for (int i = 0; i < retourJSONArray.length(); i++)
                            {
                                JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);

                                if (retourJSONObject.getString("En_Attente_de").equals("Mise en quarantaine"))
                                {
                                    Retour retour = new Retour(retourJSONObject);

                                    //check quarantaine existe
                                    Retour quarantaine_existe = RetourOpenHelper.getRetourByID(db, retour.get_UID());
                                    if(quarantaine_existe != null)
                                    {
                                        RetourOpenHelper.supprimerUnRetour(db, quarantaine_existe);
                                    }

                                    retourList.add(retour);
                                    RetourOpenHelper.insererUnRetourEnBDD(db, retour);
                                    JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");

                                    for (int k = 0; k < retourLigneJSONArray.length(); k++)
                                    {
                                        Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLigneJSONArray.getJSONObject(k)));
                                    }
                                }
                            }

                            JSONArray serialisationJSONArray = response.getJSONArray("PH_Serialisation");
                            for(int j = 0; j < serialisationJSONArray.length(); j++)
                            {
                                JSONObject serialisationObject = serialisationJSONArray.getJSONObject(j);
                                PH_Serialisation serialisation = new PH_Serialisation(serialisationObject);
                                PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                            }

                        }
                    } catch (Throwable t) {
                        Log.e(TAG, "Error JSON", t);
                    }
                    handler.sendMessage(handler.obtainMessage());
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerte(context, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Quarantaine", "alerte");
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
        return obreq;
    }

    public void viderTablesConcernees() {
        List<Retour> retourList = RetourOpenHelper.getRetoursByEnAttenteDe(db, getString(R.string.Quarantaine));
        retourList.addAll(RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.MiseEnQuarantaine)));

        for (Retour retour : retourList)
        {
            if(!retour.getIntitule().contentEquals("Quarantaine_ALCYONS"))
            {
                List<Retour_Ligne> retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour);
                for (Retour_Ligne retourLigne : retourLigneList)
                {
                    Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigne);
                }
                RetourOpenHelper.supprimerUnRetour(db, retour);
            }
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, retourAdapter, null, "Produit, Intitulé, N°...");
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        item.setOnMenuItemClickListener(item1 -> {
            lancerScan();
            return true;
        });
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDatamatrix).setVisible(true);
        return true;
    }


    public void lancerScan()
    {
        Bundle scanDocumentBundle = ServiceQuarantaineActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);


        Intent scanDocumentIntent;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceQuarantaineActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceQuarantaineActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceQuarantaineActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        resultScanDocument.launch(scanDocumentIntent);
    }

    public void afficherSnackBarPreparationQuarantaine() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}

