package fr.alcyons.phimr4.PAD;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Prescription_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.Classes.Composants_patient;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PAD_Proposition;
import fr.alcyons.phimr4.Classes.PAD_Proposition_Ligne;
import fr.alcyons.phimr4.Classes.Prescription_patient;
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


public class DetailPADComposantPrescriptionActivity extends ServiceActivity {


    public ImageView photoImageView;
    Depot depotPUI;
    Depot depotPatient;
    PAD_Proposition padProposition;
    Produit produitCourant;
    Protocoles_Patients protocoles_patients;
    List<String> listeDate;
    List<PAD_Proposition_Ligne> padPropositionLigneList;
    List<Composants_patient> composantsPatientList;
    List<Prescription_patient> prescriptionPatientList;
    String CycleAu;
    String CycleDu;
    String IPP;
    String referenceCycle;
    String Vd_Livraison_Max_String;
    int idcycle;
    int listIndicateur = 0;
    int listPosition = 0;
    int listTaille = 0;
    int n = 1;
    int qteCartonFerme = 0;
    int qteUnite = 0;
    AlertDialog alertDialog;
    Bitmap produitPhoto_Bitmap;
    TextView referenceProduitTextView;
    TextView fournisseurProduitTextView;
    TextView indicateurProgressionTextView;
    TextView nbSeanceTextView;
    TextView nomProduitTextView;
    TextView conditionnementProduitTextView;

