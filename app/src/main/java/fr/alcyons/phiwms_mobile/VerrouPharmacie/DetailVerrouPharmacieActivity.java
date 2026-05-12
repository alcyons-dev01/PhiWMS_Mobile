package fr.alcyons.phiwms_mobile.VerrouPharmacie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

    // ─── Données ────────────────────────────────────────────────────────────

    public PH_Preparation phPreparationSelectionne;
    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdaptes;
    public List<Stock_Lot_Emplacement_Light> stockLotEmplacementLightList;
    public PH_Preparation_Ligne_VerrouPharmacieAdapter phPreparationLigneVerrouPharmacieAdapter;

    public ListView phPreparationLigneListView;
    public PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolderAModifier;
    public TextView datePeremptionTextView;
    public EditText numeroLotEditText;

    // Compteurs locaux — source de vérité, jamais recalculés depuis les listeners
    private int nbColisVerrouille   = 0;
    private int nbColisDeverrouille = 0;

    // Vues compteurs
    private TextView tvNbColisTotal;
    private TextView tvNbColisDeverrouille;

    // Alerte composition
    TextView textViewNBPalette;
    TextView textViewNBCaisse;
    TextView textViewNBContainer;
    TextView textViewNBScelle;

    PackageManager pm;

    // ─── Scan code-barres ───────────────────────────────────────────────────

    public void decoderCodeBarre(EditText numLot, TextView date,
                                 PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder) {

        datePeremptionTextView = date;
        numeroLotEditText      = numLot;
        viewHolderAModifier    = viewHolder;

        PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte =
                phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList
                        .get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList
                                .indexOf(viewHolderAModifier));

        String designation = adapte.getProduitCorrespondant().getDesignation_interne();

        Bundle bundle = super.getBundle();
        bundle.putBoolean("doitEtreIdentique", true);
        bundle.putString("Designation", designation);
        bundle.putBoolean("isBoutonSuppressionExistant", true);

        Intent intent;
        if (android.os.Build.MANUFACTURER.contains("Zebra Technologies")
                || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell")) {
            intent = new Intent(this, ScannerSearchOnlyActivity.class);
        } else if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            intent = new Intent(this, BarcodeCaptureActivity.class);
        } else {
            intent = new Intent(this, ScannerSearchOnlyActivity.class);
        }

        intent.putExtras(bundle);
        startActivityForResult(intent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }

    // ─── Lifecycle ──────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_verrou_pharmacie);

        pm = getPackageManager();

        // Vues compteurs — récupérées une seule fois
        tvNbColisTotal       = findViewById(R.id.nbColisTotal);
        tvNbColisDeverrouille = findViewById(R.id.nbColisTotalReceptionner);

        // Préparation sélectionnée
        phPreparationSelectionne = PH_PreparationOpenHelper.getPH_PreparationByID(
                db, intent.getExtras().getInt("phPreparationSelectionneID"));

        // En-tête
        Depot depot = DepotOpenHelper.getDepotParID(db, phPreparationSelectionne.getDepotDestinataireID());
        ((TextView) findViewById(R.id.depotNom)).setText(depot.getNom());
        ((TextView) findViewById(R.id.preparationNumero))
                .setText("N° " + phPreparationSelectionne.getUID());

        Commande commandeCourante = CommandeOpenHelper.getCommandeByID(
                db, phPreparationSelectionne.getCommande_ID());
        String numeroCommande;
        if (commandeCourante != null) {
            ((TextView) findViewById(R.id.commandeNumero))
                    .setText("#" + commandeCourante.getNumero());
            ((TextView) findViewById(R.id.fournisseurCommande))
                    .setText(commandeCourante.getFournisseur());
            numeroCommande = commandeCourante.getNumero();
        } else {
            String[] tab = phPreparationSelectionne.getListe().split(" ");
            numeroCommande = tab[tab.length - 1];
            ((TextView) findViewById(R.id.commandeNumero)).setText("#" + numeroCommande);
            ((TextView) findViewById(R.id.fournisseurCommande)).setText("...");
        }

        // ListView
        phPreparationLigneListView = findViewById(R.id.listeView);
        phPreparationLigneListView.setItemsCanFocus(true);

        // Construction de la liste d'items
        phPreparationLigneVerrouPharmacieAdaptes = new ArrayList<>();
        construireLignes(numeroCommande);

        // Compteur initial — toutes les lignes sont verrouillées au départ
        nbColisVerrouille   = nbColisTotal();
        nbColisDeverrouille = 0;
        mettreAJourAffichageCompteurs();

        // Retour arrière
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                afficherAlerteConfirmationRetour(
                        DetailVerrouPharmacieActivity.this,
                        LayoutInflater.from(DetailVerrouPharmacieActivity.this),
                        DetailVerrouPharmacieActivity.super.getBundle());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tri alphabétique
        Collections.sort(phPreparationLigneVerrouPharmacieAdaptes,
                (o1, o2) -> o1.getDesignation().compareTo(o2.getDesignation()));

        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte item : phPreparationLigneVerrouPharmacieAdaptes)
            Log.d("DEBUG_LOT", "Désignation: " + item.getDesignation() + " | Lot: " + item.getNumLot());
        // Adapter
        phPreparationLigneVerrouPharmacieAdapter =
                new PH_Preparation_Ligne_VerrouPharmacieAdapter(
                        this, db, phPreparationLigneVerrouPharmacieAdaptes);

        // ─── Listener verrouillage ───────────────────────────────────────
        // Appelé UNIQUEMENT depuis le clic sur layoutQuantite dans l'adapter.
        // mettreAJourPHPrepLigne() NE DOIT PAS appeler onDataChanged.
        phPreparationLigneVerrouPharmacieAdapter.setOnDataChangeListener(
                (quantiteAvant, quantiteApres, estEnTrainDeDeverrouiller) -> {
                    if (estEnTrainDeDeverrouiller) {
                        // On vient de déverrouiller → retire du verrou, ajoute au déverrou
                        nbColisVerrouille   -= quantiteApres;
                        nbColisDeverrouille += quantiteApres;
                    } else {
                        // On vient de reverrouiller → retire du déverrou, remet dans verrou
                        nbColisVerrouille   += quantiteAvant;
                        nbColisDeverrouille -= quantiteAvant;
                    }
                    mettreAJourAffichageCompteurs();
                });

        phPreparationLigneListView.setAdapter(phPreparationLigneVerrouPharmacieAdapter);

        // Bouton valider
        ((LinearLayout) findViewById(R.id.btnValiderVerrou_LL))
                .setOnClickListener(v -> onClick_ActionContenant());
    }

    // ─── Construction des lignes ─────────────────────────────────────────────

    private void construireLignes(String numeroCommande) {
        List<PH_Preparation_Ligne> phPreparationLignes =
                PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(
                        db, phPreparationSelectionne);

        for (PH_Preparation_Ligne ligne : phPreparationLignes) {
            Produit produit = ProduitOpenHelper.getProduitByID(db, ligne.getProduitID());
            stockLotEmplacementLightList =
                    Stock_Lot_EmplacementLightOpenHelper
                            .getAllStock_Lot_EmplacementsByProduitIDEtCommandeNumero(
                                    db, produit, numeroCommande);

            if (!stockLotEmplacementLightList.isEmpty()) {
                for (Stock_Lot_Emplacement_Light stockLot : stockLotEmplacementLightList) {
                    int qteRALLot = ligne.getSuivi_Par_Lot() ? (int) stockLot.getQte() : 0;
                    int qteParLot = stockLot.getQte_Preparer() != 0
                            ? stockLot.getQte_Preparer() : (int) stockLot.getQte();

                    phPreparationLigneVerrouPharmacieAdaptes.add(
                            new PH_Preparation_Ligne_VerrouPharmacie_Adapte(
                                    ligne.getProduitDesignation(), ligne.getProduitReference(),
                                    String.valueOf(recupererNbColis(stockLot.getProduit_Code(), qteRALLot)),
                                    String.valueOf(qteRALLot), String.valueOf(qteParLot),
                                    stockLot.getLot(), stockLot.getPeremptionDate(),
                                    stockLot.getEmplacement(), stockLot, ligne,
                                    phPreparationSelectionne,
                                    ligne.isSuivi_Par_Serie(), ligne.isSerialiser_Reception(),
                                    ligne.getSerieNumero(), db, false));
                }
            } else {
                boolean estVerrou = phPreparationSelectionne.getListe()
                        .contentEquals("ALCYONS_VERROU");

                Stock_Lot_Emplacement_Light stockLotVide = estVerrou
                        ? new Stock_Lot_Emplacement_Light(
                        ligne.getQte_preparer(), "", "",
                        ligne.getEmplacementParDefaut(),
                        phPreparationSelectionne.getDepotDestinataireReference(),
                        ligne.getZoneDepot(), ligne.getProduitID(),
                        ligne.getQte_preparer(), "")
                        : new Stock_Lot_Emplacement_Light(0, "", "", "", "", "", 0, 0, "");

                phPreparationLigneVerrouPharmacieAdaptes.add(
                        new PH_Preparation_Ligne_VerrouPharmacie_Adapte(
                                ligne.getProduitDesignation(), ligne.getProduitReference(),
                                estVerrou ? "0" : "",
                                estVerrou ? String.valueOf(ligne.getQte_preparer()) : "",
                                estVerrou ? String.valueOf(ligne.getQte_preparer()) : "",
                                "", "", "",
                                stockLotVide, ligne, phPreparationSelectionne,
                                ligne.isSuivi_Par_Serie(), ligne.isSerialiser_Reception(),
                                ligne.getSerieNumero(), db, false));
            }
        }
    }

    // ─── Compteurs ──────────────────────────────────────────────────────────

    /** Calcule le total initial de colis (toutes lignes verrouillées). */
    private int nbColisTotal() {
        int total = 0;
        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte
                : phPreparationLigneVerrouPharmacieAdaptes) {
            String nbColisStr = adapte.getNbColis();
            if (nbColisStr != null && !nbColisStr.isEmpty()) {
                try { total += Integer.parseInt(nbColisStr); }
                catch (NumberFormatException ignored) {}
            }
        }
        return total;
    }

    /** Met à jour les deux TextViews compteurs depuis les variables locales. */
    private void mettreAJourAffichageCompteurs() {
        tvNbColisTotal.setText(String.valueOf(Math.max(0, nbColisVerrouille)));
        tvNbColisDeverrouille.setText(String.valueOf(Math.max(0, nbColisDeverrouille)));
    }

    // ─── Résultat scan GS1 ──────────────────────────────────────────────────

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (requestCode == CodesEchangesActivites.RETOUR_CODE_GS1) {
            String codeComplet = data.getStringExtra("code");
            if (codeComplet == null || codeComplet.isEmpty()) return;

            Map<String, String> gs1 = OutilsDecodage.decouperGTIN(codeComplet);

            if (gs1.size() == 1) {
                afficherToast("Le code fourni n'est pas un code GS1, veuillez réessayer.");
                return;
            }

            if (datePeremptionTextView == null || numeroLotEditText == null) {
                afficherToast("Le produit scanné ne correspond pas au produit attendu");
                return;
            }

            PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte =
                    phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList
                            .get(phPreparationLigneVerrouPharmacieAdapter.viewHolderList
                                    .indexOf(viewHolderAModifier));

            Produit produitScanner = ProduitOpenHelper.getUnProduitParGTIN(
                    db, gs1.get(OutilsDecodage.codeGtin));

            if (produitScanner == null) {
                afficherToast("Le produit scanné inconnu");
                return;
            }

            if (produitScanner.getID_produit()
                    != adapte.getProduitCorrespondant().getID_produit()) {
                afficherToast("Le produit scanné ne correspond pas au produit attendu");
                return;
            }

            DateFormat fmt1 = new SimpleDateFormat("yy-MM-dd");
            DateFormat fmt2 = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            try {
                date = fmt1.parse(gs1.get(OutilsDecodage.dateDePeremption));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateFinale = fmt2.format(date);
            datePeremptionTextView.setText(dateFinale);
            numeroLotEditText.setText(gs1.get(OutilsDecodage.numeroLot));
            viewHolderAModifier.valeurDate = dateFinale;
            viewHolderAModifier.valeurLot  = gs1.get(OutilsDecodage.numeroLot);
            mettreAJourPHPrepLigne(viewHolderAModifier, adapte);
        }
    }

    // ─── Calcul nb colis ────────────────────────────────────────────────────

    public int recupererNbColis(int produitID, double qte) {
        if (produitID == 0 || qte == 0) return 0;
        Produit p = ProduitOpenHelper.getProduitByID(db, produitID);
        if (p == null) return 0;

        int cond = p.getCond_achat();
        if (cond == 0) cond = (int) p.getCond_distrib();
        if (cond == 0) return 1;

        int nb = (int) Math.ceil(qte / cond);
        return nb == 0 ? 1 : nb;
    }

    // ─── Mise à jour BDD (sans toucher aux compteurs) ───────────────────────

    public void mettreAJourPHPrepLigne(
            PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder viewHolder,
            PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte) {

        PH_Preparation_Ligne ligne       = adapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLot = adapte.getStockLotEmplacement();

        DateFormat fmtSQL     = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fmtDisplay = new SimpleDateFormat("dd/MM/yyyy");

        stockLot.setLot(viewHolder.valeurLot);
        stockLot.setQte_Preparer(viewHolder.valeurQteParLot);

        try {
            Date d = fmtDisplay.parse(viewHolder.valeurDate);
            String dateSQL = fmtSQL.format(d);
            stockLot.setPeremptionDate(dateSQL);
            adapte.setPeremptionDate(dateSQL);
            ligne.setPeremptionDate(dateSQL);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLot);

        adapte.setNumLot(viewHolder.valeurLot);
        adapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));
        ligne.setLotNumero(viewHolder.valeurLot);
        ligne.setQte_RAL(viewHolder.valeurQteParLot);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
    }

    // ─── Validation ─────────────────────────────────────────────────────────

    public void onClick_ActionContenant() {
        boolean toutDeverrouille = true;
        for (PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder vh
                : phPreparationLigneVerrouPharmacieAdapter.viewHolderList) {
            if (vh.valeurStatut.contentEquals("Verrouillé")) {
                toutDeverrouille = false;
                break;
            }
        }

        if (!toutDeverrouille) {
            Alerte.afficherAlerte(this, "Alerte",
                    "Veuillez déverrouiller toutes les lignes", "alerte");
            return;
        }

        // Affichage de l'alerte composition
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alerte_preparation, null);

        LinearLayout LinearPalette   = view.findViewById(R.id.LinearPalette);
        LinearLayout LinearColis     = view.findViewById(R.id.LinearColis);
        LinearLayout LinearContainer = view.findViewById(R.id.LinearContainer);
        LinearLayout LinearScelle    = view.findViewById(R.id.LinearScelle);
        textViewNBPalette   = view.findViewById(R.id.nbPaletteSelectionne);
        textViewNBCaisse    = view.findViewById(R.id.nbColisSelectionne);
        textViewNBContainer = view.findViewById(R.id.nbContainerSelectionne);
        textViewNBScelle    = view.findViewById(R.id.nbScelleSelectionne);

        // Calcul nb colis pour pré-remplissage
        int nbColis = 0;
        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte courant
                : phPreparationLigneVerrouPharmacieAdaptes) {
            PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder vh =
                    phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(
                            phPreparationLigneVerrouPharmacieAdapter
                                    .phPreparationLigneVerrouPharmacieAdapteList.indexOf(courant));
            int qte = vh.valeurQteParLot == -1 ? vh.valeurQteDemander : vh.valeurQteParLot;
            if (qte > 0) {
                Produit p = ProduitOpenHelper.getProduitByID(
                        db, courant.getPhPreparationLigne().getProduitID());
                int cond = (int) p.getCond_distrib();
                if (cond == 0) cond = 1;
                int nb = qte / cond;
                nbColis += nb == 0 ? 1 : nb;
            }
        }
        textViewNBCaisse.setText(String.valueOf(nbColis));

        configurerNumberPickerColis();
        configurerNumberPickerPalette(LinearPalette);
        configurerNumberPickerContainer(LinearContainer);
        configurerSaisieScelle(LinearScelle);

        ImageView ok      = view.findViewById(R.id.ok);
        ImageView annuler = view.findViewById(R.id.annuler);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(v -> {
            int nbPalette  = parseOuZero(textViewNBPalette);
            int nbCaisse   = parseOuZero(textViewNBCaisse);
            int nbContainer = parseOuZero(textViewNBContainer);
            String scelle  = textViewNBScelle.getText().toString();

            phPreparationSelectionne.setColisNB(nbCaisse);
            phPreparationSelectionne.setPaletteNB(nbPalette);
            phPreparationSelectionne.setConteneur_NB(nbContainer);
            phPreparationSelectionne.setNumero_scelle(scelle);
            PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationSelectionne);
            alertDialog.dismiss();
            validerVerrou();
        });

        annuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    // ─── NumberPickers composition ───────────────────────────────────────────

    private void configurerNumberPickerColis() {
        textViewNBCaisse.setOnClickListener(v ->
                Alerte.afficherAlerteNumberPicker(this,
                        "Saisir le nombre de colis", "Nombre de colis : ", 0, 20,
                        (dialog, id) -> {
                            textViewNBCaisse.setText(String.valueOf(aNumberPicker.getValue()));
                            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                    .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();
                        }));
    }

    private void configurerNumberPickerPalette(LinearLayout zone) {
        View.OnClickListener listener = v ->
                Alerte.afficherAlerteNumberPicker(this,
                        "Saisir le nombre de palette", "Nombre de palettes : ", 0, 15,
                        (dialog, id) -> {
                            textViewNBPalette.setText(String.valueOf(aNumberPicker.getValue()));
                            dialog.dismiss();
                        });
        zone.setOnClickListener(listener);
        textViewNBPalette.setOnClickListener(listener);
    }

    private void configurerNumberPickerContainer(LinearLayout zone) {
        View.OnClickListener listener = v ->
                Alerte.afficherAlerteNumberPicker(this,
                        "Saisir le nombre de container", "Nombre de container : ", 0, 15,
                        (dialog, id) -> {
                            textViewNBContainer.setText(String.valueOf(aNumberPicker.getValue()));
                            dialog.dismiss();
                        });
        zone.setOnClickListener(listener);
        textViewNBContainer.setOnClickListener(listener);
    }

    private void configurerSaisieScelle(LinearLayout zone) {
        View.OnClickListener listener = v -> {
            String texte = Alerte.afficherAlerteEditText(this,
                    "Numéro de scellé", "Saisir un numéro de scellé");
            textViewNBScelle.setText(texte);
        };
        zone.setOnClickListener(listener);
        textViewNBScelle.setOnClickListener(listener);
    }

    // ─── Validation finale ───────────────────────────────────────────────────

    private void validerVerrou() {
        DateFormat fmtSQL     = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat fmtDisplay = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = Calendar.getInstance().getTime();

        boolean confirmation = Alerte.afficherAlerte(this,
                "Validation", "Êtes-vous sûr de vouloir valider ?", "OuiNon");
        if (!confirmation) return;

        // Création action utilisateur
        int actionId = genererIdNegatif();
        SimpleDateFormat parseFmt  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFmt   = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat heureFmt  = new SimpleDateFormat("HH:mm:ss");
        Date now         = new Date();
        String only_date = dateFmt.format(now);
        String only_heure = heureFmt.format(now);

        ActionUtilisateur action = new ActionUtilisateur(
                actionId, utilisateurConnecte.getId(), parseFmt.format(now),
                serviceActuel.getId(), utilisateurConnecte.getEtablissementId(),
                "En attente", phPreparationSelectionne.getUID(), "", "Verrou Pharmacie");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, action);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR,
                action.getPhiMR4UUID(), action.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        int compteurReussite = 0;

        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte
                : phPreparationLigneVerrouPharmacieAdapter.phPreparationLigneVerrouPharmacieAdapteList) {

            PH_Preparation_Ligne_VerrouPharmacieAdapter.PH_Preparation_Ligne_AdapteViewHolder vh =
                    phPreparationLigneVerrouPharmacieAdapter.viewHolderList.get(
                            phPreparationLigneVerrouPharmacieAdapter
                                    .phPreparationLigneVerrouPharmacieAdapteList.indexOf(adapte));

            PH_Preparation_Ligne ligne       = adapte.getPhPreparationLigne();
            Stock_Lot_Emplacement_Light stock = adapte.getStockLotEmplacement();

            // Duplication si lot différent
            if (adapte.getProduitCorrespondant().getID_produit() == ligne.getProduitID()
                    && !adapte.getNumLot().contentEquals(ligne.getLotNumero())
                    && !ligne.getLotNumero().contentEquals("")) {

                PH_Preparation_Ligne copie = new PH_Preparation_Ligne(ligne);
                int uid = genererIdNegatif();
                copie.set_UID(uid);
                copie.setPhiMR4UUID(uid);
                PH_Preparation_LigneOpenHelper.insererUnPH_Preparation_LigneEnBDD(db, copie);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                        PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                        copie.getPhiMR4UUID(), copie.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
            }

            stock.setLot(vh.valeurLot);
            int qtePrepared = vh.valeurQteParLot == -1 ? vh.valeurQteDemander : vh.valeurQteParLot;
            stock.setQte_Preparer(qtePrepared);

            Date datePeremption = null;
            try {
                datePeremption = fmtDisplay.parse(vh.valeurDate);
                String dateSQL = fmtSQL.format(datePeremption);
                stock.setPeremptionDate(dateSQL);
                adapte.setPeremptionDate(dateSQL);
                ligne.setPeremptionDate(dateSQL);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (datePeremption != null && dateDuJour.after(datePeremption)) {
                Alerte.afficherAlerte(this, "Erreur",
                        "Un produit de la liste est périmé", "alerte");
                return;
            }

            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stock);
            adapte.setNumLot(vh.valeurLot);
            ligne.setLotNumero(vh.valeurLot);
            ligne.setQte_preparer(stock.getQte_Preparer());
            ligne.setQte_APreparer(stock.getQte_Preparer());
            ligne.setQte_RAL(stock.getQte_Preparer());
            ligne.setEmplacementParDefaut(stock.getEmplacement());

            long rowID = PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
            if (rowID != -1) {
                compteurReussite++;
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                        PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                        ligne.getPhiMR4UUID(), ligne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                compteurReussite = 0;
            }

            // Action ligne
            int ligneId = genererIdNegatif();
            ActionUtilisateur_Ligne actionLigne = new ActionUtilisateur_Ligne(
                    ligneId, action.getId(), "PH_Preparation_Ligne",
                    ligne.get_UID(), "", 0, ligne.getQte_RAL(),
                    ligne.getProduitDesignation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionLigne);
        }

        int total = phPreparationLigneVerrouPharmacieAdapter
                .phPreparationLigneVerrouPharmacieAdapteList.size();

        if (compteurReussite == total) {
            phPreparationSelectionne.setStatut(getString(R.string.statutDéverrouillée));
            phPreparationSelectionne.setPharmacien_userID(utilisateurConnecte.getId());
            phPreparationSelectionne.setDelivranceValider_Par(utilisateurConnecte.getId());
            phPreparationSelectionne.setDelivranceValider_Le(only_date);
            phPreparationSelectionne.setDelivranceValider_A(only_heure);

            long rowID = PH_PreparationOpenHelper.mettreAJourUnPHPreparation(
                    db, phPreparationSelectionne);
            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                        PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION,
                        phPreparationSelectionne.getPhiMR4UUID(),
                        phPreparationSelectionne.getUID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                compteurReussite = 0;
            }
        }

        if (compteurReussite != total) {
            Alerte.afficherAlerte(this, "Alerte",
                    "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
            finish();
            return;
        }

        Toast.makeText(this, "Préparation déverrouillée", Toast.LENGTH_SHORT).show();
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(
                    this, db, utilisateurConnecte, true);
        }
        onBackPressed();
    }

    // ─── Alerte confirmation retour ──────────────────────────────────────────

    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater,
                                                 Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation, null);

        LinearLayout zoneOk      = layout.findViewById(R.id.buttonOk);
        LinearLayout zoneAnnuler = layout.findViewById(R.id.buttonAnnuler);
        ((TextView) layout.findViewById(R.id.messageFin))
                .setText("Vous allez quitter le verrou, confirmez vous ?");
        builder.setView(layout);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        zoneOk.setOnClickListener(v -> {
            alertDialog.dismiss();
            retourService(bundle);
        });
        zoneAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }

    @Override
    public void retourService(Bundle bundle) {
        Intent intent = new Intent(this, ServiceVerrouPharmacieActivity.class);
        intent.putExtras(super.getBundle());
        startActivity(intent);
        finish();
    }

    // ─── Utilitaires ────────────────────────────────────────────────────────

    private void afficherToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private int parseOuZero(TextView tv) {
        String s = tv.getText().toString();
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }

    private int genererIdNegatif() {
        int id = new Random().nextInt();
        return id > 0 ? -id : id;
    }
}