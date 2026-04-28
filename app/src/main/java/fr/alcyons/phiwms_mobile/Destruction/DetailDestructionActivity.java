package fr.alcyons.phiwms_mobile.Destruction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_RetourMotif;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_DestructionAdapter;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceDestructionActivity;

public class DetailDestructionActivity extends ServiceActivity
{
    // OTHERS
    private String commentaire = null;
    private Retour retourSelectionne = null;
    private List<Retour_Ligne> listeRetourLignes = null;

    // UI
    private ListView listViewRetourLignes = null;
    private Retour_Ligne_DestructionAdapter adapter = null;

    @Override protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_detail_destruction);

        // Récupération des variables globales
        this.retourSelectionne = RetourOpenHelper.getRetourByID(this.db, Objects.requireNonNull(this.intent.getExtras()).getInt("retourSelectionneID"));

        // Affichage des constantes
        ((TextView) this.findViewById(R.id.intitule)).setText(this.retourSelectionne.getIntitule().trim());
        ((TextView) this.findViewById(R.id.numero)).setText(this.retourSelectionne.getNumero().trim());

        // Gestion de la ListView
        this.listViewRetourLignes = this.findViewById(R.id.listeView);
        //listViewRetourLignes.setDivider(footer);
        this.listViewRetourLignes.setItemsCanFocus(true);

        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override public void handleOnBackPressed()
            {
                final Intent detailDestructionIntent = new Intent(DetailDestructionActivity.this, ServiceDestructionActivity.class);
                final Bundle detailDestructionBundle = DetailDestructionActivity.super.getBundle();
                detailDestructionIntent.putExtras(detailDestructionBundle);
                DetailDestructionActivity.this.startActivity(detailDestructionIntent);
                DetailDestructionActivity.this.finish();
            }
        });
    }

    @Override public void onResume()
    {
        super.onResume();
        this.invalidateOptionsMenu();

        // Récupération en BDD locale de la liste des retourLignes
        this.listeRetourLignes = Retour_LigneOpenHelper.getAllRetourLignesByRetour(this.db, this.retourSelectionne);
        this.adapter = new Retour_Ligne_DestructionAdapter(DetailDestructionActivity.this, this.listeRetourLignes);
        this.listViewRetourLignes.setAdapter(this.adapter);
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        menu.findItem(R.id.menuCommentaire).setVisible(true);

        return true;
    }

    @Override public boolean onPrepareOptionsMenu(final Menu menu)
    {
        final MenuItem item = menu.findItem(R.id.menuSaveCircle);
        item.setOnMenuItemClickListener(item1 -> {
            Alerte.afficherAlerteSaisieText(DetailDestructionActivity.this, this.getLayoutInflater(), "Validation destruction", "Souhaitez-vous valider la destruction ?", "Ajouter un commentaire...");
            return true;
        });

        final MenuItem item_commentaire = menu.findItem(R.id.menuCommentaire);

        if(this.retourSelectionne.getCommentaire().contentEquals(""))
        {
            item_commentaire.getIcon().mutate().setAlpha(50);
            item_commentaire.setOnMenuItemClickListener(null);
        }
        else
        {
            item_commentaire.getIcon().mutate().setAlpha(255);
            item_commentaire.setOnMenuItemClickListener(item1-> {
                Alerte.afficherAlerteInformation(DetailDestructionActivity.this, this.getLayoutInflater(), "Commentaire", this.retourSelectionne.getCommentaire(), false, false);
                return true;
            });
        }

        return true;
    }

    @Override public void retourSaisieText(final String text)
    {
        this.commentaire = text;
        this.validerDestruction();
    }

    private void validerDestruction()
    {
        int compteurReussite = 0;

        //MAJ du User qui as mis à jour le retour
        this.retourSelectionne.setSYS_USER_MAJ(this.utilisateurConnecte.getIdentifiant());

        String motif = this.retourSelectionne.getMotif();
        if (motif.contentEquals(""))
        {
            final List<PH_RetourMotif> ph_retourMotifListe = PH_RetourMotifOpenHelper.getAllPH_RetourMotif(this.db);
            final List<String> retourMotifStringList = new ArrayList<>();
            for (final PH_RetourMotif ph_retourMotif : ph_retourMotifListe) { retourMotifStringList.add(ph_retourMotif.getMotifRetour()); }
            motif = Alerte.afficherAlerteListView(DetailDestructionActivity.this, "Sélectionner le motif", retourMotifStringList);
        }

        if (null == motif)
        {
            Alerte.afficherAlerteInformation(DetailDestructionActivity.this, this.getLayoutInflater(), "Alerte", "Motif invalide", false, false);
            return;
        }

        this.retourSelectionne.setMotif(motif.trim());

        final Random random = new Random();
        int actionId = random.nextInt();
        if (0 < actionId) actionId = actionId * -1;

        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date dateDestruction = new Date();
        final String date_string = parseFormat.format(dateDestruction);

        final ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, this.utilisateurConnecte.getId(), date_string, this.serviceActuel.getId(), this.utilisateurConnecte.getEtablissementId(), "En attente", this.retourSelectionne.get_UID(), "", "Destruction");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(this.db, new_action_utilisateur);
        for (final Retour_Ligne retourLigne : this.adapter.retour_Lignes)
        {
            retourLigne.setQte_Retourner((double) 0 == retourLigne.getQte_Retourner() ? retourLigne.getQte_Demander() : retourLigne.getQte_Retourner());

            final long rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(this.db, retourLigne);
            if (-1L != rowID)
            {
                compteurReussite++;
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.getPhiMR4UUID(), retourLigne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
            }

            final Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if (0 < actionligneId) actionligneId = actionligneId * -1;

            final ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int) retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(this.db, actionUtilisateur_ligne);
        }

        // Si tous les retoursLignes ont bien été mis à jour, on met à jour le retour
        if (compteurReussite == this.adapter.retour_Lignes.size())
        {
            final String intitule = this.retourSelectionne.getIntitule();
            this.retourSelectionne.setIntitule(intitule.replace(this.getString(R.string.DestructionDemandee), this.getString(R.string.DestructionEffectuee)));
            this.retourSelectionne.setEn_Attente_de(this.getString(R.string.DestructionEffectuee));
            this.retourSelectionne.setCommentaire(this.commentaire);

            final Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            this.retourSelectionne.setDate_retour(dateFormat.format(date));


            final long rowID = RetourOpenHelper.mettreAJourRetour(this.db, this.retourSelectionne);
            if (-1L != rowID) { ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, RetourOpenHelper.Constantes.TABLE_RETOUR, this.retourSelectionne.getPhiMR4UUID(), this.retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ); }
            else { compteurReussite = 0; }
        }

        // Si une erreur est survenue, on annule tout
        if (compteurReussite != this.adapter.retour_Lignes.size())
        {
            Alerte.afficherAlerteInformation(DetailDestructionActivity.this, this.getLayoutInflater(), "Alerte", "une erreur est survenue, aucun traitement ne sera effectué", false, false);
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(this.db);
            DetailDestructionActivity.this.finish();
            return;
        }

        // Si possible, on tente de tout mettre à jour en BDD distante directement
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(this.db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        Toast.makeText(DetailDestructionActivity.this, "Destruction effectuée", Toast.LENGTH_SHORT).show();
        if (MainActivity.statutConnexion) { ElementASynchroniserOpenHelper.toutSynchroniser(DetailDestructionActivity.this, this.db, this.utilisateurConnecte, true); }

        final Intent detailDestructionIntent = new Intent(DetailDestructionActivity.this, ServiceDestructionActivity.class);
        final Bundle detailDestructionBundle = DetailDestructionActivity.super.getBundle();
        detailDestructionIntent.putExtras(detailDestructionBundle);
        DetailDestructionActivity.this.startActivity(detailDestructionIntent);
        DetailDestructionActivity.this.finish();
    }
}
