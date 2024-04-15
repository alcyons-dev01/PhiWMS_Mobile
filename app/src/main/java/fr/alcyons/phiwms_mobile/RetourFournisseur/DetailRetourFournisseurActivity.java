package fr.alcyons.phiwms_mobile.RetourFournisseur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourFournisseurAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailRetourFournisseurActivity extends ServiceActivity {

    Retour retourSelectionne;

    List<Retour_Ligne> listeRetourLignes;
    ListView listViewRetourLignes;
    Retour_Ligne_RetourFournisseurAdapter adapter;
    String signatureName;

    EditText commentaire;
    Dialogue dialogue;
    View.OnClickListener onClickListenerValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int compteurReussite = 0;

            //recuperation de la signature
            String content = "";
            Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
            String nom_chauffeur = dialogue.edit_nom_chauffeur.getText().toString();
            String prenom_chauffeur = dialogue.edit_prenom_chauffeur.getText().toString();
            String transporteur = dialogue.edit_transporteur.getText().toString();

            if(nom_chauffeur.contentEquals("") || transporteur.contentEquals(""))
            {
                Alerte.afficherAlerte(DetailRetourFournisseurActivity.this, "Erreur", "Veuillez saisir toutes les informations demandées s'il vous plaît", "alerte");
                return;
            }

            //fermer le clavier
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            if(bitmap != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String img_str = Base64.encodeToString(byteArray, Base64.DEFAULT);
                retourSelectionne.setSignature_Chauffeur(img_str);
            }

            retourSelectionne.setNom_Chauffeur(nom_chauffeur);
            retourSelectionne.setPrenom_Chauffeur(prenom_chauffeur);
            retourSelectionne.setTransporteur(transporteur);

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDay =new Date();
            String date_string = parseFormat.format(dateDay);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retourSelectionne.get_UID(), "", "Retour Frs");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            //fin de la création de l'action utilisateur

            // Mise à jour des Retour_Ligne
            for (Retour_Ligne retourLigne : adapter.retour_Lignes) {
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

            // Si tout est ok mise à jour du Retour
            if (compteurReussite == adapter.retour_Lignes.size()) {
                retourSelectionne.setCommentaire(commentaire.getText().toString().trim());
                String intitulé = retourSelectionne.getIntitulé().replace(getString(R.string.RetourFRSDemandé), getString(R.string.RetourFRSEffectué));
                retourSelectionne.setIntitulé(intitulé);
                retourSelectionne.setEn_Attente_de(getString(R.string.RetourFRSEffectué));
                retourSelectionne.setRef_Depot_Dest("Retour Fournisseur");
                retourSelectionne.setStatut(getString(R.string.statutEncours));
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                retourSelectionne.setDate_retour(format.format(date));

                long rowID = gestionnaireRetour.mettreAJourRetour(db, retourSelectionne);
                if (rowID != -1) {
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussite = 0;
                }
            }

            if (compteurReussite != adapter.retour_Lignes.size()) {
                Alerte.afficherAlerte(DetailRetourFournisseurActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                DetailRetourFournisseurActivity.this.finish();
                return;
            }

            Toast.makeText(DetailRetourFournisseurActivity.this, "Retour fournisseur effectué", Toast.LENGTH_SHORT).show();
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            if (OutilsGestionConnexionReseau.isServerAccessible(DetailRetourFournisseurActivity.this)) {
                gestionnaireElementASynchroniser.toutSynchroniser(DetailRetourFournisseurActivity.this, db, utilisateurConnecte, true);
            }

            DetailRetourFournisseurActivity.this.finish();
        }
    };



    // Permet de réaliser le traitement des produits qui doivent retourner aux fournisseurs
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogue = new Dialogue(DetailRetourFournisseurActivity.this, onClickListenerValider, utilisateurConnecte);
            dialogue.signaturePadOpenFournisseur();
        }
    };
    FloatingActionButton boutonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_fournisseur);

        // Récupération du Retour grâce à la variable globale
        retourSelectionne = gestionnaireRetour.getRetourByID(db, intent.getExtras().getInt("retourSelectionneID"));

        // Récupération des objets graphiques
        commentaire = (EditText) findViewById(R.id.commentaire);

        // Affichage des informations de base
        String[] provenance_tab = retourSelectionne.getIntitulé().split(":");
        String origine_retour = provenance_tab[0];
        ((TextView) findViewById(R.id.depotOrigine)).setText(origine_retour);
        ((TextView) findViewById(R.id.fournisseurDestinataire)).setText(retourSelectionne.getRef_Depot_Dest());
        ((TextView) findViewById(R.id.numero)).setText("#"+retourSelectionne.getNumero());
        commentaire.setText(retourSelectionne.getCommentaire());

        // Récupération et initialisation du bouton de sauvegarde
        boutonSave = (FloatingActionButton) findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);

        // Gestion de la listView
        listViewRetourLignes = (ListView) findViewById(R.id.listeView);
        listViewRetourLignes.setDivider(footer);
        listViewRetourLignes.setItemsCanFocus(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        listeRetourLignes = gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retourSelectionne);

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Retour_Ligne_RetourFournisseurAdapter(DetailRetourFournisseurActivity.this, listeRetourLignes);
        listViewRetourLignes.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent detailRetourFournisseurIntent = new Intent(DetailRetourFournisseurActivity.this, ServiceRetourFournisseurActivity.class);
        Bundle detailRetourFournisseurBundle = super.getBundle();
        detailRetourFournisseurIntent.putExtras(detailRetourFournisseurBundle);
        DetailRetourFournisseurActivity.this.startActivity(detailRetourFournisseurIntent);
        DetailRetourFournisseurActivity.this.finish();
    }
}
