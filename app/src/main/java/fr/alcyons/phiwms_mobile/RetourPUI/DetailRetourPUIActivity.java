package fr.alcyons.phiwms_mobile.RetourPUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_RetourPUI_Adapte;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_RetourPUIAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.VerrouPharmacie.DetailVerrouPharmacieActivity;
import fr.alcyons.phiwms_mobile.VerrouPharmacie.ServiceVerrouPharmacieActivity;

import static fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS;

public class  DetailRetourPUIActivity extends ServiceActivity {

    Retour retourSelectionne;

    List<Retour_Ligne_RetourPUI_Adapte> retourLigneRetourPUIAdapteList;
    ListView retourLigneListView;
    Retour_Ligne_RetourPUIAdapter adapter;
    Retour_Ligne_RetourPUIAdapter.Retour_LigneViewHolder viewHolderAModifier;

    EditText commentaireEditText;
    // Permet de réaliser le traitement des produits qui doivent retourner à la PUI
    public View.OnClickListener clicBoutonSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int compteurReussiteGlobale = 0;
            int compteurReussiteRetour = 0;

            // On vérifie que chaque élément a au moins un emplacement de retour
            for (Retour_Ligne_RetourPUI_Adapte retour_ligneAdapte : adapter.retourLigneRetourPUIAdapteList)
            {
                if (retour_ligneAdapte.getEmplacementAdaptes().size() < 1) {
                    Alerte.afficherAlerte(DetailRetourPUIActivity.this, "Alerte", "Tous les éléments n'ont pas d'emplacement de retour.", "alerte");
                    return;
                }
            }

