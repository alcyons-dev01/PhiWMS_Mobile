package fr.alcyons.phimr4.DemandeDotationPAD;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation_Patient;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.PH_PatientAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by jessica on 06/10/2017.
 */

public class ListeDotationPatientActivity extends ServiceAvecConnexionActivity {

    Depot depot;

    List<PH_Patient> phPatientList;
    ListView phPatientListView;
    PH_PatientAdapter phPatientAdapter;

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
        setContentView(R.layout.activity_liste_ph_patient);

        // Récupération des variables globales
        depotID = intent.getExtras().getInt("depotSelectionneID");
        // Récupération du dépot sélectionné
        depot = DepotOpenHelper.getDepotParID(db, depotID);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.titre)).setText("Dotation Patient - " + depot.getNom().trim());
        dateInventaireTextView = (TextView) findViewById(R.id.dateInventaire);
        dateLivraisonProchaineTextView = (TextView) findViewById(R.id.dateLivraisonProchaine);
        dateLivraisonSuivanteTextView = (TextView) findViewById(R.id.dateLivraisonSuivante);

        // Récupération et initialisation de l'action de la listView
        phPatientListView = (ListView) findViewById(R.id.listeView);
        phPatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Patient PH_PatientSelectionne = (PH_Patient) phPatientAdapter.getItem(position);

                Bundle listeDotationPatient_Bundle = ListeDotationPatientActivity.super.getBundle();
                listeDotationPatient_Bundle.putInt("depotSelectionneID", depotID);
                listeDotationPatient_Bundle.putInt("PH_PatientSelectionneID", PH_PatientSelectionne.getPhiMR4UUID());
                listeDotationPatient_Bundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
                listeDotationPatient_Bundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
                listeDotationPatient_Bundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

                Intent listeDotationPatient_Intent = new Intent(ListeDotationPatientActivity.this, InformationDotationPatientActivity.class);
                listeDotationPatient_Intent.putExtras(listeDotationPatient_Bundle);
                ListeDotationPatientActivity.this.startActivity(listeDotationPatient_Intent);
                ListeDotationPatientActivity.this.finish();
            }
        });


        /* Gestion des dates et des DatePicker */
        // Récupération du " Calendar " du téléphone
        calendar = Calendar.getInstance();
        // Initialisation dateInventaire à la date du jour
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        dateInventaireTextView.setText(sdf.format(calendar.getTime()));
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

                DatePickerDialog dateInventairePickerDialog = new DatePickerDialog(ListeDotationPatientActivity.this, dateInventaireDatePicker, year, month, day);

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

                DatePickerDialog dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDotationPatientActivity.this, dateLivraisonProchaineDatePicker, year, month, day);

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

                    dateLivraisonProchainePickerDialog = new DatePickerDialog(ListeDotationPatientActivity.this, dateLivraisonProchaineDatePicker, year, month, day);
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

                DatePickerDialog dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDotationPatientActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
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

                    dateLivraisonSuivantePickerDialog = new DatePickerDialog(ListeDotationPatientActivity.this, dateLivraisonSuivanteDatePicker, year, month, day);
                    dateLivraisonSuivantePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                }

                dateLivraisonSuivantePickerDialog.show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        phPatientList = new ArrayList<>();

        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ListeDotationPatientActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ListeDotationPatientActivity.this, "Veuillez patienter", "Synchronisation des Dotations patients en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ListeDotationPatientActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteProtocoles_Patient + depot.getDepot_UID() + "/dotation";

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
                                        Alerte.afficherAlerte(ListeDotationPatientActivity.this, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ListeDotationPatientActivity.this.finishAffinity();
                                        Intent intent = new Intent(ListeDotationPatientActivity.this, AuthentificationActivity.class);
                                        ListeDotationPatientActivity.this.startActivity(intent);
                                    } else {
                                        vide = true;
                                    }
                                } else {
                                    JSONArray Protocoles_PatientsJson = response.getJSONArray("Protocoles_Patients");
                                    viderTablesConcernees();

                                    for (int i = 0; i < Protocoles_PatientsJson.length(); i++) {
                                        JSONObject Protocoles_PatientJson = Protocoles_PatientsJson.getJSONObject(i);
                                        Protocoles_Patients Protocoles_Patient = new Protocoles_Patients(Protocoles_PatientJson);

                                        JSONArray Dotation_PatientsJson = Protocoles_PatientJson.getJSONArray("dotation_patients");
                                        if (Dotation_PatientsJson.length() == 0) {
                                            Alerte.afficherAlerte(ListeDotationPatientActivity.this, "Alerte", "Aucune Dotation Patient trouvée", "alerte");
                                        } else {

                                            // Date de prochaine livraison
                                            JSONObject DateLivraisonJson = Protocoles_PatientJson.getJSONObject("date_Livraison");

                                            Date date;
                                            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            String dateAAfficher;
                                            try {
                                                date = dateDecodeur.parse(DateLivraisonJson.getString("prochaine_livraison"));
                                                dateAAfficher = dateFormat.format(date);
                                                dateLivraisonProchaineTextView.setText(dateAAfficher.trim());
                                                date = dateDecodeur.parse(DateLivraisonJson.getString("livraison_suivante"));
                                                dateAAfficher = dateFormat.format(date);
                                                dateLivraisonSuivanteTextView.setText(dateAAfficher.trim());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            Protocoles_PatientsOpenHelper.insererProtocoles_PatientsEnBDD(db, Protocoles_Patient);

                                            JSONObject PH_PatientsJson = Protocoles_PatientJson.getJSONObject("ph_patients");
                                            PH_Patient ph_patient = new PH_Patient(PH_PatientsJson);
                                            PH_PatientOpenHelper.insererPH_PatientEnBDD(db, ph_patient);

                                            phPatientList.add(ph_patient);

                                            for (int k = 0; k < Dotation_PatientsJson.length(); k++) {
                                                JSONObject Dotation_PatientJson = Dotation_PatientsJson.getJSONObject(k);
                                                Dotation_Patient dotation_patient = new Dotation_Patient(Dotation_PatientJson);
                                                Dotation_PatientOpenHelper.insererDotation_PatientEnBDD(db, dotation_patient);


                                                JSONArray ph_stock_destinataire = Dotation_PatientJson.getJSONArray("ph_stock_destinataire");
                                                JSONArray ph_stock_pui = Dotation_PatientJson.getJSONArray("ph_stock_pui");

                                                for (int y = 0; y < ph_stock_destinataire.length(); y++) {
                                                    Stock stock = new Stock(ph_stock_destinataire.getJSONObject(y));
                                                    StockOpenHelper.insererUnStockEnBDD(db, stock);
                                                }

                                                for (int y = 0; y < ph_stock_pui.length(); y++) {
                                                    Stock stock = new Stock(ph_stock_pui.getJSONObject(y));
                                                    StockOpenHelper.insererUnStockEnBDD(db, stock);
                                                }

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
                            Alerte.afficherAlerte(ListeDotationPatientActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Demande Dotation PAD)", "alerte");
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
            for (Protocoles_Patients protocoles_patient : Protocoles_PatientsOpenHelper.getProtocoles_PatientsByDepot(db, depotID)) {
                PH_Patient ph_patient = PH_PatientOpenHelper.getPH_PAtientByIPP(db, protocoles_patient.getIPP());
                phPatientList.add(ph_patient);
            }

            if (phPatientList.size() == 0) {
                connexionNecessaire();
                return;
            }
        }

        /* Code nécessaire à l'affichage de la liste */
        phPatientAdapter = new PH_PatientAdapter(ListeDotationPatientActivity.this, phPatientList, db);
        // Permet d'enlever le séparateur entre deux éléments d'une listeView
        phPatientListView.setDivider(footer);
        phPatientListView.setAdapter(phPatientAdapter);

        if (phPatientList.size() == 0) {
            vide = true;
            nomServiceVide = "Dotation Patient";
            ListeDotationPatientActivity.this.finish();
        } else if (phPatientList.size() == 1) {
            PH_Patient PH_PatientSelectionne = phPatientList.get(0);

            Bundle listeDotationPatient_Bundle = ListeDotationPatientActivity.super.getBundle();
            listeDotationPatient_Bundle.putInt("depotSelectionneID", depotID);
            listeDotationPatient_Bundle.putInt("PH_PatientSelectionneID", PH_PatientSelectionne.getPhiMR4UUID());
            listeDotationPatient_Bundle.putString("dateInventaire", dateInventaireTextView.getText().toString());
            listeDotationPatient_Bundle.putString("dateLivraisonProchaine", dateLivraisonProchaineTextView.getText().toString());
            listeDotationPatient_Bundle.putString("dateLivraisonSuivante", dateLivraisonSuivanteTextView.getText().toString());

            Intent listeDotationPatient_Intent = new Intent(ListeDotationPatientActivity.this, InformationDotationPatientActivity.class);
            listeDotationPatient_Intent.putExtras(listeDotationPatient_Bundle);
            ListeDotationPatientActivity.this.startActivity(listeDotationPatient_Intent);
            ListeDotationPatientActivity.this.finish();
        }

        invalidateOptionsMenu();
    }

    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateTextView.setText(sdf.format(calendar.getTime()));
    }

    public void viderTablesConcernees() {
        Protocoles_PatientsOpenHelper.viderTableProtocoles_Patients(db);
        PH_PatientOpenHelper.viderTablePH_Patient(db);
        Dotation_PatientOpenHelper.viderTableDotation_Patient(db);
        StockOpenHelper.viderTableStocks(db);
    }

}
