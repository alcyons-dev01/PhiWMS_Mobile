package fr.alcyons.phimr4.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 12/04/2019.
 */

public class ActionUtilisateurAdapter  extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<ActionUtilisateur> actionUtilisateurs = new ArrayList<ActionUtilisateur>();
    public ArrayList<ActionUtilisateur> actionUtilisateursDeBase = new ArrayList<ActionUtilisateur>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    Context context;
    SQLiteDatabase db;
    private LayoutInflater mInflater;

    public ActionUtilisateurAdapter(Context context, SQLiteDatabase database) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = database;
        this.context = context;
        this.actionUtilisateursDeBase = new ArrayList<>();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final ActionUtilisateur item) {
        actionUtilisateurs.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final ActionUtilisateur item) {
        actionUtilisateurs.add(item);
        sectionHeader.add(actionUtilisateurs.size() - 1);
        notifyDataSetChanged();
    }

    public void setListActionUtilisateur(List<ActionUtilisateur> listActionUtilisateur)
    {
        this.actionUtilisateursDeBase.addAll(listActionUtilisateur);
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return actionUtilisateurs.size();
    }

    @Override
    public ActionUtilisateur getItem(int position) {
        return actionUtilisateurs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ActionUtilisateurViewHolder viewHolder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new ActionUtilisateurViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.row_action_utilisateur, parent, false);
                    viewHolder.nomService = (TextView) convertView.findViewById(R.id.nomService);
                    viewHolder.champsId = (TextView) convertView.findViewById(R.id.champsId);
                    viewHolder.statutAction = (TextView) convertView.findViewById(R.id.statutAction);
                    viewHolder.layout_indicateur = (LinearLayout) convertView.findViewById(R.id.layout_indicateur);
                    viewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
                    viewHolder.layout_principal = (LinearLayout) convertView.findViewById(R.id.layout_principal);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_date_action_utilisateur, parent, false);
                    viewHolder.zoneDate = (LinearLayout) convertView.findViewById(R.id.zoneDate);
                    viewHolder.dateAction = (TextView) convertView.findViewById(R.id.textSeparator);

            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ActionUtilisateurViewHolder) convertView.getTag();
        }

        ActionUtilisateur actionCourante = (ActionUtilisateur) getItem(position);


        if (rowType == TYPE_ITEM) {
            Service service = ServiceOpenHelper.getServiceByID(db, actionCourante.getServiceId());
            if(service != null)
            {
                viewHolder.nomService.setText(service.getNom());
                if(actionCourante.getChampsParentId() == 0)
                {
                    viewHolder.champsId.setText("Aucun document associé");
                }
                else
                {
                    viewHolder.champsId.setText("N°"+actionCourante.getChampsParentId());
                }
            }

            viewHolder.statutAction.setText(actionCourante.getStatut());

            switch (actionCourante.getStatut())
            {
                case "En attente":
                    viewHolder.layout_indicateur.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_encours, null));
                    viewHolder.layout_principal.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.contour_gestion_statut_action_encours, null));
                    break;
                case "Soumise":
                    viewHolder.layout_indicateur.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_soumis, null));
                    viewHolder.layout_principal.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.contour_gestion_statut_action_soumise, null));
                    break;
                case "Annulée":
                    viewHolder.layout_indicateur.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_annuler, null));
                    viewHolder.layout_principal.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.contour_gestion_statut_action_annuler, null));
                    break;
                case "En conflit":
                    viewHolder.layout_indicateur.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_conflit, null));
                    viewHolder.layout_principal.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.contour_gestion_statut_action_conflit, null));
                    break;
                default :
                    viewHolder.layout_indicateur.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_valider, null));
                    viewHolder.layout_principal.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.contour_gestion_statut_action_valider, null));
                    break;
            }

            if(actionCourante.getCheminPhoto() != null && !actionCourante.getCheminPhoto().contentEquals("chemin"))
            {
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(actionCourante.getCheminPhoto()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewHolder.profile_image.setImageBitmap(photo);
            }

        } else if (rowType == TYPE_HEADER) {
            DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();

            try {
                date = dateFormat1.parse(actionCourante.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String date_courante = dateFormat2.format(date);
            viewHolder.dateAction.setText(date_courante);
        }

        return convertView;
    }

    private class ActionUtilisateurViewHolder {
        public TextView nomService;
        public TextView champsId;
        public LinearLayout layout_indicateur;
        public TextView statutAction;
        public ImageView profile_image;
        public LinearLayout layout_principal;
        public LinearLayout zoneDate;
        public TextView dateAction;
    }

    public void filter(String charText) {
        actionUtilisateurs.clear();
        List<String> listeDate = new ArrayList<>();
        if (charText.length() == 0) {
            for(ActionUtilisateur action_courante : actionUtilisateursDeBase)
            {
                DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();

                try {
                    date = dateFormat1.parse(action_courante.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String date_courante = dateFormat2.format(date);
                if (listeDate.indexOf(date_courante) == -1) {
                    listeDate.add(date_courante);
                    addSectionHeaderItem(action_courante);
                }
                addItem(action_courante);
            }
        } else {
            for (ActionUtilisateur courante : actionUtilisateursDeBase) {
                Service service_courant = ServiceOpenHelper.getServiceByID(db, courante.getServiceId());
                if(service_courant != null)
                {
                    String service_nom = ServiceOpenHelper.getServiceByID(db, courante.getServiceId()).getNom();

                    if (service_nom.toLowerCase().contains(charText) || String.valueOf(courante.getChampsParentId()).contains(charText))
                    {
                        DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();

                        try {
                            date = dateFormat1.parse(courante.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String date_courante = dateFormat2.format(date);
                        if (listeDate.indexOf(date_courante) == -1) {
                            listeDate.add(date_courante);
                            addSectionHeaderItem(courante);
                        }
                        addItem(courante);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}