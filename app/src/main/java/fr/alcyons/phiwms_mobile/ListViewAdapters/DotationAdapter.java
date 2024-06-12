package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class DotationAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<Dotation> listeDotation = new ArrayList<Dotation>();
    public TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    Context context;
    SQLiteDatabase db;
    private LayoutInflater mInflater;
    Utilisateur utilisateur;

    public List<PH_Preparation> listePhPreparation;

    public DotationAdapter(Context context, SQLiteDatabase database, Utilisateur utilisateur) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = database;
        this.context = context;
        this.utilisateur = utilisateur;
        this.listePhPreparation = new ArrayList<>();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final Dotation item) {
        listeDotation.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final Dotation item) {
        listeDotation.add(item);
        sectionHeader.add(listeDotation.size() - 1);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return listeDotation.size();
    }

    @Override
    public Dotation getItem(int position) {
        return listeDotation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);

        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    convertView =  mInflater.inflate(R.layout.row_dotation, parent, false);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_ph_preparation_plein_vide, parent, false);
                    break;
            }
        }
        DotationViewHolder viewHolder = (DotationViewHolder) convertView.getTag();

        if(viewHolder == null)
        {
            viewHolder = new DotationViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView =  mInflater.inflate(R.layout.row_dotation, parent, false);
                    // Récupération des objets graphiques
                    viewHolder.nom = (TextView) convertView.findViewById(R.id.intituleDotation);
                    viewHolder.nbProduitDotation = (TextView) convertView.findViewById(R.id.nbProduitDotation);
                    viewHolder.statutDotation = (TextView) convertView.findViewById(R.id.statutDotation);
                    viewHolder.barreStatutDotation = (ImageView) convertView.findViewById(R.id.barreStatutDotation);
                    viewHolder.separateurQuantite = (ImageView) convertView.findViewById(R.id.separateurQuantite);
                    viewHolder.linearQuantite = (LinearLayout) convertView.findViewById(R.id.linearQuantite);
                    viewHolder.nbProduitDotationTotal = (TextView) convertView.findViewById(R.id.nbProduitDotationTotal);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_ph_preparation_plein_vide, parent, false);
                    viewHolder.zoneDate = (LinearLayout) convertView.findViewById(R.id.zoneDate);
                    viewHolder.dateLivraison = (TextView) convertView.findViewById(R.id.textSeparator);
                    viewHolder.zoneDate.setClickable(false);
                    viewHolder.dateLivraison.setEnabled(false);
                    viewHolder.dateLivraison.setClickable(false);
                    viewHolder.zoneDate.setEnabled(false);
                    break;
            }
            convertView.setTag(viewHolder);
        }

        if (rowType == TYPE_ITEM) {
            Dotation dotationCourant = (Dotation) getItem(position);
            int nbDetail = 0;

            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourant.getDepot_UID());
            if(dateProchaineLivraison == "")
            {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                dateProchaineLivraison = dateFormat.format(tomorrow);
            }
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandeDotationGlobaleEnInstance(db, "Dotation Globale : " + dotationCourant.getIntitule(), dateProchaineLivraison);

            List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);
            for(PH_Preparation_Ligne preparation_ligne : ListPhPreparationLigne)
            {
                if(preparation_ligne.getQte_StockSaisie() > -1 && !preparation_ligne.getSYS_DT_MAJ().contentEquals("0000-00-00"))
                {
                    nbDetail ++;
                }
            }

            listePhPreparation.add(phPreparationCourante);

            // Affichage des valeurs
            viewHolder.nom.setText(dotationCourant.getIntitule());
            viewHolder.nbProduitDotation.setText(String.valueOf(nbDetail));
            viewHolder.nbProduitDotationTotal.setText(String.valueOf(ListPhPreparationLigne.size()));

            if(nbDetail == 0)
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
                viewHolder.linearQuantite.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_bleu, null));
                viewHolder.separateurQuantite.setBackgroundColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
            }
            else if(phPreparationCourante.getStatut().contentEquals("En cours de préparation"))
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.rouge, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.rouge, null));
                viewHolder.linearQuantite.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_rouge, null));
                viewHolder.separateurQuantite.setBackgroundColor(context.getResources().getColor(R.color.rouge, null));
            }
            else
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.orange, null));
                viewHolder.linearQuantite.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_orange, null));
                viewHolder.separateurQuantite.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
            }

            if(phPreparationCourante.getStatut().contentEquals("En instance"))
            {
                if(nbDetail == 0)
                {
                    viewHolder.statutDotation.setTextColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
                }
                else
                {
                    viewHolder.statutDotation.setTextColor(context.getResources().getColor(R.color.orange, null));
                }
                viewHolder.statutDotation.setText("A ENVOYER");
            }
            else if(phPreparationCourante.getStatut().contentEquals("En cours de régularisation"))
            {
                viewHolder.statutDotation.setText("En cours");
                viewHolder.statutDotation.setTextColor(context.getResources().getColor(R.color.orange, null));
            }
            else if(phPreparationCourante.getStatut().contentEquals("En cours de préparation"))
            {
                viewHolder.statutDotation.setTextColor(context.getResources().getColor(R.color.rouge, null));
                viewHolder.statutDotation.setText(phPreparationCourante.getStatut());
            }
            else
            {
                viewHolder.statutDotation.setTextColor(context.getResources().getColor(R.color.noir, null));
                viewHolder.statutDotation.setText(phPreparationCourante.getStatut());
            }
        } else if (rowType == TYPE_HEADER) {
            // Gestion des dates
            String dateProchaineLivraison = listeDotation.get(position).getDateLivraison();
            String[] tabDate = dateProchaineLivraison.split("/");
            if(tabDate.length < 3)
                dateProchaineLivraison = null;
            else
                dateProchaineLivraison = tabDate[tabDate.length-1]+"-"+tabDate[1]+"-"+tabDate[0];
            Date date = null;
            if (dateProchaineLivraison != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(dateProchaineLivraison);
                    Calendar calendarCourant = Calendar.getInstance();
                    calendarCourant.setTime(date);
                    calendarCourant.add(Calendar.DAY_OF_WEEK, - 1);
                    int numeroJour = calendarCourant.get(Calendar.DAY_OF_WEEK);
                    if(numeroJour == Calendar.SUNDAY)
                    {
                        calendarCourant.add(Calendar.DAY_OF_WEEK, -2);
                    }
                    else if(numeroJour == Calendar.SATURDAY)
                    {
                        calendarCourant.add(Calendar.DAY_OF_WEEK, -1);
                    }

                    date = calendarCourant.getTime();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                String dayOfTheWeek = "";
                String day          = "";
                String monthString  = "";
                String monthNumber  = "";
                String year         = "";

                dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
                day          = (String) android.text.format.DateFormat.format("dd",   date);
                monthString  = (String) android.text.format.DateFormat.format("MMM",  date);
                year         = (String) android.text.format.DateFormat.format("yyyy", date);

                switch (dayOfTheWeek.toUpperCase())
                {
                    case "MONDAY":
                        dayOfTheWeek = "LUNDI";
                        break;
                    case "TUESDAY":
                        dayOfTheWeek = "MARDI";
                        break;
                    case "WEDNESDAY":
                        dayOfTheWeek = "MERCREDI";
                        break;
                    case "THURSDAY":
                        dayOfTheWeek = "JEUDI";
                        break;
                    case "FRIDAY":
                        dayOfTheWeek = "VENDREDI";
                        break;
                    case "SATURDAY":
                        dayOfTheWeek = "SAMEDI";
                        break;
                    case "SUNDAY":
                        dayOfTheWeek = "DIMANCHE";
                        break;
                }

                switch (monthString.toUpperCase())
                {
                    case "JANUARY":
                        monthString = "JANVIER";
                        break;
                    case "FEBRUARY":
                        monthString = "FÉVRIER";
                        break;
                    case "MARCH":
                        monthString = "MARS";
                        break;
                    case "APRIL":
                        monthString = "AVRIL";
                        break;
                    case "MAY":
                        monthString = "MAI";
                        break;
                    case "JUNE":
                        monthString = "JUIN";
                        break;
                    case "JULY":
                        monthString = "JUILLET";
                        break;
                    case "AUGUST":
                        monthString = "AOÛT";
                        break;
                    case "SEPTEMBER":
                        monthString = "SEPTEMBRE";
                        break;
                    case "OCTOBER":
                        monthString = "OCTOBRE";
                        break;
                    case "NOVEMBER":
                        monthString = "NOVEMBRE";
                        break;
                    case "DECEMBER":
                        monthString = "DÉCEMBRE";
                        break;
                }

                String dateProchaineLivraisonString = (String) android.text.format.DateFormat.format("dd/MM/yyyy", date);
                viewHolder.setDateLivraisonColor(dateProchaineLivraisonString);

                viewHolder.dateLivraison.setText(dayOfTheWeek.substring(0,3)+" "+day+" "+monthString+" "+year);
            }
        }

        return convertView;
    }

    private class DotationViewHolder {
        public TextView nom;
        public TextView nbProduitDotation;
        public TextView nbProduitDotationTotal;
        public TextView statutDotation;
        public ImageView barreStatutDotation;

        public TextView dateLivraison;
        public LinearLayout zoneDate;
        public LinearLayout linearQuantite;
        public ImageView separateurQuantite;
        public void setDateLivraisonColor(String dateProchaineLivraison) {
            Date date = null;
            if(!dateProchaineLivraison.contentEquals(""))
            {
                String[] tabDate = dateProchaineLivraison.split("/");
                dateProchaineLivraison = tabDate[tabDate.length-1]+"-"+tabDate[1]+"-"+tabDate[0];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = format.parse(dateProchaineLivraison);
                    System.out.println(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (date != null) {

                Date dateDuJour = new Date();
                long diff = dateDuJour.getTime() - date.getTime();
                int delai = (int) (diff / (1000 * 60 * 60 * 24));

                int demain = -1;

                if (delai > 0) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.grey_color_fonce, null));
                } else if (delai == 0) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.rouge2, null));
                } else if (delai == demain) {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.orange2, null));
                } else {
                    zoneDate.setBackgroundColor(context.getResources().getColor(R.color.vert, null));
                }
            } else {
                zoneDate.setBackgroundColor(context.getResources().getColor(R.color.noir, null));
            }

        }
    }
}
