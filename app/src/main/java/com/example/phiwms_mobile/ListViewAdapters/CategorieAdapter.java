package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 23/11/2017.
 */

public class CategorieAdapter extends ArrayAdapter {

    public List<String> categories;
    Context context;

    public CategorieAdapter(Context context, List<String> categories) {
        super(context, 0, categories);
        this.categories = categories;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_categorie, parent, false);
        }

        CategorieViewHolder viewHolder = (CategorieViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new CategorieViewHolder();
            viewHolder.nomCategorie = (TextView) convertView.findViewById(R.id.nomCategorie);
            convertView.setTag(viewHolder);
        }

        String fournisseurCourant = (String) getItem(position);

        viewHolder.nomCategorie.setText(fournisseurCourant);

        return convertView;
    }

    @Override
    public void clear() {
        categories.clear();
    }

    private class CategorieViewHolder {
        public TextView nomCategorie;
    }
}