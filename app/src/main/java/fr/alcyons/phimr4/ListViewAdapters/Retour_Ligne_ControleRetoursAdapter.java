package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 26/06/2017.
 */

public class Retour_Ligne_ControleRetoursAdapter extends ArrayAdapter {

    public List<Retour_Ligne_ControleRetour_Adapte> retour_Lignes;
    public List<Retour_LigneViewHolder> retourLigneViewHolderList;
    Context context;
    SQLiteDatabase db;

    public Retour_Ligne_ControleRetoursAdapter(Context context, List<Retour_Ligne_ControleRetour_Adapte> retour_Lignes, SQLiteDatabase db) {
        super(context, 0, retour_Lignes);
        this.retour_Lignes = retour_Lignes;
        this.context = context;
        this.db = db;
        retourLigneViewHolderList = new ArrayList<>();
        for (int i = 0; i < retour_Lignes.size(); i++) {
            Retour_LigneViewHolder viewHolder = new Retour_LigneViewHolder();
            retourLigneViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Retour_LigneViewHolder viewHolder = retourLigneViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_retour_ligne_controle_retours_new, parent, false);

        // Récupération des objets graphiques
        viewHolder.designation = (TextView) convertView.findViewById(R.id.designationProduit);
        viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
        viewHolder.fournisseur = (TextView) convertView.findViewById(R.id.nomFournisseur);
        viewHolder.qteDeclaree = (TextView) convertView.findViewById(R.id.qteDeclaree);
        viewHolder.nbLotsRetournes = (TextView) convertView.findViewById(R.id.nbLotsRetournes);
        //viewHolder.listLots = (LinearLayout) convertView.findViewById(R.id.listLots);
        Retour_Ligne_ControleRetour_Adapte retourLigneAdapteCourant = (Retour_Ligne_ControleRetour_Adapte) getItem(position);
        Retour_Ligne retourLigne = Retour_LigneOpenHelper.getRetourLigneByID(db, retourLigneAdapteCourant.getRetourLigneID());

        // Affichage des valeurs
        if(retourLigne != null)
        {
            viewHolder.designation.setText(retourLigne.getProduit_Designation());
            viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
            viewHolder.fournisseur.setText(retourLigne.getProduit_Fournisseur());
            viewHolder.qteDeclaree.setText(String.valueOf((int) retourLigne.getQte_Demander()));
            List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotAdaptesAvecValeur = new ArrayList<>();
            int qte_retourner = 0;
            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lotAdapte : retourLigneAdapteCourant.getLotAdaptes()) {
                if (lotAdapte.getQteSaisie() != 0) {
                    lotAdaptesAvecValeur.add(lotAdapte);
                    qte_retourner = lotAdapte.getQteSaisie()+qte_retourner;
                }
            }

            viewHolder.nbLotsRetournes.setText(String.valueOf(qte_retourner));
        }

        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designation;
        public TextView referenceProduit;
        public TextView fournisseur;
        public TextView qteDeclaree;
        public TextView nbLotsRetournes;
        public LinearLayout listLots;
    }

}
