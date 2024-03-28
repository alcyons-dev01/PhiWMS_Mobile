package com.example.phiwms_mobile.ListViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.phiwms_mobile.Classes.Retour_Ligne;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.R;
import com.example.phiwms_mobile.RetourDemande.DetailRetourDemandeActivity;

import static com.example.phiwms_mobile.Outils.Alerte.aNumberPicker;

/**
 * Created by olivier on 05/01/2018.
 */

public class RetourDemande_detailAdapter extends ArrayAdapter {

    public List<RetourLigneViewHolder> viewHolders = new ArrayList<>();
    SQLiteDatabase db;
    List<Retour_Ligne> retour_lignes;
    Context context;

    public RetourDemande_detailAdapter(Context context, List<Retour_Ligne> retour_lignes, SQLiteDatabase db) {
        super(context, 0, retour_lignes);
        this.retour_lignes = retour_lignes;
        this.context = context;
        this.db = db;
        for (Retour_Ligne retour_ligne : retour_lignes) {
            viewHolders.add(new RetourLigneViewHolder());
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_detail_retour_demande, parent, false);
        }

        RetourLigneViewHolder viewHolder = (RetourLigneViewHolder) convertView.getTag();

        if (viewHolder == null) {

            viewHolder = viewHolders.get(position);
            viewHolder.qteRetournee = (TextView) convertView.findViewById(R.id.qteRetournee);
            viewHolder.StockTheorique = (TextView) convertView.findViewById(R.id.StockTheorique);
            viewHolder.DesignationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.ReferenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.NomFournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
            viewHolder.Supprimer = (ImageView) convertView.findViewById(R.id.Supprimer);
        }

        final Retour_Ligne RetourLigneCourant = (Retour_Ligne) getItem(position);
        viewHolder.qteRetournee.setText(String.valueOf((int) RetourLigneCourant.getQte_Demander()));
        viewHolder.StockTheorique.setText(String.valueOf((int) RetourLigneCourant.getQte_avant_retour()));
        viewHolder.DesignationProduit.setText(RetourLigneCourant.getProduit_Designation());
        viewHolder.ReferenceProduit.setText(RetourLigneCourant.getProduit_Reference());
        viewHolder.NomFournisseur.setText(RetourLigneCourant.getProduit_Fournisseur());
        viewHolder.Supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetourLigneViewHolder viewHolderSelectionne = null;

                for (RetourLigneViewHolder viewHolder : viewHolders) {
                    if (viewHolder.Supprimer == v) {
                        viewHolderSelectionne = viewHolder;
                    }
                }
                ((DetailRetourDemandeActivity) context).supprimerRetourLigne(viewHolderSelectionne);
            }
        });

        GestionnaireEditText gestionnaireEditText = new GestionnaireEditText(viewHolder, RetourLigneCourant);


        final RetourLigneViewHolder finalViewHolder = viewHolder;


        viewHolder.qteRetournee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ouvre une boite de dialogue avec un NumberPicker
                String title = viewHolders.get(position).ReferenceProduit.getText().toString();
                String message = "Quantité à préparer : ";
                int maxValue = 1000000;
                int value = 0;
                if(!viewHolders.get(position).qteRetournee.getText().toString().contentEquals(""))
                    value = Integer.parseInt(viewHolders.get(position).qteRetournee.getText().toString());

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int qteAprès = aNumberPicker.getValue();
                        viewHolders.get(position).qteRetournee.setText(String.valueOf(qteAprès));
                        RetourLigneCourant.setQte_Retourner(qteAprès);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                };

                Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);

            }
        });


        viewHolder.qteRetournee.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(finalViewHolder.qteRetournee.getWindowToken(), 0);
                    finalViewHolder.qteRetournee.clearFocus();
                }

                return false;
            }
        });


        return convertView;
    }


    public class RetourLigneViewHolder {
        public TextView qteRetournee;
        public TextView StockTheorique;
        public TextView DesignationProduit;
        public TextView ReferenceProduit;
        public TextView NomFournisseur;
        public ImageView Supprimer;

    }


    private class GestionnaireEditText {

        public RetourLigneViewHolder viewHolder;
        public Retour_Ligne retour_ligne;


        TextWatcher textWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String valeur = viewHolder.qteRetournee.getText().toString().trim();
                if (valeur == null || valeur.equals("")) {
                    retour_ligne.setQte_Demander(0);
                } else {
                    retour_ligne.setQte_Demander(Integer.parseInt(String.valueOf(valeur)));
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        public GestionnaireEditText(RetourLigneViewHolder view, Retour_Ligne retour_ligne) {
            this.viewHolder = view;
            this.retour_ligne = retour_ligne;
            this.viewHolder.qteRetournee.addTextChangedListener(textWatcher);
        }
    }

}
