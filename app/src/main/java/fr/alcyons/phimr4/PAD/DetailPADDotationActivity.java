package fr.alcyons.phimr4.PAD;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation_Patient;
import fr.alcyons.phimr4.Classes.PAD_Proposition;
import fr.alcyons.phimr4.Classes.PAD_Proposition_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.MedicalObjective;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.PrisePhoto.PrisePhoto;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by jessica on 03/04/2018.
 */

public class DetailPADDotationActivity extends ServiceActivity {

    public ImageView photoImageView;
    Depot depotPUI;
    Depot depotPatient;
    Protocoles_Patients protocoles_patients;
    PAD_Proposition padProposition;
    Produit produitCourant;
    List<String> listeDate;
    List<PAD_Proposition_Ligne> padPropositionLigneList;
    List<Dotation_Patient> dotationPatientList;
    String IPP;
    String Vd_Livraison_Max_String;
    String referenceCycle;
    String CycleDu;
    String CycleAu;
    int idcycle;
    int listTaille = 0;
    int listPosition = 0;
    int listIndicateur = 0;
    int n = 1;
    TextView nomProduitTextView;
    TextView fournisseurProduitTextView;
    TextView referenceProduitTextView;
    TextView indicateurProgressionTextView;

    TextView recevoirProduitOuiTextView;
    TextView recevoirProduitNonTextView;
    EditText dotationQteALivrerEditText;

    LinearLayout dotationQteALivrerLinearLayout;

    Bitmap produitPhoto_Bitmap;
    AlertDialog alertDialog;

