package fr.alcyons.phiwms_mobile.Reception;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RESULT_ZONE;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

public class CreationLotManuelReceptionActivity extends ServiceActivity {
    Produit produitSelectionne;
    Depot depotSelectionne;
    Depot_Zone zoneSelectionner;
    Depot_Emplacement emplacementSelectionner;
    List<Depot_Emplacement> emplacementList;
    List<Depot_Zone> depotZoneList;
    TextView zoneTextView;
    TextView emplacementTextView;
    TextView numSerieEditText;
    TextView lotEditText;
    TextView datePeremptionTextView;
    TextView fournisseurTextView;
    TextView qteActuelleEditText;
    ImageView datamatrix1ImageView;
    ImageView datamatrix2ImageView;
    TextView labelSerie;
    TextView numPreparation;
    TextView referenceProduit;
    LinearLayout validationScan;
    ImageView imageValidation;
    RelativeLayout relativeQte;
    Commande commandecourante;
    PH_Reliquat reliquat_courant;
    PH_Reliquat_Reception_Adapte phReliquatReceptionAdapte;
    int qte_commande;
    int qte_receptionne;
    int qte_restante;
    PackageManager pm;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_lot_preparation);

        //gestion du package manager
        pm = CreationLotManuelReceptionActivity.this.getPackageManager();

        // Récupération des variables globales
        produitSelectionne = ProduitOpenHelper.getProduitByID(db, Objects.requireNonNull(intent.getExtras()).getInt("produitID"));
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotID"));
        reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, intent.getExtras().getInt("ReliquatID"));
        phReliquatReceptionAdapte = (PH_Reliquat_Reception_Adapte) intent.getExtras().getSerializable("phReliquatReceptionAdapte");
        commandecourante = CommandeOpenHelper.getCommandeByNumero(db, reliquat_courant.getCommandeNumero());

        /**
         * Gestion quantité restant à réceptionner
         */
        qte_commande = reliquat_courant.getQteCommande() - reliquat_courant.getQteLivraison();

        List<PH_Reliquat> listeReliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, reliquat_courant.getcommandeNumero(), reliquat_courant.getProduitID());
        for(PH_Reliquat reliquat : listeReliquatReceptionne)
        {
            qte_receptionne = qte_receptionne + reliquat.getQteLivraison();
        }
        qte_restante = qte_commande - qte_receptionne;

        // Récupération des objets graphiques
        zoneTextView = (TextView) findViewById(R.id.zoneName);
        emplacementTextView = (TextView) findViewById(R.id.nomEmplacement);
        labelSerie = (TextView) findViewById(R.id.labelSerie);
        numPreparation = (TextView) findViewById(R.id.numPreparation);
        referenceProduit = (TextView) findViewById(R.id.referenceProduit);
        fournisseurTextView = (TextView) findViewById(R.id.depotPreparation);
        lotEditText = (TextView) findViewById(R.id.numLot);
        numSerieEditText = (TextView) findViewById(R.id.numSerie);
        datePeremptionTextView = (TextView) findViewById(R.id.datePeremption);
        qteActuelleEditText = (TextView) findViewById(R.id.qteActuelle);
        datamatrix1ImageView = (ImageView) findViewById(R.id.datamatrix1);
        datamatrix2ImageView = (ImageView) findViewById(R.id.datamatrix2);
        imageValidation = (ImageView) findViewById(R.id.imageValidation);
        validationScan = (LinearLayout) findViewById(R.id.validationScan);
        relativeQte = (RelativeLayout) findViewById(R.id.relativeQte);

        //gestion du produit non tracé
        if(!produitSelectionne.isSuivi_Lot())
        {
            //gestion de la date
            String currentDate = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date());

            lotEditText.setText("Phi"+currentDate);
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            int currentYear = calendar.get(Calendar.YEAR)+1;
            int currentMonth = calendar.get(Calendar.MONTH)+1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            String dateNextYear = currentDay+"/"+currentMonth+"/"+currentYear;

            datePeremptionTextView.setText(dateNextYear);
        }

        //affichage des informations en barre de titre
        numPreparation.setText("#"+commandecourante.getNumero());
        fournisseurTextView.setText(commandecourante.getFournisseur());

        // Définition des actions sur Click
        datePeremptionTextView.setOnClickListener(v -> {
            DatePickerFragmentReception newFragment = new DatePickerFragmentReception();
            newFragment.setTextView(datePeremptionTextView);
            newFragment.show((CreationLotManuelReceptionActivity.this).getSupportFragmentManager(), "timePicker");
        });

        if(!commandecourante.getRef_Depot_Dest().contains("-PAD"))
        {
            //affichage de la liste des zones
            zoneTextView.setOnClickListener(view -> {
                depotZoneList = new ArrayList<>();
                depotZoneList = ZoneOpenHelper.getZonesEtEmplacementsParDepot(db, depotSelectionne);

                if (!depotZoneList.isEmpty()) {
                    Intent newIntent = new Intent(CreationLotManuelReceptionActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = CreationLotManuelReceptionActivity.super.getBundle();
                    extras.putInt("depotID", depotSelectionne.getDepot_UID());
                    newIntent.putExtras(extras);
                    CreationLotManuelReceptionActivity.this.startActivityForResult(newIntent, RESULT_ZONE);
                }

            });

            //affichage des emplacements
            emplacementTextView.setOnClickListener(view -> {
                emplacementList = new ArrayList<>();
                if (zoneSelectionner != null) {
                    emplacementList = EmplacementOpenHelper.getEmplacementsParZone(db, zoneSelectionner);
                }
                if (!emplacementList.isEmpty()) {
                    Intent newIntent = new Intent(CreationLotManuelReceptionActivity.this, ListeEmplacementCreationActivity.class);
                    Bundle extras = CreationLotManuelReceptionActivity.super.getBundle();
                    extras.putInt("zoneid", zoneSelectionner.getZoneID());
                    newIntent.putExtras(extras);
                    CreationLotManuelReceptionActivity.this.startActivityForResult(newIntent, RETOUR_CODE_EMPLACEMENT);
                }

            });

            //clic sur le datamatrix de la zone et de l'emplacement
            datamatrix1ImageView.setOnClickListener(view -> {
                if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Intent detailProduitPlanDePlacementIntent = getDetailProduitPlanDePlacementIntent();
                    CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, BarcodeCaptureActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        Intent detailProduitPlanDePlacementIntent = getProduitPlanDePlacementIntent();
                        CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                }
            });

            if (depotSelectionne.getStructure().contains("PAD")) {
                zoneTextView.setText(produitSelectionne.getZone_PAD_Defaut());
                emplacementTextView.setText(produitSelectionne.getEmplacement_PAD_Defaut());
                zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_PAD_Defaut());
            } else if (depotSelectionne.getStructure().contains("PUF")) {
                zoneTextView.setText(produitSelectionne.getZone_UF_Defaut());
                emplacementTextView.setText(produitSelectionne.getEmplacement_UF_Defaut());
                zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_UF_Defaut());
            } else {
                zoneTextView.setText(produitSelectionne.getZone_PUI_Defaut());
                emplacementTextView.setText(produitSelectionne.getEmplacement_PUI_Defaut());
                zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_PUI_Defaut());
            }
        }
        else
        {
            ((LinearLayout) findViewById(R.id.zonePlacementLot)).setVisibility(View.GONE);
            zoneTextView.setText("RECEPTION");
            emplacementTextView.setText("RECEPTION-" + commandecourante.getNumero() + "-" + commandecourante.getPatient_identite());
        }

        //clic sur le datamtrix permettant de récupérer le numéro de lot et la date de péremption
        datamatrix2ImageView.setOnClickListener(view -> {
            if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
                detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", false);
                detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, ScannerProduitActivity.class);
                detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, BarcodeCaptureActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
                else
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, ScannerProduitActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelReceptionActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
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
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String dateAAfficher = "";
            Date date;

            try {
                date = dateDecodeur.parse(dateDePeremption);
                assert date != null;
                dateAAfficher = dateFormat.format(date);
            } catch (ParseException e) {
                Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
            }
            datePeremptionTextView.setText(dateAAfficher);
        }

        if(!produitSelectionne.isSuivi_Serialisation())
        {
            numSerieEditText.setVisibility(View.GONE);
            labelSerie.setVisibility(View.GONE);
        }

        lotEditText.setOnClickListener(view -> {
            String lotnumero = Alerte.afficherAlerteEditText(CreationLotManuelReceptionActivity.this, "Numéro de lot", "Saisir le numéro de lot");
            lotEditText.setText(lotnumero);
            apparitionValider();
        });

        //affichage de la quantité de base
        qteActuelleEditText.setText(String.valueOf(qte_restante));


        relativeQte.setOnClickListener(view -> {
            String title = produitSelectionne.getDesignation_interne();
            String message = "Choisir une quantité: ";
            int maxValue = qte_restante;
            int value = qte_restante;
            int conditionnement = (int)produitSelectionne.getCond_Achat_Gros_volume();

            if(conditionnement == 0 || conditionnement > qte_restante)
                conditionnement = (int)produitSelectionne.getCond_achat();

            if(conditionnement == 0 || conditionnement > qte_restante)
                conditionnement = 1;

            int finalConditionnement = conditionnement;
            DialogInterface.OnClickListener onClickListener = (dialog, id) -> {

                int qteApres = aNumberPicker.getValue()* finalConditionnement;
                qteActuelleEditText.setText(String.valueOf(qteApres).trim());
                dialog.dismiss();
                apparitionValider();
            };

            Alerte.afficherAlerteNumberPickerAvecPas(CreationLotManuelReceptionActivity.this, title, message, value, maxValue, onClickListener, conditionnement);
        });

        //on affiche des valeurs fictive si c'est alcyons qui est connecté
        if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons"))
        {
            lotEditText.setText("LotAclyons");
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            int currentYear = calendar.get(Calendar.YEAR)+1;
            int currentMonth = calendar.get(Calendar.MONTH)+1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            String dateNextYear = currentDay+"/"+currentMonth+"/"+currentYear;

            datePeremptionTextView.setText(dateNextYear);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                CreationLotManuelReceptionActivity.this.finish();
            }
        });

        apparitionValider();
        imageValidation.setOnClickListener(view -> onMenuSaveClick());
    }

    @NonNull
    private Intent getProduitPlanDePlacementIntent() {
        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, ScannerEmplacementActivity.class);
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        return detailProduitPlanDePlacementIntent;
    }

    @NonNull
    private Intent getDetailProduitPlanDePlacementIntent() {
        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionActivity.this, ScannerEmplacementActivity.class);
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
        if (zoneTextView.getText().toString().trim().isEmpty() || emplacementTextView.getText().toString().trim().isEmpty() || lotEditText.getText().toString().trim().isEmpty() || datePeremptionTextView.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreationLotManuelReceptionActivity.this, "Tous les éléments n'ont pas été saisis.", Toast.LENGTH_SHORT).show();
        } else {
            Bundle onMenuSaveClick_Bundle = CreationLotManuelReceptionActivity.super.getBundle();
            onMenuSaveClick_Bundle.putString("nomZone", zoneTextView.getText().toString());
            onMenuSaveClick_Bundle.putString("nomEmplacement", emplacementTextView.getText().toString());
            onMenuSaveClick_Bundle.putString("numLot", lotEditText.getText().toString());
            onMenuSaveClick_Bundle.putString("numSerie", numSerieEditText.getText().toString());
            if(!qteActuelleEditText.getText().toString().trim().isEmpty()){
                onMenuSaveClick_Bundle.putInt("qteActuelle", Integer.parseInt(qteActuelleEditText.getText().toString().trim()));
            }

            @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date;
            String dateARetourner = "";

            try {
                date = dateDecodeur.parse(datePeremptionTextView.getText().toString().trim());
                assert date != null;
                dateARetourner = dateFormat.format(date);
            } catch (ParseException e) {
                Log.e("ParseException", Objects.requireNonNull(e.getMessage()));
            }

            onMenuSaveClick_Bundle.putString("datePeremption", dateARetourner);

            Intent onMenuSaveClick_Intent = new Intent();
            onMenuSaveClick_Intent.putExtras(onMenuSaveClick_Bundle);

            CreationLotManuelReceptionActivity.this.setResult(RETOUR_LOT, onMenuSaveClick_Intent);
            CreationLotManuelReceptionActivity.this.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RESULT_ZONE:
                    int zoneid = Objects.requireNonNull(data.getExtras()).getInt("zoneid");
                    if(zoneid != -1)
                    {
                        zoneSelectionner = ZoneOpenHelper.getUneZoneByID(db, zoneid);
                        zoneTextView.setText(zoneSelectionner.getZoneName().trim());
                        emplacementTextView.performClick();
                    }
                    break;
                case RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = Objects.requireNonNull(data.getExtras()).getInt("emplacementId");
                    if(emplacementid != -1)
                    {
                        emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                        emplacementTextView.setText(emplacementSelectionner.getAdressage().trim());
                    }
                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Depot_Emplacement depotEmplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Objects.requireNonNull(data.getExtras()).getInt("emplacementSelectionneID"));
                    String code_emplacement = data.getStringExtra("code");
                    if(code_emplacement != null && !code_emplacement.contentEquals(""))
                    {
                        if(depotEmplacement == null)
                        {
                            if (code_emplacement.startsWith("PHITAGPLACE")) {
                                String[] tabchaine = code_emplacement.split(":");
                                code_emplacement = tabchaine[1];
                                depotEmplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(code_emplacement));
                            }
                        }
                        if(depotEmplacement != null)
                        {
                            Depot_Zone depotZone = ZoneOpenHelper.getUneZoneByID(db, depotEmplacement.getZoneID());
                            if(depotZone != null){
                                zoneTextView.setText(depotZone.getZoneName().trim());
                                emplacementTextView.setText(depotEmplacement.getAdressage().trim());
                            }
                        }
                        else
                        {
                            zoneTextView.performClick();
                        }
                    }
                    else
                    {
                        zoneTextView.performClick();
                    }
                    break;

                case RETOUR_LOT:
                    String code = Objects.requireNonNull(data.getExtras()).getString("code");
                    String numLot = data.getExtras().getString("numLot");
                    String datePeremptionScanner = data.getExtras().getString("datePeremption");
                    if(code != null)
                    {
                        if(numLot == null || numLot.contentEquals(""))
                        {
                            Map<String, String> DecoupeMap = OutilsDecodage.decouperGTIN(code);
                            if(DecoupeMap.size()>1)
                            {
                                Produit produitScanne = null;
                                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, DecoupeMap.get(OutilsDecodage.codeGtin));

                                if(produits.size() == 0)
                                    produits = ProduitOpenHelper.getProduitsParGTIN(db, DecoupeMap.get(OutilsDecodage.codeGtinSansAi));

                                if (produits.size() == 1) {
                                    produitScanne = produits.get(0);
                                }

                                if(produitScanne == null || produitScanne.getID_produit() != produitSelectionne.getID_produit())
                                {
                                    numLot = null;
                                    datePeremptionScanner = null;
                                    lotEditText.setText("");
                                    datePeremptionTextView.setText("");
                                    afficherSnackBar();
                                }
                                else
                                {
                                    numLot = DecoupeMap.get(OutilsDecodage.numeroLot);
                                    datePeremptionScanner = DecoupeMap.get(OutilsDecodage.dateDePeremption);
                                    if(datePeremptionScanner != null)
                                    {
                                        String[] tab_date = datePeremptionScanner.split("-");
                                        if(tab_date.length == 3)
                                        {
                                            datePeremptionScanner = tab_date[2]+"/"+tab_date[1]+"/"+tab_date[0];
                                        }
                                    }
                                }
                            }
                        }
                        if(numLot != null)
                        {
                            lotEditText.setText(numLot);
                        }

                        if(datePeremptionScanner != null)
                        {
                            datePeremptionTextView.setText(datePeremptionScanner);
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    public void apparitionValider()
    {
        if(!lotEditText.getText().toString().contentEquals("") && !datePeremptionTextView.getText().toString().contentEquals("") && !qteActuelleEditText.getText().toString().contentEquals("0"))
        {
            validationScan.setVisibility(View.VISIBLE);
            blinkImage();
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
            ((CreationLotManuelReceptionActivity) requireActivity()).apparitionValider();

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
