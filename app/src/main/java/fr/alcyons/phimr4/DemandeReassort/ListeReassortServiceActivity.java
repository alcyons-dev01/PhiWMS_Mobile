package fr.alcyons.phimr4.DemandeReassort;

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
import android.widget.AdapterView;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import fr.alcyons.phimr4.Classes.PH_Reassort;
import fr.alcyons.phimr4.Classes.PH_Reassort_Ligne;
import fr.alcyons.phimr4.ListViewAdapters.PH_ReassortAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 05/10/2017.
 */

public class ListeReassortServiceActivity extends ServiceAvecConnexionActivity {

    Depot depot;

    List<PH_Reassort> phReassortList;
    ListView phReassortListView;
    PH_ReassortAdapter phReassortAdapter;

    Calendar calendar;

    TextView dateInventaireTextView;
    TextView dateLivraisonProchaineTextView;
    TextView dateLivraisonSuivanteTextView;

    DatePickerDialog.OnDateSetListener dateInventaireDatePicker;
    DatePickerDialog.OnDateSetListener dateLivraisonProchaineDatePicker;
    DatePickerDialog.OnDateSetListener dateLivraisonSuivanteDatePicker;

    Integer depotID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_ph_reassort);

        // Récupération des variables globales
        depotID = intent.getExtras().getInt("depotSelectionneID");

        // Récupération du dépot sélectionné
        depot = gestionnaireDepot.getDepotParID(db, depotID);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Réassort de service - " + depot.getNom().trim());
        dateInventaireTextView = (TextView) findViewById(R.id.dateInventaire);
        dateLivraisonProchaineTextView = (TextView) findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonSuivanteTextView = (TextView) findViewById(R.id.dateLivraisonSuivante);

        // Récupération et initialisation de l'action de la listView
        phReassortListView = (ListView) findViewById(R.id.listeView);
        phReassortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Reassort PH_ReassortSelectionne = (PH_Reassort) phReassortAdapter.getItem(position);

                Bundle listeReassortService_Bundle = ListeReassortServiceActivity.super.getBundle();
                listeReassortService_Bundle.putInt("depotSelectionneID", depotID);
                listeReassortService_Bundle.putInt("PH_ReassortSelectionneID", PH_ReassortSelectionne.getPhiMR4UUID());
                listeReassortService_Bundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
                listeReassortService_Bundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
                listeReassortService_Bundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

                Intent listeReassortService_Intent = new Intent(ListeReassortServiceActivity.this, InformationDemandeReassortActivity.class);
                listeReassortService_Intent.putExtras(listeReassortService_Bundle);
                ListeReassortServiceActivity.this.startActivity(listeReassortService_Intent);
                ListeReassortServiceActivity.this.finish();
            }
        });
        /* Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();
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

                DatePickerDialog dateInventairePickerDialog = new DatePickerDialog(ListeReassortServiceActivity.this, dateInventaireDatePicker, year, month, day);

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

                DatePickerDialog dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeReassortServiceActivity.this, dateLivraisonProchaineDatePicker, year, month, day);

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

                    // Create a new instance of DatePickerDialog and return it
                    dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeReassortServiceActivity.this, dateLivraisonProchaineDatePicker, year, month, day);
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

                DatePickerDialog dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeReassortServiceActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
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

                    // Create a new instance of DatePickerDialog and return it
                    dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeReassortServiceActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
                    dateLivraisonSuivantePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                dateLivraisonSuivantePickerDialog.show();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        phReassortList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeReassortServiceActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeReassortServiceActivity.this, "Veuillez patienter", "Récupération date livraison en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeReassortServiceActivity.this);
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
                                        Alerte.afficherAlerte(ListeReassortServiceActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeReassortServiceActivity.this.finishAffinity();
                                        Intent intent = new Intent(ListeReassortServiceActivity.this, AuthentificationActivity.class);
                                        ListeReassortServiceActivity.this.startActivity(intent);
                                    } else {
                                        vide = true;
                                    }
                                } else {
                                    // Date de prochaine livraison
                                    JSONObject eventJSON = response.getJSONObject("EVENTS");
                                    Date date;
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
                            Alerte.afficherAlerte(ListeReassortServiceActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Récupération date livraison)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                    headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
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
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeReassortServiceActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeReassortServiceActivity.this, "Veuillez patienter", "Synchronisation des Reassorts de service en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeReassortServiceActivity.this);
            String urlRequete = null;
            /*try {
                urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePH_Reassort + URLEncoder.encode(depot.getDepot_Reference(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/

            urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePH_Reassort + depot.getDepot_Reference();
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
                                        Alerte.afficherAlerte(ListeReassortServiceActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeReassortServiceActivity.this.finishAffinity();
                                        Intent intent = new Intent(ListeReassortServiceActivity.this, AuthentificationActivity.class);
                                        ListeReassortServiceActivity.this.startActivity(intent);
                                    } else {
                                        Alerte.afficherAlerte(ListeReassortServiceActivity.this, "Alerte", "Aucun Reassort de service trouvé", "alerte");
                                    }
                                } else {
                                    JSONArray PH_ReassortsJson = response.getJSONArray("PH_Reassorts");
                                    viderTablesConcernees();

                                    long rowID = 0;
                                    for (int i = 0; i < PH_ReassortsJson.length(); i++) {
                                        JSONObject PH_ReassortJson = PH_ReassortsJson.getJSONObject(i);
                                        PH_Reassort PH_Reassort = new PH_Reassort(PH_ReassortJson);

                                        rowID = gestionnairePH_Reassort.insererPH_ReassortEnBDD(db, PH_Reassort);
                                        if (rowID != -1) {
                                            phReassortList.add(PH_Reassort);
                                        }

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
                            Alerte.afficherAlerte(ListeReassortServiceActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Demande Réassort)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    headers.put("UserId", String.valueOf(utilisateurConnecte.getId()));
                    headers.put("EtablissementId", String.valueOf(utilisateurConnecte.getEtablissementId()));
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
        } else {
            phReassortList = gestionnairePH_Reassort.getPH_ReassortByDepot(db, depot.getDepot_Reference());
            if (phReassortList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        /* Code nécessaire à l'affichage de la liste */
        phReassortAdapter = new PH_ReassortAdapter(ListeReassortServiceActivity.this, phReassortList, db);

        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        phReassortListView.setDivider(footer);
        phReassortListView.setAdapter(phReassortAdapter);

        if (phReassortList.size() == 0) {
            vide = true;
            nomServiceVide = "PH Réassort";
            ListeReassortServiceActivity.this.finish();
        } else if (phReassortList.size() == 1) {
            PH_Reassort PH_ReassortSelectionne = phReassortList.get(0);

            Intent newIntent = new Intent(ListeReassortServiceActivity.this, InformationDemandeReassortActivity.class);
            Bundle extras = ListeReassortServiceActivity.super.getBundle();

            extras.putInt("depotSelectionneID", depotID);
            extras.putInt("PH_ReassortSelectionneID", PH_ReassortSelectionne.getPhiMR4UUID());
            extras.putString("dateInventaire", dateInventaireTextView.getText().toString());
            extras.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
            extras.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

            newIntent.putExtras(extras);
            ListeReassortServiceActivity.this.startActivity(newIntent);
            ListeReassortServiceActivity.this.finish();
        }

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, phReassortAdapter, null, "Intitulé réassort...");
        return true;
    }

    public void viderTablesConcernees() {
        for (PH_Reassort PH_Reassort : gestionnairePH_Reassort.getPH_ReassortByDepot(db, depot.getDepot_Reference())) {
            for (PH_Reassort_Ligne PH_Reassort_Ligne : gestionnairePH_Reassort_Ligne.getAllPH_Reassort_LigneParPH_Reassort(db, PH_Reassort)) {
                gestionnairePH_Reassort_Ligne.supprimerUnPH_Reassort_Ligne(db, PH_Reassort_Ligne);
            }
            gestionnairePH_Reassort.supprimerUnePH_Reassort(db, PH_Reassort);
        }

        gestionnaireStock.viderTableStocks(db);
    }

    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(calendar.getTime()));
    }


}
