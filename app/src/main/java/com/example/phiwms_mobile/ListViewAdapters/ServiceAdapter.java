package com.example.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.phiwms_mobile.Classes.Service;
import com.example.phiwms_mobile.R;

/**
 * Created by quentinlanusse on 14/04/2017.
 */


public class ServiceAdapter extends ArrayAdapter<Service> implements Filterable {

    public List<Service> services;
    Context context;
    List<String> serviceIndicateurNom;
    List<Integer> serviceIndicateurValeur;

    public ServiceAdapter(Context context, List<Service> services, List<String> serviceIndicateurNom, List<Integer> serviceIndicateurValeur) {
        super(context, 0, services);
        this.services = services;
        this.context = context;
        this.serviceIndicateurNom = serviceIndicateurNom;
        this.serviceIndicateurValeur = serviceIndicateurValeur;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_service, parent, false);
        }

        ServiceViewHolder viewHolder = (ServiceViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ServiceViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomService);
            viewHolder.indicateurService = (TextView) convertView.findViewById(R.id.indicateurService);
            viewHolder.statutService = (ImageView) convertView.findViewById(R.id.statutService);
            viewHolder.separateur = (ImageView) convertView.findViewById(R.id.separateur);
            convertView.setTag(viewHolder);
        }

        Service serviceCourant = getItem(position);

        viewHolder.nom.setText(serviceCourant.getNom());
        String statut = serviceCourant.getStatut();

        if (serviceIndicateurNom != null) {
            int position2 = serviceIndicateurNom.indexOf(serviceCourant.getNom());
            if (position2 != -1) {
                viewHolder.indicateurService.setText(String.valueOf(serviceIndicateurValeur.get(position2)));
                viewHolder.separateur.setVisibility(View.VISIBLE);
            } else {
                viewHolder.indicateurService.setText("");
                viewHolder.separateur.setVisibility(View.INVISIBLE);
            }
        }

        int color;
        switch (statut) {
            case "PRODUCTION":
                color = context.getResources().getColor(R.color.vert, null);
                viewHolder.nom.setTextColor(context.getResources().getColor(R.color.noir, null));
                break;
            case "PROTOTYPE":
                color = context.getResources().getColor(R.color.orange2, null);
                viewHolder.nom.setTextColor(color);
                break;
            case "DESIGN":
                color = context.getResources().getColor(R.color.rouge2, null);
                viewHolder.nom.setTextColor(color);
                break;
            default:
                color = context.getResources().getColor(R.color.bleu_clair_alcyons, null);
                viewHolder.nom.setTextColor(color);
                break;
        }
        ViewCompat.setBackgroundTintList(viewHolder.statutService, ColorStateList.valueOf(color));

        return convertView;
    }

    private class ServiceViewHolder {
        public TextView nom;
        public TextView indicateurService;
        public ImageView statutService;
        public ImageView separateur;
    }

}
