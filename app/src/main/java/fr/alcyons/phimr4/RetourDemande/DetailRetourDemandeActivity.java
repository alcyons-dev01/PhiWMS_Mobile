package fr.alcyons.phimr4.RetourDemande;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.PH_RetourMotif;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Retour;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.Classes.Stock;
import fr.alcyons.phimr4.ListViewAdapters.RetourDemande_detailAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by olivier on 05/01/2018.
 */

public class DetailRetourDemandeActivity extends ServiceActivity {

    Depot depotSelectionne;
    List<Integer> listeProduit;
    List<Retour_Ligne> listRetourLigne;
    List<Stock> listeStock;
    List<String> prochaineDateLivraisonListe;
    Retour retour;
    RetourDemande_detailAdapter adapter;
    ListView listViewRetourLigne;


    TextView dateRepriseTextView;
    EditText commentaireEditText;
    LinearLayout dateLinear;

    List<String> produitSansQteDemande;
    // Définition de l'action a réalisé au Click sur le bouton Save
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            produitSansQteDemande = new ArrayList<>();

            List<PH_RetourMotif> ph_retourMotifListe = gestionnairePH_RetourMotif.getAllPH_RetourMotif(db);
            List<String> retourMotifStringList = new ArrayList<>();
            for (PH_RetourMotif ph_retourMotif : ph_retourMotifListe) {
                retourMotifStringList.add(ph_retourMotif.getMotifRetour());
            }

            String motif = Alerte.afficherAlerteListView(DetailRetourDemandeActivity.this, "Sélectionner le motif", retourMotifStringList);

            // On vérifie que le motif est valide
            if (motif == null) {
                Alerte.afficherAlerte(DetailRetourDemandeActivity.this, "Alerte", "Motif de retour obligatoire", "alerte");
                return;
            }

            retour.setMotif(motif.trim());

            if (commentaireEditText.getText() != null) {
                retour.setCommentaire(String.valueOf(commentaireEditText.getText()).trim());
            }

            // Date de prochaine livraison
            Date date = null;
            DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String dateReprise_String = retour.getDate_Reprise();
                date = dateDecodeur.parse(dateReprise_String);
                retour.setDate_Reprise(dateFormat.format(date));
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean toutEstOk = true;

            long retourPhiMR4UUID = gestionnaireRetour.insererUnRetourEnBDD(db, retour);
            if (retourPhiMR4UUID != -1) {
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retour.getPhiMR4UUID(), retour.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                for (Retour_Ligne retour_ligne : listRetourLigne) {
                    if (retour_ligne.getQte_Demander() == 0) {
                        produitSansQteDemande.add(retour_ligne.getProduit_Designation());
                    }
                }

                //création de l'action
                Random random = new Random();
                int actionId = random.nextInt();
                if(actionId > 0)
                    actionId= actionId*-1;
                SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateRetour =new Date();
                String date_string = parseFormat.format(dateRetour);
                ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retour.get_UID(), "", "Retour Demandé");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                //fin de la création de l'action utilisateur

