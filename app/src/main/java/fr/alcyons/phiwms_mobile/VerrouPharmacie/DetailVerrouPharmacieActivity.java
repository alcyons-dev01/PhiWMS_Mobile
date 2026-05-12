package fr.alcyons.phiwms_mobile.VerrouPharmacie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_VerrouPharmacie_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_VerrouPharmacieAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceVerrouPharmacieActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

public class DetailVerrouPharmacieActivity extends ServiceActivity {

    public PH_Preparation phPreparationSelectionne;

    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdaptes;
    public List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList;

    public PH_Preparation_Ligne_VerrouPharmacieAdapter phPreparationLigneVerrouPharmacieAdapter;

    public ListView phPreparationLigneListView;
    public PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolderAModifier;
    public TextView datePeremptionTextView;
    public EditText numeroLotEditText;
    TextView textViewNBPalette;
    TextView textViewNBCaisse;
    TextView textViewNBContainer;
    TextView textViewNBScelle;

    public Integer nbColisTotal = 0;

    PackageManager pm;

    // Permet de lancer l'activity BarcodeCaptureActivity afin de lire un codebarre
    public void decoderCodeBarre(EditText numLot, TextView date, PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder) {
        datePeremptionTextView = date;
        numeroLotEditText = numLot;
        viewHolderAModifier = viewHolder;

        PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte = phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList.indexOf(viewHolderAModifier));
        String designation = phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getDesignation_interne();

