package fr.alcyons.phimr4.PAD;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Dotation_PatientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Protocoles_PatientsOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Dotation_Patient;
import fr.alcyons.phimr4.Classes.PAD_Proposition;
import fr.alcyons.phimr4.Classes.PAD_Proposition_Ligne;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.ListViewAdapters.PAD_Preparation_LigneAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.MedicalObjective;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 07/03/2018.
 */

public class DetailPADActivity extends ServiceAvecConnexionActivity {

    Depot depotPatient;
    PAD_Preparation_LigneAdapter padPreparationLigneAdapter;
    PAD_Proposition padProposition;
    PH_Patient patient;

    List<PAD_Proposition_Ligne> padPropositionLigneList;
    List<String> listeDate;
    List<Protocoles_Patients> protocolesPatientsList;

    ListView padPropositionListView;
    TextView messageTextView;
    TextView titreTextView;
    ProgressBar progressBar;
    LinearLayout buttonOkLinearLayout;

    SimpleDateFormat format;
    Dialog alertePatientezDialog;

    String Vd_Livraison_Max_String;
    String referenceCycle;
    String CycleDu;
    String CycleAu;
    String IPP;

    int idcycle;
    int preparationID;

    ServicePADMethodes servicePADMethodes;

    TextView recevoirProduitOuiTextView;
    TextView recevoirProduitNonTextView;
    EditText dotationQteALivrerEditText;

    LinearLayout dotationQteALivrerLinearLayout;

    boolean recevoirProduit;
    boolean choisirQteProduit;
    PAD_Proposition_Ligne padPropositionLigneSelectionner;
    public ImageView photoImageView;
    int qteCartonFerme = 0;
    int qteUnite = 0;

