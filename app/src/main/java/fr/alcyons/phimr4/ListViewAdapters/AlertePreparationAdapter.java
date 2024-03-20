package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.alcyons.phimr4.R;

public class AlertePreparationAdapter extends ArrayAdapter {

    public List<String> listenom;
    Context context;

    public AlertePreparationAdapter(Context context, List<String> listenom) {
        super(context, 0, listenom);
        this.listenom = listenom;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_alerte_preparation, parent, false);
        }

        AlertePreparationViewHolder viewHolder = (AlertePreparationViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new AlertePreparationViewHolder();
            viewHolder.nomProduit = (TextView) convertView.findViewById(R.id.nomProduit);
            convertView.setTag(viewHolder);
        }

        String nomCourant = (String)getItem(position);

        viewHolder.nomProduit.setText(nomCourant);

        return convertView;
    }

    @Override
    public void clear() {
        listenom.clear();
    }

    private class AlertePreparationViewHolder {
        public TextView nomProduit;
    }
}