        Bundle detailVerrouPharmacieBundle = super.getBundle();
        detailVerrouPharmacieBundle.putBoolean("doitEtreIdentique", true);
        detailVerrouPharmacieBundle.putString("Designation", designation);
        detailVerrouPharmacieBundle.putBoolean("isBoutonSuppressionExistant", true);
        Intent detailVerrouPharmacieIntent = null;
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {

        }

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieActivity.this, ScannerSearchOnlyActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                detailVerrouPharmacieIntent = new Intent(DetailVerrouPharmacieActivity.this, ScannerSearchOnlyActivity.class);

            }
        }

        detailVerrouPharmacieIntent.putExtras(detailVerrouPharmacieBundle);
        DetailVerrouPharmacieActivity.this.startActivityForResult(detailVerrouPharmacieIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_verrou_pharmacie);

        //gestion du package manager
        pm = DetailVerrouPharmacieActivity.this.getPackageManager();

        // Récupération da la préparation selectionné
        phPreparationSelectionne = PH_PreparationOpenHelper.getPH_PreparationByID(db, intent.getExtras().getInt("phPreparationSelectionneID"));

        // Entete
        Depot depot = DepotOpenHelper.getDepotParID(db, phPreparationSelectionne.getDepotDestinataireID());
        ((TextView) findViewById(R.id.depotNom)).setText(depot.getNom());
        String preparationNumero = "N° " + String.valueOf(phPreparationSelectionne.getUID());
        ((TextView) findViewById(R.id.preparationNumero)).setText(preparationNumero);

        Commande commandeCourante = CommandeOpenHelper.getCommandeByID(db, phPreparationSelectionne.getCommande_ID());
        String numeroCommande = "";
        if(commandeCourante != null)
        {
            ((TextView) findViewById(R.id.commandeNumero)).setText("#"+commandeCourante.getNumero());
            ((TextView) findViewById(R.id.fournisseurCommande)).setText(commandeCourante.getFournisseur());
            numeroCommande = commandeCourante.getNumero();
        }
        else
        {
            String[] tab_liste = phPreparationSelectionne.getListe().split(" ");
            numeroCommande = tab_liste[tab_liste.length-1];
            ((TextView) findViewById(R.id.commandeNumero)).setText("#"+numeroCommande);
            ((TextView) findViewById(R.id.fournisseurCommande)).setText("...");
        }

        // Gestion de la listView
        phPreparationLigneListView = (ListView) findViewById(R.id.listeView);
        phPreparationLigneListView.setItemsCanFocus(true);

        // Génération de la liste de PH_Preparation_Ligne_VerrouPharmacieAdapter
        phPreparationLigneVerrouPharmacieAdaptes = new ArrayList<>();

        List<PH_Preparation_Ligne> phPreparationLignes = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationSelectionne);
        for (PH_Preparation_Ligne phPreparationLigne : phPreparationLignes) {
            Produit produit = ProduitOpenHelper.getProduitByID(db, phPreparationLigne.getProduitID());
            stockLotEmplacementLightList = Stock_Lot_EmplacementLightOpenHelper.getAllStock_Lot_EmplacementsByProduitIDEtCommandeNumero(db, produit, numeroCommande);
            if (stockLotEmplacementLightList.size() > 0) {
                for (Stock_Lot_Emplacement_Light stockLotEmplacement : stockLotEmplacementLightList) {

                    int qteRALLot = 0;
                    if (phPreparationLigne.getSuivi_Par_Lot()) {
                        qteRALLot = (int) stockLotEmplacement.getQte();
                    }

                    int qteParLot = (int) stockLotEmplacement.getQte();

                    if (stockLotEmplacement.getQte_Preparer() != 0) {
                        qteParLot = stockLotEmplacement.getQte_Preparer();
                    }

                    String numLot = stockLotEmplacement.getLot();
                    String datePeremption = stockLotEmplacement.getPeremptionDate();
                    String emplacementAdressage = stockLotEmplacement.getEmplacement();

                    int nbColis = recupererNbColis(stockLotEmplacement.getProduit_Code(), qteRALLot);
                    nbColisTotal = nbColisTotal + nbColis;

                    phPreparationLigneVerrouPharmacieAdaptes.add(new PH_Preparation_Ligne_VerrouPharmacie_Adapte(phPreparationLigne.getProduitDesignation(), phPreparationLigne.getProduitReference(), String.valueOf(nbColis), String.valueOf(qteRALLot), String.valueOf(qteParLot), numLot, datePeremption, emplacementAdressage, stockLotEmplacement, phPreparationLigne, phPreparationSelectionne, phPreparationLigne.isSuivi_Par_Serie(), phPreparationLigne.isSerialiser_Reception(), phPreparationLigne.getSerieNumero(), db, false));
                }
            } else {
                String numLot = "";
                String datePeremption = "";
                String emplacementAdressage = "";
                Stock_Lot_Emplacement_Light stockLotEmplacementCourant = null;
                if(phPreparationSelectionne.getListe().contentEquals("ALCYONS_VERROU"))
                    stockLotEmplacementCourant = new Stock_Lot_Emplacement_Light(phPreparationLigne.getQte_preparer(), "", "", phPreparationLigne.getEmplacementParDefaut(), phPreparationSelectionne.getDepotDestinataireReference(), phPreparationLigne.getZoneDepot(), phPreparationLigne.getProduitID(), phPreparationLigne.getQte_preparer(), "");
                else
                    stockLotEmplacementCourant = new Stock_Lot_Emplacement_Light(0, "", "", "", "", "", 0, 0, "");

                if(phPreparationSelectionne.getListe().contentEquals("ALCYONS_VERROU"))
                    phPreparationLigneVerrouPharmacieAdaptes.add(new PH_Preparation_Ligne_VerrouPharmacie_Adapte(phPreparationLigne.getProduitDesignation(), phPreparationLigne.getProduitReference(), String.valueOf(0), String.valueOf(phPreparationLigne.getQte_preparer()), String.valueOf(phPreparationLigne.getQte_preparer()), numLot, datePeremption, emplacementAdressage, stockLotEmplacementCourant, phPreparationLigne, phPreparationSelectionne, phPreparationLigne.isSuivi_Par_Serie(), phPreparationLigne.isSerialiser_Reception(), phPreparationLigne.getSerieNumero(), db, false));
                else
                    phPreparationLigneVerrouPharmacieAdaptes.add(new PH_Preparation_Ligne_VerrouPharmacie_Adapte(phPreparationLigne.getProduitDesignation(), phPreparationLigne.getProduitReference(), "", "", "", numLot, datePeremption, emplacementAdressage, stockLotEmplacementCourant, phPreparationLigne, phPreparationSelectionne, phPreparationLigne.isSuivi_Par_Serie(), phPreparationLigne.isSerialiser_Reception(), phPreparationLigne.getSerieNumero(), db, false));
            }
        }

        ((TextView) findViewById(R.id.nbColisTotal)).setText(String.valueOf(nbColisTotal));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                afficherAlerteConfirmationRetour(DetailVerrouPharmacieActivity.this, LayoutInflater.from(DetailVerrouPharmacieActivity.this), DetailVerrouPharmacieActivity.super.getBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Collections.sort(phPreparationLigneVerrouPharmacieAdaptes, new Comparator<PH_Preparation_Ligne_VerrouPharmacie_Adapte>() {
            @Override
            public int compare(PH_Preparation_Ligne_VerrouPharmacie_Adapte o1, PH_Preparation_Ligne_VerrouPharmacie_Adapte o2) {
                return o1.getDesignation().compareTo(o2.getDesignation());
            }
        });

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        phPreparationLigneVerrouPharmacieAdapter = new PH_Preparation_Ligne_VerrouPharmacieAdapter(DetailVerrouPharmacieActivity.this, db, phPreparationLigneVerrouPharmacieAdaptes);

        // Permet de mettre à jour les données se trouvant pas dans l'adapter mais dépend de lui
        phPreparationLigneVerrouPharmacieAdapter.setOnDataChangeListener(new PH_Preparation_Ligne_VerrouPharmacieAdapter.OnDataChangeListener() {
            public void onDataChanged(int quantitéAvant, int quantitéAprès, boolean deverrouillee) {

                Integer nbColisTotalVerrouille = Integer.parseInt(((TextView) findViewById(R.id.nbColisTotal)).getText().toString());
                Integer nbColisTotalDeverrouille = Integer.parseInt(((TextView) findViewById(R.id.nbColisTotalReceptionner)).getText().toString());

                if(deverrouillee)
                {
                    nbColisTotalVerrouille = nbColisTotalVerrouille - quantitéAprès;
                    nbColisTotalDeverrouille = nbColisTotalDeverrouille + quantitéAprès;
                }
                else
                {
                    nbColisTotalVerrouille = nbColisTotalVerrouille + quantitéAvant;
                    nbColisTotalDeverrouille = nbColisTotalDeverrouille - quantitéAvant;
                }

                ((TextView) findViewById(R.id.nbColisTotal)).setText(String.valueOf(nbColisTotalVerrouille));
                ((TextView) findViewById(R.id.nbColisTotalReceptionner)).setText(String.valueOf(nbColisTotalDeverrouille));

            }
        });

        phPreparationLigneListView.setAdapter(phPreparationLigneVerrouPharmacieAdapter);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = Calendar.getInstance().getTime();
        boolean perime_present = false;
        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte : phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList) {
            PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.indexOf(phPreparationLigneVerrouPharmacieAdapte));
            Date date_peremption = null;
            try {
                date_peremption = dateDecodeur.parse(viewHolder.valeurDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(date_peremption != null)
            {
                if(dateDuJour.after(date_peremption))
                {
                    perime_present = true;
                    break;
                }
            }
        }

        if(perime_present)
        {
            //Alerte.afficherAlerte(DetailVerrouPharmacieActivity.this, "Erreur", "Un produit de la liste est périmé", "alerte");
        }

        ((LinearLayout) findViewById(R.id.btnValiderVerrou_LL)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_ActionContenant();
            }
        });

        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    String codeComplet = data.getStringExtra("code");
                    if(codeComplet != null && !codeComplet.contentEquals(""))
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (gs1Decoupe.size() != 1) {
                            if (datePeremptionTextView != null && numeroLotEditText != null)
                            {
                                PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte = phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                Produit produitScanner = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                if(produitScanner != null)
                                {
                                    if(produitScanner.getID_produit() == phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getID_produit())
                                    {
                                        DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                        Date date = new Date();

                                        try {
                                            date = dateFormat1.parse(gs1Decoupe.get(OutilsDecodage.dateDePeremption));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        String dateFinale = dateFormat2.format(date);

                                        datePeremptionTextView.setText(dateFinale);
                                        numeroLotEditText.setText(gs1Decoupe.get(OutilsDecodage.numeroLot));


                                        viewHolderAModifier.valeurDate = dateFinale;
                                        viewHolderAModifier.valeurLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                        mettreAJourPHPrepLigne(viewHolderAModifier, phPreparationLigneVerrouPharmacieAdapte);
                                    }
                                    else
                                    {
                                        Toast toast = Toast.makeText(DetailVerrouPharmacieActivity.this, "Le produit scanné ne correspond pas au produit attendu", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                                else
                                {
                                    Toast toast = Toast.makeText(DetailVerrouPharmacieActivity.this, "Le produit scanné inconnu", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                            else
                            {
                                Toast toast = Toast.makeText(DetailVerrouPharmacieActivity.this, "Le produit scanné ne correspond pas au produit attendu", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                        else
                        {
                            Toast toast = Toast.makeText(DetailVerrouPharmacieActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    else
                    {

                    }
                    invalidateOptionsMenu();
                    break;
            }
        }
    }

    // Permet de calculer les nombre de colis en fonction du conditionnement du produit
    public int recupererNbColis(int produitID, double qte) {
        int nbColis = 0;

        int conditionnementAchat = 0;
        int quantite = (int) qte;

        if (produitID != 0) {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, produitID);
            conditionnementAchat = produitCorrespondant.getCond_achat();
            if (conditionnementAchat == 0) {
                conditionnementAchat = (int) produitCorrespondant.getCond_distrib();
            }
        }
        if (quantite != 0 && conditionnementAchat != 0) {
            nbColis = quantite / conditionnementAchat;
            nbColis = (int) Math.ceil(nbColis);
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }

    // Permet de mettre à jour un PH_Preparation_Ligne
    public void mettreAJourPHPrepLigne(PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder, PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte) {
        PH_Preparation_Ligne phPreparationLigne = phPreparationLigneVerrouPharmacieAdapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLotEmplacementLight = phPreparationLigneVerrouPharmacieAdapte.getStockLotEmplacement();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

        stockLotEmplacementLight.setLot(viewHolder.valeurLot);
        stockLotEmplacementLight.setQte_Preparer(viewHolder.valeurQteParLot);
        try {
            Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
            stockLotEmplacementLight.setPeremptionDate(dateFormat.format(dateFournie));
            phPreparationLigneVerrouPharmacieAdapte.setPeremptionDate(dateFormat.format(dateFournie));
            phPreparationLigne.setPeremptionDate(dateFormat.format(dateFournie));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacementLight);

        phPreparationLigneVerrouPharmacieAdapte.setNumLot(viewHolder.valeurLot);
        phPreparationLigneVerrouPharmacieAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));

        phPreparationLigne.setLotNumero(viewHolder.valeurLot);
        phPreparationLigne.setQte_RAL(viewHolder.valeurQteParLot);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPreparationLigne);
    }


    public void onClick_ActionContenant() {

        boolean deverouiller = true;

        for(int i = 0; i < phPreparationLigneVerrouPharmacieAdapter.viewHolderList.size(); i++)
        {
            PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(i);

            String statut = viewHolder.valeurStatut;

            if(statut.contentEquals("Verrouillé"))
            {
                deverouiller = false;
                break;
            }
        }

        if(deverouiller)
        {
            final HashMap<Integer, Integer> resultat = new HashMap<>();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = this.getLayoutInflater().inflate(R.layout.alerte_preparation, null);

            LinearLayout LinearPalette = (LinearLayout) view.findViewById(R.id.LinearPalette);
            LinearLayout LinearColis = (LinearLayout) view.findViewById(R.id.LinearColis);
            LinearLayout LinearContainer = (LinearLayout) view.findViewById(R.id.LinearContainer);
            LinearLayout LinearScelle = (LinearLayout) view.findViewById(R.id.LinearScelle);
            textViewNBPalette = (TextView) view.findViewById(R.id.nbPaletteSelectionne);
            textViewNBCaisse = (TextView) view.findViewById(R.id.nbColisSelectionne);
            textViewNBContainer = (TextView) view.findViewById(R.id.nbContainerSelectionne);
            textViewNBScelle = (TextView) view.findViewById(R.id.nbScelleSelectionne);

            //on calcule le nombre de colis
            int nbColis = 0;
            for(PH_Preparation_Ligne_VerrouPharmacie_Adapte courant : phPreparationLigneVerrouPharmacieAdaptes)
            {
                PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.indexOf(courant));
                PH_Preparation_Ligne ph_preparation_ligne = courant.getPhPreparationLigne();
                int quantitePreparer = (viewHolder.valeurQteParLot == -1 ? viewHolder.valeurQteDemander : viewHolder.valeurQteParLot);
                if(quantitePreparer > 0)
                {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne.getProduitID());
                    int conditionnement = (int) produit.getCond_distrib();
                    if(conditionnement == 0)
                    {
                        conditionnement = 1;
                    }
                    int nbColisProduit = (int) ((int) quantitePreparer / (int) conditionnement);

                    if(nbColisProduit == 0)
                    {
                        nbColisProduit = 1;
                    }
                    nbColis = nbColis + nbColisProduit;
                }
            }

            textViewNBCaisse.setText(String.valueOf(nbColis));

            /* Gestion des numbers pickers */

            //gestion des colis
            textViewNBCaisse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    String title = "Saisir le nombre de colis";
                    String message = "Nombre de colis : ";
                    int maxValue = 20;
                    int value = 0;

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int qteAprès = aNumberPicker.getValue();
                            textViewNBCaisse.setText(String.valueOf(qteAprès));
                            InputMethodManager imm = (InputMethodManager) DetailVerrouPharmacieActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();
                        }
                    };

                    Alerte.afficherAlerteNumberPicker(DetailVerrouPharmacieActivity.this, title, message, value, maxValue, onClickListener);
                }
            });


            //gestion des palettes
            LinearPalette.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    textViewNBPalette.performClick();
                }
            });

            textViewNBPalette.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    String title = "Saisir le nombre de palette";
                    String message = "Nombre de palettes : ";
                    int maxValue = 15;
                    int value = 0;

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int qteAprès = aNumberPicker.getValue();
                            textViewNBPalette.setText(String.valueOf(qteAprès));

                            dialog.dismiss();
                        }
                    };

                    Alerte.afficherAlerteNumberPicker(DetailVerrouPharmacieActivity.this, title, message, value, maxValue, onClickListener);
                }
            });

            //gestion des container
            LinearContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    textViewNBContainer.performClick();
                }
            });

            textViewNBContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    String title = "Saisir le nombre de container";
                    String message = "Nombre de container : ";
                    int maxValue = 15;
                    int value = 0;

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int qteAprès = aNumberPicker.getValue();
                            textViewNBContainer.setText(String.valueOf(qteAprès));

                            dialog.dismiss();
                        }
                    };

                    Alerte.afficherAlerteNumberPicker(DetailVerrouPharmacieActivity.this, title, message, value, maxValue, onClickListener);
                }
            });

            //gestion des scelles
            LinearScelle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    textViewNBScelle.performClick();
                }
            });

            textViewNBScelle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Ouvre une boite de dialogue avec un edittext
                    String textscelle = Alerte.afficherAlerteEditText(DetailVerrouPharmacieActivity.this, "Numéro de scellé", "Saisir un numéro de scellé");
                    textViewNBScelle.setText(textscelle);
                }
            });

            ImageView ok = (ImageView) view.findViewById(R.id.ok);
            ImageView annuler = (ImageView) view.findViewById(R.id.annuler);
            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int nbPalette = 0;
                    if(!textViewNBPalette.getText().toString().contentEquals(""))
                        nbPalette = Integer.parseInt(textViewNBPalette.getText().toString());

                    int nbCaisse = 0;
                    if(!textViewNBCaisse.getText().toString().contentEquals(""))
                        nbCaisse = Integer.parseInt(textViewNBCaisse.getText().toString());

                    int Conteneur_NB = 0;
                    if(!textViewNBContainer.getText().toString().contentEquals(""))
                        Conteneur_NB = Integer.parseInt(textViewNBContainer.getText().toString());

                    String numero_scelle = "";
                    if(textViewNBScelle.getText().toString() != null)
                        numero_scelle = textViewNBScelle.getText().toString();

                    phPreparationSelectionne.setColisNB(nbCaisse);
                    phPreparationSelectionne.setPaletteNB(nbPalette);
                    phPreparationSelectionne.setConteneur_NB(Conteneur_NB);
                    phPreparationSelectionne.setNumero_scelle(numero_scelle);
                    PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationSelectionne);
                    alertDialog.dismiss();
                    validerVerrou();
                }
            });

            annuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        }
        else
        {
            Alerte.afficherAlerte(DetailVerrouPharmacieActivity.this, "Alerte", "Veuillez déverrouiller toutes les lignes", "alerte");
        }
    }

    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater, final Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Vous allez quitter le verrou, confirmez vous ?");
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                retourService(bundle);
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    @Override
    public void retourService(final Bundle bundle)
    {
        Intent detailVerrouIntent = new Intent(DetailVerrouPharmacieActivity.this, ServiceVerrouPharmacieActivity.class);
        Bundle detailVerrouBundle = super.getBundle();
        detailVerrouIntent.putExtras(detailVerrouBundle);
        DetailVerrouPharmacieActivity.this.startActivity(detailVerrouIntent);
        DetailVerrouPharmacieActivity.this.finish();
    }

    private void validerVerrou()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = Calendar.getInstance().getTime();

        boolean confirmation;
        confirmation = Alerte.afficherAlerte(DetailVerrouPharmacieActivity.this, "Validation", "Êtes-vous sur de vouloir valider ?", "OuiNon");

        if (confirmation) {

            //création de l'action
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat parseHeureFormat = new SimpleDateFormat("HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            String only_date = parseDateFormat.format(date);
            String only_heure = parseHeureFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", phPreparationSelectionne.getUID(), "", "Verrou Pharmacie");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur

            int compteurReussite = 0;

            for (PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte : phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList) {
                PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder = phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.indexOf(phPreparationLigneVerrouPharmacieAdapte));
                PH_Preparation_Ligne phPreparationLigne = phPreparationLigneVerrouPharmacieAdapte.getPhPreparationLigne();
                Stock_Lot_Emplacement_Light stockLotEmplacementLight = phPreparationLigneVerrouPharmacieAdapte.getStockLotEmplacement();

                if(phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getID_produit() == phPreparationLigne.getProduitID() && !phPreparationLigneVerrouPharmacieAdapte.getNumLot().contentEquals(phPreparationLigne.getLotNumero()) && !phPreparationLigne.getLotNumero().contentEquals(""))
                {
                    //duplication ph_preparation_ligne
                    PH_Preparation_Ligne phpreparationligneold = new PH_Preparation_Ligne(phPreparationLigne);
                    phPreparationLigne = new PH_Preparation_Ligne(phpreparationligneold);
                    Random randomactionPhpreparationLigne = new Random();
                    int phPreparationLigneUID = randomactionPhpreparationLigne.nextInt();
                    if(phPreparationLigneUID > 0)
                        phPreparationLigneUID= phPreparationLigneUID*-1;

                    phPreparationLigne.set_UID(phPreparationLigneUID);
                    phPreparationLigne.setPhiMR4UUID(phPreparationLigneUID);
                    PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, phPreparationLigne);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, phPreparationLigne.getPhiMR4UUID(), phPreparationLigne.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }

                stockLotEmplacementLight.setLot(viewHolder.valeurLot);
                stockLotEmplacementLight.setQte_Preparer(viewHolder.valeurQteParLot == -1 ? viewHolder.valeurQteDemander : viewHolder.valeurQteParLot);
                Date date_peremption = null;
                try {
                    date_peremption = dateDecodeur.parse(viewHolder.valeurDate);
                    stockLotEmplacementLight.setPeremptionDate(dateFormat.format(date_peremption));
                    phPreparationLigneVerrouPharmacieAdapte.setPeremptionDate(dateFormat.format(date_peremption));
                    phPreparationLigne.setPeremptionDate(dateFormat.format(date_peremption));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(date_peremption != null)
                {
                    if(dateDuJour.after(date_peremption))
                    {
                        Alerte.afficherAlerte(DetailVerrouPharmacieActivity.this, "Erreur", "Un produit de la liste est périmé", "alerte");
                        return;
                    }
                }

                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacementLight);

                phPreparationLigneVerrouPharmacieAdapte.setNumLot(viewHolder.valeurLot);

                phPreparationLigne.setLotNumero(viewHolder.valeurLot);
                phPreparationLigne.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());
                phPreparationLigne.setQte_APreparer(stockLotEmplacementLight.getQte_Preparer());
                phPreparationLigne.setQte_RAL(stockLotEmplacementLight.getQte_Preparer());
                phPreparationLigne.setEmplacementParDefaut(stockLotEmplacementLight.getEmplacement());
                long rowID = PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPreparationLigne);
                if (rowID != -1) {
                    compteurReussite++;
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, phPreparationLigne.getPhiMR4UUID(), phPreparationLigne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussite = 0;
                }

                //gestiond des action utilisateur ligne pour le verrou pharmacie
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Preparation_Ligne", phPreparationLigne.get_UID(), "", 0, phPreparationLigne.getQte_RAL(), phPreparationLigne.getProduitDesignation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }

            if (compteurReussite == phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.size()) {
                phPreparationSelectionne.setStatut(getString(R.string.statutDéverrouillée));
                phPreparationSelectionne.setPharmacien_userID(utilisateurConnecte.getId());
                phPreparationSelectionne.setDelivranceValider_Par(utilisateurConnecte.getId());
                phPreparationSelectionne.setDelivranceValider_Le(only_date);
                phPreparationSelectionne.setDelivranceValider_A(only_heure);

                long rowID = PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationSelectionne);
                if (rowID != -1) {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, phPreparationSelectionne.getPhiMR4UUID(), phPreparationSelectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussite = 0;
                }
            }

            if (compteurReussite != phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList.size()) {
                Alerte.afficherAlerte(DetailVerrouPharmacieActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                DetailVerrouPharmacieActivity.this.finish();
                return;
            }

            Toast.makeText(DetailVerrouPharmacieActivity.this, "Préparation déverrouillée", Toast.LENGTH_SHORT).show();

            if (statutConnexion) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailVerrouPharmacieActivity.this, db, utilisateurConnecte, true);
            }
            onBackPressed();
        }
    }
}
