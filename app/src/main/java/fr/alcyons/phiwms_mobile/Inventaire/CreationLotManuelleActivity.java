package fr.alcyons.phiwms_mobile.Inventaire;
import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerInventaireActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class CreationLotManuelleActivity  extends ServiceAvecConnexionActivity {
    Produit produitSelectionne;
    TextView zoneTextView;
    TextView emplacementTextView;
    TextView numSerieEditText;
    TextView lotEditText;
    TextView fournisseurTextView;
    TextView qteActuelleEditText;
    ImageView datamatrix1ImageView;
    ImageView datamatrix2ImageView;
    TextView numPreparation;
    TextView referenceProduit;
    LinearLayout validationScan;
    ImageView imageValidation;
    PackageManager pm;
    Spinner spinnerMoisDatePeremption_SP;
    Spinner spinnerAnneeDatePeremption_SP;
    String zonecourante_VS;
    String emplacementcourant_VS;
    LinearLayout layoutMoins_LL;
    LinearLayout layoutPlus_LL;
    LinearLayout layoutCartonFermer_LL;
    LinearLayout layoutCartonOuvert_LL;
    TextView textCartonFermer_TV;
    Inventaire inventaire;
    Inventaire_Ligne_Temp inventaireLigneTempCourant;
    int conditionnement;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_lot_manuelle);

        //gestion du package manager
        pm = CreationLotManuelleActivity.this.getPackageManager();

        // Récupération des variables globales
        produitSelectionne = ProduitOpenHelper.getProduitByID(db, Objects.requireNonNull(intent.getExtras()).getInt("produitCourantId"));
        zonecourante_VS = intent.getExtras().getString("zoneCourante");
        emplacementcourant_VS = intent.getExtras().getString("emplacementDefaut");
        inventaire = InventaireOpenHelper.getInventaireById(db, intent.getExtras().getInt("inventaireId"));
        inventaireLigneTempCourant = Inventaire_Ligne_TempOpenHelper.getInventaireLigneTempByphiwms_mobileUUID(db, intent.getExtras().getInt("inventaireLigneTempId"));

        // Récupération des objets graphiques
        zoneTextView = (TextView) findViewById(R.id.zoneName);
        emplacementTextView = (TextView) findViewById(R.id.nomEmplacement);
        numPreparation = (TextView) findViewById(R.id.numPreparation);
        referenceProduit = (TextView) findViewById(R.id.referenceProduit);
        fournisseurTextView = (TextView) findViewById(R.id.depotPreparation);
        lotEditText = (TextView) findViewById(R.id.numLot);
        numSerieEditText = (TextView) findViewById(R.id.numSerie);
        textCartonFermer_TV = (TextView) findViewById(R.id.textCartonFermer_TV);
        qteActuelleEditText = (TextView) findViewById(R.id.qteActuelle);
        datamatrix1ImageView = (ImageView) findViewById(R.id.datamatrix1);
        datamatrix2ImageView = (ImageView) findViewById(R.id.datamatrix2);
        imageValidation = (ImageView) findViewById(R.id.imageValidation);
        validationScan = (LinearLayout) findViewById(R.id.validationScan);
        layoutPlus_LL = (LinearLayout) findViewById(R.id.layoutPlus_LL);
        layoutMoins_LL = (LinearLayout) findViewById(R.id.layoutMoins_LL);
        layoutCartonFermer_LL = (LinearLayout) findViewById(R.id.layoutCartonFermer_LL);
        layoutCartonOuvert_LL = (LinearLayout) findViewById(R.id.layoutCartonOuvert_LL);
        spinnerMoisDatePeremption_SP = (Spinner) findViewById(R.id.selecteurDateMois_SP);
        spinnerAnneeDatePeremption_SP = findViewById(R.id.selecteurDateAnnee_SP);

        ((LinearLayout) findViewById(R.id.layoutNumSerie)).setVisibility(View.GONE);
        numPreparation.setText("#"+inventaire.getInventaire_ID()+" - "+inventaire.getObjet());
        fournisseurTextView.setText(zonecourante_VS);

        ArrayAdapter<String> adapterMoisPeremption = new ArrayAdapter<>(CreationLotManuelleActivity.this, R.layout.spinner_date_item, getListeMoisDatePicker());
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoisDatePeremption_SP.setAdapter(adapterMoisPeremption);

        ArrayAdapter<String> adapterAnneePeremption = new ArrayAdapter<>(CreationLotManuelleActivity.this, R.layout.spinner_date_item, getListeAnneeDatePicker());
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnneeDatePeremption_SP.setAdapter(adapterAnneePeremption);
        spinnerAnneeDatePeremption_SP.setSelection(3);

        //on n'affiche pas le datamatrix des zones
        datamatrix1ImageView.setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.linearDatamatrix1)).setVisibility(View.GONE);

        //clic sur le datamtrix permettant de récupérer le numéro de lot et la date de péremption
        datamatrix2ImageView.setOnClickListener(view -> {

            if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                Bundle detailProduitPlanDePlacementBundle = CreationLotManuelleActivity.super.getBundle();
                detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", false);
                detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelleActivity.this, ScannerProduitActivity.class);
                detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                CreationLotManuelleActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelleActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelleActivity.this, BarcodeCaptureActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelleActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
                else
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelleActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelleActivity.this, ScannerProduitActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelleActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
            }

        });

        if(!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            datamatrix2ImageView.setVisibility(View.GONE);
        }

        // Hydratation des objets graphiques
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne());
        referenceProduit.setText(produitSelectionne.getRef_fourni());
        zoneTextView.setText(zonecourante_VS);
        emplacementTextView.setText(emplacementcourant_VS);
        String numLot = intent.getExtras().getString("numLot");
        if (numLot != null) {
            lotEditText.setText(numLot);
        }

        String numSerie = intent.getExtras().getString("numSerie");
        if(numSerie != null)
        {
            String last_char = numSerie.substring(numSerie.length()-1);
            if(last_char.contentEquals("@"))
                numSerie = numSerie.substring(0, numSerie.length()-1);
            numSerieEditText.setText(numSerie);
        }

        // Transformation d'une date au format yyyy-MM-dd à dd/MM/yyyyy
        String dateDePeremption = intent.getExtras().getString("datePeremption");
        if (dateDePeremption != null) {
            @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String dateAAfficher = "";
            Date date;

            try {
                date = dateDecodeur.parse(dateDePeremption);
                assert date != null;
                dateAAfficher = dateFormat.format(date);
            } catch (ParseException e) {
                Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
            }

            String[] tabPeremption = dateAAfficher.split("-");
            String annee = tabPeremption[0];
            String mois = tabPeremption[1];
            int anneeCourante = getCurrentYear();
            int anneeMin = anneeCourante - 2;
            int anneeSelectionnee = Integer.parseInt(annee); // ex : 2027

            spinnerMoisDatePeremption_SP.setSelection(Integer.parseInt(mois) - 1);
            spinnerAnneeDatePeremption_SP.setSelection(anneeSelectionnee - anneeMin);
        }

        lotEditText.setOnClickListener(view -> {
            String lotnumero = Alerte.afficherAlerteEditText(CreationLotManuelleActivity.this, "Numéro de lot", "Saisir le numéro de lot");
            lotEditText.setText(lotnumero);
            apparitionValider();
        });

        //affichage de la quantité de base
        qteActuelleEditText.setText(String.valueOf(0));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CreationLotManuelleActivity.this.finish();
            }
        });

        //gestion du plus et mois
        conditionnement = produitSelectionne.getCond_achat();
        textCartonFermer_TV.setText("Carton fermer (x"+String.valueOf(produitSelectionne.getCond_achat())+")");

        layoutCartonFermer_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement = produitSelectionne.getCond_achat();
                layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreationLotManuelleActivity.this, R.color.blanc)));
                layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreationLotManuelleActivity.this, R.color.vertTransparent)));
            }
        });

        layoutCartonOuvert_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conditionnement = 1;
                layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreationLotManuelleActivity.this, R.color.vertTransparent)));
                layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(CreationLotManuelleActivity.this, R.color.blanc)));
            }
        });

        layoutPlus_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteCourante = Integer.parseInt(qteActuelleEditText.getText().toString());
                qteCourante += conditionnement;
                qteActuelleEditText.setText(String.valueOf(qteCourante));
                apparitionValider();
            }
        });

        layoutMoins_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qteCourante = Integer.parseInt(qteActuelleEditText.getText().toString());
                qteCourante -= conditionnement;
                if(qteCourante < 0)
                    qteCourante = 0;
                qteActuelleEditText.setText(String.valueOf(qteCourante));
                apparitionValider();
            }
        });

        apparitionValider();
        imageValidation.setOnClickListener(view -> onMenuSaveClick());
    }

    @NonNull
    private Intent getProduitPlanDePlacementIntent() {
        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelleActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelleActivity.this, ScannerEmplacementActivity.class);
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        return detailProduitPlanDePlacementIntent;
    }

    @NonNull
    private Intent getDetailProduitPlanDePlacementIntent() {
        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelleActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelleActivity.this, ScannerEmplacementActivity.class);
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        return detailProduitPlanDePlacementIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    private void onMenuSaveClick() {
        if (zoneTextView.getText().toString().trim().isEmpty() || emplacementTextView.getText().toString().trim().isEmpty() || lotEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreationLotManuelleActivity.this, "Tous les éléments n'ont pas été saisis.", Toast.LENGTH_SHORT).show();
        } else {
            //création de l'inventaire ligne temp
            Inventaire_Ligne_Temp nouvelInventaireLigneTemp = new Inventaire_Ligne_Temp(inventaireLigneTempCourant);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateDuJour = sdf.format(new Date());
            Random randominventairelignetemp = new Random();
            int inventairelignetempid = randominventairelignetemp.nextInt();
            if(inventairelignetempid > 0)
                inventairelignetempid= inventairelignetempid*-1;

            nouvelInventaireLigneTemp.set_UID(inventairelignetempid);
            nouvelInventaireLigneTemp.setInventaireDate(dateDuJour);
            nouvelInventaireLigneTemp.setStockPhysique(Integer.parseInt(qteActuelleEditText.getText().toString().trim()));
            nouvelInventaireLigneTemp.setLot(lotEditText.getText().toString().trim());
            nouvelInventaireLigneTemp.setSynchroniser(false);
            String anneeSelection = spinnerAnneeDatePeremption_SP.getSelectedItem().toString();
            String moisSelection = spinnerMoisDatePeremption_SP.getSelectedItem().toString();
            String dateExpirationLotTemp = getDateDepuisMoisAnnee(moisSelection, anneeSelection);
            String[] dateParts = dateExpirationLotTemp.split("/");
            if(dateParts.length == 3)
                dateExpirationLotTemp = dateParts[2]+"-"+dateParts[1]+"-"+dateParts[0];

            nouvelInventaireLigneTemp.setPeremptionDate(dateExpirationLotTemp);

            nouvelInventaireLigneTemp.setEmplacement(emplacementTextView.getText().toString().trim());

            Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, nouvelInventaireLigneTemp);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, nouvelInventaireLigneTemp.getPhiMR4UUID(), nouvelInventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
            ElementASynchroniserOpenHelper.toutSynchroniser(CreationLotManuelleActivity.this, db, utilisateurConnecte, false);

            Bundle onMenuSaveClick_Bundle = CreationLotManuelleActivity.super.getBundle();
            Intent onMenuSaveClick_Intent = new Intent();
            onMenuSaveClick_Intent.putExtras(onMenuSaveClick_Bundle);
            CreationLotManuelleActivity.this.setResult(RETOUR_LOT, onMenuSaveClick_Intent);
            CreationLotManuelleActivity.this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RETOUR_LOT:
                    String code = Objects.requireNonNull(data.getExtras()).getString("code");
                    String numLot = data.getExtras().getString("numLot");
                    String datePeremptionScanner = data.getExtras().getString("datePeremptionSqlFormat");
                    if(code != null)
                    {
                        if(numLot != null)
                        {
                            lotEditText.setText(numLot);
                        }

                        if(datePeremptionScanner != null)
                        {
                            String[] tabPeremption = datePeremptionScanner.split("-");
                            String annee = tabPeremption[0];
                            String mois = tabPeremption[1];
                            int anneeCourante = getCurrentYear();
                            int anneeMin = anneeCourante - 2;
                            int anneeSelectionnee = Integer.parseInt(annee); // ex : 2027

                            spinnerMoisDatePeremption_SP.setSelection(Integer.parseInt(mois) - 1);
                            spinnerAnneeDatePeremption_SP.setSelection(anneeSelectionnee - anneeMin);
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    public void apparitionValider()
    {
        if(!lotEditText.getText().toString().contentEquals("") && !qteActuelleEditText.getText().toString().contentEquals("0"))
        {
            validationScan.setVisibility(View.VISIBLE);
            blinkImage();
        }
        else
        {
            validationScan.setVisibility(View.GONE);
        }
    }

    private void blinkImage() {
        // set its background to our AnimationDrawable XML resource.
        imageValidation.setBackgroundResource(R.drawable.animation_blinking);

        /*
         * Get the background, which has been compiled to an AnimationDrawable
         * object.
         */
        AnimationDrawable frameAnimation = (AnimationDrawable) imageValidation
                .getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    // Class static permettant de faire apparaitre le DatePicker du téléphone
    public static class DatePickerFragmentReception extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TextView datePeremption;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR)+1;
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            int month = c.get(Calendar.MONTH);
            int day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            month++;
            String mois = "";
            if (month < 10) {
                mois += "0";
            }
            mois += String.valueOf(month);
            String date = day + "/" + mois + "/" + year;
            Date datedate = null;
            try {
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                datedate = dateFormat.parse(date);

            } catch (ParseException e) {
                Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
            }
            datePeremption.setText(date);
            setDatePeremptionColor(datedate);
            ((CreationLotManuelleActivity) requireActivity()).apparitionValider();

        }

        private void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(requireContext().getResources().getColor(R.color.rouge2, null));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(requireContext().getResources().getColor(R.color.orange2, null));
                } else {
                    datePeremption.setTextColor(requireContext().getResources().getColor(R.color.vert, null));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }
        }

        public void setTextView(TextView editText) {
            datePeremption = editText;
        }
    }

    public void afficherSnackBar() {
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Mauvais produit scanné</b>", 0), Snackbar.LENGTH_LONG);

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
