package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Lot_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Commande;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Lot_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.R;

public class VerrouInterneAdapter extends ArrayAdapter implements Filterable {

    public List<PH_Preparation> ph_preparationList;
    public List<PH_Preparation> ph_preparationDeBaseList;
    Context context;
    SQLiteDatabase db;
    Utilisateur utilisateur;

    public VerrouInterneAdapter(Context context, SQLiteDatabase database, List<PH_Preparation> ph_preparationList, Utilisateur utilisateur) {
        super(context, 0, ph_preparationList);
        this.context = context;
        this.db = database;
        this.ph_preparationList = ph_preparationList;
        this.utilisateur = utilisateur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_verrou_pharmacie_interne, parent, false);
        }

        VerrouInterneViewHolder viewHolder = (VerrouInterneViewHolder) convertView.getTag();
        if (viewHolder == null) {
            // Récupération des objets graphiques
            viewHolder = new VerrouInterneViewHolder();
            viewHolder.numCommande = (TextView) convertView.findViewById(R.id.numCommande);
            viewHolder.depotNom = (TextView) convertView.findViewById(R.id.depotNom);
            viewHolder.dateLiv = (TextView) convertView.findViewById(R.id.livraisonPrevueDate);
            viewHolder.delai = (TextView) convertView.findViewById(R.id.delaiLivraison);
            viewHolder.fournisseurVerrou = (TextView) convertView.findViewById(R.id.fournisseurVerrou);
            viewHolder.numeroCommande = (TextView) convertView.findViewById(R.id.numeroCommande);
            viewHolder.layoutCommande = (LinearLayout) convertView.findViewById(R.id.layoutCommande);
            viewHolder.barreIndicateur = (ImageView) convertView.findViewById(R.id.barreIndicateur);
            convertView.setTag(viewHolder);
        }

        PH_Preparation phPreparation = (PH_Preparation) getItem(position);

        viewHolder.layoutCommande.setVisibility(View.GONE);

        if (phPreparation != null) {
            List<PH_Preparation_Ligne> ph_preparation_ligneList = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparation);
            int nb_ligne_verrouiller = 0;
            int nbLot = 0;
            for(PH_Preparation_Ligne courant : ph_preparation_ligneList)
            {
                List<PH_Lot_Ligne> ph_lot_ligneList = PH_Lot_LigneOpenHelper.getListePH_Lot_LigneByPreparationLigne(db, courant.get_UID());

                for(PH_Lot_Ligne lot_ligne : ph_lot_ligneList)
                {
                    nbLot ++;
                    if(lot_ligne.isVerrouiller())
                    {
                        nb_ligne_verrouiller ++;
                    }
                }
            }

            if(nb_ligne_verrouiller == 0)
            {
                viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
            }
            else if(nb_ligne_verrouiller == nbLot)
            {
                viewHolder.barreIndicateur.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));

            }
            else
            {
                viewHolder.barreIndicateur.setBackgroundColor(context.getResources().getColor(R.color.orange2, null));
            }


            Commande commandeCourante = CommandeOpenHelper.getCommandeByID(db, phPreparation.getCommande_ID());
            if(commandeCourante != null)
            {
                viewHolder.fournisseurVerrou.setText(commandeCourante.getFournisseur());
            }
            else
            {
                String[] tab_liste = phPreparation.getListe().split(" ");
                String numeroCommande = tab_liste[tab_liste.length-1];
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
                if(phPreparation.getLivraisonPrevueDate().contentEquals("0000-00-00"))
                {
                    dateLiv = null;
                }
                else
                {
                    dateLiv = dateDecodeur.parse(phPreparation.getLivraisonPrevueDate().substring(0, 10));
                }
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
                viewHolder.delai.setText(libelle + String.valueOf(Math.abs(delaiLivraison)));
                viewHolder.dateLiv.setText(dateFormat.format(dateLiv));
            }
            else
            {
                viewHolder.delai.setTextColor(context.getResources().getColor(R.color.vert, null));
                viewHolder.dateLiv.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                viewHolder.delai.setText("");
                viewHolder.dateLiv.setText("00/00/0000");
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

    private class VerrouInterneViewHolder {
        public TextView numCommande;
        public TextView depotNom;
        public TextView dateLiv;
        public TextView delai;
        public TextView fournisseurVerrou;
        public TextView numeroCommande;
        public ImageView barreIndicateur;
        LinearLayout layoutCommande;
    }
}
