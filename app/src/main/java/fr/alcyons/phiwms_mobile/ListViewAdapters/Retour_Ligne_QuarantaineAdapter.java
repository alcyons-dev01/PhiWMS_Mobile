package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.MenuActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Quarantaine.DetailQuarantaineActivity;
import fr.alcyons.phiwms_mobile.R;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

public class Retour_Ligne_QuarantaineAdapter extends ArrayAdapter<Retour_Ligne> {

    // ─── Données ────────────────────────────────────────────────────────────

    public List<Retour_Ligne> retourLigneList;
    public List<Retour_LigneViewHolder> viewHolderList;

    /** Résultats de sérialisation pré-calculés, indexés par position */
    private final List<String> serialisationResultats;

    SQLiteDatabase db;
    Context context;

    private static final DateFormat DATE_FORMAT_SQL     = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat("dd/MM/yyyy");

    // ─── Constructeur ───────────────────────────────────────────────────────

    public Retour_Ligne_QuarantaineAdapter(Context context, SQLiteDatabase db,
                                           List<Retour_Ligne> retourLigneList) {
        super(context, 0, retourLigneList);
        this.context        = context;
        this.retourLigneList = retourLigneList;
        this.db             = db;

        this.viewHolderList       = new ArrayList<>();
        this.serialisationResultats = new ArrayList<>();

        // Pré-charge toutes les données lourdes UNE SEULE FOIS
        for (Retour_Ligne retourLigne : retourLigneList) {
            // ViewHolder initialisé avec les valeurs de départ
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            viewHolder.layoutRef        = R.layout.row_retour_ligne_quarantaine;
            viewHolder.valeurQteRetourner = (int) retourLigne.getQte_Retourner();
            viewHolder.valeurLot        = retourLigne.getLot_Retourner() != null
                    ? retourLigne.getLot_Retourner() : "";
            viewHolder.valeurSerie      = retourLigne.getSerie_Retourner() != null
                    ? retourLigne.getSerie_Retourner() : "";

            // Date initiale
            try {
                if (retourLigne.getPeremptionDate() != null
                        && retourLigne.getPeremptionDate().length() >= 10) {
                    Date date = DATE_FORMAT_SQL.parse(
                            retourLigne.getPeremptionDate().substring(0, 10));
                    viewHolder.valeurDate = DATE_FORMAT_DISPLAY.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.viewHolderList.add(viewHolder);

            // Sérialisation pré-chargée
            String resultat = prechargerSerialisation(retourLigne);
            serialisationResultats.add(resultat);

            // Si UNKNOWN → destruction auto
            if ("UNKNOWN".equals(resultat)) {
                viewHolder.valeurDestruction = viewHolder.valeurQteRetourner;
            }
        }
    }

    /** Requête BDD faite UNE FOIS à l'init, jamais dans getView */
    private String prechargerSerialisation(Retour_Ligne retourLigne) {
        String serie = retourLigne.getSerie_Retourner();
        if (serie == null || serie.isEmpty()) return "";

        Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
        if (produit == null) return "";

        String gtin = produit.getGTIN();
        if (gtin == null) return "";
        if (gtin.length() > 14) gtin = gtin.substring(2);

        PH_Serialisation s = PH_SerialisationOpenHelper
                .getPH_SerialisationQuarantaine(db, gtin, serie);
        return s != null ? s.getResultat() : "";
    }

    // ─── getView ────────────────────────────────────────────────────────────

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Retour_LigneViewHolder viewHolder = viewHolderList.get(position);
        final Retour_Ligne retourLigne          = getItem(position);

        // Réutilisation du convertView — inflate uniquement si nécessaire
        if (convertView == null
                || getViewTypeForHolder(convertView) != viewHolder.layoutRef) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(viewHolder.layoutRef, parent, false);
        }

        if (retourLigne == null) return convertView;

        // Binding des vues
        bindVues(viewHolder, convertView);

        // Affichage des données (jamais de BDD ici)
        afficherDonnees(viewHolder, retourLigne, position);

        // Listeners
        configurerListeners(viewHolder, retourLigne, position);

        return convertView;
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public int getItemViewType(int position) {
        return viewHolderList.get(position).layoutRef
                == R.layout.row_retour_ligne_quarantaine ? 0 : 1;
    }

    private int getViewTypeForHolder(View view) {
        // Tag posé à l'inflate pour savoir quel layout est actuellement dans la vue
        Object tag = view.getTag(R.id.tag_layout_ref);
        return tag != null ? (int) tag : -1;
    }

    // ─── Méthodes privées ───────────────────────────────────────────────────

    private void bindVues(Retour_LigneViewHolder vh, View v) {
        vh.designation          = v.findViewById(R.id.designationProduit);
        vh.referenceProduit     = v.findViewById(R.id.referenceProduit);
        vh.fournisseur          = v.findViewById(R.id.nomFournisseur);
        vh.lotRetourne          = v.findViewById(R.id.lotRetourne);
        vh.numeroSerieModifiable= v.findViewById(R.id.numeroSerieModifiable);
        vh.datePeremption       = v.findViewById(R.id.datePeremption);
        vh.qteRetourner         = v.findViewById(R.id.QteRetourner);
        vh.qteDestruction       = v.findViewById(R.id.QteDestruction);
        vh.qteRetourPUI         = v.findViewById(R.id.QteRetourPUI);
        vh.qteRetourFournisseur = v.findViewById(R.id.QteRetourFournisseur);
        vh.resultatSerialisation= v.findViewById(R.id.resultatSerialisation);
        vh.zoneNumSerie         = v.findViewById(R.id.zoneNumSerie);

        v.setTag(R.id.tag_layout_ref, vh.layoutRef);
    }

    private void afficherDonnees(Retour_LigneViewHolder vh, Retour_Ligne rl, int position) {
        // Textes statiques
        vh.designation.setText(rl.getProduit_Designation());
        vh.referenceProduit.setText(rl.getProduit_Reference());

        String fournisseur = rl.getProduit_Fournisseur();
        vh.fournisseur.setText(fournisseur != null && !fournisseur.isEmpty()
                ? fournisseur.split(" ")[0] : "");

        vh.qteRetourner.setText(String.valueOf((int) rl.getQte_Retourner()));

        // Quantités saisies
        vh.qteDestruction.setText(vh.valeurDestruction > -1
                ? String.valueOf(vh.valeurDestruction)
                : (rl.getDestruction_Qte() <= 0 ? "" : String.valueOf((int) rl.getDestruction_Qte())));

        vh.qteRetourFournisseur.setText(vh.valeurFrs > -1
                ? String.valueOf(vh.valeurFrs)
                : (rl.getRetourFrs_Qte() <= 0 ? "" : String.valueOf((int) rl.getRetourFrs_Qte())));

        vh.qteRetourPUI.setText(vh.valeurPUI > -1
                ? String.valueOf(vh.valeurPUI)
                : (rl.getRetourPui_Qte() <= 0 ? "" : String.valueOf((int) rl.getRetourPui_Qte())));

        // Lot
        vh.lotRetourne.setText(vh.valeurLot);

        // Série
        String serie = rl.getSerie_Retourner();
        if (serie != null && !serie.isEmpty()) {
            vh.numeroSerieModifiable.setText(serie);
            vh.zoneNumSerie.setVisibility(View.VISIBLE);
        } else {
            vh.zoneNumSerie.setVisibility(View.GONE);
        }

        // Date péremption
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

        // Sérialisation — déjà pré-chargée, pas de BDD
        String resultat = serialisationResultats.get(position);
        vh.valeurSerialisation = resultat;
        vh.resultatSerialisation.setText(resultat);
    }

    private void configurerListeners(final Retour_LigneViewHolder vh,
                                     final Retour_Ligne rl, final int position) {

        boolean estModifiable = vh.layoutRef == R.layout.row_retour_ligne_quarantaine_modifiable;

        if (estModifiable) {
            // Mode modifiable : date picker + datamatrix
            FrameLayout zoneDate = vh.zoneDate;
            if (zoneDate != null)
            {
                zoneDate.setOnClickListener(v -> {
                    DatePickerFragment f = new DatePickerFragment();
                    f.setViewHolder(vh, rl, db);
                    f.show(((AppCompatActivity) context).getSupportFragmentManager(), "datePicker");
                });
            }



            LinearLayout zoneDataMatrix = vh.dataMatrix;
            if (zoneDataMatrix != null) {
                zoneDataMatrix.setOnClickListener(v ->
                        ((DetailQuarantaineActivity) context).decoderCodeBarre(
                                vh.datePeremption, vh.lotRetourne, vh,
                                vh.designation.getText().toString()));
            }

            vh.datePeremption.setPaintFlags(
                    vh.datePeremption.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            // NumberPickers si mode modification
            if (vh.modification) {
                String resultat = serialisationResultats.get(position);
                if (!"UNKNOWN".equals(resultat)) {
                    vh.qteRetourFournisseur.setOnClickListener(
                            v -> afficherNumberPicker(vh, "Fournisseur", rl));
                    vh.qteRetourPUI.setOnClickListener(
                            v -> afficherNumberPicker(vh, "PUI", rl));
                }
                vh.qteDestruction.setOnClickListener(
                        v -> afficherNumberPicker(vh, "Destruction", rl));
            }

        } else {
            // Mode lecture : clics simples sur les zones quantité
            new GestionnaireTextView(vh, rl);
        }
    }

    // ─── Mode modif ─────────────────────────────────────────────────────────

    public void setModeModif(boolean modeModif, Retour_LigneViewHolder viewHolder) {
        viewHolder.layoutRef    = modeModif
                ? R.layout.row_retour_ligne_quarantaine_modifiable
                : R.layout.row_retour_ligne_quarantaine;
        viewHolder.modification = modeModif;
        if (!modeModif) viewHolder.firstModifPassage = true;
    }

    // ─── Mise à jour BDD ────────────────────────────────────────────────────

    public long mettreAJourUnRetourLigne(Retour_Ligne retourLigne,
                                         Retour_LigneViewHolder viewHolder) {
        try {
            if (!viewHolder.valeurDate.isEmpty()) {
                Date dateFournie = DATE_FORMAT_DISPLAY.parse(viewHolder.valeurDate);
                retourLigne.setPeremptionDate(DATE_FORMAT_SQL.format(dateFournie));
                viewHolder.setDatePeremptionColor(dateFournie);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        retourLigne.setLot_Retourner(viewHolder.valeurLot);
        retourLigne.setSerie_Retourner(viewHolder.valeurSerie);
        retourLigne.setDestruction_Qte(viewHolder.valeurDestruction);
        retourLigne.setRetourPui_Qte(viewHolder.valeurPUI);
        retourLigne.setRetourFrs_Qte(viewHolder.valeurFrs);
        return Retour_LigneOpenHelper.mettreAJourUnRetourLigne(
                ((MenuActivity) context).db, retourLigne);
    }

    // ─── DatePickerFragment ─────────────────────────────────────────────────

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        Retour_LigneViewHolder viewHolder;
        Retour_Ligne retourLigne;
        SQLiteDatabase db;

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
            String mois = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String jour = day < 10 ? "0" + day : String.valueOf(day);
            String date = jour + "/" + mois + "/" + year;

            viewHolder.datePeremption.setText(date);
            viewHolder.valeurDate = date;

            try {
                Date dateFournie = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                retourLigne.setPeremptionDate(
                        new SimpleDateFormat("yyyy-MM-dd").format(dateFournie));
                viewHolder.setDatePeremptionColor(dateFournie);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            retourLigne.setLot_Retourner(viewHolder.valeurLot);
            retourLigne.setSerie_Retourner(viewHolder.valeurSerie);
            retourLigne.setDestruction_Qte(viewHolder.valeurDestruction);
            retourLigne.setRetourPui_Qte(viewHolder.valeurPUI);
            retourLigne.setRetourFrs_Qte(viewHolder.valeurFrs);
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
        }

        public void setViewHolder(Retour_LigneViewHolder v, Retour_Ligne rl,
                                  SQLiteDatabase database) {
            viewHolder  = v;
            retourLigne = rl;
            db          = database;
        }
    }

    // ─── GestionnaireTextView (mode lecture) ────────────────────────────────

    private class GestionnaireTextView {

        final Retour_LigneViewHolder viewHolder;
        final Retour_Ligne retourLigne;

        GestionnaireTextView(Retour_LigneViewHolder vh, Retour_Ligne rl) {
            this.viewHolder  = vh;
            this.retourLigne = rl;

            ((View) vh.qteRetourPUI.getParent()).setOnClickListener(v -> clicPUI());
            ((View) vh.qteDestruction.getParent()).setOnClickListener(v -> clicDestruction());
            ((View) vh.qteRetourFournisseur.getParent()).setOnClickListener(v -> clicFournisseur());
        }

        private void clicPUI() {
            if ("UNKNOWN".equals(viewHolder.valeurSerialisation)) return;
            boolean dejaPlein = viewHolder.qteRetourPUI.getText()
                    .toString().equals(String.valueOf(viewHolder.valeurQteRetourner));
            viewHolder.valeurPUI         = viewHolder.valeurQteRetourner;
            viewHolder.valeurFrs         = 0;
            viewHolder.valeurDestruction = 0;
            if (dejaPlein) {
                setModeModif(true, viewHolder);
                notifyDataSetChanged();
            } else {
                viewHolder.qteRetourPUI.setText(viewHolder.qteRetourner.getText());
                viewHolder.qteRetourFournisseur.setText("0");
                viewHolder.qteDestruction.setText("0");
            }
            mettreAJourUnRetourLigne(retourLigne, viewHolder);
        }

        private void clicDestruction() {
            boolean dejaPlein = viewHolder.qteDestruction.getText()
                    .toString().equals(String.valueOf(viewHolder.valeurQteRetourner));
            viewHolder.valeurDestruction = viewHolder.valeurQteRetourner;
            viewHolder.valeurFrs         = 0;
            viewHolder.valeurPUI         = 0;
            if (dejaPlein) {
                setModeModif(true, viewHolder);
                notifyDataSetChanged();
            } else {
                viewHolder.qteDestruction.setText(viewHolder.qteRetourner.getText());
                viewHolder.qteRetourFournisseur.setText("0");
                viewHolder.qteRetourPUI.setText("0");
            }
            mettreAJourUnRetourLigne(retourLigne, viewHolder);
        }

        private void clicFournisseur() {
            if ("UNKNOWN".equals(viewHolder.valeurSerialisation)) return;
            boolean dejaPlein = viewHolder.qteRetourFournisseur.getText()
                    .toString().equals(String.valueOf(viewHolder.valeurQteRetourner));
            viewHolder.valeurFrs         = viewHolder.valeurQteRetourner;
            viewHolder.valeurPUI         = 0;
            viewHolder.valeurDestruction = 0;
            if (dejaPlein) {
                setModeModif(true, viewHolder);
                notifyDataSetChanged();
            } else {
                viewHolder.qteRetourFournisseur.setText(viewHolder.qteRetourner.getText());
                viewHolder.qteRetourPUI.setText("0");
                viewHolder.qteDestruction.setText("0");
            }
            mettreAJourUnRetourLigne(retourLigne, viewHolder);
        }
    }

    // ─── NumberPicker ───────────────────────────────────────────────────────

    public void afficherNumberPicker(final Retour_LigneViewHolder vh,
                                     final String denomination,
                                     final Retour_Ligne courant) {
        final int maxValue = vh.valeurQteRetourner;
        int value = 0;
        switch (denomination) {
            case "PUI":
                if (!vh.qteRetourPUI.getText().toString().isEmpty())
                    value = Integer.parseInt(vh.qteRetourPUI.getText().toString());
                break;
            case "Destruction":
                if (!vh.qteDestruction.getText().toString().isEmpty())
                    value = Integer.parseInt(vh.qteDestruction.getText().toString());
                break;
            default:
                if (!vh.qteRetourFournisseur.getText().toString().isEmpty())
                    value = Integer.parseInt(vh.qteRetourFournisseur.getText().toString());
                break;
        }

        Alerte.afficherAlerteNumberPicker(context, denomination, "Quantité placée : ",
                value, maxValue, (dialog, id) -> {
                    int qteApres = aNumberPicker.getValue();
                    appliquerQuantite(vh, denomination, qteApres, maxValue, courant);
                    notifyDataSetChanged();
                    ((InputMethodManager) context.getSystemService(
                            Context.INPUT_METHOD_SERVICE))
                            .toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.dismiss();
                });

        setModeModif(false, vh);
        vh.modification = false;
    }

    private void appliquerQuantite(Retour_LigneViewHolder vh, String denomination,
                                   int qteApres, int max, Retour_Ligne courant) {
        int frs         = Math.max(vh.valeurFrs, 0);
        int destruction = Math.max(vh.valeurDestruction, 0);
        int pui         = Math.max(vh.valeurPUI, 0);

        switch (denomination) {
            case "PUI":
                pui = qteApres;
                int diffPUI = max - (destruction + frs + pui);
                while (diffPUI < 0) {
                    if (frs > 0) frs--; else destruction--;
                    diffPUI = max - (destruction + frs + pui);
                }
                vh.valeurPUI = pui; vh.valeurFrs = frs; vh.valeurDestruction = destruction;
                vh.qteRetourPUI.setText(String.valueOf(pui));
                vh.qteRetourFournisseur.setText(String.valueOf(frs));
                vh.qteDestruction.setText(String.valueOf(destruction));
                courant.setRetourPui_Qte(pui); courant.setRetourFrs_Qte(frs);
                break;

            case "Destruction":
                destruction = qteApres;
                int diffDest = max - (destruction + frs + pui);
                while (diffDest < 0) {
                    if (frs > 0) frs--; else pui--;
                    diffDest = max - (destruction + frs + pui);
                }
                vh.valeurDestruction = destruction; vh.valeurFrs = frs; vh.valeurPUI = pui;
                vh.qteDestruction.setText(String.valueOf(destruction));
                vh.qteRetourFournisseur.setText(String.valueOf(frs));
                vh.qteRetourPUI.setText(String.valueOf(pui));
                courant.setDestruction_Qte(destruction); courant.setRetourPui_Qte(pui);
                break;

            default: // Fournisseur
                frs = qteApres;
                int diffFrs = max - (destruction + frs + pui);
                while (diffFrs < 0) {
                    if (destruction > 0) destruction--; else pui--;
                    diffFrs = max - (destruction + frs + pui);
                }
                vh.valeurFrs = frs; vh.valeurDestruction = destruction; vh.valeurPUI = pui;
                vh.qteRetourFournisseur.setText(String.valueOf(frs));
                vh.qteDestruction.setText(String.valueOf(destruction));
                vh.qteRetourPUI.setText(String.valueOf(pui));
                courant.setRetourFrs_Qte(frs); courant.setDestruction_Qte(destruction);
                break;
        }

        mettreAJourUnRetourLigne(courant, vh);
    }

    // ─── ViewHolder ─────────────────────────────────────────────────────────

    public class Retour_LigneViewHolder {
        public int    layoutRef;
        public int    valeurPUI           = -1;
        public int    valeurFrs           = -1;
        public int    valeurDestruction   = -1;
        public int    valeurQteRetourner;
        public String valeurLot           = "";
        public String valeurDate          = "";
        public String valeurSerie         = "";
        public String valeurSerialisation = "";
        public boolean firstModifPassage  = true;
        public boolean modification       = false;

        public TextView   designation;
        public TextView   referenceProduit;
        public TextView   fournisseur;
        public TextView   lotRetourne;
        public TextView   datePeremption;
        public TextView   qteRetourner;
        public TextView   qteDestruction;
        public TextView   qteRetourPUI;
        public TextView   qteRetourFournisseur;
        public TextView   numeroSerieModifiable;
        public TextView   resultatSerialisation;
        public LinearLayout dataMatrix;
        public LinearLayout zoneNumSerie;
        public FrameLayout  zoneDate;

        // ─── Actions globales (appelées depuis le BottomSheet) ───────────

        public void toutRetournerPUI() {
            valeurPUI = valeurQteRetourner; valeurFrs = 0; valeurDestruction = 0;
            mettreAJourUnRetourLigne(
                    retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutRetournerFrs() {
            valeurPUI = 0; valeurFrs = valeurQteRetourner; valeurDestruction = 0;
            mettreAJourUnRetourLigne(
                    retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutDetruire() {
            valeurPUI = 0; valeurFrs = 0; valeurDestruction = valeurQteRetourner;
            mettreAJourUnRetourLigne(
                    retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutRemettreAZero() {
            valeurPUI = 0; valeurFrs = 0; valeurDestruction = 0;
            mettreAJourUnRetourLigne(
                    retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
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