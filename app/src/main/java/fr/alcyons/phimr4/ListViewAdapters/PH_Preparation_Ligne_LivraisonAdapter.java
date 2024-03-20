package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.MenuActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.R;

import static fr.alcyons.phimr4.Outils.Alerte.aNumberPicker;

/**
 * Created by quentinlanusse on 16/08/2017.
 */

public class PH_Preparation_Ligne_LivraisonAdapter extends ArrayAdapter {

    public List<PH_Preparation_Ligne> ph_preparation_lignes;
    Context context;

    public PH_Preparation_Ligne_LivraisonAdapter(Context context, List<PH_Preparation_Ligne> ph_preparation_lignes) {
        super(context, 0, ph_preparation_lignes);
        this.ph_preparation_lignes = ph_preparation_lignes;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_preparation_ligne_livraison, parent, false);
        }

        PreparationLigneViewHolder viewHolder = (PreparationLigneViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new PreparationLigneViewHolder();

            viewHolder.designationProduit = (TextView) convertView.findViewById(R.id.designationProduit);
            viewHolder.referenceProduit = (TextView) convertView.findViewById(R.id.referenceProduit);
            viewHolder.nbColis = (TextView) convertView.findViewById(R.id.nbColis);
            viewHolder.qte_livree = (TextView) convertView.findViewById(R.id.qte_livree);
            viewHolder.Accepter = (ImageView) convertView.findViewById(R.id.Accepter);
            viewHolder.Refuser = (ImageView) convertView.findViewById(R.id.Refuser);
            viewHolder.layout_qteLivrer = (LinearLayout) convertView.findViewById(R.id.layout_qteLivrer);
            viewHolder.layoutValidation = (LinearLayout) convertView.findViewById(R.id.layoutValidation);

            convertView.setTag(viewHolder);
        }

        final PH_Preparation_Ligne ph_preparation_ligneCourante = (PH_Preparation_Ligne) getItem(position);

        int nbColisVE = 0;
        int conditionnementAchat = 0;
        int qte = (int) ph_preparation_ligneCourante.getQte_livrer();

        Produit produit = ProduitOpenHelper.getProduitByID(((MenuActivity) context).db, ph_preparation_ligneCourante.getProduitID());

        if (produit != null) {
            conditionnementAchat = produit.getCond_achat();
            int conditionnementSet = conditionnementAchat;
            if (qte != 0 && conditionnementSet != 0) {
                nbColisVE = (int) qte / conditionnementSet;
                if (qte % conditionnementSet != 0) {
                    nbColisVE++;
                }
            }
            if (qte != 0) {
                if (nbColisVE == 0) {
                    nbColisVE = 1;
                }
            }
        }

        viewHolder.designationProduit.setText(ph_preparation_ligneCourante.getProduitDesignation());
        viewHolder.referenceProduit.setText(ph_preparation_ligneCourante.getProduitReference());
        viewHolder.nbColis.setText(String.valueOf(nbColisVE));
        viewHolder.qte_livree.setText(String.valueOf(ph_preparation_ligneCourante.getQte_livrer()));

        int qteLivrer = ph_preparation_ligneCourante.getQte_livrer();
        int qteAPreparer = ph_preparation_ligneCourante.getQte_APreparer();

        if (qteLivrer == qteAPreparer) {
            viewHolder.Accepter.setVisibility(View.VISIBLE);
            viewHolder.Refuser.setVisibility(View.GONE);
            viewHolder.layoutValidation.setBackground(context.getDrawable(R.drawable.background_livraison_vert));
        } else if(ph_preparation_ligneCourante.getQte_livrer()== 0) {
            viewHolder.Accepter.setVisibility(View.GONE);
            viewHolder.Refuser.setVisibility(View.VISIBLE);
            viewHolder.layoutValidation.setBackground(context.getDrawable(R.drawable.background_livraison_rouge));
        }
        else
        {
            viewHolder.Accepter.setVisibility(View.VISIBLE);
            viewHolder.Refuser.setVisibility(View.GONE);
            viewHolder.layoutValidation.setBackground(context.getDrawable(R.drawable.background_livraison_orange));
        }

        final PreparationLigneViewHolder finalViewHolder = viewHolder;
        final View finalConvertView = convertView;
        viewHolder.layout_qteLivrer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                afficherNumberPicker(finalConvertView, finalViewHolder, ph_preparation_ligneCourante);
                return false;
            }
        });

        return convertView;
    }

    private class PreparationLigneViewHolder {
        public TextView designationProduit;
        public TextView referenceProduit;
        public TextView nbColis;
        public TextView qte_livree;
        public ImageView Accepter;
        public ImageView Refuser;
        public LinearLayout layout_qteLivrer;
        public LinearLayout layoutValidation;
    }

    public void afficherNumberPicker(final View view, final PreparationLigneViewHolder viewHolder, final PH_Preparation_Ligne courant)
    {
        // Ouvre une boite de dialogue avec un NumberPicker
        String title = courant.getProduitDesignation();
        String message = "Quantité livrée : ";
        int maxValue = courant.getQte_APreparer();
        int value = courant.getQte_livrer();


        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int qteAprès = aNumberPicker.getValue();
                courant.setQte_livrer(qteAprès);
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(((MenuActivity) context).db, courant);

                notifyDataSetChanged();
                dialog.dismiss();
            }
        };

        Alerte.afficherAlerteNumberPicker(context, title, message, value, maxValue, onClickListener);
    }
}
