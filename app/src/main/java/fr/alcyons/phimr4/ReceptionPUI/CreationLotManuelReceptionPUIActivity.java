package fr.alcyons.phimr4.ReceptionPUI;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import android.text.Html;
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
import java.util.TimeZone;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerEmplacementActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phimr4.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RESULT_ZONE;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

public class CreationLotManuelReceptionPUIActivity extends ServiceActivity {

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
    TextView emplacementParDefaut;

    Commande commandecourante;
    PH_Reliquat reliquat_courant;
    PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte;

    PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_lot_preparation);

        //gestion du package manager
        pm = CreationLotManuelReceptionPUIActivity.this.getPackageManager();

        // Récupération des variables globales
        produitSelectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotID"));
        reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, intent.getExtras().getInt("ReliquatID"));
        phReliquatReceptionPUIAdapte = (PH_Reliquat_ReceptionPUI_Adapte) intent.getExtras().getSerializable("phReliquatReceptionPUIAdapte");
        commandecourante = CommandeOpenHelper.getCommandeByNumero(db, reliquat_courant.getCommandeNumero());

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
        datePeremptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragmentReception newFragment = new DatePickerFragmentReception();
                newFragment.setTextView(datePeremptionTextView);
                newFragment.show((CreationLotManuelReceptionPUIActivity.this).getSupportFragmentManager(), "timePicker");
            }
        });


        //affichage de la liste des zones
        zoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depotZoneList = new ArrayList<Depot_Zone>();
                depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);

                if (depotZoneList.size() != 0) {
                    Intent newIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = CreationLotManuelReceptionPUIActivity.super.getBundle();
                    extras.putInt("depotID", depotSelectionne.getDepot_UID());
                    newIntent.putExtras(extras);
                    CreationLotManuelReceptionPUIActivity.this.startActivityForResult(newIntent, RESULT_ZONE);
                }

            }
        });

        //affichage des emplacements
        emplacementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emplacementList = new ArrayList<>();
                if (zoneSelectionner != null) {
                    emplacementList = gestionnaireEmplacement.getEmplacementsParZone(db, zoneSelectionner);
                }


                if (emplacementList.size() != 0) {
                    Intent newIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ListeEmplacementCreationActivity.class);
                    Bundle extras = CreationLotManuelReceptionPUIActivity.super.getBundle();
                    extras.putInt("zoneid", zoneSelectionner.getZoneID());
                    newIntent.putExtras(extras);
                    CreationLotManuelReceptionPUIActivity.this.startActivityForResult(newIntent, RETOUR_CODE_EMPLACEMENT);
                }

            }
        });

        //clic sur le datamatrix de la zone et de l'emplacement
        datamatrix1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                    detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ScannerEmplacementActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, BarcodeCaptureActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ScannerEmplacementActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                }
            }
        });

        //clic sur le datamtrix permettant de récupérer le numéro de lot et la date de péremption
        datamatrix2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", false);
                    detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                    detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ScannerProduitActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                        detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, BarcodeCaptureActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                    }
                    else
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                        detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotManuelReceptionPUIActivity.this, ScannerProduitActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotManuelReceptionPUIActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);
                    }
                }

            }
        });

        if(!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            datamatrix2ImageView.setVisibility(View.GONE);
        }

        // Hydratation des objets graphiques
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne());
        referenceProduit.setText(produitSelectionne.getRef_fourni());
        if (depotSelectionne.getStructure().contains("PAD")) {
            zoneTextView.setText(produitSelectionne.getZone_PAD_Defaut());
            emplacementTextView.setText(produitSelectionne.getEmplacement_PAD_Defaut());
            zoneSelectionner = gestionnaireZone.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_PAD_Defaut());
        } else if (depotSelectionne.getStructure().contains("PUF")) {
            zoneTextView.setText(produitSelectionne.getZone_UF_Defaut());
            emplacementTextView.setText(produitSelectionne.getEmplacement_UF_Defaut());
            zoneSelectionner = gestionnaireZone.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_UF_Defaut());
        } else {
            zoneTextView.setText(produitSelectionne.getZone_PUI_Defaut());
            emplacementTextView.setText(produitSelectionne.getEmplacement_PUI_Defaut());
            zoneSelectionner = gestionnaireZone.getZoneByDepotEtNom(db, depotSelectionne, produitSelectionne.getZone_PUI_Defaut());
        }

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
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String dateAAfficher = "";
            Date date = new Date();

            try {
                date = dateDecodeur.parse(dateDePeremption);
                dateAAfficher = dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePeremptionTextView.setText(dateAAfficher);
        }

        if(!produitSelectionne.isSuivi_Serialisation())
        {
            numSerieEditText.setVisibility(View.GONE);
            labelSerie.setVisibility(View.GONE);
        }

        lotEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lotnumero = Alerte.afficherAlerteEditText(CreationLotManuelReceptionPUIActivity.this, "Numéro de lot", "Saisir le numéro de lot");
                lotEditText.setText(lotnumero);
                apparitionValider();
            }
        });

        //affichage de la quantité de base
        if(produitSelectionne.getCond_Achat_Gros_volume() != 0 && produitSelectionne.getCond_Achat_Gros_volume() <= reliquat_courant.getQteReliquat_X())
        {
            qteActuelleEditText.setText(String.valueOf((int)produitSelectionne.getCond_Achat_Gros_volume()));
        }
        else if(produitSelectionne.getCond_achat() != 0 && produitSelectionne.getCond_achat() <= reliquat_courant.getQteReliquat_X())
        {
            qteActuelleEditText.setText(String.valueOf((int)produitSelectionne.getCond_achat()));
        }
        else
        {
            qteActuelleEditText.setText(String.valueOf((int)reliquat_courant.getQteReliquat_X()));
        }

        relativeQte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = produitSelectionne.getDesignation_interne();
                String message = "Choisir une quantité: ";
                int maxValue = reliquat_courant.getQteReliquat_X();
                int value = reliquat_courant.getQteReliquat_X();

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue()*reliquat_courant.getConditionnementAchat();
                        qteActuelleEditText.setText(String.valueOf(qteAprès).trim());
                        dialog.dismiss();
                        apparitionValider();
                    }
                };

                Alerte.afficherAlerteNumberPickerAvecPas(CreationLotManuelReceptionPUIActivity.this, title, message, value, maxValue, onClickListener, reliquat_courant.getConditionnementAchat());
            }
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

        apparitionValider();
        imageValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuSaveClick();
            }
        });
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
        if (zoneTextView.getText().toString().trim().equals("") || emplacementTextView.getText().toString().trim().equals("") || lotEditText.getText().toString().trim().equals("") || datePeremptionTextView.getText().toString().trim().equals("")) {
            Toast.makeText(CreationLotManuelReceptionPUIActivity.this, "Tous les éléments n'ont pas été saisis.", Toast.LENGTH_SHORT).show();
        } else {
            Bundle onMenuSaveClick_Bundle = CreationLotManuelReceptionPUIActivity.super.getBundle();
            onMenuSaveClick_Bundle.putString("nomZone", zoneTextView.getText().toString());
            onMenuSaveClick_Bundle.putString("nomEmplacement", emplacementTextView.getText().toString());
            onMenuSaveClick_Bundle.putString("numLot", lotEditText.getText().toString());
            onMenuSaveClick_Bundle.putString("numSerie", numSerieEditText.getText().toString());
            if(qteActuelleEditText.getText().toString().trim().length()!=0){
                onMenuSaveClick_Bundle.putInt("qteActuelle", Integer.parseInt(qteActuelleEditText.getText().toString().trim()));
            }

            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = new Date();
            String dateARetourner = "";

            try {
                date = dateDecodeur.parse(datePeremptionTextView.getText().toString().trim());
                dateARetourner = dateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            onMenuSaveClick_Bundle.putString("datePeremption", dateARetourner);

            Intent onMenuSaveClick_Intent = new Intent();
            onMenuSaveClick_Intent.putExtras(onMenuSaveClick_Bundle);

            CreationLotManuelReceptionPUIActivity.this.setResult(RETOUR_LOT, onMenuSaveClick_Intent);
            CreationLotManuelReceptionPUIActivity.this.finish();
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case RESULT_ZONE:
                    int zoneid = data.getExtras().getInt("zoneid");
                    if(zoneid != -1)
                    {
                        zoneSelectionner = gestionnaireZone.getUneZoneByID(db, zoneid);
                        zoneTextView.setText(zoneSelectionner.getZoneName().trim());
                        emplacementTextView.performClick();
                    }
                    break;
                case RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    if(emplacementid != -1)
                    {
                        emplacementSelectionner = gestionnaireEmplacement.getUnEmplacementByID(db, emplacementid);
                        emplacementTextView.setText(emplacementSelectionner.getAdressage().trim());
                    }
                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));
                    String code_emplacement = data.getStringExtra("code");
                    if(code_emplacement != null && !code_emplacement.contentEquals(""))
                    {
                        if(depotEmplacement == null)
                        {
                            if (code_emplacement.startsWith("PHITAGPLACE+")) {
                                String[] tabchaine = code_emplacement.split(":");
                                code_emplacement = tabchaine[1];
                                depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, Integer.parseInt(code_emplacement));
                            }
                        }
                        if(depotEmplacement != null)
                        {
                            Depot_Zone depotZone = gestionnaireZone.getUneZoneByID(db, depotEmplacement.getZoneID());
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
                    String code = data.getExtras().getString("code");
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
        String qte = qteActuelleEditText.getText().toString();

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


    @Override
    public void onBackPressed()
    {
        CreationLotManuelReceptionPUIActivity.this.finish();
    }

    // Class static permettant de faire apparaitre le DatePicker du téléphone
    public static class DatePickerFragmentReception extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TextView datePeremption;

        @TargetApi(Build.VERSION_CODES.N)
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
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Date dateFournie = null;
            month++;

            String mois = "";
            if (month < 10) {
                mois += "0";
            }

            mois += String.valueOf(month);

            String date = String.valueOf(day) + "/" + mois + "/" + String.valueOf(year);
            Date datedate = null;
            String dateAAfficher = "";
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                datedate = dateFormat.parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePeremption.setText(date);
            setDatePeremptionColor(datedate);
            ((CreationLotManuelReceptionPUIActivity) getActivity()).apparitionValider();

        }

        private void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.rouge2));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.orange2));
                } else {
                    datePeremption.setTextColor(getContext().getResources().getColor(R.color.vert));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }
        }

        public void setTextView(TextView editText) {
            datePeremption = editText;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void afficherSnackBar() {
        Snackbar snackbar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Mauvais produit scanné</b>", 0), Snackbar.LENGTH_LONG);
        }
        ;

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        snackbar.show();
    }
}
