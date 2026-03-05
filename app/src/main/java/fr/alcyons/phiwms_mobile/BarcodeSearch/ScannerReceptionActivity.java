package fr.alcyons.phiwms_mobile.BarcodeSearch;

import static fr.alcyons.phiwms_mobile.OutilsSerialisation.WS_PKI.checkApiAsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

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
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeEmplacementCreationActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.ListeZoneCreationActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ScannerReceptionActivity extends ServiceActivity {
    //Gestion des réceptions
    int receptionID;
    Commande commandeCourante;
    Produit produitCourant;

    // GRAPHIQUE
    EditText EditTextScanee;
    TextView numCommande;
    TextView depot;

    Depot_Emplacement emplacement_courant;
    Serialisation serialisation;
    boolean serialisationActive;
    String tempCodeScanne;
    List<PH_Reliquat> listPH_Reliquat;
    PH_Reliquat reliquatCourant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_reception);

        // INTENT
        intent = ScannerReceptionActivity.this.getIntent();
        receptionID = intent.getExtras().getInt("ReceptionID");

        // GRAPHIQUE
        EditTextScanee = (EditText) findViewById(R.id.EditTextScanee);
        numCommande = (TextView) findViewById(R.id.numPreparation);
        depot = (TextView) findViewById(R.id.depot);
        EditTextScanee.setBackground(getResources().getDrawable(R.drawable.background_cadre_vert_fond_noir));

        commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
        numCommande.setText("#" + commandeCourante.getNumero());
        depot.setText(commandeCourante.getFournisseur());
        listPH_Reliquat = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeCourante.getNumero());

        //SERIALISATION
        serialisation = new Serialisation(ScannerReceptionActivity.this, db, utilisateurConnecte);
        checkApiAsync(this).thenAccept(success -> {
            serialisationActive = success;
            if(success)
            {
                boolean serialisationactive = false;
                for(PH_Reliquat reliquat : listPH_Reliquat)
                {
                    if(reliquat.isSuiviParSerieActif() && reliquat.isSerialiserReception()) {
                        serialisationactive = true;
                        break;
                    }
                }

                if(serialisationactive)
                    ((ImageView) findViewById(R.id.imageLogoFMVO)).setVisibility(View.VISIBLE);
            }
        });

        // CONTEXTE
        ((TextView) findViewById(R.id.EmplacementLotProduit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandeCourante = CommandeOpenHelper.getCommandeByID(db, receptionID);
                Depot depotOrigine = DepotOpenHelper.getDepotPUI(db);
                Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeZoneCreationActivity.class);
                Bundle extras = ScannerReceptionActivity.super.getBundle();
                extras.putInt("depotID", depotOrigine.getDepot_UID());
                newIntent.putExtras(extras);
                ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RESULT_ZONE);
            }
        });

        //on cache le clavier à chaque fois que l'éditText reprend le focus après l'avoir perdu
        EditTextScanee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                InputMethodManager imm = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        EditTextScanee.requestFocus();

        // Mise à jour GRAPHIQUE
        findViewById(R.id.boutonFermeture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scannerSearchOnlyIntent = new Intent();
                Bundle scannerSearchOnlyBundle = new Bundle();
                int codeEchangeActivity = 0;

                scannerSearchOnlyBundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_courant);
                scannerSearchOnlyBundle.putSerializable("ProduitPrecedent", (Serializable) produitCourant);
                codeEchangeActivity = CodesEchangesActivites.RETOUR_SCANNER;

                scannerSearchOnlyIntent.putExtras(scannerSearchOnlyBundle);
                ScannerReceptionActivity.this.setResult(codeEchangeActivity, scannerSearchOnlyIntent);
                ScannerReceptionActivity.this.finish();
            }
        });

        findViewById(R.id.scannerMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditTextScanee.setText("01040462410085001721123110alcyons2\n");
            }
        });

        findViewById(R.id.scannerMode).setVisibility(View.INVISIBLE);

        EditTextScanee.setShowSoftInputOnFocus(false);

        EditTextScanee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().endsWith("\n")) {
                    String codeScanne = s.toString().substring(0, s.length() - 1);
                    if((codeScanne.startsWith("01") || codeScanne.startsWith("02")) && codeScanne.length() == 16)
                    {
                        tempCodeScanne = codeScanne;
                        Produit produitTemp = ProduitOpenHelper.getUnProduitParGTIN(db, codeScanne);
                        if(produitTemp == null)
                            produitTemp = ProduitOpenHelper.getUnProduitParGTIN(db, codeScanne.substring(2));

                        if(produitTemp != null)
                        {
                            if(emplacement_courant == null)
                            {
                                Depot depotPui = DepotOpenHelper.getDepotPUI(db);
                                Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPui, produitTemp.getZone_PUI_Defaut());
                                if(zoneCourante != null)
                                    emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitTemp.getEmplacement_PUI_Defaut());
                            }
                            if(emplacement_courant != null) {
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());
                            }
                        }

                        ((TextView) findViewById(R.id.instruction)).setText("Scannez la deuxième partie du code scindé");
                    }
                    else if(tempCodeScanne != null && !tempCodeScanne.contentEquals("") && !codeScanne.startsWith("01") && !codeScanne.startsWith("02"))
                    {
                        tempCodeScanne = tempCodeScanne +  codeScanne;
                        EditTextScanee.setText(tempCodeScanne+"\n");
                        tempCodeScanne = "";
                    }
                    else
                    {
                        tempCodeScanne = "";
                        String lot;
                        String serie;
                        String gtin_courant = "";
                        String gtin_courant_sans_ai = "";
                        String date_peremption_courant = "";
                        String date_peremption_serialisation = "";
                        if(codeScanne.startsWith("PHITAGPLACE"))
                        {
                            serie = "";
                            lot = "";
                            String[] tab_emplacement = codeScanne.split(":");
                            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);

                            if(emplacement_courant != null)
                            {
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());
                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                            }
                            else
                            {

                                ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutScannerEmplacementInconnu)).setVisibility(View.INVISIBLE);
                                    }
                                }, 2500);
                            }
                        }
                        else
                        {
                            GS1Parser.GS1Result result = GS1Parser.parseGS1Code(codeScanne);

                            if (!result.productCode.contentEquals(""))
                            {
                                //on récupère les informations du découpage du GS1
                                lot = result.lotNumber;
                                serie = result.serie;
                                gtin_courant = "01"+result.productCode;
                                gtin_courant_sans_ai = result.productCode;
                                date_peremption_courant = result.expirationDateAffichage;
                                date_peremption_serialisation = result.expirationDate;

                                //récucpération du produit avec le GTIN
                                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gtin_courant);

                                //Si la liste est vide on essaye avec le GTIN sans AI
                                if(produits.isEmpty())
                                {
                                    produits = ProduitOpenHelper.getProduitsParGTIN(db, gtin_courant_sans_ai);
                                }

                                //Si la liste n'est pas vide on réupère le produit courant
                                if(!produits.isEmpty())
                                    produitCourant = produits.get(0);
                            }
                            else
                            {
                                serie = "";
                                lot = "";
                                //on essaye de récupérer via le code inconnu
                                List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.toString().substring(0, s.length()-1));
                                if (produits.size() == 1) {
                                    produitCourant = produits.get(0);
                                }
                            }

                            if(produitCourant != null)
                            {
                                if(emplacement_courant == null)
                                {
                                    Depot depotPui = DepotOpenHelper.getDepotPUI(db);
                                    Depot_Zone zoneCourante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPui, produitCourant.getZone_PUI_Defaut());
                                    if(zoneCourante != null)
                                        emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zoneCourante, produitCourant.getEmplacement_PUI_Defaut());
                                }

                                if(emplacement_courant != null)
                                {
                                    ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                                    ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage().trim());

                                    //on vérifie que le produit courant fait partie de la liste des ph_preparation_ligne
                                    boolean produit_present = false;
                                    for(PH_Reliquat courant : listPH_Reliquat)
                                    {
                                        if(courant.getProduitID() == produitCourant.getID_produit())
                                        {
                                            reliquatCourant = courant;
                                            produit_present = true;
                                            break;
                                        }
                                    }

                                    if(!produit_present)
                                    {
                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((LinearLayout) findViewById(R.id.layoutProduitAbsent)).setVisibility(View.INVISIBLE);
                                            }
                                        }, 2000);
                                        produitCourant = null;
                                        reinitialisationInterface();
                                    }
                                    else if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance() && serie.contentEquals(""))
                                    {
                                        ((LinearLayout) findViewById(R.id.layoutSerieNonScannee)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.layoutSerieNonScannee)).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((LinearLayout) findViewById(R.id.layoutSerieNonScannee)).setVisibility(View.INVISIBLE);
                                            }
                                        }, 2000);
                                    }
                                    else
                                    {
                                        List<PH_Reliquat> reliquatPreparer = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.getNumero(), produitCourant.getID_produit());
                                        int qte_demander = reliquatCourant.getQteReliquat_X();
                                        int qte_receptionne = 0;
                                        int qte_restante = 0;
                                        for(PH_Reliquat ligne_temp : reliquatPreparer)
                                        {
                                            qte_receptionne = qte_receptionne + ligne_temp.getQteLivraison();
                                        }
                                        qte_restante = qte_demander - qte_receptionne;

                                        String designationProduit = reliquatCourant.getDesignationCourte();
                                        String referenceProduit = reliquatCourant.getProduit_Reference();
                                        String conditionnement = String.valueOf((int)reliquatCourant.getConditionnementAchat());

                                        if(qte_restante == 0)
                                        {
                                            ((LinearLayout) findViewById(R.id.layoutProduitComplet)).setVisibility(View.VISIBLE);
                                            ((TextView) findViewById(R.id.designationComplete)).setText(designationProduit);
                                            ((TextView) findViewById(R.id.quantiteComplete)).setText(qte_receptionne+" / "+qte_demander);
                                            ((LinearLayout) findViewById(R.id.layoutProduitComplet)).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((LinearLayout) findViewById(R.id.layoutProduitComplet)).setVisibility(View.INVISIBLE);
                                                    ((TextView) findViewById(R.id.quantiteComplete)).setText("");
                                                    ((TextView) findViewById(R.id.designationComplete)).setText("");
                                                }
                                            }, 2000);
                                            produitCourant = null;
                                            reinitialisationInterface();
                                        }
                                        else
                                        {
                                            if(emplacement_courant != null || commandeCourante.getRef_Depot_Dest().contains("-PAD"))
                                            {
                                                ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");

                                                if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText("RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite());
                                                } else {
                                                    ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacement_courant.getAdressage());
                                                }
                                            }
                                            else
                                            {
                                                ((ImageView) findViewById(R.id.ImageViewProduit)).setVisibility(View.GONE);
                                                ((ImageView) findViewById(R.id.ImageViewEmplacement)).setVisibility(View.VISIBLE);
                                                ((TextView) findViewById(R.id.instruction)).setText("Scannez un emplacment");
                                            }

                                            int qteProgress = qte_receptionne+Integer.parseInt(conditionnement);
                                            ((ProgressBar) findViewById(R.id.progressBarQuantite)).setMax(qte_demander);
                                            ((ProgressBar) findViewById(R.id.progressBarQuantite)).setProgress(qteProgress);
                                            ((TextView) findViewById(R.id.designationValidation)).setText(designationProduit);
                                            ((TextView) findViewById(R.id.quantiteValidation)).setText(qteProgress+" / "+qte_demander);
                                            ((TextView) findViewById(R.id.lotValidation)).setText(lot);
                                            ((TextView) findViewById(R.id.peremptionValidation)).setText(date_peremption_courant);
                                            ((TextView) findViewById(R.id.emplacementValidation)).setText(emplacement_courant.getAdressage());

                                            boolean seriedejascanne = false;
                                            for(PH_Reliquat courant : reliquatPreparer)
                                            {
                                                if(courant.getLot().equals(lot))
                                                {
                                                    if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance())
                                                    {
                                                        if(courant.getSerie().contentEquals(serie))
                                                        {
                                                            seriedejascanne = true;
                                                        }
                                                    }
                                                }
                                            }

                                            if(seriedejascanne)
                                            {
                                                produitCourant = null;
                                                reinitialisationInterface();
                                                ((LinearLayout) findViewById(R.id.layoutSerieScannee)).setVisibility(View.VISIBLE);
                                                ((LinearLayout) findViewById(R.id.layoutSerieScannee)).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((LinearLayout) findViewById(R.id.layoutSerieScannee)).setVisibility(View.INVISIBLE);
                                                    }
                                                }, 2000);
                                            }
                                            else
                                            {
                                                if(!serie.contentEquals(""))
                                                {
                                                    /**
                                                     * TODO : vérification du statut du numéro de série lors du scan
                                                     * */
                                                    if(serialisationActive)
                                                    {
                                                        int serialisationUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G110", gtin_courant_sans_ai, "GTIN", lot, date_peremption_serialisation, serie, "CDE", commandeCourante.getNumero());
                                                        //int serialisationdispenserUID = (int) Serialisation.Serialisation_Creer(utilisateurConnecte.getId(), "G120", gtin_courant_sans_ai, "GTIN", lot, date_peremption_serialisation, serie, "CDE", commandeCourante.getNumero());

                                                    }
                                                    ((TextView) findViewById(R.id.serieValidation)).setText(serie);
                                                    ((LinearLayout) findViewById(R.id.layoutSerieValidation)).setVisibility(View.VISIBLE);
                                                }
                                                else
                                                {
                                                    ((LinearLayout) findViewById(R.id.layoutSerieValidation)).setVisibility(View.INVISIBLE);
                                                    ((TextView) findViewById(R.id.serieValidation)).setText("");
                                                }

                                                //gestion du clic sur le compteur
                                                int finalQte_restante = qte_restante;

                                                //gestion de la validation du scan
                                                ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.VISIBLE);
                                                enregistrerPhReliquat(reliquatCourant);
                                                produitCourant = null;
                                                reliquatCourant = null;
                                                reinitialisationInterface();
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).setVisibility(View.VISIBLE);
                                    ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((LinearLayout) findViewById(R.id.layoutScannerEmplacement)).setVisibility(View.INVISIBLE);
                                        }
                                    }, 2500);
                                }
                            }
                            else
                            {
                                ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((LinearLayout) findViewById(R.id.layoutProduitInconnu)).setVisibility(View.INVISIBLE);
                                    }
                                }, 3000);
                            }
                        }
                    }

                    EditTextScanee.getText().clear();
                }
                EditTextScanee.setShowSoftInputOnFocus(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            /** TODO revoir */
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_ZONE:
                    int zoneid = data.getExtras().getInt("zoneid");
                    if(zoneid != -1)
                    {
                        Intent newIntent = new Intent(ScannerReceptionActivity.this, ListeEmplacementCreationActivity.class);
                        Bundle extras = ScannerReceptionActivity.super.getBundle();
                        extras.putInt("zoneid", zoneid);
                        newIntent.putExtras(extras);
                        ScannerReceptionActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
                    }
                    break;

                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    int emplacementid = data.getExtras().getInt("emplacementId");
                    if(emplacementid != -1)
                    {
                        Depot_Emplacement emplacementSelectionner = EmplacementOpenHelper.getUnEmplacementByID(db, emplacementid);
                        ((TextView) findViewById(R.id.EmplacementLotProduit)).setText(emplacementSelectionner.getAdressage().trim());
                        emplacement_courant = emplacementSelectionner;
                        ((TextView) findViewById(R.id.instruction)).setText("Scannez une référence");
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.boutonFermeture).performClick();
    }


    public void afficherSnackBar(String message) {
        final InputMethodManager imm = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>" + message + "</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));

        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.setMargins(0, 50, 0, 0);
        snackBarView.setLayoutParams(params);
        snackbar.show();

        InputMethodManager imme = (InputMethodManager) ScannerReceptionActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imme.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void reinitialisationInterface()
    {
        ((LinearLayout) findViewById(R.id.layoutIconeValidation)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout) findViewById(R.id.layoutIconeValidation)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.designationValidation)).setText("");
                ((TextView) findViewById(R.id.quantiteValidation)).setText("");
                ((TextView) findViewById(R.id.lotValidation)).setText("");
                ((TextView) findViewById(R.id.peremptionValidation)).setText("");
                ((TextView) findViewById(R.id.emplacementValidation)).setText("");
                ((TextView) findViewById(R.id.serieValidation)).setText("");
            }
        }, 1500);
    }

    private void enregistrerPhReliquat(PH_Reliquat ph_reliquat_base)
    {
        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, ph_reliquat_base.getProduitID());
        boolean serialiser = produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance();
        List<PH_Reliquat> liste_reliquat_receptionner = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeCourante.getNumero(), produitCourant.getID_produit());
        String numeroLot =  ((TextView) findViewById(R.id.lotValidation)).getText().toString();
        String datePeremption = ((TextView) findViewById(R.id.peremptionValidation)).getText().toString();
        String[] datePeremptionTab = datePeremption.split("/");
        if(datePeremptionTab.length == 3)
            datePeremption = datePeremptionTab[2] + "-" + datePeremptionTab[1] + "-" + datePeremptionTab[0];

        Depot_Zone zonecourante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());

        String zoneName = zonecourante.getZoneName();
        String emplacementName = emplacement_courant.getAdressage();
        String numero_Serie = ((TextView) findViewById(R.id.serieValidation)).getText().toString();

        boolean creation = true;
        for(PH_Reliquat reliquat : liste_reliquat_receptionner)
        {
            if(reliquat.getLot().contentEquals(numeroLot) && reliquat.getSerie().contentEquals(numero_Serie))
            {
                creation = false;
                reliquat.setQteLivraison(reliquat.getQteLivraison()+reliquat.getConditionnementAchat());
                PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat);
            }
        }

        if(creation)
        {
            Random randomreliquat = new Random();
            int reliquatId = randomreliquat.nextInt();
            if (reliquatId > 0)
                reliquatId = reliquatId * -1;

            PH_Reliquat phReliquatCourant = ph_reliquat_base;
            phReliquatCourant.setReliquat_UID(reliquatId);
            int quantite = phReliquatCourant.getConditionnementAchat();

            phReliquatCourant.setLot(numeroLot.trim());
            phReliquatCourant.setSerie(numero_Serie.trim());
            phReliquatCourant.setPeremptionDate(datePeremption.trim());

            if (commandeCourante.getRef_Depot_Dest().contains("-PAD")) {
                phReliquatCourant.setZone("RECEPTION");
                phReliquatCourant.setEmplacement("RECEPTION-" + commandeCourante.getNumero() + "-" + commandeCourante.getPatient_identite());
            } else {
                phReliquatCourant.setZone(zoneName.trim());
                phReliquatCourant.setEmplacement(emplacementName.trim());
            }
            phReliquatCourant.setQteLivraison(quantite);
            phReliquatCourant.setBL_Numero("");
            phReliquatCourant.setScanValue("");

            long rowID = PH_ReliquatOpenHelper.insererPH_ReliquatEnBDD(db, phReliquatCourant);
            if(rowID != -1)
            {
            }
        }
    }
}

//01040462411078381728010110lot1