    EditText qteCartonFermeEditText;
    EditText qteUniteEditText;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pad_composant_prescription);

        //gestion du package manager
        pm = DetailPADComposantPrescriptionActivity.this.getPackageManager();

        //  Récupération élément graphique
        indicateurProgressionTextView = (TextView) findViewById(R.id.indicateurProgression);
        //  Zone Produit
        photoImageView = (ImageView) findViewById(R.id.photo);
        nomProduitTextView = (TextView) findViewById(R.id.nomProduit);
        fournisseurProduitTextView = (TextView) findViewById(R.id.fournisseurProduit);
        referenceProduitTextView = (TextView) findViewById(R.id.referenceProduit);
        conditionnementProduitTextView = (TextView) findViewById(R.id.conditionnementProduit);
        //  Zone Saisie
        TextWatcher textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                updateNbSeanceTextView();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        qteCartonFermeEditText = (EditText) findViewById(R.id.qteCartonFerme);
        qteCartonFermeEditText.addTextChangedListener(textWatcher);
        qteUniteEditText = (EditText) findViewById(R.id.qteUnite);
        qteUniteEditText.addTextChangedListener(textWatcher);
        //  Zone Seance
        nbSeanceTextView = (TextView) findViewById(R.id.nbSeance);

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

        // Récupération Protocoles_Patients / Composants_patient / Prescription_patient
        protocoles_patients = Protocoles_PatientsOpenHelper.getProtocoles_PatientsByIPP(db, IPP);

        padPropositionLigneList = new ArrayList<>();

        if (protocoles_patients != null) {
            composantsPatientList = Composants_patientOpenHelper.getComposants_patientByProcotolesPatients(db, protocoles_patients.get_UID());
            if (composantsPatientList != null) {
                Collections.sort(composantsPatientList, new Comparator<Composants_patient>() {
                    @Override
                    public int compare(Composants_patient o1, Composants_patient o2) {
                        return o1.getDésignation().toLowerCase().compareTo(o2.getDésignation().toLowerCase());
                    }
                });
                for (Composants_patient composantsPatient : composantsPatientList) {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, composantsPatient.getCode_produit());
                    if(produit != null)
                    {
                        PAD_Proposition_Ligne padPropositionLigne = new PAD_Proposition_Ligne(composantsPatient.getCode_produit(), composantsPatient.get_UID(), 0, produit.getCond_achat(), 0, 0);
                        padPropositionLigneList.add(padPropositionLigne);
                    }
                }
            }

            try {
                Date date = new Date();

                prescriptionPatientList = Prescription_patientOpenHelper.getPrescriptionByDate(db, date);
                if (prescriptionPatientList != null) {
                    Collections.sort(prescriptionPatientList, new Comparator<Prescription_patient>() {
                        @Override
                        public int compare(Prescription_patient o1, Prescription_patient o2) {
                            return o1.getDesignation().toLowerCase().compareTo(o2.getDesignation().toLowerCase());
                        }
                    });
                    for (Prescription_patient prescriptionPatient : prescriptionPatientList) {
                        Produit produit = ProduitOpenHelper.getProduitByID(db, prescriptionPatient.getCode_Produit());
                        if(produit != null)
                        {
                            PAD_Proposition_Ligne padPropositionLigne = new PAD_Proposition_Ligne(prescriptionPatient.getCode_Produit(), 0, prescriptionPatient.get_UID(), produit.getCond_achat(), 0, 0);
                            padPropositionLigneList.add(padPropositionLigne);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Alerte.afficherAlerte(this, "Erreur", "Aucun protocole trouvé", "alerte");
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
                    if (resultCode == DetailPADComposantPrescriptionActivity.RESULT_OK) {
                        String gs1 = data.getStringExtra("code");

                        int compteurErreur = 0;

                        if (gs1.length() == 13) {
                            produitCourant.setGTIN(gs1.trim());
                            compteurErreur++;
                        } else {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
                            produitCourant.setGTIN(gs1Decoupe.get(OutilsDecodage.codeGtin).trim());
                            compteurErreur++;
                        }

                        long rowId = gestionnaireProduit.mettreAJourProduit(db, produitCourant);
                        if (rowId != -1) {
                            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitCourant.getPhiMR4UUID(), produitCourant.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                        }


                        if (compteurErreur == 0) {
                            Alerte.afficherAlerte(DetailPADComposantPrescriptionActivity.this, "Alerte", "Impossible de récupérer le code GTIN du produit.", "alerte");
                        } else {
                            Intent detailPADComposantPrescription_Intent = new Intent(DetailPADComposantPrescriptionActivity.this, PrisePhoto.class);
                            Bundle detailPADComposantPrescription_Bundle = DetailPADComposantPrescriptionActivity.super.getBundle();
                            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                            detailPADComposantPrescription_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            detailPADComposantPrescription_Bundle.putString("nomProduit", produitCourant.getDesignation_ext());
                            detailPADComposantPrescription_Intent.putExtras(detailPADComposantPrescription_Bundle);
                            DetailPADComposantPrescriptionActivity.this.startActivityForResult(detailPADComposantPrescription_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
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
                            if (OutilsGestionConnexionReseau.isServerAccessible(DetailPADComposantPrescriptionActivity.this)) {
                                MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depotPUI, depotPatient, produitCourant, true);
                                medicalObjective.savePicture(produitPhoto_Bitmap, String.valueOf(n), "PADComposantPrescription", false);
                                if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                                {
                                    boolean master = Alerte.afficherAlerte(DetailPADComposantPrescriptionActivity.this, "Master", "Publier la photo sur master ?", "OuiNon");
                                    if(master)
                                    {
                                        medicalObjective.savePicture(produitPhoto_Bitmap, String.valueOf(n), "PADComposantPrescription", true);
                                    }
                                }
                                n = n + 1;
                                Toast.makeText(DetailPADComposantPrescriptionActivity.this, "Image envoyée à Médical Objective", Toast.LENGTH_SHORT).show();
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

    public void onClick_barreDeTitre(View v) {
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

    public void onClick_Precedent(View v) {
        if (listPosition != 0) {
            listPosition--;
        } else {
            listPosition = listTaille - 1;
        }
        manageView();
    }

    public void onClick_Photo(View v) {

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            if (produitCourant.getGTIN().length() == 0) {
                Intent detailPADComposantPrescription_Intent = new Intent(DetailPADComposantPrescriptionActivity.this, BarcodeCaptureActivity.class);
                Bundle detailPADComposantPrescription_Bundle = DetailPADComposantPrescriptionActivity.super.getBundle();
                detailPADComposantPrescription_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                detailPADComposantPrescription_Bundle.putBoolean("modeRafale", false);
                detailPADComposantPrescription_Bundle.putBoolean("modePhoto", false);
                detailPADComposantPrescription_Intent.putExtras(detailPADComposantPrescription_Bundle);
                DetailPADComposantPrescriptionActivity.this.startActivityForResult(detailPADComposantPrescription_Intent, CodesEchangesActivites.RETOUR_CODE_GS1);
            } else {
                Intent detailPADComposantPrescription_Intent = new Intent(DetailPADComposantPrescriptionActivity.this, PrisePhoto.class);
                Bundle detailPADComposantPrescription_Bundle = DetailPADComposantPrescriptionActivity.super.getBundle();
                detailPADComposantPrescription_Bundle.putString("nomProduit", produitCourant.getDesignation_ext());
                // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                detailPADComposantPrescription_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                detailPADComposantPrescription_Intent.putExtras(detailPADComposantPrescription_Bundle);
                DetailPADComposantPrescriptionActivity.this.startActivityForResult(detailPADComposantPrescription_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
            }
        }
    }

    public void onClick_Suivant(View v) {
        if (listPosition != listTaille - 1) {
            listPosition++;
        } else {
            listPosition = 0;
        }
        manageView();
    }

    public void onClick_ValiderButton(View v) {
        findViewById(R.id.confirmerButton).setVisibility(View.VISIBLE);
        findViewById(R.id.validerButton).setVisibility(View.GONE);
    }

    public void onClick_ConfirmerButton(View v) {
        checkEditText();
        padPropositionLigneList.get(listPosition).qteCartonFerme = qteCartonFerme;
        padPropositionLigneList.get(listPosition).qteUnite = qteUnite;
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

            //  INDICATEUR
            listIndicateur = listPosition + 1;
            indicateurProgressionTextView.setText(String.valueOf(listIndicateur) + "/" + String.valueOf(listTaille));

            //  BOUTON
            findViewById(R.id.confirmerButton).setVisibility(View.GONE);
            findViewById(R.id.validerButton).setVisibility(View.VISIBLE);

            //  PRODUIT
            int produitCode = padPropositionLigne.produitID;
            produitCourant = null;
            produitCourant = ProduitOpenHelper.getProduitByID(db, produitCode);

            nomProduitTextView.setText(produitCourant.getDesignation_ext());
            fournisseurProduitTextView.setText(produitCourant.getFournisseur());
            referenceProduitTextView.setText("Ref : " +produitCourant.getRef_fourni());

            conditionnementProduitTextView.setText("(x" + String.valueOf(produitCourant.getCond_achat()) + ")");
            //  IMAGE
            photoImageView.setImageBitmap(null);
            MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depotPUI, depotPatient, produitCourant, true);
            medicalObjective.getPictureImage("PADComposantPrescription");

            //  EDITTEXT
            if (padPropositionLigne.confirmer) {
                qteCartonFermeEditText.setText(String.valueOf(padPropositionLigne.qteCartonFerme));
                qteUniteEditText.setText(String.valueOf(padPropositionLigne.qteUnite));
            } else {
                qteCartonFermeEditText.setText("");
                qteUniteEditText.setText("");
            }

            qteCartonFermeEditText.requestFocus();

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

    private void checkEditText() {
        qteCartonFerme = 0;
        qteUnite = 0;

        String qteCartonFerme_String = qteCartonFermeEditText.getText().toString();
        String qteUnite_String = qteUniteEditText.getText().toString();

        if (!qteCartonFerme_String.contentEquals("")) {
            qteCartonFerme = Integer.parseInt(qteCartonFerme_String);
        }
        if (!qteUnite_String.contentEquals("")) {
            qteUnite = Integer.parseInt(qteUnite_String);
        }
    }

    private void updateNbSeanceTextView() {

        PAD_Proposition_Ligne padPropositionLigne = padPropositionLigneList.get(listPosition);
        int qteStock;
        int nbSeance = 0;

        checkEditText();

        qteStock = (produitCourant.getCond_achat() * qteCartonFerme) + qteUnite;

        if (padPropositionLigne.composantID != 0) {
            Composants_patient composantsPatient = Composants_patientOpenHelper.getComposants_patientByID(db, padPropositionLigne.composantID);
            if (composantsPatient.getQté() > 0) {
                nbSeance = (int) (qteStock / composantsPatient.getQté());
            }
        } else {
            Prescription_patient prescriptionPatient = Prescription_patientOpenHelper.getPrescription_patientByID(db, padPropositionLigne.precriptionID);
            if (prescriptionPatient.getQuantite() > 0) {
                nbSeance = qteStock / prescriptionPatient.getQuantite();
            }
        }
        nbSeanceTextView.setText(String.valueOf(nbSeance));
    }

    private void finishActivity() {

        padProposition.padPropositionLigneList.addAll(padPropositionLigneList);

        Bundle bundlePADComposantPrescription = new Bundle();
        bundlePADComposantPrescription.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundlePADComposantPrescription.putInt("serviceSelectionneID", serviceActuel.getId());
        bundlePADComposantPrescription.putSerializable("padProposition", padProposition);
        bundlePADComposantPrescription.putStringArrayList("listeDate", (ArrayList<String>) listeDate);
        bundlePADComposantPrescription.putSerializable("DepotPatient", depotPatient);
        bundlePADComposantPrescription.putString("IPP", IPP);
        bundlePADComposantPrescription.putString("Vd_Livraison_Max_String", Vd_Livraison_Max_String);
        bundlePADComposantPrescription.putString("referenceCycle", referenceCycle);
        bundlePADComposantPrescription.putString("CycleDu", CycleDu);
        bundlePADComposantPrescription.putString("CycleAu", CycleAu);
        bundlePADComposantPrescription.putInt("idcycle", idcycle);

        Intent intentPADComposantPrescription = new Intent(DetailPADComposantPrescriptionActivity.this, DetailPADDotationActivity.class);
        intentPADComposantPrescription.putExtras(bundlePADComposantPrescription);

        DetailPADComposantPrescriptionActivity.this.startActivity(intentPADComposantPrescription);
        DetailPADComposantPrescriptionActivity.this.finish();
    }
}
