package fr.alcyons.phimr4.ListViewAdapters;


import android.content.Context;
import android.content.DialogInterface;


import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.R;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;


/**
 * Created by jessica on 04/10/2017.
 */

public class Demande_PUIAdapter extends ArrayAdapter {

    public List<Demande_PUI> demandePuiList;
    public List<Demande_PUI> demandePuiOriginalList;
    public List<Demande_PUIViewHolder> Demande_PUIViewHolderList;
    Context context;
    Demande_PUIFilter filter;

    Demande_PUIViewHolder viewHolderAModifier;
    Demande_PUI demandePUI;

    int qteSaisieStockDestinataire = 0;
    int qteSaisieApreparer = 0;
    Demande_PUIViewHolder viewHolder;

    public Demande_PUIAdapter(Context context, List<Demande_PUI> demandePuiList) {
        super(context, 0, demandePuiList);
        this.demandePuiList = demandePuiList;
        this.context = context;

        this.demandePuiOriginalList = new ArrayList<>();
        demandePuiOriginalList.addAll(this.demandePuiList);

        Demande_PUIViewHolderList = new ArrayList<>();
        for (int i = 0; i < demandePuiList.size(); i++) {
            Demande_PUIViewHolder viewHolder = new Demande_PUIViewHolder();
            Demande_PUIViewHolderList.add(viewHolder);
        }

        filter = null;
        viewHolderAModifier = null;
        demandePUI = null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = null;
        final Demande_PUI Demande_PUICourant = (Demande_PUI) getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_demande_pui_modifiable, parent, false);


            viewHolder = Demande_PUIViewHolderList.get(position);
            // Récupération des objets graphiques
            viewHolder.designation = (TextView) convertView.findViewById(R.id.designation);
            viewHolder.categorie = (TextView) convertView.findViewById(R.id.categorie);
            viewHolder.reference = (TextView) convertView.findViewById(R.id.reference);
            viewHolder.qteStockPUI = (TextView) convertView.findViewById(R.id.qteStockPUI);
            viewHolder.qteBesoin = (TextView) convertView.findViewById(R.id.qteBesoin);
            viewHolder.qteConditionnement = (TextView) convertView.findViewById(R.id.qteConditionnement);
            viewHolder.unite = (TextView) convertView.findViewById(R.id.unite);

            viewHolder.qteStockDestinataire = (TextView) convertView.findViewById(R.id.qteStockDestinataire);
            viewHolder.qte_a_preparer = (TextView) convertView.findViewById(R.id.qte_a_preparer);


            // Affichage des valeurs
            viewHolder.designation.setText(Demande_PUICourant.getDésignation());
            viewHolder.categorie.setText(Demande_PUICourant.getCatégorie());
            viewHolder.reference.setText(Demande_PUICourant.getRéférence());
            viewHolder.qteStockPUI.setText(String.valueOf((int) Demande_PUICourant.getStock_pui()));
            viewHolder.qteBesoin.setText(String.valueOf(Demande_PUICourant.getBesoin()));
            viewHolder.qteConditionnement.setText(String.valueOf(Demande_PUICourant.getConditionnement()));
            viewHolder.unite.setText(Demande_PUICourant.getUnité());

            if((int) Demande_PUICourant.getStock_destinataire() != 0){
                viewHolder.qteStockDestinataire.setText(String.valueOf((int) Demande_PUICourant.getStock_destinataire()));
            }
            else{
                viewHolder.qteStockDestinataire.setText("");
            }
            if(Demande_PUICourant.getA_preparer() != 0){
                viewHolder.qte_a_preparer.setText(String.valueOf(Demande_PUICourant.getA_preparer()));
            }
            else{
                viewHolder.qte_a_preparer.setText("");
            }

            EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        saisie_EditText(v);
                        return true;
                    }
                    return false;
                }
            };

            viewHolder.qteStockDestinataire.setOnEditorActionListener(onEditorActionListener);
            viewHolder.qte_a_preparer.setOnEditorActionListener(onEditorActionListener);

            viewHolder.qteStockDestinataire.clearFocus();
            viewHolder.qte_a_preparer.clearFocus();
        }

        viewHolder.qteStockDestinataire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ouvre une boite de dialogue avec un NumberPicker
                String title = Demande_PUIViewHolderList.get(position).designation.getText().toString();
                String message = "Stock destinataire : ";
                int maxValue = 1000000;
                int value =0;
                if(!Demande_PUIViewHolderList.get(position).qteStockDestinataire.getText().toString().contentEquals(""))
                    value = Integer.parseInt(Demande_PUIViewHolderList.get(position).qteStockDestinataire.getText().toString());

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        Demande_PUIViewHolderList.get(position).qteStockDestinataire.setText(String.valueOf(qteAprès));
                        Demande_PUICourant.setStock_destinataire(qteAprès);
                        notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });

        viewHolder.qte_a_preparer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ouvre une boite de dialogue avec un NumberPicker
                String title = Demande_PUIViewHolderList.get(position).designation.getText().toString();
                String message = "Quantité à préparer : ";
                int maxValue = 1000000;
                int value = 0;
                if(!Demande_PUIViewHolderList.get(position).qte_a_preparer.getText().toString().contentEquals(""))
                    value = Integer.parseInt(Demande_PUIViewHolderList.get(position).qte_a_preparer.getText().toString());

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        Demande_PUIViewHolderList.get(position).qte_a_preparer.setText(String.valueOf(qteAprès));
                        Demande_PUICourant.setA_preparer(qteAprès);
                        notifyDataSetChanged();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });

        return convertView;
    }

    public void saisie_EditText(View v){

        viewHolderAModifier = null;
        boolean qteAPreparerSaisie = false;
        for (Demande_PUIViewHolder viewHolderCourant : Demande_PUIViewHolderList) {
            if (viewHolderCourant.qte_a_preparer == v || viewHolderCourant.qteStockDestinataire == v) {
                if(viewHolderCourant.qte_a_preparer == v){
                    qteAPreparerSaisie = true;
                }
                viewHolderAModifier = viewHolderCourant;
                break;
            }
        }
        int position = Demande_PUIViewHolderList.indexOf(viewHolderAModifier);

        demandePUI = (Demande_PUI) getItem(position);

        qteSaisieStockDestinataire = 0;
        qteSaisieApreparer = 0;

        // Récupération des valeurs saisies
        if (!viewHolderAModifier.qteStockDestinataire.getText().toString().equals("")) {
            qteSaisieStockDestinataire = Integer.parseInt(viewHolderAModifier.qteStockDestinataire.getText().toString());
        }

        if (!viewHolderAModifier.qte_a_preparer.getText().toString().equals("")) {
            qteSaisieApreparer = Integer.parseInt(viewHolderAModifier.qte_a_preparer.getText().toString());
        }
        
        String name = context.getClass().getSimpleName();
        if (name.contains("InformationDemandeParticuliereActivity")) {

        } else {
            if(!qteAPreparerSaisie){
                if (qteSaisieStockDestinataire >= qteSaisieApreparer || demandePUI.getBesoin() == 0) {
                    qteSaisieApreparer = 0;
                }

                if (qteSaisieStockDestinataire < demandePUI.getBesoin()) {
                    qteSaisieApreparer = demandePUI.getBesoin() - qteSaisieStockDestinataire;
                }

                if (qteSaisieApreparer > demandePUI.getStock_pui()) {
                    qteSaisieApreparer = (int) demandePUI.getStock_pui();
                }
            }
        }

        qteSaisieApreparer = (int) demandePUI.Conditionnement_Calcul(qteSaisieApreparer, demandePUI.getConditionnement());

        demandePUI.setA_preparer(qteSaisieApreparer);
        demandePUI.setStock_destinataire(qteSaisieStockDestinataire);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        notifyDataSetChanged();
    }

    public class Demande_PUIViewHolder {

        public TextView designation;
        public TextView categorie;
        public TextView reference;
        public TextView qteStockPUI;
        public TextView qteBesoin;
        public TextView qteConditionnement;
        public TextView unite;
        public TextView qteStockDestinataire;
        public TextView qte_a_preparer;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new Demande_PUIFilter();

        return filter;
    }

    private class Demande_PUIFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String chaineAtrouver = constraint.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();

            demandePuiList.clear();
            for (Demande_PUI Demande_PUICourant : demandePuiOriginalList
                    ) {
                demandePuiList.add(Demande_PUICourant);
            }

            if (chaineAtrouver != null && chaineAtrouver.toString().length() > 0) {
                List<Demande_PUI> founded = new ArrayList<Demande_PUI>();

                for (Demande_PUI Demande_PUI : demandePuiList) {

                    // Vérifie le début du premier mot
                    String Demande_PUIProduitDesignation = Demande_PUI.getDésignation().toLowerCase();

                    if (Demande_PUIProduitDesignation.startsWith(String.valueOf(chaineAtrouver))) {
                        founded.add(Demande_PUI);
                    } else if (Demande_PUI.getCatégorie().toLowerCase().contains(chaineAtrouver)) {
                        founded.add(Demande_PUI);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = Demande_PUIProduitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(chaineAtrouver))) {
                                founded.add(Demande_PUI);
                                break;
                            }
                        }
                    }
                }

                filterResults.values = founded;
                filterResults.count = founded.size();
            } else {
                filterResults.values = demandePuiOriginalList;
                filterResults.count = demandePuiOriginalList.size();
            }
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            demandePuiList.clear();
            for (Demande_PUI Demande_PUI : (List<Demande_PUI>) results.values) {
                add(Demande_PUI);
            }
            notifyDataSetChanged();
        }

    }
}