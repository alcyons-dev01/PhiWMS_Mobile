package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Detail_DotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Detail_Dot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.R;

public class PleinVideAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    public ArrayList<Dotation> listeDotation = new ArrayList<>();
    public TreeSet<Integer> sectionHeader = new TreeSet<>();
    Context context;
    SQLiteDatabase db;
    private final LayoutInflater mInflater;
    Utilisateur utilisateur;

    public List<PH_Preparation> listePhPreparation;

    public PleinVideAdapter(Context context, SQLiteDatabase database, Utilisateur utilisateur) {
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

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DotationViewHolder viewHolder;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new DotationViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView =  mInflater.inflate(R.layout.row_dotation, parent, false);
                    // Récupération des objets graphiques
                    viewHolder.nom = convertView.findViewById(R.id.intituleDotation);
                    viewHolder.nbProduitDotation = convertView.findViewById(R.id.nbProduitDotation);
                    viewHolder.statutDotation = convertView.findViewById(R.id.statutDotation);
                    viewHolder.barreStatutDotation = convertView.findViewById(R.id.barreStatutDotation);
                    viewHolder.separateurQuantitePleinVide = convertView.findViewById(R.id.separateurQuantitePleinVide);
                    viewHolder.linearQuantitePleinVide = convertView.findViewById(R.id.linearQuantitePleinVide);
                    viewHolder.nbProduitDotationTotal = convertView.findViewById(R.id.nbProduitDotationTotal);
                    break;
                case TYPE_HEADER:
                    convertView = mInflater.inflate(R.layout.row_header_ph_preparation_plein_vide, parent, false);
                    viewHolder.zoneDate = convertView.findViewById(R.id.zoneDate);
                    viewHolder.dateLivraison = convertView.findViewById(R.id.textSeparator);
                    viewHolder.zoneDate.setClickable(false);
                    viewHolder.dateLivraison.setEnabled(false);
                    viewHolder.dateLivraison.setClickable(false);
                    viewHolder.zoneDate.setEnabled(false);
                    break;
            }
            assert convertView != null;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DotationViewHolder) convertView.getTag();
        }

        if (rowType == TYPE_ITEM) {
            Dotation dotationCourant = getItem(position);
            int nbDetail = 0;

            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourant.getDepot_UID());
            PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotationCourant.get_UID() + ":" + dotationCourant.getIntitule(), dateProchaineLivraison);

            if(phPreparationCourante == null)
            {
                phPreparationCourante = CreationPhPreparation(dotationCourant);
                ElementASynchroniserOpenHelper.toutSynchroniser(context, db, utilisateur, false);
            }
            List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);
            for(PH_Preparation_Ligne preparation_ligne : ListPhPreparationLigne)
            {
                if(preparation_ligne.getQte_APreparer() > 0)
                {
                    nbDetail ++;
                }
            }

            listePhPreparation.add(phPreparationCourante);

            // Affichage des valeurs
            String[] tabIntitule = dotationCourant.getIntitule().split("- PLEINVIDE");
            viewHolder.nom.setText(tabIntitule[0]);
            viewHolder.nbProduitDotation.setText(String.valueOf(nbDetail));
            viewHolder.nbProduitDotationTotal.setText(String.valueOf(ListPhPreparationLigne.size()));

            if(nbDetail == 0)
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
                viewHolder.linearQuantitePleinVide.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_bleu, null));
                viewHolder.separateurQuantitePleinVide.setBackgroundColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
            }
            else if(phPreparationCourante.getStatut().contentEquals("En cours de préparation"))
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.rouge, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.rouge, null));
                viewHolder.linearQuantitePleinVide.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_rouge, null));
                viewHolder.separateurQuantitePleinVide.setBackgroundColor(context.getResources().getColor(R.color.rouge, null));
            }
            else
            {
                viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
                viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.orange, null));
                viewHolder.linearQuantitePleinVide.setBackground(context.getResources().getDrawable(R.drawable.background_border_left_orange, null));
                viewHolder.separateurQuantitePleinVide.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
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
            String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, listeDotation.get(position).getDepot_UID());
            String[] tabDate = dateProchaineLivraison.split("/");
            dateProchaineLivraison = tabDate[tabDate.length-1]+"-"+tabDate[1]+"-"+tabDate[0];
            Date date;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = format.parse(dateProchaineLivraison);
                Calendar calendarCourant = getCalendar(date);

                date = calendarCourant.getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            String dayOfTheWeek;
            String day;
            String monthString;
            String year;

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

        return convertView;
    }

    @NonNull
    private static Calendar getCalendar(Date date) {
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
        return calendarCourant;
    }

    private class DotationViewHolder {
        public TextView nom;
        public TextView nbProduitDotation;
        public TextView nbProduitDotationTotal;
        public TextView statutDotation;
        public ImageView barreStatutDotation;

        public TextView dateLivraison;
        public LinearLayout zoneDate;
        public LinearLayout linearQuantitePleinVide;
        public ImageView separateurQuantitePleinVide;
        public void setDateLivraisonColor(String dateProchaineLivraison) {
            Date date = null;
            if(!dateProchaineLivraison.contentEquals(""))
            {
                String[] tabDate = dateProchaineLivraison.split("/");
                dateProchaineLivraison = tabDate[tabDate.length-1]+"-"+tabDate[1]+"-"+tabDate[0];
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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

    private PH_Preparation CreationPhPreparation(Dotation dotation)
    {
        Random phPreparationRandom = new Random();
        int phPreparationID = phPreparationRandom.nextInt();
        if (phPreparationID > 0) {
            phPreparationID = phPreparationID * -1;
        }

        Depot depotPUI = DepotOpenHelper.getDepotPUI(db);
        //Création d'une PH_Preparation
        // Initialisation des données permettant de créer un PH_Préparation
        int UID = phPreparationID;
        String Service = "";
        Boolean Erreur_Valid = false;
        String PHIE_Tag = "";
        String Saisie_Le = "";
        String A_tel_heure = "";
        int produitID = 0;
        String produitDesignation = "";
        double Qte_demandee = 0;
        Boolean Livree = false;
        Boolean Validee = false;
        String Origine = "";
        String Liste = "Dotation PleinVide DPV" + dotation.get_UID() + ":" + dotation.getIntitule();
        int depotDestinataireID = dotation.getDepot_UID();
        String depotDestinataireReference = dotation.getRef_Depot();
        String SYS_DT_MAJ = "";
        String SYS_HEURE_MAJ = "";
        String SYS_USER_MAJ = "";
        String PrescripteurReference = "";
        String Prescription_date = "";
        String PrescripteurNom = "";
        String depotOrigineReference = depotPUI.getDepot_Reference();
        int depotOrigineID = depotPUI.getDepot_UID();
        String Commentaires = "";
        String PreparationDate = "";
        String[] dateTab = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID()).split("/");
        String LivraisonPrevueDate = "";
        if(dateTab.length > 0)
        {
            LivraisonPrevueDate = dateTab[dateTab.length-1]+"-"+dateTab[1]+"-"+dateTab[0];
        }
        String DN_Groupe = "";
        double Montant_HT = 0;
        double Montant_TTC = 0;
        double Poids = 0;
        int Commande_ID = 0;
        String Preparateur = "";
        String Statut = "En instance";
        String PHIE_SYNCHRO = "";
        String receptionUFNonComforme = "";
        String livraisonDate = "";
        String Frequence = "";
        String previsionDateDebut = "";
        String previsionDateFin = "";
        Boolean URGENT = dotation.isURGENCE();
        String Motif = "";
        int preparateur_userID = 0;
        int pharmacien_userID = 0;
        double Volume = 0;
        int PaletteNB = 0;
        int CaisseNB = 0;
        int Conteneur_NB = 0;
        String numero_scelle = "";

        // Création et insertion en base du PH_Preparation
        PH_Preparation ph_preparation = new PH_Preparation(UID, Service, Erreur_Valid, PHIE_Tag, Saisie_Le, A_tel_heure, produitID, produitDesignation, Qte_demandee, Livree, Validee, Origine, Liste, depotDestinataireID, depotDestinataireReference, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, PrescripteurReference, Prescription_date, PrescripteurNom, depotOrigineReference, depotOrigineID, Commentaires, PreparationDate, LivraisonPrevueDate, DN_Groupe, Montant_HT, Montant_TTC, Poids, Commande_ID, Preparateur, Statut, PHIE_SYNCHRO, receptionUFNonComforme, livraisonDate, Frequence, previsionDateDebut, previsionDateFin, URGENT, Motif, preparateur_userID, pharmacien_userID, Volume, PaletteNB, CaisseNB,Conteneur_NB, numero_scelle);
        int ph_preparationPHIMR4uid = (int) PH_PreparationOpenHelper.insererUnPH_PreparationEnBDD(db, ph_preparation);

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

        List<Detail_Dot> listDetailDot = Detail_DotOpenHelper.getAllDetailDotParDotation(db, dotation);

        for(Detail_Dot detail_dot : listDetailDot)
        {
            CreatePhPreparationLigneDetailPleinVide(detail_dot, dotation);
        }

        return ph_preparation;
    }

    public void CreatePhPreparationLigneDetailPleinVide(Detail_Dot detailDot, Dotation dotation)
    {
        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotation.getDepot_UID());
        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + dotation.get_UID() + ":" + dotation.getIntitule(), dateProchaineLivraison);
        PH_Preparation_Ligne ph_preparation_ligneCourant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByPreparationAndIdProduit(db, phPreparationCourante, detailDot.getCode_produit());

        if(ph_preparation_ligneCourant == null)
        {
            Produit produitCorrespondant = ProduitOpenHelper.getProduitByID(db, detailDot.getCode_produit());

            Random phPreparationRandom = new Random();
            int phPreparationLigneID = phPreparationRandom.nextInt();
            if (phPreparationLigneID > 0) {
                phPreparationLigneID = phPreparationLigneID * -1;
            }

            // Initialisation des données permettant de créer un PH_Préparation_Ligne
            int PreparationID = phPreparationCourante.getUID();
            int _UID = phPreparationLigneID;
            int produitID = produitCorrespondant.getID_produit();
            String produitDesignation = produitCorrespondant.getDesignation_interne();
            int Qte_APreparer = 0;
            int Qte_livrer = 0;
            Boolean Livrer = false;
            Boolean Valider = false;
            String ValidationDate = "";
            String produitReference = produitCorrespondant.getRef_fourni();
            String ZoneDepot = "";
            String produitCategorie = produitCorrespondant.getCategorie();
            int Qte_RAL = 0;
            String SYS_DT_MAJ = "";
            String SYS_HEURE_MAJ = "";
            String SYS_USER_MAJ = "";
            double produitCondDistrib = produitCorrespondant.getCond_distrib();
            double produitPUHT = 0;
            Boolean Suivi_Par_Lot = produitCorrespondant.isSuivi_Lot();
            int patientID = 0;
            String PatientNom = "";
            String PrescripteurNom = "";
            String prescripteurReference = "";
            int Ordre_Impression = 0;
            int Prescription_ID = 0;
            String LotNumero = "";
            String PeremptionDate = "0000-00-00";
            double produitPoids = 0;
            double produitTVA = 0;
            double Montant_HT = 0;
            double Montant_TTC = 0;
            double PoidsTotal = 0;
            String depot_Destinataire_Reference = "";
            String utilisation_Date_Prevue = "";
            int Qte_besoin = 0;
            int Qte_StockSaisie = 0;
            int Qte_Demander = 0;
            String EmplacementParDefaut = "";
            int Qte_preparer = 0;
            boolean accepter = false;

            // Création et insertion en base du PH_Preparation_Ligne
            PH_Preparation_Ligne ph_preparation_ligne = new PH_Preparation_Ligne(PreparationID, _UID, produitID, produitDesignation, Qte_APreparer, Qte_livrer, Livrer, Valider, ValidationDate, produitReference, ZoneDepot, produitCategorie, Qte_RAL, SYS_DT_MAJ, SYS_HEURE_MAJ, SYS_USER_MAJ, produitCondDistrib, produitPUHT, Suivi_Par_Lot, patientID, PatientNom, PrescripteurNom, prescripteurReference, Ordre_Impression, Prescription_ID, LotNumero, PeremptionDate, produitPoids, produitTVA, Montant_HT, Montant_TTC, PoidsTotal, depot_Destinataire_Reference, utilisation_Date_Prevue, Qte_besoin, Qte_StockSaisie, Qte_Demander, EmplacementParDefaut, Qte_preparer, accepter, phPreparationCourante.getUID());

            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE, ph_preparation_ligne.getPhiMR4UUID(), ph_preparation_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
        }
    }
}
