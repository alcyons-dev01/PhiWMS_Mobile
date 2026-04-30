package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PH_Preparation_PreparationAdapter extends ArrayAdapter implements Filterable {

    public List<PH_Preparation> phPreparationPreparation;
    public List<PH_Preparation> phPreparationPreparationOriginal;

    Context context;
    Utilisateur utilisateur;
    SQLiteDatabase db;
    PH_Preparation_PreparationFilter filter;

    public PH_Preparation_PreparationAdapter(Context context, List<PH_Preparation> listeInitiale, SQLiteDatabase database, Utilisateur utilisateur) {
        super(context, 0, listeInitiale);
        this.phPreparationPreparation = listeInitiale;
        this.context = context;
        this.utilisateur = utilisateur;
        db = database;
        this.phPreparationPreparationOriginal = new ArrayList<>();
        this.phPreparationPreparationOriginal.addAll(phPreparationPreparation);
        this.filter = null;
    }

    @Override
    public PH_Preparation_PreparationFilter getFilter() {
        if (filter == null)
            filter = new PH_Preparation_PreparationFilter();
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_ph_preparation_liste, parent, false);
        }

        PH_PreparationPreparationViewHolder viewHolder = (PH_PreparationPreparationViewHolder) convertView.getTag();
        if (viewHolder == null) {
            // Récupération des objets graphiques
            viewHolder = new PH_PreparationPreparationViewHolder();

            viewHolder.zoneDateLivraisonPrevue = (LinearLayout) convertView.findViewById(R.id.zoneDateLivraisonPrevue);
            viewHolder.livraisonPrevueDate = (TextView) convertView.findViewById(R.id.livraisonPrevueDate);
            viewHolder.depotNom = (TextView) convertView.findViewById(R.id.depotNom);
            viewHolder.uidPHPreparation = (TextView) convertView.findViewById(R.id.uidPHPreparation);
            viewHolder.nbAPreparer = (TextView) convertView.findViewById(R.id.nbAPreparer);
            viewHolder.origine = (TextView) convertView.findViewById(R.id.origine);
            viewHolder.preparationUrgente = (TextView) convertView.findViewById(R.id.preparationUrgente);
            viewHolder.cartePreparation = (LinearLayout) convertView.findViewById(R.id.cartePreparation);

            convertView.setTag(viewHolder);
        }

        PH_Preparation phPreparationCourant = (PH_Preparation) getItem(position);
        Depot depot = DepotOpenHelper.getDepotParReference(db, phPreparationCourant.getDepotDestinataireReference());

        if(phPreparationCourant.isURGENT())
        {
            viewHolder.preparationUrgente.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.preparationUrgente.setVisibility(View.GONE);
        }

        List<PH_Preparation_Ligne> ph_preparation_ligne_List = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesBaseParPHPreparation(db, phPreparationCourant);
        int nbApreparer = 0;
        for (PH_Preparation_Ligne phPreparationLigne : ph_preparation_ligne_List) {
            if (phPreparationLigne.getQte_Demander() > 0 && phPreparationLigne.getQte_APreparer() >0) {
                nbApreparer++;
            }
        }

        if(nbApreparer > 0)
        {
            viewHolder.nbAPreparer.setText(String.valueOf(nbApreparer));

            String textDepot = "";

            if(depot != null)
            {
                if(utilisateur.getIdentifiant().toLowerCase().contentEquals("alcyons") && depot.getStructure().contentEquals("PAD"))
                {
                    textDepot = "Patient - "+depot.getPAD_IPP();
                }
                else
                {
                    textDepot = depot.getNom();
                }
            }
            else
            {
                textDepot = phPreparationCourant.getDepotDestinataireReference();
            }

            viewHolder.depotNom.setText(textDepot);
            viewHolder.uidPHPreparation.setText("#"+String.valueOf(phPreparationCourant.getUID()));
            viewHolder.origine.setText(phPreparationCourant.getListe());

            if(phPreparationCourant.getStatut().contentEquals("En Reliquat"))
            {
                viewHolder.cartePreparation.setBackgroundColor(Color.parseColor("#FFE0D0"));
            }
            else
            {
                viewHolder.cartePreparation.setBackgroundColor(Color.parseColor("#F0F4FF"));
            }

            // Gestion des dates
            Date dateLiv = null;
            DateFormat dateDecodeur = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                String livraisonPrevueLe = phPreparationCourant.getLivraisonPrevueDate();

                if (!livraisonPrevueLe.contentEquals("0000-00-00")) {
                    dateLiv = dateDecodeur.parse(livraisonPrevueLe.substring(0, 10));
                    viewHolder.livraisonPrevueDate.setText(dateFormat.format(dateLiv));
                } else {
                    viewHolder.livraisonPrevueDate.setText("00/00/0000");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.setDateLivraisonColor(dateLiv);
        }


        return convertView;
    }

    @Override
    public void clear() {
        phPreparationPreparation.clear();
    }

    private class PH_Preparation_PreparationFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            phPreparationPreparation.clear();
            for (PH_Preparation retourCourant : phPreparationPreparationOriginal
                    ) {
                phPreparationPreparation.add(retourCourant);
            }
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                List<PH_Preparation> founded = new ArrayList<>();
                for (PH_Preparation item : phPreparationPreparation) {
                    Depot depot = DepotOpenHelper.getDepotParReference(db, item.getDepotDestinataireReference());

                    String valueText = item.getChaineFiltrePhPreparationLigne(db).toLowerCase();
                    if(String.valueOf(item.getUID()).contentEquals(String.valueOf(constraint))){
                        founded.add(item);
                    }
                    else if(valueText.startsWith(String.valueOf(constraint))) {
                        founded.add(item);
                    } else if (depot!= null && depot.getNom().toLowerCase().contains(constraint)) {
                        founded.add(item);
                    } else {
                        /* Vérifie le début de chaque mot */
                        final String[] words = valueText.split(" ");
                        for (String word : words) {
                            if (word.startsWith(String.valueOf(constraint))) {
                                founded.add(item);
                                break;
                            }
                        }
                    }
                }
                result.values = founded;
                result.count = founded.size();
            } else {
                result.values = phPreparationPreparationOriginal;
                result.count = phPreparationPreparationOriginal.size();
            }
            return result;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results != null && results.values != null) {
                phPreparationPreparation.clear();
                for (PH_Preparation item : (List<PH_Preparation>) results.values) {
                    add(item);
                }

                notifyDataSetChanged();
            }
        }

    }

    private class PH_PreparationPreparationViewHolder {
        public LinearLayout zoneDateLivraisonPrevue;
        public TextView livraisonPrevueDate;
        public TextView depotNom;
        public TextView uidPHPreparation;
        public TextView nbAPreparer;
        public TextView origine;
        public TextView preparationUrgente;
        public LinearLayout cartePreparation;

        public void setDateLivraisonColor(Date date) {

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int demain = -1;

                if (delai > 0) {
                    zoneDateLivraisonPrevue.setBackgroundColor(context.getResources().getColor(R.color.gris, null));
                } else if (delai == 0) {
                    zoneDateLivraisonPrevue.setBackgroundColor(context.getResources().getColor(R.color.rouge, null));
                } else if (delai == demain) {
                    zoneDateLivraisonPrevue.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
                } else {
                    zoneDateLivraisonPrevue.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                zoneDateLivraisonPrevue.setBackgroundColor(context.getResources().getColor(R.color.noir, null));
            }

        }
    }
}
