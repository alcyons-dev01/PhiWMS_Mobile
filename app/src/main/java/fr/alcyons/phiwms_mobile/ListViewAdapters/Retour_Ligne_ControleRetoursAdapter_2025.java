package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.R;

public class Retour_Ligne_ControleRetoursAdapter_2025 extends ArrayAdapter {

    public List<Retour_Ligne> retour_Lignes;
    public List<Retour_LigneViewHolder> retourLigneViewHolderList;
    Context context;
    SQLiteDatabase db;
    Retour retourCourant;

    public Retour_Ligne_ControleRetoursAdapter_2025(Context context, List<Retour_Ligne> retour_Lignes, SQLiteDatabase db, Retour retouCourant) {
        super(context, 0, retour_Lignes);
        this.retour_Lignes = retour_Lignes;
        this.context = context;
        this.db = db;
        retourCourant = retouCourant;
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
        viewHolder.linear_principal = (LinearLayout) convertView.findViewById(R.id.linear_principal);
        viewHolder.relativeparent = (RelativeLayout) convertView.findViewById(R.id.relativeparent);
        Retour_Ligne retourLigne = (Retour_Ligne) getItem(position);

        // Affichage des valeurs
        if(retourLigne != null)
        {
            viewHolder.designation.setText(retourLigne.getProduit_Designation());
            viewHolder.referenceProduit.setText(retourLigne.getProduit_Reference());
            viewHolder.fournisseur.setText(retourLigne.getProduit_Fournisseur());
            viewHolder.qteDeclaree.setText(String.valueOf((int) retourLigne.getQte_Demander()));
            int qte_retourner = 0;

            List<Retour_Ligne> retourLigneRetourner = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourCourant, retourLigne.getCode_produit());
            for(Retour_Ligne temp : retourLigneRetourner)
            {
                qte_retourner = qte_retourner+(int) temp.getQte_Retourner();
            }

            viewHolder.nbLotsRetournes.setText(String.valueOf(qte_retourner));

            if(qte_retourner == retourLigne.getQte_Demander())
            {
                viewHolder.relativeparent.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_vert));
                viewHolder.nbLotsRetournes.setVisibility(View.GONE);
                viewHolder.qteDeclaree.setTextColor(context.getResources().getColor(R.color.vert));
            }
            else
            {
                viewHolder.relativeparent.setBackground(context.getResources().getDrawable(R.drawable.background_cadre_bleu));
                viewHolder.nbLotsRetournes.setVisibility(View.VISIBLE);
                viewHolder.qteDeclaree.setTextColor(context.getResources().getColor(R.color.noir));
            }
        }

        return convertView;
    }

    public class Retour_LigneViewHolder {
        public TextView designation;
        public TextView referenceProduit;
        public TextView fournisseur;
        public TextView qteDeclaree;
        public TextView nbLotsRetournes;
        public LinearLayout linear_principal;
        public LinearLayout listLots;
        public RelativeLayout relativeparent;
    }
}