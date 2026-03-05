package fr.alcyons.phiwms_mobile.Reception;

import static com.google.android.gms.vision.L.TAG;
import static fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos.verifyStoragePermissions;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ImprimanteEtiquetteOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.ImprimanteEtiquette;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.EtiquetteZebraAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Reliquat_ReceptionAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPadActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceReceptionPuiActivity;

public class DetailReceptionActivity extends ServiceActivity {
    Commande commandeSelectionne;
    List<PH_Reliquat> phReliquatList;
    ListView phReliquatListView;
    PH_Reliquat_ReceptionAdapter phReliquatReceptionAdapter;
    PH_Reliquat_ReceptionAdapter.PH_Reliquat_ReceptionViewHolder phReliquatReceptionViewHolder;
    String bonLivraison = "";
    String subject = "";
    String body = "";
    String bonLivraisonPhotoName = "";
    Boolean orientation = false;
    boolean check_lot_present;
    boolean envoyerCopie;
    String EmailCopie;
    Bitmap bonLivraisonBitmap;
    String erreur ="";
    boolean second_passage_photo;
    List<String> listeProduitRAL = new ArrayList<>();
    String tri_choisi;
    LinearLayout lancerScan;
    Depot_Emplacement emplacement_precedent;
    Produit produitPrecedent;
    MenuItem item;
    List<ImprimanteEtiquette> listeImprimanteEtiquette;

