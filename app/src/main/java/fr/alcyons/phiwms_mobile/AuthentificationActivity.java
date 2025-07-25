package fr.alcyons.phiwms_mobile;
import static com.google.android.gms.vision.L.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DotationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.FournisseurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Demande_MotifOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReassortOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.SYS_User_RulesOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.CGU.CguActivity;
import fr.alcyons.phiwms_mobile.Classes.SYS_User_Rules;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.FirebaseManager.MyFirebaseInstanceIdService;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GPSTracker;
import fr.alcyons.phiwms_mobile.Outils.OutilsEncodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.ParametresServeur.ServiceParametresServeurActivity;
import fr.alcyons.phiwms_mobile.VerificationConnexion.VerificationConnexionActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.leolin.shortcutbadger.ShortcutBadger;


public class AuthentificationActivity extends MainActivity {
    public static int nbTableAInserer = 11;
    public static int nbTableinserees = 0;
    public boolean pretAPasserActiviteSuivante = false;
    // base de donnée
    public SQLiteDatabase db;
    public String etablissement;
    private boolean authentification_scan;
    Boolean activerAuthentificationForte;
    Boolean connexionDirecte;
    GPSTracker gps;
    // Elément à synchroniser
    TextView nbElementsTextView;
    TextView textElementTextView;
    Utilisateur utilisateurConnecte = null;
    List<String> channels = new ArrayList<>();
    RequestQueue requestQueueUtilisateur;
    //Liste pour gérer l'alerte
    List<TextView> textViewList = new ArrayList<>();
    List<ImageView> imageViewList = new ArrayList<>();
    SeekBar zoneok;
    AlertDialog alertDialog;
    List<String> tabErreur = new ArrayList<>();
    int i = 0;
    LinearLayout gestionExpandable;
    TextView TextSynchronisation;
    String channelTelephone;
    JSONObject utilisateurJson;
    String token;
    Button boutonConnexion;
    ProgressBar progressBar;
    PackageManager pm;

    DBOpenHelper gestionnaireBaseDeDonnee;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint({"HardwareIds", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ouverture de la BDD
        gestionnaireBaseDeDonnee = new DBOpenHelper(AuthentificationActivity.this, DBOpenHelper.Constantes.NOM_BDD, null, DBOpenHelper.Constantes.DATABASE_VERSION);
        db = gestionnaireBaseDeDonnee.openDB();
        gestionnaireBaseDeDonnee.onUpgrade(db, 0, 0);
        OutilsGestionConnexionReseau.isServerAccessibleV2(AuthentificationActivity.this);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        setContentView(R.layout.activity_authentification);

        //ouverture CGU
        findViewById(R.id.versCGU).setOnClickListener(v -> {
            Intent versCGU = new Intent(AuthentificationActivity.this, CguActivity.class);
            AuthentificationActivity.this.startActivity(versCGU);
        });

        //gestion du package manager
        pm = AuthentificationActivity.this.getPackageManager();

        //gestion du boolean pour l'authentification par scan
        authentification_scan = false;

        // Si l'application a été ouvert par le clic sur la notification
        String test = getIntent().getStringExtra(getString(R.string.notifContent));
        if (test != null) {
            // Access the default SharedPreferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            int badgeCount = preferences.getInt("badgeCount", 0);
            if (badgeCount > 0) {
                badgeCount--;
                if (ShortcutBadger.isBadgeCounterSupported(AuthentificationActivity.this)) {
                    // Edit the saved preferences
                    editor.putInt("badgeCount", badgeCount);
                    editor.apply();
                    ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
                }
            }
        }

        //on supprime le contenu dossier "Document" qui contient les photos qui ont été enregistrée lors de la dernière session
        File dir = new File(getFilesDir().getAbsolutePath() + File.separator + "Documents");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
                new File(dir, children[i]).delete();
            }
        }


        Intent newIntent = new Intent(AuthentificationActivity.this, MyFirebaseInstanceIdService.class);
        startService(newIntent);



