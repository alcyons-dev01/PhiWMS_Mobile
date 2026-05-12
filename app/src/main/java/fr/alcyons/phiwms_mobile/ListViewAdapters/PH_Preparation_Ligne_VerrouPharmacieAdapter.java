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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class PH_Preparation_Ligne_VerrouPharmacieAdapter extends ArrayAdapter<PH_Preparation_Ligne_VerrouPharmacie_Adapte> {

    // ─── Données ────────────────────────────────────────────────────────────

    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdapteList;
    public List<PH_Preparation_Ligne_AdapteViewHolder> viewHolderList;

    Context context;
    SQLiteDatabase db;
    int nb_inactive;
    boolean first_passage;
    OnDataChangeListener mOnDataChangeListener;

    /**
     * Cache des conditionnements par produitID — évite les requêtes BDD répétées
     * dans recupererNbColis() à chaque scroll / frappe clavier.
     */
    private final Map<Integer, Integer> conditionnementCache = new HashMap<>();

    /**
     * Résultats de sérialisation pré-chargés à l'init — indexés par position.
     * Valeur : résultat ("INACTIVE", "UNKNOWN", "") ou null si non applicable.
     */
    private final List<String> serialisationResultats = new ArrayList<>();

    private static final DateFormat DATE_FORMAT_SQL     = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("dd/MM/yyyy");

    // ─── Listener date ──────────────────────────────────────────────────────

    View.OnClickListener clicDate = v -> {
        DatePickerFragment newFragment = new DatePickerFragment();
        PH_Preparation_Ligne_AdapteViewHolder viewHolder = null;
        for (PH_Preparation_Ligne_AdapteViewHolder vh : viewHolderList) {
            if (vh.datePeremption == v.findViewById(R.id.datePeremption)) {
                viewHolder = vh;
                break;
            }
        }
        if (viewHolder != null) {
            newFragment.setViewHolder(viewHolder, ((MenuActivity) context).db,
                    phPreparationLigneVerrouPharmacieAdapteList.get(
                            viewHolderList.indexOf(viewHolder)));
            newFragment.show(((AppCompatActivity) context)
                    .getSupportFragmentManager(), "datePicker");
        }
    };

    // ─── Constructeur ───────────────────────────────────────────────────────

    public PH_Preparation_Ligne_VerrouPharmacieAdapter(
            Context context, SQLiteDatabase database,
            List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> list) {

        super(context, 0, list);
        this.context = context;
        this.db      = database;
        this.phPreparationLigneVerrouPharmacieAdapteList = list;
        this.first_passage = false;
        this.nb_inactive   = 0;

        this.viewHolderList = new ArrayList<>();

        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte item : list) {
            viewHolderList.add(new PH_Preparation_Ligne_AdapteViewHolder(item));

            // Pré-charge le conditionnement du produit une seule fois
            prechargerConditionnement(item);

            // Pré-charge la sérialisation une seule fois
            serialisationResultats.add(prechargerSerialisation(item));
        }
    }

    // ─── Pré-chargements ────────────────────────────────────────────────────

    private void prechargerConditionnement(PH_Preparation_Ligne_VerrouPharmacie_Adapte item) {
        if (item.getProduitCorrespondant() == null) return;
        int produitID = item.getProduitCorrespondant().getID_produit();
        if (conditionnementCache.containsKey(produitID)) return;

        Produit produit = ProduitOpenHelper.getProduitByID(db, produitID);
        if (produit == null) {
            conditionnementCache.put(produitID, 1);
            return;
        }
        int cond = produit.getCond_achat();
        if (cond == 0) cond = (int) produit.getCond_distrib();
        conditionnementCache.put(produitID, cond);
    }

    private String prechargerSerialisation(PH_Preparation_Ligne_VerrouPharmacie_Adapte item) {
        String serie = item.getSerieNumero();
        if (serie == null || serie.isEmpty() || serie.equals("null")) return "";

        if (item.getProduitCorrespondant() == null) return "";

        String numeroCommande = item.getPhPreparation().getListe().trim();
        String[] tabNumero    = numeroCommande.split(" ");
        numeroCommande        = tabNumero[tabNumero.length - 1];

        PH_Serialisation s = PH_SerialisationOpenHelper.getPH_SerialisationVerrou(
                db, numeroCommande, item.getProduitCorrespondant().getID_produit());

        return s != null ? s.getResultat() : "";
    }

    // ─── getView ────────────────────────────────────────────────────────────

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final PH_Preparation_Ligne_AdapteViewHolder viewHolder =
                viewHolderList.get(position);
        final PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte =
                getItem(position);

        // Réutilise le convertView existant — inflate uniquement si nécessaire
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_ph_preparation_ligne_verrou_pharmacie, parent, false);
        }

        // Binding des vues
        bindVues(viewHolder, convertView);

        if (phPrepAdapte != null) {
            afficherDonnees(viewHolder, phPrepAdapte);
            appliquerStatutVerrouillage(viewHolder);
            configurerListeners(viewHolder, phPrepAdapte, position);
            appliquerSerialisation(viewHolder, phPrepAdapte, position);
        }

        // Alerte produit inactif — une seule fois, en fin de liste
        if (position + 1 == viewHolderList.size() && !first_passage && nb_inactive > 0) {
            first_passage = true;
            Alerte.afficherAlerte(context, "Erreur",
                    "Produit inactif détecté, vérifiez vos surveillances références",
                    "alerte");
        }

        return convertView;
    }

    // ─── Bind & affichage ───────────────────────────────────────────────────

    private void bindVues(PH_Preparation_Ligne_AdapteViewHolder vh, View v) {
        vh.designation     = v.findViewById(R.id.designationProduit);
        vh.produitRef      = v.findViewById(R.id.referenceProduit);
        vh.nbColis         = v.findViewById(R.id.colis);
        vh.qteParLot       = v.findViewById(R.id.qte_par_lot);
        vh.numLot          = v.findViewById(R.id.numLot);
        vh.datePeremption  = v.findViewById(R.id.datePeremption);
        vh.textviewSerie   = v.findViewById(R.id.textviewSerie);
        vh.layoutQuantite  = v.findViewById(R.id.layoutQuantite);
        vh.numeroSerie     = v.findViewById(R.id.numeroSerie);
        vh.layoutNumSerie  = v.findViewById(R.id.layoutNumSerie);
        vh.layoutPrincipal = v.findViewById(R.id.layoutPrincipal);
        vh.statutLigne     = v.findViewById(R.id.statutLigne);
    }

    private void afficherDonnees(PH_Preparation_Ligne_AdapteViewHolder vh,
                                 PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte) {

        vh.designation.setText(phPrepAdapte.getDesignation());
        vh.produitRef.setText(phPrepAdapte.getProduitRef());
        vh.nbColis.setText(phPrepAdapte.getNbColis());
        vh.statutLigne.setText(vh.valeurStatut);

        if (vh.produit_inconnu) return;

        // Quantité
        if (vh.valeurQteParLot > -1) {
            vh.qteParLot.setText(String.valueOf(vh.valeurQteParLot));
        } else {
            String qte = phPrepAdapte.getQteParLot();
            vh.qteParLot.setText(qte.equals("0") ? "" : qte);
            vh.valeurQteParLot = Integer.parseInt(qte);
        }

        vh.enCoursDeChargement = true; // bloque tout setText parasite résiduel

        if (!vh.valeurLot.isEmpty()) {
            vh.numLot.setText(vh.valeurLot);
        } else {
            vh.numLot.setText(phPrepAdapte.getNumLot());
            vh.valeurLot = phPrepAdapte.getNumLot();
        }

        // Série
        String serie = phPrepAdapte.getSerieNumero();
        if (serie == null || serie.isEmpty()) {
            vh.layoutNumSerie.setVisibility(View.GONE);
            vh.valeurSerie = "";
        } else {
            vh.numeroSerie.setText(serie);
            vh.valeurSerie = serie;
        }

        vh.enCoursDeChargement = false;

        // Date péremption
        if (vh.valeurDate.isEmpty()) {
            try {
                String dateStr = phPrepAdapte.getPeremptionDate();
                if (dateStr != null && dateStr.length() >= 10) {
                    Date date = DATE_FORMAT_SQL.parse(dateStr.substring(0, 10));
                    vh.valeurDate = DATE_FORMAT_DISPLAY.format(date);
                    vh.setDatePeremptionColor(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!vh.valeurDate.isEmpty()) {
            vh.datePeremption.setText(vh.valeurDate);
            try {
                vh.setDatePeremptionColor(DATE_FORMAT_DISPLAY.parse(vh.valeurDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            vh.datePeremption.setText("");
            vh.setDatePeremptionColor(null);
        }
    }

    private void appliquerStatutVerrouillage(PH_Preparation_Ligne_AdapteViewHolder vh) {
        boolean deVerrouille = vh.valeurStatut.equals("Déverrouillé");
        vh.layoutQuantite.setCardBackgroundColor(
                context.getColor(deVerrouille ? R.color.vert : R.color.rouge2));
        vh.statutLigne.setText(deVerrouille ? "Déverrouillé" : "Verrouillé");
    }

    // ─── Listeners ──────────────────────────────────────────────────────────

    private void configurerListeners(PH_Preparation_Ligne_AdapteViewHolder vh,
                                     PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte,
                                     int position) {
        // Date
        vh.datePeremption.setOnClickListener(clicDate);

        // Verrouillage / Déverrouillage
        vh.layoutQuantite.setOnClickListener(v -> {
            PH_Preparation_Ligne_AdapteViewHolder item = viewHolderList.get(position);
            boolean estVerrouille = item.valeurStatut.equals("Verrouillé");
            item.valeurStatut = estVerrouille ? "Déverrouillé" : "Verrouillé";
            appliquerStatutVerrouillage(item);

            if (mOnDataChangeListener != null) {
                int nbColis = recupererNbColisDepuisCache(
                        phPrepAdapte.getProduitCorrespondant().getID_produit(),
                        item.valeurQteParLot);
                int avant = estVerrouille ? 0 : nbColis;
                int apres = estVerrouille ? nbColis : 0;
                mOnDataChangeListener.onDataChanged(avant, apres, estVerrouille);
            }
            notifyDataSetChanged();
        });

        // Long clic → NumberPicker
        vh.qteParLot.setOnLongClickListener(v -> {
            String title    = viewHolderList.get(position).designation.getText().toString();
            String message  = "Quantité déverrouillée : ";
            int maxValue    = vh.valeurQteDemander;
            String qteText  = vh.qteParLot.getText().toString();
            int value       = qteText.isEmpty() ? 0 : Integer.parseInt(qteText);

            Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue,
                    (dialog, id) -> {
                        int qteApres = aNumberPicker.getValue();
                        vh.qteParLot.setText(String.valueOf(qteApres));
                        phPrepAdapte.setQteParLot(String.valueOf(qteApres));
                        vh.valeurQteParLot = qteApres;
                        vh.valeurStatut    = "Déverrouillé";
                        notifyDataSetChanged();

                        ((InputMethodManager) context.getSystemService(
                                Context.INPUT_METHOD_SERVICE))
                                .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    });
            return true;
        });

        // Listeners — instanciés une seule fois par viewHolder
        if (!vh.listenersInitialises) {
            new GestionnaireEditText(vh, phPrepAdapte);
            vh.listenersInitialises = true;
        }
    }

    // ─── Sérialisation (depuis cache, sans BDD) ──────────────────────────────

    private void appliquerSerialisation(PH_Preparation_Ligne_AdapteViewHolder vh,
                                        PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepAdapte,
                                        int position) {

        String resultat = serialisationResultats.get(position);
        if (resultat == null || resultat.isEmpty()) return;
        if (!resultat.equals("INACTIVE") && !resultat.equals("UNKNOWN")) return;

        if (first_passage) {
            vh.datePeremption.setText(vh.valeurDate.isEmpty() ? "00/00/0000" : vh.valeurDate);
            vh.qteParLot.setText(String.valueOf(vh.valeurQteParLot));
            vh.numeroSerie.setText(vh.valeurSerie);
            vh.numLot.setText(vh.valeurLot);
        } else {
            vh.numLot.setText("");
            vh.valeurLot       = "";
            vh.numeroSerie.setText("");
            vh.valeurSerie     = "";
            vh.valeurQteParLot = 0;
            vh.valeurDate      = "00/00/0000";
            vh.datePeremption.setText("00/00/0000");
            vh.qteParLot.setText("0");
            vh.produit_inconnu = true;
            nb_inactive++;
        }
    }

    // ─── Calcul nb colis depuis cache (0 BDD) ───────────────────────────────

    private int recupererNbColisDepuisCache(int produitID, double qte) {
        if (produitID == 0 || qte == 0) return 0;
        Integer cond = conditionnementCache.get(produitID);
        if (cond == null || cond == 0) return qte > 0 ? 1 : 0;
        int nbColis = (int) Math.ceil(qte / cond);
        return nbColis == 0 && qte > 0 ? 1 : nbColis;
    }

    // ─── Mise à jour BDD ────────────────────────────────────────────────────

    public void mettreAJourPHPrepLigne(PH_Preparation_Ligne_AdapteViewHolder vh,
                                       PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {

        PH_Preparation_Ligne phPrepLigne       = phPrepLigneAdapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLot   = phPrepLigneAdapte.getStockLotEmplacement();

        stockLot.setLot(vh.valeurLot);
        stockLot.setQte_Preparer(vh.valeurQteParLot);

        try {
            Date dateFournie = DATE_FORMAT_DISPLAY.parse(vh.valeurDate);
            String dateSQL   = DATE_FORMAT_SQL.format(dateFournie);
            stockLot.setPeremptionDate(dateSQL);
            phPrepLigneAdapte.setPeremptionDate(dateSQL);
            phPrepLigne.setPeremptionDate(dateSQL);
            vh.setDatePeremptionColor(dateFournie);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLot);

        int qteAvant = 0;
        if (phPrepLigneAdapte.getNbColis() != null && !phPrepLigneAdapte.getNbColis().isEmpty()) {
            qteAvant = Integer.parseInt(phPrepLigneAdapte.getNbColis());
        }

        int qteApres = recupererNbColisDepuisCache(
                phPrepLigneAdapte.getProduitCorrespondant().getID_produit(),
                vh.valeurQteParLot);

        phPrepLigneAdapte.setNbColis(String.valueOf(qteApres));
        phPrepLigneAdapte.setNumLot(vh.valeurLot);
        phPrepLigneAdapte.setQteParLot(String.valueOf(vh.valeurQteParLot));

        phPrepLigne.setLotNumero(vh.valeurLot);
        phPrepLigne.setQte_RAL(vh.valeurQteParLot);
        phPrepLigne.setSerieNumero(vh.valeurSerie);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPrepLigne);
        // Pas d appel a onDataChanged ici : les compteurs ne bougent
        // qu au clic verrou/deverrou dans configurerListeners().
    }

    @Override
    public void clear() {
        phPreparationLigneVerrouPharmacieAdapteList.clear();
    }

    public void setOnDataChangeListener(OnDataChangeListener l) {
        mOnDataChangeListener = l;
    }

    public interface OnDataChangeListener {
        void onDataChanged(int quantitéAvant, int quantitéAprès, boolean verrou);
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
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthStr = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String dayStr   = day < 10 ? "0" + day : String.valueOf(day);
            String date     = dayStr + "/" + monthStr + "/" + year;

            viewHolder.datePeremption.setText(date);
            viewHolder.valeurDate = date;

            PH_Preparation_Ligne phPrepLigne     = phPrepLigneAdapte.getPhPreparationLigne();
            Stock_Lot_Emplacement_Light stockLot = phPrepLigneAdapte.getStockLotEmplacement();

            stockLot.setLot(viewHolder.valeurLot);
            stockLot.setQte_Preparer(viewHolder.valeurQteParLot);

            try {
                Date dateFournie = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                viewHolder.setDatePeremptionColor(dateFournie);
                String dateSQL = new SimpleDateFormat("yyyy-MM-dd").format(dateFournie);
                stockLot.setPeremptionDate(dateSQL);
                phPrepLigneAdapte.setPeremptionDate(dateSQL);
                phPrepLigne.setPeremptionDate(dateSQL);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLot);
            phPrepLigneAdapte.setNumLot(viewHolder.valeurLot);
            phPrepLigneAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));
            phPrepLigne.setLotNumero(viewHolder.valeurLot);
            phPrepLigne.setQte_RAL(viewHolder.valeurQteParLot);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPrepLigne);
        }

        public void setViewHolder(PH_Preparation_Ligne_AdapteViewHolder v,
                                  SQLiteDatabase database,
                                  PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte) {
            viewHolder       = v;
            db               = database;
            phPrepLigneAdapte = adapte;
        }
    }

    // ─── GestionnaireEditText ───────────────────────────────────────────────
    // Utilise onFocusChangeListener au lieu de TextWatcher pour éviter
    // les déclenchements parasites lors des setText() internes au scroll.

    private class GestionnaireEditText {

        GestionnaireEditText(PH_Preparation_Ligne_AdapteViewHolder vh,
                             PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {

            // Sauvegarde du lot uniquement quand l utilisateur quitte le champ
            vh.numLot.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String nouvellValeur = vh.numLot.getText().toString();
                    if (!nouvellValeur.equals(vh.valeurLot)) {
                        vh.valeurLot = nouvellValeur;
                        int idx = viewHolderList.indexOf(vh);
                        if (idx >= 0) {
                            mettreAJourPHPrepLigne(vh,
                                    phPreparationLigneVerrouPharmacieAdapteList.get(idx));
                        }
                    }
                }
            });

            // Sauvegarde de la série uniquement quand l utilisateur quitte le champ
            vh.numeroSerie.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String nouvellValeur = vh.numeroSerie.getText().toString();
                    if (!nouvellValeur.equals(vh.valeurSerie)) {
                        vh.valeurSerie = nouvellValeur;
                        int idx = viewHolderList.indexOf(vh);
                        if (idx >= 0) {
                            mettreAJourPHPrepLigne(vh,
                                    phPreparationLigneVerrouPharmacieAdapteList.get(idx));
                        }
                    }
                }
            });

            // Quantité : sauvegarde à la perte de focus
            vh.qteParLot.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String qteText     = vh.qteParLot.getText().toString();
                    int nouvelleQte    = qteText.isEmpty() ? 0 : Integer.parseInt(qteText);
                    if (nouvelleQte != vh.valeurQteParLot) {
                        vh.valeurQteParLot = nouvelleQte;
                        int nbColis = recupererNbColisDepuisCache(
                                phPrepLigneAdapte.getProduitCorrespondant().getID_produit(),
                                vh.valeurQteParLot);
                        vh.nbColis.setText(String.valueOf(nbColis));
                        int idx = viewHolderList.indexOf(vh);
                        if (idx >= 0) {
                            mettreAJourPHPrepLigne(vh,
                                    phPreparationLigneVerrouPharmacieAdapteList.get(idx));
                        }
                    }
                }
            });

            // KeyListener conservé pour détecter la touche Entrée
            vh.qteParLot.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    v.clearFocus(); // déclenche onFocusChange → sauvegarde
                }
                return false;
            });
        }
    }

    // ─── ViewHolder ─────────────────────────────────────────────────────────

    public class PH_Preparation_Ligne_AdapteViewHolder {

        public TextView     designation;
        public TextView     produitRef;
        public TextView     qteParLot;
        public TextView     numLot;
        public TextView     nbColis;
        public TextView     datePeremption;
        public TextView     textviewSerie;
        public TextView     statutLigne;
        public EditText     numeroSerie;
        public LinearLayout layoutNumSerie;
        public LinearLayout layoutPrincipal;
        public CardView     layoutQuantite;

        public boolean produit_inconnu      = false;
        public boolean listenersInitialises = false;
        public boolean enCoursDeChargement  = false; // bloque les TextWatchers pendant setText()

        public int    valeurQteParLot  = -1;
        public int    valeurQteDemander;
        public String valeurDate       = "";
        public String valeurLot        = "";
        public String valeurSerie      = "";
        public String valeurStatut     = "Verrouillé";

        public PH_Preparation_Ligne_AdapteViewHolder(
                PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigne) {

            String qte = phPrepLigne.getQteParLot();
            if (qte != null && !qte.isEmpty()) {
                this.valeurQteDemander = Integer.parseInt(qte);
                this.valeurQteParLot   = Integer.parseInt(qte);
            }
            this.valeurLot = phPrepLigne.getNumLot() != null ? phPrepLigne.getNumLot() : "";

            try {
                String dateStr = phPrepLigne.getPeremptionDate();
                if (dateStr != null && !dateStr.isEmpty()) {
                    Date date      = DATE_FORMAT_SQL.parse(dateStr);
                    this.valeurDate = DATE_FORMAT_DISPLAY.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void setDatePeremptionColor(Date date) {
            if (datePeremption == null) return;
            if (date == null) {
                datePeremption.setTextColor(Color.BLACK);
                return;
            }
            long diff  = new Date().getTime() - date.getTime();
            int  delai = (int) (diff / (1000L * 60 * 60 * 24));

            if (delai >= -30) {
                datePeremption.setTextColor(
                        context.getResources().getColor(R.color.rouge2, null));
            } else if (delai >= -60) {
                datePeremption.setTextColor(
                        context.getResources().getColor(R.color.orange2, null));
            } else {
                datePeremption.setTextColor(
                        context.getResources().getColor(R.color.vert, null));
            }
        }
    }
}