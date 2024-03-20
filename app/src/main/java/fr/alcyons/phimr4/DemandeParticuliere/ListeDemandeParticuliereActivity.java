package fr.alcyons.phimr4.DemandeParticuliere;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.alcyons.phimr4.AuthentificationActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_ParticuliereAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 04/10/2017.
 */

public class ListeDemandeParticuliereActivity extends ServiceAvecConnexionActivity {

    Depot depot;

    List<Produit> produitList;
    List<Produit> produitSelectionneList = new ArrayList<>();
    List<Integer> produitIDList;

    ListView produitListView;
    Demande_ParticuliereAdapter demandeParticuliereAdapter;

    JSONArray phStocksJSONArray;

    Calendar calendar;

    TextView dateInventaireTextView;
    TextView dateLivraisonProchaineTextView;
    TextView dateLivraisonSuivanteTextView;


    DatePickerDialog.OnDateSetListener dateInventaireDatePicker;
    DatePickerDialog.OnDateSetListener dateLivraisonProchaineDatePicker;
    DatePickerDialog.OnDateSetListener dateLivraisonSuivanteDatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_demande_particuliere);

        // Récupération des variables globales et du dépot sélectionné
        depot = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));
        produitIDList = intent.getIntegerArrayListExtra("ListeProduit");

        // Récupération de l'ensemble des produits
        produitList = gestionnaireProduit.getAllProduits(db);

        // Récupération des produits que l'on a sélectionné
        for (Integer i : produitIDList) {
            for (Produit produitCourant : produitList) {
                if (produitCourant.getID_produit() == i) {
                    produitSelectionneList.add(produitCourant);
                }
            }
        }

        // Récupération de la listView
        produitListView = (ListView) findViewById(R.id.listeView);

        /* Gestion des dates et des DatePicker */

        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();

        // Initialisation dateInventaire à la date du jour
        dateInventaireTextView = (TextView) findViewById(R.id.dateInventaire);
        String format = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
        dateInventaireTextView.setText(simpleDateFormat.format(calendar.getTime()));

        dateInventaireDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateInventaireTextView);
            }

        };
        dateInventaireTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MAX à celle de dateLivraisonProchaine
                String dateMaxString = dateLivraisonProchaineTextView.getText().toString();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateInventairePickerDialog = new DatePickerDialog(ListeDemandeParticuliereActivity.this, dateInventaireDatePicker, year, month, day);

                if (!dateMaxString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMax = new Date();
                    try {
                        dateMax = dateFormat.parse(dateMaxString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dateInventairePickerDialog.getDatePicker().setMaxDate(dateMax.getTime());
                }

                dateInventairePickerDialog.show();

            }
        });

        // Initialisation dateLivraisonProchaine
        dateLivraisonProchaineTextView = (TextView) findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonProchaineDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateLivraisonProchaineTextView);
            }

        };
        dateLivraisonProchaineTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MIN à celle de dateInventaire
                //Restriction de la selection de la date MAX à celle de dateLivraisonSuivante
                String dateMinString = dateInventaireTextView.getText().toString();
                String dateMaxString = dateLivraisonSuivanteTextView.getText().toString();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDemandeParticuliereActivity.this, dateLivraisonProchaineDatePicker, year, month, day);

                if (!dateMinString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMin = new Date();
                    try {
                        dateMin = dateFormat.parse(dateMinString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    year = dateMin.getYear();
                    month = dateMin.getMonth();
                    day = dateMin.getDay();

                    dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDemandeParticuliereActivity.this, dateLivraisonProchaineDatePicker, year, month, day);
                    dateLivraisonProchainePickerDialog.getDatePicker().setMinDate(dateMin.getTime());

                    if (!dateMaxString.contentEquals("00/00/0000")) {
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date dateMax = new Date();
                        try {
                            dateMax = dateFormat.parse(dateMaxString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dateLivraisonProchainePickerDialog.getDatePicker().setMaxDate(dateMax.getTime());
                    }
                }

                dateLivraisonProchainePickerDialog.show();
            }
        });

        // Initialisation dateLivraisonSuivante
        dateLivraisonSuivanteTextView = (TextView) findViewById(R.id.dateLivraisonSuivante);
        dateLivraisonSuivanteDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateLivraisonSuivanteTextView);
            }

        };

        dateLivraisonSuivanteTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Restriction de la selection de la date MIN à celle de dateLivraisonProchaine
                String dateMinString = dateLivraisonProchaineTextView.getText().toString();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDemandeParticuliereActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
                dateLivraisonSuivantePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());

                if (!dateMinString.contentEquals("00/00/0000")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dateMin = new Date();
                    try {
                        dateMin = dateFormat.parse(dateMinString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    year = dateMin.getYear();
                    month = dateMin.getMonth();
                    day = dateMin.getDay();

                    dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDemandeParticuliereActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
                    dateLivraisonSuivantePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                dateLivraisonSuivantePickerDialog.show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        gestionnaireStock.viderTableStocks(db);

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeDemandeParticuliereActivity.this)) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeDemandeParticuliereActivity.this, "Veuillez patienter", "Récupération date livraison en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeDemandeParticuliereActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteEvent + depot.getDepot_UID();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String string = response.getString("erreur");
                                    if (string.equals(getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeDemandeParticuliereActivity.this.finishAffinity();
                                        Intent intent = new Intent(ListeDemandeParticuliereActivity.this, AuthentificationActivity.class);
                                        ListeDemandeParticuliereActivity.this.startActivity(intent);
                                    } else {
                                        Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur requete : Demande Particulière récupération stock))", "alerte");
                                    }
                                } else {
                                    // Date de prochaine livraison
                                    JSONObject eventJSON = response.getJSONObject("EVENTS");
                                    Date date = new Date();
                                    DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateAAfficher;
                                    try {
                                        date = dateDecodeur.parse(eventJSON.getString("prochaine_livraison"));
                                        dateAAfficher = dateFormat.format(date);
                                        dateLivraisonProchaineTextView.setText(dateAAfficher);
                                        date = dateDecodeur.parse(eventJSON.getString("livraison_suivante"));
                                        dateAAfficher = dateFormat.format(date);
                                        dateLivraisonSuivanteTextView.setText(dateAAfficher);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(handler.obtainMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Récupération date livraison)", "alerte");
                        }
                    }
            ) {
                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
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
        }

        for (Integer i : produitIDList) {

            if (OutilsGestionConnexionReseau.isServerAccessible(ListeDemandeParticuliereActivity.this)) {

                if (!swipeRefreshLayout.isRefreshing()) {
                    mProgressDialog = ProgressDialog.show(ListeDemandeParticuliereActivity.this, "Veuillez patienter", "synchronisation des stocks en cours");
                }

                RequestQueue requestQueue = Volley.newRequestQueue(ListeDemandeParticuliereActivity.this);
                String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteStocks + "produit/" + i;
                phStocksJSONArray = new JSONArray();
                JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                        new Response.Listener<JSONObject>() {

                            // Takes the response from the JSON request
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int nbResultat = response.getInt("resultCount");
                                    phStocksJSONArray = response.getJSONArray("PH_Stocks");
                                    if (nbResultat == 0) {
                                        String string = response.getString("erreur");
                                        if (string.equals(getString(R.string.tokenInvalide))) {
                                            Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                            DBOpenHelper.viderBasesDeDonnees(db);
                                            ListeDemandeParticuliereActivity.this.finishAffinity();
                                            Intent intent = new Intent(ListeDemandeParticuliereActivity.this, AuthentificationActivity.class);
                                            ListeDemandeParticuliereActivity.this.startActivity(intent);
                                        } else {
                                            Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur requete : Demande Particulière récupération stock))", "alerte");
                                        }
                                    } else {
                                        for (int i = 0; i < phStocksJSONArray.length(); i++) {
                                            Stock stock = new Stock(phStocksJSONArray.getJSONObject(i));
                                            gestionnaireStock.insererUnStockEnBDD(db, stock);
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                handler.sendMessage(handler.obtainMessage());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                                Alerte.afficherAlerte(ListeDemandeParticuliereActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Demande Particulière récupération stock)", "alerte");
                            }
                        }
                ) {
                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
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
            }

            /* Code nécessaire à l'affichage de la liste */
            demandeParticuliereAdapter = new Demande_ParticuliereAdapter(ListeDemandeParticuliereActivity.this, produitSelectionneList, db);
            // Permet d'enlever le séparateur entre deux éléments d'une listeView
            produitListView.setDivider(footer);
            produitListView.setAdapter(demandeParticuliereAdapter);

            if (produitSelectionneList.size() == 0) {
                ListeDemandeParticuliereActivity.this.finish();
            }

            invalidateOptionsMenu();
        }
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, demandeParticuliereAdapter, null, "Désignation produit...");
        return true;
    }

    @Override
    public void onBackPressed() {
        Bundle listeDermandeParticuliereBundle = super.getBundle();
        listeDermandeParticuliereBundle.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) produitIDList);
        listeDermandeParticuliereBundle.putInt("depotSelectionneID", depot.getDepot_UID());
        listeDermandeParticuliereBundle.putBoolean("Back", true);

        Intent listeDermandeParticuliereIntent = new Intent(ListeDemandeParticuliereActivity.this, ListeProduitActivity.class);
        listeDermandeParticuliereIntent.putExtras(listeDermandeParticuliereBundle);
        ListeDemandeParticuliereActivity.this.startActivity(listeDermandeParticuliereIntent);
    }

    public void onClick_boutonSave(View v){
        Bundle listeDermandeParticuliereBundle = ListeDemandeParticuliereActivity.super.getBundle();
        listeDermandeParticuliereBundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
        listeDermandeParticuliereBundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
        listeDermandeParticuliereBundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());
        listeDermandeParticuliereBundle.putInt("depotSelectionneID", depot.getDepot_UID());
        listeDermandeParticuliereBundle.putIntegerArrayList("listeProduit", (ArrayList<Integer>) produitIDList);

        Intent listeDermandeParticuliereIntent = new Intent(ListeDemandeParticuliereActivity.this, InformationDemandeParticuliereActivity.class);
        listeDermandeParticuliereIntent.putExtras(listeDermandeParticuliereBundle);
        ListeDemandeParticuliereActivity.this.startActivity(listeDermandeParticuliereIntent);
    }

    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(calendar.getTime()));
    }

}
