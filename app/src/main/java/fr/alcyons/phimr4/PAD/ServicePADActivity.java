package fr.alcyons.phimr4.PAD;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Prescription_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.Classes.Composants_patient;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation_Patient;
import fr.alcyons.phimr4.Classes.PAD_Proposition;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.Prescription_patient;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.DepotSelecteur.DepotSelecteurSimpleActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 07/03/2018.
 */

public class ServicePADActivity extends ServiceAvecConnexionActivity {

    List<String> listeDate = new ArrayList<>();
    Depot depotPatient;
    String IPP;
    String Vd_Livraison_Max_String;
    String referenceCycle;
    String CycleDu;
    String CycleAu;
    int idcycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialisation de l'IPP
        if (utilisateurConnecte.getDepot_UID() != 0) {
            depotPatient = gestionnaireDepot.getDepotParID(db, utilisateurConnecte.getDepot_UID());
            IPP = depotPatient.getPAD_IPP();
            recupererPatientDPP();
        } else {
            Bundle bundlePAD = new Bundle();
            bundlePAD.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));

            if(serviceActuel != null)
            {
                bundlePAD.putInt("serviceSelectionneID", serviceActuel.getId());
            }

            bundlePAD.putString("depotType", "PAD");

            Intent intentPAD = new Intent(ServicePADActivity.this, DepotSelecteurSimpleActivity.class);
            intentPAD.putExtras(bundlePAD);

            ServicePADActivity.this.startActivityForResult(intentPAD, CodesEchangesActivites.RESULT_SELECTION_DEPOT);
        }
    }

    public void recupererPatientDPP() {
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServicePADActivity.this)) {

            mProgressDialog = ProgressDialog.show(ServicePADActivity.this, "Veuillez patienter", "Synchronisation du DPP en cours");

            RequestQueue requestQueueNotification = Volley.newRequestQueue(ServicePADActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequetePh_Patient + IPP;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    Alerte.afficherAlerte(ServicePADActivity.this, "Attention", "Aucun protocole en cours", "alerte");
                                    ServicePADActivity.this.finish();
                                } else {
                                    viderTablesConcernees();

                                    Vd_Livraison_Max_String = response.getString("PremiereDateMoisProchain");

                                    //récupération des informations du patient
                                    JSONArray PatientJSONArray = response.getJSONArray("PH_Patients");
                                    PH_Patient patient = new PH_Patient(PatientJSONArray.getJSONObject(0));
                                    PH_PatientOpenHelper.insererPH_PatientEnBDD(db, patient);

                                    //on récupère les protocoles du patient
                                    JSONArray ProtocoleJSONArray = response.getJSONArray("ProtocolesPatients");
                                    for (int i = 0; i < ProtocoleJSONArray.length(); i++) {
                                        JSONObject protocoleJSONObject = ProtocoleJSONArray.getJSONObject(i);
                                        Protocoles_Patients protocoles_patients = new Protocoles_Patients(protocoleJSONObject);
                                        Protocoles_PatientsOpenHelper.insererProtocoles_PatientsEnBDD(db, protocoles_patients);
                                    }

                                    //récupération de la liste des produits pour le patient
                                    JSONArray ComposantJSONArray = response.getJSONArray("Composant");
                                    for (int i = 0; i < ComposantJSONArray.length(); i++) {
                                        JSONObject composantJSONObject = ComposantJSONArray.getJSONObject(i);
                                        Composants_patient composants_patient = new Composants_patient(composantJSONObject);
                                        Composants_patientOpenHelper.insererComposants_patientEnBDD(db, composants_patient);
                                    }

                                    //on gère les prescription
                                    JSONArray PrescriptionJSONArray = response.getJSONArray("Prescription");
                                    if (PrescriptionJSONArray.length() != 0) {
                                        for (int i = 0; i < PrescriptionJSONArray.length(); i++) {
                                            JSONObject PrescriptionJSONObject = PrescriptionJSONArray.getJSONObject(i);
                                            Prescription_patient prescriptionPatient = new Prescription_patient(PrescriptionJSONObject);
                                            Prescription_patientOpenHelper.insererPrescription_patientEnBDD(db, prescriptionPatient);
                                        }
                                    }

                                    JSONArray DotationJSONArray = response.getJSONArray("Dotation");
                                    for (int i = 0; i < DotationJSONArray.length(); i++) {
                                        JSONObject dotationJSONObject = DotationJSONArray.getJSONObject(i);
                                        Dotation_Patient dotation_patient = new Dotation_Patient(dotationJSONObject);
                                        Dotation_PatientOpenHelper.insererDotation_PatientEnBDD(db, dotation_patient);
                                    }

                                    //on récupère le cycle
                                    JSONArray CycleJSONArray = response.getJSONArray("Cycle");
                                    if (CycleJSONArray.length() != 0) {
                                        JSONObject CycleJSONObject = CycleJSONArray.getJSONObject(0);
                                        referenceCycle = CycleJSONObject.getString("Reference");
                                        idcycle = CycleJSONObject.getInt("_UID");
                                        CycleDu = CycleJSONObject.getString("Du");
                                        CycleAu = CycleJSONObject.getString("Au");
                                    }

                                    //on récupère les dates de livraison du dépôt pour le mois en cours
                                    JSONArray DateJSONArray = response.getJSONArray("DateLivraison");
                                    if (DateJSONArray.length() == 0) {
                                        //Alerte.afficherAlerte(ServicePADActivity.this, "Attention", "Aucune livraison n'est prévue ce mois ci.", "alerte");
                                        //ServicePADActivity.this.finish();
                                        afficherAlerteErreur(ServicePADActivity.this, ServicePADActivity.this.getLayoutInflater());
                                    } else {
                                        for (int i = 0; i < DateJSONArray.length(); i++) {
                                            JSONObject dateCourante = DateJSONArray.getJSONObject(i);
                                            String dateString = dateCourante.getString("Date_event");
                                            listeDate.add(dateString);
                                        }
                                        passerActiviteSuivante(depotPatient, IPP);
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
                            Alerte.afficherAlerte(ServicePADActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP service PAD", "alerte");
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
            requestQueueNotification.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            connexionNecessaire();
            return;
        }
    }

    public void viderTablesConcernees() {
        gestionnairePH_Patient.viderTablePH_Patient(db);
        gestionnaireProtocoles_Patients.viderTableProtocoles_Patients(db);
        gestionnaireComposants_patient.viderTableComposants_patient(db);
        gestionnairePrescription.viderTablePrescription_patient(db);
        gestionnaireDotationPatient.viderTableDotation_Patient(db);
    }

    public void passerActiviteSuivante(Depot depotPatient, String IPP) {

        PAD_Proposition padProposition = new PAD_Proposition(depotPatient.getDepot_UID(), IPP);

        Bundle bundlePAD = new Bundle();
        bundlePAD.putInt("utilisateurConnecteID", intent.getExtras().getInt("utilisateurConnecteID"));
        bundlePAD.putInt("serviceSelectionneID", serviceActuel.getId());
        bundlePAD.putSerializable("padProposition", padProposition);
        bundlePAD.putStringArrayList("listeDate", (ArrayList<String>) listeDate);
        bundlePAD.putSerializable("DepotPatient", depotPatient);
        bundlePAD.putString("IPP", IPP);
        bundlePAD.putString("Vd_Livraison_Max_String", Vd_Livraison_Max_String);
        bundlePAD.putString("referenceCycle", referenceCycle);
        bundlePAD.putString("CycleDu", CycleDu);
        bundlePAD.putString("CycleAu", CycleAu);
        bundlePAD.putInt("idcycle", idcycle);

        Intent intentPAD = new Intent(ServicePADActivity.this, DetailPADComposantPrescriptionActivity.class);
        intentPAD.putExtras(bundlePAD);

        ServicePADActivity.this.startActivity(intentPAD);
        ServicePADActivity.this.finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_SELECTION_DEPOT: {
                if (data != null) {
                    int depotUID = data.getExtras().getInt("depotUID_Selectionne");
                    depotPatient = gestionnaireDepot.getDepotParID(db, depotUID);
                    IPP = depotPatient.getPAD_IPP();
                    recupererPatientDPP();
                } else {
                    ServicePADActivity.this.finish();
                }
            }
            break;
        }
    }

    public void afficherAlerteErreur(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        ImageView buttonOk = (ImageView) layout.findViewById(R.id.buttonOk);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        TextView titre = (TextView) layout.findViewById(R.id.titre);

        titre.setText("Erreur");
        messageFin.setText("Aucune livraison n'est prévue ce mois ci.");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ServicePADActivity.this.finish();
            }
        });
    }
}

