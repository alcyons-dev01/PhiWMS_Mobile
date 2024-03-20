package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import fr.alcyons.phimr4.Classes.PerimetreFonctionnel;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.R;

public class ListPerimetreAdapter extends ArrayAdapter {

    public List<PerimetreFonctionnel> perimetreFonctionnels;
    Context context;
    Utilisateur utilisateur;

    public ListPerimetreAdapter(Context context, List<PerimetreFonctionnel> perimetreFonctionnels, Utilisateur utilisateur) {
        super(context, 0, perimetreFonctionnels);
        this.perimetreFonctionnels = perimetreFonctionnels;
        this.context = context;
        this.utilisateur = utilisateur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_perimetre, parent, false);
        }

        PerimetreViewHolder viewHolder = (PerimetreViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new PerimetreViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomPerimetre);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconeService);
            convertView.setTag(viewHolder);
        }

        PerimetreFonctionnel perimetreCourant = (PerimetreFonctionnel) getItem(position);

        String nomPerimetre = perimetreCourant.getNom();

        viewHolder.nom.setText(perimetreCourant.getNom());

        switch (nomPerimetre) {
            case "Commun":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_build_black_24dp);
                break;
            case "Préférences":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_build_black_24dp);
                break;
            case "Pharmacien":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_local_pharmacy_black);
                break;
            case "Patient":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_personne_black);
                break;
            case "Magasinier":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_colis);
                break;
            case "Chauffeurs":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_local_shipping);
                break;
            case "Infirmiers":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_assignment);
                break;
            case "Préparateur":
                viewHolder.icon.setBackgroundResource(R.drawable.ic_inbox);
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public void clear() {
        perimetreFonctionnels.clear();
    }

    private class PerimetreViewHolder {
        public TextView nom;
        public ImageView icon;
    }
}
