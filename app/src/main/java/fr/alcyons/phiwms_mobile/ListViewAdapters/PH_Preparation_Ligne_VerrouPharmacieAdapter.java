package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_VerrouPharmacie_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

public class PH_Preparation_Ligne_VerrouPharmacieAdapter extends ArrayAdapter {

    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdapteList;
    public List<PH_Preparation_Ligne_AdapteViewHolder> viewHolderList;
    Context context;
    SQLiteDatabase db;
    int nb_inactive;
    boolean first_passage;
    OnDataChangeListener mOnDataChangeListener;

    private static final DateFormat DATE_FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("dd/MM/yyyy");

    View.OnClickListener clicDate = v -> {
        DatePickerFragment newFragment = new DatePickerFragment();
        PH_Preparation_Ligne_AdapteViewHolder viewHolder = null;
        for (PH_Preparation_Ligne_AdapteViewHolder viewHolderC : viewHolderList) {
            if (viewHolderC.datePeremption == v.findViewById(R.id.datePeremption)) {
                viewHolder = viewHolderC;
                break;
            }
        }
        if (viewHolder != null) {
            newFragment.setViewHolder(viewHolder, ((MenuActivity) context).db,
                    phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
            newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "datePicker");
        }
    };

    public PH_Preparation_Ligne_VerrouPharmacieAdapter(Context context, SQLiteDatabase database,
                                                       List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdapteList) {
        super(context, 0, phPreparationLigneVerrouPharmacieAdapteList);
        this.context = context;
        this.db = database;
        this.phPreparationLigneVerrouPharmacieAdapteList = phPreparationLigneVerrouPharmacieAdapteList;
        this.first_passage = false;
        this.nb_inactive = 0;

        this.viewHolderList = new ArrayList<>();
        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte item : phPreparationLigneVerrouPharmacieAdapteList) {
            this.viewHolderList.add(new PH_Preparation_Ligne_AdapteViewHolder(item));
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Ferme le clavier si ouvert
        fermerClavier();

        final PH_Preparation_Ligne_AdapteViewHolder viewHolder = viewHolderList.get(position);
        final PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte =
                (PH_Preparation_Ligne_VerrouPharmacie_Adapte) getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.row_ph_preparation_ligne_verrou_pharmacie, parent, false);

        // Binding des vues
        bindVues(viewHolder, convertView);

        if (phPrepAdapte != null) {
            // Affichage des données
            afficherDonnees(viewHolder, phPrepAdapte);

            // Statut verrouillage
            appliquerStatutVerrouillage(viewHolder);

            // Listeners
            configurerListeners(viewHolder, phPrepAdapte, position);

            // Gestion de la sérialisation
            gererSerialisation(viewHolder, phPrepAdapte, position);
        }

        // Alerte produit inactif en fin de liste
        if (position + 1 == viewHolderList.size() && !first_passage && nb_inactive > 0) {
            first_passage = true;
            Alerte.afficherAlerte(context, "Erreur",
                    "Produit inactif détecté, vérifiez vos surveillances références", "alerte");
        }