    Spinner optionTri;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reception);

        //initilisation de l'emplacement précédent
        emplacement_precedent = null;
        produitPrecedent = null;

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));

        orientation = false;
        check_lot_present = false;
        second_passage_photo = false;

        //gestion de la rotation de l'écran
        if (savedInstanceState != null) {
            bonLivraison = savedInstanceState.getString("NomBonLivraison");
            phReliquatList = (List<PH_Reliquat>) savedInstanceState.getSerializable("listePHReliquat");
            orientation = savedInstanceState.getBoolean("Orientation");
            phReliquatListView = (ListView) findViewById(R.id.liste_view);
            onResume();
        }

        listeImprimanteEtiquette = ImprimanteEtiquetteOpenHelper.getAllImprimante(db);
        if(listeImprimanteEtiquette.size() > 0)
        {
            ((LinearLayout)findViewById(R.id.printEtiquette)).setVisibility(View.VISIBLE);

            ((LinearLayout) findViewById(R.id.printEtiquette)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    afficherAlerteSelectionEtiquette(DetailReceptionActivity.this, DetailReceptionActivity.this.getLayoutInflater());
                }
            });
        }

        if(commandeSelectionne.getCommentaire() != null && !commandeSelectionne.getCommentaire().contentEquals(""))
        {
            ((LinearLayout) findViewById(R.id.informationPreparation)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alerte.afficherAlerteInformation(DetailReceptionActivity.this, getLayoutInflater(), "Commentaire", commandeSelectionne.getCommentaire(), false, false);
                }
            });
        }
        else
        {
            ((LinearLayout) findViewById(R.id.informationPreparation)).setOnClickListener(null);
            ((LinearLayout) findViewById(R.id.informationPreparation)).setAlpha(0.3F);
        }

        optionTri = (Spinner) findViewById(R.id.optionTri);
        phReliquatListView = (ListView) findViewById(R.id.liste_view);
        phReliquatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PH_Reliquat reliquatcourant = phReliquatReceptionAdapter.ph_reliquat_liste.get(position);

                if (reliquatcourant != null) {
                    Intent detailReception_Intent = new Intent(DetailReceptionActivity.this, ListeLotReceptionActivity.class);
                    Bundle detailReception_Bundle = DetailReceptionActivity.super.getBundle();
                    detailReception_Bundle.putInt("commandeID_Selectionne", commandeSelectionne.getID_commande());
                    detailReception_Bundle.putInt("phReliquatId", reliquatcourant.getReliquat_UID());
                    detailReception_Bundle.putSerializable("EmplacementPrecedent", emplacement_precedent);
                    detailReception_Bundle.putSerializable("ProduitPrecedent", produitPrecedent);
                    detailReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));

                    detailReception_Intent.putExtras(detailReception_Bundle);

                    DetailReceptionActivity.this.startActivityForResult(detailReception_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
                }
            }
        });

        // Récupération da la commande selectionné
        if (commandeSelectionne != null) {
            // Entete
            ((TextView) findViewById(R.id.nomFournisseur)).setText(commandeSelectionne.getFournisseur());
            ((TextView) findViewById(R.id.numCommande)).setText("#" + commandeSelectionne.getNumero());

            phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeSelectionne.getNumero());

            phReliquatList.sort(Comparator.comparing(PH_Reliquat::getdesignationCourte));


        } else {
            DetailReceptionActivity.this.finish();
        }

        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // drapeau pour ignorer le premier appel

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false; // on consomme le premier appel
                    return; // ne rien faire au lancement
                }

                if (((TextView) parent.getChildAt(0)) != null) {
                    ((TextView) parent.getChildAt(0)).setVisibility(View.INVISIBLE);
                }
                tri_choisi = optionTri.getItemAtPosition(position).toString();
                ParametreUtilisateurOpenHelper.mettreAJourTriReliquat(db, 0, tri_choisi);

                switch (tri_choisi)
                {
                    case "Categorie":
                        onClickTriCategorie();
                        break;
                    case "Designation":
                        onClickTriDesignation();
                        break;

                    case "Place":
                        onTriParPlace();
                        break;
                    case "Poids":
                        onTriParPoids();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                List<PH_Reliquat> listeReliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, commandeSelectionne.getNumero());
                if(!listeReliquatReceptionne.isEmpty())
                    Alerte.afficherAlerteConfirmation(DetailReceptionActivity.this, getLayoutInflater(), getBundle(), "Vous allez quitter la réception, confirmez vous ?", true, false,DetailReceptionActivity.this);
                else
                    retourService(DetailReceptionActivity.super.getBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //initi du tri
        tri_choisi = ParametreUtilisateurOpenHelper.getChoixTriReliquat(db);
        lancerScan = (LinearLayout) findViewById(R.id.lancerScan);
        //gestion du bouton qui lance le scan
        lancerScan.setOnClickListener(view -> {
            Intent listeLotReception_Intent;
            Bundle listeLotReception_Bundle = new Bundle();
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                listeLotReception_Intent = new Intent(DetailReceptionActivity.this, ScannerReceptionActivity.class);
            }
            else
            {
                listeLotReception_Intent = new Intent(DetailReceptionActivity.this, BarcodeReceptionActivity.class);
            }

            listeLotReception_Bundle.putString("contexte", String.valueOf(R.string.scannerContextReceptionListe));
            listeLotReception_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
            listeLotReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            listeLotReception_Bundle.putString("ordreTri", tri_choisi);
            listeLotReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
            listeLotReception_Bundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_precedent);
            listeLotReception_Bundle.putSerializable("ProduitPrecedent", (Serializable) produitPrecedent);
            listeLotReception_Intent.putExtras(listeLotReception_Bundle);
            DetailReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
        });

        switch (tri_choisi)
        {
            case "Categorie":
                onClickTriCategorie();
                break;
            case "Designation":
                onClickTriDesignation();
                break;

            case "Place":
                onTriParPlace();
                break;
            case "Poids":
                onTriParPoids();
                break;
        }


        invalidateOptionsMenu();
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        phReliquatList.sort((o1, o2) -> {
            if(o2 == null || o1 == null)
                return 1;
            else
                return o1.getDesignationCourte().toLowerCase().compareTo(o2.getDesignationCourte().toLowerCase());

        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, utilisateurConnecte);
        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Reliquat ph_reliquat : phReliquatList)
        {
            if(ph_reliquat != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    phReliquatReceptionAdapter.addSectionHeaderItem(ph_reliquat);
                }

                phReliquatReceptionAdapter.addItem(ph_reliquat);
            }
        }
        //phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Categorie";
        phReliquatList.sort((o1, o2) -> {

            if(o1 == null || o2 == null)
            {
                return 1;
            }
            else
            {
                Produit produit1 = ProduitOpenHelper.getProduitByID(db, o1.getProduitID());
                Produit produit2 = ProduitOpenHelper.getProduitByID(db, o2.getProduitID());

                if(produit1 == null || produit2 == null)
                {
                    return 1;
                }
                else
                {
                    return produit1.getCategorie().toLowerCase().compareTo(produit2.getCategorie().toLowerCase());
                }
            }
        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, utilisateurConnecte);
        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Reliquat ph_reliquat : phReliquatList)
        {
            if(ph_reliquat != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    phReliquatReceptionAdapter.addSectionHeaderItem(ph_reliquat);
                }

                phReliquatReceptionAdapter.addItem(ph_reliquat);
            }
        }
        //phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    private void onTriParPlace()
    {
        tri_choisi = "Place";
        phReliquatList.sort((o1, o2) -> {
            if(o2 == null || o1 == null)
                return 1;
            else
            {
                String oo1EmplacementParDefaut = o1.getEmplacement();
                String oo2EmplacementParDefaut = o2.getEmplacement();
                String oo1ZoneDefaut = o1.getZone();
                String oo2ZoneDefaut = o2.getZone();

                if (oo1EmplacementParDefaut == null || oo1EmplacementParDefaut.contentEquals("")) {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, o1.getProduitID());
                    oo1EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();

                }
                if (oo2EmplacementParDefaut == null || oo2EmplacementParDefaut.contentEquals("")) {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, o2.getProduitID());
                    oo2EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();
                }

                if((oo1EmplacementParDefaut == null || oo1EmplacementParDefaut.contentEquals("")) && (oo2EmplacementParDefaut == null || oo2EmplacementParDefaut.contentEquals("")))
                {
                    if (oo1ZoneDefaut == null || oo1ZoneDefaut.contentEquals("")) {
                        Produit produit = ProduitOpenHelper.getProduitByID(db, o1.getProduitID());
                        oo1ZoneDefaut = produit.getZone_PUI_Defaut();

                    }
                    if (oo2ZoneDefaut == null || oo2ZoneDefaut.contentEquals("")) {
                        Produit produit = ProduitOpenHelper.getProduitByID(db, o2.getProduitID());
                        oo2ZoneDefaut = produit.getZone_PUI_Defaut();
                    }
                    return oo1ZoneDefaut.compareTo(oo2ZoneDefaut);
                }
                else
                {
                    return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
                }

            }
        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, utilisateurConnecte);
        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Reliquat ph_reliquat : phReliquatList)
        {
            if(ph_reliquat != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    phReliquatReceptionAdapter.addSectionHeaderItem(ph_reliquat);
                }

                phReliquatReceptionAdapter.addItem(ph_reliquat);
            }
        }
        //phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    private void onTriParPoids()
    {
        tri_choisi = "Poids";
        phReliquatList.sort((o1, o2) -> {
            if(o2 == null || o1 == null)
                return 1;
            else
            {
                double poids1 = 0;
                double poids2= 0;

                Produit produit = ProduitOpenHelper.getProduitByID(db, o1.getProduitID());
                poids1 = produit.getPoids()*o1.getQteReliquat_X();

                Produit produit2 = ProduitOpenHelper.getProduitByID(db, o2.getProduitID());
                poids2 = produit2.getPoids()*o2.getQteReliquat_X();


                return Double.compare(poids1, poids2);
            }
        });
        phReliquatReceptionAdapter = new PH_Reliquat_ReceptionAdapter(DetailReceptionActivity.this, db, utilisateurConnecte);
        List<String> listeZoneEmplacement = new ArrayList<>();
        for(PH_Reliquat ph_reliquat : phReliquatList)
        {
            if(ph_reliquat != null)
            {
                Produit produit = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                String zone = produit.getZone_PUI_Defaut();
                String emplacement = produit.getEmplacement_PUI_Defaut();
                String zoneemplacement = zone + "-" + emplacement;

                if(!listeZoneEmplacement.contains(zoneemplacement)) {
                    listeZoneEmplacement.add(zoneemplacement);
                    phReliquatReceptionAdapter.addSectionHeaderItem(ph_reliquat);
                }

                phReliquatReceptionAdapter.addItem(ph_reliquat);
            }
        }
        //phReliquatListView.setDivider(footer);
        phReliquatListView.setAdapter(phReliquatReceptionAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        menu.findItem(R.id.menuPhoto).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.menuSaveCircle);
        item.setOnMenuItemClickListener(item1 -> {
            onMenuSaveClick();
            return true;
        });

        MenuItem itemPhoto = menu.findItem(R.id.menuPhoto);
        itemPhoto.setOnMenuItemClickListener(item12 -> {
            prendrePhotoBL();
            return true;
        });
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    onResume();
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduits = Objects.requireNonNull(data.getExtras()).getString("photoProduit");
                    if (photoProduits == null || photoProduits.contentEquals("")) {
                        // Récupération du bon de livraison

                        if(second_passage_photo)
                        {
                            onMenuSaveClick();
                        }
                    } else {
                        try {
                            bonLivraisonBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduits));
                            if(second_passage_photo)
                            {
                                onMenuSaveClick();
                            }
                        } catch (IOException e) {
                            Log.e("IOException", Objects.requireNonNull(e.getMessage()));
                        }
                    }
                    break;
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    onResume();
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate) {
        //gestion du commentaire pour ne pas effacer l'édit text pendant la rotation
        if (bonLivraison != null) {
            outstate.putString("NomBonLivraison", bonLivraison);
        }
        outstate.putBoolean("Orientation", true);
        //gestion de la liste de retour ligne pour gérer le changement de valeur
        outstate.putSerializable("listePHReliquat", (Serializable) phReliquatList);

        super.onSaveInstanceState(outstate);
    }

    public void envoyerMail(boolean copieMail, final String email) throws JSONException {
        if (copieMail) {
            EmailCopie = utilisateurConnecte.getMail();
            if (EmailCopie == null || EmailCopie.contentEquals("")) {
                envoyerCopie = false;
            }
        }

        if (email != null) {
            new DetailReceptionActivity.SendEmailTask().execute(email);
        }

        Toast toast = Toast.makeText(DetailReceptionActivity.this, "Réception effectuée", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        retourService(super.getBundle());
    }

    public void onMenuSaveClick() {
        boolean validationPossible = false;
        List<PH_Reliquat> listePHReliquat = new ArrayList<>();
        listePHReliquat = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, commandeSelectionne.getNumero());
        if(!listePHReliquat.isEmpty())
            validationPossible = true;

        if(validationPossible)
        {
            afficherAlerteConfirmation(DetailReceptionActivity.this, LayoutInflater.from(DetailReceptionActivity.this));
            item.setVisible(false);
        }
        else
        {
            Alerte.afficherAlerteInformation(DetailReceptionActivity.this, getLayoutInflater(), "Attention", "Aucune ligne n'a été réceptionnée.", false, false);
            item.setVisible(true);
        }
    }

    public void prendrePhotoBL()
    {
        Intent detailReception_Intent = new Intent(DetailReceptionActivity.this, PrisePhoto.class);
        Bundle detailReception_Bundle = DetailReceptionActivity.super.getBundle();
        detailReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailReception_Bundle.putString("CommandeNumero", commandeSelectionne.getNumero());
        detailReception_Bundle.putString("contexte", "priseDePhotoContexteBonDeLivraison");
        detailReception_Intent.putExtras(detailReception_Bundle);
        DetailReceptionActivity.this.startActivityForResult(detailReception_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_reception, null);

        final LinearLayout buttonOk = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout zonefermer = (LinearLayout) layout.findViewById(R.id.fermer_alerte_reception);
        final EditText numeroBLEdit = (EditText) layout.findViewById(R.id.numeroBL);
        final ImageView btn_photo = (ImageView) layout.findViewById(R.id.btnPhoto);
        final ImageView iconValidation = (ImageView) layout.findViewById(R.id.iconValidation);
        final LinearLayout boutonPhoto = (LinearLayout) layout.findViewById(R.id.boutonPhoto);

        Rect displayRectangle = new Rect();
        Window window = DetailReceptionActivity.this.getWindow();
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        zonefermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                item.setVisible(true);
            }
        });

        buttonOk.setOnClickListener(v -> {

            if(utilisateurConnecte.getEtablissement().toUpperCase().contentEquals("ARAUCO"))
            {
                FirebaseCrashlytics.getInstance().log("Bouton validation réception par l'utilisateur ARAUCO");
                FirebaseCrashlytics.getInstance().recordException(new Exception("Exception simulée après clic validation réception"));
            }

            buttonOk.setBackgroundColor(getResources().getColor(R.color.vert, null));
            ViewCompat.setBackgroundTintList(iconValidation, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
            String numeroBL = numeroBLEdit.getText().toString();
            commandeSelectionne.setBLNumero(numeroBL);
            bonLivraison = numeroBL;
            alertDialog.dismiss();
            //on vérifie qu'une saisie a été effectué avant d'enregistrer
            boolean saisie_effectuer = false;
            List<PH_Reliquat> reliquatReceptionner = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, commandeSelectionne.getNumero());
            if(!reliquatReceptionner.isEmpty())
                saisie_effectuer = true;

            if(saisie_effectuer)
            {
                second_passage_photo = false;
                Boolean receptionEffectuee = receptionner(commandeSelectionne);
                if (!receptionEffectuee) {
                    boolean continuer = false;
                    // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
                    if(erreur.contentEquals("Quantité"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ? ", listeProduitRAL, "OuiNon");
                    }
                    else if(erreur.contentEquals("Lot"))
                    {
                        continuer = Alerte.afficherAlerteList(DetailReceptionActivity.this, "Attention", "Les produits suivant n'ont pas été réceptionnés, continuer ?", listeProduitRAL, "OuiNon");
                    }
                    else
                    {
                        Alerte.afficherAlerte(DetailReceptionActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                        ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                        DetailReceptionActivity.this.finish();
                    }

                    if(continuer)
                        valider_reception();

                } else {

                    valider_reception();
                }
            }
        });

        boutonPhoto.setOnClickListener(v -> {
            boutonPhoto.setBackgroundColor(getResources().getColor(R.color.bleu_clair_alcyons, null));
            ViewCompat.setBackgroundTintList(btn_photo, ColorStateList.valueOf(getResources().getColor(R.color.blanc, null)));
            prendrePhotoBL();
        });
    }

    private Boolean receptionner(Commande commande) {
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", commande.getID_commande(), "", "Réception PUI");
        if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
            new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", commande.getID_commande(), "", "Réception PAD");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

        List<PH_Reliquat> listeReliquatBase = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeSelectionne.getNumero());
        for(PH_Reliquat reliquat : listeReliquatBase)
        {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquat);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquat.getPhiMR4UUID(), reliquat.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        List<PH_Reliquat> listeReliquat = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commandeSelectionne.getNumero());
        for(PH_Reliquat reliquatcourant : listeReliquat)
        {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquatcourant.getPhiMR4UUID(), reliquatcourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;
            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Reliquat", reliquatcourant.getReliquat_UID(), "", 0, (int)reliquatcourant.getQteLivraison(), reliquatcourant.getDesignationCourte());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
        }

        List<PH_Serialisation> list_serialisation = PH_SerialisationOpenHelper.getAllPH_SerialisationByMvtId(db, String.valueOf(commandeSelectionne.getNumero()));
        if(!list_serialisation.isEmpty())
        {
            for(PH_Serialisation serialisation : list_serialisation)
            {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation.getPhiMR4UUID(), serialisation.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                Random randomAUSeri = new Random();
                int actionSerId = randomAUSeri.nextInt();
                if(actionSerId > 0)
                    actionSerId= actionSerId*-1;
                ActionUtilisateur new_action_utilisateur_serialisation = new ActionUtilisateur(actionSerId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", serialisation.get_UID(), "", "Serialisation");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur_serialisation);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur_serialisation.getPhiMR4UUID(), new_action_utilisateur_serialisation.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            }
        }

        commande.setSituation("RM"); //R = Réception, M = Mobile
        long rowID = CommandeOpenHelper.mettreAJourUneCommande(db, commande);
        if (rowID != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, CommandeOpenHelper.Constantes.TABLE_COMMANDE, commande.getPhiMR4UUID(), commande.getID_commande(), DBOpenHelper.ActionsEAS.MAJ);
            //on ajoute l'action utilisateur à synchroniser à la fin
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            // Si possible, on essaie de mettre à jour les éléments
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailReceptionActivity.this, db, utilisateurConnecte, true);


            return listeProduitRAL.isEmpty();
        } else {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void valider_reception()
    {
        //on check la connexion à internet pour l'envoie du mail
        boolean internet = checkInternetConnection();
        if(!internet)
        {
            Alerte.afficherAlerte(DetailReceptionActivity.this, "Erreur", "Aucune connexion internet détectée, aucun envoi de mail possible","alerte");
        }
        else
        {
            //Construction mail
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date dateDuJour = new Date();
            String date = dateFormat.format(dateDuJour);
            Depot depotdest = DepotOpenHelper.getDepotParReference(db, commandeSelectionne.getRef_Depot_Dest());
            String depot_destinataire = "";
            if(depotdest != null)
            {
                depot_destinataire = depotdest.getNom();
            }

            subject = "PhiWMS Mobile - "+depot_destinataire+" - "+commandeSelectionne.getFournisseur()+" - Réception PUI N°" + commandeSelectionne.getNumero() + " - " + date;

            if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
                subject = "PhiWMS Mobile - "+depot_destinataire+" - "+commandeSelectionne.getFournisseur()+" - Réception PAD N°" + commandeSelectionne.getNumero() + " - " + date;

            if(bonLivraisonBitmap != null)
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + commandeSelectionne.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+commandeSelectionne.getBLNumero()+"\n\n" +
                        "Vous pourrez trouver ci-joint le bon de livraison. \n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }
            else
            {
                body = "Madame, Monsieur, \n \n" +
                        "La réception N°" + commandeSelectionne.getNumero() + " a été réalisée par "+utilisateurConnecte.getNom()+" "+utilisateurConnecte.getPrenom()+". \n" +
                        "Le numéro de bon de livraison saisi est le suivant : "+commandeSelectionne.getBLNumero()+"\n\n" +
                        "Cordialement, \n\n" +
                        "L'équipe Phi \n\n" +
                        "Ceci est un message automatique merci de ne pas répondre";
            }


            //Sauvegarde de la signature dans une image
            if (bonLivraisonBitmap != null) {
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                dateDuJour = new Date();
                date = dateFormat.format(dateDuJour);

                bonLivraisonPhotoName = commandeSelectionne.getNumero() + "_" + date + "_ReceptionPuiBonLivraison";

                verifyStoragePermissions(DetailReceptionActivity.this);
            }

            // Récupération Mail Pharmacie
            String email = ParametresServeurOpenHelper.getMailPharmacie(db);
            if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
            {
                email = "dev01@alcyons.fr";
            }
            afficherAlerteConfirmationMail(DetailReceptionActivity.this, LayoutInflater.from(DetailReceptionActivity.this), email);
        }
    }

    public void afficherAlerteConfirmationMail(Context context, LayoutInflater inflater, final String email) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            try {
                envoyerMail(true, email);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        buttonAnnuler.setOnClickListener(v -> {
            alertDialog.dismiss();
            try {
                envoyerMail(false, email);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void retourService(final Bundle bundle)
    {
        Intent detailReceptionIntent = new Intent(DetailReceptionActivity.this, ServiceReceptionPuiActivity.class);
        if(commandeSelectionne.getRef_Depot_Dest().contains("-PAD"))
            detailReceptionIntent = new Intent(DetailReceptionActivity.this, ServiceReceptionPadActivity.class);
        detailReceptionIntent.putExtras(bundle);
        DetailReceptionActivity.this.startActivity(detailReceptionIntent);
        DetailReceptionActivity.this.finish();
    }

    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {

            Mail sender = new Mail(DetailReceptionActivity.this, "dev01@alcyons.fr", true, db, utilisateurConnecte);
            try {
                // Envoi du mail avec pdf
                //sender.sendMailVerification(subject, body);
                if(bonLivraisonPhotoName.contentEquals(""))
                {
                    sender.sendMailVerification(subject, body);
                }
                else
                {
                    sender.sendMail(subject, body, "Documents/"+bonLivraisonPhotoName + ".jpeg");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    private void enregistrerPhReliquat(PH_Reliquat_Reception_Adapte reliquatReceptionAdapte)
    {
        listeProduitRAL = new ArrayList<>();
        PH_Reliquat ph_reliquat_base = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquatReceptionAdapte.getPhReliquatUID());

        List<PH_Reliquat> listeReliquatAjouter = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), ph_reliquat_base.getProduitID());
        for(PH_Reliquat reliquatcourant : listeReliquatAjouter)
        {
            PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatcourant);
            //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, reliquatcourant.getPhiMR4UUID(), reliquatcourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.SUPPR);
        }

        for(PH_Reliquat_Reception_Adapte.Lot lot : reliquatReceptionAdapte.getlotList()) {
            for (PH_Reliquat_Reception_Adapte.ZoneEtEmplacement zoneEtEmplacement : lot.getZoneEtEmplacementList()) {
                Random randomreliquat = new Random();
                int reliquatId = randomreliquat.nextInt();
                if (reliquatId > 0)
                    reliquatId = reliquatId * -1;

                PH_Reliquat phReliquatCourant = ph_reliquat_base;
                phReliquatCourant.setReliquat_UID(reliquatId);
                String numeroLot = lot.getNumeroLot();
                String datePeremption = lot.getDatePeremption();
                String[] datePeremptionTab = datePeremption.split("/");
                if(datePeremptionTab.length == 3)
                    datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];
                String zoneName = zoneEtEmplacement.getZoneName();
                String emplacementName = zoneEtEmplacement.getEmplacementName();
                String numero_Serie = lot.getNumero_serie();
                int quantite = zoneEtEmplacement.getQuantite();

                if (quantite == 0) {
                    erreur = "Quantité";
                    listeProduitRAL.add(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                }

                if (numeroLot.contentEquals("")) {
                    erreur = "Lot";
                    int index = listeProduitRAL.indexOf(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                    if (index == -1)
                        listeProduitRAL.add(phReliquatCourant.getDesignationCourte() + " - " + phReliquatCourant.getQteCommande());
                }

                phReliquatCourant.setLot(numeroLot.trim());
                phReliquatCourant.setSerie(numero_Serie.trim());
                phReliquatCourant.setPeremptionDate(datePeremption.trim());

                if (commandeSelectionne.getRef_Depot_Dest().contains("-PAD")) {
                    phReliquatCourant.setZone("RECEPTION");
                    phReliquatCourant.setEmplacement("RECEPTION-" + commandeSelectionne.getNumero() + "-" + commandeSelectionne.getPatient_identite());
                } else {
                    phReliquatCourant.setZone(zoneName.trim());
                    phReliquatCourant.setEmplacement(emplacementName.trim());
                }
                phReliquatCourant.setQteLivraison(quantite);
                phReliquatCourant.setBL_Numero(bonLivraison);
                phReliquatCourant.setScanValue("");

                long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
                if(rowID != -1)
                {
                    //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_ReliquatOpenHelper.Constantes.TABLE_PH_RELIQUAT, phReliquatCourant.getPhiMR4UUID(), phReliquatCourant.getReliquat_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }
        }

        //ElementASynchroniserOpenHelper.toutSynchroniser(DetailReception2025Activity.this, db, utilisateurConnecte, false);
    }

    private void envoyerImpressionZebra(PH_Reliquat reliquatCourant, String nomImprimante) throws JSONException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(cal.getTime());

        JSONArray Etiquette_TO = new JSONArray();

        String designationProduit = reliquatCourant.getDesignationCourte();
        String numeroLot = reliquatCourant.getLot();
        String numeroSerie = reliquatCourant.getSerie();
        String datePeremption = reliquatCourant.getPeremptionDate();

        if(designationProduit.length() > 30)
            designationProduit = designationProduit.substring(0, 30);

        JSONObject codeBarrJO = new JSONObject();
        codeBarrJO.put("type", "Datamatrix");
        codeBarrJO.put("phitag", "PHIBCF:"+commandeSelectionne.getNumero());

        JSONObject etiquette_v1_JO = new JSONObject();
        etiquette_v1_JO.put("codeBarre", codeBarrJO);
        etiquette_v1_JO.put("nomfournisseur", commandeSelectionne.getFournisseur());
        etiquette_v1_JO.put("phiTag", commandeSelectionne.getNumero());
        etiquette_v1_JO.put("date", strDate);
        etiquette_v1_JO.put("designation", designationProduit);
        etiquette_v1_JO.put("qtereceptionne",reliquatCourant.getQteLivraison());
        etiquette_v1_JO.put("numlot", numeroLot);
        etiquette_v1_JO.put("dateperemption", datePeremption);

        Etiquette_TO.put(etiquette_v1_JO);

        String imprimante_VT = nomImprimante;
        String aImprimer = "true";
        String format = "Réception";

        JSONObject body = new JSONObject();
        try {
            body.put("Imprimante", imprimante_VT);
            body.put("aImprimer", aImprimer);
            body.put("format", format);
            body.put("etiquettes", Etiquette_TO);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException :", e);
        }
        String urlRequete = ParametresServeurOpenHelper.getUrlsWeb(db) + DBOpenHelper.Urls.uriZebraImprimer;
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.POST, urlRequete, body, response -> {
            Toast.makeText(DetailReceptionActivity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
        },
                error -> {
                    Log.e("Etiquette Volley", error.toString());
                    if(!error.toString().contains("\"isOk\":true"))
                    {
                        Alerte.afficherAlerte(DetailReceptionActivity.this, "Erreur HTTP", "Erreur lors de l\'impression de l\'étiquette : "+error.toString(), "alerte");
                    }
                    else
                    {
                        Toast.makeText(DetailReceptionActivity.this, "Etiquette envoyée", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json;charset=utf-8");
                return params;
            }
        };
        RequestQueue requestQueueUtilisateur = Volley.newRequestQueue(this);
        requestQueueUtilisateur.add(obreq);
    }

    private void afficherAlerteSelectionEtiquette(Context context, LayoutInflater inflater)
    {
        List<PH_Reliquat> reliquatAImprimer = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_impression_zebra, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.fermer_alerte_etiquette_zebra);
        ListView listeReliquatReceptionne = (ListView) layout.findViewById(R.id.listeReliquatReceptionne);
        Spinner spinnerImprimante = (Spinner) layout.findViewById(R.id.spinnerImprimante);

        List<PH_Reliquat> listeReliquatReceptionnee = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumero(db, commandeSelectionne.getNumero());
        EtiquetteZebraAdapter adapter = new EtiquetteZebraAdapter(this, listeReliquatReceptionnee, reliquatAImprimer);
        listeReliquatReceptionne.setAdapter(adapter);

        listeReliquatReceptionne.setOnItemClickListener(null);

        List<String> ListNomImprimante = new ArrayList<>();
        for(ImprimanteEtiquette imprimante : listeImprimanteEtiquette)
        {
            ListNomImprimante.add(imprimante.getNom());
        }
        ArrayAdapter<String> adapterImprimante= new ArrayAdapter<String>(this,
                R.layout.inscription_spinner_item, ListNomImprimante);
        spinnerImprimante.setAdapter(adapterImprimante);


        builder.setView(layout);
        AlertDialog alertDialogEtiquette = builder.create();
        Objects.requireNonNull(alertDialogEtiquette.getWindow()).setGravity(Gravity.CENTER);
        alertDialogEtiquette.show();

        zoneok.setOnClickListener(v -> {

            String nomImprimante = spinnerImprimante.getSelectedItem().toString();
            for(PH_Reliquat reliquat : reliquatAImprimer)
            {
                try {
                    envoyerImpressionZebra(reliquat, nomImprimante);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonAnnuler.setOnClickListener(v -> alertDialogEtiquette.dismiss());
    }
}
