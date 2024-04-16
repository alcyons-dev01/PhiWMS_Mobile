package fr.alcyons.phiwms_mobile.Destruction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_RetourMotif;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailDestructionActivity extends ServiceActivity {

    Retour retourSelectionne;

    List<Retour_Ligne> listeRetourLignes;
    List<Retour_Ligne> listeRetourFranceMVO;
    ListView listViewRetourLignes;
    Retour_Ligne_DestructionAdapter adapter;

    EditText commentaire;

    Serialisation serialisation;
    // Définition de l'action a réalisé au Click sur le bouton Save
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int compteurReussite = 0;

            //MAJ du User qui as mis à jour le retour
            retourSelectionne.setSYS_USER_MAJ(utilisateurConnecte.getIdentifiant());

            String motif = retourSelectionne.getMotif();
            if(motif.contentEquals(""))
            {
                List<PH_RetourMotif> ph_retourMotifListe = gestionnairePH_RetourMotif.getAllPH_RetourMotif(db);
                List<String> retourMotifStringList = new ArrayList<>();
                for (PH_RetourMotif ph_retourMotif : ph_retourMotifListe) {
                    retourMotifStringList.add(ph_retourMotif.getMotifRetour());
                }
                motif = Alerte.afficherAlerteListView(DetailDestructionActivity.this, "Sélectionner le motif", retourMotifStringList);
            }

            // On vérifie que le motif est valide
            if (motif == null) {
                Alerte.afficherAlerte(DetailDestructionActivity.this, "Alerte", "Motif invalide", "alerte");
                return;
            }

            retourSelectionne.setMotif(motif.trim());

            boolean confirmation_destruction = Alerte.afficherAlerte(DetailDestructionActivity.this, "Confirmation", "Confirmez-vous la destruction du produit demandé ?", "OuiNon");

            if(confirmation_destruction)
            {
                //Création de l'action utilisateur
                Random random = new Random();
                int actionId = random.nextInt();
                if(actionId > 0)
                    actionId= actionId*-1;
                SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateDestruction =new Date();
                String date_string = parseFormat.format(dateDestruction);
                ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retourSelectionne.get_UID(), "", "Destruction");
                ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
                //fin de la création de l'action utilisateur

                //vérification du réseau
                boolean differe = false;
                for (Retour_Ligne retourLigne : adapter.retour_Lignes) {
                    Produit produit  = ProduitOpenHelper.getProduitByID(db,retourLigne.getCode_produit());

                    String gtin = produit.getGTIN();
                    if(gtin.length() > 14)
                    {
                        gtin = gtin.substring(2);
                    }

                    //changement format date
                    String peremption = "";
                    Date peremption_temp = null;
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat output = new SimpleDateFormat("yyMMdd");
                    try {
                        peremption_temp = input.parse(retourLigne.getPeremptionDate().toString());                 // parse input
                        peremption = output.format(peremption_temp);    // format output
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(!retourLigne.getSerie_Retourner().contentEquals("null") && !retourLigne.getSerie_Retourner().contentEquals(""))
                    {
                        long serilisation_uid = serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin, "GTIN", retourLigne.getLot_Retourner(), peremption, retourLigne.getSerie_Retourner(), "Destruction", String.valueOf(retourLigne.get_UID()), "", "");
                        PH_Serialisation serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int)serilisation_uid);
                        if(serialisation_courant.getResultat().contentEquals("ACTIVE"))
                        {
                            listeRetourFranceMVO.add(retourLigne);
                        }
                        else if(serialisation_courant.getResultat().contentEquals("INACTIVE"))
                        {
                            long serialisation_uid = serialisation.Serialisation_Deserialiser(utilisateurConnecte.getId(), false, differe, serialisation_courant.getRaison(), produit.getGTIN(), "GTIN", retourLigne.getLot(), retourLigne.getPeremptionDate(), retourLigne.getSerie_Retourner(), "Retour", String.valueOf(retourLigne.get_UID()));
                            PH_Serialisation new_ph = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) serialisation_uid);
                            if(new_ph.getResultat().contentEquals("ACTIVE"))
                            {
                                listeRetourFranceMVO.add(retourLigne);
                            }
                        }
                    }

                    retourLigne.setQte_Retourner(retourLigne.getQte_Retourner() == 0 ? retourLigne.getQte_Demander() : retourLigne.getQte_Retourner());

                    long rowID = gestionnaireRetour_Ligne.mettreAJourUnRetourLigne(db, retourLigne);
                    if (rowID != -1) {
                        compteurReussite++;
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.getPhiMR4UUID(), retourLigne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                    }

                    //gestion des actions lignes
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if(actionligneId > 0)
                        actionligneId= actionligneId*-1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int)retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }

                // Si tous les retoursLignes ont bien été mis à jour, on met à jour le retour
                if (compteurReussite == adapter.retour_Lignes.size()) {
                    String intitulé = retourSelectionne.getIntitule();
                    retourSelectionne.setIntitule(intitulé.replace(getString(R.string.DestructionDemandée), getString(R.string.DestructionEffectuée)));
                    retourSelectionne.setEn_Attente_de(getString(R.string.DestructionEffectuée));
                    retourSelectionne.setCommentaire(commentaire.getText().toString().trim());
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    retourSelectionne.setDate_retour(dateFormat.format(date));


                    long rowID = gestionnaireRetour.mettreAJourRetour(db, retourSelectionne);
                    if (rowID != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                    } else {
                        compteurReussite = 0;
                    }
                }

                //on envoie les requetes à France MVO pour les produits où c'est possible
                if(listeRetourFranceMVO.size() != 0)
                {
                    for(Retour_Ligne retour_ligne_courant : listeRetourFranceMVO)
                    {
                        Produit produit_courant = ProduitOpenHelper.getProduitByID(db, retour_ligne_courant.getCode_produit());
                        String gtin = produit_courant.getGTIN();
                        if(gtin.length() > 14)
                        {
                            gtin = gtin.substring(2);
                        }

                        //changement format date
                        String peremption = "";
                        Date peremption_temp = null;
                        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat output = new SimpleDateFormat("yyMMdd");
                        try {
                            peremption_temp = input.parse(retour_ligne_courant.getPeremptionDate().toString());                 // parse input
                            peremption = output.format(peremption_temp);    // format output
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long serialisation_uid = serialisation.Serialisation_Serialiser(utilisateurConnecte.getId(), false, false, "DESTROYED", gtin, "GTIN", retour_ligne_courant.getLot(), peremption, retour_ligne_courant.getSerie_Retourner(), "Destruction", String.valueOf(retour_ligne_courant.get_UID()));
                    }
                }

                // Si une erreur est survenue, on annule tout
                if (compteurReussite != adapter.retour_Lignes.size()) {
                    Alerte.afficherAlerte(DetailDestructionActivity.this, "Alerte", "une erreur est survenue, aucun traitement ne sera effectué", "alerte");
                    gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                    DetailDestructionActivity.this.finish();
                    return;
                }

                // Si possible, on tente de tout mettre à jour en BDD distante directement
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                Toast.makeText(DetailDestructionActivity.this, "Destruction effectuée", Toast.LENGTH_SHORT).show();
                if (OutilsGestionConnexionReseau.isServerAccessible(DetailDestructionActivity.this)) {
                    gestionnaireElementASynchroniser.toutSynchroniser(DetailDestructionActivity.this, db, utilisateurConnecte, true);
                }

                DetailDestructionActivity.this.finish();
            }
            else
            {
                return;
            }
        }
    };
    FloatingActionButton boutonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_destruction);

        //gestion de la sérialisation
        serialisation = new Serialisation(DetailDestructionActivity.this, db, utilisateurConnecte);
        listeRetourFranceMVO = new ArrayList<>();

        // Récupération des variables globales
        retourSelectionne = gestionnaireRetour.getRetourByID(db, intent.getExtras().getInt("retourSelectionneID"));
        commentaire = (EditText) findViewById(R.id.commentaire);

        // Affichage des constantes
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitule().trim());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero().trim());

        // Récupération du bouton d'enregistrement
        boutonSave = (FloatingActionButton) findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);

        // Gestion de la ListView
        listViewRetourLignes = (ListView) findViewById(R.id.listeView);
        listViewRetourLignes.setDivider(footer);
        listViewRetourLignes.setItemsCanFocus(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Récupération en BDD locale de la liste des retourLignes
        listeRetourLignes = gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retourSelectionne);
        adapter = new Retour_Ligne_DestructionAdapter(DetailDestructionActivity.this, listeRetourLignes);
        listViewRetourLignes.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent detailDestructionIntent = new Intent(DetailDestructionActivity.this, ServiceDestructionActivity.class);
        Bundle detailDestructionBundle = super.getBundle();
        detailDestructionIntent.putExtras(detailDestructionBundle);
        DetailDestructionActivity.this.startActivity(detailDestructionIntent);
        DetailDestructionActivity.this.finish();
    }
}
