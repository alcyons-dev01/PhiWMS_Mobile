package fr.alcyons.phiwms_mobile.Inventaire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerInventaireActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoInventaire;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.InventaireOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Inventaire_Ligne_TempOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Inventaire;
import fr.alcyons.phiwms_mobile.Classes.Inventaire_Ligne_Temp;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.DetailInventaireAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceInventaireGeneralActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceInventairePartielActivity;

public class DetailInventaire_V2Activity extends ServiceAvecConnexionActivity {
    Context context;
    Inventaire inventaireCourant;
    List<Inventaire_Ligne_Temp> inventaireLigneTempList;
    String zoneCourante;
    ListView inventaireListView;
    DetailInventaireAdapter adapter;
    MenuItem valider_item;
    PackageManager pm;
    Depot depotCourant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_inventaire);
        context = DetailInventaire_V2Activity.this;
        pm = DetailInventaire_V2Activity.this.getPackageManager();

        // Récupération de l'inventaire courant depuis les extras de l'intent
        inventaireCourant = InventaireOpenHelper.getInventaireById(db, intent.getExtras().getInt("inventaireId"));
        zoneCourante = intent.getExtras().getString("zoneSelectionne");
        depotCourant = DepotOpenHelper.getDepotParReference(db, intent.getExtras().getString("depotSelectionne"));

        ((TextView) findViewById(R.id.intitule)).setText(inventaireCourant.getObjet());
        ((TextView) findViewById(R.id.zone)).setText(zoneCourante);

        /*if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
        {
            ((Button) findViewById(R.id.alcyonsSimulerInventaire_B)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.alcyonsSimulerInventaire_B)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getAllInventaireLigneTempByInventaireEtZoneEtDepot(db, inventaireCourant.getInventaire_ID(), zoneCourante, depotCourant.getDepot_Reference());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateDuJour = sdf.format(new Date());
                    for(Inventaire_Ligne_Temp inventaireLigneTemp : inventaireLigneTempList)
                    {
                        inventaireLigneTemp.setStockPhysique(10);
                        inventaireLigneTemp.setInventaireDate(dateDuJour);
                        Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, inventaireLigneTemp);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, inventaireLigneTemp.getPhiMR4UUID(), inventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                    }
                    ElementASynchroniserOpenHelper.toutSynchroniser(DetailInventaire_V2Activity.this, db, utilisateurConnecte, false);
                    onResume();
                }
            });
        }*/
        inventaireListView = (ListView) findViewById(R.id.listeView);
        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceInventaire_Intent = null;
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google")) {
                    serviceInventaire_Intent = new Intent(DetailInventaire_V2Activity.this, ScannerInventaireActivity.class);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
                    {
                        serviceInventaire_Intent = new Intent(DetailInventaire_V2Activity.this, ScannerPhotoInventaire.class);
                    }
                    else
                    {
                        serviceInventaire_Intent = new Intent(DetailInventaire_V2Activity.this, ScannerInventaireActivity.class);
                    }
                }

                Bundle serviceInventaire_Bundle = DetailInventaire_V2Activity.super.getBundle();
                serviceInventaire_Bundle.putInt("inventaireID", inventaireCourant.getInventaire_ID());
                serviceInventaire_Bundle.putString("zoneSelectionne", zoneCourante);
                serviceInventaire_Bundle.putSerializable("inventaireLigneTempList", (Serializable) inventaireLigneTempList);
                serviceInventaire_Bundle.putInt("produitId", 0);
                serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
                DetailInventaire_V2Activity.this.startActivityForResult(serviceInventaire_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getDistinctInventaireLigneTempByInventaireEtZoneEtDepot(db, inventaireCourant.getInventaire_ID(), zoneCourante, depotCourant.getDepot_Reference());

        if (adapter == null) {
            adapter = new DetailInventaireAdapter(DetailInventaire_V2Activity.this, inventaireLigneTempList, db);
            inventaireListView.setAdapter(adapter);
        } else {
            adapter.updateList(inventaireLigneTempList);
            adapter.notifyDataSetChanged();
        }

        invalidateOptionsMenu();
        verificationEtatInventaire();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:


                    onResume();
                    break;
            }
        }
        invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);

        valider_item = menu.findItem(R.id.menuSaveCircle).setVisible(true);
        verificationEtatInventaire();
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Désignation référence, inventaire non complet,...");
        valider_item.setOnMenuItemClickListener(menuItem -> {
            Random randomaction = new Random();
            int actionId = randomaction.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDestruction =new Date();
            String date_string = parseFormat.format(dateDestruction);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", inventaireCourant.getInventaire_ID(), "", "Inventaire Partiel à traiter");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailInventaire_V2Activity.this, db, utilisateurConnecte, false);
            DetailInventaire_V2Activity.this.finish();
            return true;
        });

        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if(inventaireCourant.getObjet().contentEquals("Inventaire Général"))
        {
            intent = new Intent(DetailInventaire_V2Activity.this, ServiceInventaireGeneralActivity.class);
        }
        else
        {
            intent = new Intent(DetailInventaire_V2Activity.this, ServiceInventairePartielActivity.class);
        }

        Bundle extras = new Bundle();
        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        intent.putExtras(extras);
        DetailInventaire_V2Activity.this.startActivity(intent);
        DetailInventaire_V2Activity.this.finish();
    }
    public void verificationEtatInventaire() {
        List<Integer> produitIdList = new ArrayList<>();
        boolean inventaireComplet = false;
        int nbReferenceInventorie = 0;
        for (Inventaire_Ligne_Temp ligne : inventaireLigneTempList) {
            if(!produitIdList.contains(ligne.getProduitID())) {
                produitIdList.add(ligne.getProduitID());
                int qteStockPhysique = Inventaire_Ligne_TempOpenHelper.getQteInventorieByInventaireProduitZoneDepot(db, ligne.getInventaire_ID(), ligne.getProduitID(), ligne.getZone(), ligne.getDepotReference());
                if(qteStockPhysique >= 0)
                    nbReferenceInventorie++;
            }
        }

        if(valider_item != null)
        {
            valider_item.setVisible(nbReferenceInventorie == produitIdList.size());
            inventaireComplet = nbReferenceInventorie == produitIdList.size();
        }

        ((TextView) findViewById(R.id.nbReferenceInventorie_TV)).setText(String.valueOf(nbReferenceInventorie));
        ((TextView) findViewById(R.id.nbReferenceTotal_TV)).setText(String.valueOf(produitIdList.size()));
        ((ProgressBar) findViewById(R.id.progressBarInventaire_PB)).setMax(produitIdList.size());
        ((ProgressBar) findViewById(R.id.progressBarInventaire_PB)).setProgress(nbReferenceInventorie);

        if(inventaireComplet)
        {
            ((ProgressBar) findViewById(R.id.progressBarInventaire_PB)).setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.vert)));
        }
    }
    private int getIdInventaireLigneTemp() {
        Random randominventairelignetemp = new Random();
        int inventairelignetempid = randominventairelignetemp.nextInt();
        if(inventairelignetempid > 0)
            inventairelignetempid= inventairelignetempid*-1;

        return inventairelignetempid;
    }
    private String getDateDuJour() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateDuJour = sdf.format(new Date());

        return dateDuJour;
    }
    public void onClickLigne(int position) {

        Inventaire_Ligne_Temp courant = inventaireLigneTempList.get(position);
        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, courant.getProduitID());
        final int[] conditionnement = {produitCourant.getCond_achat()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.alerte_inventaire_comptage, null);

        TextView designationReference_TV = view.findViewById(R.id.designationReference_TV);
        TextView zoneLot_TV = view.findViewById(R.id.zoneLot_TV);
        TextView emplacementLot_TV = view.findViewById(R.id.emplacementLot_TV);
        TextView textCartonFermer_TV = view.findViewById(R.id.textCartonFermer_TV);
        EditText numeroLot_ET = view.findViewById(R.id.numeroLot_ET);
        EditText quantiteComptee_ET = view.findViewById(R.id.quantiteComptee_ET);
        LinearLayout layoutCartonFermer_LL = view.findViewById(R.id.layoutCartonFermer_LL);
        LinearLayout layoutCartonOuvert_LL = view.findViewById(R.id.layoutCartonOuvert_LL);
        LinearLayout layoutMoins_LL = view.findViewById(R.id.layoutMoins_LL);
        LinearLayout layoutPlus_LL = view.findViewById(R.id.layoutPlus_LL);
        LinearLayout layoutValider_LL = view.findViewById(R.id.layoutValider_LL);
        LinearLayout layout_gestion_conditionnement_LL = view.findViewById(R.id.layout_gestion_conditionnement_LL);
        LinearLayout linearLayoutLot = view.findViewById(R.id.linearLayoutLot);
        LinearLayout linearLayoutPeremption = view.findViewById(R.id.linearLayoutPeremption);
        ImageView quitterModale_IV = view.findViewById(R.id.quitterModale_IV);

        Spinner spinnerMoisDatePeremption_SP = view.findViewById(R.id.selecteurDateMois_SP);
        ArrayAdapter<String> adapterMoisPeremption = new ArrayAdapter<>(DetailInventaire_V2Activity.this, R.layout.spinner_date_item, getListeMoisDatePicker());
        adapterMoisPeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoisDatePeremption_SP.setAdapter(adapterMoisPeremption);

        Spinner spinnerAnneeDatePeremption_SP = view.findViewById(R.id.selecteurDateAnnee_SP);
        ArrayAdapter<String> adapterAnneePeremption = new ArrayAdapter<>(DetailInventaire_V2Activity.this, R.layout.spinner_date_item, getListeAnneeDatePicker());
        adapterAnneePeremption.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnneeDatePeremption_SP.setAdapter(adapterAnneePeremption);
        spinnerAnneeDatePeremption_SP.setSelection(3);

        designationReference_TV.setText(courant.getDesignation());
        zoneLot_TV.setText(courant.getZone());
        emplacementLot_TV.setText(courant.getEmplacement());
        numeroLot_ET.setText("");
        textCartonFermer_TV.setText(textCartonFermer_TV.getText()+"(x"+produitCourant.getCond_achat()+")");

        //on gère le suivi du produit
        if(!produitCourant.isSuivi_Lot())
        {
            linearLayoutLot.setVisibility(View.GONE);
            numeroLot_ET.setText("LOT NON TRACE");
        }
        else
            numeroLot_ET.setText("");

        if(!produitCourant.isPeremption())
        {
            linearLayoutPeremption.setVisibility(View.GONE);
        }

        if(produitCourant.getCond_achat() == 1) {
            layout_gestion_conditionnement_LL.setVisibility(View.GONE);
        }

        quantiteComptee_ET.setText("0");

        layoutCartonFermer_LL.setOnClickListener(view5 -> {
            conditionnement[0] = produitCourant.getCond_achat();
            layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailInventaire_V2Activity.this, R.color.blanc)));
            layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailInventaire_V2Activity.this, R.color.vertTransparent)));
        });

        layoutCartonOuvert_LL.setOnClickListener(view4 -> {
            conditionnement[0] = 1;
            layoutCartonOuvert_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailInventaire_V2Activity.this, R.color.vertTransparent)));
            layoutCartonFermer_LL.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(DetailInventaire_V2Activity.this, R.color.blanc)));
        });

        layoutPlus_LL.setOnClickListener(view3 -> {
            int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
            qteActuelle += conditionnement[0];
            quantiteComptee_ET.setText(String.valueOf(qteActuelle));
        });

        layoutMoins_LL.setOnClickListener(view2 -> {
            int qteActuelle = Integer.parseInt(quantiteComptee_ET.getText().toString());
            qteActuelle -= conditionnement[0];
            if(qteActuelle < 0)
                qteActuelle = 0;
            quantiteComptee_ET.setText(String.valueOf(qteActuelle));
        });

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        layoutValider_LL.setOnClickListener(view1 -> {
            //vérification du numéro de lot et de la date de péremption
            String numeroLotTemp = numeroLot_ET.getText().toString().trim();
            String anneeSelection = spinnerAnneeDatePeremption_SP.getSelectedItem().toString();
            String moisSelection = spinnerMoisDatePeremption_SP.getSelectedItem().toString();
            String dateExpirationLotTemp = getDateDepuisMoisAnnee(moisSelection, anneeSelection);

            if(numeroLotTemp.contentEquals(""))
            {
                Alerte.afficherAlerteInformation(DetailInventaire_V2Activity.this, getLayoutInflater(),"Numéro de lot", "Veuillez saisir un numéro de lot", false, false);
                numeroLot_ET.requestFocus();
            }
            else {
                if(numeroLotTemp.contentEquals("LOT NON TRACE")) {
                    dateExpirationLotTemp = "0000-00-00";
                }
                else {
                    String[] dateParts = dateExpirationLotTemp.split("/");
                    if(dateParts.length == 3)
                        dateExpirationLotTemp = dateParts[2]+"-"+dateParts[1]+"-"+dateParts[0];
                }

                Inventaire_Ligne_Temp nouvelInventaireLigneTemp = Inventaire_Ligne_TempOpenHelper.getInventaireLigneByProduitLotPeremptionZoneDepot(db,courant.getInventaire_ID(), courant.getProduitID(), numeroLotTemp, dateExpirationLotTemp, courant.getZone(), depotCourant.getDepot_Reference());

                if(nouvelInventaireLigneTemp == null) {
                    nouvelInventaireLigneTemp = new Inventaire_Ligne_Temp(courant);
                    nouvelInventaireLigneTemp.set_UID(getIdInventaireLigneTemp());
                    nouvelInventaireLigneTemp.setLot(numeroLotTemp);
                    nouvelInventaireLigneTemp.setPeremptionDate(dateExpirationLotTemp);
                    nouvelInventaireLigneTemp.setStockPhysique(Integer.parseInt(quantiteComptee_ET.getText().toString()));
                    nouvelInventaireLigneTemp.setInventaireDate(getDateDuJour());
                    Inventaire_Ligne_TempOpenHelper.insererUnInventaire_Ligne_TempEnBDD(db, nouvelInventaireLigneTemp);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, nouvelInventaireLigneTemp.getPhiMR4UUID(), nouvelInventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    ElementASynchroniserOpenHelper.toutSynchroniser(DetailInventaire_V2Activity.this, db, utilisateurConnecte, false);
                }
                else
                {
                    nouvelInventaireLigneTemp.setStockPhysique(Integer.parseInt(quantiteComptee_ET.getText().toString()));
                    nouvelInventaireLigneTemp.setInventaireDate(getDateDuJour());
                    Inventaire_Ligne_TempOpenHelper.mettreAJourInventaireLigneTemp(db, nouvelInventaireLigneTemp);

                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Inventaire_Ligne_TempOpenHelper.Constantes.TABLE_INVENTAIRE_LIGNE_TEMP, nouvelInventaireLigneTemp.getPhiMR4UUID(), nouvelInventaireLigneTemp.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                    ElementASynchroniserOpenHelper.toutSynchroniser(DetailInventaire_V2Activity.this, db, utilisateurConnecte, false);
                }

                inventaireLigneTempList = Inventaire_Ligne_TempOpenHelper.getDistinctInventaireLigneTempByInventaireEtZoneEtDepot(db, inventaireCourant.getInventaire_ID(), zoneCourante, depotCourant.getDepot_Reference());
                verificationEtatInventaire();
                alertDialog.dismiss();
            }
        });

        quitterModale_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void ajoutManuel(int position)
    {
        onClickLigne(position);
    }

    public void versDetail(int position)
    {
        Inventaire_Ligne_Temp ligneSelectionnee = inventaireLigneTempList.get(position);
        int qteStockPhysique = Inventaire_Ligne_TempOpenHelper.getQteInventorieByInventaireProduitZoneDepot(db, ligneSelectionnee.getInventaire_ID(), ligneSelectionnee.getProduitID(), ligneSelectionnee.getZone(), ligneSelectionnee.getDepotReference());
        if(qteStockPhysique >= 0)
        {
            Intent serviceInventaire_Intent = new Intent(DetailInventaire_V2Activity.this, ListeLotInventaireActivity.class);
            Bundle serviceInventaire_Bundle = DetailInventaire_V2Activity.super.getBundle();
            serviceInventaire_Bundle.putInt("inventaireId", inventaireCourant.getInventaire_ID());
            serviceInventaire_Bundle.putString("zoneSelectionne", zoneCourante);
            serviceInventaire_Bundle.putInt("produitId", ligneSelectionnee.getProduitID());
            serviceInventaire_Bundle.putString("depotSelectionne", depotCourant.getDepot_Reference());
            serviceInventaire_Intent.putExtras(serviceInventaire_Bundle);
            DetailInventaire_V2Activity.this.startActivity(serviceInventaire_Intent);
        }
        else
        {
            onClickLigne(position);
        }
    }
}
