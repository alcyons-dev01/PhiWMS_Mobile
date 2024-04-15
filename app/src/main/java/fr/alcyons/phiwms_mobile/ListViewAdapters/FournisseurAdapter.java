package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 26/09/2017.
 */

public class FournisseurAdapter extends ArrayAdapter {

    public List<String> fournisseurs;
    Context context;

    public FournisseurAdapter(Context context, List<String> fournisseurs) {
        super(context, 0, fournisseurs);
        this.fournisseurs = fournisseurs;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_fournisseur, parent, false);
        }

        FournisseurViewHolder viewHolder = (FournisseurViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new FournisseurViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomFournisseur);
            convertView.setTag(viewHolder);
        }

        String fournisseurCourant = (String) getItem(position);

        viewHolder.nom.setText(fournisseurCourant);

        return convertView;
    }

    @Override
    public void clear() {
        fournisseurs.clear();
    }

    private class FournisseurViewHolder {
        public TextView nom;
    }
}