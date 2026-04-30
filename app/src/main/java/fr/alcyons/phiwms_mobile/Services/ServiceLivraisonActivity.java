package fr.alcyons.phiwms_mobile.Services;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Lot_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.ListViewAdapters.ListePointDeLivraisonAdapter;
import fr.alcyons.phiwms_mobile.Livraison.ListeLivraisonDepot;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceLivraisonActivity extends ServiceAvecConnexionActivity {
    Context context;
    List<PH_Preparation> ph_preparation_List;
    List<PH_Preparation> ph_preparation_List_base;
    ListView ph_preparation_ListView;
    ListePointDeLivraisonAdapter listePointDeLivraisonAdapter;
    List<String> listeDate;
    List<String> listePointLivraison;
    JSONArray ph_preparation_JSONArray;
    PackageManager pm;
    boolean connexionDirecte;
    List<String> listeDepotLivraison;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner spinner;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_refresh);
        context = ServiceLivraisonActivity.this;

        pm = ServiceLivraisonActivity.this.getPackageManager();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add("Tous");
        //Gestion de la listView
        ph_preparation_ListView = findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener((parent, view, position, id) -> {

            if (!listePointDeLivraisonAdapter.sectionHeader.contains(position)) {
                PH_Preparation ph_preparation_Selectionne = listePointDeLivraisonAdapter.getItem(position);
                Intent serviceLivraison_Intent = new Intent(ServiceLivraisonActivity.this, ListeLivraisonDepot.class);
                Bundle serviceLivraison_Bundle = ServiceLivraisonActivity.super.getBundle();
                serviceLivraison_Bundle.putString("depotRef", ph_preparation_Selectionne.getDepotDestinataireReference());
                serviceLivraison_Bundle.putString("dateLivraison", ph_preparation_Selectionne.getLivraisonPrevueDate());
                serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                ServiceLivraisonActivity.this.startActivity(serviceLivraison_Intent);
            }
        });

        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);

        passageParOnCreate = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        ph_preparation_List = new ArrayList<>();
        ph_preparation_List_base = new ArrayList<>();
        listeDate = new ArrayList<>();
        listePointLivraison = new ArrayList<>();
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (statutConnexion && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ServiceLivraisonActivity.this, LayoutInflater.from(ServiceLivraisonActivity.this));
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceLivraisonActivity.this);


            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceLivraison;

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                Log.e(TAG, "RuntimeException :", e);
            }
            passageParOnCreate = false;
        }
        else {
            ph_preparation_List = PH_PreparationOpenHelper.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId());
            ph_preparation_List_base = PH_PreparationOpenHelper.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId());
            if (ph_preparation_List.isEmpty()) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = getRetourVersServiceConnexionDirectIntent();
                    ServiceLivraisonActivity.this.startActivity(retourVersServiceConnexionDirectIntent);
                    ServiceLivraisonActivity.this.finish();
                }
                else
                {
                    Intent serviceLivraison_Intent = new Intent(ServiceLivraisonActivity.this, NavigationActivity.class);
                    Bundle serviceLivraison_Bundle = ServiceLivraisonActivity.super.getBundle();
                    serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                    ServiceLivraisonActivity.this.startActivity(serviceLivraison_Intent);
                    ServiceLivraisonActivity.this.finish();
                }
            }

            if(connexionDirecte)
                connexionDirecte = false;

        }

        // Tri par Date : de la plus récente à la plus ancienne
        ph_preparation_List.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));

        listePointDeLivraisonAdapter = new ListePointDeLivraisonAdapter(ServiceLivraisonActivity.this, db, utilisateurConnecte);

       for (PH_Preparation ph_courant : ph_preparation_List) {
            if (!listeDate.contains(ph_courant.getLivraisonPrevueDate())) {
                listeDate.add(ph_courant.getLivraisonPrevueDate());
                listePointDeLivraisonAdapter.addSectionHeaderItem(ph_courant);
                listePointLivraison.add(ph_courant.getDepotDestinataireReference());
                listePointDeLivraisonAdapter.addItem(ph_courant);
            }

            if(!listePointLivraison.contains(ph_courant.getDepotDestinataireReference()))
            {
                listePointLivraison.add(ph_courant.getDepotDestinataireReference());
                listePointDeLivraisonAdapter.addItem(ph_courant);
            }
        }


        ph_preparation_ListView.setDivider(footer);
        ph_preparation_ListView.setAdapter(listePointDeLivraisonAdapter);


        int taille_liste = ph_preparation_List.size();
        String titre = "Livraisons";
        if(taille_liste < 2)
            titre = "Livraison";

        /* Code nécessaire à l'affichage de la liste */

        if (ph_preparation_List.isEmpty()) {
            vide = true;
            nomServiceVide = "Livraison";
            ServiceLivraisonActivity.this.finish();
        }
        else
        {
            //initi du tri
            spinner = (Spinner) findViewById(R.id.optionTri);

            spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listeDepotLivraison);
            spinner.setAdapter(spinnerArrayAdapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(parent.getChildAt(0) != null)
                    {
                        parent.getChildAt(0).setVisibility(View.INVISIBLE);
                    }
                    String depot = spinner.getItemAtPosition(position).toString();

                    ph_preparation_List = new ArrayList<>();
                    listeDate = new ArrayList<>();
                    listePointLivraison = new ArrayList<>();

                    if(depot.contentEquals("Tous"))
                    {
                        ph_preparation_List.addAll(ph_preparation_List_base);
                    }
                    else
                    {
                        for(PH_Preparation preparation_courant : ph_preparation_List_base)
                        {
                            Depot depotCourant = DepotOpenHelper.getDepotParReference(db, preparation_courant.getDepotDestinataireReference());
                            if(depotCourant.getNom().contentEquals(depot))
                            {
                                ph_preparation_List.add(preparation_courant);
                            }
                        }
                    }

                    ph_preparation_List.sort(Comparator.comparing(PH_Preparation::getLivraisonPrevueDate));

                    listePointDeLivraisonAdapter = new ListePointDeLivraisonAdapter(ServiceLivraisonActivity.this, db, utilisateurConnecte);

                    for (PH_Preparation ph_courant : ph_preparation_List) {
                        if (!listeDate.contains(ph_courant.getLivraisonPrevueDate())) {
                            listeDate.add(ph_courant.getLivraisonPrevueDate());
                            listePointDeLivraisonAdapter.addSectionHeaderItem(ph_courant);
                            listePointLivraison.add(ph_courant.getDepotDestinataireReference());
                            listePointDeLivraisonAdapter.addItem(ph_courant);
                        }

                        if(!listePointLivraison.contains(ph_courant.getDepotDestinataireReference()))
                        {
                            listePointLivraison.add(ph_courant.getDepotDestinataireReference());
                            listePointDeLivraisonAdapter.addItem(ph_courant);
                        }
                    }

                    ph_preparation_ListView.setDivider(footer);
                    ph_preparation_ListView.setAdapter(listePointDeLivraisonAdapter);

                    int taille_liste = ph_preparation_List.size();
                    String titre = "Livraisons";
                    if(taille_liste < 2)
                        titre = "Livraison";

                    /* Code nécessaire à l'affichage de la liste */
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });


            invalidateOptionsMenu();
        }
    }

    @NonNull
    private Intent getRetourVersServiceConnexionDirectIntent() {
        Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceLivraisonActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle retourVersServiceConnexionDirectBundle = new Bundle();
        retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
        retourVersServiceConnexionDirectBundle.putString("nomService", "Livraison");

        retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
        return retourVersServiceConnexionDirectIntent;
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                response -> {
                    try {
                        int resultCount = response.getInt("resultCount");
                        if (resultCount == 0) {
                            String erreur = response.getString("erreur");
                            if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                //DBOpenHelper.viderBasesDeDonnees(db);
                                ServiceLivraisonActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                ServiceLivraisonActivity.this.finishAffinity();
                                Intent intent = new Intent(context, AuthentificationActivity.class);
                                context.startActivity(intent);
                            } else if (erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                arreterSpinner();
                                Alerte.afficherAlerte(ServiceLivraisonActivity.this, "Alerte", "Aucune Livraison à traiter", "alerte");
                                //retourNavigation(ServiceLivraisonActivity.this);
                            }
                        } else {
                            ph_preparation_JSONArray = response.getJSONArray("PH_Preparations");
                            viderTablesConcernees();
                            for (int i = 0; i < ph_preparation_JSONArray.length(); i++) {
                                JSONObject ph_preparation_JSONObject = ph_preparation_JSONArray.getJSONObject(i);
                                PH_Preparation ph_preparation = new PH_Preparation(ph_preparation_JSONObject);

                                //gestion de la liste des dépôts
                                Depot depotDestinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());
                                if(depotDestinataire != null)
                                {
                                    if(!listeDepotLivraison.contains(depotDestinataire.getNom()))
                                        listeDepotLivraison.add(depotDestinataire.getNom());
                                }

                                ph_preparation_List.add(ph_preparation);
                                ph_preparation_List_base.add(ph_preparation);
                                long rowID = PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);
                                if (rowID != -1) {
                                    JSONArray ph_preparationLignesJson = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                    for (int k = 0; k < ph_preparationLignesJson.length(); k++) {
                                        PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLignesJson.getJSONObject(k)));
                                    }
                                }
                            }

                            //récupération des PH_Lot_Ligne
                            JSONArray ph_lot_ligneJSONArray = response.getJSONArray("PH_Lot_Ligne");
                            for(int j = 0; j < ph_lot_ligneJSONArray.length(); j++)
                            {
                                JSONArray lot_ligne_array = ph_lot_ligneJSONArray.getJSONArray(j);

                                for(int k = 0; k < lot_ligne_array.length(); k++)
                                {
                                    JSONObject lot_ligne_object = lot_ligne_array.getJSONObject(k);
                                    PH_Lot_Ligne lot_ligne_courant = new PH_Lot_Ligne(lot_ligne_object);
                                    boolean present = PH_Lot_LigneOpenHelper.CheckPH_Lot_Ligne(db, lot_ligne_courant);
                                    if(!present)
                                    {
                                        PH_Lot_LigneOpenHelper.insererUnPH_Lot_LigneBDD(db, lot_ligne_courant);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error JSON :", e);
                    }
                    handler.sendMessage(handler.obtainMessage());
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerte(ServiceLivraisonActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Livraison)", "alerte");
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
        for (PH_Preparation ph_preparation : PH_PreparationOpenHelper.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId())
        ) {
            List<PH_Preparation_Ligne> ph_preparation_lignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);
            for (PH_Preparation_Ligne ph_preparation_ligne : ph_preparation_lignes) {
                //suppression des ph_lot_ligne en bdd
                PH_Lot_LigneOpenHelper.supprimerPH_LotLigne(db, ph_preparation_ligne.get_UID());
                PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
            }
            PH_PreparationOpenHelper.supprimerUnPhPreparation(db, ph_preparation);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        Bundle scanDocumentBundle = ServiceLivraisonActivity.super.getBundle();
        scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteDocument));
        scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent scanDocumentIntent = null;
        if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            scanDocumentIntent = new Intent(ServiceLivraisonActivity.this, ScannerDocumentActivity.class);
            scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
            scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                scanDocumentIntent = new Intent(ServiceLivraisonActivity.this, BarcodeCaptureActivity.class);
                scanDocumentBundle.putBoolean("modeRafale", false);
            }
            else
            {
                scanDocumentIntent = new Intent(ServiceLivraisonActivity.this, ScannerDocumentActivity.class);
                scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteDocument);
                scanDocumentBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un document");
            }
        }

        scanDocumentIntent.putExtras(scanDocumentBundle);
        ServiceLivraisonActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_DOCUMENT);
    }
}
