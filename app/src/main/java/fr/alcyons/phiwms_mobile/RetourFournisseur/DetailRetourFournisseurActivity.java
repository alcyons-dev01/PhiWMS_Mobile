package fr.alcyons.phiwms_mobile.RetourFournisseur;

import android.annotation.SuppressLint;
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

import androidx.activity.OnBackPressedCallback;

import com.github.clans.fab.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourFournisseurAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourPUI.ServiceRetourPUIActivity;
import fr.alcyons.phiwms_mobile.ServiceActivity;

/**
 * Created by olivier on 16/04/2024.
 */

public class DetailRetourFournisseurActivity extends ServiceActivity {
    Retour retourSelectionne;
    List<Retour_Ligne> listeRetourLignes;
    ListView listViewRetourLignes;
    Retour_Ligne_RetourFournisseurAdapter adapter;
    EditText commentaire;
    Dialogue dialogue;
    View.OnClickListener onClickListenerValider = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int compteurReussite = 0;

            Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
            String nom_chauffeur = dialogue.edit_nom_chauffeur.getText().toString();
            String prenom_chauffeur = dialogue.edit_prenom_chauffeur.getText().toString();
            String transporteur = dialogue.edit_transporteur.getText().toString();

            if(nom_chauffeur.contentEquals("") || transporteur.contentEquals(""))
            {
                Alerte.afficherAlerte(DetailRetourFournisseurActivity.this, "Erreur", "Veuillez saisir toutes les informations demandées s'il vous plaît", "alerte");
                return;
            }

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

            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateDay =new Date();
            String date_string = parseFormat.format(dateDay);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retourSelectionne.get_UID(), "", "Retour Frs");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

            for (Retour_Ligne retourLigne : adapter.retour_Lignes) {
                retourLigne.setQte_Retourner(retourLigne.getQte_Retourner() == 0 ? retourLigne.getQte_Demander() : retourLigne.getQte_Retourner());

                long rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
                if (rowID != -1) {
                    compteurReussite++;
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.getPhiMR4UUID(), retourLigne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                }

                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int)retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            }

            if (compteurReussite == adapter.retour_Lignes.size()) {
                retourSelectionne.setCommentaire(commentaire.getText().toString().trim());
                String intitule = retourSelectionne.getIntitule().replace(getString(R.string.RetourFRSDemande), getString(R.string.RetourFRSEffectue));
                retourSelectionne.setIntitule(intitule);
                retourSelectionne.setEn_Attente_de(getString(R.string.RetourFRSEffectue));
                retourSelectionne.setRef_Depot_Dest("Retour Fournisseur");
                retourSelectionne.setStatut(getString(R.string.statutEncours));
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                retourSelectionne.setDate_retour(format.format(date));

                long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);
                if (rowID != -1) {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussite = 0;
                }
            }

            if (compteurReussite != adapter.retour_Lignes.size()) {
                Alerte.afficherAlerte(DetailRetourFournisseurActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
                ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
                DetailRetourFournisseurActivity.this.finish();
                return;
            }

            Toast.makeText(DetailRetourFournisseurActivity.this, "Retour fournisseur effectué", Toast.LENGTH_SHORT).show();
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            if (statutConnexion) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailRetourFournisseurActivity.this, db, utilisateurConnecte, true);
            }

            Intent detailRetourFournisseurIntent = new Intent(DetailRetourFournisseurActivity.this, ServiceRetourFournisseurActivity.class);
            Bundle detailRetourFournisseurBundle = DetailRetourFournisseurActivity.super.getBundle();
            detailRetourFournisseurIntent.putExtras(detailRetourFournisseurBundle);
            DetailRetourFournisseurActivity.this.startActivity(detailRetourFournisseurIntent);
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_fournisseur);
        retourSelectionne = RetourOpenHelper.getRetourByID(db, Objects.requireNonNull(intent.getExtras()).getInt("retourSelectionneID"));
        commentaire = findViewById(R.id.commentaire);

        String[] provenance_tab = retourSelectionne.getIntitule().split(":");
        String origine_retour = provenance_tab[0];
        ((TextView) findViewById(R.id.depotOrigine)).setText(origine_retour);
        ((TextView) findViewById(R.id.fournisseurDestinataire)).setText(retourSelectionne.getRef_Depot_Dest());
        ((TextView) findViewById(R.id.numero)).setText("#"+retourSelectionne.getNumero());
        commentaire.setText(retourSelectionne.getCommentaire());
        boutonSave = findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);

        // Gestion de la listView
        listViewRetourLignes = findViewById(R.id.listeView);
        listViewRetourLignes.setDivider(footer);
        listViewRetourLignes.setItemsCanFocus(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent detailRetourFournisseurIntent = new Intent(DetailRetourFournisseurActivity.this, ServiceRetourFournisseurActivity.class);
                Bundle detailRetourFournisseurBundle = DetailRetourFournisseurActivity.super.getBundle();
                detailRetourFournisseurIntent.putExtras(detailRetourFournisseurBundle);
                DetailRetourFournisseurActivity.this.startActivity(detailRetourFournisseurIntent);
                DetailRetourFournisseurActivity.this.finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        listeRetourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne);
        adapter = new Retour_Ligne_RetourFournisseurAdapter(DetailRetourFournisseurActivity.this, listeRetourLignes);
        listViewRetourLignes.setAdapter(adapter);
    }
}
