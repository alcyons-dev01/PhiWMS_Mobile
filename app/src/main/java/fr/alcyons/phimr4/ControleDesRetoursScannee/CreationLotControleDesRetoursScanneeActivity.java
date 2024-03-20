package fr.alcyons.phimr4.ControleDesRetoursScannee;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RESULT_ZONE;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT;
import static fr.alcyons.phimr4.Outils.CodesEchangesActivites.RETOUR_LOT;

/**
 * Created by olivier on 14/06/2019.
 */

public class CreationLotControleDesRetoursScanneeActivity  extends ServiceActivity {

    Produit produitSelectionne;
    Depot depotSelectionne;
    Depot_Zone zoneSelectionner;
    Depot_Emplacement emplacementSelectionner;
    List<Depot_Emplacement> emplacementList;
    List<Depot_Zone> depotZoneList;
    TextView zoneTextView;
    TextView emplacementTextView;
    EditText numSerieEditText;
    EditText lotEditText;
    TextView datePeremptionTextView;
    TextView labelSerie;
    EditText qteActuelleEditText;
    ImageView datamatrix1ImageView;
    ImageView datamatrix2ImageView;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_lot_controle_des_retours);

        // Récupération des variables globales
        produitSelectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID"));
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotID"));

        //gestion du package manager
        pm = CreationLotControleDesRetoursScanneeActivity.this.getPackageManager();

        // Récupération des objets graphiques
        zoneTextView = (TextView) findViewById(R.id.zoneName);
        emplacementTextView = (TextView) findViewById(R.id.nomEmplacement);
        labelSerie = (TextView) findViewById(R.id.labelSerie);
        lotEditText = (EditText) findViewById(R.id.numLot);
        numSerieEditText = (EditText) findViewById(R.id.numSerie);
        datePeremptionTextView = (TextView) findViewById(R.id.datePeremption);
        qteActuelleEditText = (EditText) findViewById(R.id.qteActuelle);
        datamatrix1ImageView = (ImageView) findViewById(R.id.datamatrix1);
        datamatrix2ImageView = (ImageView) findViewById(R.id.datamatrix2);

        // Définition des actions sur Click
        datePeremptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTextView(datePeremptionTextView);
                newFragment.show((CreationLotControleDesRetoursScanneeActivity.this).getSupportFragmentManager(), "timePicker");
            }
        });


        //affichage de la liste des zones
        zoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depotZoneList = new ArrayList<Depot_Zone>();
                depotZoneList = gestionnaireZone.getZonesEtEmplacementsParDepot(db, depotSelectionne);

                if (depotZoneList.size() != 0) {
                    Intent newIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ListeZoneCreationScanneeActivity.class);
                    Bundle extras = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                    extras.putInt("depotID", depotSelectionne.getDepot_UID());
                    newIntent.putExtras(extras);
                    CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(newIntent, RESULT_ZONE);
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
                    Intent newIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ListeEmplacementCreationScanneeActivity.class);
                    Bundle extras = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                    extras.putInt("zoneid", zoneSelectionner.getZoneID());
                    newIntent.putExtras(extras);
                    CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(newIntent, RETOUR_CODE_EMPLACEMENT);
                }

            }
        });

        //clic sur le datamatrix de la zone et de l'emplacement
        datamatrix1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                    detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                    detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                    detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                    Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ScannerSearchOnlyActivity.class);
                    detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                    CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, BarcodeCaptureActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                    else
                    {
                        Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteEmplacement);
                        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
                        Intent detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ScannerSearchOnlyActivity.class);
                        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                        CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                    }
                }
            }
        });

        datamatrix2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle detailProduitPlanDePlacementBundle = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
                Intent detailProduitPlanDePlacementIntent = null;
                detailProduitPlanDePlacementBundle.putBoolean("doitEtreIdentique", true);
                detailProduitPlanDePlacementBundle.putString("Designation", produitSelectionne.getDesignation_interne());
                detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un numéro de lot");
                detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteProduit);
                    detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ScannerSearchOnlyActivity.class);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
                        detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, BarcodeCaptureActivity.class);
                    }
                    else
                    {
                        detailProduitPlanDePlacementBundle.putInt("scannerContexteInt", R.string.scannerContexteProduit);
                        detailProduitPlanDePlacementIntent = new Intent(CreationLotControleDesRetoursScanneeActivity.this, ScannerSearchOnlyActivity.class);
                    }
                }
                detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                CreationLotControleDesRetoursScanneeActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_LOT);

            }
        });

        // Hydratation des objets graphiques
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne());
        if (depotSelectionne.getStructure().contains("PAD")) {
            zoneTextView.setText(produitSelectionne.getZone_PAD_Defaut());
            emplacementTextView.setText(produitSelectionne.getEmplacement_PAD_Defaut());
            String zonePADDefaut = produitSelectionne.getZone_PAD_Defaut();
            if(zonePADDefaut == null)
            {
                zonePADDefaut = "ZONE";
            }
            zoneSelectionner = gestionnaireZone.getZoneByDepotEtNom(db, depotSelectionne, zonePADDefaut);
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
        if(numSerie != null && !numSerie.contentEquals(""))
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });
        return true;
    }

    private void onMenuSaveClick() {

        if(lotEditText.getText().toString().trim().equals(""))
        {
            Toast.makeText(CreationLotControleDesRetoursScanneeActivity.this, "Veuillez renseigner le numéro de lot de la référence", Toast.LENGTH_SHORT).show();
        }
        else if(datePeremptionTextView.getText().toString().trim().equals(""))
        {
            Toast.makeText(CreationLotControleDesRetoursScanneeActivity.this, "Veuillez renseigner la date de péremption de la référence", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Bundle onMenuSaveClick_Bundle = CreationLotControleDesRetoursScanneeActivity.super.getBundle();
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

            CreationLotControleDesRetoursScanneeActivity.this.setResult(RETOUR_LOT, onMenuSaveClick_Intent);
            CreationLotControleDesRetoursScanneeActivity.this.finish();
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
                    zoneSelectionner = gestionnaireZone.getUneZoneByID(db, zoneid);
                    zoneTextView.setText(zoneSelectionner.getZoneName().trim());
                    emplacementTextView.performClick();
                    break;
                case RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    emplacementSelectionner = gestionnaireEmplacement.getUnEmplacementByID(db, emplacementid);
                    emplacementTextView.setText(emplacementSelectionner.getAdressage().trim());
                    break;

                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    String code = data.getExtras().getString("code");
                    if(!code.contentEquals(""))
                    {
                        String[] tab_code = code.split(":");
                        int id_emplacement = Integer.parseInt(tab_code[tab_code.length-1]);
                        Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, id_emplacement);
                        if(depotEmplacement != null)
                        {
                            Depot_Zone depotZone = gestionnaireZone.getUneZoneByID(db, depotEmplacement.getZoneID());
                            if(depotEmplacement!= null && depotZone != null){
                                zoneTextView.setText(depotZone.getZoneName().trim());
                                emplacementTextView.setText(depotEmplacement.getAdressage().trim());
                            }
                        }
                        else
                        {
                            Toast.makeText(CreationLotControleDesRetoursScanneeActivity.this, "Emplacement scanné inconnu", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case RETOUR_LOT:
                    String codeRetouner = data.getExtras().getString("code");
                    if(codeRetouner != null)
                    {
                        String numLot = data.getExtras().getString("numLot");
                        String datePeremptionScanner = data.getExtras().getString("datePeremption");
                        if (numLot == null || numLot.contentEquals("")) {
                            Map<String, String> DecoupeMap = OutilsDecodage.decouperGTIN(codeRetouner);
                            if (DecoupeMap.size() > 1) {
                                numLot = DecoupeMap.get(OutilsDecodage.numeroLot);
                                datePeremptionScanner = DecoupeMap.get(OutilsDecodage.dateDePeremption);
                                String[] tab_date = datePeremptionScanner.split("-");
                                datePeremptionScanner = tab_date[2] + "/" + tab_date[1] + "/" + tab_date[0];
                            }
                        }
                        lotEditText.setText(numLot);
                        datePeremptionTextView.setText(datePeremptionScanner);
                    }

                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent onMenuSaveClick_Intent = new Intent();
        CreationLotControleDesRetoursScanneeActivity.this.setResult(RETOUR_LOT, onMenuSaveClick_Intent);
        CreationLotControleDesRetoursScanneeActivity.this.finish();
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
