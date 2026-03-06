package fr.alcyons.phiwms_mobile.PlanDePlacement;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoEmplacement;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoReception;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPlanDePlacementActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitPlaceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Produit_Place;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ScanPlanDePlacementActivity extends ServiceAvecConnexionActivity {
    boolean firstPassage = true;
    PackageManager pm;
    Context context;
    List<Produit> listeProduitScannees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_plan_de_placement);
        pm = ScanPlanDePlacementActivity.this.getPackageManager();
        intent = ScanPlanDePlacementActivity.this.getIntent();
        listeProduitScannees = new ArrayList<>();
        context = ScanPlanDePlacementActivity.this;
    }

    @Override
    public void onResume() {
        super.onResume();
        //invalidateOptionsMenu();
        //Lance l'activite BarcodeCaptureActivity une seule fois
        if (statutConnexion && passageParOnCreate) {
            if (!swipeRefreshLayout.isRefreshing()) {
                afficherSpinner(ScanPlanDePlacementActivity.this, LayoutInflater.from(ScanPlanDePlacementActivity.this));
            }
            RequestQueue requestQueue = Volley.newRequestQueue(ScanPlanDePlacementActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriProduitPlace+"depot/"+intent.getExtras().getInt("depotUID_Selectionne");

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            if (firstPassage) {
                Intent servicePlanDePlacementIntent = null;
                Bundle servicePlanDePlacementBundle = super.getBundle();

                if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google")) {
                    servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ScannerPlanDePlacementActivity.class);
                } else {
                    servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, BarcodeCaptureActivity.class);
                }

                servicePlanDePlacementBundle.putSerializable("ListProduitScannees", (Serializable) listeProduitScannees);
                servicePlanDePlacementBundle.putSerializable("depotUID", intent.getExtras().getInt("depotUID_Selectionne"));
                servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
                ScanPlanDePlacementActivity.this.startActivityForResult(servicePlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                firstPassage = false;
            }
        }


    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ScanPlanDePlacementActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    listeProduitScannees = new ArrayList<>();
                    listeProduitScannees.addAll((List<Produit>) data.getExtras().getSerializable("ListProduitScannes"));

                    boolean placement = data.getExtras().getBoolean("placement");
                    Intent servicePlanDePlacementIntent = servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ListeProduitsPlanDePlacementActivity.class);
                    Bundle servicePlanDePlacementBundle = super.getBundle();
                    servicePlanDePlacementBundle.putSerializable("ListProduitScannes", (Serializable) listeProduitScannees);
                    servicePlanDePlacementBundle.putSerializable("placement", (Serializable) placement);
                    servicePlanDePlacementBundle.putSerializable("depotUID", intent.getExtras().getInt("depotUID_Selectionne"));
                    servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);

                    ScanPlanDePlacementActivity.this.startActivity(servicePlanDePlacementIntent);
                    ScanPlanDePlacementActivity.this.finish();
                    break;
            }
            invalidateOptionsMenu();
        }
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
                                Alerte.afficherAlerteInformation(ScanPlanDePlacementActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est invalide, veuillez vous reconnecter.", false, true);
                            } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                Alerte.afficherAlerteInformation(ScanPlanDePlacementActivity.this, getLayoutInflater(), "Erreur", "Votre session de connexion est expirée, veuillez vous reconnecter.", false, true);
                            } else if (!erreur.contentEquals("Aucun ProduitPlace trouvé")) {
                                Alerte.afficherAlerteInformation(ScanPlanDePlacementActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Préparation PUF", false, true);
                            } else {
                                if (firstPassage) {
                                    Intent servicePlanDePlacementIntent = null;
                                    Bundle servicePlanDePlacementBundle = super.getBundle();

                                    if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google")) {
                                        servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ScannerPlanDePlacementActivity.class);
                                    } else {
                                        servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ScannerPhotoEmplacement.class);
                                    }

                                    servicePlanDePlacementBundle.putSerializable("ListProduitScannees", (Serializable) listeProduitScannees);
                                    servicePlanDePlacementBundle.putSerializable("depotUID", intent.getExtras().getInt("depotUID_Selectionne"));
                                    servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
                                    ScanPlanDePlacementActivity.this.startActivityForResult(servicePlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                                    firstPassage = false;
                                    arreterSpinner();
                                }
                            }
                        } else {
                            JSONArray produitPlace_JSONArray = response.getJSONArray("ProduitPlace");
                            viderTablesConcernees();
                            long rowID = 0;
                            for (int i = 0; i < produitPlace_JSONArray.length(); i++) {
                                JSONObject produitPlace_JSONObject = produitPlace_JSONArray.getJSONObject(i);
                                Produit_Place produitPlace = new Produit_Place(produitPlace_JSONObject);
                                rowID = ProduitPlaceOpenHelper.insererProduitPlaceEnBDD(db, produitPlace);
                            }

                            if (firstPassage) {
                                Intent servicePlanDePlacementIntent = null;
                                Bundle servicePlanDePlacementBundle = super.getBundle();

                                if (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google")) {
                                    servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ScannerPlanDePlacementActivity.class);
                                } else {
                                    servicePlanDePlacementIntent = new Intent(ScanPlanDePlacementActivity.this, ScannerPhotoEmplacement.class);
                                }

                                servicePlanDePlacementBundle.putSerializable("ListProduitScannees", (Serializable) listeProduitScannees);
                                servicePlanDePlacementBundle.putSerializable("depotUID", intent.getExtras().getInt("depotUID_Selectionne"));
                                servicePlanDePlacementIntent.putExtras(servicePlanDePlacementBundle);
                                ScanPlanDePlacementActivity.this.startActivityForResult(servicePlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                                firstPassage = false;
                            }

                            new Handler(Looper.getMainLooper()).postDelayed(this::arreterSpinner, 500);
                        }
                    } catch (JSONException e) {
                        Log.e("JSON Exception", Objects.requireNonNull(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Volley", "Error");
                    Alerte.afficherAlerteInformation(ScanPlanDePlacementActivity.this, getLayoutInflater(), "Erreur", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Préparation PUF", false, true);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
        return obreq;
    }

    public void viderTablesConcernees() {
        ProduitPlaceOpenHelper.viderTableProduitPlace(db);
    }

}
