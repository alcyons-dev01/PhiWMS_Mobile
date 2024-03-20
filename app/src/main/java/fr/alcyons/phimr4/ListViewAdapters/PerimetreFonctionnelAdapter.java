package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.PerimetreFonctionnelOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.Classes.PerimetreFonctionnel;
import fr.alcyons.phimr4.R;

/**
 * Created by quentinlanusse on 14/04/2017.
 */

public class PerimetreFonctionnelAdapter extends ArrayAdapter<PerimetreFonctionnel> {

    public List<PerimetreFonctionnel> perimetreFonctionnelsDeBase;
    public List<PerimetreFonctionnel> ListePerimetre = new ArrayList<>();
    PerimetreFonctionnelAdapter.PerimetreFonctionnelFilter filter;
    List<PerimetreFonctionnel> perimetreFonctionnels;
    SQLiteDatabase db;
    Context context;


    public PerimetreFonctionnelAdapter(Context context, List<PerimetreFonctionnel> listePerimetresFonctionnels, SQLiteDatabase database) {
        super(context, 0, listePerimetresFonctionnels);
        perimetreFonctionnels = listePerimetresFonctionnels;
        this.context = context;

        // Permet de garder une version de la liste complète des éléments
        perimetreFonctionnelsDeBase = new ArrayList<>();
        db = database;

        // La boucle foreach permet d'insérer les valeurs d'un objet dans l'autre, "perimetreFonctionnelsDeBase = perimetreFonctionnels" aurait "fusionné" les deux objets et modifier l'un des deux aurait modifié l'autre
        for (PerimetreFonctionnel perimetreFonctionnelCourant : listePerimetresFonctionnels
                ) {
            perimetreFonctionnelsDeBase.add(perimetreFonctionnelCourant);
        }
        ListePerimetre = PerimetreFonctionnelOpenHelper.getAllPerimetreFonctionnel(db);
        this.filter = null;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new PerimetreFonctionnelAdapter.PerimetreFonctionnelFilter();

        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_perimetre_fonctionnel, parent, false);
        }

        PerimetreFonctionnelViewHolder viewHolder = (PerimetreFonctionnelViewHolder) convertView.getTag();

        if (viewHolder == null) {
            viewHolder = new PerimetreFonctionnelViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomPerimetreFonctionnel);
            viewHolder.iconeService = (ImageView) convertView.findViewById(R.id.iconeService);
            convertView.setTag(viewHolder);
        }

        PerimetreFonctionnel perimetreFonctionnelCourant = getItem(position);
        viewHolder.nom.setText(perimetreFonctionnelCourant.getNom());

        String nomPerimetre = perimetreFonctionnelCourant.getNom();

        switch (nomPerimetre) {
            case "Commun":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_build_black_24dp);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Pharmacien":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_local_pharmacy_black);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Patient":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_personne_black);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Magasinier":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_colis);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Chauffeurs":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_local_shipping);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Infirmiers":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_assignment);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;

            case "Préparateur":
                viewHolder.iconeService.setBackgroundResource(R.drawable.ic_inbox);
                ViewCompat.setBackgroundTintList(viewHolder.iconeService, ColorStateList.valueOf(context.getResources().getColor(R.color.grey_color_fonce, null)));
                break;
            default:
                break;
        }
        return convertView;
    }

    private class PerimetreFonctionnelFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            perimetreFonctionnels.clear();
            for (PerimetreFonctionnel perimetreFonctionnelCourant : perimetreFonctionnelsDeBase
                    ) {
                perimetreFonctionnels.add(perimetreFonctionnelCourant);
            }
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                List<PerimetreFonctionnel> founded = new ArrayList<PerimetreFonctionnel>();
                for (PerimetreFonctionnel item : perimetreFonctionnels) {

                    // Vérifie le début du premier mot

                    // Valeur à modifier si on souhaite filtrer sur une autre valeur que le toString() de l'item
                    String valueText = item.getFiltre(ServiceOpenHelper.getServiceParPerimetreFonctionnel(db, item)).toLowerCase();

                    if (valueText.startsWith(String.valueOf(constraint))) {
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
                // Si le texte à filtrer (constraint) est vide, on remet les emplacements de base
                result.values = ListePerimetre;
                result.count = ListePerimetre.size();
            }
            return result;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            perimetreFonctionnels.clear();
            for (PerimetreFonctionnel item : (List<PerimetreFonctionnel>) results.values) {
                add(item);
            }
            notifyDataSetChanged();
        }
    }

    private class PerimetreFonctionnelViewHolder {
        public TextView nom;
        public ImageView iconeService;
    }
}