        // Vérification que les paramètres serveur ont été enregistrés
        if (ParametresServeurOpenHelper.getNbParametresServeur(db) != 1) {
            newIntent = new Intent(AuthentificationActivity.this, ServiceParametresServeurActivity.class);
            AuthentificationActivity.this.startActivity(newIntent);
        } else {
            //Nom de l'établissement
            etablissement = ParametresServeurOpenHelper.getEtablissementNom(db);
        }

        //lancer le lecteur de scan en cliquant sur le datamatrix
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            findViewById(R.id.vers_identification_scan).setOnClickListener(v -> {

                Intent scanDocumentIntent = new Intent(AuthentificationActivity.this, BarcodeCaptureActivity.class);
                Bundle scanDocumentBundle = new Bundle();

                if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell")  || Build.MANUFACTURER.toLowerCase().contains("google"))
                {
                    scanDocumentIntent = new Intent(AuthentificationActivity.this, ScannerSearchOnlyActivity.class);
                    scanDocumentBundle.putInt("scannerContexteInt", R.string.scannerContexteAuthentification);
                }

                scanDocumentBundle.putString("contexte", String.valueOf(R.string.scannerContexteAuthentification));
                scanDocumentBundle.putBoolean("isBoutonSuppressionExistant", true);
                scanDocumentBundle.putBoolean("modeRafale", false);
                scanDocumentBundle.putString("ServiceCourant", "Authentification");
                scanDocumentIntent.putExtras(scanDocumentBundle);
                AuthentificationActivity.this.startActivityForResult(scanDocumentIntent, CodesEchangesActivites.RETOUR_AUTHENTIFICATION);
            });
        } else {
            findViewById(R.id.vers_identification_scan).setVisibility(View.INVISIBLE);
        }

        boutonConnexion = findViewById(R.id.boutonConnexion);
        progressBar = findViewById(R.id.progressBar);

        ((EditText) findViewById(R.id.motDePasse)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    boutonConnexion.callOnClick();
                }
            }
        });

        ((EditText) findViewById(R.id.motDePasse)).setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                boutonConnexion.setVisibility(View.INVISIBLE);
                boutonConnexion.performClick();
            }
            return false;
        });


        // Définition de la fonction à appliquer en cas de clic sur le bouton de connexion
        boutonConnexion.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) AuthentificationActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

            supprimerDonneesTest();

            boutonConnexion.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            // Récupération des objets contenant l'identifiant et du mot de passe de l'utilisateur
            TextView textViewIdentifiant = findViewById(R.id.identifiant);
            TextView textViewMotDePasse = findViewById(R.id.motDePasse);

            // Récupération de l'identifiant et du mot de passe de l'utilisateur
            final String identifiant = textViewIdentifiant.getText().toString();
            String motDePasse = textViewMotDePasse.getText().toString();

            // Vérification qu'un identifiant et un mot de passe ont bien été rentrés
            if (!(motDePasse.isEmpty() || identifiant.isEmpty())) {
                // Encodage du mot de passe
                String motDePasseHache;
                if (authentification_scan) {
                    motDePasseHache = motDePasse;
                    authentification_scan = false;
                } else
                    motDePasseHache = OutilsEncodage.recupererHashageMD5(motDePasse);

                // Appel de la fonction de connexion
                identificationUtilisateur(motDePasseHache, identifiant);
                //identificationUtilisateur("53bfbf997fa54f1b3c4e9ff6637084e6", "jy.aglae");
            } else {
                Toast toast = Toast.makeText(AuthentificationActivity.this, "Veuillez saisir les informations de connexion", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                boutonConnexion.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        // Affichage du nombre d'élément à synchroniser si nécessaire
        int nbElement = ElementASynchroniserOpenHelper.compterElementsASynchroniser(db);
        if (nbElement > 0) {
            nbElementsTextView = findViewById(R.id.nbElements);
            textElementTextView = findViewById(R.id.textElement);
            nbElementsTextView.setVisibility(View.VISIBLE);
            textElementTextView.setVisibility(View.VISIBLE);
            nbElementsTextView.setText(String.valueOf(nbElement));
        }

        // Demande des autorisations
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //récupération IMEI telephone
        if(!Build.MANUFACTURER.toLowerCase().contains("honeywell") && !Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            findViewById(R.id.imageLogo).setOnClickListener(v -> {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(AuthentificationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String device_id;

                if(tm != null && !Build.MANUFACTURER.toLowerCase().contains("crosscall") && !Build.MANUFACTURER.toLowerCase().contains("huawei") && !Build.MANUFACTURER.toLowerCase().contains("zebra") && !Build.MANUFACTURER.toLowerCase().contains("samsung"))
                {
                    device_id = tm.getSubscriberId();
                }
                else
                {
                    device_id= Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                }


                if(device_id != null)
                {
                    if(device_id.contentEquals("6b3405cfd1cf57fd") || device_id.contentEquals("359467074856616") || device_id.contentEquals("42b3cc2997eab48e")|| device_id.contentEquals("283fd36870b99d52") || device_id.contentEquals("865545031537572") || device_id.contentEquals("358439079740070") || device_id.contentEquals("e76b2dc0dc33f6b2") ||device_id.contentEquals("baad6c7f647267d2")||device_id.contentEquals("66e4d0b5f734a6e7") || device_id.contentEquals("356672848915688") || device_id.contentEquals("351921588915688") || device_id.contentEquals("7db4057f77ad69c0") || device_id.contentEquals("352681302875720"))
                    {
                        TextView textViewIdentifiant = findViewById(R.id.identifiant);
                        TextView textViewMotDePasse = findViewById(R.id.motDePasse);

                        /*textViewIdentifiant.setText("alcyons");
                        textViewMotDePasse.setText("65ken64btz");*/
                    }
                }

            });
        }
        else if(Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            TextView textViewIdentifiant = findViewById(R.id.identifiant);
            TextView textViewMotDePasse = findViewById(R.id.motDePasse);

            /*textViewIdentifiant.setText("alcyons");
            textViewMotDePasse.setText("65ken64btz");*/
        }

        // create class object
        gps = new GPSTracker(AuthentificationActivity.this);
        if (!gps.canGetLocation()) {
            //gps.showSettingsAlert();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        nbTableAInserer = 11;
        nbTableinserees = 0;
        activerAuthentificationForte = ParametreUtilisateurOpenHelper.getAuthentificationForte(db);
        connexionDirecte = ParametreUtilisateurOpenHelper.getConnexionDirecte(db);
        boutonConnexion.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            effacerAlerte(alertDialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR:
                    if (Objects.requireNonNull(data.getExtras()).getBoolean("verifier")) {
                        // Si l'identification s'est bien passé, on récupère le plan d'habilitation de l'utilisateur connecté
                        // Activer les notifications pour l'utilisateur
                        if(connexionDirecte)
                        {
                            UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                            versConnexionDirecte();
                        }
                        else
                        {
                            afficherAlerteConnexion();
                            UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);

                            mettreAJourBDD();
                        }
                    } else {
                        Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur", "Le code saisie n'est pas le bon", "alerte");
                    }

                    break;
                case CodesEchangesActivites.RETOUR_AUTHENTIFICATION:
                    String nom_utilisateur = data.getExtras().getString("username");
                    String mot_de_passe = data.getExtras().getString("password");
                    if(nom_utilisateur != null && mot_de_passe != null)
                    {
                        ((EditText) findViewById(R.id.identifiant)).setText(nom_utilisateur);
                        ((EditText) findViewById(R.id.motDePasse)).setText(mot_de_passe.trim());
                        authentification_scan = true;
                        boutonConnexion.performClick();
                        boutonConnexion.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    break;
            }

        }
    }

    public void identificationUtilisateur(final String motDePasseHache, final String identifiant) {
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteUtilisateur;
        if (!statutConnexion) {
            utilisateurConnecte = UtilisateurOpenHelper.identifierUtilisateurLocalement(identifiant, motDePasseHache, db);

            if (utilisateurConnecte != null) {
                Toast toast = Toast.makeText(AuthentificationActivity.this, "Vous êtes en mode hors connexion !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                pretAPasserActiviteSuivante = true;
                passerActiviteSuivante();
            } else {
                Toast toast = Toast.makeText(AuthentificationActivity.this, "Impossible de vous connecter !", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                progressBar.setVisibility(View.GONE);
                boutonConnexion.setVisibility(View.VISIBLE);
            }
        } else {

            requestQueueUtilisateur = Volley.newRequestQueue(this);

            JSONObject body = new JSONObject();
            try {
                body.put("identifiant", identifiant);
                body.put("mdp", motDePasseHache);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
                try {
                    int nbResultats = response.getInt("resultCount");
                    if (nbResultats != 1) {
                        if (nbResultats > 1) {
                            Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete identificationUtilisateur", "alerte");
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        if (nbResultats == 0) {
                            Toast toast = Toast.makeText(AuthentificationActivity.this, "Identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            boutonConnexion.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        // Récupération de l'utilisateur en BDD distante
                        JSONArray utilisateurJsonArray = response.getJSONArray("utilisateur");
                        JSONArray parametresJsonArray = response.getJSONArray("parametres");
                        if (parametresJsonArray.length() == 1) {
                            JSONObject paramatres = parametresJsonArray.getJSONObject(0);
                            String ip = ParametresServeurOpenHelper.getIPServeur(db);
                            String port = ParametresServeurOpenHelper.getPortServeur(db);
                            String apiVersion = ParametresServeurOpenHelper.getAPIVersion(db);
                            Boolean Reliquats_pour_prevision = ParametresServeurOpenHelper.getReliquats_pour_prevision(db);
                            Boolean Liv_indirecte_egal_Cond_achat = ParametresServeurOpenHelper.getLiv_indirecte_egal_Cond_achat(db);
                            String mailPharmacie = paramatres.getString("etablissementMailPharmacie");
                            String pubnubPublishKey = paramatres.getString("pubnubPublishKey");
                            String pubnubSubscribeKey = paramatres.getString("pubnubSubscribeKey");
                            String etablissementNom = paramatres.getString("etablissementNom");
                            int etablissementNumero = paramatres.getInt("etablissementNumero");
                            String etablissementLogoNom = paramatres.getString("etablissementLogoNom");
                            String mailEmetteur = paramatres.getString("Mail_Emetteur");
                            String mdpEmetteur = paramatres.getString("MDP_Emetteur");
                            int smtpPort = paramatres.getInt("SMTP_Port");
                            String smtpServeur = paramatres.getString("SMTP_Serveur");
                            int smtpSession = paramatres.getInt("SMTP_Session");

                            /*
                             * ADH
                             * mailEmetteur : pharmadh@adh-asso.net
                             * mdpEmetteur :
                             * psswordEmetteur = "gbx55df1";
                             * loginEmetteur = "pharmaadh@adh-asso.local";
                             * smtpPort : 465
                             * smtpServeur : mail.adh-asso.net
                             * smtpSession : 1
                             * emeteurID : adh-asso\pharmaadh
                             */
                            String loginEmetteur = "";

                            if(etablissementNom.contentEquals("ADH"))
                            {
                                smtpPort = 25;
                                mailEmetteur = "pharmadh@adh-asso.net";
                                mdpEmetteur = "gbx55df1";
                                smtpServeur = "mail.adh-asso.net";
                                smtpSession = 1;
                                loginEmetteur = "pharmaadh@adh-asso.local";
                            }

                            Boolean plan_de_cueillette = false;
                            Boolean module_transport = paramatres.getInt("Module_Transport")==1;
                            ParametresServeurOpenHelper.updateParametresServeurEnBDD(db, ip, port, apiVersion, mailPharmacie, pubnubPublishKey, pubnubSubscribeKey, etablissementNom, etablissementNumero, etablissementLogoNom, Reliquats_pour_prevision, Liv_indirecte_egal_Cond_achat, plan_de_cueillette,module_transport, mailEmetteur, mdpEmetteur, smtpPort, smtpServeur, smtpSession, loginEmetteur);
                        }

                        token = response.getString("token");
                        utilisateurJson = utilisateurJsonArray.getJSONObject(0);

                        // Connexion de l'utilisateur
                        connexionUtilisateur(utilisateurJson, token);

                    }
                } catch (JSONException exception) {
                    Log.e(TAG, "JSONException :", exception);
                    effacerAlerte(alertDialog);
                    progressBar.setVisibility(View.GONE);
                    boutonConnexion.setVisibility(View.VISIBLE);
                }
            },
                    error -> {
                        Log.e("Idenitifcation Volley", error.toString());
                        Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP identificationUtilisateur", "alerte");
                        effacerAlerte(alertDialog);
                        progressBar.setVisibility(View.GONE);
                        boutonConnexion.setVisibility(View.VISIBLE);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json;charset=utf-8");
                    return params;
                }
            };
            // Adds the JSON object request "obreq" to the request queue
            requestQueueUtilisateur.add(obreq);
        }
    }

    public void geolocaliserUtilisateur() {

        if(gps != null)
        {
            Location location = gps.getLocation();
            if (location != null) {
                utilisateurConnecte.setLocalisation(location);
            }
        }
    }

    public void verifierLocalisationUtilisateur() {
        if(gps != null)
        {
            Location location = gps.getLocation();
            if (location != null) {
                utilisateurConnecte.setLocalisation(location);
            }
        }
    }

    public void connexionUtilisateur(JSONObject utilisateurJson, String token) {
        boolean recuperationUtilisateur = false;

        try {
            utilisateurConnecte = UtilisateurOpenHelper.getUtilisateurByID(db, utilisateurJson.getInt("id"));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :", e);
        }
        if (utilisateurConnecte == null) {
            // Création de l'utilisateur
            utilisateurConnecte = new Utilisateur(utilisateurJson);
            utilisateurConnecte.setToken(token);
            utilisateurConnecte.setLastPerimetre(0);
            long rowID = UtilisateurOpenHelper.insererUnUtilisateurEnBD(db, utilisateurConnecte);
            if (rowID != -1) {
                // Si l'utilisateur n'existe pas, on l'insère en BDD
                recuperationUtilisateur = true;
            }
        } else {
            utilisateurConnecte.setToken(token);
            try {
                utilisateurConnecte.setEtablissement(utilisateurJson.getString("etablissement"));
                UtilisateurOpenHelper.mettreAJourEtablissement(db, utilisateurConnecte);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException :", e);
            }
            // Si l'utilisateur existe, on met à jour son token
            long rowId = UtilisateurOpenHelper.mettreAJourToken(db, utilisateurConnecte);
            if (rowId != -1) {
                recuperationUtilisateur = true;
            }
        }


        if (!recuperationUtilisateur) {
            Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete connexionUtilisateur", "alerte");
        } else {
            //on checked l'authentification forte pour savoir si on le fait ou si on synchronise directement
            if (activerAuthentificationForte) {
                // Si l'identification s'est bien passé on vérifie la connexion de l'utilisateur en lui envoyant un code par mail et notificaiton
                channelTelephone = "Channel_" + utilisateurConnecte.getId() + "_" + etablissement;
                channels.add(channelTelephone);
                verificationUtilisateur(channelTelephone);
            }
            else if(connexionDirecte)
            {
                UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);
                versConnexionDirecte();
            }
            else {

                if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons"))
                {
                    int nbProduit = ProduitOpenHelper.getNbProduit(db);
                    if(nbProduit == 0)
                    {
                        afficherAlerteConnexion();
                        // Si l'identification s'est bien passé, on récupère le plan d'habilitation de l'utilisateur connecté
                        // Activer les notifications pour l'utilisateur
                        UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);

                        mettreAJourBDD();
                    }
                    else
                    {
                        passerActiviteSuivante();
                    }
                }
                else
                {
                    afficherAlerteConnexion();
                    // Si l'identification s'est bien passé, on récupère le plan d'habilitation de l'utilisateur connecté
                    // Activer les notifications pour l'utilisateur
                    UtilisateurOpenHelper.mettreAJourUtilisateur(db, utilisateurConnecte);

                    mettreAJourBDD();
                }

            }
        }

        //Synchronisation des photos non enregistré avec MedicalObjective
        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/DCIM/PhotoASynchroniser/");
        FileInputStream in;
        BufferedInputStream buf;
        try {
            File[] tabFichier = dir.listFiles();
            for (int i = 0; i < Objects.requireNonNull(tabFichier).length; i++) {
                in = new FileInputStream(tabFichier[i]);
                buf = new BufferedInputStream(in);
                byte[] bMapArray = new byte[buf.available()];
                buf.read(bMapArray);
                Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);

                String root2 = Environment.getExternalStorageDirectory().toString();
                File dir2 = new File(root2 + "/DCIM/MedicalObjective");
                dir2.mkdir();
                String nomPhoto = tabFichier[i].toString();
                String[] chainePhoto = nomPhoto.split("/");
                File file = new File(dir2, chainePhoto[chainePhoto.length - 1]);
                if (file.exists()) file.delete();
                FileOutputStream out = new FileOutputStream(file);
                bMap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                tabFichier[i].delete();

                in.close();
                buf.close();
            }
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        recuperationSysUserRules();

    }

    private void recuperationSysUserRules()  {
        String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteSysUserRules+utilisateurConnecte.getId();
        if (!statutConnexion) {
            Toast toast = Toast.makeText(AuthentificationActivity.this, "Impossible de vous connecter !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {

            requestQueueUtilisateur = Volley.newRequestQueue(this);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null, response -> {
                try {
                    int nbResultats = response.getInt("resultCount");
                    if (nbResultats != 1) {
                        if (nbResultats > 1) {
                            Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete identificationUtilisateur", "alerte");
                        }
                        if (nbResultats == 0) {
                            Toast toast = Toast.makeText(AuthentificationActivity.this, "Identifiant ou mot de passe incorrect !", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        SYS_User_RulesOpenHelper.viderTableSYS_User_Rules(db);
                        // Récupération de l'utilisateur en BDD distante
                        JSONArray userRulesJsonArray = response.getJSONArray("SYS_User_Rules");
                        JSONObject userRulesJsonObject = userRulesJsonArray.getJSONObject(0);
                        if(userRulesJsonObject != null)
                        {
                            SYS_User_Rules userRules = new SYS_User_Rules(userRulesJsonObject);
                            SYS_User_RulesOpenHelper.insererSYS_User_RulesEnBDD(db, userRules);
                        }
                    }
                } catch (JSONException exception) {
                    Log.e(TAG, "JSONException :", exception);
                    effacerAlerte(alertDialog);
                }
            },
                    error -> {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(AuthentificationActivity.this, "Erreur HTTP", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : HTTP récupération userRules", "alerte");
                        effacerAlerte(alertDialog);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json;charset=utf-8");
                    params.put("Authorization", utilisateurConnecte.getToken());

                    return params;
                }
            };
            
            // Adds the JSON object request "obreq" to the request queue
            requestQueueUtilisateur.add(obreq);
        }
    }

    public void mettreAJourBDD() {
        String token = utilisateurConnecte.getToken();
        ElementASynchroniserOpenHelper.toutSynchroniser(AuthentificationActivity.this, db, utilisateurConnecte, true);
        ServiceOpenHelper.insererBDDLocaleServicesEtPerimetresFonctionnelsphiwms_mobile(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        ZoneOpenHelper.insererBDDLocaleDepotsZones(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        DepotOpenHelper.insererBDDLocaleDepots(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        EmplacementOpenHelper.insererBDDLocaleDepotsEmplacements(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        ProduitOpenHelper.insererBDDLocaleProduits(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        PH_RetourMotifOpenHelper.insererBDDLocalePH_RetourMotif(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        DotationOpenHelper.insererBDDLocaleDotation(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
        PH_ReassortOpenHelper.insererBDDLocaleReassort(AuthentificationActivity.this, db, token, utilisateurConnecte);
        PH_Demande_MotifOpenHelper.insererBDDLocaleDemandeMotif(AuthentificationActivity.this, db,token, utilisateurConnecte);
        EVENTOpenHelper.insererBDDLocaleEvent(AuthentificationActivity.this, db,token, utilisateurConnecte);
        ActionUtilisateurOpenHelper.insererBDDLocaleActionUtilisatuer(AuthentificationActivity.this, db, token, utilisateurConnecte, statutConnexion);
    }

    public void insertionDeTableEffectuee(String tableNom, boolean etat, String erreur) {

        if (!etat) {
            tabErreur.add(erreur);
        }

        nbTableinserees++;

        updateAlerte(tableNom, nbTableinserees, etat);
        if (nbTableinserees == nbTableAInserer) {
            if (!tabErreur.isEmpty()) {
                RapportErreur();
            } else {
                passerActiviteSuivante();
            }
        }
    }

    public void afficherAlerteConnexion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthentificationActivity.this);
        LayoutInflater inflater = AuthentificationActivity.this.getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.alert_connexion, null);


        initialisationAlerte(layout, builder);
    }

    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    private void updateAlerte(String tableNom, int nbTableinserees, boolean etat) {
        i = nbTableinserees - 1;

        if (nbTableinserees > 13) {
            i = textViewList.size() - 1;
        }

        for(TextView textView: textViewList){
            if(textView.getText().toString().contentEquals(tableNom)){
                int position = textViewList.indexOf(textView);
                if (etat) {
                    textViewList.get(position).setTextColor(getResources().getColor(R.color.noir, null));
                    imageViewList.get(position).setBackgroundTintList(getResources().getColorStateList(R.color.vert, null));
                } else {
                    textViewList.get(position).setTextColor(getResources().getColor(R.color.noir, null));
                    imageViewList.get(position).setBackground(getResources().getDrawable(R.drawable.ic_clear, null));
                    imageViewList.get(position).setBackgroundTintList(getResources().getColorStateList(R.color.rouge, null));
                }
                break;
            }
        }

        zoneok.setProgress(nbTableinserees);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialisationAlerte(View layout, AlertDialog.Builder builder) {

        TextView service = layout.findViewById(R.id.Service);
        textViewList.add(service);
        TextView depot = layout.findViewById(R.id.depot);
        textViewList.add(depot);
        TextView Zone = layout.findViewById(R.id.Zone);
        textViewList.add(Zone);
        TextView Emplacement = layout.findViewById(R.id.Emplacement);
        textViewList.add(Emplacement);
        TextView produit = layout.findViewById(R.id.Produit);
        textViewList.add(produit);
        TextView Motif = layout.findViewById(R.id.Motif);
        textViewList.add(Motif);
        TextView Dotation = layout.findViewById(R.id.Dotation);
        textViewList.add(Dotation);
        TextView Action = layout.findViewById(R.id.Action);
        textViewList.add(Action);
        TextView Reassort = layout.findViewById(R.id.Reassort);
        textViewList.add(Reassort);
        TextView MotifDemande = layout.findViewById(R.id.MotifDemande);
        textViewList.add(MotifDemande);
        TextView Event = layout.findViewById(R.id.Event);
        textViewList.add(Event);

        zoneok = layout.findViewById(R.id.barDeProgression);
        zoneok.setOnTouchListener((v, event) -> true);

        TextSynchronisation = layout.findViewById(R.id.TextSynchronisation);

        ImageView checkService = layout.findViewById(R.id.checkService);
        imageViewList.add(checkService);
        ImageView checkDepot = layout.findViewById(R.id.checkDepot);
        imageViewList.add(checkDepot);
        ImageView checkZone = layout.findViewById(R.id.checkZone);
        imageViewList.add(checkZone);
        ImageView checkEmplacement = layout.findViewById(R.id.checkEmplacement);
        imageViewList.add(checkEmplacement);
        ImageView checkProduit = layout.findViewById(R.id.checkProduit);
        imageViewList.add(checkProduit);
        ImageView checkMotif = layout.findViewById(R.id.checkMotif);
        imageViewList.add(checkMotif);
        ImageView checkDotation = layout.findViewById(R.id.checkDotation);
        imageViewList.add(checkDotation);
        ImageView checkAction = layout.findViewById(R.id.checkAction);
        imageViewList.add(checkAction);
        ImageView checkReassort = layout.findViewById(R.id.checkReassort);
        imageViewList.add(checkReassort);
        ImageView checkMotifDemande = layout.findViewById(R.id.checkMotifDemande);
        imageViewList.add(checkMotifDemande);
        ImageView checkEvent = layout.findViewById(R.id.checkEvent);
        imageViewList.add(checkEvent);

        gestionExpandable = layout.findViewById(R.id.gestionExpandable);

        builder.setView(layout);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    private void effacerAlerte(AlertDialog alertDialog) {
        if(alertDialog != null)
        {
            if(alertDialog.isShowing())
            {
                alertDialog.dismiss();
            }
        }
    }

    public void RapportErreur() {
        gestionExpandable.setVisibility(View.VISIBLE);
       // expandableLayout1.toggle();
        zoneok.setVisibility(View.GONE);
        TextSynchronisation.setVisibility(View.GONE);

        for (int j = 0; j < imageViewList.size(); j++) {
            if (imageViewList.get(j).getBackgroundTintList() == getResources().getColorStateList(R.color.vert, null)) {
                imageViewList.get(j).setVisibility(View.GONE);
                textViewList.get(j).setVisibility(View.GONE);
            }
        }
        voirErreur();
    }

    //Ouvrir l'expandable pour consulter les erreur
    public void voirErreur() {

       /* final TextView messageErreur = (TextView) expandableLayout1.findViewById(R.id.messageErreur);
        LinearLayout ok = (LinearLayout) expandableLayout1.findViewById(R.id.boutonOk);
        ImageView suivant = (ImageView) expandableLayout1.findViewById(R.id.suivant);
        ImageView precedent = (ImageView) expandableLayout1.findViewById(R.id.precedent);
        final TextView nombreMessageErreur = (TextView) expandableLayout1.findViewById(R.id.nombreMessageErreur);
        final int[] positionMessage = {0};

        messageErreur.setText(tabErreur.get(positionMessage[0]));
        int nombreMessage = positionMessage[0] + 1;
        nombreMessageErreur.setText(nombreMessage + "/" + tabErreur.size());
        suivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionMessage[0] = positionMessage[0] + 1;
                if (positionMessage[0] >= tabErreur.size()) {
                    positionMessage[0] = 0;
                }

                messageErreur.setText(tabErreur.get(positionMessage[0]));
                int nombreMessage = positionMessage[0] + 1;
                nombreMessageErreur.setText(nombreMessage + "/" + tabErreur.size());

            }
        });

        precedent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionMessage[0] = positionMessage[0] - 1;
                if (positionMessage[0] < 0) {
                    positionMessage[0] = tabErreur.size() - 1;
                }

                messageErreur.setText(tabErreur.get(positionMessage[0]));
                int nombreMessage = positionMessage[0] + 1;
                nombreMessageErreur.setText(nombreMessage + "/" + tabErreur.size());
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passerActiviteSuivante();
            }
        });*/

    }

    public void verificationUtilisateur(String channelTelephone) {
        Intent intent = new Intent(AuthentificationActivity.this, VerificationConnexionActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putString("channel", channelTelephone);
        intent.putExtras(extras);
        AuthentificationActivity.this.startActivityForResult(intent, CodesEchangesActivites.RESULT_VERIFICATION_UTILISATEUR);
    }

    public void versConnexionDirecte() {
        Intent intent = new Intent(AuthentificationActivity.this, ServiceConnexionDirecteActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        intent.putExtras(extras);
        AuthentificationActivity.this.startActivity(intent);
    }

    public void passerActiviteSuivante() {

        geolocaliserUtilisateur();
        verifierLocalisationUtilisateur();
        Intent intent = new Intent(AuthentificationActivity.this, NavigationActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        extras.putString("etablissement", etablissement);
        intent.putExtras(extras);
        AuthentificationActivity.this.startActivity(intent);
        AuthentificationActivity.this.finish();
    }

    public void supprimerDonneesTest()
    {
        DepotOpenHelper.supprimerDonneesTest(db);
        ZoneOpenHelper.supprimerDonneesTest(db);
        EmplacementOpenHelper.supprimerDonneesTest(db);
        ProduitOpenHelper.supprimerDonneesTest(db);
        FournisseurOpenHelper.supprimerDonneesTest(db);
        PH_PreparationOpenHelper.supprimerDonneesTest(db);
        PH_Preparation_LigneOpenHelper.supprimerDonneesTest(db);
        RetourOpenHelper.supprimerDonneesTest(db);
        Retour_LigneOpenHelper.supprimerDonneesTest(db);
        CommandeOpenHelper.supprimerDonneesTest(db);
        PH_ReliquatOpenHelper.supprimerDonneesTest(db);
    }
}