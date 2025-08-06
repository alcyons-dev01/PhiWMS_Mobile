package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.R;

public class ReceptionAdapter extends ArrayAdapter implements Filterable {

    public List<Commande> commandeList;
    public List<Commande> commandeDeBaseList;
    Context context;
    SQLiteDatabase db;
    Commande commandeCourant;

    ReceptionFilter filter;

    public ReceptionAdapter(Context context, SQLiteDatabase db, List<Commande> commandeList) {
        super(context, 0, commandeList);
        this.context = context;
        this.db = db;

        this.commandeList = commandeList;
        this.commandeDeBaseList = new ArrayList<>();
        commandeDeBaseList.addAll(commandeList);

        this.filter = null;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new ReceptionFilter();

        return filter;
    }

    @NonNull
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_reception_liste, parent, false);
        }

        // Récupération des objets graphiques
        ProduitViewHolder viewHolder =(ProduitViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new ProduitViewHolder();
            viewHolder.livraisonPrevueDate = (TextView) convertView.findViewById(R.id.livraisonPrevueDate);
            viewHolder.fabricant = (TextView) convertView.findViewById(R.id.fabricant);
            viewHolder.numCommande = (TextView) convertView.findViewById(R.id.numCommande);
            viewHolder.nbColisTotal = (TextView) convertView.findViewById(R.id.nbColisTotal);
            viewHolder.nbColisAttente = (TextView) convertView.findViewById(R.id.nbColisAttente);
            viewHolder.poidsTotal = (TextView) convertView.findViewById(R.id.poidsTotal);
            viewHolder.zone_info_reception = (LinearLayout) convertView.findViewById(R.id.zone_info_reception);
            viewHolder.ZoneNbColisEnAttente = (LinearLayout) convertView.findViewById(R.id.ZoneNbColisEnAttente);
            viewHolder.relative_principal = (RelativeLayout) convertView.findViewById(R.id.relative_principal);
            convertView.setTag(viewHolder);
        }


        commandeCourant = (Commande) getItem(position);

        assert commandeCourant != null;
        viewHolder.fabricant.setText(commandeCourant.getFournisseur().trim());
        viewHolder.numCommande.setText("#"+commandeCourant.getNumero());

        if (commandeCourant.getSituation().equals("L")) {
            viewHolder.zone_info_reception.setBackgroundColor(ContextCompat.getColor(context, R.color.orange2));
            viewHolder.ZoneNbColisEnAttente.setBackgroundColor(ContextCompat.getColor(context, R.color.orange2));
            viewHolder.relative_principal.setBackground(ContextCompat.getDrawable(context, R.drawable.background_preparation_orange));
        } else {
            viewHolder.zone_info_reception.setBackgroundColor(ContextCompat.getColor(context, R.color.bleu_clair_alcyons));
            viewHolder.ZoneNbColisEnAttente.setBackgroundColor(ContextCompat.getColor(context, R.color.orange2));
            viewHolder.relative_principal.setBackground(ContextCompat.getDrawable(context, R.drawable.background_preparation_bleu));
        }

        List<PH_Reliquat> phReliquatList = PH_ReliquatOpenHelper.getPH_ReliquatBaseByCommandeNumero(db, commandeCourant.getNumero());
        int nbColis = 0;

        String situation = getSituation(commandeCourant.getSituation());

        if(situation.contentEquals("En attente"))
        {
            nbColis = commandeCourant.getNbColisTotal_CE();
        }
        else
        {
            for (PH_Reliquat phReliquat : phReliquatList) {
                nbColis += viewHolder.getNombreColis(phReliquat.getProduitID(), phReliquat.getQteReliquat_X(), "Achats");
            }
        }
        viewHolder.nbColisAttente.setText(String.valueOf(nbColis));

        //affichage du nombre de colis et du poids
        viewHolder.nbColisTotal.setText(String.valueOf(commandeCourant.getNbColisTotal_CE()));
        viewHolder.poidsTotal.setText(String.valueOf(commandeCourant.getPoidsTotal_CE()));

        // Gestion des dates
        Date dateLiv = null;
        @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDuJour = new Date();
        try {
            dateLiv = dateDecodeur.parse(commandeCourant.getDate_Liv().substring(0, 10));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dateLiv != null) {
            // Calcul du délai de livraison
            long diff = dateDuJour.getTime() - dateLiv.getTime();
            int delaiLivraison = (int) (diff / (1000 * 60 * 60 * 24));
            int delaiExpire = 0;
            int delaiDemain = -1;
            int delaiApresDemain = -2;
            if (delaiLivraison >= delaiExpire) {
                viewHolder.livraisonPrevueDate.setTextColor(ContextCompat.getColor(context, R.color.rouge2));
            } else if (delaiLivraison == delaiDemain) {
                viewHolder.livraisonPrevueDate.setTextColor(ContextCompat.getColor(context, R.color.rouge2));
            } else if (delaiLivraison == delaiApresDemain) {
                viewHolder.livraisonPrevueDate.setTextColor(ContextCompat.getColor(context, R.color.orange2));
            } else {
                viewHolder.livraisonPrevueDate.setTextColor(ContextCompat.getColor(context, R.color.vert));
            }
            viewHolder.livraisonPrevueDate.setText(dateFormat.format(dateLiv));
        }

        return convertView;
    }

    private class ReceptionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            commandeList.clear();
            commandeList.addAll(commandeDeBaseList);

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();


            if (!constraint.toString().isEmpty()) {
                List<Commande> founded = new ArrayList<>();

                for (Commande commande : commandeList) {

                    // Vérifie le début du premier mot
                    String commandeProduitDesignation = commande.getChaineFiltreProduit(db, commande.getNumero()).toLowerCase();

                    if (commandeProduitDesignation.toLowerCase().contains(String.valueOf(constraint))) {
                        founded.add(commande);
                    } else if (commande.getNumero().toLowerCase().contains(constraint)) {
                        founded.add(commande);
                    } else if (commande.getFournisseur().toLowerCase().contains(constraint)) {
                        founded.add(commande);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = commandeProduitDesignation.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                founded.add(commande);
                                break;
                            }
                        }
                    }
                }

                result.values = founded;
                result.count = founded.size();
            } else {
                result.values = commandeDeBaseList;
                result.count = commandeDeBaseList.size();
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null && results.values != null)
            {
                commandeList.clear();
                commandeList.addAll((Collection<? extends Commande>) results.values);
                notifyDataSetChanged();
            }
        }

    }


    private class ProduitViewHolder {
        public TextView livraisonPrevueDate;
        public TextView fabricant;
        public TextView numCommande;
        public RelativeLayout relative_principal;
        public TextView nbColisAttente;
        public LinearLayout zone_info_reception;
        public LinearLayout ZoneNbColisEnAttente;
        public TextView nbColisTotal;
        public TextView poidsTotal;

        public int getNombreColis(int produit_id, int qte, String context) {
            Produit produit = null;
            int nbColis_VE = 0;
            int conditionnementSet;
            int conditionnementAchat = 0;
            int conditionnementAchatVolume = 0;
            int conditionnementDistribution = 0;

            if (produit_id != 0) {
                produit = ProduitOpenHelper.getProduitByID(db, produit_id);
            }

            if (produit != null) {
                conditionnementAchat = produit.getCond_achat();
                conditionnementAchatVolume = (int) produit.getCond_Achat_Gros_volume();
                conditionnementDistribution = (int) produit.getCond_distrib();
            }

            if(context.contentEquals("Achats"))
                conditionnementSet = conditionnementAchat;
            else if(context.contentEquals("Achats Palette") ||context.contentEquals("Distribution Palette"))
                conditionnementSet = conditionnementAchatVolume;
            else
                conditionnementSet = conditionnementDistribution;

            if (qte != 0 && conditionnementSet != 0) {
                nbColis_VE = qte / conditionnementSet;
                if ((qte % conditionnementSet) != 0) {
                    nbColis_VE++;
                }
            }
            if (qte != 0) {
                if (nbColis_VE == 0) {
                    nbColis_VE = 1;
                }
            }
            return nbColis_VE;
        }
    }

    private String getSituation(String situation)
    {
        String situationRetourner = "En attente";

        switch (situation)
        {
            case "A":
                situationRetourner = "À avaliser";
                break;

            case "E":
                situationRetourner = "En attente";
                break;

            case "L":
                situationRetourner = "En reliquat";
                break;

            case "R":
                situationRetourner = "Refusée";
                break;

            case "S":
                situationRetourner = "Livrée";
                break;

            case "F":
                situationRetourner = "Facturée";
                break;

            case "T":
                situationRetourner = "Comptabilisée";
                break;

            case "X":
                situationRetourner = "Annulée";
                break;
        }

        return situationRetourner;
    }

}