                if (produitSansQteDemande.size() > 0) {
                    Boolean reponse = Alerte.afficherAlerteList(DetailRetourDemandeActivity.this, "Attention", "Produits avec quantité retourné égale à 0, continuer ?", produitSansQteDemande, "OuiNon");
                    if (reponse) {
                        for (Retour_Ligne retour_ligne : listRetourLigne) {
                            if (retour_ligne.getQte_Demander() != 0) {
                                long retourLignePhiMR4UUID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, retour_ligne);
                                if (retourLignePhiMR4UUID != -1) {
                                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligne.getPhiMR4UUID(), retour_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                                    //gestion des action utilisateur ligne pour le verrou pharmacie
                                    Random randomactionligne = new Random();
                                    int actionligneId = randomactionligne.nextInt();
                                    if(actionligneId > 0)
                                        actionligneId= actionligneId*-1;

                                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retour_ligne.get_UID(), "", 0, (int)retour_ligne.getQte_Retourner(), retour_ligne.getProduit_Designation());
                                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                                } else {
                                    toutEstOk = false;
                                }
                            }
                        }
                    }
                } else {
                    for (Retour_Ligne retour_ligne : listRetourLigne) {
                        long retourLignePhiMR4UUID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, retour_ligne);
                        if (retourLignePhiMR4UUID != -1) {
                            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligne.getPhiMR4UUID(), retour_ligne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                            //gestion des action utilisateur ligne pour le verrou pharmacie
                            Random randomactionligne = new Random();
                            int actionligneId = randomactionligne.nextInt();
                            if(actionligneId > 0)
                                actionligneId= actionligneId*-1;

                            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retour_ligne.get_UID(), "", 0, (int)retour_ligne.getQte_Retourner(), retour_ligne.getProduit_Designation());
                            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                        } else {
                            toutEstOk = false;
                        }
                    }
                }
            } else {
                toutEstOk = false;
            }

            if (toutEstOk) {
                gestionnaireElementASynchroniser.toutSynchroniser(DetailRetourDemandeActivity.this, db, utilisateurConnecte, true);
                Toast toast = Toast.makeText(DetailRetourDemandeActivity.this, "Demande de retour effectuée", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Alerte.afficherAlerte(DetailRetourDemandeActivity.this, "Erreur", "Une erreur est survenue dans le traitement de votre demande\nVeuillez réitérer votre demande", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
            }

            DetailRetourDemandeActivity.this.finish();
        }
    };
    Calendar date;
    String anneeComplete;
    String jour;
    String mois;
    String anneeCourte;
    String dateComplete;
    String dateAvecAnneeComplete;
    Random random;
    String dateProchaineLivraison;
    DatePickerDialog.OnDateSetListener dateRepriseDuDatePicker;
    com.github.clans.fab.FloatingActionButton boutonSave;
    Boolean finish = false;
    RetourDemande_detailAdapter.RetourLigneViewHolder viewHolderAModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_demande);


        listeStock = new ArrayList<>();

        //Initialisation de la date
        //sélection de la date
        date = Calendar.getInstance();
        anneeComplete = String.valueOf(date.get(date.YEAR));
        jour = String.valueOf(date.get(date.DATE));
        mois = String.valueOf(date.get(date.MONTH) + 1);
        if (date.get(date.MONTH) < 10) {
            mois = "0" + mois;
        }
        anneeCourte = anneeComplete.substring(anneeComplete.length() - 2, anneeComplete.length());
        dateComplete = jour + "/" + mois + "/" + anneeCourte;
        dateAvecAnneeComplete = jour + "/" + mois + "/" + anneeComplete;

        // Récupération du dépot grace aux variables globales
        depotSelectionne = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotUID_Selectionne"));

        //Récupération de la date de la prochaine livraison
        dateProchaineLivraison = intent.getStringExtra("dateProchaineLivraison");


        // Affichage des informations de base
        ((TextView) findViewById(R.id.nomDepot)).setText(depotSelectionne.getNom());

        //Récupération des Stock sélectionner
        listeProduit = intent.getExtras().getIntegerArrayList("ListeProduit");

        // Récupération de la listView
        listViewRetourLigne = (ListView) findViewById(R.id.listeView);

        //Initialisation des textView et des EditText
        dateRepriseTextView = ((TextView) findViewById(R.id.dateReprise));
        commentaireEditText = ((EditText) findViewById(R.id.commentaire));
        dateLinear = ((LinearLayout) findViewById(R.id.date));

        for (Integer i : listeProduit) {
            Produit produit = gestionnaireProduit.getProduitByID(db, i);
            Stock stock;
            if(produit != null)
            {
                stock = gestionnaireStock.getStockByProduitEtDepot(db, produit, depotSelectionne);
                listeStock.add(stock);
            }
        }


        //récupération de la liste des prochaines date de livraison
        prochaineDateLivraisonListe = new ArrayList<>();
        prochaineDateLivraisonListe = intent.getStringArrayListExtra("ListeDate");

        //Création PH_Retour
        //gestion de l'ID unique et random
        random = new Random();
        int retourID = random.nextInt();
        if (retourID > 0) {
            retourID = retourID * -1;
        }

        Depot depot_dest = gestionnaireDepot.getDepotRec(db);
        String intitule = depotSelectionne.getNom() + " : " + dateComplete;
        if (prochaineDateLivraisonListe.size() == 0) {
            retour = new Retour(retourID, String.valueOf(dateComplete + retourID), 0, intitule, depot_dest.getDepot_Reference(), "en attente", dateAvecAnneeComplete, 0, "", depotSelectionne.getDepot_Reference(), "", "Reprise demandée", dateAvecAnneeComplete, depotSelectionne.getDepot_Reference());
        } else {
            retour = new Retour(retourID, String.valueOf(dateComplete + retourID), 0, intitule, depot_dest.getDepot_Reference(), "en attente", dateAvecAnneeComplete, 0, "", depotSelectionne.getDepot_Reference(), "", "Reprise demandée", prochaineDateLivraisonListe.get(0), depotSelectionne.getDepot_Reference());
        }


        //Affichage des informations relatifs au PH_Retour
        commentaireEditText.setText(retour.getCommentaire().trim());
        dateRepriseTextView.setText(retour.getDate_Reprise().trim());

        //Création du PH_Retour_Ligne
        listRetourLigne = new ArrayList<>();
        for (Stock stockCourant : listeStock) {
            int retourLigneID = random.nextInt();
            if (retourLigneID > 0) {
                retourLigneID = retourLigneID * -1;
            }
            Produit produitCourant = gestionnaireProduit.getProduitByID(db, stockCourant.getProduit_UID());
            Retour_Ligne retour_ligne = new Retour_Ligne(retourLigneID, retour.get_UID(), (int) stockCourant.getQuantite_Actuelle(), produitCourant.getID_produit(), produitCourant.getRef_fourni(), produitCourant.getFournisseur(), (int) produitCourant.getPrix_unitaire(), (int) produitCourant.getTaux_de_TVA(), produitCourant.getDesignation_interne(), (int) stockCourant.getQuantite_Actuelle(), 0);
            listRetourLigne.add(retour_ligne);
        }

        //Trier la liste
        Collections.sort(listRetourLigne, new Comparator<Retour_Ligne>() {
            @Override
            public int compare(Retour_Ligne o1, Retour_Ligne o2) {
                return o1.getProduit_Designation().compareTo(o2.getProduit_Designation());
            }
        });


        // Récupération du bouton d'enregistrement
        boutonSave = (FloatingActionButton) findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);

        //Gestion du DataPicker pour modifier la date de reprise
        dateRepriseDuDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, monthOfYear);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateRepriseTextView);
            }

        };

        //Récupération de la date
        final String dateMinString = dateAvecAnneeComplete;
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);
        int moismax = month + 3;
        String moimaxString = String.valueOf(moismax);

        if (month < 9) {
            moimaxString = "0" + moismax;
        }

        final String dataMaxString = day + "/" + moimaxString + "/" + year;


        dateLinear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (prochaineDateLivraisonListe == null) {
                    DatePickerDialog dateReprisePickerDialog = new DatePickerDialog(DetailRetourDemandeActivity.this, dateRepriseDuDatePicker, year, month, day);

                    if (!dateMinString.contentEquals("00/00/0000")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date dateMin = new Date();
                        Date dateMax = new Date();
                        try {
                            dateMin = dateFormat.parse(dateMinString);
                            dateMax = dateFormat.parse(dataMaxString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dateReprisePickerDialog.getDatePicker().setMinDate(dateMin.getTime());
                        dateReprisePickerDialog.getDatePicker().setMaxDate(dateMax.getTime());
                    }

                    dateReprisePickerDialog.show();
                } else {
                    String dateChoisi = Alerte.afficherAlerteListView(DetailRetourDemandeActivity.this, "Sélectionner la date de reprise", prochaineDateLivraisonListe);

                    // On vérifie que le motif est valide
                    if (dateChoisi == null) {
                        return;
                    }

                    retour.setDate_Reprise(dateChoisi);
                    dateRepriseTextView.setText(dateChoisi);
                }

            }
        });

        //gestion de la rotation de l'écran
        if (savedInstanceState != null) {
            commentaireEditText.setText(savedInstanceState.getString("Commentaire").trim());
            dateRepriseTextView.setText(savedInstanceState.getString("DateReprise").trim());
            listRetourLigne = (List<Retour_Ligne>) savedInstanceState.getSerializable("listeRetourLigne");
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(savedInstanceState.getInt("nombreElement")));
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Affichage du nombre de produit
        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listRetourLigne.size()));

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new RetourDemande_detailAdapter(DetailRetourDemandeActivity.this, listRetourLigne, db);
        listViewRetourLigne.setDivider(footer);
        listViewRetourLigne.setAdapter(adapter);

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {

        Intent detailRetourDemandeIntent = new Intent(DetailRetourDemandeActivity.this, ListeProduitActivity.class);
        Bundle detailRetourDemandeBundle = super.getBundle();
        detailRetourDemandeBundle.putIntegerArrayList("ListeProduit", (ArrayList<Integer>) listeProduit);
        detailRetourDemandeBundle.putInt("depotUID_Selectionne", depotSelectionne.getDepot_UID());
        detailRetourDemandeBundle.putBoolean("Back", true);
        detailRetourDemandeIntent.putExtras(detailRetourDemandeBundle);
        DetailRetourDemandeActivity.this.startActivityForResult(detailRetourDemandeIntent, CodesEchangesActivites.RESULT_RETOUR_DEMANDE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_RETOUR_DEMANDE: {
                finish = data.getBooleanExtra("Finish", false);
                if (finish == false) {
                    Boolean present = false;
                    List<Integer> listeRetourBack = new ArrayList<>();
                    listeRetourBack = data.getIntegerArrayListExtra("ListeProduit");
                    for (Integer i : listeRetourBack) {
                        if (listeProduit.indexOf(i) != -1) {
                            present = true;
                        }

                        if (present == false) {
                            listeProduit.add(i);

                            int retourLigneID = random.nextInt();
                            if (retourLigneID > 0) {
                                retourLigneID = retourLigneID * -1;
                            }
                            Produit produitCourant = gestionnaireProduit.getProduitByID(db, i);
                            Stock stock = gestionnaireStock.getStockByProduitEtDepot(db, produitCourant, depotSelectionne);
                            Retour_Ligne retour_ligne = new Retour_Ligne(retourLigneID, retour.get_UID(), (int) stock.getQuantite_Actuelle(), produitCourant.getID_produit(), produitCourant.getRef_fourni(), produitCourant.getFournisseur(), (int) produitCourant.getPrix_unitaire(), (int) produitCourant.getTaux_de_TVA(), produitCourant.getDesignation_interne(), (int) stock.getQuantite_Actuelle(), 0);
                            listRetourLigne.add(retour_ligne);

                        }
                        present = false;
                    }


                    Iterator<Integer> iterator = listeProduit.iterator();
                    while (iterator.hasNext()) {
                        Integer i = iterator.next();
                        if (listeRetourBack.indexOf(i) == -1) {
                            iterator.remove();

                            Iterator<Retour_Ligne> retour_ligneIterator = listRetourLigne.iterator();
                            while (retour_ligneIterator.hasNext()) {
                                Retour_Ligne retour_ligne = retour_ligneIterator.next();
                                if (retour_ligne.getCode_produit() == i) {
                                    retour_ligneIterator.remove();
                                }
                            }
                        }
                    }

                    //Trier la liste
                    Collections.sort(listRetourLigne, new Comparator<Retour_Ligne>() {
                        @Override
                        public int compare(Retour_Ligne o1, Retour_Ligne o2) {
                            return o1.getProduit_Designation().compareTo(o2.getProduit_Designation());
                        }
                    });
                } else {
                    DetailRetourDemandeActivity.this.finish();
                }

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuEdit).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuEdit);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuEditClick();
                return true;
            }
        });
        return true;
    }

    private void onMenuEditClick() {
        onBackPressed();
    }


    // Transformation de la date choisi au format voulu
    private void updateLabel(TextView dateTextView) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        dateRepriseTextView.setText(sdf.format(date.getTime()));
        retour.setDate_Reprise(sdf.format(date.getTime()));
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        //gestion du commentaire pour ne pas effacer l'édit text pendant la rotation
        if (commentaireEditText.getText() != null) {
            outstate.putString("Commentaire", String.valueOf(commentaireEditText.getText()));
        }

        //gestion de la date pour garder la date sélectionnée en cas de changement
        if (dateRepriseTextView.getText() != null) {
            outstate.putString("DateReprise", String.valueOf(dateRepriseTextView.getText()));
        }

        //gestion de la liste de retour ligne pour gérer le changement de valeur
        outstate.putSerializable("listeRetourLigne", (Serializable) listRetourLigne);

        outstate.putInt("nombreElement", adapter.viewHolders.size());


        super.onSaveInstanceState(outstate);
    }

    // Permet de supprimer un élément de la liste
    public void supprimerRetourLigne(RetourDemande_detailAdapter.RetourLigneViewHolder viewHolder) {
        viewHolderAModifier = viewHolder;
        listRetourLigne.remove(adapter.viewHolders.indexOf(viewHolderAModifier));
        listeProduit.remove(adapter.viewHolders.indexOf(viewHolderAModifier));
        if (listRetourLigne.size() == 0) {
            listeProduit = new ArrayList<>();
            onBackPressed();
        } else {
            ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(listRetourLigne.size()));
            onResume();
        }
    }
}
