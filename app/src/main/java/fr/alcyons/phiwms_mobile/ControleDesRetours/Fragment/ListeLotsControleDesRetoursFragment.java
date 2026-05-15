package fr.alcyons.phiwms_mobile.ControleDesRetours.Fragment;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_CODE_GS1;
import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LOT;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerRetourActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ControleDesRetours.CreationLotControleDesRetoursActivity;
import fr.alcyons.phiwms_mobile.ControleDesRetours.LotControleDesRetoursActions;
import fr.alcyons.phiwms_mobile.ListViewAdapters.LotControleDesRetoursAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ListeLotsControleDesRetoursFragment extends Fragment implements LotControleDesRetoursActions {
    private static final String ARG_RETOUR_LIGNE_ID = "retourLigneId";
    private static final String ARG_PRODUIT_ID = "produitID";

    public interface OnLotsControleValidesListener { void onLotsControleValides(); }

    private OnLotsControleValidesListener listener;
    private LotControleDesRetoursAdapter adapter;
    private int quantiteRetournee = 0;
    private int qteDeclaree;
    private int quantiteRestant;
    private Depot depot;
    private Produit produit;
    private Retour_Ligne retourLigne;
    private Retour retourCourant;
    private RecyclerView recyclerView;
    private List<Stock_Lot_Emplacement_Light> listeStockLotEmplacement = new ArrayList<>();
    private List<String> listelot = new ArrayList<>();
    private SQLiteDatabase db;

    public static ListeLotsControleDesRetoursFragment newInstance(int retourLigneId, int produitId) {
        ListeLotsControleDesRetoursFragment fragment = new ListeLotsControleDesRetoursFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RETOUR_LIGNE_ID, retourLigneId);
        args.putInt(ARG_PRODUIT_ID, produitId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = context instanceof OnLotsControleValidesListener ? (OnLotsControleValidesListener) context : null;
    }

    @Override public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_liste_lots_controle_des_retours_2025, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = ((ServiceActivity) requireActivity()).db;
        Bundle args = requireArguments();
        retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, args.getInt(ARG_RETOUR_LIGNE_ID));
        if (retourLigne == null) { return; }
        retourCourant = RetourOpenHelper.getRetourByID(db, retourLigne.getRetour_UID());
        depot = DepotOpenHelper.getDepotParReference(db, retourCourant.getRef_Depot_Origine());
        produit = ProduitOpenHelper.getProduitByID(db, args.getInt(ARG_PRODUIT_ID));

        view.findViewById(R.id.btnAjoutManuel).setOnClickListener(v -> ouvrirCreationLotManuelle());
        view.findViewById(R.id.lancerScan).setOnClickListener(v -> lancerScan());
        bindLots();
    }

    @Override public void onResume() {
        super.onResume();
        bindLots();
    }

    private void bindLots() {
        View view = getView();
        if (view == null || produit == null || depot == null) { return; }

        listeStockLotEmplacement = Stock_Lot_EmplacementLightOpenHelper.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot);
        listelot = new ArrayList<>();
        for (Stock_Lot_Emplacement_Light tempLot : listeStockLotEmplacement) { listelot.add(tempLot.getLot()); }

        qteDeclaree = (int) retourLigne.getQte_Demander();
        quantiteRestant = qteDeclaree;
        quantiteRetournee = 0;
        List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());
        for (Retour_Ligne temp : retourLigneRetourner) {
            quantiteRetournee += (int) temp.getQte_Retourner();
            quantiteRestant -= (int) temp.getQte_Retourner();
        }

        int nbColis = 0;
        if (produit.getCond_distrib() > 0) { nbColis = (int) Math.ceil(quantiteRestant / produit.getCond_distrib()); }

        ((TextView) view.findViewById(R.id.QteRetourner)).setText(String.valueOf(quantiteRetournee));
        ((TextView) view.findViewById(R.id.qteDeclaree)).setText(String.valueOf(qteDeclaree));
        ((TextView) view.findViewById(R.id.designationProduit)).setText(produit.getDesignation_interne());
        ((TextView) view.findViewById(R.id.referenceProduit)).setText(produit.getRef_fourni());
        ((TextView) view.findViewById(R.id.numeroRetour)).setText("#" + retourCourant.getNumero());
        ((TextView) view.findViewById(R.id.nomDepot)).setText(depot.getNom());
        ((TextView) view.findViewById(R.id.colis)).setText(String.valueOf(nbColis));

        recyclerView = view.findViewById(R.id.liste_view_lot_retour_ligne);
        while (recyclerView.getItemDecorationCount() > 0) { recyclerView.removeItemDecorationAt(0); }
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider));
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LotControleDesRetoursAdapter(listeStockLotEmplacement, this::supprimerLotPosition, requireContext(), this);
        recyclerView.setAdapter(adapter);
        majVisuel();
    }

    private void ouvrirCreationLotManuelle() {
        Bundle bundle = ((ServiceActivity) requireActivity()).getBundle();
        bundle.putInt("produitID", produit.getID_produit());
        bundle.putInt("depotID", depot.getDepot_UID());
        bundle.putInt("retourUID", retourCourant.get_UID());
        bundle.putInt("retourLigneID", retourLigne.get_UID());
        Intent intent = new Intent(requireContext(), CreationLotControleDesRetoursActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RETOUR_LOT);
    }

    private void lancerScan() {
        List<Retour_Ligne> listeScannerRetourLigne = new ArrayList<>();
        listeScannerRetourLigne.add(retourLigne);
        Bundle bundle = ((ServiceActivity) requireActivity()).getBundle();
        bundle.putBoolean("isBoutonSuppressionExistant", true);
        bundle.putSerializable("RetourCourant", (Serializable) retourCourant);
        bundle.putSerializable("DepotOrigine", (Serializable) depot);
        bundle.putStringArrayList("liste_lot", (ArrayList<String>) listelot);
        bundle.putSerializable("ListeRetourLigne", (Serializable) listeScannerRetourLigne);
        bundle.putBoolean("EmplacementUF", true);

        Intent intent = new Intent(requireContext(), ScannerRetourActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, RETOUR_CODE_GS1);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && (requestCode == RETOUR_CODE_GS1 || requestCode == RETOUR_LOT)) { bindLots(); }
    }

    @Override public void ClickNumberPicker(final int position) {
        Stock_Lot_Emplacement_Light stockCourant = adapter.getLotAt(position);
        if (stockCourant.getQte_Preparer() > 0) {
            int qteAvant = stockCourant.getQte_Preparer();
            stockCourant.setQte_Preparer(0);
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockCourant);
            supprimerUnRetourLigne(retourLigne, stockCourant);
            quantiteRestant += qteAvant;
            majVisuel();
        }

        int maxValue = (int) retourLigne.getQte_Demander();
        if (stockCourant.getQte() < maxValue) { maxValue = (int) stockCourant.getQte(); }
        if (maxValue > quantiteRestant) { maxValue = quantiteRestant; }
        int value = maxValue;

        android.content.DialogInterface.OnClickListener onClickListener = (dialog, id) -> {
            Stock_Lot_Emplacement_Light courant = adapter.getLotAt(position);
            int qteApres = aNumberPicker.getValue();
            courant.setQte_Preparer(qteApres);
            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);
            majValues(true, qteApres);
            enregistrementRetourLigne(retourLigne, courant);
            bindLots();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            dialog.dismiss();
        };

        Alerte.afficherAlerteNumberPicker(requireContext(), stockCourant.getLot(), "Quantité placée : ", value, maxValue, onClickListener);
    }

    @Override public void ClickLigneLot(int position) {
        Stock_Lot_Emplacement_Light courant = listeStockLotEmplacement.get(position);
        if (courant.getLot().contentEquals("")) {
            Alerte.afficherAlerteInformation(requireContext(), getLayoutInflater(), "Erreur", "Vous ne pouvez pas préparer un lot vide.", false, false);
            return;
        }
        if (quantiteRestant == 0 || courant.getQte() == 0) { return; }

        int quantiteStockSelectionne = (int) courant.getQte();
        if (quantiteStockSelectionne > quantiteRestant) { quantiteStockSelectionne = quantiteRestant; }
        courant.setQte_Preparer(quantiteStockSelectionne);
        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, courant);
        majValues(true, quantiteStockSelectionne);
        enregistrementRetourLigne(retourLigne, courant);
        bindLots();
    }

    private void supprimerLotPosition(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (!(viewHolder instanceof LotControleDesRetoursAdapter.LotViewHolder)) { return; }
        Stock_Lot_Emplacement_Light stockCourant = listeStockLotEmplacement.get(position);
        LotControleDesRetoursAdapter.LotViewHolder holder = (LotControleDesRetoursAdapter.LotViewHolder) viewHolder;
        int qteSaisie = Integer.parseInt(holder.qteSaisie.getText().toString());
        majValues(false, qteSaisie);
        holder.qteSaisie.setText("0");
        stockCourant.setQte_Preparer(0);
        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockCourant);
        supprimerUnRetourLigne(retourLigne, stockCourant);
        bindLots();
    }

    private void majVisuel() {
        View view = getView();
        if (view == null) { return; }
        if (quantiteRestant == 0) {
            ((LinearLayout) view.findViewById(R.id.firstRow)).setBackground(requireContext().getResources().getDrawable(R.drawable.background_cadre_vert));
            ((TextView) view.findViewById(R.id.qteDeclaree)).setTextColor(requireContext().getResources().getColor(R.color.vert));
            view.findViewById(R.id.lancerScan).setVisibility(View.GONE);
            view.findViewById(R.id.QteRetourner).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.btnAjoutManuel).setVisibility(View.INVISIBLE);
        }
        else {
            ((LinearLayout) view.findViewById(R.id.firstRow)).setBackground(requireContext().getResources().getDrawable(R.drawable.background_cadre_orange));
            view.findViewById(R.id.lancerScan).setVisibility(View.VISIBLE);
            view.findViewById(R.id.QteRetourner).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.qteDeclaree)).setTextColor(requireContext().getResources().getColor(R.color.noir));
            view.findViewById(R.id.btnAjoutManuel).setVisibility(View.VISIBLE);
        }
    }

    private void majValues(boolean ajout, int quantiteAModifier) {
        quantiteRetournee = ajout ? quantiteRetournee + quantiteAModifier : quantiteRetournee - quantiteAModifier;
        quantiteRestant = qteDeclaree - quantiteRetournee;
    }

    public void onMenuSaveClick() {
        if (listener != null) { listener.onLotsControleValides(); }
    }

    private void enregistrementRetourLigne(Retour_Ligne retourLigneBase, Stock_Lot_Emplacement_Light stockCourant) {
        Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneBase);
        int retourLigneId = new Random().nextInt();
        if (retourLigneId > 0) { retourLigneId *= -1; }
        retourLigneCourant.set_UID(retourLigneId);
        retourLigneCourant.setQte_Retourner(stockCourant.getQte_Preparer());
        retourLigneCourant.setLot_Retourner(stockCourant.getLot());
        retourLigneCourant.setSerie_Retourner(stockCourant.getSerie());
        retourLigneCourant.setPeremptionDate(stockCourant.getPeremptionDate());
        Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
    }

    private void supprimerUnRetourLigne(Retour_Ligne retourLigneBase, Stock_Lot_Emplacement_Light stockCourant) {
        Retour_Ligne retourLigneASupprimer = Retour_LigneOpenHelper.getRetourLigneNegByProduitLot(db, retourLigneBase.getRetour_UID(), retourLigneBase.getCode_produit(), stockCourant.getLot(), stockCourant.getSerie());
        if (retourLigneASupprimer != null) { Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retourLigneASupprimer); }
    }
}
