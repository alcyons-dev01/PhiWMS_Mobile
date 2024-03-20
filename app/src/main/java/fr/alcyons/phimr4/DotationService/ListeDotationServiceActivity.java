package fr.alcyons.phimr4.DotationService;

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
import fr.alcyons.phimr4.Classes.Dotation;
import fr.alcyons.phimr4.ListViewAdapters.DotationAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 03/10/2017.
 */

public class ListeDotationServiceActivity extends ServiceAvecConnexionActivity {

    Depot depot;

    List<Dotation> dotationList;
    ListView dotationListView;
    DotationAdapter dotationAdapter;

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
        setContentView(R.layout.activity_liste_dotation_service);

        // Récupération des variables globales
        depotID = intent.getExtras().getInt("depotSelectionneID");
        // Récupération du dépot sélectionné
        depot = gestionnaireDepot.getDepotParID(db, depotID);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Dotation Globale - " + depot.getNom().trim());
        dateInventaireTextView = (TextView) findViewById(R.id.dateInventaire);
        dateLivraisonProchaineTextView = (TextView) findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonSuivanteTextView = (TextView) findViewById(R.id.dateLivraisonSuivante);

        // Récupération et initialisation de l'action de la listView
        dotationListView = (ListView) findViewById(R.id.listeView);
        dotationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dotation DotationSelectionne = (Dotation) dotationAdapter.getItem(position);

                Bundle listeDotationService_Bundle = ListeDotationServiceActivity.super.getBundle();
                listeDotationService_Bundle.putInt("depotSelectionneID", depotID);
                listeDotationService_Bundle.putInt("dotationSelectionneID", DotationSelectionne.getPhiMR4UUID());
                listeDotationService_Bundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
                listeDotationService_Bundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
                listeDotationService_Bundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

                Intent listeDotationService_Intent = new Intent(ListeDotationServiceActivity.this, InformationDotationServiceActivity.class);
                listeDotationService_Intent.putExtras(listeDotationService_Bundle);
                ListeDotationServiceActivity.this.startActivity(listeDotationService_Intent);
                ListeDotationServiceActivity.this.finish();
            }
        });

        /* Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();
        // Initialisation dateInventaire à la date du jour
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

                DatePickerDialog dateInventairePickerDialog = new DatePickerDialog(ListeDotationServiceActivity.this, dateInventaireDatePicker, year, month, day);

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

                DatePickerDialog dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDotationServiceActivity.this, dateLivraisonProchaineDatePicker, year, month, day);

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
                    dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDotationServiceActivity.this, dateLivraisonProchaineDatePicker, year, month, day);
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

                DatePickerDialog dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDotationServiceActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
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

                    dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDotationServiceActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
                    dateLivraisonSuivantePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                dateLivraisonSuivantePickerDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        dotationList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeDotationServiceActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeDotationServiceActivity.this, "Veuillez patienter", "Synchronisation des Dotations en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeDotationServiceActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteDotationUF + String.valueOf(depotID);

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
                                        Alerte.afficherAlerte(ListeDotationServiceActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeDotationServiceActivity.this.finishAffinity();
                                        Intent intent = new Intent(ListeDotationServiceActivity.this, AuthentificationActivity.class);
                                        ListeDotationServiceActivity.this.startActivity(intent);
                                    } else {
                                        vide = true;
                                    }
                                } else {
                                    JSONArray dotationsJson = response.getJSONArray("Dotations");
                                    viderTablesConcernees();

                                    long rowID = 0;
                                    for (int i = 0; i < dotationsJson.length(); i++) {
                                        JSONObject dotationJson = dotationsJson.getJSONObject(i);
                                        Dotation dotation = new Dotation(dotationJson);

                                        if(!dotation.isPLEINVIDE()){
                                            // Date de prochaine livraison
                                            JSONObject DateLivraisonJson = dotationJson.getJSONObject("date_Livraison");

                                            Date date = new Date();
                                            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            String dateAAfficher;
                                            try {
                                                date = dateDecodeur.parse(DateLivraisonJson.getString("prochaine_livraison"));
                                                dateAAfficher = dateFormat.format(date);
                                                dateLivraisonProchaineTextView.setText(dateAAfficher);
                                                date = dateDecodeur.parse(DateLivraisonJson.getString("livraison_suivante"));
                                                dateAAfficher = dateFormat.format(date);
                                                dateLivraisonSuivanteTextView.setText(dateAAfficher);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            rowID = gestionnaireDotation.insererDotationEnBDD(db, dotation);
                                            if (rowID != -1) {
                                                dotationList.add(dotation);
                                            }
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
                            Alerte.afficherAlerte(ListeDotationServiceActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Dotation Service)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
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
        } else {
            dotationList = gestionnaireDotation.getDotationByDepot(db, depotID);
            if (dotationList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        /* Code nécessaire à l'affichage de la liste */
        dotationAdapter = new DotationAdapter(ListeDotationServiceActivity.this, dotationList, db);

        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        dotationListView.setDivider(footer);
        dotationListView.setAdapter(dotationAdapter);

        if (dotationList.size() == 0) {
            vide = true;
            nomServiceVide = "Dotation Service";
            ListeDotationServiceActivity.this.finish();
        } else if (dotationList.size() == 1) {
            Dotation DotationSelectionne = dotationList.get(0);

            Bundle listeDotationService_Bundle = ListeDotationServiceActivity.super.getBundle();
            listeDotationService_Bundle.putInt("depotSelectionneID", depotID);
            listeDotationService_Bundle.putInt("dotationSelectionneID", DotationSelectionne.getPhiMR4UUID());
            listeDotationService_Bundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
            listeDotationService_Bundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
            listeDotationService_Bundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

            Intent listeDotationService_Intent = new Intent(ListeDotationServiceActivity.this, InformationDotationServiceActivity.class);
            listeDotationService_Intent.putExtras(listeDotationService_Bundle);
            ListeDotationServiceActivity.this.startActivity(listeDotationService_Intent);
        }

        invalidateOptionsMenu();
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, dotationAdapter, null, "Intitulé dotation...");
        return true;
    }


    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(calendar.getTime()));
    }

    public void viderTablesConcernees() {
       // gestionnaireDotation.viderTableDotation(db);
       // gestionnaireDetail_Dot.viderTableDetail_Dot(db);
        gestionnaireStock.viderTableStocks(db);
    }
}