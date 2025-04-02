package fr.alcyons.phiwms_mobile.BarcodeSearch;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.AuthentificationContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ControleDesRetourScanContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.DocumentScannerContext;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.EmplacementContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PleinVideLocalisationContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.PreparationScanneeScanProduitScannerSearchContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ProduitReceptionScanneeScannerSearchContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ReceptionPADScannerContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ServiceContexte;
import fr.alcyons.phiwms_mobile.BarcodeSearch.contexte.ZoneEtEmplacementContext;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionPAD;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_PreparationScanneeAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_ReceptionPADAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Produit_ReceptionScanneeAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;

import fr.alcyons.phiwms_mobile.ServiceActivity;

import static android.view.View.GONE;

public class ScannerSearchOnlyActivity extends ServiceActivity {

    List<String> stringList;
    String bannerTexte;
    String code;

    // INTENT
    boolean modeRafale;
    boolean modePhoto;
    boolean modeCumule;
    boolean activerTextSuppression;
    boolean doitEtreIdentique;
    boolean serialisation;
    boolean bouton_suppression;
    String designation;
    int scannerContexteInt;
    int conditionnementProduit;

    // CONTEXTE
    ProduitContexte produitContexte;
    EmplacementContexte emplacementContexte;
    PleinVideContexte pleinVideContexte;
    PleinVideLocalisationContexte pleinVideLocalisationContexte;
    ProduitReceptionScanneeScannerSearchContexte produitReceptionScanneeContexte;
    PreparationScanneeScanProduitScannerSearchContexte preparationADHContext;
    ReceptionPADScannerContexte receptionPADScannerContexte;
    ServiceContexte serviceContexte;
    AuthentificationContext authentificationContext;
    ControleDesRetourScanContext controleDesRetourScanContext;

    // GRAPHIQUE
    EditText contenuCode;
    EditText contenuCodeManuel;
    TextView message;
    TextView compteurScan;
    RelativeLayout LinearResultatFranceMVO;
    ToneGenerator toneGen1;
    AlertDialog alertDialogFranceMVO;

    //MAIL
    String subject = "";
    String body = "";

