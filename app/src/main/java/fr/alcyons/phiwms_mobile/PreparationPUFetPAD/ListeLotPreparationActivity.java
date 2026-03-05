package fr.alcyons.phiwms_mobile.PreparationPUFetPAD;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_LOTS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPhotoPreparation;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.StockUtilisesOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.StockUtilises;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotAdapter;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotAdapter_V2;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

public class ListeLotPreparationActivity extends ServiceAvecConnexionActivity {

    PH_Preparation_Ligne ph_preparation_ligne_base;
    RecyclerView recyclerView;
    Depot depot;
    Produit produit;
    LotAdapter_V2 adapter;
    boolean camera_first;
    PackageManager pm;
    Context context;

    boolean produitsuiviserie;
    boolean produitserialiserreception;
    int quantiteDemandeeBase;
    int restantAPrepaper;
    Integer qteDejaPreparer;
    List<PH_Preparation_Ligne> phPreparationLignesPreparer;
    PH_Preparation ph_preparation;
    List<Stock_Lot_Emplacement_Light> listeStockLotEmplacement;
    List<String> listelot;
    boolean passageParScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lot_prepration);
        context = ListeLotPreparationActivity.this;
        pm = ListeLotPreparationActivity.this.getPackageManager();
        camera_first = false;
        passageParScanner = false;

        // Récupération du ph_preparation_ligne, produit, depot sélectionné
        listelot = new ArrayList<>();
        ph_preparation_ligne_base = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, intent.getExtras().getInt("phPreparationLigneId"));
        ph_preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne_base.getPreparationID());
        phPreparationLignesPreparer = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparationAndProduitNeg(db, ph_preparation, ph_preparation_ligne_base.getProduitID());
        produit = ProduitOpenHelper.getProduitByID(db, ph_preparation_ligne_base.getProduitID());
        depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotOrigineID());
        String depotText = ph_preparation.getDepotDestinataireReference();
        Depot depot_destinataire = DepotOpenHelper.getDepotParReference(db, ph_preparation.getDepotDestinataireReference());

        if (produit == null || depot == null) {
            Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Alerte", "Un problème a été constaté en Base de données, veuillez synchroniser l'application ou contacter la société Alcyons (service Préparation", "alerte");
            ListeLotPreparationActivity.this.finish();
            return;
        }

        produitserialiserreception = produit.isSerialiser_Reception_Delivrance();
        produitsuiviserie = produit.isSuivi_Serialisation();

        quantiteDemandeeBase = ph_preparation_ligne_base.getQte_APreparer();
        qteDejaPreparer = 0;
        for(PH_Preparation_Ligne lignecourante : phPreparationLignesPreparer)
        {
            qteDejaPreparer = qteDejaPreparer + lignecourante.getQte_preparer();
        }
        restantAPrepaper = quantiteDemandeeBase - qteDejaPreparer;

        if(depot_destinataire != null)
        {
            depotText = depot_destinataire.getNom();

            if(utilisateurConnecte.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot_destinataire.getStructure().contentEquals("PAD"))
            {
                depotText = "Patient - "+depot_destinataire.getPAD_IPP();
            }
        }

        ((TextView) findViewById(R.id.depot)).setText(depotText);
        ((TextView) findViewById(R.id.intitule)).setText("#"+ph_preparation.getUID());
        ((TextView) findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
        ((TextView) findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());

        ((LinearLayout) findViewById(R.id.lancerScan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuDatamatrixClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        if (statutConnexion && passageParOnCreate)
        {
            RequestQueue requestQueue = Volley.newRequestQueue(ListeLotPreparationActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteStock_Lot_Emplacements+"produit/"+produit.getID_produit()+"/depot/"+depot.getDepot_Reference();

            JsonObjectRequest obreq = getJsonObjectRequest(urlRequete);
            requestQueue.add(obreq);
        }
        else
        {
            gestionAdapter();
        }

        if(!camera_first)
        {
           MAJVisuel();
        }
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String urlRequete) {
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int nbResultat = response.getInt("resultCount");
                            if (nbResultat == 0) {
                                String erreur = response.getString("erreur");
                                if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session a expirée, veuillez vous reconnecter.", "alerte");
                                } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                    Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");;
                                } else if (!erreur.contentEquals("Aucun PH_Stock_Lot_Emplacement trouvé")) {
                                    Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Aucune ligne trouvée", "alerte");
                                }
                                else {
                                 }
                            } else {
                                JSONArray ph_stockLotEmplacement_JSONArray = response.getJSONArray("PH_Stock_Lot_Emplacements");
                                for (int k = 0; k < ph_stockLotEmplacement_JSONArray.length(); k++) {
                                    Stock_Lot_Emplacement_Light stock_lot_emplacement_light = new Stock_Lot_Emplacement_Light(ph_stockLotEmplacement_JSONArray.getJSONObject(k));
                                    Stock_Lot_Emplacement_Light stock_lot_emplacement_bdd = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stock_lot_emplacement_light.get_UID());

                                    if (stock_lot_emplacement_bdd == null) {
                                        if (stock_lot_emplacement_light.getQte() >= 0) {
                                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, stock_lot_emplacement_light);
                                        }
                                    }
                                    else
                                    {
                                        if(stock_lot_emplacement_bdd.getQte() != stock_lot_emplacement_light.getQte())
                                        {
                                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_lot_emplacement_light);
                                        }
                                    }
                                }

                                //gestion des stocks utilisés
                                List<StockUtilises> stockUtilisesList = StockUtilisesOpenHelper.getStockUtiliserByNotUser(db, utilisateurConnecte.getId());
                                for (StockUtilises stockUtilisesTemp : stockUtilisesList) {
                                    if(stockUtilisesTemp.getUserId() != utilisateurConnecte.getId())
                                    {
                                        Stock_Lot_Emplacement_Light stockCourantTemp = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, stockUtilisesTemp.getStockId());
                                        if(stockCourantTemp != null)
                                        {
                                            stockCourantTemp.setQte(stockCourantTemp.getQte() - stockUtilisesTemp.getQuantite());
                                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockCourantTemp);
                                        }
                                    }
                                }

                                invalidateOptionsMenu();
                                passageParOnCreate = false;
                                arreterSpinner();
                                gestionAdapter();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Stock Lot Emplacement)", "alerte");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", utilisateurConnecte.getToken());
                return headers;
            }
        };
        obreq.setRetryPolicy(retryPolicy);
        return obreq;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH:
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
                    listelot = new ArrayList<>();
                    if(!listeStockLotEmplacement.isEmpty())
                    {
                        for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                        {
                            listelot.add(courant.getLot());
                        }
                    }

                    if(!produitserialiserreception && produitsuiviserie)
                    {
                        listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
                    }
                    qteDejaPreparer = 0;
                    for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                    {
                        qteDejaPreparer = qteDejaPreparer + courant.getQte_Preparer();
                    }
                    MAJValues(true, 0);
                    MAJVisuel();
                    onResume();
                break;
            }
        }
        invalidateOptionsMenu();
    }

    // On remet les quantités à 0 et on quitte l'activité
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onMenuSaveClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);

        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSave = menu.findItem(R.id.menuSaveCircle);

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuSaveClick();
                return true;
            }
        });

        return true;
    }

    private void onMenuSaveClick() {
        MAJListeLot();
        //String erreur = verificationDisponibilite();
        String erreur = "";

        if(erreur.contentEquals(""))
        {
            Intent resultIntent = new Intent();

            Bundle extras = ListeLotPreparationActivity.super.getBundle();
            resultIntent.putExtras(extras);

            ListeLotPreparationActivity.this.setResult(RETOUR_LISTE_LOTS, resultIntent);
            ListeLotPreparationActivity.this.finish();
        }
    }

    private void onMenuDatamatrixClick() {
        int index = -1;
        MAJListeLot();
        passageParScanner = true;
        Intent listeLotPreparation_Intent = new Intent(ListeLotPreparationActivity.this, ScannerPhotoPreparation.class);
        //gestion du zebra
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
        {
            listeLotPreparation_Intent = new Intent(ListeLotPreparationActivity.this, ScannerPreparationActivity.class);
        }

        List<PH_Preparation_Ligne> listePhPreparationLigne = new ArrayList<>();
        listePhPreparationLigne.add(ph_preparation_ligne_base);

        Bundle listeLotPreparation_Bundle = super.getBundle();
        listeLotPreparation_Bundle.putInt("UserId", utilisateurConnecte.getId());
        listeLotPreparation_Bundle.putInt("preparationId", ph_preparation_ligne_base.getPreparationID());
        listeLotPreparation_Bundle.putInt("preparationLigneId", ph_preparation_ligne_base.get_UID());
        listeLotPreparation_Bundle.putSerializable("liste_ph_preparation_ligne", (Serializable) listePhPreparationLigne);
        listeLotPreparation_Bundle.putStringArrayList("liste_lot", (ArrayList<String>) listelot);

        listeLotPreparation_Intent.putExtras(listeLotPreparation_Bundle);
        ListeLotPreparationActivity.this.startActivityForResult(listeLotPreparation_Intent, CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH);
    }

    public void ValiderQuantiteSaisie(final int position, int qteApres)
    {
        Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
        if(courant.getQte_Preparer() != qteApres)
        {
            int max = 0;
            max = (int) courant.getQte();

            if(courant.getQte_Preparer() > 0)
            {
                int qte_avant = courant.getQte_Preparer();
                courant.setQte_Preparer(0);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                MAJVisuel();
                MAJValues(false, qte_avant);
            }

            int reste = quantiteDemandeeBase - qteDejaPreparer;
            if(max > reste)
                max = reste;

            if(qteApres > max)
                qteApres = max;
            else
            {
                //gestion du conditionnement
                if(qteApres % (int)ph_preparation_ligne_base.getProduitCondDistrib() != 0)
                {
                    boolean confirmation = Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Attention", "La quantité saisie ne correspond pas au conditionnement du produit. Souhaitez-vous arrondir au conditionnement ?", "OuiNon");
                    if(confirmation)
                    {
                        qteApres = ((qteApres + (int)ph_preparation_ligne_base.getProduitCondDistrib() - 1) / (int)ph_preparation_ligne_base.getProduitCondDistrib()) * (int)ph_preparation_ligne_base.getProduitCondDistrib();
                    }
                }
            }

            MAJValues(true, qteApres);

            courant.setQte_Preparer(qteApres);

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                monViewHolder.qteSaisie.setText((String.valueOf(qteApres)));
                adapter.notifyItemChanged(position);
            }
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

            if(qteApres == 0)
                supprimerPhPreparationLigne(ph_preparation_ligne_base, courant);
            else
            {
                PH_Preparation_Ligne ligneCourante = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotSerieNegPreparation(db, ph_preparation_ligne_base.getProduitID(), ph_preparation_ligne_base.getPreparationID(), courant.getLot(), courant.getSerie(), courant.getEmplacement());
                if(ligneCourante == null)
                    enregistrementPreparationLigne(ph_preparation_ligne_base, courant);
                else
                    modifierPreparationLigne(ph_preparation_ligne_base, ligneCourante, courant);
            }
        }
        MAJVisuel();
    }

    /*public void ClickNumberPicker(final int position) {
        Context context = ListeLotPreparationActivity.this;
        if(!produit.isSuivi_Serialisation() || produit.isSerialiser_Reception_Delivrance())
        {
                Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
                if(courant.getLot().contentEquals(""))
                {
                    Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Vous ne pouvez pas préparer un lot vide.", "alerte");
                }
                else
                {
                    int max = 0;
                    max = (int) courant.getQte();

                    String title = courant.getLot();
                    String message = "Quantité placée : ";

                    //gestion d'un stock déjà saisie
                    if(courant.getQte_Preparer() > 0)
                    {
                        int qte_avant = courant.getQte_Preparer();
                        courant.setQte_Preparer(0);
                        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                        MAJVisuel();
                        MAJValues(false, qte_avant);
                    }

                    int value_max = (int) max;
                    int reste = quantiteDemandeeBase - qteDejaPreparer;
                    if(value_max > reste)
                        value_max = reste;

                    int maxValue = value_max;
                    int value = maxValue;

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int qteApres = aNumberPicker.getValue();
                            qteApres = qteApres * (int)ph_preparation_ligne_base.getProduitCondDistrib();
                            MAJValues(true, qteApres);

                            courant.setQte_Preparer(qteApres);
                            // lotPreparationAdapter.quantiteAPreparer = restantAPrepaper;
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                                LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                                monViewHolder.qteSaisie.setText((String.valueOf(qteApres)));
                                adapter.notifyItemChanged(position);
                            }
                            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                            if(qteApres == 0)
                                supprimerPhPreparationLigne(ph_preparation_ligne_base, courant);
                            else
                            {
                                PH_Preparation_Ligne ligneCourante = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotSerieNegPreparation(db, ph_preparation_ligne_base.getProduitID(), ph_preparation_ligne_base.getPreparationID(), courant.getLot(), courant.getSerie(), courant.getEmplacement());
                                if(ligneCourante == null)
                                    enregistrementPreparationLigne(ph_preparation_ligne_base, courant);
                                else
                                    modifierPreparationLigne(ph_preparation_ligne_base, ligneCourante, courant);
                            }
                            MAJVisuel();
                            // lotPreparationAdapter.notifyDataSetChanged();
                            InputMethodManager imm = (InputMethodManager) ListeLotPreparationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();
                        }
                    };

                    //Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
                    Alerte.afficherAlerteNumberPickerAvecPas(context, title, message, value, maxValue, onClickListener, (int)ph_preparation_ligne_base.getProduitCondDistrib());
                }


        }
    };*/

    public void ClickLigneLot(int position)
    {
        if(!produit.isSuivi_Serialisation() || produit.isSerialiser_Reception_Delivrance())
        {
            Context context = ListeLotPreparationActivity.this;
            Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
            if(courant.getLot().contentEquals(""))
            {
                Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Vous ne pouvez pas préparer un lot vide.", "alerte");
            }
            else
            {
                int quantite_stock_selectionne = (int) courant.getQte();

                if(quantite_stock_selectionne > restantAPrepaper)
                {
                    quantite_stock_selectionne = restantAPrepaper;
                }

                //gestion du visuel
                courant.setQte_Preparer(quantite_stock_selectionne);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);

                MAJValues(true, quantite_stock_selectionne);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null && viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                    LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                    monViewHolder.qteSaisie.setText((String.valueOf(quantite_stock_selectionne)));
                    adapter.notifyItemChanged(position);
                }
                if(courant.getEmplacement().contentEquals(""))
                {
                    Intent listeZonesIntent = new Intent(ListeLotPreparationActivity.this, ListeZonesActivity.class);
                    Bundle listeZonesBundle = ListeLotPreparationActivity.super.getBundle();
                    Depot depotpui = DepotOpenHelper.getDepotPUI(db);
                    listeZonesBundle.putInt("depotSelectionneID", depotpui.getDepot_UID());
                    listeZonesIntent.putExtras(listeZonesBundle);
                    ListeLotPreparationActivity.this.startActivityForResult(listeZonesIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }

                if(quantite_stock_selectionne > 0)
                {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = now.format(formatter);
                    StockUtilises stockUtilises = new StockUtilises(String.valueOf(ph_preparation.getUID()), produit.getID_produit(), courant.get_UID(), courant.getLot(), courant.getPeremptionDate(), depot.getDepot_UID(), courant.getZone(), courant.getEmplacement(), quantite_stock_selectionne, utilisateurConnecte.getId(), formattedDateTime, utilisateurConnecte.getEtablissementId());
                    StockUtilisesOpenHelper.insererUnStockUtilisesEnBDD(db, stockUtilises);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE, stockUtilises.getphiwms_mobileUUID(), stockUtilises.getphiwms_mobileUUID(), DBOpenHelper.ActionsEAS.AJOUT);
                    ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotPreparationActivity.this, db, utilisateurConnecte, false);
                }

                enregistrementPreparationLigne(ph_preparation_ligne_base, courant);
                MAJVisuel();
            }
        }
    }

    private void MAJListeLot() {

    }

    private void MAJVisuel() {
        ((TextView) findViewById(R.id.QteDemandee)).setText(String.valueOf(quantiteDemandeeBase));
        ((TextView) findViewById(R.id.QtePreparer)).setText(String.valueOf(qteDejaPreparer));

        if(quantiteDemandeeBase == qteDejaPreparer)
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_cadre_vert));
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.vert));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.INVISIBLE);
        }
        else
        {
            //si c'est le cas on cache les autres lignes
            ((LinearLayout) findViewById(R.id.firstRow)).setBackground(ListeLotPreparationActivity.this.getResources().getDrawable(R.drawable.background_cadre_orange));
            ((TextView) findViewById(R.id.QtePreparer)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.orange2));
            ((LinearLayout) findViewById(R.id.lancerScan)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QtePreparer)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.QteDemandee)).setTextColor(ListeLotPreparationActivity.this.getResources().getColor(R.color.noir));
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
            nbColis = (int) Math.ceil(nbColis);
        }
        if (quantite != 0) {
            if (nbColis == 0) {
                nbColis = 1;
            }
        }

        return nbColis;
    }

    private void MAJValues(boolean ajout, int quantiteamodifier) {
        if(ajout)
        {
            qteDejaPreparer = qteDejaPreparer + quantiteamodifier;
        }
        else
        {
            qteDejaPreparer = qteDejaPreparer - quantiteamodifier;
        }

        restantAPrepaper = quantiteDemandeeBase - qteDejaPreparer;

        if(restantAPrepaper == 0)
            onMenuSaveClick();
    }

    private void modifierPreparationLigne(PH_Preparation_Ligne ph_preparationLigneCorrespondant, PH_Preparation_Ligne ph_preparation_ligne, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        ph_preparation_ligne.setQte_Demander(GlobalAPreparer);
        GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
        ph_preparation_ligne.setQte_RAL(GlobalAPreparer);
        ph_preparation_ligne.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());

        long rowID = PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
    }

    private void enregistrementPreparationLigne(PH_Preparation_Ligne ph_preparationLigneCorrespondant, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        /* on supprime les lignes déjà enregistrer qui ne sont pas les lignes de bases */
       int GlobalAPreparer = ph_preparationLigneCorrespondant.getQte_Demander();
        PH_Preparation_Ligne ph_preparationLigneCourant = null;

        ph_preparationLigneCourant = new PH_Preparation_Ligne(ph_preparationLigneCorrespondant);
        Random random = new Random();
        int new_id = random.nextInt();
        if(new_id > 0)
        {
            new_id= new_id*-1;
        }

        ph_preparationLigneCourant.set_UID(new_id);
        ph_preparationLigneCourant.setQte_Demander(GlobalAPreparer);
        GlobalAPreparer = GlobalAPreparer - stockLotEmplacementLight.getQte_Preparer();
        ph_preparationLigneCourant.setQte_RAL(GlobalAPreparer);
        ph_preparationLigneCourant.setQte_preparer(stockLotEmplacementLight.getQte_Preparer());
        ph_preparationLigneCourant.setLotNumero(stockLotEmplacementLight.getLot().trim());
        ph_preparationLigneCourant.setPeremptionDate(stockLotEmplacementLight.getPeremptionDate());
        ph_preparationLigneCourant.setZoneDepot(stockLotEmplacementLight.getZone().trim());
        ph_preparationLigneCourant.setEmplacementParDefaut(stockLotEmplacementLight.getEmplacement().trim());
        ph_preparationLigneCourant.setSerieNumero(stockLotEmplacementLight.getSerie().trim());
        ph_preparationLigneCourant.set_UID_4D(ph_preparationLigneCorrespondant.get_UID_4D());

        long rowID = PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, ph_preparationLigneCourant);
    }

    private void supprimerPhPreparationLigne(PH_Preparation_Ligne ligne_base, Stock_Lot_Emplacement_Light stockLotEmplacementLight)
    {
        PH_Preparation_Ligne ligne_a_supprimer = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByProduitLotSerieNegPreparation(db, ligne_base.getProduitID(), ligne_base.getPreparationID(), stockLotEmplacementLight.getLot(), stockLotEmplacementLight.getSerie(), stockLotEmplacementLight.getEmplacement());

        if(ligne_a_supprimer != null)
            PH_Preparation_LigneOpenHelper.supprimerUnPhPreparationLigne(db, ligne_a_supprimer);

        if(produitsuiviserie && !produitserialiserreception)
        {
            if(!stockLotEmplacementLight.getSerie().contentEquals(""))
                Stock_Lot_EmplacementLightOpenHelper.supprimerUnStockLotEmplacement(db, stockLotEmplacementLight);
        }
    }

    private void gestionListe()
    {
        listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
        boolean gtin_ok = !produit.getGTIN().contentEquals("");

        if (!listeStockLotEmplacement.isEmpty()) {
            Collections.sort(listeStockLotEmplacement, new Comparator<Stock_Lot_Emplacement_Light>() {
                @Override
                public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                    return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                }
            });

            for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
            {
                listelot.add(courant.getLot());
            }

            if(!produitserialiserreception && produitsuiviserie)
            {
                listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
            }
        }
        else
        {
            Alerte.afficherAlerteInformation(ListeLotPreparationActivity.this, getLayoutInflater(), "Alerte", "Aucun lot existant pour cette référence", true, false);
        }

        if(!gtin_ok && produitsuiviserie)
        {
            Alerte.afficherAlerte(ListeLotPreparationActivity.this, "Erreur", "Aucun GTIN renseigné pour le produit sélectionné, impossible d'ouvrir le scan", "alerte");
        }
        else
        {
            if(produitsuiviserie && !produitserialiserreception)
            {
                if(qteDejaPreparer < quantiteDemandeeBase)
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        if(!passageParScanner)
                            onMenuDatamatrixClick();
                    }
                }
            }
        }
    }

    private void gestionAdapter()
    {
        gestionListe();
        recyclerView = findViewById(R.id.recyclerView);
        int decorationCount = recyclerView.getItemDecorationCount();
        for (int i = 0; i < decorationCount; i++) {
            recyclerView.removeItemDecorationAt(0);
        }
        DividerItemDecoration divider = new DividerItemDecoration(ListeLotPreparationActivity.this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ListeLotPreparationActivity.this, R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new LotAdapter_V2(listeStockLotEmplacement, position -> {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null && viewHolder instanceof LotAdapter_V2.LotViewHolder) {
                Stock_Lot_Emplacement_Light stock_courant = listeStockLotEmplacement.get(position);
                LotAdapter_V2.LotViewHolder monViewHolder = (LotAdapter_V2.LotViewHolder) viewHolder;
                int qte_Saisie = Integer.parseInt(monViewHolder.qteSaisie.getText().toString());
                MAJValues(false, qte_Saisie);
                monViewHolder.qteSaisie.setText("0");
                stock_courant.setQte_Preparer(0);
                Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock_courant);
                ((LotAdapter_V2.LotViewHolder) viewHolder).isSwipedOpen = false;
                supprimerPhPreparationLigne(ph_preparation_ligne_base, stock_courant);

                listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);

                Collections.sort(listeStockLotEmplacement, new Comparator<Stock_Lot_Emplacement_Light>() {
                    @Override
                    public int compare(Stock_Lot_Emplacement_Light o1, Stock_Lot_Emplacement_Light o2) {
                        return o1.getPeremptionDate().compareTo(o2.getPeremptionDate());
                    }
                });

                for(Stock_Lot_Emplacement_Light courant: listeStockLotEmplacement)
                {
                    listelot.add(courant.getLot());
                }

                if(!produitserialiserreception && produitsuiviserie)
                {
                    listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepotSerie(db, produit, depot);
                }

                //on recherche la ligne de stock utilisé pour la supprimer aussi
                StockUtilises stockUtilises = StockUtilisesOpenHelper.getStockUtiliserByStockIdAndUser(db, stock_courant.get_UID(), utilisateurConnecte.getId());

                if(stockUtilises != null)
                {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, StockUtilisesOpenHelper.Constantes.TABLE_STOCK_UTILISE, stockUtilises.getphiwms_mobileUUID(), stockUtilises.getphiwms_mobileUUID(), DBOpenHelper.ActionsEAS.SUPPR);
                    ElementASynchroniserOpenHelper.toutSynchroniser(ListeLotPreparationActivity.this, db, utilisateurConnecte, false);
                    StockUtilisesOpenHelper.supprimerUnStockUtilise(db, stockUtilises);
                }

                adapter.notifyItemChanged(position);

                MAJVisuel();
                onResume();
            }

            // Tu peux appeler confirm dialog ici
        }, ListeLotPreparationActivity.this);

        recyclerView.setAdapter(adapter);

        int nbColis = recupererNbColis(produit.getID_produit(), ph_preparation_ligne_base.getQte_APreparer());
        ((TextView) findViewById(R.id.colis)).setText(String.valueOf(nbColis));
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
                    ValiderQuantiteSaisie(pos, qte);
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