        return convertView;
    }

    // ─── Méthodes privées ───────────────────────────────────────────────────

    private void fermerClavier() {
        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }
    }

    private void bindVues(PH_Preparation_Ligne_AdapteViewHolder viewHolder, View convertView) {
        viewHolder.designation    = convertView.findViewById(R.id.designationProduit);
        viewHolder.produitRef     = convertView.findViewById(R.id.referenceProduit);
        viewHolder.nbColis        = convertView.findViewById(R.id.colis);
        viewHolder.qteParLot      = convertView.findViewById(R.id.qte_par_lot);
        viewHolder.numLot         = convertView.findViewById(R.id.numLot);
        viewHolder.datePeremption = convertView.findViewById(R.id.datePeremption);
        viewHolder.textviewSerie  = convertView.findViewById(R.id.textviewSerie);
        viewHolder.layoutQuantite = convertView.findViewById(R.id.layoutQuantite);
        viewHolder.numeroSerie    = convertView.findViewById(R.id.numeroSerie);
        viewHolder.layoutNumSerie = convertView.findViewById(R.id.layoutNumSerie);
        viewHolder.layoutPrincipal = convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.statutLigne    = convertView.findViewById(R.id.statutLigne);
    }

    private void afficherDonnees(PH_Preparation_Ligne_AdapteViewHolder viewHolder,
                                 PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte) {

        viewHolder.designation.setText(phPrepAdapte.getDesignation());
        viewHolder.produitRef.setText(phPrepAdapte.getProduitRef());
        viewHolder.nbColis.setText(phPrepAdapte.getNbColis());
        viewHolder.statutLigne.setText(viewHolder.valeurStatut);

        if (viewHolder.produit_inconnu) return;

        // Quantité
        if (viewHolder.valeurQteParLot > -1) {
            viewHolder.qteParLot.setText(String.valueOf(viewHolder.valeurQteParLot));
        } else {
            String qte = phPrepAdapte.getQteParLot();
            viewHolder.qteParLot.setText(qte.equals("0") ? "" : qte);
            viewHolder.valeurQteParLot = Integer.parseInt(qte);
        }

        // Lot
        if (!viewHolder.valeurLot.isEmpty()) {
            viewHolder.numLot.setText(viewHolder.valeurLot);
        } else {
            viewHolder.numLot.setText(phPrepAdapte.getNumLot());
            viewHolder.valeurLot = phPrepAdapte.getNumLot();
        }

        // Série
        String serie = phPrepAdapte.getSerieNumero();
        if (serie == null || serie.isEmpty()) {
            viewHolder.layoutNumSerie.setVisibility(View.GONE);
            viewHolder.valeurSerie = "";
        } else {
            viewHolder.numeroSerie.setText(serie);
            viewHolder.valeurSerie = serie;
        }

        // Date péremption
        if (viewHolder.valeurDate.isEmpty()) {
            try {
                String dateStr = phPrepAdapte.getPeremptionDate();
                if (dateStr != null && dateStr.length() >= 10) {
                    Date date = DATE_FORMAT_SQL.parse(dateStr.substring(0, 10));
                    viewHolder.valeurDate = DATE_FORMAT_DISPLAY.format(date);
                    viewHolder.setDatePeremptionColor(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!viewHolder.valeurDate.isEmpty()) {
            viewHolder.datePeremption.setText(viewHolder.valeurDate);
            try {
                Date date = DATE_FORMAT_DISPLAY.parse(viewHolder.valeurDate);
                viewHolder.setDatePeremptionColor(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.datePeremption.setText("");
            viewHolder.setDatePeremptionColor(null);
        }
    }

    private void appliquerStatutVerrouillage(PH_Preparation_Ligne_AdapteViewHolder viewHolder) {
        boolean deVerrouille = viewHolder.valeurStatut.equals("Déverrouillé");
        viewHolder.layoutQuantite.setCardBackgroundColor(
                context.getColor(deVerrouille ? R.color.vert : R.color.rouge2));
        viewHolder.statutLigne.setText(deVerrouille ? "Déverrouillé" : "Verrouillé");
    }

    private void configurerListeners(PH_Preparation_Ligne_AdapteViewHolder viewHolder,
                                     PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte, int position) {

        // Date
        viewHolder.datePeremption.setOnClickListener(clicDate);

        // Verrouillage / Déverrouillage
        viewHolder.layoutQuantite.setOnClickListener(v -> {
            PH_Preparation_Ligne_AdapteViewHolder item = viewHolderList.get(position);
            boolean estVerrouille = item.valeurStatut.equals("Verrouillé");
            item.valeurStatut = estVerrouille ? "Déverrouillé" : "Verrouillé";
            appliquerStatutVerrouillage(item);
            notifyDataSetChanged();
        });

        // Long clic → NumberPicker
        viewHolder.qteParLot.setOnLongClickListener(v -> {
            String title = viewHolderList.get(position).designation.getText().toString();
            String message = "Quantité déverrouillée : ";
            int maxValue = viewHolder.valeurQteDemander;
            int value = 0;

            String qteText = viewHolderList.get(position).qteParLot.getText().toString();
            if (!qteText.isEmpty()) {
                value = Integer.parseInt(qteText);
            }

            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue,
                    (dialog, id) -> {
                        int qteApres = aNumberPicker.getValue();
                        viewHolderList.get(position).qteParLot.setText(String.valueOf(qteApres));
                        phPrepAdapte.setQteParLot(String.valueOf(qteApres));
                        viewHolder.valeurQteParLot = qteApres;
                        viewHolderList.get(position).valeurStatut = "Déverrouillé";
                        notifyDataSetChanged();

                        InputMethodManager imm = (InputMethodManager)
                                context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    });
            return true;
        });

        // TextWatcher et KeyListener
        new GestionnaireEditText(viewHolder, phPrepAdapte);
    }

    private void gererSerialisation(PH_Preparation_Ligne_AdapteViewHolder viewHolder,
                                    PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte, int position) {

        String serie = phPrepAdapte.getSerieNumero();
        if (serie == null) {
            phPrepAdapte.setSerieNumero("");
            return;
        }

        if (serie.isEmpty() || serie.equals("null")) return;

        String numeroCommande = phPrepAdapte.getPhPreparation().getListe().trim();
        String[] tabNumero = numeroCommande.split(" ");
        numeroCommande = tabNumero[tabNumero.length - 1];

        PH_Serialisation serialisationCourant = null;
        if (phPrepAdapte.getProduitCorrespondant() != null) {
            serialisationCourant = PH_SerialisationOpenHelper.getPH_SerialisationVerrou(
                    db, numeroCommande, phPrepAdapte.getProduitCorrespondant().getID_produit());
        }

        if (serialisationCourant == null) return;

        String resultat = serialisationCourant.getResultat();
        if (!resultat.equals("INACTIVE") && !resultat.equals("UNKNOWN")) return;

        if (first_passage) {
            // Restaure les valeurs sauvegardées
            viewHolder.datePeremption.setText(viewHolder.valeurDate.isEmpty() ? "00/00/0000" : viewHolder.valeurDate);
            viewHolder.qteParLot.setText(String.valueOf(viewHolder.valeurQteParLot));
            viewHolder.numeroSerie.setText(viewHolder.valeurSerie);
            viewHolder.numLot.setText(viewHolder.valeurLot);
        } else {
            // Reset
            viewHolder.numLot.setText("");
            viewHolder.valeurLot = "";
            viewHolder.numeroSerie.setText("");
            viewHolder.valeurSerie = "";
            viewHolder.valeurQteParLot = 0;
            viewHolder.valeurDate = "00/00/0000";
            viewHolder.datePeremption.setText("00/00/0000");
            viewHolder.qteParLot.setText("0");
            viewHolder.produit_inconnu = true;
            nb_inactive++;
        }
    }

    // ─── Mise à jour BDD ────────────────────────────────────────────────────

    public void mettreAJourPHPrepLigne(PH_Preparation_Ligne_AdapteViewHolder viewHolder,
                                       PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {

        PH_Preparation_Ligne phPrepLigne = phPrepLigneAdapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLotEmplacement = phPrepLigneAdapte.getStockLotEmplacement();

        stockLotEmplacement.setLot(viewHolder.valeurLot);
        stockLotEmplacement.setQte_Preparer(viewHolder.valeurQteParLot);

        try {
            Date dateFournie = DATE_FORMAT_DISPLAY.parse(viewHolder.valeurDate);
            String dateSQL = DATE_FORMAT_SQL.format(dateFournie);
            stockLotEmplacement.setPeremptionDate(dateSQL);
            phPrepLigneAdapte.setPeremptionDate(dateSQL);
            phPrepLigne.setPeremptionDate(dateSQL);
            viewHolder.setDatePeremptionColor(dateFournie);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement);

        int qteAvant = 0;
        if (phPrepLigneAdapte.getNbColis() != null && !phPrepLigneAdapte.getNbColis().isEmpty()) {
            qteAvant = Integer.parseInt(phPrepLigneAdapte.getNbColis());
        }

        int qteApres = viewHolder.recupererNbColis(
                phPrepLigneAdapte.getProduitCorrespondant().getID_produit(),
                viewHolder.valeurQteParLot);

        phPrepLigneAdapte.setNbColis(String.valueOf(qteApres));
        phPrepLigneAdapte.setNumLot(viewHolder.valeurLot);
        phPrepLigneAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));

        phPrepLigne.setLotNumero(viewHolder.valeurLot);
        phPrepLigne.setQte_RAL(viewHolder.valeurQteParLot);
        phPrepLigne.setSerieNumero(viewHolder.valeurSerie);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPrepLigne);

        if (mOnDataChangeListener != null) {
            mOnDataChangeListener.onDataChanged(qteAvant, qteApres);
        }
    }

    @Override
    public void clear() {
        phPreparationLigneVerrouPharmacieAdapteList.clear();
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    public interface OnDataChangeListener {
        void onDataChanged(int quantitéAvant, int quantitéAprès);
    }

    // ─── DatePickerFragment ─────────────────────────────────────────────────

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        PH_Preparation_Ligne_AdapteViewHolder viewHolder;
        SQLiteDatabase db;
        PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte;

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            return new DatePickerDialog(getActivity(), this,
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthStr = month < 9 ? "0" + (month + 1) : String.valueOf(month + 1);
            String dayStr = day < 10 ? "0" + day : String.valueOf(day);
            String date = dayStr + "/" + monthStr + "/" + year;

            viewHolder.datePeremption.setText(date);
            viewHolder.valeurDate = date;

            PH_Preparation_Ligne phPrepLigne = phPrepLigneAdapte.getPhPreparationLigne();
            Stock_Lot_Emplacement_Light stockLotEmplacement = phPrepLigneAdapte.getStockLotEmplacement();

            stockLotEmplacement.setLot(viewHolder.valeurLot);
            stockLotEmplacement.setQte_Preparer(viewHolder.valeurQteParLot);

            try {
                Date dateFournie = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                viewHolder.setDatePeremptionColor(dateFournie);
                String dateSQL = new SimpleDateFormat("yyyy-MM-dd").format(dateFournie);
                stockLotEmplacement.setPeremptionDate(dateSQL);
                phPrepLigneAdapte.setPeremptionDate(dateSQL);
                phPrepLigne.setPeremptionDate(dateSQL);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement);
            phPrepLigneAdapte.setNumLot(viewHolder.valeurLot);
            phPrepLigneAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));
            phPrepLigne.setLotNumero(viewHolder.valeurLot);
            phPrepLigne.setQte_RAL(viewHolder.valeurQteParLot);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPrepLigne);
        }

        public void setViewHolder(PH_Preparation_Ligne_AdapteViewHolder view,
                                  SQLiteDatabase database, PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte) {
            viewHolder = view;
            db = database;
            phPrepLigneAdapte = adapte;
        }
    }

    // ─── GestionnaireEditText ───────────────────────────────────────────────

    private class GestionnaireEditText {

        public GestionnaireEditText(PH_Preparation_Ligne_AdapteViewHolder viewHolder,
                                    PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {

            viewHolder.numLot.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    viewHolder.valeurLot = s.toString();
                    mettreAJourPHPrepLigne(viewHolder,
                            phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                }
            });

            viewHolder.numeroSerie.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    viewHolder.valeurSerie = s.toString();
                    mettreAJourPHPrepLigne(viewHolder,
                            phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                }
            });

            viewHolder.qteParLot.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    String qteText = viewHolder.qteParLot.getText().toString();
                    viewHolder.valeurQteParLot = qteText.isEmpty() ? 0 : Integer.parseInt(qteText);

                    int nbColis = viewHolder.recupererNbColis(
                            phPrepLigneAdapte.getProduitCorrespondant().getID_produit(),
                            viewHolder.valeurQteParLot);
                    viewHolder.nbColis.setText(String.valueOf(nbColis));

                    mettreAJourPHPrepLigne(viewHolder,
                            phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                }
                return false;
            });
        }
    }

    // ─── ViewHolder ─────────────────────────────────────────────────────────

    public class PH_Preparation_Ligne_AdapteViewHolder {
        public TextView designation;
        public TextView produitRef;
        public TextView qteParLot;
        public TextView numLot;
        public TextView nbColis;
        public TextView datePeremption;
        public TextView textviewSerie;
        public TextView statutLigne;
        public EditText numeroSerie;
        public LinearLayout layoutNumSerie;
        public LinearLayout layoutPrincipal;
        public CardView layoutQuantite;
        public boolean produit_inconnu = false;
        public int valeurQteParLot = -1;
        public int valeurQteDemander;
        public String valeurDate = "";
        public String valeurLot = "";
        public String valeurSerie = "";
        public String valeurStatut = "Verrouillé";

        public PH_Preparation_Ligne_AdapteViewHolder(PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigne) {
            String qte = phPrepLigne.getQteParLot();
            if (qte != null && !qte.isEmpty()) {
                this.valeurQteDemander = Integer.parseInt(qte);
                this.valeurQteParLot = Integer.parseInt(qte);
            }
            this.valeurLot = phPrepLigne.getNumLot();

            try {
                Date date = DATE_FORMAT_SQL.parse(phPrepLigne.getPeremptionDate());
                this.valeurDate = DATE_FORMAT_DISPLAY.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void setDatePeremptionColor(Date date) {
            if (date == null) {
                datePeremption.setTextColor(Color.BLACK);
                return;
            }

            long diff = new Date().getTime() - date.getTime();
            int delai = (int) (diff / (1000 * 60 * 60 * 24));

            if (delai >= -30) {
                datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2, null));
            } else if (delai >= -60) {
                datePeremption.setTextColor(context.getResources().getColor(R.color.orange2, null));
            } else {
                datePeremption.setTextColor(context.getResources().getColor(R.color.vert, null));
            }
        }

        public int recupererNbColis(int produitID, double qte) {
            if (produitID == 0 || qte == 0) return 0;

            Produit produit = ProduitOpenHelper.getProduitByID(db, produitID);
            int conditionnement = produit.getCond_achat();
            if (conditionnement == 0) {
                conditionnement = (int) produit.getCond_distrib();
            }

            if (conditionnement == 0) return qte > 0 ? 1 : 0;

            int nbColis = (int) Math.ceil(qte / conditionnement);
            return nbColis == 0 && qte > 0 ? 1 : nbColis;
        }
    }
}