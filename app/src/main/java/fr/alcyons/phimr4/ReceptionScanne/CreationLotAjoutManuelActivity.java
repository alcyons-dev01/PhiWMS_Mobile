package fr.alcyons.phimr4.ReceptionScanne;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phimr4.ControleDesRetoursScannee.ListeEmplacementCreationScanneeActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RESULT_ZONE;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

public class CreationLotAjoutManuelActivity extends ServiceActivity {

    List<String>liste_designation_produit;
    List<PH_Reliquat> liste_reliquat_commande_courante;
    List<ObjetReceptionScannee> list_produit_scannee;
    int zoneid;
    int emplacementid;

    //objet graphique
    TextView zone_selectionne;
    TextView emplacement_selectionne;
    EditText numeroLot_edittext;
    TextView date_peremption_textview;
    LinearLayout linear_expiration;
    //objet system
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_lot_reception_scannee);

        //gestion du package manager
        pm = CreationLotAjoutManuelActivity.this.getPackageManager();

        //récupération des données
        liste_designation_produit = new ArrayList<>();
        liste_reliquat_commande_courante = (List<PH_Reliquat>) intent.getExtras().getSerializable("listeReliquatCommande");
        list_produit_scannee = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("listeProduitScannee");

        //récupération des objets graphiques
        Spinner spinner = (Spinner) findViewById(R.id.choixProduit);
        numeroLot_edittext = (EditText) findViewById(R.id.numLotNouveauProduit);
        date_peremption_textview = (TextView) findViewById(R.id.datePeremptionNouveauProduit);
        final TextView qte_reception_textview = (TextView) findViewById(R.id.quantiteReceptionNouveauProduit);
        LinearLayout datamatrix_zone_emplacement = (LinearLayout) findViewById(R.id.datamatrix_zone_emplacement);
        zone_selectionne = (TextView) findViewById(R.id.zone_selectionne);
        emplacement_selectionne = (TextView) findViewById(R.id.emplacement_selectionne);
        final com.github.clans.fab.FloatingActionButton annuler_layout = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.layoutAnnulation);
        final com.github.clans.fab.FloatingActionButton confirmer_layout = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.layoutConfirmation);
        LinearLayout layout_zone_emplacement = (LinearLayout) findViewById(R.id.layout_zone_emplacement);
        final LinearLayout datamatrix_produit = (LinearLayout) findViewById(R.id.datamatrix_produit);
        final LinearLayout linear_expiration = (LinearLayout) findViewById(R.id.linear_expiration);

        //gestion de la liste à afficher dans le spinner
        for(PH_Reliquat reliquat : liste_reliquat_commande_courante)
        {
            if(reliquat.getQteReliquat_X() != 0)
            {
                liste_designation_produit.add(reliquat.getDesignationCourte());
            }
        }

        //récupération de produit pour mettre dans le spinner
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, liste_designation_produit);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp1);

        //Récupération du produit sélectionner quand on le change sur le spinner
        final String[] produit_selectionne = {""};
        final PH_Reliquat[] reliquat_courant = {null};

        //clic sur le datamatrix pour scanner un produit et récupérer son numéro de lot et la date de péremption
        datamatrix_produit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Produit produitSelectionne = ProduitOpenHelper.getProduitByID(db, reliquat_courant[0].getProduitID());

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle scanProduitBundle = CreationLotAjoutManuelActivity.super.getBundle();
                    scanProduitBundle.putBoolean("doitEtreIdentique", true);
                    scanProduitBundle.putString("bannerText", "Scanner un numéro de lot");
                    scanProduitBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                    scanProduitBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                    scanProduitBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent scanProduitIntent = new Intent(CreationLotAjoutManuelActivity.this, ScannerSearchOnlyActivity.class);
                    scanProduitIntent.putExtras(scanProduitBundle);
                    CreationLotAjoutManuelActivity.this.startActivityForResult(scanProduitIntent, CodesEchangesActivites.RETOUR_LOT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle scanProduitBundle = CreationLotAjoutManuelActivity.super.getBundle();
                        scanProduitBundle.putBoolean("doitEtreIdentique", true);
                        scanProduitBundle.putString("bannerText", "Scanner un numéro de lot");
                        scanProduitBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                        scanProduitBundle.putBoolean("isBoutonSuppressionExistant", true);
                        scanProduitBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                        Intent scanProduitIntent = new Intent(CreationLotAjoutManuelActivity.this, BarcodeCaptureActivity.class);
                        scanProduitIntent.putExtras(scanProduitBundle);
                        CreationLotAjoutManuelActivity.this.startActivityForResult(scanProduitIntent, CodesEchangesActivites.RETOUR_LOT);
                    }
                    else
                    {
                        Bundle scanProduitBundle = CreationLotAjoutManuelActivity.super.getBundle();
                        scanProduitBundle.putBoolean("doitEtreIdentique", true);
                        scanProduitBundle.putString("bannerText", "Scanner un numéro de lot");
                        scanProduitBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                        scanProduitBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                        scanProduitBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent scanProduitIntent = new Intent(CreationLotAjoutManuelActivity.this, ScannerSearchOnlyActivity.class);
                        scanProduitIntent.putExtras(scanProduitBundle);
                        CreationLotAjoutManuelActivity.this.startActivityForResult(scanProduitIntent, CodesEchangesActivites.RETOUR_LOT);
                    }
                }
            }
        });

        //click sur le datamatrix de l'emplacement
        datamatrix_zone_emplacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle scanZoneEmplacementBundle = CreationLotAjoutManuelActivity.super.getBundle();
                    scanZoneEmplacementBundle.putString("bannerText", "Scanner un emplacement");
                    scanZoneEmplacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                    scanZoneEmplacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent scanZoneEmplacementIntent = new Intent(CreationLotAjoutManuelActivity.this, ScannerSearchOnlyActivity.class);
                    scanZoneEmplacementIntent.putExtras(scanZoneEmplacementBundle);
                    CreationLotAjoutManuelActivity.this.startActivityForResult(scanZoneEmplacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle scanZoneEmplacementBundle = CreationLotAjoutManuelActivity.super.getBundle();
                        scanZoneEmplacementBundle.putString("bannerText", "Scanner un emplacement");
                        scanZoneEmplacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                        scanZoneEmplacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent scanZoneEmplacementIntent = new Intent(CreationLotAjoutManuelActivity.this, BarcodeCaptureActivity.class);
                        scanZoneEmplacementIntent.putExtras(scanZoneEmplacementBundle);
                        CreationLotAjoutManuelActivity.this.startActivityForResult(scanZoneEmplacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        Bundle scanZoneEmplacementBundle = CreationLotAjoutManuelActivity.super.getBundle();
                        scanZoneEmplacementBundle.putString("bannerText", "Scanner un emplacement");
                        scanZoneEmplacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                        scanZoneEmplacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent scanZoneEmplacementIntent = new Intent(CreationLotAjoutManuelActivity.this, ScannerSearchOnlyActivity.class);
                        scanZoneEmplacementIntent.putExtras(scanZoneEmplacementBundle);
                        CreationLotAjoutManuelActivity.this.startActivityForResult(scanZoneEmplacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                }
            }
        });

        //affichage de la liste des zones
        layout_zone_emplacement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Depot_Zone> depotZoneList = new ArrayList<Depot_Zone>();
                Depot depotSelectionne = DepotOpenHelper.getDepotPUI(db);
                depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);

                if (depotZoneList.size() != 0) {
                    Intent newIntent = new Intent(CreationLotAjoutManuelActivity.this, ListeZoneCreationActivity.class);
                    Bundle extras = CreationLotAjoutManuelActivity.super.getBundle();
                    extras.putInt("depotID", depotSelectionne.getDepot_UID());
                    newIntent.putExtras(extras);
                    CreationLotAjoutManuelActivity.this.startActivityForResult(newIntent, RESULT_ZONE);
                }
            }
        });

        //gestion des emplacements
        emplacement_selectionne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Depot_Zone> depotZoneList = new ArrayList<Depot_Zone>();
                Depot depotSelectionne = DepotOpenHelper.getDepotPUI(db);
                depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);
                if(zoneid == 0)
                {
                    if (depotZoneList.size() != 0) {
                        Intent newIntent = new Intent(CreationLotAjoutManuelActivity.this, ListeZoneCreationActivity.class);
                        Bundle extras = CreationLotAjoutManuelActivity.super.getBundle();
                        extras.putInt("depotID", depotSelectionne.getDepot_UID());
                        newIntent.putExtras(extras);
                        CreationLotAjoutManuelActivity.this.startActivityForResult(newIntent, RESULT_ZONE);
                    }
                }
                else
                {
                    Intent newIntent = new Intent(CreationLotAjoutManuelActivity.this, ListeEmplacementCreationScanneeActivity.class);
                    Bundle extras = CreationLotAjoutManuelActivity.super.getBundle();
                    extras.putInt("zoneid", zoneid);
                    extras.putInt("depotID", depotSelectionne.getDepot_UID());
                    newIntent.putExtras(extras);
                    CreationLotAjoutManuelActivity.this.startActivityForResult(newIntent, RETOUR_CODE_EMPLACEMENT);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //récupération de la désignation du produit sélectionner
                produit_selectionne[0] = liste_designation_produit.get(position);

                //récupération du ph_reliquat correspondant
                for(PH_Reliquat courant : liste_reliquat_commande_courante)
                {
                    if(courant.getDesignationCourte().contentEquals(produit_selectionne[0]))
                    {
                        reliquat_courant[0] = courant;
                        break;
                    }
                }

                    if(reliquat_courant[0] != null)
                    {
                        //on récupère le produit pour vérifier si il est suivi par lot ou pas
                        final Produit produit_courant = ProduitOpenHelper.getProduitByID(db, reliquat_courant[0].getProduitID());

                        //initialisation des objets
                        qte_reception_textview.setText(String.valueOf(reliquat_courant[0].getConditionnementAchat()));

                        if(produit_courant.isSuivi_Lot())
                        {
                            //gestion du click pour la date de péremption
                            date_peremption_textview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatePickerFragment newFragment = new DatePickerFragment();
                                    newFragment.setTextView(date_peremption_textview);
                                    newFragment.show((CreationLotAjoutManuelActivity.this).getSupportFragmentManager(), "timePicker");
                                }
                            });
                        }
                        else
                        {
                            linear_expiration.setVisibility(View.GONE);
                            datamatrix_produit.setVisibility(View.GONE);
                            numeroLot_edittext.setFocusable(false);
                            String currentDate = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date());
                            numeroLot_edittext.setText("Phi"+currentDate);
                        }

                        //gestion du click sur la quantite pour modifier la quantite de réception
                        qte_reception_textview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Ouvre une boite de dialogue avec un NumberPicker
                                String title = produit_selectionne[0];
                                String message = "Quantité réceptionné : ";
                                int maxValue = reliquat_courant[0].getQteReliquat_X();
                                int value = reliquat_courant[0].getQteReliquat_X();

                                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        int qteAprès = aNumberPicker.getValue()*reliquat_courant[0].getConditionnementAchat();
                                        qte_reception_textview.setText(String.valueOf(qteAprès));
                                        InputMethodManager imm = (InputMethodManager) CreationLotAjoutManuelActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                        dialog.dismiss();
                                    }
                                };

                                Alerte.afficherAlerteNumberPickerAvecPas(CreationLotAjoutManuelActivity.this, title, message, value, maxValue, onClickListener, reliquat_courant[0].getConditionnementAchat());
                            }
                        });


                        //gestion des boutons d'annulation et de confirmation de l'alerte
                        annuler_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CreationLotAjoutManuelActivity.this.finish();
                            }
                        });

                        confirmer_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //récupération des valeurs
                                int quantite_retourner = Integer.parseInt(qte_reception_textview.getText().toString());
                                String numLot_retourner = numeroLot_edittext.getText().toString();
                                String datePeremption_retourner = date_peremption_textview.getText().toString();

                                if(numLot_retourner.contentEquals(""))
                                {
                                    Toast.makeText(CreationLotAjoutManuelActivity.this, "Saisir un numéro de lot", Toast.LENGTH_SHORT).show();
                                }
                                else if(datePeremption_retourner.contentEquals("") && produit_courant.isSuivi_Lot())
                                {
                                    Toast.makeText(CreationLotAjoutManuelActivity.this, "Sélectionner une date de péremption", Toast.LENGTH_SHORT).show();
                                }
                                else if(emplacementid == 0)
                                {
                                    Toast.makeText(CreationLotAjoutManuelActivity.this, "Sélectionner un emplacement", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //check date
                                    String date_gs1 = "";
                                    if(!datePeremption_retourner.contentEquals(""))
                                    {
                                        String[] date_tab = datePeremption_retourner.split("/");
                                        date_gs1 = "17"+date_tab[date_tab.length-1].substring(2)+date_tab[1]+date_tab[0];
                                    }

                                    Produit produit_courant = ProduitOpenHelper.getProduitByID(db, reliquat_courant[0].getProduitID());

                                    String gs1_reconstruit = produit_courant.getGTIN()+date_gs1+"10"+numLot_retourner;
                                    if(produit_courant.getGTIN().contentEquals(""))
                                    {
                                        date_gs1 = "dp"+date_gs1;
                                        gs1_reconstruit = "ci"+produit_courant.getCodeInconnue()+date_gs1+"nl10"+numLot_retourner;
                                    }

                                    ObjetReceptionScannee objetReceptionScannee = new ObjetReceptionScannee();
                                    objetReceptionScannee.setGs1_scannee(gs1_reconstruit);
                                    objetReceptionScannee.setEmplacement_uid(emplacementid);
                                    objetReceptionScannee.setQuantiteScannee(quantite_retourner);
                                    objetReceptionScannee.setResultat_france_mvo("");

                                    list_produit_scannee.add(objetReceptionScannee);

                                    //mise a jour du reliquat
                                    reliquat_courant[0].setQteReliquat_X(reliquat_courant[0].getQteReliquat_X()-quantite_retourner);
                                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat_courant[0]);

                                    //on retourne au récapitulatif
                                    Intent resultIntent = new Intent();
                                    Bundle extras = CreationLotAjoutManuelActivity.super.getBundle();
                                    extras.putSerializable("listeProduitScannee", (Serializable) list_produit_scannee);
                                    resultIntent.putExtras(extras);
                                    CreationLotAjoutManuelActivity.this.setResult(CodesEchangesActivites.RETOUR_OBJET_RECEPTION_SCANNEE, resultIntent);
                                    CreationLotAjoutManuelActivity.this.finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(CreationLotAjoutManuelActivity.this, "Sélectionner un produit dans la liste", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_ZONE:
                    zoneid = data.getExtras().getInt("zoneid");
                    Depot_Zone zoneSelectionner = gestionnaireZone.getUneZoneByID(db, zoneid);
                    zone_selectionne.setText(zoneSelectionner.getZoneName().trim());
                    emplacement_selectionne.performClick();
                    break;

                case RETOUR_CODE_EMPLACEMENT:
                    emplacementid = data.getExtras().getInt("emplacementId");
                    Depot_Emplacement emplacementSelectionner = gestionnaireEmplacement.getUnEmplacementByID(db, emplacementid);
                    emplacement_selectionne.setText(emplacementSelectionner.getAdressage().trim());
                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));
                    if(depotEmplacement == null)
                    {
                        String code = data.getStringExtra("code");
                        if(code != null)
                        {
                            if (code.startsWith("PHITAGPLACE+")) {
                                String[] tabchaine = code.split(":");
                                code = tabchaine[1];
                            }
                            depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, Integer.parseInt(code));
                        }
                    }

                    if(depotEmplacement != null)
                    {
                        Depot_Zone depotZone = gestionnaireZone.getUneZoneByID(db, depotEmplacement.getZoneID());
                        if(depotEmplacement!= null && depotZone != null){
                            zone_selectionne.setText(depotZone.getZoneName().trim());
                            emplacement_selectionne.setText(depotEmplacement.getAdressage().trim());

                            zoneid = depotZone.getZoneID();
                            emplacementid = depotEmplacement.get_UID();
                        }
                    }
                    break;

                case RETOUR_LOT:
                    String code = data.getExtras().getString("code");
                    if(code != null && code != "")
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        String numLot = "";
                        String datePeremptionScanner = "";
                        if(gs1Decoupe.size() != 1 && !code.startsWith("ci"))
                        {
                            numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                            datePeremptionScanner = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                        }
                        else
                        {
                            String code_inconnu = code;
                            if(code_inconnu.startsWith("ci"))
                            {
                                Map<String, String> mapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                                numLot = mapInconnu.get("Lot");
                                datePeremptionScanner = mapInconnu.get("Date");
                            }
                        }

                        String[] tab_date = datePeremptionScanner.split("-");
                        datePeremptionScanner = tab_date[2]+"/"+tab_date[1]+"/"+tab_date[0];

                        numeroLot_edittext.setText(numLot);
                        date_peremption_textview.setText(datePeremptionScanner);
                    }
                    break;
            }
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed()
    {
        CreationLotAjoutManuelActivity.this.finish();
    }

    // Class static permettant de faire apparaitre le DatePicker du téléphone
    public static class DatePickerFragment extends DialogFragment
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
}
