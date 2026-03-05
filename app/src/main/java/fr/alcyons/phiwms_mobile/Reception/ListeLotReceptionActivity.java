package fr.alcyons.phiwms_mobile.Reception;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeReceptionActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerReceptionActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotReception_Adapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeLotReceptionActivity extends ServiceActivity {
    //gestion des variables
    Commande commandeSelectionne;
    LotReception_Adapter lotReceptionAdapter;
    Depot depot;
    PH_Reliquat phReliquat;
    Produit produit;
    boolean scanProduit;
    int quantiteRestant = 0;
    int quantiteLivree = 0;
    boolean numero_serie;
    boolean lotmanuel;
    EditText numLotEditText;
    TextView datePeremptionTextView;
    TextView numeroReceptionTextView;
    TextView nomFournisseurTextView;
    TextView designationProduitTextView;
    TextView referenceProduitTextView;
    TextView QteDemandeeTextView;
    TextView QtePreparerTextView;
    LinearLayout lancerScanLinearLayout;
    LinearLayout firstRowLinearLayout;
    PackageManager pm;
    Depot_Emplacement emplacement_precedent;
    Produit produit_precedent;
    RecyclerView recyclerView;

    private void initObjetGraphique()
    {
        numeroReceptionTextView = ((TextView) findViewById(R.id.numeroReception));
        nomFournisseurTextView = ((TextView) findViewById(R.id.nomFournisseur));
        designationProduitTextView = ((TextView) findViewById(R.id.designationProduit));
        referenceProduitTextView = ((TextView) findViewById(R.id.referenceProduit));
        QteDemandeeTextView = ((TextView) findViewById(R.id.QteDemandee));
        QtePreparerTextView = ((TextView) findViewById(R.id.QtePreparer));
        lancerScanLinearLayout = ((LinearLayout) findViewById(R.id.lancerScan));
        firstRowLinearLayout = ((LinearLayout) findViewById(R.id.firstRow));
        recyclerView = (RecyclerView) findViewById(R.id.liste_view_reception);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int decorationCount = recyclerView.getItemDecorationCount();
        for (int i = 0; i < decorationCount; i++) {
            recyclerView.removeItemDecorationAt(0);
        }
        DividerItemDecoration divider = new DividerItemDecoration(ListeLotReceptionActivity.this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ListeLotReceptionActivity.this, R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_reception);

        //gestion des objets graphique
        initObjetGraphique();

        //gestion du package manager
        pm = ListeLotReceptionActivity.this.getPackageManager();

        depot = DepotOpenHelper.getPUICourant(db);
        scanProduit = false;

        //gestion du booleen d'ajout manuel d'un lot
        lotmanuel = false;

        commandeSelectionne = CommandeOpenHelper.getCommandeByID(db, Objects.requireNonNull(intent.getExtras()).getInt("commandeID_Selectionne"));
        phReliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, intent.getExtras().getInt("phReliquatId"));

        //récupération de l'emplacement et du produit précédent
        emplacement_precedent = (Depot_Emplacement) intent.getExtras().getSerializable("EmplacementPrecedent");
        produit_precedent = (Produit) intent.getExtras().getSerializable("ProduitPrecedent");

        //gestion de l'entête
        numeroReceptionTextView.setText("#"+commandeSelectionne.getNumero());
        nomFournisseurTextView.setText(commandeSelectionne.getFournisseur());

        if (phReliquat != null) {
            produit = ProduitOpenHelper.getProduitByID(db, phReliquat.getProduitID());
            numero_serie = produit.isSuivi_Serialisation() && produit.isSerialiser_Reception_Delivrance();

            //Entête
            designationProduitTextView.setText(phReliquat.getdesignationCourte().trim());
            referenceProduitTextView.setText(phReliquat.getProduit_Reference().trim());
            QteDemandeeTextView.setText(String.valueOf(phReliquat.getQteReliquat_X()));

            calculQuantiteReception();
            //gestion du clic sur la zone du datamatrix -> ouverture de l'appareil photo pour scanner des lots de la référence
            lancerScanLinearLayout.setOnClickListener(view -> {

                Intent listeLotReception_Intent;
                Bundle listeLotReception_Bundle = new Bundle();

                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies")  || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, ScannerReceptionActivity.class);
                }
                else
                {
                    listeLotReception_Intent = new Intent(ListeLotReceptionActivity.this, BarcodeReceptionActivity.class);
                }

                listeLotReception_Bundle.putInt("ReceptionID", commandeSelectionne.getID_commande());
                listeLotReception_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                listeLotReception_Bundle.putString("ordreTri", "");
                listeLotReception_Bundle.putInt("serviceSelectionneID", Objects.requireNonNull(intent.getExtras()).getInt("serviceSelectionneID"));
                listeLotReception_Bundle.putSerializable("EmplacementPrecedent", (Serializable) emplacement_precedent);
                listeLotReception_Bundle.putSerializable("ProduitPrecedent", (Serializable) produit_precedent);

                listeLotReception_Intent.putExtras(listeLotReception_Bundle);
                ListeLotReceptionActivity.this.startActivityForResult(listeLotReception_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
            });

        } else {
            ListeLotReceptionActivity.this.finish();
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onMenuSaveClick();
            }
        });

        ((Button) findViewById(R.id.btnAjoutManuel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creationManuelleLot();
            }
        });

        if(numero_serie)
            if(quantiteRestant > 0)
                lancerScanLinearLayout.performClick();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        super.onResume();

        if(!scanProduit)
        {
            quantiteLivree = 0;
            List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), phReliquat.getProduitID());
            for (PH_Reliquat reliquatCourant : reliquatReceptionne) {
                quantiteLivree += reliquatCourant.getQteLivraison();
            }

            if(!numero_serie)
                ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);

            lotReceptionAdapter = new LotReception_Adapter(reliquatReceptionne, position -> {
                PH_Reliquat courantasupprimer = lotReceptionAdapter.getReliquatAt(position);
                if(courantasupprimer != null)
                {
                    PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, courantasupprimer);
                    lotReceptionAdapter.notifyItemRemoved(position);
                    calculQuantiteReception();
                    checkEtatReception();
                    lotReceptionAdapter.notifyItemChanged(position);
                    onResume();
                }
            }, ListeLotReceptionActivity.this);

            recyclerView.setAdapter(lotReceptionAdapter);
            invalidateOptionsMenu();
        }

        calculQuantiteReception();
        checkEtatReception();

        int nbColis = recupererNbColis(produit.getID_produit(), phReliquat.getQteReliquat_X());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    String codeComplet = data.getStringExtra("code");
                    if(codeComplet != null)
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (!gs1Decoupe.isEmpty()) {
                            if (datePeremptionTextView != null && numLotEditText != null) {
                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                Date date = new Date();

                                try {
                                    date = dateFormat1.parse(Objects.requireNonNull(gs1Decoupe.get(OutilsDecodage.dateDePeremption)));
                                } catch (ParseException e) {
                                    Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
                                }

                                assert date != null;
                                String dateFinale = dateFormat2.format(date);

                                //PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = lotReceptionAdapter.lotList.get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));
                                PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = null;

                                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

                                try {
                                    Date dateFournie = dateDecodeur.parse(dateFinale);
                                    assert dateFournie != null;
                                    phReliquatLotReception.setDatePeremption(dateFormat.format(dateFournie));
                                } catch (ParseException e) {
                                    Log.e("Parse Exception", Objects.requireNonNull(e.getMessage()));
                                }

                                String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                assert serie != null;
                                String last_char = serie.substring(serie.length()-1);
                                if(last_char.contentEquals("@"))
                                    serie = serie.substring(0, serie.length()-1);

                                String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                assert lot != null;
                                last_char = lot.substring(lot.length()-1);
                                if(last_char.contentEquals("@"))
                                    lot = lot.substring(0, lot.length()-1);

                                phReliquatLotReception.setNumeroLot(lot);
                                phReliquatLotReception.setNumero_serie(serie);
                            }
                        } else {
                            Toast toast = Toast.makeText(ListeLotReceptionActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        scanProduit = true;
                    }
                    invalidateOptionsMenu();
                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    break;
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    PH_Reliquat_Reception_Adapte.Lot phReliquatLotReceptionRecu = (PH_Reliquat_Reception_Adapte.Lot) Objects.requireNonNull(data.getExtras()).getSerializable("lotZoneEtEmplacement");
                    // PH_Reliquat_Reception_Adapte.Lot phReliquatLotReception = phReliquatReceptionAdapte.getlotList().get(lotReceptionAdapter.viewHolderList.indexOf(viewHolderAModifier));
                    assert phReliquatLotReceptionRecu != null;
                    //phReliquatLotReception.setZoneEtEmplacementList(phReliquatLotReceptionRecu.getZoneEtEmplacementList());
                    break;

                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                        onResume();
                    break;
                case RETOUR_LOT:
                    onResume();
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(item1 -> {
            onMenuSaveClick();
            return true;
        });
        return true;
    }
    private void onMenuSaveClick()
    {
        boolean quantite_ok = true;
        int quantite_total = 0;
        List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, phReliquat.getcommandeNumero(), phReliquat.getProduitID());
        for(PH_Reliquat courant : reliquatReceptionne)
        {
            quantite_total += courant.getQteLivraison();
        }

        if(quantite_total > phReliquat.getQteReliquat_X())
        {
            quantite_ok = false;
        }

        if(quantite_ok)
        {
            Intent resultIntent = new Intent();
            Bundle extras = ListeLotReceptionActivity.super.getBundle();
            ListeLotReceptionActivity.this.setResult(CodesEchangesActivites.RETOUR_LISTE_LOTS, resultIntent);
            ListeLotReceptionActivity.this.finish();
        }
        else
        {
            Alerte.afficherAlerte(ListeLotReceptionActivity.this, "Alerte", "La quantité saisie est supérieur à la quantité attendu", "alerte");
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkEtatReception()
    {
        if(quantiteRestant == 0)
        {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_cadre_vert, null));
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.vert, null));
            lancerScanLinearLayout.setVisibility(View.GONE);
            QtePreparerTextView.setVisibility(View.INVISIBLE);
            lotReceptionAdapter.receptionComplete = true;
            ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.GONE);
        }
        else {
            //si c'est le cas on cache les autres lignes
            firstRowLinearLayout.setBackground(ListeLotReceptionActivity.this.getResources().getDrawable(R.drawable.background_cadre_orange, null));
            lancerScanLinearLayout.setVisibility(View.VISIBLE);
            QtePreparerTextView.setVisibility(View.VISIBLE);
            QteDemandeeTextView.setTextColor(ListeLotReceptionActivity.this.getResources().getColor(R.color.noir, null));
            lotReceptionAdapter.receptionComplete = false;

            if(!numero_serie)
                ((Button) findViewById(R.id.btnAjoutManuel)).setVisibility(View.VISIBLE);
        }
    }
    public void validerQuantiteReception(final int position, PH_Reliquat reliquatAModifier, int qteApres)
    {
        if(qteApres != reliquatAModifier.getQteLivraison())
        {
            //annulation du reliquat courant
            modifierReliquatBDD(reliquatAModifier, 0);
            calculQuantiteReception();

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            //erreur si c'est un reliquat
            int maxValue = quantiteRestant;

            if(maxValue < qteApres)
                qteApres = maxValue;

            if(qteApres < 0)
                qteApres = 0;

            lotReceptionAdapter.getReliquatAt(position).setQteLivraison(qteApres);

            if (viewHolder != null && viewHolder instanceof LotReception_Adapter.LotViewHolder) {
                ((LotReception_Adapter.LotViewHolder) viewHolder).qteSaisie.setText(String.valueOf(qteApres));
            }

            if(qteApres == 0)
            {
                PH_ReliquatOpenHelper.supprimerUnPHReliquat(db, reliquatAModifier);
                lotReceptionAdapter.notifyItemRemoved(position);
            }
            else
            {
                modifierReliquatBDD(reliquatAModifier, qteApres);
            }

            calculQuantiteReception();
            checkEtatReception();
            lotReceptionAdapter.notifyDataSetChanged();
            onResume();
        }
    }
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
            nbColis = (int) (double) nbColis;
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }
    private void calculQuantiteReception()
    {
        int qte_receptionne = 0;
        quantiteRestant = phReliquat.getQteReliquat_X();
        List<PH_Reliquat> reliquatReceptionne = PH_ReliquatOpenHelper.getPH_ReliquatNegByCommandeNumeroAndProduit(db, commandeSelectionne.getNumero(), phReliquat.getProduitID());
        for(PH_Reliquat ph_reliquat : reliquatReceptionne)
        {
            qte_receptionne += ph_reliquat.getQteLivraison();
            quantiteRestant = quantiteRestant - ph_reliquat.getQteLivraison();
        }
        QtePreparerTextView.setText(String.valueOf(qte_receptionne));
    }
    private void creationManuelleLot()
    {
        Bundle clicBoutonAjouterManuellement_Bundle = ListeLotReceptionActivity.super.getBundle();
        clicBoutonAjouterManuellement_Bundle.putInt("produitID", produit.getID_produit());
        clicBoutonAjouterManuellement_Bundle.putInt("depotID", depot.getDepot_UID());
        clicBoutonAjouterManuellement_Bundle.putInt("ReliquatID", phReliquat.getReliquat_UID());

        Intent clicBoutonAjouterManuellement_Intent = new Intent(ListeLotReceptionActivity.this, CreationLotManuelReceptionActivity.class);
        clicBoutonAjouterManuellement_Intent.putExtras(clicBoutonAjouterManuellement_Bundle);
        ListeLotReceptionActivity.this.startActivityForResult(clicBoutonAjouterManuellement_Intent, RETOUR_LOT);
    }
    private void modifierReliquatBDD(PH_Reliquat reliquatAModifier, int quantiteAModifier) {
        reliquatAModifier.setQteLivraison(quantiteAModifier);

        long rowID = PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquatAModifier);
        if (rowID != -1) {

        }
    }
    public void fermerClavierEtRelayout() {
        View v = getCurrentFocus();
        if (v != null) v.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View decor = getWindow().getDecorView();
        if (imm != null) imm.hideSoftInputFromWindow(decor.getWindowToken(), 0);

        // force une nouvelle mesure après fermeture IME
        View content = findViewById(android.R.id.content);
        content.post(content::requestLayout);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN && getCurrentFocus() instanceof EditText) {

            EditText et = (EditText) getCurrentFocus();

            int[] coords = new int[2];
            et.getLocationOnScreen(coords);

            float x = ev.getRawX() + et.getLeft() - coords[0];
            float y = ev.getRawY() + et.getTop() - coords[1];

            boolean outside = (x < et.getLeft() || x > et.getRight() || y < et.getTop() || y > et.getBottom());

            if (outside) {

                // 1) retrouver la row (itemView) qui contient l'EditText
                View row = recyclerView.findContainingItemView(et);
                int pos = (row != null) ? recyclerView.getChildAdapterPosition(row) : RecyclerView.NO_POSITION;

                // 2) lire la quantité
                int qte = 0;
                String txt = et.getText().toString().trim();
                if (!txt.isEmpty()) {
                    try { qte = Integer.parseInt(txt); } catch (Exception ignored) {}
                }

                // 3) valider
                if (pos != RecyclerView.NO_POSITION) {
                    validerQuantiteReception(pos, lotReceptionAdapter.getReliquatAt(pos), qte);
                }

                // 4) fermer clavier + focus
                et.clearFocus();

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                }

                View decor = getWindow().getDecorView();
                decor.post(() -> {
                    decor.requestApplyInsets();      // important
                    recyclerView.requestLayout();    // optionnel
                    recyclerView.invalidate();       // optionnel
                });

                recyclerView.post(recyclerView::requestLayout);
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}