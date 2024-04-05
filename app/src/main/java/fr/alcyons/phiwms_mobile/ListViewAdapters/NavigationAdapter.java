package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;


import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Service;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import com.example.phiwms_mobile.R;

public class NavigationAdapter extends ArrayAdapter<Service> implements Filterable {

    public List<Service> services;
    public List<NavigationViewHolder> navigationViewHolderList;

    Map<String, Integer> mapServiceIndicateur;
    Context context;

    String nomPerimetreFonctionnel;
    Utilisateur utilisateurConnecte;
    SQLiteDatabase db;
    public boolean afficherInfo;

    public NavigationAdapter(Context context, Utilisateur utilisateurConnecte, SQLiteDatabase db, List<Service> services, Map<String, Integer> mapServiceIndicateur, String nomPerimetreFonctionnel, boolean afficherInfo) {
        super(context, 0, services);
        this.services = services;
        this.context = context;
        this.mapServiceIndicateur = mapServiceIndicateur;
        navigationViewHolderList = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            NavigationViewHolder viewHolder = new NavigationViewHolder();
            navigationViewHolderList.add(viewHolder);
        }

        this.nomPerimetreFonctionnel = nomPerimetreFonctionnel;
        this.utilisateurConnecte = utilisateurConnecte;
        this.db = db;
        this.afficherInfo = afficherInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationViewHolder viewHolder = navigationViewHolderList.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_service_navigation, parent, false);

        viewHolder.nom = (TextView) convertView.findViewById(R.id.nomService);
        viewHolder.descriptionService = (TextView) convertView.findViewById(R.id.descriptionService);
        viewHolder.indicateurService = (TextView) convertView.findViewById(R.id.indicateurService);
        viewHolder.principal = (LinearLayout) convertView.findViewById(R.id.principal);
        viewHolder.listViewSeparation = (ImageView) convertView.findViewById(R.id.listViewSeparation);
        viewHolder.barreIndicateur = (ImageView) convertView.findViewById(R.id.barreIndicateur);
        viewHolder.boutonInformation = (ImageView) convertView.findViewById(R.id.boutonInformation);

        if(position == navigationViewHolderList.size())
        {
            viewHolder.listViewSeparation.setVisibility(View.GONE);
        }

        final Service serviceCourant = getItem(position);

        viewHolder.nom.setText(serviceCourant.getNom());
        viewHolder.descriptionService.setText(serviceCourant.getDescription());
        if(!nomPerimetreFonctionnel.toLowerCase().contentEquals("commun"))
        {
            if(mapServiceIndicateur != null)
            {
                if(mapServiceIndicateur.containsKey(serviceCourant.getNom()))
                {
                    int indicateur = (int) mapServiceIndicateur.get(serviceCourant.getNom());
                    if(serviceCourant.getNom().contentEquals("Demande PleinVide"))
                    {
                        if(indicateur == 0)
                        {
                            indicateur = -1;
                        }
                    }
                    if(indicateur == -1)
                    {
                        viewHolder.indicateurService.setVisibility(View.GONE);
                    }
                    else
                    {
                        viewHolder.indicateurService.setText(String.valueOf(indicateur));
                    }
                }
            }
        }
        else
        {
            if(serviceCourant.getNom().contentEquals("Actions utilisateurs"))
            {
                int nb_action_en_attente = ActionUtilisateurOpenHelper.getNbActionNonTerminee(db, utilisateurConnecte.getId());
                viewHolder.indicateurService.setText(String.valueOf(nb_action_en_attente));
            }
            else
            {
                viewHolder.indicateurService.setVisibility(View.GONE);
            }
        }

        String statut = serviceCourant.getStatut();

        int color;
        switch (statut) {
            case "PRODUCTION":
                color = context.getResources().getColor(R.color.vert, null);
                viewHolder.barreIndicateur.setBackgroundColor(color);
                viewHolder.nom.setTextColor(context.getResources().getColor(R.color.noir, null));
                break;
            case "PROTOTYPE":
                color = context.getResources().getColor(R.color.orange2, null);
                viewHolder.barreIndicateur.setBackgroundColor(color);
                viewHolder.nom.setTextColor(color);
                break;
            case "DESIGN":
                color = context.getResources().getColor(R.color.rouge2, null);
                viewHolder.barreIndicateur.setBackgroundColor(color);
                viewHolder.nom.setTextColor(color);
                break;
            default:
                color = context.getResources().getColor(R.color.bleu_clair_alcyons, null);
                viewHolder.barreIndicateur.setBackgroundColor(color);
                viewHolder.nom.setTextColor(color);
                break;
        }

        if(afficherInfo)
        {
            viewHolder.boutonInformation.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.boutonInformation.setVisibility(View.GONE);
        }

        viewHolder.boutonInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationActivity) context).clickInformationService(serviceCourant);
            }
        });

        return convertView;
    }

    private class NavigationViewHolder {
        public TextView nom;
        public TextView descriptionService;
        public TextView indicateurService;
        public ImageView listViewSeparation;
        public ImageView boutonInformation;
        public LinearLayout principal;
        public ImageView barreIndicateur;
    }

}
