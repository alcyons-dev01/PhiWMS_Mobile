package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PH_PreparationAdapter extends ArrayAdapter implements Filterable {

    public List<PH_Preparation> ph_preparationList;
    public List<PH_Preparation> ph_preparationDeBaseList;
    Context context;
    SQLiteDatabase db;
    Utilisateur utilisateur;

    public PH_PreparationAdapter(Context context, SQLiteDatabase database, List<PH_Preparation> ph_preparationList, Utilisateur utilisateur) {
        super(context, 0, ph_preparationList);
        this.context = context;
        this.db = database;
        this.ph_preparationList = ph_preparationList;
        this.utilisateur = utilisateur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_preparation, parent, false);
        }

        PH_PreparationViewHolder viewHolder = (PH_PreparationViewHolder) convertView.getTag();
        if (viewHolder == null) {
            // Récupération des objets graphiques
            viewHolder = new PH_PreparationViewHolder();
            viewHolder.numCommande = (TextView) convertView.findViewById(R.id.numCommande);
            viewHolder.depotNom = (TextView) convertView.findViewById(R.id.depotNom);
            viewHolder.dateLiv = (TextView) convertView.findViewById(R.id.livraisonPrevueDate);
            viewHolder.delai = (TextView) convertView.findViewById(R.id.delaiLivraison);
            viewHolder.fournisseurVerrou = (TextView) convertView.findViewById(R.id.fournisseurVerrou);
            viewHolder.numeroCommande = (TextView) convertView.findViewById(R.id.numeroCommande);
            viewHolder.layoutCommande = (LinearLayout) convertView.findViewById(R.id.layoutCommande);
            convertView.setTag(viewHolder);
        }

        PH_Preparation phPreparation = (PH_Preparation) getItem(position);

        if (phPreparation != null) {
            Commande commandeCourante = CommandeOpenHelper.getCommandeByID(db, phPreparation.getCommande_ID());
            if(commandeCourante != null)
            {
                viewHolder.numeroCommande.setText("#"+commandeCourante.getNumero());
                viewHolder.fournisseurVerrou.setText(commandeCourante.getFournisseur());
            }
            else
            {
                String[] tab_liste = phPreparation.getListe().split(" ");
                String numeroCommande = tab_liste[tab_liste.length-1];
                viewHolder.numeroCommande.setText("#"+numeroCommande);
                viewHolder.fournisseurVerrou.setText("...");
            }

            viewHolder.numCommande.setText(String.valueOf(phPreparation.getUID()));
            Depot depot = DepotOpenHelper.getDepotParID(db, phPreparation.getDepotDestinataireID());

            viewHolder.depotNom.setText(depot.getNom());

            // Gestion des dates
            Date dateLiv = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateDuJour = new Date();
            try {
                dateLiv = dateDecodeur.parse(phPreparation.getLivraisonPrevueDate().substring(0, 10));
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
                String libelle = "J-";
                if (delaiLivraison >= delaiExpire) {
                    viewHolder.delai.setTextColor(context.getResources().getColor(R.color.grey_color_fonce, null));
                    viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.grey_color_fonce, null));
                    libelle = "J+";
                } else if (delaiLivraison >= delaiDemain) {
                    viewHolder.delai.setTextColor(context.getResources().getColor(R.color.rouge2, null));
                    viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delaiLivraison >= delaiApresDemain) {
                    viewHolder.delai.setTextColor(context.getResources().getColor(R.color.orange2, null));
                    viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    viewHolder.delai.setTextColor(context.getResources().getColor(R.color.vert, null));
                    viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                }
                viewHolder.delai.setText(libelle + String.valueOf(delaiLivraison));
                viewHolder.dateLiv.setText(dateFormat.format(dateLiv));
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<PH_Preparation> tempPhPreparationList = new ArrayList<>();
                ph_preparationDeBaseList = PH_PreparationOpenHelper.getAllPHPreparationVerrouPharmacie(db);

                if (constraint != null && ph_preparationDeBaseList != null) {
                    for (PH_Preparation ph_preparation : ph_preparationDeBaseList) {
                        if (constraint.length() == 0) {
                            tempPhPreparationList.add(ph_preparation);
                        } else {
                            Depot depot = DepotOpenHelper.getDepotParID(db, ph_preparation.getDepotDestinataireID());
                            if (depot.getNom().toLowerCase().contains(constraint)) {
                                tempPhPreparationList.add(ph_preparation);

                            } else if (String.valueOf(ph_preparation.getCommande_ID()).contains(constraint)) {
                                tempPhPreparationList.add(ph_preparation);
                            }
                        }
                    }
                    filterResults.values = tempPhPreparationList;
                    filterResults.count = tempPhPreparationList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ph_preparationList.clear();
                ph_preparationList.addAll((List<PH_Preparation>) results.values);
                notifyDataSetChanged();
            }

        };
    }

    @Override
    public void clear() {
        ph_preparationList.clear();
    }

    private class PH_PreparationViewHolder {
        public TextView numCommande;
        public TextView depotNom;
        public TextView dateLiv;
        public TextView delai;
        public TextView fournisseurVerrou;
        public TextView numeroCommande;
        LinearLayout layoutCommande;
    }
}