    EditText qteCartonFermeEditText;
    EditText qteUniteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pad);

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

        // Récupération Protocoles_Patients
        protocolesPatientsList = Protocoles_PatientsOpenHelper.getProtocoles_PatientsByDepot(db, depotPatient.getDepot_UID());

        //  Gestion alerte
        alertePatientezDialog = new Dialog(DetailPADActivity.this);
        alertePatientezDialog.setContentView(R.layout.alerte_patientez);
        alertePatientezDialog.setCancelable(false);
        messageTextView = (TextView) alertePatientezDialog.findViewById(R.id.message);
        titreTextView = (TextView) alertePatientezDialog.findViewById(R.id.titre);
        progressBar = (ProgressBar) alertePatientezDialog.findViewById(R.id.progressBar);
        buttonOkLinearLayout = (LinearLayout) alertePatientezDialog.findViewById(R.id.buttonOk);
        buttonOkLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        //  Gestion format date
        format = new SimpleDateFormat("yyyy-MM-dd");

        //  Récupération de la liste_view à remplir
        padPropositionListView = (ListView) findViewById(R.id.listeView);
        padPropositionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Preparation_Ligne preparationLigne = (Preparation_Ligne) padPreparationLigneAdapter.getItem(position);

                for(PAD_Proposition_Ligne padPropositionLigneCourant : padPropositionLigneList){
                    if(padPropositionLigneCourant.produitID == preparationLigne.getCode_prod()){
                        padPropositionLigneSelectionner = padPropositionLigneCourant;
                        break;
                    }
                }

                if(padPropositionLigneSelectionner != null){
                    showAlerteModification();
                }
            }
        });

        padPropositionLigneList = padProposition.padPropositionLigneList;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(preparationID != 0){
            PreparationOpenHelper.supprimerUnePreparationEnBDD(db, preparationID);
            Preparation_LigneOpenHelper.supprimerUnePreparationLigneParPreparation(db, preparationID);
            preparationID = 0;
        }

        servicePADMethodes = new ServicePADMethodes(DetailPADActivity.this, db, referenceCycle, idcycle, CycleDu, CycleAu, IPP, patient, depotPatient, protocolesPatientsList, listeDate, padPropositionLigneList);
        Date Vd_Livraison_Max = null;
        //Récupération de date de prochaine livraison et de la date de livraison suivante
        if (listeDate.size() != 0) {
            try {
                Vd_Livraison_Max = format.parse(Vd_Livraison_Max_String);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        try {
            preparationID = servicePADMethodes.prevision_calculer(Vd_Livraison_Max);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Preparation_Ligne> preparationLigneList = Preparation_LigneOpenHelper.getPreparationLigneByPreparation(db, preparationID);
        // Creation de l'adapter qui va gérer la liste des zones
        padPreparationLigneAdapter = new PAD_Preparation_LigneAdapter(DetailPADActivity.this, db, preparationLigneList);
        // Remplir la vue
        padPropositionListView.setAdapter(padPreparationLigneAdapter);
        padPropositionListView.setDivider(footer);
        invalidateOptionsMenu();
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

    private void showAlertePatientez() {
        WindowManager.LayoutParams layoutParams = alertePatientezDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        if (!alertePatientezDialog.isShowing()) {
            alertePatientezDialog.show();
        }
    }

    private void updateAlertePatientez() {
        titreTextView.setText("Envoi terminé");
        messageTextView.setVisibility(View.VISIBLE);
        messageTextView.setText("Commande envoyée");
        progressBar.setVisibility(View.GONE);
        buttonOkLinearLayout.setVisibility(View.VISIBLE);
    }

    private void finishActivity() {
        alertePatientezDialog.dismiss();
        DetailPADActivity.this.finish();
    }

    private void showAlerteModification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_pad_proposition_ligne, null);

        photoImageView = (ImageView) view.findViewById(R.id.photo);
        TextView nomProduitTextView = (TextView) view.findViewById(R.id.nomProduit);
        TextView fournisseurProduitTextView = (TextView) view.findViewById(R.id.fournisseurProduit);
        TextView referenceProduitTextView = (TextView) view.findViewById(R.id.referenceProduit);
        TextView conditionnementProduitTextView = (TextView) view.findViewById(R.id.conditionnementProduit);

        LinearLayout zoneSaisieComposantPrescriptionLinearLayout = (LinearLayout) view.findViewById(R.id.zoneSaisieComposantPrescription);
        LinearLayout zoneSaisieDotationLinearLayout = (LinearLayout) view.findViewById(R.id.zoneSaisieDotation);

        //  DEPOT PUI
        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);

        //  PRODUIT
        int produitCode = padPropositionLigneSelectionner.produitID;
        Produit produit = ProduitOpenHelper.getProduitByID(db, produitCode);

         nomProduitTextView.setText(produit.getDesignation_ext());
         fournisseurProduitTextView.setText(produit.getFournisseur());
         referenceProduitTextView.setText("Ref : " +produit.getRef_fourni());

         conditionnementProduitTextView.setText("(x" + String.valueOf(produit.getCond_achat()) + ")");
        //  IMAGE
         photoImageView.setImageBitmap(null);
        MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depotPUI, depotPatient, produit, true);


        if(padPropositionLigneSelectionner.dotationID==0){
            zoneSaisieComposantPrescriptionLinearLayout.setVisibility(View.VISIBLE);
            zoneSaisieDotationLinearLayout.setVisibility(View.GONE);
            medicalObjective.getPictureImage("PADComposantPrescription");

            qteCartonFermeEditText = (EditText) view.findViewById(R.id.qteCartonFerme);
            qteUniteEditText = (EditText) view.findViewById(R.id.qteUnite);

            //  EDITTEXT
            if (padPropositionLigneSelectionner.confirmer) {
                qteCartonFermeEditText.setText(String.valueOf(padPropositionLigneSelectionner.qteCartonFerme));
                qteUniteEditText.setText(String.valueOf(padPropositionLigneSelectionner.qteUnite));
            } else {
                qteCartonFermeEditText.setText("");
                qteUniteEditText.setText("");
            }

            qteCartonFermeEditText.requestFocus();
        }
        else{
            zoneSaisieComposantPrescriptionLinearLayout.setVisibility(View.GONE);
            zoneSaisieDotationLinearLayout.setVisibility(View.VISIBLE);
            medicalObjective.getPictureImage("PADDotation");

            recevoirProduitOuiTextView = (TextView) view.findViewById(R.id.recevoirProduitOui);
            recevoirProduitNonTextView = (TextView) view.findViewById(R.id.recevoirProduitNon);
            dotationQteALivrerEditText = (EditText) view.findViewById(R.id.dotationQteALivrer);
            dotationQteALivrerLinearLayout = (LinearLayout) view.findViewById(R.id.dotationQteALivrerLinearLayout);

            if(padPropositionLigneSelectionner.recevoirProduit){
                recevoirProduitOuiTextView.setTextColor(getResources().getColor(R.color.vert));
                recevoirProduitNonTextView.setTextColor(Color.GRAY);
                dotationQteALivrerLinearLayout.setVisibility(View.VISIBLE);
            }
            else{
                recevoirProduitNonTextView.setTextColor(getResources().getColor(R.color.rouge));
                recevoirProduitOuiTextView.setTextColor(Color.GRAY);
                dotationQteALivrerLinearLayout.setVisibility(View.INVISIBLE);
            }
            recevoirProduit = padPropositionLigneSelectionner.recevoirProduit;
            choisirQteProduit = padPropositionLigneSelectionner.choisirQteProduit;
            dotationQteALivrerEditText.setText(String.valueOf(padPropositionLigneSelectionner.qteALivrer));
        }



        LinearLayout fermerAlerteLinearLayout = (LinearLayout) view.findViewById(R.id.fermerAlerteLinearLayout);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        fermerAlerteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = padPropositionLigneList.indexOf(padPropositionLigneSelectionner);
                if(padPropositionLigneSelectionner.dotationID==0){
                    checkEditText();
                    padPropositionLigneList.get(position).qteCartonFerme = qteCartonFerme;
                    padPropositionLigneList.get(position).qteUnite = qteUnite;
                }
                else{
                    Dotation_Patient dotation_patient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, padPropositionLigneList.get(position).dotationID);
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

                    padPropositionLigneList.get(position).recevoirProduit = recevoirProduit;
                    padPropositionLigneList.get(position).choisirQteProduit = choisirQteProduit;
                    padPropositionLigneList.get(position).qteALivrer = qteALivrer;
                }

                alertDialog.dismiss();
                onResume();
            }
        });
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

    protected void onClick_recevoirProduitOui(View v){
        recevoirProduitOuiTextView.setTextColor(getResources().getColor(R.color.vert));
        recevoirProduitNonTextView.setTextColor(Color.GRAY);
        dotationQteALivrerLinearLayout.setVisibility(View.VISIBLE);

        recevoirProduit = true;
        choisirQteProduit = false;

        int dotationID = padPropositionLigneSelectionner.dotationID;
        Dotation_Patient dotationPatient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, dotationID);

        dotationQteALivrerEditText.setText(String.valueOf(Math.round(dotationPatient.getQté())));
    }

    protected void onClick_recevoirProduitNon(View v){
        recevoirProduitNonTextView.setTextColor(getResources().getColor(R.color.rouge));
        recevoirProduitOuiTextView.setTextColor(Color.GRAY);
        dotationQteALivrerLinearLayout.setVisibility(View.INVISIBLE);

        recevoirProduit = false;
        choisirQteProduit = false;

        int dotationID = padPropositionLigneSelectionner.dotationID;
        Dotation_Patient dotationPatient = Dotation_PatientOpenHelper.getDotation_PatientByID(db, dotationID);

        dotationQteALivrerEditText.setText(String.valueOf(Math.round(dotationPatient.getQté())));
    }

    public void onClick_EnvoyerButton(View v) {
        showAlertePatientez();

        boolean envoieCommande = Alerte.afficherAlerte(DetailPADActivity.this, "Attention", "Êtes-vous sûr de vouloir envoyer cette commande ?", "OuiNon");

        if (envoieCommande) {
            servicePADMethodes.elementASynchroniser_inserer(utilisateurConnecte, serviceActuel);
            gestionnaireElementASynchroniser.toutSynchroniser(DetailPADActivity.this, db, utilisateurConnecte, true);
            updateAlertePatientez();
        } else {
            alertePatientezDialog.dismiss();
        }
    }
}

