package fr.alcyons.phiwms_mobile.Services;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Looper;
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.SYS_User_RulesOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.SYS_User_Rules;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourDestructionAdapter;

import fr.alcyons.phiwms_mobile.ListViewAdapters.RetourQuarantaineAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Quarantaine.DetailQuarantaineActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
public class ServiceQuarantaineActivity extends ServiceAvecConnexionActivity {
    Retour retourSelectionne;
    PackageManager pm;
    List<Retour> retourList;
    List<Retour> retourListBase;
    ListView retourListView;
    RetourQuarantaineAdapter retourQuarantaineAdapter;
    JSONArray retourJSONArray;
    Context context;
    boolean connexionDirecte;
    ActivityResultLauncher<Intent> resultScanDocument;

    List<String> listeDepotQuarantaine;
    ArrayAdapter<String> autoCompleteAdapter;
    AutoCompleteTextView autoComplete;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        context = ServiceQuarantaineActivity.this;
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
            retourSelectionne = (Retour) retourQuarantaineAdapter.getItem(position);

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
                    if (result.getResultCode() == CodesEchangesActivites.RESULT_OK) {
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

                                    gestionAdapter();
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

                                gestionAdapter();
                                if (retourList.isEmpty()) {
                                    vide = true;
                                    nomServiceVide = "Quarantaine";
                                    ServiceQuarantaineActivity.this.finish();
                                }
                            }
                        } else {
                           gestionAdapter();
                            if (retourList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Quarantaine";
                                ServiceQuarantaineActivity.this.finish();
                            }
                        }
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceQuarantaineActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceQuarantaineActivity.this.startActivity(intent);
                ServiceQuarantaineActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        retourList = new ArrayList<>();
        retourListBase = new ArrayList<>();
        listeDepotQuarantaine = new ArrayList<>();
        listeDepotQuarantaine.add("Tous les dépôts");

        if (statutConnexion && passageParOnCreate && !connexionDirecte) {

            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceQuarantaineActivity.this, LayoutInflater.from(ServiceQuarantaineActivity.this));
            }

            //test();
            RequestQueue requestQueueQuarantaineUtilisateur = Volley.newRequestQueue(ServiceQuarantaineActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteQuarantaine;
            JsonObjectRequest obreq = getObjectRequest(urlRequete);
            requestQueueQuarantaineUtilisateur.add(obreq);
        }
        else
        {
            retourList = RetourOpenHelper.getAllRetoursByStatutEtEnAttenteDe(db, getString(R.string.statutEncours), getString(R.string.MiseEnQuarantaine));
            retourListBase.addAll(retourList);

            if (retourList.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceQuarantaineActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceQuarantaineActivity.this.finish();
                }
                else
                {
                    //connexionNecessaire();
                    return;
                }
            }
            else if (retourSelectionne != null) {
                if (retourSelectionne.getStatut().equals(getString(R.string.statutValide)) && retourSelectionne.getEn_Attente_de().equals(getString(R.string.Quarantaine))) {
                    retourList.remove(retourSelectionne);
                    retourListBase.remove(retourSelectionne);
                }
            }
            else
            {
                passageParOnCreate = false;
                if(connexionDirecte)
                {
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

    @SuppressLint("SetTextI18n")
    @NonNull
    private JsonObjectRequest getObjectRequest(String urlRequete) {

        return new JsonObjectRequest
                (Request.Method.GET, urlRequete, null, response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(),"Erreur HTTP", "Votre session est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(),"Erreur HTTP", "Votre session a expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                Alerte.afficherAlerteInformation(context, getLayoutInflater(),"Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Récupération Quarantaine", false, true);
                            } else {
                                arreterSpinner();
                                vide = true;
                                nomServiceVide = "Quarantaine";
                                retourNavigation();
                            }
                        } else {
                            retourJSONArray = response.getJSONArray("PH_Retours");
                            viderTablesConcernees();
                            for (int i = 0; i < retourJSONArray.length(); i++) {
                                JSONObject retourJSONObject = retourJSONArray.getJSONObject(i);

                                if (retourJSONObject.getString("En_Attente_de").equals("Mise en quarantaine")) {
                                    Retour retour = new Retour(retourJSONObject);

                                    //check quarantaine existe
                                    Retour quarantaine_existe = RetourOpenHelper.getRetourByID(db, retour.get_UID());
                                    if (quarantaine_existe != null) {
                                        RetourOpenHelper.supprimerUnRetour(db, quarantaine_existe);
                                    }

                                    retourList.add(retour);
                                    retourListBase.add(retour);

                                    String[] intitule_tab = retour.getIntitule().split(":");
                                    String depot_origine = intitule_tab[0];

                                    if(!listeDepotQuarantaine.contains(depot_origine))
                                        listeDepotQuarantaine.add(depot_origine);

                                    RetourOpenHelper.insererUnRetourEnBDD(db, retour);
                                    JSONArray retourLigneJSONArray = retourJSONObject.getJSONArray("ph_retour_ligne");

                                    for (int k = 0; k < retourLigneJSONArray.length(); k++) {
                                        Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, new Retour_Ligne(retourLigneJSONArray.getJSONObject(k)));
                                    }
                                }
                            }

                            JSONArray serialisationJSONArray = response.getJSONArray("PH_Serialisation");
                            for (int j = 0; j < serialisationJSONArray.length(); j++) {
                                JSONObject serialisationObject = serialisationJSONArray.getJSONObject(j);
                                PH_Serialisation serialisation = new PH_Serialisation(serialisationObject);
                                PH_SerialisationOpenHelper.insererPH_SerialisationEnBDD(db, serialisation);
                            }

                            if (retourList.isEmpty()) {
                                vide = true;
                                nomServiceVide = "Quarantaine";
                                ServiceQuarantaineActivity.this.finish();
                            } else {
                                if (passageParOnCreate) {
                                    initialiserAutoComplete();
                                    gestionAdapter();
                                    new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                                }
                                passageParOnCreate = false;
                            }
                        }
                    }
                    catch (Throwable t)
                    {
                        Log.e(TAG, "Error JSON", t);
                    }
                }, error -> {
                    // TODO: Handle error
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(context, getLayoutInflater(),"Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP Service Récupération Quarantaine", false, true);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
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

    public void afficherSnackBarPreparationQuarantaine() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    private void gestionAdapter()
    {
        retourList.sort(Comparator.comparing(Retour::getDate_retour));
        retourQuarantaineAdapter = new RetourQuarantaineAdapter(ServiceQuarantaineActivity.this, db, retourList, utilisateurConnecte);
        retourListView.setAdapter(retourQuarantaineAdapter);
    }

    private void initialiserAutoComplete() {
        autoComplete = findViewById(R.id.listeFiltre);

        autoCompleteAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_depot, listeDepotQuarantaine);
        autoComplete.setAdapter(autoCompleteAdapter);
        autoComplete.setThreshold(100); // Empêche le filtrage automatique

        // Affiche le premier élément par défaut
        if (!listeDepotQuarantaine.isEmpty()) {
            autoComplete.setText(listeDepotQuarantaine.get(0), false);
        }

        // Hauteur = 1/3 de l'écran
        int hauteurEcran = getResources().getDisplayMetrics().heightPixels;
        autoComplete.setDropDownHeight(hauteurEcran / 3);
        int dpToPx = (int) (12 * getResources().getDisplayMetrics().density);
        autoComplete.post(() -> autoComplete.setDropDownWidth(findViewById(R.id.listeFiltre_LL).getWidth() - dpToPx));
        autoComplete.setDropDownBackgroundResource(android.R.color.white);

        // Ouvre la liste au clic
        autoComplete.setOnClickListener(v -> autoComplete.showDropDown());

        // Chevron ouvre aussi la liste
        findViewById(R.id.chevronFiltre).setOnClickListener(v -> autoComplete.showDropDown());

        // Gère la sélection
        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String depot = listeDepotQuarantaine.get(position);
            autoComplete.setText(depot, false);
            autoComplete.dismissDropDown();

            retourList = new ArrayList<>();

            if (depot.contentEquals("Tous les dépôts")) {
                retourList.addAll(retourListBase);
            } else {
                for (Retour retourCourant : retourListBase) {
                    String[] intitule_tab = retourCourant.getIntitule().split(":");
                    String depot_origine = intitule_tab[0];

                    if (depot_origine.contentEquals(depot)) {
                        retourList.add(retourCourant);
                    }
                }
            }

            gestionAdapter();
        });
    }
}

