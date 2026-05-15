package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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
import java.util.Random;
import java.util.TimeZone;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class CreationLotControleDesRetoursActivity extends ServiceActivity
{
    private Produit produitSelectionne = null;
    private Depot depotSelectionne = null;
    private Depot depotPUI = null;

    private Depot_Zone zoneSelectionner = null;
    private Depot_Emplacement emplacementSelectionner = null;
    private List<Depot_Emplacement> emplacementList = null;
    private List<Depot_Zone> depotZoneList = null;

    private TextView zoneTextView = null;
    private TextView emplacementTextView = null;
    private TextView numSerieEditText = null;
    private TextView lotEditText = null;
    private TextView datePeremptionTextView = null;
    private TextView fournisseurTextView = null;
    private TextView qteActuelleEditText = null;
    private ImageView datamatrix1ImageView = null;
    private ImageView datamatrix2ImageView = null;
    private TextView labelSerie = null;
    private TextView numPreparation = null;
    private TextView referenceProduit = null;
    private LinearLayout validationScan = null;
    private ImageView imageValidation = null;
    private RelativeLayout relativeQte = null;

    private int qte_a_retourner = 0;
    private int qte_retourner = 0;
    private int qte_restante = 0;
    private PackageManager pm = null;

    private Retour retourSelectionne = null;
    private Retour_Ligne retourLigneCourant = null;

    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_creation_lot_preparation);

        //gestion du package manager
        this.pm = CreationLotControleDesRetoursActivity.this.getPackageManager();

        // Récupération des variables globales
        this.produitSelectionne = ProduitOpenHelper.getProduitByID(this.db, Objects.requireNonNull(this.intent.getExtras()).getInt("produitID"));
        this.depotSelectionne = DepotOpenHelper.getDepotParID(this.db, this.intent.getExtras().getInt("depotID"));
        this.retourLigneCourant = Retour_LigneOpenHelper.getRetourLigneByID(this.db, this.intent.getExtras().getInt("retourLigneID"));
        this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, this.intent.getExtras().getInt("retourUID"));
        this.depotPUI = DepotOpenHelper.getDepotPUI(this.db);

        /**
         * Gestion quantité restant à retourner
         */
        this.qte_a_retourner = (int) this.retourLigneCourant.getQte_Demander();
        this.qte_retourner = 0;
        final List<Retour_Ligne> listeRetourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne, this.retourLigneCourant.getCode_produit());
        for(final Retour_Ligne retourLigne : listeRetourLigneRetourner) { this.qte_retourner = (int) ((double) this.qte_retourner + retourLigne.getQte_Retourner()); }
        this.qte_restante = this.qte_a_retourner - this.qte_retourner;

        // Récupération des objets graphiques
        this.zoneTextView = this.findViewById(R.id.zoneName);
        this.emplacementTextView = this.findViewById(R.id.nomEmplacement);
        this.labelSerie = this.findViewById(R.id.labelSerie);
        this.numPreparation = this.findViewById(R.id.numPreparation);
        this.referenceProduit = this.findViewById(R.id.referenceProduit);
        this.fournisseurTextView = this.findViewById(R.id.depotPreparation);
        this.lotEditText = this.findViewById(R.id.numLot);
        this.numSerieEditText = this.findViewById(R.id.numSerie);
        this.datePeremptionTextView = this.findViewById(R.id.datePeremption);
        this.qteActuelleEditText = this.findViewById(R.id.qteActuelle);
        this.datamatrix1ImageView = this.findViewById(R.id.datamatrix1);
        this.datamatrix2ImageView = this.findViewById(R.id.datamatrix2);
        this.imageValidation = this.findViewById(R.id.imageValidation);
        this.validationScan = this.findViewById(R.id.validationScan);
        this.relativeQte = this.findViewById(R.id.relativeQte);

        //gestion du produit non tracé
        if(!this.produitSelectionne.isSuivi_Lot())
        {
            //gestion de la date
            final String currentDate = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date());

            this.lotEditText.setText("Phi"+currentDate);
            final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            final int currentYear = calendar.get(Calendar.YEAR)+1;
            final int currentMonth = calendar.get(Calendar.MONTH)+1;
            final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            final String dateNextYear = currentDay+"/"+currentMonth+"/"+currentYear;

            this.datePeremptionTextView.setText(dateNextYear);
        }

        //affichage des informations en barre de titre
        this.numPreparation.setText("#"+ this.retourSelectionne.getNumero());
        this.fournisseurTextView.setText(this.retourSelectionne.getRef_Depot_Origine());

        // Définition des actions sur Click
        this.datePeremptionTextView.setOnClickListener(v -> {
            final CreationLotControleDesRetoursActivity.DatePickerFragmentReception newFragment = new CreationLotControleDesRetoursActivity.DatePickerFragmentReception();
            newFragment.setTextView(this.datePeremptionTextView);
            newFragment.show((CreationLotControleDesRetoursActivity.this).getSupportFragmentManager(), "timePicker");
        });

        //affichage de la liste des zones
        this.zoneTextView.setOnClickListener(view -> {
            this.depotZoneList = new ArrayList<>();
            this.depotZoneList = ZoneOpenHelper.getZonesEtEmplacementsParDepot(this.db, this.depotSelectionne);

            if (!this.depotZoneList.isEmpty())
            {
                final Intent newIntent = new Intent(CreationLotControleDesRetoursActivity.this, ListeZoneCreationActivity.class);
                final Bundle extras = CreationLotControleDesRetoursActivity.super.getBundle();
                extras.putInt("depotID", this.depotSelectionne.getDepot_UID());
                newIntent.putExtras(extras);
                CreationLotControleDesRetoursActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
            }

        });

        //affichage des emplacements
        this.emplacementTextView.setOnClickListener(view -> {
            this.emplacementList = new ArrayList<>();
            if (null != this.zoneSelectionner) { this.emplacementList = EmplacementOpenHelper.getEmplacementsParZone(this.db, this.zoneSelectionner); }
            if (!this.emplacementList.isEmpty())
            {
                final Intent newIntent = new Intent(CreationLotControleDesRetoursActivity.this, ListeEmplacementCreationActivity.class);
                final Bundle extras = CreationLotControleDesRetoursActivity.super.getBundle();
                extras.putInt("zoneid", this.zoneSelectionner.getZoneID());
                newIntent.putExtras(extras);
                CreationLotControleDesRetoursActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
            }

        });

        //clic sur le datamatrix de la zone et de l'emplacement
        this.datamatrix1ImageView.setOnClickListener(view -> {
            if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                final Intent detailProduitPlanDePlacementIntent = this.getDetailProduitPlanDePlacementIntent();
                CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
            }
            else
            {
                if(this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, BarcodeCaptureActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    final Intent detailProduitPlanDePlacementIntent = this.getProduitPlanDePlacementIntent();
                    CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
            }
        });

        if (this.depotSelectionne.getStructure().contains("PAD"))
        {
            this.zoneTextView.setText(this.produitSelectionne.getZone_PAD_Defaut());
            this.emplacementTextView.setText(this.produitSelectionne.getEmplacement_PAD_Defaut());
            this.zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(this.db, this.depotSelectionne, this.produitSelectionne.getZone_PAD_Defaut());
        }
        else if (this.depotSelectionne.getStructure().contains("PUF"))
        {
            this.zoneTextView.setText(this.produitSelectionne.getZone_UF_Defaut());
            this.emplacementTextView.setText(this.produitSelectionne.getEmplacement_UF_Defaut());
            this.zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(this.db, this.depotSelectionne, this.produitSelectionne.getZone_UF_Defaut());
        }
        else
        {
            this.zoneTextView.setText(this.produitSelectionne.getZone_PUI_Defaut());
            this.emplacementTextView.setText(this.produitSelectionne.getEmplacement_PUI_Defaut());
            this.zoneSelectionner = ZoneOpenHelper.getZoneByDepotEtNom(this.db, this.depotSelectionne, this.produitSelectionne.getZone_PUI_Defaut());
        }

        //clic sur le datamtrix permettant de récupérer le numéro de lot et la date de péremption
        this.datamatrix2ImageView.setOnClickListener(view -> {

            if(Build.MANUFACTURER.contains("Zebra Technologies") || Build.MANUFACTURER.toLowerCase().contains("honeywell") || Build.MANUFACTURER.toLowerCase().contains("google"))
            {
                final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
                detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", false);
                detailProduitPlanDePlacementBundle.putString("Designation", this.produitSelectionne.getDesignation_interne());
                detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                detailProduitPlanDePlacementBundle.putString("numerodocument", this.retourSelectionne.getNumero());
                detailProduitPlanDePlacementBundle.putInt("depotdestinataireid", this.depotPUI.getDepot_UID());
                detailProduitPlanDePlacementBundle.putString("depotRef", this.retourSelectionne.getRef_Depot_Origine());
                final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, ScannerProduitActivity.class);
                detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
            }
            else
            {
                if(this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                {
                    final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", this.produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, BarcodeCaptureActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
                else
                {
                    final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                    detailProduitPlanDePlacementBundle.putString("Designation", this.produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, ScannerProduitActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotControleDesRetoursActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
            }

        });

        if(!this.pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { this.datamatrix2ImageView.setVisibility(View.GONE); }

        // Hydratation des objets graphiques
        ((TextView) this.findViewById(R.id.nomProduit)).setText(this.produitSelectionne.getDesignation_interne());
        this.referenceProduit.setText(this.produitSelectionne.getRef_fourni());
        final String numLot = this.intent.getExtras().getString("numLot");
        if (null != numLot) { this.lotEditText.setText(numLot); }

        String numSerie = this.intent.getExtras().getString("numSerie");
        if(null != numSerie)
        {
            final String last_char = numSerie.substring(numSerie.length()-1);
            if(last_char.contentEquals("@")) numSerie = numSerie.substring(0, numSerie.length()-1);
            this.numSerieEditText.setText(numSerie);
        }

        // Transformation d'une date au format yyyy-MM-dd à dd/MM/yyyyy
        final String dateDePeremption = this.intent.getExtras().getString("datePeremption");
        if (null != dateDePeremption)
        {
            final DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String dateAAfficher = "";
            final Date date;

            try
            {
                date = dateDecodeur.parse(dateDePeremption);
                assert null != date;
                dateAAfficher = dateFormat.format(date);
            }
            catch (final ParseException e) { Log.e("Parse Exception", Objects.requireNonNull(e.getMessage())); }
            this.datePeremptionTextView.setText(dateAAfficher);
        }

        if(!this.produitSelectionne.isSuivi_Serialisation() || !this.produitSelectionne.isSerialiser_Reception_Delivrance())
        {
            this.numSerieEditText.setVisibility(View.GONE);
            this.labelSerie.setVisibility(View.GONE);
        }

        this.lotEditText.setOnClickListener(view -> {
            final String lotnumero = Alerte.afficherAlerteEditText(CreationLotControleDesRetoursActivity.this, "Numéro de lot", "Saisir le numéro de lot");
            this.lotEditText.setText(lotnumero);
            this.apparitionValider();
        });

        //affichage de la quantité de base
        this.qteActuelleEditText.setText(String.valueOf(this.qte_restante));


        this.relativeQte.setOnClickListener(view -> {
            final String title = this.produitSelectionne.getDesignation_interne();
            final String message = "Choisir une quantité: ";
            final int maxValue = this.qte_restante;
            final int value = this.qte_restante;
            int conditionnement = (int) this.produitSelectionne.getCond_Achat_Gros_volume();

            if(0 == conditionnement || conditionnement > this.qte_restante) conditionnement = this.produitSelectionne.getCond_achat();

            if(0 == conditionnement || conditionnement > this.qte_restante) conditionnement = 1;

            final int finalConditionnement = conditionnement;

            final DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
                final int qteApres = Alerte.aNumberPicker.getValue()* finalConditionnement;
                this.qteActuelleEditText.setText(String.valueOf(qteApres).trim());
                dialog.dismiss();
                this.apparitionValider();
            };

            Alerte.afficherAlerteNumberPickerAvecPas(CreationLotControleDesRetoursActivity.this, title, message, value, maxValue, onClickListener, conditionnement);
        });

        //on affiche des valeurs fictive si c'est alcyons qui est connecté
        if(this.utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons"))
        {
            this.lotEditText.setText("LotAclyons");
            final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            final int currentYear = calendar.get(Calendar.YEAR)+1;
            final int currentMonth = calendar.get(Calendar.MONTH)+1;
            final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            final String dateNextYear = currentDay+"/"+currentMonth+"/"+currentYear;

            this.datePeremptionTextView.setText(dateNextYear);
        }

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { CreationLotControleDesRetoursActivity.this.finish(); }
        });

        this.apparitionValider();
        this.imageValidation.setOnClickListener(view -> this.onMenuSaveClick());
    }

    @NonNull private Intent getProduitPlanDePlacementIntent()
    {
        final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, ScannerEmplacementActivity.class);
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        return detailProduitPlanDePlacementIntent;
    }

    @NonNull private Intent getDetailProduitPlanDePlacementIntent()
    {
        final Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursActivity.super.getBundle();
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        final Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursActivity.this, ScannerEmplacementActivity.class);
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        return detailProduitPlanDePlacementIntent;
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(final Menu menu) { return true; }

    private void onMenuSaveClick()
    {
        if (this.zoneTextView.getText().toString().trim().isEmpty() || this.emplacementTextView.getText().toString().trim().isEmpty() || this.lotEditText.getText().toString().trim().isEmpty() || this.datePeremptionTextView.getText().toString().trim().isEmpty())
        { Toast.makeText(CreationLotControleDesRetoursActivity.this, "Tous les éléments n'ont pas été saisis.", Toast.LENGTH_SHORT).show(); }
        else
        {
            final Bundle onMenuSaveClick_Bundle = CreationLotControleDesRetoursActivity.super.getBundle();

            this.ajoutRetourLigneBDD();

            final Intent onMenuSaveClick_Intent = new Intent();
            onMenuSaveClick_Intent.putExtras(onMenuSaveClick_Bundle);

            CreationLotControleDesRetoursActivity.this.setResult(CodesEchangesActivites.RETOUR_LOT, onMenuSaveClick_Intent);
            CreationLotControleDesRetoursActivity.this.finish();
        }
    }

    @Override public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data)
        {
            switch (requestCode)
            {
                case CodesEchangesActivites.RESULT_ZONE:
                    final int zoneid = Objects.requireNonNull(data.getExtras()).getInt("zoneid");
                    if(-1 != zoneid)
                    {
                        this.zoneSelectionner = ZoneOpenHelper.getUneZoneByID(this.db, zoneid);
                        this.zoneTextView.setText(this.zoneSelectionner.getZoneName().trim());
                        this.emplacementTextView.performClick();
                    }
                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    final int emplacementid = Objects.requireNonNull(data.getExtras()).getInt("emplacementId");
                    if(-1 != emplacementid)
                    {
                        this.emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(this.db, emplacementid);
                        this.emplacementTextView.setText(this.emplacementSelectionner.getAdressage().trim());
                    }
                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Depot_Emplacement depotEmplacement = EmplacementOpenHelper.getUnEmplacementByID(this.db, Objects.requireNonNull(data.getExtras()).getInt("emplacementSelectionneID"));
                    String code_emplacement = data.getStringExtra("code");
                    if(null != code_emplacement && !code_emplacement.contentEquals(""))
                    {
                        if(null == depotEmplacement)
                        {
                            if (code_emplacement.startsWith("PHITAGPLACE")) {
                                final String[] tabchaine = code_emplacement.split(":");
                                code_emplacement = tabchaine[1];
                                depotEmplacement = EmplacementOpenHelper.getUnEmplacementByID(this.db, Integer.parseInt(code_emplacement));
                            }
                        }
                        if(null != depotEmplacement)
                        {
                            final Depot_Zone depotZone = ZoneOpenHelper.getUneZoneByID(this.db, depotEmplacement.getZoneID());
                            if(null != depotZone)
                            {
                                this.zoneTextView.setText(depotZone.getZoneName().trim());
                                this.emplacementTextView.setText(depotEmplacement.getAdressage().trim());
                            }
                        }
                        else { this.zoneTextView.performClick(); }
                    }
                    else { this.zoneTextView.performClick(); }
                    break;

                case CodesEchangesActivites.RETOUR_LOT:
                    final String code = Objects.requireNonNull(data.getExtras()).getString("code");
                    String numLot = data.getExtras().getString("numLot");
                    String datePeremptionScanner = data.getExtras().getString("datePeremption");
                    if(null != code)
                    {
                        if(null == numLot || numLot.contentEquals(""))
                        {
                            final Map<String, String> DecoupeMap = OutilsDecodage.decouperGTIN(code);
                            if(1 < DecoupeMap.size())
                            {
                                Produit produitScanne = null;
                                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(this.db, DecoupeMap.get(OutilsDecodage.codeGtin));

                                if(produits.isEmpty()) produits = ProduitOpenHelper.getProduitsParGTIN(this.db, DecoupeMap.get(OutilsDecodage.codeGtinSansAi));

                                if (1 == produits.size()) { produitScanne = produits.get(0); }

                                if(null == produitScanne || produitScanne.getID_produit() != this.produitSelectionne.getID_produit())
                                {
                                    numLot = null;
                                    datePeremptionScanner = null;
                                    this.lotEditText.setText("");
                                    this.datePeremptionTextView.setText("");
                                    this.afficherSnackBar();
                                }
                                else
                                {
                                    numLot = DecoupeMap.get(OutilsDecodage.numeroLot);
                                    datePeremptionScanner = DecoupeMap.get(OutilsDecodage.dateDePeremption);
                                    if(null != datePeremptionScanner)
                                    {
                                        final String[] tab_date = datePeremptionScanner.split("-");
                                        if(3 == tab_date.length) { datePeremptionScanner = tab_date[2]+"/"+tab_date[1]+"/"+tab_date[0]; }
                                    }
                                }
                            }
                        }

                        if(null != numLot) { this.lotEditText.setText(numLot); }

                        if(null != datePeremptionScanner) { this.datePeremptionTextView.setText(datePeremptionScanner); }
                    }
                    break;
            }

            this.invalidateOptionsMenu();
        }
    }

    void apparitionValider()
    {
        if(!this.lotEditText.getText().toString().contentEquals("") && !this.datePeremptionTextView.getText().toString().contentEquals("") && !this.qteActuelleEditText.getText().toString().contentEquals("0"))
        {
            this.validationScan.setVisibility(View.VISIBLE);
            this.blinkImage();
        }
    }

    private void blinkImage()
    {
        // set its background to our AnimationDrawable XML resource.
        this.imageValidation.setBackgroundResource(R.drawable.animation_blinking);

        /*
         * Get the background, which has been compiled to an AnimationDrawable
         * object.
         */
        final AnimationDrawable frameAnimation = (AnimationDrawable) this.imageValidation.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    // Class static permettant de faire apparaitre le DatePicker du téléphone
    public static class DatePickerFragmentReception extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        TextView datePeremption = null;

        @Override @NonNull public Dialog onCreateDialog(final Bundle savedInstanceState)
        {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            final int year = c.get(Calendar.YEAR)+1;
            c.add(Calendar.MONTH, -1);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            final int month = c.get(Calendar.MONTH);
            final int day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(this.requireActivity(), this, year, month, day);
        }

        public void onDateSet(final DatePicker view, final int year, int month, final int day)
        {
            month++;
            String mois = "";
            if (10 > month) { mois += "0"; }
            mois += String.valueOf(month);
            final String date = day + "/" + mois + "/" + year;
            Date datedate = null;
            try
            {
                final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                datedate = dateFormat.parse(date);

            }
            catch (final ParseException e) { Log.e("Parse Exception", Objects.requireNonNull(e.getMessage())); }
            this.datePeremption.setText(date);
            this.setDatePeremptionColor(datedate);
            ((CreationLotControleDesRetoursActivity) this.requireActivity()).apparitionValider();

        }

        private void setDatePeremptionColor(final Date date)
        {
            if (null != date)
            {
                final Date dateDuJour = new Date();
                final long diff = dateDuJour.getTime() - date.getTime();
                final int delai = (int) (diff / (long) (1000 * 60 * 60 * 24));

                final int delai30jours = -30;
                final int delai60jours = -60;

                if (delai >= delai30jours) { this.datePeremption.setTextColor(this.requireContext().getResources().getColor(R.color.rouge2, null)); }
                else if (delai >= delai60jours) { this.datePeremption.setTextColor(this.requireContext().getResources().getColor(R.color.orange2, null)); }
                else { this.datePeremption.setTextColor(this.requireContext().getResources().getColor(R.color.vert, null)); }
            }
            else { this.datePeremption.setTextColor(Color.BLACK); }
        }

        public void setTextView(final TextView editText) {
            this.datePeremption = editText;
        }
    }

    public void afficherSnackBar()
    {
        final Snackbar snackbar;
        snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Mauvais produit scanné</b>", 0), Snackbar.LENGTH_LONG);

        final Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(this.getResources().getColor(R.color.rouge2, null));
        final TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8.0F);
        snackbar.show();
    }

    private void ajoutRetourLigneBDD()
    {
        //on regarde si un reliquat existe déjà avec ces informations
        final List<Retour_Ligne> retourLigneListe = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(this.db, this.retourSelectionne, this.produitSelectionne.getID_produit());
        Retour_Ligne retourLigneTemp  = this.retourLigneCourant;

        boolean existe = false;
        for(final Retour_Ligne retourligne : retourLigneListe)
        {
            String datePeremption = this.datePeremptionTextView.getText().toString();
            final String[] datePeremptionTab = datePeremption.split("/");
            if (3 == datePeremptionTab.length)
                datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

            if(retourligne.getLot_Retourner().trim().contentEquals(this.lotEditText.getText().toString().trim()) && retourligne.getPeremptionDate().trim().contentEquals(datePeremption))
            {
                retourLigneTemp = retourligne;
                existe = true;
            }
        }

        if(existe)
        {
            final int quantite = Integer.parseInt(this.qteActuelleEditText.getText().toString());

            /**
             * MAJ du stock lot emplacement
             */
            final Stock_Lot_Emplacement_Light stockLotEmplacementLight = Stock_Lot_EmplacementLightOpenHelper.getStockLotEmplacementByProduitLotSerieEtDepot(this.db, this.produitSelectionne, this.depotSelectionne, retourLigneTemp.getLot(), retourLigneTemp.getSerie_Retourner());
            stockLotEmplacementLight.setQte(stockLotEmplacementLight.getQte()+ (double) quantite);
            stockLotEmplacementLight.setQte_Preparer(stockLotEmplacementLight.getQte_Preparer()+quantite);
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(this.db, stockLotEmplacementLight);

            /**
             * MAJ retourLigne
             */
            retourLigneTemp.setQte_Retourner(retourLigneTemp.getQte_Retourner()+ (double) quantite);
            final long rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(this.db, retourLigneTemp);
        }
        else
        {
            final String numeroLot = this.lotEditText.getText().toString();
            String datePeremption = this.datePeremptionTextView.getText().toString();
            final String[] datePeremptionTab = datePeremption.split("/");
            if (3 == datePeremptionTab.length)
                datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

            final String zoneName = this.zoneTextView.getText().toString();
            final String emplacementName = this.emplacementTextView.getText().toString();
            final String numero_Serie = this.numSerieEditText.getText().toString();
            final int quantite = Integer.parseInt(this.qteActuelleEditText.getText().toString());

            /**
             * Création du stock correspondant
             */
            final Random randomStock = new Random();
            int stockId = randomStock.nextInt();
            if (0 < stockId) stockId = stockId * -1;
            final Stock_Lot_Emplacement_Light newStockLot = new Stock_Lot_Emplacement_Light((double) quantite, numeroLot, datePeremption, emplacementName, this.depotSelectionne.getDepot_Reference(), zoneName, this.produitSelectionne.getID_produit(), quantite, numero_Serie);
            newStockLot.set_UID(stockId);
            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(this.db, newStockLot);

            /**
             * Création du retourLigne
             */
            final Random randomretourLigne = new Random();
            int retourLigneId = randomretourLigne.nextInt();
            if (0 < retourLigneId) retourLigneId = retourLigneId * -1;

            retourLigneTemp.set_UID(retourLigneId);
            retourLigneTemp.setLot_Retourner(numeroLot.trim());
            retourLigneTemp.setSerie_Retourner(numero_Serie.trim());
            retourLigneTemp.setPeremptionDate(datePeremption.trim());

            retourLigneTemp.setRetourPUI_Zone(zoneName.trim());
            retourLigneTemp.setRetourPUI_Emplacement(emplacementName.trim());
            retourLigneTemp.setQte_Retourner((double) quantite);

            final long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(this.db, retourLigneTemp);
            if (-1L != rowID) {}
        }
    }
}