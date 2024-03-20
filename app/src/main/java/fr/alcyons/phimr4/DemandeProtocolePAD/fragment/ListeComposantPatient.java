package fr.alcyons.phimr4.DemandeProtocolePAD.fragment;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.alcyons.phimr4.BaseDeDonnees.Composants_patientOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.StockOpenHelper;
import fr.alcyons.phimr4.Classes.Composants_patient;
import fr.alcyons.phimr4.Classes.Demande_PUI;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_Patient;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Protocoles_Patients;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.Demande_PUIAdapter;
import fr.alcyons.phimr4.MenuActivity;
import fr.alcyons.phimr4.R;

/**
 * Created by jessica on 09/10/2017.
 */

public class ListeComposantPatient extends Fragment {

    Protocoles_Patients protocoles_patient;
    PH_Patient ph_patient;

    Context context;

    List<Demande_PUI> listeDemandePUI;
    ListView listViewDemandePUI;
    Demande_PUIAdapter adapter;

    String dateLivraisonSuivante;
    String dateInventaire;
    Integer jours_de_reserve;
    int multiplicateur = 1;

    // Fonctions permettant au parent de nous transmettre des paramètres
    public void setParametres(Protocoles_Patients protocoles_patient, PH_Patient ph_patient, String dateInventaire, String dateLivraisonSuivante, Integer jours_de_reserve) {
        this.protocoles_patient = protocoles_patient;
        this.ph_patient = ph_patient;
        this.dateInventaire = dateInventaire;
        this.dateLivraisonSuivante = dateLivraisonSuivante;
        this.jours_de_reserve = jours_de_reserve;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.liste_composant_patient, container, false);

        context = getContext();

        // Affichage des informations de base
        ((TextView) v.findViewById(R.id.nomDemande)).setText(String.valueOf(ph_patient.getNom_Usuel() + " " + ph_patient.getPrénom()));

        // Récupération de la listView et initialisation de la liste contenant les Demande PUI
        listViewDemandePUI = ((ListView) v.findViewById(R.id.listeView));
        listeDemandePUI = new ArrayList<>();

        /* Calcul du multiplicateur
        *
        * La demande de protocole patient a une particularité !
        * Le besoin d'un produit dépend de la quantité par séance mutliplié par le nombre de jours à dialyser.
        * Le nombre de jours à dialyser depénd :
        *   - du nombre de jours entre le date d'inventaire et la date de livraison suivante + le nombre de jour de reserve
        *   - la fréquence de dialyse du patient
        *
        * */
        SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy");
        try {
            //Calcul date de fin = dateLivraisonSuivante + jours_de_reserve
            Date date = dateParser.parse(dateLivraisonSuivante);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + jours_de_reserve);
            Date dateFin = c.getTime();

            // Date debut = dateInventaire
            date = dateParser.parse(dateInventaire);
            c = Calendar.getInstance();
            c.setTime(date);
            Date dateDebut = c.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateDebut);
            Calendar calMax = Calendar.getInstance();
            calMax.setTime(dateFin);

            /* Calendar.DAY_OF_WEEK retourne un int compris entre 1 et 7
            *  Le tableau " days " nous permet d'associer le Calendar.DAY_OF_WEEK à sa représentation dans " frequence "
            *  Le premier élément du tableau est volontairement mis à blanc afin de faire correspondre " days " au Calendar.DAY_OF_WEEK
            *  Sinon ERREUR index en dehors du tableau
            * */
            String[] days = new String[]{"", "D", "L", "Ma", "Me", "J", "V", "S"};

            // Récupération de la fréquence de dialyse du patient
            String frequence = protocoles_patient.getFrequence();

            // Parcours d'une plage de date
            for (; calendar.before(calMax); calendar.add(Calendar.DATE, 1)) {
                String day = days[calendar.get(Calendar.DAY_OF_WEEK)];

                if (frequence.contains(day)) {
                    multiplicateur++;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Récupération des composants patient du Protocoles Patient
        for (Composants_patient composants_patient : Composants_patientOpenHelper.getComposants_patientByProcotolesPatients(((MenuActivity) context).db, protocoles_patient.get_UID())) {

            Produit produit = ProduitOpenHelper.getProduitByID(((MenuActivity) context).db, composants_patient.getCode_produit());
            Depot depotPUI = DepotOpenHelper.getDepotPUI(((MenuActivity) context).db);
            Depot depotDestinataire = DepotOpenHelper.getDepotParReference(((MenuActivity) context).db, protocoles_patient.getDepot_Reference());

            Stock stockPUI = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotPUI);
            Stock stockDestinataire = StockOpenHelper.getStockByProduitEtDepot(((MenuActivity) context).db, produit, depotDestinataire);

            double qteStockPui = 0;
            double qteStockDestinataire = 0;

            if (stockPUI != null)
                qteStockPui = stockPUI.getQuantite_Actuelle();
            if (stockDestinataire != null)
                qteStockDestinataire = stockDestinataire.getQuantite_Actuelle();

            Demande_PUI demande_pui = new Demande_PUI(composants_patient, produit, qteStockPui, qteStockDestinataire, multiplicateur);

            listeDemandePUI.add(demande_pui);
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Demande_PUIAdapter(getActivity(), listeDemandePUI);

        listViewDemandePUI.setAdapter(adapter);
        listViewDemandePUI.setItemsCanFocus(true);
        listViewDemandePUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Effacer le clavier
                Demande_PUIAdapter.Demande_PUIViewHolder viewHolder = adapter.Demande_PUIViewHolderList.get(position);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MenuActivity) context).invalidateOptionsMenu();
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Demande_PUIAdapter(getActivity(), listeDemandePUI);
        listViewDemandePUI.setAdapter(adapter);
    }

}