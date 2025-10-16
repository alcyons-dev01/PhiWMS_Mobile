package fr.alcyons.phiwms_mobile.Destruction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.github.clans.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_RetourMotifOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_RetourMotif;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourFournisseur.DetailRetourFournisseurActivity;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceDestructionActivity;

public class DetailDestructionActivity extends ServiceActivity {
    Retour retourSelectionne;
    List<Retour_Ligne> listeRetourLignes;
    ListView listViewRetourLignes;
    Retour_Ligne_DestructionAdapter adapter;
    Serialisation serialisation;
    String commentaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_destruction);

        //gestion de la sérialisation
        serialisation = new Serialisation(DetailDestructionActivity.this, db, utilisateurConnecte);

        // Récupération des variables globales
        retourSelectionne = RetourOpenHelper.getRetourByID(db, Objects.requireNonNull(intent.getExtras()).getInt("retourSelectionneID"));

        // Affichage des constantes
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitule().trim());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero().trim());

        // Gestion de la ListView
        listViewRetourLignes = findViewById(R.id.listeView);
        //listViewRetourLignes.setDivider(footer);
        listViewRetourLignes.setItemsCanFocus(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent detailDestructionIntent = new Intent(DetailDestructionActivity.this, ServiceDestructionActivity.class);
                Bundle detailDestructionBundle = DetailDestructionActivity.super.getBundle();
                detailDestructionIntent.putExtras(detailDestructionBundle);
                DetailDestructionActivity.this.startActivity(detailDestructionIntent);
                DetailDestructionActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // Récupération en BDD locale de la liste des retourLignes
        listeRetourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne);
        adapter = new Retour_Ligne_DestructionAdapter(DetailDestructionActivity.this, listeRetourLignes);
        listViewRetourLignes.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(item1 -> {
            afficherModaleCommentaire(DetailDestructionActivity.this, getLayoutInflater());
            return true;
        });
        return true;
    }

    private void afficherModaleCommentaire(Context context, LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_commentaire, null);
        TextView titre = layout.findViewById(R.id.messageFin);
        EditText editCommentaire = layout.findViewById(R.id.commentaire);
        LinearLayout buttonAnnuler = layout.findViewById(R.id.buttonAnnuler);
        LinearLayout buttonValider = layout.findViewById(R.id.buttonOk);
        titre.setText("Souhaitez-vous valider la destruction ?");
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();

        editCommentaire.setText(retourSelectionne.getCommentaire());

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentaire = editCommentaire.getText().toString();
                validerDestruction();
                alertDialog.dismiss();
            }
        });
    }

    private void validerDestruction() {
        int compteurReussite = 0;

        //MAJ du User qui as mis à jour le retour
        retourSelectionne.setSYS_USER_MAJ(utilisateurConnecte.getIdentifiant());

        String motif = retourSelectionne.getMotif();
        if (motif.contentEquals("")) {
            List<PH_RetourMotif> ph_retourMotifListe = PH_RetourMotifOpenHelper.getAllPH_RetourMotif(db);
            List<String> retourMotifStringList = new ArrayList<>();
            for (PH_RetourMotif ph_retourMotif : ph_retourMotifListe) {
                retourMotifStringList.add(ph_retourMotif.getMotifRetour());
            }
            motif = Alerte.afficherAlerteListView(DetailDestructionActivity.this, "Sélectionner le motif", retourMotifStringList);
        }
        if (motif == null) {
            Alerte.afficherAlerte(DetailDestructionActivity.this, "Alerte", "Motif invalide", "alerte");
            return;
        }

        retourSelectionne.setMotif(motif.trim());

        Random random = new Random();
        int actionId = random.nextInt();
        if (actionId > 0)
            actionId = actionId * -1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateDestruction = new Date();
        String date_string = parseFormat.format(dateDestruction);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", retourSelectionne.get_UID(), "", "Destruction");
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
            if (actionligneId > 0)
                actionligneId = actionligneId * -1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int) retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
        }

        // Si tous les retoursLignes ont bien été mis à jour, on met à jour le retour
        if (compteurReussite == adapter.retour_Lignes.size()) {
            String intitule = retourSelectionne.getIntitule();
            retourSelectionne.setIntitule(intitule.replace(getString(R.string.DestructionDemandee), getString(R.string.DestructionEffectuee)));
            retourSelectionne.setEn_Attente_de(getString(R.string.DestructionEffectuee));
            retourSelectionne.setCommentaire(commentaire);
            Date date = new Date();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            retourSelectionne.setDate_retour(dateFormat.format(date));


            long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);
            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                compteurReussite = 0;
            }
        }
        // Si une erreur est survenue, on annule tout
        if (compteurReussite != adapter.retour_Lignes.size()) {
            Alerte.afficherAlerte(DetailDestructionActivity.this, "Alerte", "une erreur est survenue, aucun traitement ne sera effectué", "alerte");
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
            DetailDestructionActivity.this.finish();
            return;
        }

        // Si possible, on tente de tout mettre à jour en BDD distante directement
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        Toast.makeText(DetailDestructionActivity.this, "Destruction effectuée", Toast.LENGTH_SHORT).show();
        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailDestructionActivity.this, db, utilisateurConnecte, true);
        }

        Intent detailDestructionIntent = new Intent(DetailDestructionActivity.this, ServiceDestructionActivity.class);
        Bundle detailDestructionBundle = DetailDestructionActivity.super.getBundle();
        detailDestructionIntent.putExtras(detailDestructionBundle);
        DetailDestructionActivity.this.startActivity(detailDestructionIntent);
        DetailDestructionActivity.this.finish();
    }
}
