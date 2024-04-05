package fr.alcyons.phiwms_mobile.ListViewAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EVENTOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Dotation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import com.example.phiwms_mobile.DemandePleinVide.ServiceDemandePleinVideActivity;
import com.example.phiwms_mobile.R;

/**
 * Created by jessica on 03/10/2017.
 */

public class DotationAdapter extends ArrayAdapter {

    public List<Dotation> dotations;
    public List<DotationViewHolder> dotationsViewHolderList;
    Context context;
    SQLiteDatabase db;

    public DotationAdapter(Context context, List<Dotation> dotations, SQLiteDatabase db) {
        super(context, 0, dotations);
        this.dotations = dotations;
        this.context = context;
        this.db = db;
        dotationsViewHolderList = new ArrayList<>();
        for (int i = 0; i < dotations.size(); i++) {
            DotationViewHolder viewHolder = new DotationViewHolder();
            dotationsViewHolderList.add(viewHolder);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DotationViewHolder viewHolder = dotationsViewHolderList.get(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_dotation, parent, false);

        // Récupération des objets graphiques
        viewHolder.nom = (TextView) convertView.findViewById(R.id.intituleDotation);
        viewHolder.nbProduitDotation = (TextView) convertView.findViewById(R.id.nbProduitDotation);
        viewHolder.statutDotation = (TextView) convertView.findViewById(R.id.statutDotation);
        viewHolder.barreStatutDotation = (ImageView) convertView.findViewById(R.id.barreStatutDotation);

        Dotation dotationCourant = (Dotation) getItem(position);
        int nbDetail = 0;
        String dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourant.getDepot_UID());
        PH_Preparation phPreparationCourante = PH_PreparationOpenHelper.getDemandePleinVideEnInstance(db, "Dotation PleinVide DPV" + String.valueOf(dotationCourant.get_UID()) + ":" + dotationCourant.getIntitulé(), dateProchaineLivraison);

        if(phPreparationCourante == null)
        {
            CreationPhPreparation(dotationCourant);
        }
        else
        {
            List<PH_Preparation_Ligne> ListPhPreparationLigne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, phPreparationCourante);
            nbDetail = ListPhPreparationLigne.size();
        }


        // Affichage des valeurs
        String[] tabIntitule = dotationCourant.getIntitulé().split("- PLEINVIDE");
        viewHolder.nom.setText(tabIntitule[0]);
        viewHolder.nbProduitDotation.setText(String.valueOf(nbDetail));

        if(nbDetail == 0)
        {
            viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
            viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.bleu_clair_alcyons, null));
        }
        else
        {
            viewHolder.barreStatutDotation.setBackgroundColor(context.getResources().getColor(R.color.orange, null));
            viewHolder.nbProduitDotation.setTextColor(context.getResources().getColor(R.color.orange, null));
        }

        dateProchaineLivraison = EVENTOpenHelper.getDateProchaineLivraison(db, dotationCourant.getDepot_UID());
        if(dateProchaineLivraison.contentEquals(""))
            viewHolder.statutDotation.setText("A envoyer");
        else
            viewHolder.statutDotation.setText("A envoyer avant le "+dateProchaineLivraison);

        return convertView;
    }

    private class DotationViewHolder {
        public TextView nom;
        public TextView nbProduitDotation;
        public TextView statutDotation;
        public ImageView barreStatutDotation;
    }

    private int CreationPhPreparation(Dotation dotation)
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
        String Liste = "Dotation PleinVide DPV" + String.valueOf(dotation.get_UID()) + ":" + dotation.getIntitulé();
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
        String LivraisonPrevueDate = "";
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

        long rowId = ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION, ph_preparationPHIMR4uid, ph_preparation.getUID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

        return ph_preparation.getUID();
    }
}