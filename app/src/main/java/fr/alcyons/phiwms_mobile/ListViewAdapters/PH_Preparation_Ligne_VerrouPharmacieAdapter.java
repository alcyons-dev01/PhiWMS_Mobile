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
import android.widget.ImageView;
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
import fr.alcyons.phiwms_mobile.VerrouPharmacie.DetailVerrouPharmacieActivity;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

public class PH_Preparation_Ligne_VerrouPharmacieAdapter extends ArrayAdapter {

    public List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdapteList;
    public List<PH_Preparation_Ligne_AdapteViewHolder> viewHolderList;
    Context context;
    SQLiteDatabase db;
    int nb_inactive;
    boolean first_passage;
    OnDataChangeListener mOnDataChangeListener;

    View.OnClickListener clicDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment newFragment = new DatePickerFragment();
            PH_Preparation_Ligne_AdapteViewHolder viewHolder = null;
            for (PH_Preparation_Ligne_AdapteViewHolder viewHolderC : viewHolderList) {
                if (viewHolderC.datePeremption == v.findViewById(R.id.datePeremption)) {
                    viewHolder = viewHolderC;
                    break;
                }
            }
            newFragment.setViewHolder(viewHolder, ((MenuActivity) context).db, phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
            newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "timePicker");
        }
    };
    View.OnClickListener clicDataMatrix = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout parent = (LinearLayout) v.getParent().getParent().getParent();
            LinearLayout LinearInfoVerrou = (LinearLayout) parent.findViewById(R.id.LinearInfoVerrou);
            LinearLayout zoneInfoLot = (LinearLayout) LinearInfoVerrou.findViewById(R.id.zoneInfoLot);
            LinearLayout zoneNumLot = (LinearLayout) zoneInfoLot.findViewById(R.id.layoutNumLot);
            LinearLayout layoutDateExp = (LinearLayout) zoneInfoLot.findViewById(R.id.layoutDateExp);
            TextView date = (TextView) layoutDateExp.findViewById(R.id.datePeremption);
            TextView numLot = (TextView) zoneNumLot.findViewById(R.id.numLot);
            PH_Preparation_Ligne_AdapteViewHolder viewHolderSelectionne = null;
            for (PH_Preparation_Ligne_AdapteViewHolder viewHolder : viewHolderList) {
                if (viewHolder.datePeremption == date && viewHolder.numLot == numLot) {
                    viewHolderSelectionne = viewHolder;
                }
            }
            ((DetailVerrouPharmacieActivity) context).decoderCodeBarre((EditText) numLot, date, viewHolderSelectionne);
        }
    };

    public PH_Preparation_Ligne_VerrouPharmacieAdapter(Context context, SQLiteDatabase database, List<PH_Preparation_Ligne_VerrouPharmacie_Adapte> phPreparationLigneVerrouPharmacieAdapteList) {
        super(context, 0, phPreparationLigneVerrouPharmacieAdapteList);
        this.context = context;
        this.db = database;
        this.phPreparationLigneVerrouPharmacieAdapteList = phPreparationLigneVerrouPharmacieAdapteList;
        first_passage = false;

        this.viewHolderList = new ArrayList<>();
        for (PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte : phPreparationLigneVerrouPharmacieAdapteList)
        {
            PH_Preparation_Ligne_AdapteViewHolder viewHolder = new PH_Preparation_Ligne_AdapteViewHolder(phPreparationLigneVerrouPharmacieAdapte);
            this.viewHolderList.add(viewHolder);
        }

        nb_inactive = 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
        }

        final PH_Preparation_Ligne_AdapteViewHolder viewHolder = viewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_preparation_ligne_verrou_pharmacie, parent, false);

        // Récupération des objets graphiques
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.produitRef = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.nbColis = (TextView) convertView.findViewById(R.id.colis);
        viewHolder.qteParLot = (TextView) convertView.findViewById(R.id.qte_par_lot);
        viewHolder.numLot = (TextView) convertView.findViewById(R.id.numLot);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.textviewSerie = (TextView) convertView.findViewById(R.id.textviewSerie);
        viewHolder.dataMatrix = (ImageView) convertView.findViewById(R.id.boutonRechercheDataMatrix);
        viewHolder.layoutQuantite = (LinearLayout) convertView.findViewById(R.id.layoutQuantite);
        //viewHolder.imageCadenas = (ImageView) convertView.findViewById(R.id.imageCadenas);
        viewHolder.numeroSerie = (EditText) convertView.findViewById(R.id.numeroSerie);
        viewHolder.layoutNumSerie = (LinearLayout) convertView.findViewById(R.id.layoutNumSerie);
        viewHolder.layoutPrincipal = (LinearLayout) convertView.findViewById(R.id.layoutPrincipal);
        viewHolder.statutLigne = (TextView) convertView.findViewById(R.id.statutLigne);

        final PH_Preparation_Ligne_VerrouPharmacie_Adapte phPreparationLigneVerrouPharmacieAdapte = (PH_Preparation_Ligne_VerrouPharmacie_Adapte) getItem(position);

        if (phPreparationLigneVerrouPharmacieAdapte != null) {
            // Affichage des valeurs
            viewHolder.designation.setText(phPreparationLigneVerrouPharmacieAdapte.getDesignation());
            viewHolder.produitRef.setText(phPreparationLigneVerrouPharmacieAdapte.getProduitRef());
            viewHolder.nbColis.setText(phPreparationLigneVerrouPharmacieAdapte.getNbColis());
            viewHolder.qteParLot.setText(phPreparationLigneVerrouPharmacieAdapte.getQteRALLot());
            viewHolder.statutLigne.setText(viewHolder.valeurStatut);
            if(!viewHolder.produit_inconnu)
            {
                if (viewHolder.valeurQteParLot > -1) {
                    viewHolder.qteParLot.setText(String.valueOf(viewHolder.valeurQteParLot));
                } else {
                    viewHolder.qteParLot.setText(phPreparationLigneVerrouPharmacieAdapte.getQteParLot().equals("0") ? "" : phPreparationLigneVerrouPharmacieAdapte.getQteParLot());
                    viewHolder.valeurQteParLot = Integer.parseInt(phPreparationLigneVerrouPharmacieAdapte.getQteParLot());
                }

                if (!viewHolder.valeurLot.equals("")) {
                    viewHolder.numLot.setText(viewHolder.valeurLot);
                } else {
                    viewHolder.numLot.setText(phPreparationLigneVerrouPharmacieAdapte.getNumLot());
                    viewHolder.valeurLot = phPreparationLigneVerrouPharmacieAdapte.getNumLot();
                }

                if(phPreparationLigneVerrouPharmacieAdapte.getSerieNumero().contentEquals("") || phPreparationLigneVerrouPharmacieAdapte.getSerieNumero() == null)
                {
                    viewHolder.layoutNumSerie.setVisibility(View.GONE);
                    viewHolder.valeurSerie = "";
                }
                else
                {
                    viewHolder.numeroSerie.setText(phPreparationLigneVerrouPharmacieAdapte.getSerieNumero());
                    viewHolder.valeurSerie = phPreparationLigneVerrouPharmacieAdapte.getSerieNumero();
                }

                // Gestion de la date
                if (viewHolder.valeurDate.equals("")) {
                    Date date = null;
                    DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        if (phPreparationLigneVerrouPharmacieAdapte.getPeremptionDate().length() >= 10) {
                            date = dateDecodeur.parse(phPreparationLigneVerrouPharmacieAdapte.getPeremptionDate().substring(0, 10));
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            viewHolder.valeurDate = dateFormat.format(date);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    viewHolder.setDatePeremptionColor(date);
                }

                if (!viewHolder.valeurDate.equals("")) {
                    viewHolder.datePeremption.setText(viewHolder.valeurDate);
                    Date date = null;
                    DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        date = dateDecodeur.parse(viewHolder.valeurDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    viewHolder.setDatePeremptionColor(date);
                } else {
                    viewHolder.datePeremption.setText("");
                    viewHolder.setDatePeremptionColor(null);
                }
            }

            if(viewHolder.valeurStatut.contentEquals("Dévérouillée"))
            {
                viewHolderList.get(position).layoutQuantite.setBackgroundColor(context.getColor(R.color.vert));
                //viewHolderList.get(position).imageCadenas.setBackgroundColor(context.getColor(R.color.vert));
            }
            else
            {
                viewHolderList.get(position).layoutQuantite.setBackgroundColor(context.getColor(R.color.rouge2));
                //viewHolderList.get(position).imageCadenas.setBackgroundColor(context.getColor(R.color.rouge2));
            }

            // Gestion des clics et des modifications faites par l'utilisateur
            GestionnaireEditText gestionnaireEditText = new GestionnaireEditText(viewHolder, phPreparationLigneVerrouPharmacieAdapte);
            viewHolder.datePeremption.setOnClickListener(clicDate);
            viewHolder.dataMatrix.setOnClickListener(clicDataMatrix);

            viewHolder.qteParLot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderList.get(position).valeurStatut.contentEquals("Vérouillée"))
                    {
                        viewHolderList.get(position).statutLigne.setText("Dévérouillée");
                        viewHolderList.get(position).valeurStatut = "Dévérouillée";
                        viewHolderList.get(position).layoutQuantite.setBackgroundColor(context.getColor(R.color.vert));
                    }
                    else
                    {
                        viewHolderList.get(position).statutLigne.setText("Vérouillée");
                        viewHolderList.get(position).valeurStatut = "Vérouillée";
                        viewHolderList.get(position).layoutQuantite.setBackgroundColor(context.getColor(R.color.rouge2));
                    }
                }
            });

            viewHolder.qteParLot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Ouvre une boite de dialogue avec un NumberPicker
                    String title = viewHolderList.get(position).designation.getText().toString();
                    String message = "Quantité déverrouillée : ";
                    int maxValue = viewHolder.valeurQteDemander;
                    int value = 0;
                    if(!viewHolderList.get(position).qteParLot.getText().toString().contentEquals(""))
                        value = Integer.parseInt(viewHolderList.get(position).qteParLot.getText().toString());

                    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int qteAprès = aNumberPicker.getValue();
                            viewHolderList.get(position).qteParLot.setText(String.valueOf(qteAprès));
                            phPreparationLigneVerrouPharmacieAdapte.setQteParLot(String.valueOf(qteAprès));
                            viewHolder.valeurQteParLot = qteAprès;
                            viewHolderList.get(position).valeurStatut = "Dévérouillée";
                            notifyDataSetChanged();

                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            dialog.dismiss();
                        }
                    };

                    Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

                    return false;
                }
            });

            if(phPreparationLigneVerrouPharmacieAdapte.getSerieNumero() == null)
                phPreparationLigneVerrouPharmacieAdapte.setSerieNumero("");
            if(phPreparationLigneVerrouPharmacieAdapte.getSerieNumero() != null || !phPreparationLigneVerrouPharmacieAdapte.getSerieNumero().contentEquals("") || !phPreparationLigneVerrouPharmacieAdapte.getSerieNumero().contentEquals("null"))
            {
                String numero_commande = phPreparationLigneVerrouPharmacieAdapte.getPhPreparation().getListe().trim();
                String[] tab_numero = numero_commande.split(" ");
                numero_commande = tab_numero[tab_numero.length-1];

                PH_Serialisation serialisation_courant = null;
                if(phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant() != null)
                    serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationVerrou(db, numero_commande, phPreparationLigneVerrouPharmacieAdapte.getProduitCorrespondant().getID_produit());
                if(serialisation_courant != null)
                {
                    String resultat_serialisation = serialisation_courant.getResultat();

                    if(resultat_serialisation.contentEquals("INACTIVE") || resultat_serialisation.contentEquals("UNKNOWN"))
                    {
                         if(first_passage)
                         {
                             if(viewHolder.valeurDate.contentEquals("00/00/0000"))
                             {
                                 viewHolder.valeurDate = "00/00/0000";
                                 viewHolder.datePeremption.setText("00/00/0000");
                             }
                             else
                             {
                                 viewHolder.datePeremption.setText(viewHolder.valeurDate);
                             }

                             if(viewHolder.valeurQteParLot == 0)
                             {
                                 viewHolder.valeurQteParLot = 0;
                                 viewHolder.valeurQteParLot = 0;
                             }
                             else
                             {
                                 viewHolder.qteParLot.setText(String.valueOf(viewHolder.valeurQteParLot));
                             }

                             if(viewHolder.valeurSerie.contentEquals(""))
                             {
                                 viewHolder.valeurSerie = "";
                                 viewHolder.numeroSerie.setText("");
                             }
                             else
                             {
                                 viewHolder.numeroSerie.setText(viewHolder.valeurSerie);
                             }

                             if(viewHolder.valeurLot.contentEquals(""))
                             {
                                 viewHolder.numLot.setText("");
                                 viewHolder.valeurLot = "";
                             }
                             else
                             {
                                 viewHolder.numLot.setText(viewHolder.valeurLot);
                             }
                         }

                        if(!first_passage)
                        {
                            viewHolder.numLot.setText("");
                            viewHolder.valeurLot = "";
                            viewHolder.numeroSerie.setText("");
                            viewHolder.valeurLot = "";
                            viewHolder.valeurQteParLot = 0;
                            viewHolder.valeurDate = "00/00/0000";
                            viewHolder.datePeremption.setText("00/00/0000");
                            viewHolder.qteParLot.setText("0");
                            nb_inactive++;
                            viewHolder.produit_inconnu = true;
                        }
                    }
                }
            }
        }

        if(position+1 == viewHolderList.size())
        {
            if(!first_passage)
            {
                if(nb_inactive > 0)
                {
                    first_passage = true;
                    Alerte.afficherAlerte(context, "Erreur", "Produit inactif détecté, vérifiez vos surveillances références", "alerte");
                }
            }
        }

        return convertView;
    }

    @Override
    public void clear() {
        phPreparationLigneVerrouPharmacieAdapteList.clear();
    }

    public void mettreAJourPHPrepLigne(PH_Preparation_Ligne_AdapteViewHolder viewHolder, PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {
        PH_Preparation_Ligne phPrepLigne = phPrepLigneAdapte.getPhPreparationLigne();
        Stock_Lot_Emplacement_Light stockLotEmplacement = phPrepLigneAdapte.getStockLotEmplacement();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

        stockLotEmplacement.setLot(viewHolder.valeurLot);
        stockLotEmplacement.setQte_Preparer(viewHolder.valeurQteParLot);
        try {
            Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
            stockLotEmplacement.setPeremptionDate(dateFormat.format(dateFournie));
            phPrepLigneAdapte.setPeremptionDate(dateFormat.format(dateFournie));
            phPrepLigne.setPeremptionDate(dateFormat.format(dateFournie));
            viewHolder.setDatePeremptionColor(dateFournie);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Stock_Lot_EmplacementLightOpenHelper.mettreAJourUnStockLotEmplacement(db, stockLotEmplacement);

        phPrepLigneAdapte.setNumLot(viewHolder.valeurLot);

        int qteAvant = 0;

        if(phPrepLigneAdapte.getNbColis() != null && !phPrepLigneAdapte.getNbColis().contentEquals(""))
        {
            qteAvant = Integer.parseInt(phPrepLigneAdapte.getNbColis());
        }

        int qteAprès = viewHolder.recupererNbColis(phPrepLigneAdapte.getProduitCorrespondant().getID_produit(), viewHolder.valeurQteParLot);

        phPrepLigneAdapte.setNbColis(String.valueOf(qteAprès));

        phPrepLigneAdapte.setQteParLot(String.valueOf(viewHolder.valeurQteParLot));

        phPrepLigne.setLotNumero(viewHolder.valeurLot);
        phPrepLigne.setQte_RAL(viewHolder.valeurQteParLot);
        phPrepLigne.setSerieNumero(viewHolder.valeurSerie);

        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, phPrepLigne);
        mOnDataChangeListener.onDataChanged(qteAvant, qteAprès);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        mOnDataChangeListener = onDataChangeListener;
    }

    public interface OnDataChangeListener {
        void onDataChanged(int quantitéAvant, int quantitéAprès);
    }

    /* Appel et gestion du Widget Android de sélection de date */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        PH_Preparation_Ligne_AdapteViewHolder viewHolder;
        SQLiteDatabase db;
        PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte;

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        // Je sais pas pourquoi mais les mois commencent à 0 et les jours à 1...
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String monthString = month < 9 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
            String dayString = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
            String date = dayString + "/" + monthString + "/" + String.valueOf(year);
            viewHolder.datePeremption.setText(date);
            viewHolder.valeurDate = date;

            PH_Preparation_Ligne phPrepLigne = phPrepLigneAdapte.getPhPreparationLigne();
            Stock_Lot_Emplacement_Light stockLotEmplacement = phPrepLigneAdapte.getStockLotEmplacement();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            stockLotEmplacement.setLot(viewHolder.valeurLot);
            stockLotEmplacement.setQte_Preparer(viewHolder.valeurQteParLot);
            try {
                Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
                viewHolder.setDatePeremptionColor(dateFournie);
                stockLotEmplacement.setPeremptionDate(dateFormat.format(dateFournie));
                phPrepLigneAdapte.setPeremptionDate(dateFormat.format(dateFournie));
                phPrepLigne.setPeremptionDate(dateFormat.format(dateFournie));
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

        public void setViewHolder(PH_Preparation_Ligne_AdapteViewHolder view, SQLiteDatabase database, PH_Preparation_Ligne_VerrouPharmacie_Adapte adapte) {
            viewHolder = view;
            db = database;
            phPrepLigneAdapte = adapte;
        }
    }

    private class GestionnaireEditText {

        public PH_Preparation_Ligne_AdapteViewHolder viewHolder;

        public GestionnaireEditText(final PH_Preparation_Ligne_AdapteViewHolder view, final PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigneAdapte) {
            viewHolder = view;

            viewHolder.numLot.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    viewHolder.valeurLot = s.toString();
                    mettreAJourPHPrepLigne(viewHolder, phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                }

            });

            viewHolder.numeroSerie.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    viewHolder.valeurSerie = s.toString();
                    mettreAJourPHPrepLigne(viewHolder, phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                }
            });

            viewHolder.qteParLot.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (!viewHolder.qteParLot.getText().toString().equals("")) {
                            viewHolder.valeurQteParLot = Integer.parseInt(viewHolder.qteParLot.getText().toString());
                        } else {
                            viewHolder.valeurQteParLot = 0;
                        }
                        int nbColis = viewHolder.recupererNbColis(phPrepLigneAdapte.getProduitCorrespondant().getID_produit(), viewHolder.valeurQteParLot);
                        viewHolder.nbColis.setText(String.valueOf(nbColis));

                        mettreAJourPHPrepLigne(viewHolder, phPreparationLigneVerrouPharmacieAdapteList.get(viewHolderList.indexOf(viewHolder)));
                    }
                    return false;
                }
            });
        }
    }

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
        public ImageView dataMatrix;
        public LinearLayout layoutNumSerie;
        public LinearLayout layoutPrincipal;
        public LinearLayout layoutQuantite;
        public boolean produit_inconnu = false;
        public int valeurQteParLot = -1;
        public int valeurQteDemander;
        public String valeurDate = "";
        public String valeurLot = "";
        public String valeurSerie = "";
        public String valeurStatut = "Vérouillée";

        public PH_Preparation_Ligne_AdapteViewHolder(PH_Preparation_Ligne_VerrouPharmacie_Adapte phPrepLigne) {
            if (!phPrepLigne.getQteParLot().equals("")) {
                this.valeurQteDemander = Integer.parseInt(phPrepLigne.getQteParLot());
                this.valeurQteParLot = Integer.parseInt(phPrepLigne.getQteParLot());
            } else {
                this.valeurQteDemander = 0;
                this.valeurQteParLot = 0;
            }
            this.valeurLot = phPrepLigne.getNumLot();

            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date date = new Date();
            try {
                date = dateDecodeur.parse(phPrepLigne.getPeremptionDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.valeurDate = dateFormat.format(date);
        }

        public void setDatePeremptionColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int delai30jours = -30;
                int delai60jours = -60;

                if (delai >= delai30jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delai >= delai60jours) {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    datePeremption.setTextColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                datePeremption.setTextColor(Color.BLACK);
            }

        }

        // Permet de calculer les nombre de colis en fonction du conditionnement du produit
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
    }
}