    boolean recevoirProduit;
    boolean choisirQteProduit;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pad_dotation);

        //gestion du package manager
        pm = DetailPADDotationActivity.this.getPackageManager();

        //  Récupération élément graphique
        indicateurProgressionTextView = (TextView) findViewById(R.id.indicateurProgression);

        //  Zone Produit
        photoImageView = (ImageView) findViewById(R.id.photo);
        nomProduitTextView = (TextView) findViewById(R.id.nomProduit);
        fournisseurProduitTextView = (TextView) findViewById(R.id.fournisseurProduit);
        referenceProduitTextView = (TextView) findViewById(R.id.referenceProduit);
        //  Zone Saisie
        recevoirProduitOuiTextView = (TextView) findViewById(R.id.recevoirProduitOui);
        recevoirProduitNonTextView = (TextView) findViewById(R.id.recevoirProduitNon);
        dotationQteALivrerEditText = (EditText) findViewById(R.id.dotationQteALivrer);
        dotationQteALivrerLinearLayout = (LinearLayout) findViewById(R.id.dotationQteALivrerLinearLayout);


        //  Récupération intent
        depotPatient = (Depot) intent.getExtras().getSerializable("DepotPatient");
        IPP = intent.getExtras().getString("IPP");
        padProposition = (PAD_Proposition) intent.getExtras().getSerializable("padProposition");
        listeDate = intent.getStringArrayListExtra("listeDate");
        depotPatient = (Depot) intent.getExtras().getSerializable("DepotPatient");
        IPP = intent.getExtras().getString("IPP");
        Vd_Livraison_Max_String = intent.getExtras().getString("Vd_Livraison_Max_String");
        referenceCycle = intent.getExtras().getString("referenceCycle");
        CycleDu = intent.getExtras().getString("CycleDu");
        CycleAu = intent.getExtras().getString("CycleAu");
        idcycle = intent.getExtras().getInt("idcycle");

        // Récupération Protocoles_Patients / Dotation_Patient
        protocoles_patients = Protocoles_PatientsOpenHelper.getProtocoles_PatientsByIPP(db, IPP);
        padPropositionLigneList = new ArrayList<>();
        if (protocoles_patients != null) {
            dotationPatientList = Dotation_PatientOpenHelper.getDotation_PatientByProcotolesPatients(db, protocoles_patients.get_UID());
            if (dotationPatientList != null) {
                Collections.sort(dotationPatientList, new Comparator<Dotation_Patient>() {
                    @Override
                    public int compare(Dotation_Patient o1, Dotation_Patient o2) {
                        return o1.getDesignation().toLowerCase().compareTo(o2.getDesignation().toLowerCase());
                    }
                });
                for (Dotation_Patient dotation_patient : dotationPatientList) {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, dotation_patient.getCode_Produit());
                    PAD_Proposition_Ligne padPropositionLigne = new PAD_Proposition_Ligne(produit.getID_produit(), dotation_patient.get_UID(), true, false, 0);
                    padPropositionLigneList.add(padPropositionLigne);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        depotPUI = DepotOpenHelper.getDepotPUI(db);
        listTaille = padPropositionLigneList.size();
        manageView();
        invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    if (resultCode == DetailPADDotationActivity.RESULT_OK) {
                        String gs1 = data.getStringExtra("code");

                        int compteurErreur = 0;

                        if (gs1.length() == 13) {
                            produitCourant.setGTIN(gs1);
                            compteurErreur++;
                        } else {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
                            produitCourant.setGTIN(gs1Decoupe.get(OutilsDecodage.codeGtin));
                            compteurErreur++;
                        }

                        long rowId = gestionnaireProduit.mettreAJourProduit(db, produitCourant);
                        if (rowId != -1) {
                            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitCourant.getPhiMR4UUID(), produitCourant.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                        }


                        if (compteurErreur == 0) {
                            Alerte.afficherAlerte(DetailPADDotationActivity.this, "Alerte", "Impossible de récupérer le code GTIN du produit.", "alerte");
                        } else {
                            Intent detailPADDotation_Intent = new Intent(DetailPADDotationActivity.this, PrisePhoto.class);
                            Bundle detailPADDotation_Bundle = DetailPADDotationActivity.super.getBundle();
                            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                            detailPADDotation_Bundle.putString("nomProduit", produitCourant.getDesignation_ext());
                            detailPADDotation_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            detailPADDotation_Intent.putExtras(detailPADDotation_Bundle);
                            DetailPADDotationActivity.this.startActivityForResult(detailPADDotation_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
                        }
                    }
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduit = data.getStringExtra("photoProduit");
                    if (photoProduit != null) {
                        Uri imageUri = Uri.parse(photoProduit);
                        if (imageUri != null) {
                            try {
                                produitPhoto_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            photoImageView.setImageBitmap(produitPhoto_Bitmap);
                            if (OutilsGestionConnexionReseau.isServerAccessible(DetailPADDotationActivity.this)) {
                                MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depotPUI, depotPatient, produitCourant, true);
                                medicalObjective.savePicture(produitPhoto_Bitmap, String.valueOf(n), "PADDotation", false);
                                if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                                {
                                    boolean master = Alerte.afficherAlerte(DetailPADDotationActivity.this, "Master", "Publier la photo sur master ?", "OuiNon");
                                    if(master)
                                    {
                                        medicalObjective.savePicture(produitPhoto_Bitmap, String.valueOf(n), "PADDotation", true);
                                    }
                                }
                                n = n + 1;
                                Toast.makeText(DetailPADDotationActivity.this, "Image envoyée à Médical Objective", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuDate).setVisible(true);
        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuDate);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClick_MenuDate();
                return true;
            }
        });
        return true;
    }

    private void onClick_MenuDate(){
        List<String> dateFormatDDMMAAAAList = new ArrayList<>();
        for(String date_yyyyMMdd: listeDate){
            String date_ddMMyyyy = "";
            try {
                DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                Date date = dateDecodeur.parse(date_yyyyMMdd);
                date_ddMMyyyy = dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFormatDDMMAAAAList.add(date_ddMMyyyy);
        }
        Alerte.afficherAlerteList(this,"Information","Voici vos dates de livraison",dateFormatDDMMAAAAList,"alerte");
    }

    protected void onClick_barreDeTitre(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.alerte_pad_produit, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        for (final PAD_Proposition_Ligne padPropositionLigne : padPropositionLigneList) {
            Produit produit = ProduitOpenHelper.getProduitByID(db, padPropositionLigne.produitID);
            View viewRow = LayoutInflater.from(this).inflate(R.layout.row_pad_produit, null);

            ColorStateList checkColorStateList;
            ColorStateList intituleColorStateList;
            if (padPropositionLigne.confirmer) {
                checkColorStateList = getResources().getColorStateList(R.color.vert, null);
                intituleColorStateList = getResources().getColorStateList(R.color.noir, null);
            } else {
                checkColorStateList = getResources().getColorStateList(R.color.grey_color_fonce, null);
                intituleColorStateList = checkColorStateList;
            }
            viewRow.findViewById(R.id.check).setBackgroundTintList(checkColorStateList);
            ((TextView) viewRow.findViewById(R.id.intitule)).setTextColor(intituleColorStateList);
            ((TextView) viewRow.findViewById(R.id.intitule)).setText(produit.getDesignation_ext());
            LinearLayout rowPadProduitLinearLayout = (LinearLayout) viewRow.findViewById(R.id.rowPadProduitLinearLayout);
            rowPadProduitLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    listPosition = padPropositionLigneList.indexOf(padPropositionLigne);
                    manageView();
                }
            });
            linearLayout.addView(viewRow);
        }
        LinearLayout validerAlertePadProduit = (LinearLayout) view.findViewById(R.id.validerAlertePadProduit);
        validerAlertePadProduit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.setCancelable(true);
        if (alertDialog.isShowing() == false) {
            alertDialog.show();
        }
    }

    protected void onClick_Precedent(View v) {
        if (listPosition != 0) {
            listPosition--;
        } else {
            listPosition = listTaille - 1;
        }
        manageView();
    }

    protected void onClick_Photo(View v) {
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            if (produitCourant.getGTIN().length() == 0) {
                Intent detailPADDotation_Intent = new Intent(DetailPADDotationActivity.this, BarcodeCaptureActivity.class);
                Bundle detailPADDotation_Bundle = DetailPADDotationActivity.super.getBundle();
                detailPADDotation_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                detailPADDotation_Bundle.putBoolean("modeRafale", false);
                detailPADDotation_Bundle.putBoolean("modePhoto", false);
                detailPADDotation_Intent.putExtras(detailPADDotation_Bundle);
                DetailPADDotationActivity.this.startActivityForResult(detailPADDotation_Intent, CodesEchangesActivites.RETOUR_CODE_GS1);
            } else {
                Intent detailPADDotation_Intent = new Intent(DetailPADDotationActivity.this, PrisePhoto.class);
                Bundle detailPADDotation_Bundle = DetailPADDotationActivity.super.getBundle();
                detailPADDotation_Bundle.putString("nomProduit", produitCourant.getDesignation_ext());
                // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                detailPADDotation_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                detailPADDotation_Intent.putExtras(detailPADDotation_Bundle);
                DetailPADDotationActivity.this.startActivityForResult(detailPADDotation_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
            }
        }
        else
        {
            Intent detailPADDotation_Intent = new Intent(DetailPADDotationActivity.this, PrisePhoto.class);
            Bundle detailPADDotation_Bundle = DetailPADDotationActivity.super.getBundle();
            detailPADDotation_Bundle.putString("nomProduit", produitCourant.getDesignation_ext());
            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
            detailPADDotation_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            detailPADDotation_Intent.putExtras(detailPADDotation_Bundle);
            DetailPADDotationActivity.this.startActivityForResult(detailPADDotation_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
        }
    }

    protected void onClick_Suivant(View v) {
        if (listPosition != listTaille - 1) {
            listPosition++;
        } else {
            listPosition = 0;
        }
        manageView();
    }

    public void onClick_recevoirProduitOui(View v){
        recevoirProduitOuiTextView.setTextColor(getResources().getColor(R.color.vert));
        recevoirProduitNonTextView.setTextColor(Color.GRAY);
        dotationQteALivrerLinearLayout.setVisibility(View.VISIBLE);

        recevoirProduit = true;
        choisirQteProduit = false;

        PAD_Proposition_Ligne padPropositionLigne = padPropositionLigneList.get(listPosition);
        int dotationID = padPropositionLigne.dotationID;
        Dotation_Patient dotationPatient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, dotationID);

        dotationQteALivrerEditText.setText(String.valueOf(Math.round(dotationPatient.getQté())));
    }

    public void onClick_recevoirProduitNon(View v){
        recevoirProduitNonTextView.setTextColor(getResources().getColor(R.color.rouge));
        recevoirProduitOuiTextView.setTextColor(Color.GRAY);
        dotationQteALivrerLinearLayout.setVisibility(View.INVISIBLE);

        recevoirProduit = false;
        choisirQteProduit = false;

        PAD_Proposition_Ligne padPropositionLigne = padPropositionLigneList.get(listPosition);
        int dotationID = padPropositionLigne.dotationID;
        Dotation_Patient dotationPatient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, dotationID);

        dotationQteALivrerEditText.setText(String.valueOf(Math.round(dotationPatient.getQté())));

        findViewById(R.id.confirmerButton).setVisibility(View.VISIBLE);
        findViewById(R.id.validerButton).setVisibility(View.GONE);
    }

    protected void onClick_ValiderButton(View v) {
        findViewById(R.id.confirmerButton).setVisibility(View.VISIBLE);
        findViewById(R.id.validerButton).setVisibility(View.GONE);
    }

    protected void onClick_ConfirmerButton(View v) {

        Dotation_Patient dotation_patient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, padPropositionLigneList.get(listPosition).dotationID);
        double dotationPatientQte = dotation_patient.getQté();
        int qteALivrer = (int) dotationPatientQte;

        String qteALivrer_VA = dotationQteALivrerEditText.getText().toString();
        if (!qteALivrer_VA.contentEquals("")) {
            qteALivrer = Integer.parseInt(qteALivrer_VA);
        }

        choisirQteProduit = false;
        if(dotationPatientQte != qteALivrer){
            choisirQteProduit = true;
        }

        padPropositionLigneList.get(listPosition).recevoirProduit = recevoirProduit;
        padPropositionLigneList.get(listPosition).choisirQteProduit = choisirQteProduit;
        padPropositionLigneList.get(listPosition).qteALivrer = qteALivrer;
        padPropositionLigneList.get(listPosition).confirmer = true;
        listPosition++;

        boolean saisieTermine = true;
        for (PAD_Proposition_Ligne padPropositionLigne : padPropositionLigneList) {
            if (padPropositionLigne.confirmer == false) {
                saisieTermine = false;
                break;
            }
        }
        if (saisieTermine) {
            finishActivity();
        } else {
            manageView();
        }
    }

    private void manageView() {

        if (listPosition < listTaille) {

            //  PAD_PROPOSITION_LIGNE
            PAD_Proposition_Ligne padPropositionLigne = padPropositionLigneList.get(listPosition);
            int produitCode = padPropositionLigne.produitID;
            int dotationID = padPropositionLigne.dotationID;

            //  INDICATEUR
            listIndicateur = listPosition + 1;
            indicateurProgressionTextView.setText(String.valueOf(listIndicateur) + "/" + String.valueOf(listTaille));

            // BOUTON
            findViewById(R.id.confirmerButton).setVisibility(View.GONE);
            findViewById(R.id.validerButton).setVisibility(View.VISIBLE);

            //  PRODUIT
            produitCourant = null;
            produitCourant = ProduitOpenHelper.getProduitByID(db, produitCode);

            nomProduitTextView.setText(produitCourant.getDesignation_ext());
            fournisseurProduitTextView.setText(produitCourant.getFournisseur());
            referenceProduitTextView.setText(produitCourant.getRef_fourni());

            // DOTATION_PATIENT
            Dotation_Patient dotationPatient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, dotationID);

            //  IMAGE
            photoImageView.setImageBitmap(null);
            MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depotPUI, depotPatient, produitCourant, true);
            medicalObjective.getPictureImage("PADDotation");

            //  SWITCH
            if (padPropositionLigne.confirmer) {

                if(padPropositionLigne.recevoirProduit){
                    recevoirProduitOuiTextView.setTextColor(getResources().getColor(R.color.vert));
                    recevoirProduitNonTextView.setTextColor(Color.GRAY);
                    dotationQteALivrerLinearLayout.setVisibility(View.VISIBLE);
                }
                else{
                    recevoirProduitNonTextView.setTextColor(getResources().getColor(R.color.rouge));
                    recevoirProduitOuiTextView.setTextColor(Color.GRAY);
                    dotationQteALivrerLinearLayout.setVisibility(View.INVISIBLE);
                }
                recevoirProduit = padPropositionLigne.recevoirProduit;
                choisirQteProduit = padPropositionLigne.choisirQteProduit;
                dotationQteALivrerEditText.setText(String.valueOf(padPropositionLigne.qteALivrer));

            } else {
                recevoirProduit = true;
                choisirQteProduit = false;
                recevoirProduitNonTextView.setTextColor(Color.GRAY);
                recevoirProduitOuiTextView.setTextColor(getResources().getColor(R.color.vert));
                dotationQteALivrerLinearLayout.setVisibility(View.VISIBLE);
                dotationQteALivrerEditText.setText(String.valueOf(Math.round(dotationPatient.getQté())));
            }
        } else {
            boolean saisieTermine = true;
            for (PAD_Proposition_Ligne padPropositionLigne : padPropositionLigneList) {
                if (padPropositionLigne.confirmer == false) {
                    saisieTermine = false;
                    listPosition = padPropositionLigneList.indexOf(padPropositionLigne);
                    break;
                }
            }
            if (saisieTermine) {
                finishActivity();
            } else {
                manageView();
            }
        }
    }

    private void finishActivity() {

        padProposition.padPropositionLigneList.addAll(padPropositionLigneList);

        Bundle detailPADDotation_Bundle = new Bundle();
        detailPADDotation_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailPADDotation_Bundle.putInt("serviceSelectionneID", serviceActuel.getId());
        detailPADDotation_Bundle.putSerializable("padProposition", padProposition);
        detailPADDotation_Bundle.putStringArrayList("listeDate", (ArrayList<String>) listeDate);
        detailPADDotation_Bundle.putSerializable("DepotPatient", depotPatient);
        detailPADDotation_Bundle.putString("IPP", IPP);
        detailPADDotation_Bundle.putString("Vd_Livraison_Max_String", Vd_Livraison_Max_String);
        detailPADDotation_Bundle.putString("referenceCycle", referenceCycle);
        detailPADDotation_Bundle.putString("CycleDu", CycleDu);
        detailPADDotation_Bundle.putString("CycleAu", CycleAu);
        detailPADDotation_Bundle.putInt("idcycle", idcycle);

        Intent detailPADDotation_Intent = new Intent(DetailPADDotationActivity.this, DetailPADActivity.class);
        detailPADDotation_Intent.putExtras(detailPADDotation_Bundle);

        DetailPADDotationActivity.this.startActivity(detailPADDotation_Intent);
        DetailPADDotationActivity.this.finish();
    }
}
