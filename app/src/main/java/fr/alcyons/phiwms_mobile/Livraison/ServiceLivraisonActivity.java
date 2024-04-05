package fr.alcyons.phiwms_mobile.Livraison;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.DocumentException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.AuthentificationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerDocumentActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Lot_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ConnexionDirecte.ServiceConnexionDirecteActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.PAD.DetailPADActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ReceptionPAD.DetailReceptionPadActivity;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ServiceLivraisonActivity extends ServiceAvecConnexionActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Context context;
    List<PH_Preparation> ph_preparation_List;
    List<PH_Preparation> ph_preparation_List_base;
    ListView ph_preparation_ListView;
    PH_Preparation_LivraisonAdapter ph_preparation_livraisonAdapter;
    List<String> listeDate;

    JSONArray ph_preparation_JSONArray;
    PackageManager pm;

    boolean connexionDirecte;

    List<String> listeDepotLivraison;
    ArrayAdapter<String> spinnerArrayAdapter;
    Spinner spinner;
    Dialogue dialogue;
    String signatureNameChauffeur;
    String filename;
    List<PH_Preparation_Ligne> ph_preparation_ligne_List;
    String photoProduitsChemin;
    Bitmap photoLivraisonBitmap;
    String photoLivraisonPhotoName;
    String subject;
    String body;
    Dialog alertePatientezDialog;
    ActionUtilisateur new_action_utilisateur;
    /**
     * TODO  : faire un écran supplémentaire qui présente les points de livraisons
     * Clic point de livraison : présente les préparations avec possiblité de tout valider
     * Faire un seul mail avec toutes les pièces jointes
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_livraison);
        context = ServiceLivraisonActivity.this;

        pm = ServiceLivraisonActivity.this.getPackageManager();
        listeDepotLivraison = new ArrayList<>();
        listeDepotLivraison.add("Tous");
        //Gestion de la listView
        ph_preparation_ListView = (ListView) findViewById(R.id.listeView);
        ph_preparation_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (ph_preparation_livraisonAdapter.sectionHeader.contains(position) == false) {
                    PH_Preparation ph_preparation_Selectionne = ph_preparation_livraisonAdapter.getItem(position);
                    Intent serviceLivraison_Intent = new Intent(ServiceLivraisonActivity.this, InformationLivraisonActivity.class);
                    Bundle serviceLivraison_Bundle = ServiceLivraisonActivity.super.getBundle();
                    serviceLivraison_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                    serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                    ServiceLivraisonActivity.this.startActivity(serviceLivraison_Intent);
                }
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
        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceLivraisonActivity.this) && passageParOnCreate && !connexionDirecte) {
            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(ServiceLivraisonActivity.this, "Veuillez patienter", "Synchronisation des livraisons en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(ServiceLivraisonActivity.this);


            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteServiceLivraison;

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,
                    new Response.Listener<JSONObject>() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int resultCount = response.getInt("resultCount");
                                if (resultCount == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                        DBOpenHelper.viderBasesDeDonnees(db);
                                        ServiceLivraisonActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                        ServiceLivraisonActivity.this.finishAffinity();
                                        Intent intent = new Intent(context, AuthentificationActivity.class);
                                        context.startActivity(intent);
                                    } else if (!erreur.contentEquals("Aucun PH_Preparation trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Livraison", "alerte");
                                        ServiceLivraisonActivity.this.finishAffinity();
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
                                        long rowID = gestionnairePH_Preparation.insererUnPH_PreparationEnBDD(db, ph_preparation);
                                        if (rowID != -1) {
                                            JSONArray ph_preparationLignesJson = ph_preparation_JSONObject.getJSONArray("ph_preparation_lignes");
                                            for (int k = 0; k < ph_preparationLignesJson.length(); k++) {
                                                gestionnairePH_Preparation_Ligne.insererUnPH_Preparation_LigneEnBDD(db, new PH_Preparation_Ligne(ph_preparationLignesJson.getJSONObject(k)));
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
                                e.printStackTrace();
                            }
                            handler.sendMessage(handler.obtainMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                            Alerte.afficherAlerte(ServiceLivraisonActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Livraison)", "alerte");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
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
        else {
            ph_preparation_List = gestionnairePH_Preparation.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId());
            ph_preparation_List_base = gestionnairePH_Preparation.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId());
            if (ph_preparation_List.size() == 0) {
                if(connexionDirecte)
                {
                    Intent retourVersServiceConnexionDirectIntent = new Intent(ServiceLivraisonActivity.this, ServiceConnexionDirecteActivity.class);
                    Bundle retourVersServiceConnexionDirectBundle = new Bundle();
                    retourVersServiceConnexionDirectBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    retourVersServiceConnexionDirectBundle.putBoolean("snackBar", true);
                    retourVersServiceConnexionDirectBundle.putString("nomService", "Livraison");

                    retourVersServiceConnexionDirectIntent.putExtras(retourVersServiceConnexionDirectBundle);
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
                connexionDirecte = !connexionDirecte;

        }

        // Tri par Date : de la plus récente à la plus ancienne
        Collections.sort(ph_preparation_List, new Comparator<PH_Preparation>() {
            @Override
            public int compare(PH_Preparation o1, PH_Preparation o2) {
                return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
            }
        });


        ph_preparation_livraisonAdapter = new PH_Preparation_LivraisonAdapter(ServiceLivraisonActivity.this, db, utilisateurConnecte);

        for (PH_Preparation ph_courant : ph_preparation_List) {
            if (listeDate.indexOf(ph_courant.getLivraisonPrevueDate()) == -1) {
                listeDate.add(ph_courant.getLivraisonPrevueDate());
                ph_preparation_livraisonAdapter.addSectionHeaderItem(ph_courant);
            }

            ph_preparation_livraisonAdapter.addItem(ph_courant);
        }


        ph_preparation_ListView.setDivider(footer);
        ph_preparation_ListView.setAdapter(ph_preparation_livraisonAdapter);


        int taille_liste = ph_preparation_List.size();
        String titre = "Livraisons";
        if(taille_liste < 2)
            titre = "Livraison";

        /* Code nécessaire à l'affichage de la liste */
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(taille_liste));
        ((TextView) findViewById(R.id.titre)).setText(titre);

        if (ph_preparation_List.size() == 0) {
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
                    if(((TextView) parent.getChildAt(0)) != null)
                    {
                        ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                    }
                    String depot = spinner.getItemAtPosition(position).toString();

                    ph_preparation_List = new ArrayList<>();
                    listeDate = new ArrayList<>();

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

                    // Tri par Date : de la plus récente à la plus ancienne
                    Collections.sort(ph_preparation_List, new Comparator<PH_Preparation>() {
                        @Override
                        public int compare(PH_Preparation o1, PH_Preparation o2) {
                            return o1.getLivraisonPrevueDate().compareTo(o2.getLivraisonPrevueDate());
                        }
                    });


                    ph_preparation_livraisonAdapter = new PH_Preparation_LivraisonAdapter(ServiceLivraisonActivity.this, db, utilisateurConnecte);

                    for (PH_Preparation ph_courant : ph_preparation_List) {
                        if (listeDate.indexOf(ph_courant.getLivraisonPrevueDate()) == -1) {
                            listeDate.add(ph_courant.getLivraisonPrevueDate());
                            ph_preparation_livraisonAdapter.addSectionHeaderItem(ph_courant);
                        }

                        ph_preparation_livraisonAdapter.addItem(ph_courant);
                    }


                    ph_preparation_ListView.setDivider(footer);
                    ph_preparation_ListView.setAdapter(ph_preparation_livraisonAdapter);


                    int taille_liste = ph_preparation_List.size();
                    String titre = "Livraisons";
                    if(taille_liste < 2)
                        titre = "Livraison";

                    /* Code nécessaire à l'affichage de la liste */
                    ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(taille_liste));
                    ((TextView) findViewById(R.id.titre)).setText(titre);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });


            invalidateOptionsMenu();
        }
    }

    public void viderTablesConcernees() {
        for (PH_Preparation ph_preparation : gestionnairePH_Preparation.getAllPHPreparationLivraisons(db, ParametresServeurOpenHelper.getModuleTransport(db), this.utilisateurConnecte.getId())
                ) {
            List<PH_Preparation_Ligne> ph_preparation_lignes = gestionnairePH_Preparation_Ligne.getAllPHPreparationLignesParPHPreparation(db, ph_preparation);
            for (PH_Preparation_Ligne ph_preparation_ligne : ph_preparation_lignes) {
                //suppression des ph_lot_ligne en bdd
                gestionnairePH_Lot_Ligne.supprimerPH_LotLigne(db, ph_preparation_ligne.get_UID());
                gestionnairePH_Preparation_Ligne.supprimerUnPhPreparationLigne(db, ph_preparation_ligne);
            }
            gestionnairePH_Preparation.supprimerUnPhPreparation(db, ph_preparation);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuDatamatrix);
        MenuItem itemValider = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                lancerScan();
                return true;
            }
        });

        itemValider.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                toutValider();
                return true;
            }
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
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }

    public void toutValider()
    {
        afficherAlerteValidationAllLivraison(ServiceLivraisonActivity.this, ServiceLivraisonActivity.this.getLayoutInflater());
    }

    public void afficherAlerteValidationAllLivraison(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_validation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView textDialog = (TextView) layout.findViewById(R.id.messageFin);
        textDialog.setText("Souhaitez-validez toutes les livraisons ?");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfficherSignature();
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void AfficherSignature()
    {
        dialogue = new Dialogue(ServiceLivraisonActivity.this, clicValidationSignatureAllLivraison, utilisateurConnecte);
        dialogue.signaturePadOpen(true);
    }

    public View.OnClickListener clicValidationSignatureAllLivraison = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Création du pdf
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateDuJour = new Date();
            date = dateFormat.format(dateDuJour);

            for(PH_Preparation ph_preparation_courant : ph_preparation_List)
            {
                filename = String.valueOf(ph_preparation_courant.getUID()) + "_" + date + "_Livraison.pdf";
                signatureNameChauffeur = String.valueOf(ph_preparation_courant.getUID()) + "_" + date + "_LivraisonSignature";

                //Sauvegarde de la signature dans une image
                verifyStoragePermissions(ServiceLivraisonActivity.this);
                String content = "";
                Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
                OutilsGestionPhotos.saveExternalStorageImageJPEG(ServiceLivraisonActivity.this, bitmap, signatureNameChauffeur);

                if(bitmap != null)
                {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String img_str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    ph_preparation_courant.setSignature_Livraison(img_str);
                }
            }

            dialogue.dialog.dismiss();

            dialogue = new Dialogue(ServiceLivraisonActivity.this, onClickListenerValiderAllLivraison, utilisateurConnecte);
            dialogue.padCommentairePhotoLivraison();
        }
    };

    View.OnClickListener onClickListenerValiderAllLivraison = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertePatientezDialog = new Dialog(ServiceLivraisonActivity.this);
            alertePatientezDialog.setContentView(R.layout.alerte_patientez);
            alertePatientezDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = alertePatientezDialog.getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            if (!alertePatientezDialog.isShowing()) {
                alertePatientezDialog.show();
            }
            //Récupération du commentaire
            String commentaireSaisie = dialogue.commentaireEditText.getText().toString();
            for(PH_Preparation ph_preparation_Selectionne : ph_preparation_List) {
                if (!commentaireSaisie.contentEquals("")) {
                    ph_preparation_Selectionne.setCommentaires(commentaireSaisie);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateDuJour = new Date();
                String date = dateFormat.format(dateDuJour);
                ph_preparation_Selectionne.setLivraisonDate(date);
                ph_preparation_Selectionne.setLivree(true);
                PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation_Selectionne);

                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation_Selectionne.getphiwms_mobileUUID(), ph_preparation_Selectionne.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                // Tentative de lancer la sychronisation
                if (OutilsGestionConnexionReseau.isServerAccessible(ServiceLivraisonActivity.this)) {
                    ElementASynchroniserOpenHelper.toutSynchroniser(ServiceLivraisonActivity.this, db, utilisateurConnecte, true);
                }

                //Création de l'action utilisateur
                Random random = new Random();
                int actionId = random.nextInt();
                if (actionId > 0)
                    actionId = actionId * -1;
                SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateDestruction = new Date();
                String date_string = parseFormat.format(dateDestruction);
                new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", ph_preparation_Selectionne.getUID(), "", "Livraison");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                //fin de la création de l'action utilisateur

                ph_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getALivrerPHPreparationLignesParPHPreparation(db, ph_preparation_Selectionne);
                //mise à jour des PH_Preparations Ligne dans la BDD
                for (PH_Preparation_Ligne preparation_ligne_courant : ph_preparation_ligne_List) {
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, preparation_ligne_courant);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, preparation_ligne_courant.getphiwms_mobileUUID(), preparation_ligne_courant.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

                    //gestion des actions lignes
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if (actionligneId > 0)
                        actionligneId = actionligneId * -1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Ph_Preparation_Ligne", preparation_ligne_courant.get_UID(), "", 0, (int) preparation_ligne_courant.getQte_livrer(), preparation_ligne_courant.getProduitDesignation());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }


                //Construction mail
                Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation_Selectionne.getDepotDestinataireID());
                String commentaire = ph_preparation_Selectionne.getCommentaires();
                String depotNom = "";

                if(depot != null)
                    depotNom = depot.getNom();
                if (commentaire == null || commentaire.contentEquals("")) {
                    commentaire = "Pas de commentaire saisie";
                }


                subject = "phiwms_mobile - " + depotNom + " - Livraisons N°" + ph_preparation_Selectionne.getUID() + " - " + date;

                //Sauvegarde de la signature dans une image
                if (photoLivraisonBitmap != null) {

                    dateFormat = new SimpleDateFormat("yyyyMMdd");
                    dateDuJour = new Date();
                    date = dateFormat.format(dateDuJour);

                    photoLivraisonPhotoName = String.valueOf(ph_preparation_Selectionne.getUID()) + "_" + date + "_LivraisonPhoto";

                    verifyStoragePermissions(ServiceLivraisonActivity.this);
                }

                String preparer_par = "";
                String valider_par = "";
                String livrer_par = "";
                Utilisateur userLivreur = UtilisateurOpenHelper.getUtilisateurByID(db, ph_preparation_Selectionne.getLivreur_userID());
                if (userLivreur != null) {
                    livrer_par = userLivreur.getNom() + " " + userLivreur.getPrenom();
                }
                if (ph_preparation_Selectionne.getPreparateur() != null) {
                    String[] tab_preparateur = ph_preparation_Selectionne.getPreparateur().split("\\(");
                    preparer_par = tab_preparateur[0];
                    String[] tab_valider_par = tab_preparateur[1].split("\\)");
                    valider_par = tab_valider_par[0];
                }

                //Verifier le livré par
                body = "Madame, Monsieur, \n \n" +
                        "La livraison N°" + ph_preparation_Selectionne.getUID() + " à destination de " + depotNom + " a été réalisée. \n" +
                        "Préparé par " + preparer_par + "\n" +
                        "Validé par " + valider_par + "\n" +
                        "Livré par " + livrer_par + "\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison signé. \n" +
                        "Commentaire : " + commentaire + "\n\n" +
                        "Ceci est un message automatique merci de ne pas répondre\n\n";

                try {
                    OutilsGestionPDF outilsGestionPDF = new OutilsGestionPDF(true);
                    outilsGestionPDF.createLivraisonV2(ServiceLivraisonActivity.this, filename, signatureNameChauffeur, db, ph_preparation_Selectionne);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

                // Récupération Mail Pharmacie
                String email = ParametresServeurOpenHelper.getMailPharmacie(db);
                if (utilisateurConnecte.getEtablissement().contentEquals("ADH")) {
                    email = "livraison.pui@adh-asso.net";
                }
                if (utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS")) {
                    email = "dev01@alcyons.fr";
                }

                if (email != null) {
                    new SendEmailTask().execute(email);
                }

                dialogue.dialog.dismiss();
                ph_preparation_Selectionne.setLivreur_userID(utilisateurConnecte.getId());
                mettreAJourPhPreparation(ph_preparation_Selectionne);
                Toast.makeText(ServiceLivraisonActivity.this, "Livraison effectuée", Toast.LENGTH_SHORT).show();
            }
            alertePatientezDialog.dismiss();

            onBackPressed();
        }
    };

    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(ServiceLivraisonActivity.this, email[0], true, db);
            try {
                if(filename == null || filename.contentEquals(""))
                {
                    sender.sendMailVerification(subject, body);
                }
                else if(photoLivraisonPhotoName == null || photoLivraisonPhotoName.contentEquals(""))
                {
                    sender.sendMail(subject, body, "Documents/"+filename);
                }
                else
                {
                    sender.sendMailPDFAndPhoto(subject, body, "Documents/"+filename, "Documents/"+photoLivraisonPhotoName + ".jpeg");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    public void mettreAJourPhPreparation(PH_Preparation ph_preparation) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateDuJour = new Date();
        String date = dateFormat.format(dateDuJour);

        ph_preparation.setLivree(true);
        ph_preparation.setLivraisonDate(date);
        if (ph_preparation.getStatut().contains("en")) {
            ph_preparation.setStatut("Délivrée en partie");
        } else {
            ph_preparation.setStatut("Délivrée");
        }

        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, ph_preparation);

        // Ajout du PH_Preparation au ElementASynchroniser
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparation.getphiwms_mobileUUID(), ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getphiwms_mobileUUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        // Tentative de lancer la sychronisation
        if (OutilsGestionConnexionReseau.isServerAccessible(ServiceLivraisonActivity.this)) {
            ElementASynchroniserOpenHelper.toutSynchroniser(ServiceLivraisonActivity.this, db, utilisateurConnecte, true);
        }
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
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RETOUR_DOCUMENT: {
                if (data != null) {
                    String code_recu = data.getExtras().getString("code");
                    if (code_recu != null) {
                        String code ="";
                        if(code_recu.startsWith("DDS"))
                        {
                            code = code_recu.substring(3);
                        }
                        int idPreparation = 0;
                        try {
                            idPreparation = Integer.parseInt(code);
                        } catch (NumberFormatException e) {
                            idPreparation = 0;
                        }
                        PH_Preparation ph_preparation_Selectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, idPreparation);
                        if(ph_preparation_Selectionne != null)
                        {
                            Intent serviceLivraison_Intent = new Intent(ServiceLivraisonActivity.this, InformationLivraisonActivity.class);
                            Bundle serviceLivraison_Bundle = ServiceLivraisonActivity.super.getBundle();
                            serviceLivraison_Bundle.putInt("ph_preparationUID_Selectionne", ph_preparation_Selectionne.getUID());
                            serviceLivraison_Intent.putExtras(serviceLivraison_Bundle);
                            ServiceLivraisonActivity.this.startActivity(serviceLivraison_Intent);
                            ServiceLivraisonActivity.this.finish();

                            invalidateOptionsMenu();
                        }
                        else
                        {
                            if(code_recu.startsWith("DDS"))
                            {
                                afficherSnackBarLivraison();
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    public void afficherSnackBarLivraison() {
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Document scanné inconnu</b>", 0), Snackbar.LENGTH_LONG);;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}