            //Création de l'action utilisateur
            Random random = new Random();
            int actionId = random.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =new Date();
            String date_string = parseFormat.format(date);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retourSelectionne.get_UID(), "", "Retour PUI");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            //fin de la création de l'action utilisateur

            // Mise à jour des Retour_Ligne
            for (Retour_Ligne_RetourPUI_Adapte retour_ligneAdapte : adapter.retourLigneRetourPUIAdapteList) {
                Retour_Ligne retourLigneCorrespondant = gestionnaireRetour_Ligne.getRetourLigneByID(db, retour_ligneAdapte.getRetourLigneID());

                int compteurReussiteParRetourLigne = 0;

                for (Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapteCourant : retour_ligneAdapte.getEmplacementAdaptes())
                {
                    Depot_Emplacement emplacementCourant = gestionnaireEmplacement.getUnEmplacementByID(db, emplacementAdapteCourant.getEmplacementID());
                    Depot_Zone zoneCourant = gestionnaireZone.getUneZoneByID(db, emplacementCourant.getZoneID());

                    Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneCorrespondant);
                    retourLigneCourant.set_UID(retourLigneCorrespondant.get_UID()*-1);
                    if(zoneCourant != null)
                    {
                        retourLigneCourant.setRetourPUI_Zone(zoneCourant.getZoneName().trim());
                    }
                    else
                    {
                        retourLigneCourant.setRetourPUI_Zone("");
                    }

                    if(emplacementAdapteCourant != null)
                    {
                        retourLigneCourant.setRetourPUI_Emplacement(emplacementCourant.getAdressage().trim());
                    }
                    else
                    {
                        retourLigneCourant.setRetourPUI_Emplacement("");
                    }
                    retourLigneCourant.setQte_Retourner(emplacementAdapteCourant.getQte());

                    long rowID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
                    if (rowID != -1) {
                        compteurReussiteGlobale++;
                        compteurReussiteParRetourLigne++;
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneCourant.getPhiMR4UUID(), retourLigneCourant.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                    }
                }

                if (compteurReussiteParRetourLigne == retour_ligneAdapte.getEmplacementAdaptes().size()) {
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneCorrespondant.getPhiMR4UUID(), retourLigneCorrespondant.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.SUPPR);
                    gestionnaireRetour_Ligne.supprimerUnRetourLigne(db, retourLigneCorrespondant);
                } else {
                    compteurReussiteGlobale = 0;
                }

                //gestion des actions lignes
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigneCorrespondant.get_UID(), "", 0, (int)retourLigneCorrespondant.getQte_Retourner(), retourLigneCorrespondant.getProduit_Designation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                compteurReussiteRetour++;
            }

            // Si tout est ok mise à jour du Retour
            if (compteurReussiteGlobale == adapter.retourLigneRetourPUIAdapteList.size()) {
                String intitulé = retourSelectionne.getIntitulé();
                intitulé = intitulé.replace(getString(R.string.RetourPUIDemande), getString(R.string.RetourPUIEffectué));
                retourSelectionne.setIntitulé(intitulé.trim());
                retourSelectionne.setEn_Attente_de(getString(R.string.RetourPUIEffectué));
                retourSelectionne.setCommentaire(commentaireEditText.getText().toString().trim());
                long rowID = gestionnaireRetour.mettreAJourRetour(db, retourSelectionne);
                if (rowID != -1) {
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                } else {
                    compteurReussiteGlobale = 0;
                }
            }

            if (compteurReussiteGlobale != adapter.retourLigneRetourPUIAdapteList.size()) {
                Alerte.afficherAlerte(DetailRetourPUIActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                DetailRetourPUIActivity.this.finish();
                return;
            }
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            Toast.makeText(DetailRetourPUIActivity.this, "Retour PUI effectué", Toast.LENGTH_SHORT).show();
            if (OutilsGestionConnexionReseau.isServerAccessible(DetailRetourPUIActivity.this)) {
                gestionnaireElementASynchroniser.toutSynchroniser(DetailRetourPUIActivity.this, db, utilisateurConnecte, true);
            }
            DetailRetourPUIActivity.this.finish();
        }
    };
    FloatingActionButton boutonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_pui);

        // Récupération du Retour grâce à la variable globale
        retourSelectionne = gestionnaireRetour.getRetourByID(db, intent.getExtras().getInt("retourSelectionneID"));

        // Récupération des objets graphiques
        commentaireEditText = (EditText) findViewById(R.id.commentaire);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitulé());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero());
        commentaireEditText.setText(retourSelectionne.getCommentaire());

        // Récupération et initialisation du bouton de sauvegarde
        boutonSave = (FloatingActionButton) findViewById(R.id.boutonSave);
        boutonSave.setOnClickListener(clicBoutonSave);

        // Gestion de la listView
        retourLigneListView = (ListView) findViewById(R.id.listeView);
        retourLigneListView.setItemsCanFocus(true);
        retourLigneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour_Ligne_RetourPUI_Adapte retourLigneAdapte = adapter.retourLigneRetourPUIAdapteList.get(position);
                Retour_Ligne retourLigne = gestionnaireRetour_Ligne.getRetourLigneByID(db, retourLigneAdapte.getRetourLigneID());

                viewHolderAModifier = adapter.viewHolderList.get(position);
                viewHolderAModifier.progressBar.setVisibility(View.VISIBLE);
                viewHolderAModifier.layoutPrincipal.setBackgroundColor(getResources().getColor(R.color.noirtransparent, null));

                Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ListeEmplacementRetourPUIActivity.class);
                Bundle detailRetourPUIBundle = DetailRetourPUIActivity.super.getBundle();
                detailRetourPUIBundle.putInt("produitID", retourLigne.getCode_produit());
                detailRetourPUIBundle.putSerializable("retourLigneAdapte", retourLigneAdapte);
                detailRetourPUIBundle.putInt("depotID", gestionnaireDepot.getDepotParReference(db, retourSelectionne.getRef_Depot_Dest()).getDepot_UID());
                detailRetourPUIIntent.putExtras(detailRetourPUIBundle);

                DetailRetourPUIActivity.this.startActivityForResult(detailRetourPUIIntent, RETOUR_LISTE_EMPLACEMENTS);
            }
        });

        retourLigneRetourPUIAdapteList = new ArrayList<>();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS:
                    Retour_Ligne_RetourPUI_Adapte retourLigneAdapte = (Retour_Ligne_RetourPUI_Adapte) data.getExtras().getSerializable("retourLigneAdapte");
                    for (Retour_Ligne_RetourPUI_Adapte retourLigneCourant : retourLigneRetourPUIAdapteList
                            ) {
                        if (retourLigneCourant.getRetourLigneID() == retourLigneAdapte.getRetourLigneID()) {
                            retourLigneCourant.setEmplacementAdaptes(retourLigneAdapte.getEmplacementAdaptes());
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération des retour_lige si nécessaire
        if (retourLigneRetourPUIAdapteList.size() == 0) {
            List<Retour_Ligne> retourLignes = gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retourSelectionne);
            for (Retour_Ligne retourLigne : retourLignes
                    ) {
                Retour_Ligne_RetourPUI_Adapte retourLigneAdapte = new Retour_Ligne_RetourPUI_Adapte(retourLigne.get_UID());
                Depot depot = gestionnaireDepot.getDepotParReference(db, retourSelectionne.getRef_Depot_Dest());
                Produit produit = gestionnaireProduit.getProduitByID(db, retourLigne.getCode_produit());

                Depot_Zone zone = gestionnaireZone.getZoneByDepotEtNom(db, depot, gestionnaireProduit.getProduitByID(db, retourLigne.getCode_produit()).getZone_PUI_Defaut());
                if (zone != null) {
                    Depot_Emplacement emplacement = gestionnaireEmplacement.getUnEmplacementZoneEtNom(db, zone, produit.getEmplacement_PUI_Defaut());
                    if (emplacement != null) {
                        Retour_Ligne_RetourPUI_Adapte.EmplacementAdapte emplacementAdapte = retourLigneAdapte.new EmplacementAdapte(emplacement.get_UID(), (int) retourLigne.getQte_avant_retour());
                        retourLigneAdapte.getEmplacementAdaptes().add(emplacementAdapte);
                    }
                }
                retourLigneRetourPUIAdapteList.add(retourLigneAdapte);
            }
        }
        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Retour_Ligne_RetourPUIAdapter(DetailRetourPUIActivity.this, db, retourLigneRetourPUIAdapteList);
        retourLigneListView.setDivider(footer);
        retourLigneListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        afficherAlerteConfirmationRetour(DetailRetourPUIActivity.this, LayoutInflater.from(DetailRetourPUIActivity.this), super.getBundle());
    }

    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater, final Bundle bundle) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Vous allez quitter le retour PUI, confirmez vous ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                retourService(bundle);
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void retourService(final Bundle bundle)
    {
        Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ServiceRetourPUIActivity.class);
        Bundle detailRetourPUIBundle = super.getBundle();
        detailRetourPUIIntent.putExtras(detailRetourPUIBundle);
        DetailRetourPUIActivity.this.startActivity(detailRetourPUIIntent);
        DetailRetourPUIActivity.this.finish();
    }
}