    //Pour la réception scannée
    LinearLayout LinearReceptionScanne;
    LinearLayout layoutBasic;
    LinearLayout LinearZoneEmplacementReceptionScannee;
    TextView nomZoneReceptionScannee;
    TextView nomEmplacementReceptionScannee;
    ExpandableListView ListViewProduitReceptionScannee;
    Produit_ReceptionScanneeAdapter produit_ReceptionScanneeAdapter;
    Produit_ReceptionPADAdapter receptionPADExpandableAdapter;
    Produit_PreparationScanneeAdapter produitPreparationScanneeAdapter;
    DocumentScannerContext documentScannerContext;
    ZoneEtEmplacementContext zoneEtEmplacementContext;
    LinearLayout LayoutResultatScanProduit;
    TextView referenceProduitReceptionScannee;
    TextView textPresentationRefProduitScannee;
    TextView numeroLotProduitReceptionScannee;
    TextView peremptionProduitReceptionScannee;
    TextView qteProduitReceptionScannee;
    TextView designationProduitReceptionScannee;
    TextView nomListePreparation;
    ImageView ImageSeparateur;
    List<Integer> liste_id_retour_ligne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_search_only);

        stringList = new ArrayList<>();
        bannerTexte = "";
        code = "";
        // INTENT
        liste_id_retour_ligne = intent.getExtras().getIntegerArrayList("liste_id_retour_ligne");
        modeRafale = intent.getExtras().getBoolean("modeRafale");
        modePhoto = intent.getExtras().getBoolean("modePhoto");
        modeCumule = intent.getExtras().getBoolean("modeCumule");
        bouton_suppression = intent.getBooleanExtra("isBoutonSuppressionExistant", false);
        activerTextSuppression = intent.getBooleanExtra("activerTextSuppression", false);
        doitEtreIdentique = intent.getExtras().getBoolean("doitEtreIdentique");
        designation = intent.getExtras().getString("Designation");
        scannerContexteInt = intent.getIntExtra("scannerContexteInt", 0);
        stringList = intent.getStringArrayListExtra("stringList");
        serialisation = intent.getBooleanExtra("serialisation", false);
        conditionnementProduit = intent.getExtras().getInt("ConditionnementProduit");
        int PreparationID = intent.getExtras().getInt("PreparationID");
        //Pour la réception scannée
        int actionId = intent.getIntExtra("ActionId", 0);
        boolean ADH = intent.getBooleanExtra("ADH", false);
        final boolean service_serialisation = intent.getBooleanExtra("serialisation", false);
        List<Integer> liste_id_reliquat = intent.getExtras().getIntegerArrayList("liste_id_reliquat");

        // GRAPHIQUE
        message = (TextView) findViewById(R.id.message);
        message.setVisibility(GONE);
        compteurScan = (TextView) findViewById(R.id.compteurScan);
        compteurScan.setVisibility(GONE);
        if(modeRafale || modeCumule){
            compteurScan.setVisibility(View.VISIBLE);
            if(stringList != null)
            {
                compteurScan.setText(String.valueOf(stringList.size()) + " produit(s) scanné(s)");
            }
            else
            {
                compteurScan.setText("0 produit scanné");
            }
        }
        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        //Objet graphique pour la réception scannée
        LinearReceptionScanne = (LinearLayout) findViewById(R.id.LinearReceptionScanne);
        layoutBasic = (LinearLayout) findViewById(R.id.layoutBasic);
        LinearZoneEmplacementReceptionScannee = (LinearLayout) findViewById(R.id.LinearZoneEmplacementReceptionScannee);
        LinearResultatFranceMVO = (RelativeLayout) findViewById(R.id.LinearResultatFranceMVO);
        nomZoneReceptionScannee = (TextView) findViewById(R.id.nomZoneReceptionScannee);
        nomEmplacementReceptionScannee = (TextView) findViewById(R.id.nomEmplacementReceptionScannee);
        nomListePreparation = (TextView) findViewById(R.id.nomListePreparation);
        textPresentationRefProduitScannee = (TextView) findViewById(R.id.textPresentationRefProduitScannee);
        ListViewProduitReceptionScannee = (ExpandableListView) findViewById(R.id.ListViewProduitReceptionScannee);
        contenuCode = (EditText) findViewById(R.id.contenuCode);
        contenuCodeManuel = (EditText) findViewById(R.id.contenuCodeManuel);
        LayoutResultatScanProduit = (LinearLayout) findViewById(R.id.LayoutResultatScanProduit);
        referenceProduitReceptionScannee = (TextView) findViewById(R.id.referenceProduitReceptionScannee);
        numeroLotProduitReceptionScannee = (TextView) findViewById(R.id.numeroLotProduitReceptionScannee);
        peremptionProduitReceptionScannee = (TextView) findViewById(R.id.peremptionProduitReceptionScannee);
        qteProduitReceptionScannee = (TextView) findViewById(R.id.qteProduitReceptionScannee);
        designationProduitReceptionScannee = (TextView) findViewById(R.id.designationProduitReceptionScannee);
        ImageSeparateur = (ImageView) findViewById(R.id.ImageSeparateur);

        //on donne le focus sur l'input
        contenuCode.requestFocus();

        // CONTEXTE
        produitContexte = new ProduitContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, serialisation);
        emplacementContexte = new EmplacementContexte(this, db, ADH, utilisateurConnecte);
        pleinVideContexte = new PleinVideContexte(this, message);
        zoneEtEmplacementContext = new ZoneEtEmplacementContext(this, db, utilisateurConnecte);
        pleinVideLocalisationContexte = new PleinVideLocalisationContexte(this, db, message);
        produitReceptionScanneeContexte = new ProduitReceptionScanneeScannerSearchContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, serialisation, utilisateurConnecte, actionId);
        if(utilisateurConnecte != null)
            preparationADHContext = new PreparationScanneeScanProduitScannerSearchContexte(this, db, utilisateurConnecte.getId(), message, PreparationID);
        receptionPADScannerContexte = new ReceptionPADScannerContexte(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId, liste_id_reliquat);
        documentScannerContext = new DocumentScannerContext(this, db);
        serviceContexte = new ServiceContexte(ScannerSearchOnlyActivity.this, db);
        authentificationContext = new AuthentificationContext(ScannerSearchOnlyActivity.this, db);
        controleDesRetourScanContext = new ControleDesRetourScanContext(this,db, modeRafale, modePhoto, modeCumule, doitEtreIdentique, designation, service_serialisation, conditionnementProduit, utilisateurConnecte, actionId, liste_id_retour_ligne);

        // Initialisation du CONTEXTE
        if(scannerContexteInt == R.string.scannerContexteEmplacement)
        {
            bannerTexte = emplacementContexte.bannerTexte;
            if(intent.getExtras().containsKey("designationProduit"))
            {
                TextView designationProduit_Scan = (TextView) findViewById(R.id.designationProduit_Scan);
                designationProduit_Scan.setVisibility(View.VISIBLE);
                designationProduit_Scan.setText(intent.getExtras().getString("designationProduit"));
            }
        }
        else if(scannerContexteInt == R.string.scannerContextePleinVide)
        {
            bannerTexte = intent.getExtras().getString("dotationIntitule");
            pleinVideContexte.stringList = stringList;
            pleinVideContexte.detailDotPleinVide_AdressageList = intent.getExtras().getStringArrayList("detailDotPleinVide_AdressageList");
        }
        else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
        {
            bannerTexte = pleinVideLocalisationContexte.bannerTexte;
        }
        else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
        {
            bannerTexte = zoneEtEmplacementContext.bannerTexte;
        }
        else if(scannerContexteInt == R.string.scannerContexteDocument)
        {
            contenuCodeManuel.requestFocus();
            bannerTexte = documentScannerContext.bannerTexte;
            compteurScan.setVisibility(View.GONE);
        }
        else if(scannerContexteInt == R.string.scannerContexteAuthentification)
        {
            bannerTexte = authentificationContext.bannerTexte;
            compteurScan.setVisibility(GONE);
        }
        else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
        {
            bannerTexte = controleDesRetourScanContext.bannerTexte;
        }
        else if(scannerContexteInt == R.string.scannerContextePreparationADH)
        {
            message.setVisibility(GONE);
            compteurScan.setVisibility(GONE);
            LinearZoneEmplacementReceptionScannee.setVisibility(GONE);
            nomListePreparation.setVisibility(View.VISIBLE);
            nomListePreparation.setText(intent.getExtras().getString("Preparation_Code"));
            bannerTexte = preparationADHContext.bannerTexte;
            LinearReceptionScanne.setVisibility(View.VISIBLE);
            preparationADHContext.liste_code_scanne = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
            if(preparationADHContext.liste_code_scanne == null)
            {
                preparationADHContext.liste_code_scanne = intent.getExtras().getStringArrayList("ListString");
                if(preparationADHContext.liste_code_scanne == null)
                {
                    preparationADHContext.liste_code_scanne = new ArrayList<>();
                }
            }

            preparationADHContext.liste_resultat = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
            if(preparationADHContext.liste_resultat == null)
            {
                preparationADHContext.liste_resultat = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("Liste_Objet_Scanne");
                if(preparationADHContext.liste_resultat == null)
                {
                    preparationADHContext.liste_resultat = new ArrayList<>();
                }
                else
                {
                    preparationADHContext.Initialisation(preparationADHContext.liste_resultat);
                }
            }
            else
            {
                preparationADHContext.Initialisation(preparationADHContext.liste_resultat);
                nomZoneReceptionScannee.setText(preparationADHContext.zoneProduitCourant);
                nomEmplacementReceptionScannee.setText(preparationADHContext.emplacementProduitCourant);
                bannerTexte = "Scanner le datamatrix d'un produit";
                //preparationADHContext.scanEmplacement = false;
                if(contenuCode.getVisibility() == View.VISIBLE)
                {
                    contenuCode.requestFocus();
                }
                else
                {
                    contenuCodeManuel.requestFocus();
                }
            }
            GestionAffichagePreparation();
            if(contenuCode.getVisibility() == View.VISIBLE)
            {
                contenuCode.requestFocus();
            }
            else
            {
                contenuCodeManuel.requestFocus();
            }
        }
        else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
        {
            message.setVisibility(GONE);
            compteurScan.setVisibility(GONE);
            bannerTexte = "Scanner un emplacement";
            LinearReceptionScanne.setVisibility(View.VISIBLE);
            produitReceptionScanneeContexte.stringList = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
            if(produitReceptionScanneeContexte.stringList == null || produitReceptionScanneeContexte.stringList.size()==0)
            {
                produitReceptionScanneeContexte.stringList = new ArrayList<>();
            }
            produitReceptionScanneeContexte.liste_resultat = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
            if(produitReceptionScanneeContexte.liste_resultat == null|| produitReceptionScanneeContexte.liste_resultat.size()==0)
            {
                produitReceptionScanneeContexte.liste_resultat = new ArrayList<>();
            }
            else
            {
                produitReceptionScanneeContexte.Initialisation(produitReceptionScanneeContexte.liste_resultat);
                nomZoneReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                nomEmplacementReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                bannerTexte = "Scanner le datamatrix d'un produit";
                produitReceptionScanneeContexte.scanEmplacement = false;
                GestionAffichage();
                if(contenuCode.getVisibility() == View.VISIBLE)
                {
                    contenuCode.requestFocus();
                }
                else
                {
                    contenuCodeManuel.requestFocus();
                }
            }
        }
        else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
        {
            message.setVisibility(GONE);
            compteurScan.setVisibility(GONE);
            nomListePreparation.setVisibility(View.VISIBLE);
            nomListePreparation.setText(intent.getExtras().getString("Fournisseur_Reception"));
            bannerTexte = "Scanner le datamatrix d'un produits";
            LinearReceptionScanne.setVisibility(View.VISIBLE);
            receptionPADScannerContexte.stringList = intent.getExtras().getStringArrayList("Liste_GTIN_Scannee");
            if(receptionPADScannerContexte.stringList == null)
            {
                receptionPADScannerContexte.stringList = new ArrayList<>();
            }
            receptionPADScannerContexte.list_result = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("ListeObjetScannee");
            if(receptionPADScannerContexte.list_result == null)
            {
                receptionPADScannerContexte.list_result = new ArrayList<>();
            }
            else
            {
                int size_liste = receptionPADScannerContexte.list_result.size();
                receptionPADScannerContexte.chargementscan = true;

                for(int i = 0; i < size_liste; i++)
                {
                    ObjetReceptionScannee courant = receptionPADScannerContexte.list_result.get(0);
                    Map<String, String> mapResult = OutilsDecodage.decouperGTIN(courant.getGs1_scannee());
                    Produit produitCourant = null;
                    if(mapResult.size() > 1)
                    {
                        produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, mapResult.get(OutilsDecodage.codeGtin));
                    }
                    else
                    {
                        produitCourant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, courant.getGs1_scannee());
                    }

                    if(produitCourant != null)
                    {
                        for(Integer id_courant : liste_id_reliquat)
                        {
                            PH_Reliquat reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, id_courant);
                            int produitid = produitCourant.getID_produit();
                            int reliquatproduitid = reliquat_courant.getProduitID();
                            if(produitid == reliquatproduitid)
                            {
                                receptionPADScannerContexte.ph_reliquat_courant = reliquat_courant;
                                break;
                            }
                        }
                        receptionPADScannerContexte.objetReceptionPADCourant = new ObjetReceptionPAD();
                        receptionPADScannerContexte.objetReceptionPADCourant.setDesignation_produit(receptionPADScannerContexte.ph_reliquat_courant.getDesignationCourte());
                        receptionPADScannerContexte.objetReceptionPADCourant.setQuantite_commander(receptionPADScannerContexte.ph_reliquat_courant.getQteCommande());
                        receptionPADScannerContexte.objetReceptionPADCourant.setQuantite_receptionner(courant.getQuantiteScannee());
                        receptionPADScannerContexte.quantite_a_afficher = courant.getQuantiteScannee();
                        receptionPADScannerContexte.designationProduitCourant = receptionPADScannerContexte.ph_reliquat_courant.getDesignationCourte();
                        receptionPADScannerContexte.objetReceptionScanneeCourant = courant;
                        receptionPADScannerContexte.doitEtreSupprimer = true;
                        receptionPADScannerContexte.index_objet_a_supprimer = 0;
                        receptionPADScannerContexte.objetReceptionScanneeCourant.setQuantiteScannee(0);
                        receptionPADScannerContexte.AjoutDuProduit();
                    }
                }
                receptionPADScannerContexte.chargementscan = false;

                bannerTexte = "Scanner le datamatrix d'un produit";
                GestionAffichage();
                if(contenuCode.getVisibility() == View.VISIBLE)
                {
                    contenuCode.requestFocus();
                }
                else
                {
                    contenuCodeManuel.requestFocus();
                }
            }
            GestionAffichagePAD();
        }
        else if(scannerContexteInt == R.string.scannerContexteService)
        {
            message.setVisibility(GONE);
            compteurScan.setVisibility(GONE);
            bannerTexte = "Scannez un service";
        }
        else
        {
            produitContexte.stringList = stringList;
            bannerTexte = produitContexte.bannerTexte;
        }

        // Mise à jour GRAPHIQUE
        ((TextView) findViewById(R.id.banner)).setText(bannerTexte);

        if (modeRafale) {
            findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stringList = produitContexte.stringList;
                    Intent scannerSearchOnlyIntent = new Intent();
                    Bundle scannerSearchOnlyBundle = new Bundle();
                    if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        stringList = controleDesRetourScanContext.stringList;
                        scannerSearchOnlyBundle.putSerializable("listeRetourScan", (Serializable) controleDesRetourScanContext.list_result);
                    }

                    if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        stringList = pleinVideContexte.stringList;
                    }
                    scannerSearchOnlyBundle.putStringArrayList("stringList", (ArrayList) stringList);
                    scannerSearchOnlyBundle.putBoolean("close", true);
                    scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                    ScannerSearchOnlyActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                    ScannerSearchOnlyActivity.this.finish();
                }
            });
            findViewById(R.id.boutonFermeture).setVisibility(View.VISIBLE);
            if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee|| scannerContexteInt == R.string.scannerContexteReceptionPAD)
            {
                findViewById(R.id.boutonFermeture).setVisibility(GONE);
            }
        }
        else if(bouton_suppression)
        {
            if(activerTextSuppression)
            {
                findViewById(R.id.boutonFermeture).setVisibility(GONE);
                ((TextView)findViewById(R.id.textManuelScan)).setText(intent.getExtras().getString("TextBannerManuel"));
                if(scannerContexteInt == R.string.scannerContexteAuthentification)
                {
                    Button boutonFermeture = (Button) findViewById(R.id.boutonFermetureManuel);
                    boutonFermeture.setText("Connexion manuelle");
                }
                findViewById(R.id.layoutBasic).setVisibility(GONE);
                findViewById(R.id.banner).setVisibility(GONE);
                findViewById(R.id.linearScanManuel).setVisibility(View.VISIBLE);
                findViewById(R.id.boutonFermetureManuel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent scannerSearchOnlyIntent = new Intent();
                        Bundle scannerSearchOnlyBundle = new Bundle();
                        scannerSearchOnlyBundle.putString("code", code);
                        scannerSearchOnlyBundle.putBoolean("close", true);
                        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                        ScannerSearchOnlyActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                        ScannerSearchOnlyActivity.this.finish();
                    }
                });
            }
            else
            {
                findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent scannerSearchOnlyIntent = new Intent();
                        Bundle scannerSearchOnlyBundle = new Bundle();
                        if(scannerContexteInt == R.string.scannerContextePreparationADH)
                        {
                            scannerSearchOnlyBundle.putStringArrayList("listeString", (ArrayList) preparationADHContext.liste_resultat);
                        }
                        scannerSearchOnlyBundle.putString("code", code);
                        scannerSearchOnlyBundle.putBoolean("close", true);
                        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                        ScannerSearchOnlyActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
                        ScannerSearchOnlyActivity.this.finish();
                    }
                });
                findViewById(R.id.boutonFermeture).setVisibility(View.VISIBLE);
                if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee || scannerContexteInt == R.string.scannerContexteReceptionPAD)
                {
                    findViewById(R.id.boutonFermeture).setVisibility(GONE);
                }
            }
        }

        contenuCodeManuel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
                Random random = new Random();
                String stringint = String.valueOf(random.nextInt());
                int id = Integer.parseInt(stringint.substring(0, 5))*-1;
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                TableTrace tableTrace = null;
                long rowId = 0;
                if (s.toString().endsWith("\n")) {

                    code = "";

                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Emplacement", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        code = s.toString().substring(0, s.length() - 1);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteDocument)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Document", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        documentScannerContext.onTextWatcher(s);
                        code = documentScannerContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Zone_Emplacement", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        zoneEtEmplacementContext.onTextWatcher(s);
                        code = zoneEtEmplacementContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                    {
                        authentificationContext.onTextWatcher(s);
                        code = authentificationContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        controleDesRetourScanContext.onTextWatcher(s);
                        code = controleDesRetourScanContext.code;
                        compteurScan.setText(String.valueOf(controleDesRetourScanContext.list_result.size()) + " produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Preparation_ADH", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            preparationADHContext.ajouterProduit();
                            code = "Valider";
                        }
                        else
                        {
                            preparationADHContext.onTextWatcher(s);
                            if(s.toString().startsWith("PHITAGPLACE"))
                            {
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                nomZoneReceptionScannee.setText(preparationADHContext.zoneProduitCourant);
                                nomEmplacementReceptionScannee.setText(preparationADHContext.emplacementProduitCourant);
                                bannerTexte = "Scanner le datamatrix d'un produit";
                                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                                //preparationADHContext.scanEmplacement = false;
                                GestionAffichagePreparation();
                            }
                            else
                            {
                                //LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                                ImageSeparateur.setVisibility(View.VISIBLE);
                                referenceProduitReceptionScannee.setText(preparationADHContext.referenceProduitScanne);
                                numeroLotProduitReceptionScannee.setText(preparationADHContext.numeroLotProduitScanne);
                                peremptionProduitReceptionScannee.setText(preparationADHContext.peremptionProduitScanne);
                                if(preparationADHContext.quantiteAAfficher == 0)
                                {
                                    qteProduitReceptionScannee.setText("");
                                    textPresentationRefProduitScannee.setVisibility(GONE);
                                    LayoutResultatScanProduit.setVisibility(View.GONE);
                                    ImageSeparateur.setVisibility(View.GONE);
                                }
                                else
                                {
                                    qteProduitReceptionScannee.setText(String.valueOf(preparationADHContext.quantiteAAfficher));
                                    qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Ouvre une boite de dialogue avec un NumberPicker
                                            Context context = ScannerSearchOnlyActivity.this;
                                            String title = preparationADHContext.designationProduitScanne;
                                            String message = "Changer la quantité: ";
                                            int maxValue = preparationADHContext.quantite_max_number_picker;
                                            int value = preparationADHContext.quantiteAAfficher;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    int qteAprès = Alerte.aNumberPicker.getValue();
                                                    qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                    //produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                                    //preparationADHContext.ChangerQuantite(qteAprès);
                                                    //adapter.notifyDataSetChanged();
                                                    InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                    dialog.dismiss();
                                                    GestionAffichage();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
                                        }
                                    });
                                }
                            }
                            GestionAffichagePreparation();
                            ExpandGroupAfterScan(preparationADHContext.designationProduitScanne);
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVide) {
                        pleinVideContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                    {
                        pleinVideLocalisationContexte.onTextWatcher(s);
                        code = pleinVideLocalisationContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Reception_Scannee", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("PHITAGPLACE"))
                            produitReceptionScanneeContexte.scanEmplacement = true;

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            produitReceptionScanneeContexte.AjoutDuProduit();
                            code = "Valider";
                        }
                        else
                        {
                            produitReceptionScanneeContexte.onTextWatcher(s);
                            if(produitReceptionScanneeContexte.scanEmplacement)
                            {
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                nomZoneReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                                nomEmplacementReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                                bannerTexte = "Scanner le datamatrix d'un produit";
                                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                                produitReceptionScanneeContexte.scanEmplacement = false;
                                GestionAffichage();
                            }
                            else
                            {
/*                                    LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                                ImageSeparateur.setVisibility(View.VISIBLE);
                                referenceProduitReceptionScannee.setText(produitReceptionScanneeContexte.referenceProduitCourant);
                                numeroLotProduitReceptionScannee.setText(produitReceptionScanneeContexte.numeroLotProduitCourant);
                                peremptionProduitReceptionScannee.setText(produitReceptionScanneeContexte.peremptionProduitCourant);
                                qteProduitReceptionScannee.setText(String.valueOf(produitReceptionScanneeContexte.quantite_a_afficher));
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = ScannerSearchOnlyActivity.this;
                                        String title = produitReceptionScanneeContexte.designationProduitCourant;
                                        String message = "Changer la quantité: ";
                                        int maxValue = 10000;
                                        int value = Integer.parseInt(produitReceptionScanneeContexte.quantiteProduitCourant);

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                //produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                                produitReceptionScanneeContexte.ChangerQuantite(qteAprès);
                                                //adapter.notifyDataSetChanged();
                                                dialog.dismiss();
                                                GestionAffichage();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });*/
                                GestionAffichage();
                            }
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Reception_PAD", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            receptionPADScannerContexte.AjoutDuProduit();
                            code = "Valider";
                        }
                        else
                        {
                            receptionPADScannerContexte.onTextWatcher(s);
                            LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                            ImageSeparateur.setVisibility(View.VISIBLE);
                            referenceProduitReceptionScannee.setText(receptionPADScannerContexte.referenceProduitCourant);
                            numeroLotProduitReceptionScannee.setText(receptionPADScannerContexte.numeroLotProduitCourant);
                            peremptionProduitReceptionScannee.setText(receptionPADScannerContexte.peremptionProduitCourant);
                            if(receptionPADScannerContexte.quantite_a_afficher == 0)
                            {
                                textPresentationRefProduitScannee.setVisibility(GONE);
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                qteProduitReceptionScannee.setText("");
                            }
                            else
                            {
                                qteProduitReceptionScannee.setText(String.valueOf(receptionPADScannerContexte.quantite_a_afficher));
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = ScannerSearchOnlyActivity.this;
                                        String title = receptionPADScannerContexte.designationProduitCourant;
                                        String message = "Changer la quantité: ";
                                        int maxValue = receptionPADScannerContexte.quantiteMaxNumberPicker;
                                        int value = receptionPADScannerContexte.quantite_a_afficher;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = Alerte.aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                receptionPADScannerContexte.quantite_a_afficher = qteAprès;
                                                /*receptionPADScannerContexte.ChangerQuantite(qteAprès);*/
                                                //adapter.notifyDataSetChanged();
                                                InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                dialog.dismiss();
                                                GestionAffichage();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });
                            }

                            GestionAffichagePAD();
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        serviceContexte.onTextWatcher(s);
                        code = serviceContexte.code;
                    }
                    else
                    {
                        produitContexte.onTextWatcher(s);
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Produit", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        if(produitContexte.stringList != null)
                            compteurScan.setText(String.valueOf(produitContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = produitContexte.code;
                    }

                    if(code != null && !code.isEmpty()){
                        Intent resultIntent = new Intent();
                        if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                        {
                            resultIntent.putExtra("listeString", (Serializable) produitReceptionScanneeContexte.liste_resultat);
                        }

                        if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                        {
                            resultIntent.putExtra("listeString", (Serializable) receptionPADScannerContexte.list_result);
                        }

                        if(scannerContexteInt == R.string.scannerContexteAuthentification)
                        {
                            resultIntent.putExtra("code", authentificationContext.code);
                        }

                        if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                        {
                            resultIntent.putExtra("code", controleDesRetourScanContext.code);
                            resultIntent.putExtra("listeString", (Serializable) controleDesRetourScanContext.list_result);
                        }

                        if(scannerContexteInt == R.string.scannerContextePreparationADH)
                        {
                            resultIntent.putExtra("listeString", (Serializable) preparationADHContext.liste_resultat);
                            resultIntent.putExtra("listecodescannee", (Serializable) preparationADHContext.liste_code_scanne);
                        }
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        ScannerSearchOnlyActivity.this.finish();
                    }
                    else{
                        contenuCode.getText().clear();
                        contenuCodeManuel.getText().clear();
                    }
                }
            }
        });
        contenuCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
                Random random = new Random();
                String stringint = String.valueOf(random.nextInt());
                int id = Integer.parseInt(stringint.substring(0, 5))*-1;
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                TableTrace tableTrace = null;
                long rowId = 0;
                if (s.toString().endsWith("\n")) {

                    code = "";

                    if(scannerContexteInt == R.string.scannerContexteEmplacement)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Emplacement", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        code = s.toString().substring(0, s.length() - 1);
                    }
                    else if(scannerContexteInt == R.string.scannerContexteDocument)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Document", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        documentScannerContext.onTextWatcher(s);
                        code = documentScannerContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteZoneEtEmplacement)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Zone_Emplacement", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        zoneEtEmplacementContext.onTextWatcher(s);
                        code = zoneEtEmplacementContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteAuthentification)
                    {
                        authentificationContext.onTextWatcher(s);
                        code = authentificationContext.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                    {
                        controleDesRetourScanContext.onTextWatcher(s);
                        code = controleDesRetourScanContext.code;
                        compteurScan.setText(String.valueOf(controleDesRetourScanContext.list_result.size()) + " produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePreparationADH)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Preparation_ADH", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            preparationADHContext.ajouterProduit();
                            code = "Valider";
                        }
                        else
                        {
                            preparationADHContext.onTextWatcher(s);
                            if(s.toString().startsWith("PHITAGPLACE"))
                            {
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                nomZoneReceptionScannee.setText(preparationADHContext.zoneProduitCourant);
                                nomEmplacementReceptionScannee.setText(preparationADHContext.emplacementProduitCourant);
                                bannerTexte = "Scanner le datamatrix d'un produit";
                                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                                //preparationADHContext.scanEmplacement = false;
                                GestionAffichagePreparation();
                            }
                            else
                            {
                                //LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                                ImageSeparateur.setVisibility(View.VISIBLE);
                                referenceProduitReceptionScannee.setText(preparationADHContext.referenceProduitScanne);
                                numeroLotProduitReceptionScannee.setText(preparationADHContext.numeroLotProduitScanne);
                                peremptionProduitReceptionScannee.setText(preparationADHContext.peremptionProduitScanne);
                                if(preparationADHContext.quantiteAAfficher == 0)
                                {
                                    qteProduitReceptionScannee.setText("");
                                    textPresentationRefProduitScannee.setVisibility(GONE);
                                    LayoutResultatScanProduit.setVisibility(View.GONE);
                                    ImageSeparateur.setVisibility(View.GONE);
                                }
                                else
                                {
                                    qteProduitReceptionScannee.setText(String.valueOf(preparationADHContext.quantiteAAfficher));
                                    qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Ouvre une boite de dialogue avec un NumberPicker
                                            Context context = ScannerSearchOnlyActivity.this;
                                            String title = preparationADHContext.designationProduitScanne;
                                            String message = "Changer la quantité: ";
                                            int maxValue = preparationADHContext.quantite_max_number_picker;
                                            int value = preparationADHContext.quantiteAAfficher;

                                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    int qteAprès = Alerte.aNumberPicker.getValue();
                                                    qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                    //produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                                    //preparationADHContext.ChangerQuantite(qteAprès);
                                                    //adapter.notifyDataSetChanged();
                                                    InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                    dialog.dismiss();
                                                    GestionAffichage();
                                                }
                                            };

                                            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
                                        }
                                    });
                                }
                            }
                            GestionAffichagePreparation();
                            ExpandGroupAfterScan(preparationADHContext.designationProduitScanne);
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVide)
                    {
                        pleinVideContexte.onTextWatcher(s);
                        compteurScan.setText(String.valueOf(pleinVideContexte.stringList.size()) + " produit(s) scanné(s)");
                    }
                    else if(scannerContexteInt == R.string.scannerContextePleinVideLocalisation)
                    {
                        pleinVideLocalisationContexte.onTextWatcher(s);
                        code = pleinVideLocalisationContexte.code;
                    }
                    else if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Reception_Scannee", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("PHITAGPLACE"))
                            produitReceptionScanneeContexte.scanEmplacement = true;

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            produitReceptionScanneeContexte.AjoutDuProduit();
                            code = "Valider";
                        }
                        else
                        {
                            produitReceptionScanneeContexte.onTextWatcher(s);
                            if(produitReceptionScanneeContexte.scanEmplacement)
                            {
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                nomZoneReceptionScannee.setText(produitReceptionScanneeContexte.zoneProduitCourant);
                                nomEmplacementReceptionScannee.setText(produitReceptionScanneeContexte.emplacementProduitCourant);
                                bannerTexte = "Scanner le datamatrix d'un produit";
                                ((TextView) findViewById(R.id.banner)).setText(bannerTexte);
                                produitReceptionScanneeContexte.scanEmplacement = false;
                                GestionAffichage();
                            }
                            else
                            {
/*                                    LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                                ImageSeparateur.setVisibility(View.VISIBLE);
                                referenceProduitReceptionScannee.setText(produitReceptionScanneeContexte.referenceProduitCourant);
                                numeroLotProduitReceptionScannee.setText(produitReceptionScanneeContexte.numeroLotProduitCourant);
                                peremptionProduitReceptionScannee.setText(produitReceptionScanneeContexte.peremptionProduitCourant);
                                qteProduitReceptionScannee.setText(String.valueOf(produitReceptionScanneeContexte.quantite_a_afficher));
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = ScannerSearchOnlyActivity.this;
                                        String title = produitReceptionScanneeContexte.designationProduitCourant;
                                        String message = "Changer la quantité: ";
                                        int maxValue = 10000;
                                        int value = Integer.parseInt(produitReceptionScanneeContexte.quantiteProduitCourant);

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                //produitReceptionScanneeContexte.quantite_a_afficher = qteAprès;
                                                produitReceptionScanneeContexte.ChangerQuantite(qteAprès);
                                                //adapter.notifyDataSetChanged();
                                                dialog.dismiss();
                                                GestionAffichage();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });*/
                                GestionAffichage();
                            }
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                    {
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Reception_PAD", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        LinearResultatFranceMVO.setVisibility(View.GONE);

                        if(s.toString().startsWith("RecepetionScanneeValider"))
                        {
                            receptionPADScannerContexte.AjoutDuProduit();
                            code = "Valider";
                        }
                        else
                        {
                            receptionPADScannerContexte.onTextWatcher(s);
                            LayoutResultatScanProduit.setVisibility(View.VISIBLE);
                            ImageSeparateur.setVisibility(View.VISIBLE);
                            referenceProduitReceptionScannee.setText(receptionPADScannerContexte.referenceProduitCourant);
                            numeroLotProduitReceptionScannee.setText(receptionPADScannerContexte.numeroLotProduitCourant);
                            peremptionProduitReceptionScannee.setText(receptionPADScannerContexte.peremptionProduitCourant);
                            if(receptionPADScannerContexte.quantite_a_afficher == 0)
                            {
                                textPresentationRefProduitScannee.setVisibility(GONE);
                                LayoutResultatScanProduit.setVisibility(View.GONE);
                                ImageSeparateur.setVisibility(View.GONE);
                                qteProduitReceptionScannee.setText("");
                            }
                            else
                            {
                                qteProduitReceptionScannee.setText(String.valueOf(receptionPADScannerContexte.quantite_a_afficher));
                                qteProduitReceptionScannee.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ouvre une boite de dialogue avec un NumberPicker
                                        Context context = ScannerSearchOnlyActivity.this;
                                        String title = receptionPADScannerContexte.designationProduitCourant;
                                        String message = "Changer la quantité: ";
                                        int maxValue = receptionPADScannerContexte.quantiteMaxNumberPicker;
                                        int value = receptionPADScannerContexte.quantite_a_afficher;

                                        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                int qteAprès = Alerte.aNumberPicker.getValue();
                                                qteProduitReceptionScannee.setText(String.valueOf(String.valueOf(qteAprès).trim()));
                                                receptionPADScannerContexte.quantite_a_afficher = qteAprès;
                                                /*receptionPADScannerContexte.ChangerQuantite(qteAprès);*/
                                                //adapter.notifyDataSetChanged();
                                                InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                                dialog.dismiss();
                                                GestionAffichage();
                                            }
                                        };

                                        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                                    }
                                });
                            }

                            GestionAffichagePAD();
                        }
                    }
                    else if(scannerContexteInt == R.string.scannerContexteService)
                    {
                        serviceContexte.onTextWatcher(s);
                        code = serviceContexte.code;
                    }
                    else
                    {
                        produitContexte.onTextWatcher(s);
                        if(modeTrace)
                        {
                            tableTrace = new TableTrace(id, date, "Context_Produit", "Récupération après scan", s.toString().substring(0, s.length() - 1), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                            rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                            if(rowId != -1)
                            {
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                            }
                        }
                        if(produitContexte.stringList != null)
                            compteurScan.setText(String.valueOf(produitContexte.stringList.size()) + " produit(s) scanné(s)");
                        code = produitContexte.code;
                    }

                    if(!code.isEmpty()){
                        Intent resultIntent = new Intent();
                        if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee)
                        {
                            resultIntent.putExtra("listeString", (Serializable) produitReceptionScanneeContexte.liste_resultat);
                        }

                        if(scannerContexteInt == R.string.scannerContexteReceptionPAD)
                        {
                            resultIntent.putExtra("listeString", (Serializable) receptionPADScannerContexte.list_result);
                        }

                        if(scannerContexteInt == R.string.scannerContexteControleDesRetours)
                        {
                            resultIntent.putExtra("listeString", (Serializable) controleDesRetourScanContext.list_result);
                        }

                        if(scannerContexteInt == R.string.scannerContexteAuthentification)
                        {
                            resultIntent.putExtra("code", authentificationContext.code);
                        }

                        if(scannerContexteInt == R.string.scannerContextePreparationADH)
                        {
                            resultIntent.putExtra("listeString", (Serializable) preparationADHContext.liste_resultat);
                            resultIntent.putExtra("listecodescannee", (Serializable) preparationADHContext.liste_code_scanne);
                        }
                        resultIntent.putExtra("code", code);
                        setResult(BarcodeCaptureActivity.RESULT_OK, resultIntent);
                        ScannerSearchOnlyActivity.this.finish();
                    }
                    else{
                        contenuCode.getText().clear();
                        contenuCodeManuel.getText().clear();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent scannerSearchOnlyIntent = new Intent();
        Bundle scannerSearchOnlyBundle = new Bundle();
        scannerSearchOnlyBundle.putString("code", "");
        if (scannerContexteInt == R.string.scannerContexteProduitReceptionScannee) {
            scannerSearchOnlyBundle.putSerializable("listeString", (Serializable) produitReceptionScanneeContexte.liste_resultat);
        }

        if (scannerContexteInt == R.string.scannerContexteReceptionPAD) {
            scannerSearchOnlyBundle.putSerializable("listeString", (Serializable) receptionPADScannerContexte.list_result);
        }
        if (scannerContexteInt == R.string.scannerContextePreparationADH) {
            scannerSearchOnlyBundle.putStringArrayList("listeString", (ArrayList) preparationADHContext.liste_resultat);
        }
        scannerSearchOnlyBundle.putStringArrayList("stringList", (ArrayList) pleinVideContexte.stringList);
        scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
        ScannerSearchOnlyActivity.this.setResult(RESULT_OK, scannerSearchOnlyIntent);
        ScannerSearchOnlyActivity.this.finish();
    }

    // Class permettant d'envoyer un email
    private class SendEmailTask extends AsyncTask<String, Object, Object> {

        @Override
        protected Object doInBackground(String... email) {
            Mail sender = new Mail(ScannerSearchOnlyActivity.this, email[0], true, db);
            try {
                sender.sendMailVerification(subject, body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "executed";
        }
    }

    //method to expand all groups
    private void expandAll() {
        int count = produit_ReceptionScanneeAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ListViewProduitReceptionScannee.expandGroup(i);
        }
    }

    //method to expand all groups
    private void expandAllPreparation(List<Integer> listIndex) {
        for (int i = 0; i < listIndex.size(); i++) {
            int indexExpand = listIndex.get(i);
            ListViewProduitReceptionScannee.expandGroup(indexExpand);
        }
    }

    //method to expand all groups
    private void expandAllReceptionPAD() {
        int count = receptionPADExpandableAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            ListViewProduitReceptionScannee.expandGroup(i);
        }
    }

    public void afficherAlerteFranceMVO(String produitDesignation, String resultat, String numeroSerie, String motif)
    {
        TextView DesignationProduitFranceMVO = (TextView) findViewById(R.id.DesignationProduitFranceMVO);
        TextView NumeroSerieFranceMVO = (TextView) findViewById(R.id.NumeroSerieFranceMVO);
        TextView ResultatFranceMVO = (TextView) findViewById(R.id.ResultatFranceMVO);
        TextView MotifFranceMVO = (TextView) findViewById(R.id.MotifFranceMVO);

        DesignationProduitFranceMVO.setText(produitDesignation);
        NumeroSerieFranceMVO.setText(numeroSerie);
        ResultatFranceMVO.setText(resultat);
        MotifFranceMVO.setText(motif);
        LinearResultatFranceMVO.setVisibility(View.VISIBLE);
    }

    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager)ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        if(message.contentEquals("Produit préparé"))
        {
            layout.setBackgroundColor(getResources().getColor(R.color.vert3, null));
        }
        else
        {
            layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        }

        if(message.contentEquals("Produit déjà préparé en intégralité"))
        {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            v.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE));

        }

        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 50, 0, 0);
        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    public void GestionAffichage()
    {
        designationProduitReceptionScannee.setText(produitReceptionScanneeContexte.designationProduitCourant);
        produit_ReceptionScanneeAdapter = new Produit_ReceptionScanneeAdapter(ScannerSearchOnlyActivity.this, db, produitReceptionScanneeContexte.ZoneEmplacement, produitReceptionScanneeContexte.mapExpandable, utilisateurConnecte);
        ListViewProduitReceptionScannee.setAdapter(produit_ReceptionScanneeAdapter);
        ListViewProduitReceptionScannee.setDivider(footer);
        if(produitReceptionScanneeContexte.mapExpandable.size() != 0)
        {
            expandAll();
            ListViewProduitReceptionScannee.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int groupePosition, int childPosition, long l) {
                    ObjetReceptionScannee objetClick = (ObjetReceptionScannee) produit_ReceptionScanneeAdapter.getChild(groupePosition, childPosition);
                    onClick_Child_Reception_Scannee(objetClick);
                    GestionAffichage();
                    return true;
                }
            });
        }
    }

    public void onClick_Child_Preparation_Scannee(final ObjetReceptionScannee objetCourant, final int quantiteRestante) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_preparation_uf_adh, null);

        TextView titre = (TextView) view.findViewById(R.id.titre);
        final EditText numeroSerie = (EditText) view.findViewById(R.id.numeroSerie);
        final EditText numeroLot = (EditText) view.findViewById(R.id.numeroLot);
        final TextView datePeremption = (TextView) view.findViewById(R.id.datePeremption);
        final TextView quantite = (TextView) view.findViewById(R.id.quantite);

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objetCourant.getGs1_scannee());
        final String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String serie= gs1Decoupe.get(OutilsDecodage.numeroSerie);
        String dateString = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        String[] date_tab = dateString.split("-");
        dateString = date_tab[2]+"/"+date_tab[1]+"/"+date_tab[0];


        final Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        titre.setText(produit_courant.getDesignation_interne());
        numeroSerie.setText(serie);
        numeroLot.setText(lot);
        datePeremption.setText(dateString);
        quantite.setText(String.valueOf(objetCourant.getQuantiteScannee()));

        /* Gestion des numbers pickers */
        quantite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir la quantite";
                String message = "Nouvelle quantite : ";
                int maxValue = quantiteRestante;
                int value = objetCourant.getQuantiteScannee();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = Alerte.aNumberPicker.getValue();
                        quantite.setText(String.valueOf(qteAprès));
                        objetCourant.setQuantiteScannee(qteAprès);
                        InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(ScannerSearchOnlyActivity.this, title, message, value, maxValue, onClickListener);
            }
        });

        final Calendar date = Calendar.getInstance();;
        final DatePickerDialog.OnDateSetListener dateRepriseDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

                datePeremption.setText(sdf.format(date.getTime()));
            }

        };

        //Récupération de la date
        final String dateMinString = dateString;
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);


        if(dateString != null && !dateString.contentEquals(""))
        {
            datePeremption.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                        DatePickerDialog dateReprisePickerDialog = new DatePickerDialog(ScannerSearchOnlyActivity.this, dateRepriseDuDatePicker, year, month, day);

                        dateReprisePickerDialog.show();
                }
            });
        }

        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date_tab = datePeremption.getText().toString().split("/");
                String nouvelle_date = date_tab[2].substring(0, 2)+date_tab[1]+date_tab[0];
                String new_gs1 = gtin+"21"+numeroSerie.getText().toString()+"@17"+nouvelle_date+"10"+numeroLot.getText().toString();
                objetCourant.setGs1_scannee(new_gs1);
                alertDialog.dismiss();
                expandGroupAfterQuantite(produit_courant.getDesignation_interne());
            }
        });

    }

    public void onClick_Child_Reception_Scannee(final ObjetReceptionScannee objetCourant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_reception_scanne, null);

        TextView titre = (TextView) view.findViewById(R.id.titre);
        final EditText numeroSerie = (EditText) view.findViewById(R.id.numeroSerie);
        final EditText numeroLot = (EditText) view.findViewById(R.id.numeroLot);
        final TextView datePeremption = (TextView) view.findViewById(R.id.datePeremption);
        final TextView quantite = (TextView) view.findViewById(R.id.quantite);
        final TextView uniteProduit = (TextView) view.findViewById(R.id.uniteProduit);

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objetCourant.getGs1_scannee());
        final String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
        String serie= gs1Decoupe.get(OutilsDecodage.numeroSerie);
        String dateString = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        String[] date_tab = dateString.split("-");
        dateString = date_tab[2]+"/"+date_tab[1]+"/"+date_tab[0];


        Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
        titre.setText(produit_courant.getDesignation_interne());
        numeroSerie.setText(serie);
        numeroLot.setText(lot);
        datePeremption.setText(dateString);
        quantite.setText(String.valueOf(objetCourant.getQuantiteScannee()));
        uniteProduit.setText(produit_courant.getUnite()+" x"+String.valueOf((int) produit_courant.getCond_achat()));

        /* Gestion des numbers pickers */
        quantite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvre une boite de dialogue avec un NumberPicker
                String title = "Saisir la quantite";
                String message = "Nouvelle quantite : ";
                int maxValue = 100000;
                int value = objetCourant.getQuantiteScannee();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = Alerte.aNumberPicker.getValue();
                        quantite.setText(String.valueOf(qteAprès));
                        objetCourant.setQuantiteScannee(qteAprès);
                        InputMethodManager imm = (InputMethodManager) ScannerSearchOnlyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(ScannerSearchOnlyActivity.this, title, message, value, maxValue, onClickListener);
            }
        });

        final Calendar date = Calendar.getInstance();;
        final DatePickerDialog.OnDateSetListener dateRepriseDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

                datePeremption.setText(sdf.format(date.getTime()));
            }

        };

        //Récupération de la date
        final String dateMinString = dateString;
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);



        datePeremption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dateReprisePickerDialog = new DatePickerDialog(ScannerSearchOnlyActivity.this, dateRepriseDuDatePicker, year, month, day);

                dateReprisePickerDialog.show();
            }
        });

        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] date_tab = datePeremption.getText().toString().split("/");
                String nouvelle_date = date_tab[2].substring(0, 2)+date_tab[1]+date_tab[0];
                String new_gs1 = gtin+"21"+numeroSerie.getText().toString()+"@17"+nouvelle_date+"10"+numeroLot.getText().toString();
                objetCourant.setGs1_scannee(new_gs1);
                alertDialog.dismiss();
                GestionAffichage();
            }
        });

    }

    public void GestionAffichagePAD()
    {
        designationProduitReceptionScannee.setText(receptionPADScannerContexte.designationProduitCourant);
        if(receptionPADScannerContexte.mapResult != null)
        {
            receptionPADExpandableAdapter = new Produit_ReceptionPADAdapter(ScannerSearchOnlyActivity.this, db, receptionPADScannerContexte.listeObjetReceptionPAD, receptionPADScannerContexte.mapResult, utilisateurConnecte);
            ListViewProduitReceptionScannee.setAdapter(receptionPADExpandableAdapter);
            ListViewProduitReceptionScannee.setDivider(footer);
            if(receptionPADScannerContexte.mapResult.size() != 0)
            {
                expandAllReceptionPAD();
            }
        }
    }

    public void GestionAffichagePreparation()
    {
        designationProduitReceptionScannee.setText(preparationADHContext.designationProduitScanne);
        produitPreparationScanneeAdapter = new Produit_PreparationScanneeAdapter(ScannerSearchOnlyActivity.this, db, preparationADHContext.liste_designation_produit, preparationADHContext.mapExpandable, utilisateurConnecte, preparationADHContext.Map_Zone_Emplacement_Defaut);
        ListViewProduitReceptionScannee.setAdapter(produitPreparationScanneeAdapter);
        ListViewProduitReceptionScannee.setDivider(footer);
        if(preparationADHContext.mapExpandable.size() != 0)
        {
           for(int i = 0; i < produitPreparationScanneeAdapter.getGroupCount(); i++)
           {
               ListViewProduitReceptionScannee.collapseGroup(i);
           }
        }

        ListViewProduitReceptionScannee.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupePosition, int childPosition, long l) {
                ObjetReceptionScannee objetClick = (ObjetReceptionScannee) produitPreparationScanneeAdapter.getChild(groupePosition, childPosition);
                String phraseGroup = produitPreparationScanneeAdapter.getGroup(groupePosition).toString();
                String[] tab_phraseGroup = phraseGroup.split("-");
                int quantite_totale = Integer.parseInt(tab_phraseGroup[tab_phraseGroup.length-1]);
                int quantiteRestante = 0;
                int quantite_preparer = 0;
                List<ObjetReceptionScannee> listEnfant = produitPreparationScanneeAdapter.objetParentListItems.get(produitPreparationScanneeAdapter.produitListe.get(groupePosition));
                for(int i = 0; i < listEnfant.size(); i++)
                {
                    if(i < listEnfant.size()-1)
                    {
                        quantite_preparer = quantite_preparer+listEnfant.get(i).getQuantiteScannee();
                    }
                }
                quantiteRestante = quantite_totale-quantite_preparer;
                onClick_Child_Preparation_Scannee(objetClick, quantiteRestante);
                GestionAffichagePreparation();
                return false;
            }
        });
    }

    public void ExpandGroupAfterScan(String designationProduit)
    {
        for(int i = 0; i < produitPreparationScanneeAdapter.getGroupCount(); i++)
        {
            String designationCourante = produitPreparationScanneeAdapter.getGroup(i).toString();
            String[] tab_designation_courante = designationCourante.split("-");
            designationCourante = "";
            for(int j = 0;j < tab_designation_courante.length-1; j++)
            {
                designationCourante = designationCourante+" "+tab_designation_courante[j];
            }

            if(designationCourante.startsWith(" "))
                designationCourante = designationCourante.substring(1);

            if(designationProduit != null)
            {
                if(designationProduit.trim().contentEquals(designationCourante.trim()))
                {
                    ListViewProduitReceptionScannee.expandGroup(i);

                    if(preparationADHContext.emplacement_saisie)
                    {
                        int last_child = produitPreparationScanneeAdapter.getChildrenCount(i)-1;
                        ObjetReceptionScannee lastchildobjet = (ObjetReceptionScannee) produitPreparationScanneeAdapter.getChild(i, last_child);
                       // if(lastchildobjet.getEmplacement_uid() == 0 && !lastchildobjet.getGs1_scannee().contentEquals(""))
                        //{
                          //  produitPreparationScanneeAdapter.getChildView(i, last_child, true, null, ListViewProduitReceptionScannee).findViewById(R.id.quantite).performClick();
                        //}
                    }
                }
            }
        }
    }

    public void expandGroupAfterQuantite(String designationProduit)
    {
        for(int i = 0; i < produitPreparationScanneeAdapter.getGroupCount(); i++)
        {
            String designationCourante = produitPreparationScanneeAdapter.getGroup(i).toString();
            String[] tab_designation_courante = designationCourante.split("-");
            designationCourante = "";
            for(int j = 0;j < tab_designation_courante.length-1; j++)
            {
                designationCourante = designationCourante+" "+tab_designation_courante[j];
            }

            if(designationCourante.startsWith(" "))
                designationCourante = designationCourante.substring(1);

            if(designationProduit.trim().contentEquals(designationCourante.trim()))
            {
                ListViewProduitReceptionScannee.expandGroup(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        if(scannerContexteInt == R.string.scannerContexteProduitReceptionScannee|| scannerContexteInt == R.string.scannerContexteReceptionPAD || scannerContexteInt == R.string.scannerContextePreparationADH)
            menu.findItem(R.id.menuSave).setVisible(true);
        else
            menu.findItem(R.id.menuSave).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onBackPressed();
                return true;
            }
        });
        return true;
    }
}
