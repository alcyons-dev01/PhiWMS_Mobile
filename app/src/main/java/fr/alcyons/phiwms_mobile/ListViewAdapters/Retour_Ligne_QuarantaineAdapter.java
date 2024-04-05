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
import android.widget.RelativeLayout;
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
import com.example.phiwms_mobile.Quarantaine.DetailQuarantaineActivity;
import com.example.phiwms_mobile.R;

import static fr.alcyons.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Created by quentinlanusse on 22/06/2017.
 */

public class Retour_Ligne_QuarantaineAdapter extends ArrayAdapter {

    public List<Retour_Ligne> retourLigneList;
    public List<Retour_LigneViewHolder> viewHolderList;

    SQLiteDatabase db;
    Context context;
    View.OnClickListener clicDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerFragment newFragment = new DatePickerFragment();
            Retour_LigneViewHolder viewHolder = null;
            for (Retour_LigneViewHolder viewHolderC : viewHolderList) {
                if (viewHolderC.datePeremption == v.findViewById(R.id.datePeremption)) {
                    viewHolder = viewHolderC;
                    break;
                }
            }
            newFragment.setViewHolder(viewHolder, retourLigneList.get(viewHolderList.indexOf(viewHolder)), ((MenuActivity) context).db);
            newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "timePicker");
        }
    };

    View.OnClickListener clicDataMatrix = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RelativeLayout parent = (RelativeLayout) v.getParent().getParent();
            LinearLayout zoneRef = (LinearLayout) parent.findViewById(R.id.zoneQuarantaine);
            FrameLayout zoneLot = (FrameLayout) zoneRef.findViewById(R.id.zoneLot);
            FrameLayout zoneDate = (FrameLayout) zoneRef.findViewById(R.id.zoneDate);
            TextView date = (TextView) zoneDate.findViewById(R.id.datePeremption);
            TextView numLot = (TextView) zoneLot.findViewById(R.id.lotRetourne);
            Retour_LigneViewHolder viewHolderSelectionne = null;
            for (Retour_LigneViewHolder viewHolder : viewHolderList) {
                if (viewHolder.datePeremption == date && viewHolder.lotRetourne == numLot) {
                    viewHolderSelectionne = viewHolder;
                }
            }
            ((DetailQuarantaineActivity) context).decoderCodeBarre(date, numLot, viewHolderSelectionne, String.valueOf(viewHolderSelectionne.designation.getText()));
        }
    };

    public Retour_Ligne_QuarantaineAdapter(Context context, SQLiteDatabase db, List<Retour_Ligne> retourLigneList) {
        super(context, 0, retourLigneList);
        this.context = context;
        this.retourLigneList = retourLigneList;
        this.db = db;

        this.viewHolderList = new ArrayList<>();
        for (Retour_Ligne retourLigne : retourLigneList) {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            viewHolder.layoutRef = R.layout.row_retour_ligne_quarantaine;
            viewHolder.valeurQteRetourner = (int) retourLigne.getQte_Retourner();
            this.viewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View currentFocus = ((Activity) context).getCurrentFocus();
        if (currentFocus != null) {

        }

        final Retour_LigneViewHolder viewHolder = viewHolderList.get(position);


        convertView = LayoutInflater.from(getContext()).inflate(viewHolder.layoutRef, parent, false);


        // Récupération des objets graphiques
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.fournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
        viewHolder.lotRetourne = (TextView) convertView.findViewById(R.id.lotRetourne);
        viewHolder.numeroSerieModifiable = (TextView) convertView.findViewById(R.id.numeroSerieModifiable);
        viewHolder.datePeremption = (TextView) convertView.findViewById(R.id.datePeremption);
        viewHolder.qteRetourner = (TextView) convertView.findViewById(R.id.QteRetourner);
        viewHolder.qteDestruction = (TextView) convertView.findViewById(R.id.QteDestruction);
        viewHolder.qteRetourPUI = (TextView) convertView.findViewById(R.id.QteRetourPUI);
        viewHolder.qteRetourFournisseur = (TextView) convertView.findViewById(R.id.QteRetourFournisseur);
        viewHolder.resultatSerialisation = (TextView) convertView.findViewById(R.id.resultatSerialisation);
        viewHolder.zoneNumSerie = (LinearLayout) convertView.findViewById(R.id.zoneNumSerie);

        final Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);

        if (retourLigne != null) {
            // Affichage des valeurs
            viewHolder.designation.setText(retourLigne.getProduit_Designation());
            viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());

            String[] tab_four = retourLigne.getProduit_Fournisseur().split(" ");
            String fournisseur_nom = tab_four[0];
            viewHolder.fournisseur.setText(fournisseur_nom);

            viewHolder.qteRetourner.setText(String.valueOf((int) retourLigne.getQte_Retourner()));
            viewHolder.valeurQteRetourner = (int) retourLigne.getQte_Retourner();

            // Affichage des valeurs saisissables
            if (viewHolder.valeurDestruction > -1) {
                viewHolder.qteDestruction.setText(String.valueOf(viewHolder.valeurDestruction));
            } else {
                viewHolder.qteDestruction.setText(retourLigne.getDestruction_Qte() <= 0 ? "" : String.valueOf(retourLigne.getDestruction_Qte()));
            }
            if (viewHolder.valeurFrs > -1) {
                viewHolder.qteRetourFournisseur.setText(String.valueOf(viewHolder.valeurFrs));
            } else {
                viewHolder.qteRetourFournisseur.setText(retourLigne.getRetourFrs_Qte() <= 0 ? "" : String.valueOf(retourLigne.getRetourFrs_Qte()));
            }
            if (viewHolder.valeurPUI > -1) {
                viewHolder.qteRetourPUI.setText(String.valueOf(viewHolder.valeurPUI));
            } else {
                viewHolder.qteRetourPUI.setText(retourLigne.getRetourPui_Qte() <= 0 ? "" : String.valueOf(retourLigne.getRetourPui_Qte()));
            }
            if (!viewHolder.valeurLot.equals("")) {
                viewHolder.lotRetourne.setText(viewHolder.valeurLot);
            } else {
                viewHolder.lotRetourne.setText(retourLigne.getLot_Retourner());
                viewHolder.valeurLot = retourLigne.getLot_Retourner();
            }

            if (!viewHolder.valeurSerie.equals("")) {
                viewHolder.numeroSerieModifiable.setText(viewHolder.valeurSerie);
                viewHolder.zoneNumSerie.setVisibility(View.VISIBLE);
            } else {

                viewHolder.zoneNumSerie.setVisibility(View.GONE);
            }

            // Date de péremption
            Date date = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            try {
                if (retourLigne.getPeremptionDate().length() >= 10) {
                    date = dateDecodeur.parse(retourLigne.getPeremptionDate().substring(0, 10));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (viewHolder.valeurDate.equals("") && date != null) {
                viewHolder.valeurDate = dateFormat.format(date);
            }

            if (!viewHolder.valeurDate.equals("")) {
                viewHolder.datePeremption.setText(viewHolder.valeurDate);
            } else {
                viewHolder.datePeremption.setText("");
            }
            viewHolder.setDatePeremptionColor(date);

            // Gestion des actions et saisies utilisateur
            if (viewHolder.layoutRef == R.layout.row_retour_ligne_quarantaine_modifiable) {
                viewHolder.zoneDate = (FrameLayout) convertView.findViewById(R.id.zoneDate);
                viewHolder.zoneDate.setOnClickListener(clicDate);
                viewHolder.dataMatrix = (LinearLayout) convertView.findViewById(R.id.zoneDataMatrix);
                viewHolder.dataMatrix.setOnClickListener(clicDataMatrix);
                viewHolder.datePeremption.setPaintFlags(viewHolder.datePeremption.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            } else {
                GestionnaireTextView gestionnaireTextView = new GestionnaireTextView(viewHolder, retourLigneList.get(viewHolderList.indexOf(viewHolder)));
            }

            if(retourLigne.getSerie_Retourner()!= null)
            {
                viewHolder.numeroSerieModifiable.setText(retourLigne.getSerie_Retourner());
            }
            else
            {
                viewHolder.numeroSerieModifiable.setText("");
            }
        }


        Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigne.getCode_produit());
        String gtin = produit.getGTIN();
        if(gtin.length() > 14)
        {
            gtin = gtin.substring(2);
        }

        String serie = retourLigne.getSerie_Retourner();

        if(serie != null && gtin != null)
        {
            PH_Serialisation serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationQuarantaine(db, gtin, serie);
            if(serialisation_courant != null)
            {
                if(serialisation_courant.getResultat().contentEquals("UNKNOWN")) {
                    viewHolder.valeurDestruction = viewHolder.valeurQteRetourner;
                    viewHolder.qteDestruction.setText(String.valueOf(viewHolder.valeurDestruction));
                    retourLigne.setQte_Retourner(viewHolder.valeurQteRetourner);
                }
                viewHolder.resultatSerialisation.setText(serialisation_courant.getResultat());
                viewHolder.valeurSerialisation = serialisation_courant.getResultat();
            }
        }

        if (!viewHolder.valeurSerialisation.equals("")) {
            viewHolder.resultatSerialisation.setText(viewHolder.valeurSerialisation);
        } else {
            viewHolder.resultatSerialisation.setText("");
            viewHolder.valeurSerialisation = "";
        }

        if(viewHolder.modification)
        {
            if(!viewHolder.resultatSerialisation.getText().toString().contentEquals("UNKNOWN"))
            {
                viewHolder.qteRetourFournisseur.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        afficherNumberPicker(viewHolder, "Fournisseur", retourLigne);
                    }
                });

                viewHolder.qteRetourPUI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        afficherNumberPicker(viewHolder, "PUI", retourLigne);
                    }
                });
            }

            viewHolder.qteDestruction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    afficherNumberPicker(viewHolder, "Destruction", retourLigne);
                }
            });
        }

        return convertView;
    }

    public void setModeModif(boolean modeModif, Retour_LigneViewHolder viewHolder) {
        if (modeModif) {
            viewHolder.layoutRef = R.layout.row_retour_ligne_quarantaine_modifiable;
            viewHolder.modification = true;
        } else {
            viewHolder.layoutRef = R.layout.row_retour_ligne_quarantaine;
            viewHolder.firstModifPassage = true;
        }
    }

    public long mettreAJourUnRetourLigne(Retour_Ligne retourLigne, Retour_LigneViewHolder viewHolder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
            retourLigne.setPeremptionDate(dateFormat.format(dateFournie));
            viewHolder.setDatePeremptionColor(dateFournie);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        retourLigne.setLot_Retourner(viewHolder.valeurLot);
        retourLigne.setSerie_Retourner(viewHolder.valeurSerie);
        retourLigne.setDestruction_Qte(viewHolder.valeurDestruction);
        retourLigne.setRetourPui_Qte(viewHolder.valeurPUI);
        retourLigne.setRetourFrs_Qte(viewHolder.valeurFrs);
        return Retour_LigneOpenHelper.mettreAJourUnRetourLigne(((MenuActivity) context).db, retourLigne);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        Retour_LigneViewHolder viewHolder;
        Retour_Ligne retourLigne;
        SQLiteDatabase db;

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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (month < 10) {
                month++;
            }
            String mois = "";
            if (month < 10) {
                mois += "0";
            }

            mois += String.valueOf(month);

            String date = String.valueOf(day) + "/" + mois + "/" + String.valueOf(year);

            viewHolder.datePeremption.setText(date);
            viewHolder.valeurDate = date;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
                retourLigne.setPeremptionDate(dateFormat.format(dateFournie));
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

        public void setViewHolder(Retour_LigneViewHolder view, Retour_Ligne retourL, SQLiteDatabase database) {
            this.viewHolder = view;
            this.db = database;
            this.retourLigne = retourL;
        }
    }

    private class GestionnaireTextView {
        public Retour_LigneViewHolder viewHolder;

        public Retour_Ligne retourLigne;

        View.OnClickListener clicPUIListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewHolder.resultatSerialisation.getText().toString().contentEquals("UNKNOWN"))
                {
                    if (!viewHolder.qteRetourPUI.getText().toString().equals(String.valueOf(viewHolder.valeurQteRetourner))) {
                        viewHolder.qteRetourPUI.setText(viewHolder.qteRetourner.getText().toString());
                        viewHolder.valeurPUI = viewHolder.valeurQteRetourner;

                        viewHolder.qteRetourFournisseur.setText(String.valueOf(0));
                        viewHolder.valeurFrs = 0;
                        viewHolder.qteDestruction.setText(String.valueOf(0));
                        viewHolder.valeurDestruction = 0;
                    } else {
                        viewHolder.valeurPUI = viewHolder.valeurQteRetourner;
                        viewHolder.valeurFrs = 0;
                        viewHolder.valeurDestruction = 0;
                        setModeModif(true, viewHolder);
                        notifyDataSetChanged();
                    }
                    mettreAJourUnRetourLigne(retourLigne, viewHolder);
                }
            }
        };

        View.OnClickListener clicDestructionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.qteDestruction.getText().toString().equals(String.valueOf(viewHolder.valeurQteRetourner))) {
                    viewHolder.qteDestruction.setText(viewHolder.qteRetourner.getText().toString());
                    viewHolder.valeurDestruction = viewHolder.valeurQteRetourner;

                    viewHolder.qteRetourFournisseur.setText(String.valueOf(0));
                    viewHolder.valeurFrs = 0;
                    viewHolder.qteRetourPUI.setText(String.valueOf(0));
                    viewHolder.valeurPUI = 0;
                } else {
                    viewHolder.valeurDestruction = viewHolder.valeurQteRetourner;
                    viewHolder.valeurFrs = 0;
                    viewHolder.valeurPUI = 0;
                    setModeModif(true, viewHolder);
                    notifyDataSetChanged();
                }
                mettreAJourUnRetourLigne(retourLigne, viewHolder);
            }
        };

        View.OnClickListener clicFournisseurListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewHolder.resultatSerialisation.getText().toString().contentEquals("UNKNOWN"))
                {
                    if (!viewHolder.qteRetourFournisseur.getText().toString().equals(String.valueOf(viewHolder.valeurQteRetourner))) {
                        viewHolder.qteRetourFournisseur.setText(viewHolder.qteRetourner.getText().toString());
                        viewHolder.valeurFrs = viewHolder.valeurQteRetourner;

                        viewHolder.qteRetourPUI.setText(String.valueOf(0));
                        viewHolder.valeurPUI = 0;
                        viewHolder.qteDestruction.setText(String.valueOf(0));
                        viewHolder.valeurDestruction = 0;
                    } else {
                        viewHolder.valeurFrs = viewHolder.valeurQteRetourner;
                        viewHolder.valeurPUI = 0;
                        viewHolder.valeurDestruction = 0;
                        setModeModif(true, viewHolder);
                        notifyDataSetChanged();
                    }
                    mettreAJourUnRetourLigne(retourLigne, viewHolder);
                }
            }
        };

        public GestionnaireTextView(Retour_LigneViewHolder vH, Retour_Ligne retourL) {
            this.viewHolder = vH;
            this.retourLigne = retourL;
            ((View) viewHolder.qteRetourPUI.getParent()).setOnClickListener(clicPUIListener);
            ((View) viewHolder.qteDestruction.getParent()).setOnClickListener(clicDestructionListener);
            ((View) viewHolder.qteRetourFournisseur.getParent()).setOnClickListener(clicFournisseurListener);
        }
    }

    private class GestionnaireEditText {

        public Retour_LigneViewHolder viewHolder;
        Retour_Ligne retourLigne;


        public GestionnaireEditText(Retour_LigneViewHolder view, final Retour_Ligne retour_ligne) {
            viewHolder = view;
            retourLigne = retour_ligne;


            viewHolder.lotRetourne.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    viewHolder.valeurLot = viewHolder.lotRetourne.getText().toString();
                    mettreAJourUnRetourLigne(retourLigneList.get(viewHolderList.indexOf(viewHolder)), viewHolder);
                }
            });

            viewHolder.firstModifPassage = false;
        }
    }

    public void afficherNumberPicker(final Retour_LigneViewHolder viewHolder, final String denomination, final Retour_Ligne courant)
    {
        // Ouvre une boite de dialogue avec un NumberPicker
        String title = denomination;
        String message = "Quantité placée : ";
        final int maxValue = viewHolder.valeurQteRetourner;
        int value = 0;
        switch (denomination)
        {
            case "PUI":
                if(!viewHolder.qteRetourPUI.getText().toString().contentEquals(""))
                    value = Integer.parseInt(viewHolder.qteRetourPUI.getText().toString());
                break;
            case "Destruction":
                if(!viewHolder.qteDestruction.getText().toString().contentEquals(""))
                    value = Integer.parseInt(viewHolder.qteDestruction.getText().toString());
                break;
            default:
                if(!viewHolder.qteRetourFournisseur.getText().toString().contentEquals(""))
                    value = Integer.parseInt(viewHolder.qteRetourFournisseur.getText().toString());
                break;
        }

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int qteAprès = aNumberPicker.getValue();

                if(maxValue == qteAprès)
                {
                    viewHolder.valeurPUI = 0;
                    viewHolder.valeurDestruction = 0;
                    viewHolder.valeurFrs = 0;
                }
                int total_place = 0;
                int difference = 0;
                int fournisseur = viewHolder.valeurFrs;
                int destruction = viewHolder.valeurDestruction;
                int pui = viewHolder.valeurPUI;
                switch (denomination)
                {
                    case "PUI":
                        viewHolder.valeurPUI = qteAprès;


                        if(destruction == -1)
                            destruction = 0;


                        if(fournisseur == -1)
                            fournisseur = 0;

                        total_place = destruction+fournisseur+viewHolder.valeurPUI;
                        difference = maxValue - total_place;
                        while(difference < 0)
                        {
                            if(viewHolder.valeurFrs > 0)
                            {
                                viewHolder.valeurFrs--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                            else
                            {
                                viewHolder.valeurDestruction--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                        }

                        viewHolder.qteRetourPUI.setText(String.valueOf(qteAprès));
                        viewHolder.qteRetourFournisseur.setText(String.valueOf(difference));
                        viewHolder.valeurFrs = difference;
                        viewHolder.valeurPUI = qteAprès;
                        courant.setRetourPui_Qte(qteAprès);
                        courant.setRetourFrs_Qte(difference);
                        break;
                    case "Destruction":
                        viewHolder.valeurDestruction = qteAprès;

                        if(fournisseur == -1)
                            fournisseur = 0;

                        if(pui == -1)
                            pui = 0;

                        total_place = viewHolder.valeurDestruction+fournisseur+pui;
                        difference = maxValue - total_place;
                        while(difference < 0)
                        {
                            if(viewHolder.valeurFrs > 0)
                            {
                                viewHolder.valeurFrs--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                            else
                            {
                                viewHolder.valeurPUI--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                        }
                        viewHolder.qteDestruction.setText(String.valueOf(qteAprès));
                        viewHolder.qteRetourPUI.setText(String.valueOf(difference));
                        viewHolder.valeurDestruction = qteAprès;
                        viewHolder.valeurPUI = difference;
                        courant.setDestruction_Qte(qteAprès);
                        courant.setRetourPui_Qte(difference);
                        break;
                    default:
                        viewHolder.valeurFrs = qteAprès;

                        if(destruction == -1)
                            destruction = 0;

                        if(pui == -1)
                            pui = 0;

                        total_place = destruction+viewHolder.valeurFrs+pui;
                        difference = maxValue - total_place;
                        while(difference < 0)
                        {
                            if(viewHolder.valeurDestruction > 0)
                            {
                                viewHolder.valeurDestruction--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                            else
                            {
                                viewHolder.valeurPUI--;
                                total_place = viewHolder.valeurDestruction+viewHolder.valeurFrs+viewHolder.valeurPUI;
                                difference = maxValue - total_place;
                            }
                        }
                        viewHolder.qteRetourFournisseur.setText(String.valueOf(qteAprès));
                        viewHolder.qteDestruction.setText(String.valueOf(difference));
                        viewHolder.valeurFrs = qteAprès;
                        viewHolder.valeurDestruction = difference;
                        courant.setRetourFrs_Qte(qteAprès);
                        courant.setDestruction_Qte(difference);
                        break;
                }


                notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
            }
        };

        Alerte.afficherAlerteNumberPicker(context, title, message, 0, maxValue, onClickListener);
        setModeModif(false, viewHolder);
        viewHolder.modification = false;
    }

    public class Retour_LigneViewHolder {
        public int layoutRef;
        public int valeurPUI = -1;
        public int valeurFrs = -1;
        public int valeurDestruction = -1;
        public int valeurQteRetourner;
        public String valeurLot = "";
        public String valeurDate = "";
        public String valeurSerie ="";
        public String valeurSerialisation = "";

        public boolean firstModifPassage = true;

        public TextView designation;
        public TextView referenceProduit;
        public TextView fournisseur;
        public TextView lotRetourne;
        public TextView datePeremption;
        public TextView qteRetourner;
        public TextView qteDestruction;
        public TextView qteRetourPUI;
        public TextView qteRetourFournisseur;
        public TextView numeroSerieModifiable;
        public TextView resultatSerialisation;
        public LinearLayout dataMatrix;
        public LinearLayout zoneNumSerie;
        public FrameLayout zoneDate;
        boolean modification = false;

        public void toutRetournerPUI() {
            valeurPUI = valeurQteRetourner;
            valeurFrs = 0;
            valeurDestruction = 0;
            mettreAJourUnRetourLigne(retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutRetournerFrs() {
            valeurPUI = 0;
            valeurFrs = valeurQteRetourner;
            valeurDestruction = 0;
            mettreAJourUnRetourLigne(retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutDetruire() {
            valeurPUI = 0;
            valeurFrs = 0;
            valeurDestruction = valeurQteRetourner;
            mettreAJourUnRetourLigne(retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
        }

        public void toutRemettreAZero() {
            valeurPUI = 0;
            valeurFrs = 0;
            valeurDestruction = 0;
            mettreAJourUnRetourLigne(retourLigneList.get(viewHolderList.indexOf(this)), this);
            notifyDataSetChanged();
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
    }
}
