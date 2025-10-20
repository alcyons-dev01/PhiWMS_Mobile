package fr.alcyons.phiwms_mobile.RetourPUI;

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

import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
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
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.RetourFournisseur.DetailRetourFournisseurActivity;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceRetourPUIActivity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class  DetailRetourPUIActivity extends ServiceActivity {
    Retour retourSelectionne;
    List<Retour_Ligne> retourLigneList;
    ListView retourLigneListView;
    Retour_Ligne_RetourPUIAdapter adapter;
    Retour_Ligne_RetourPUIAdapter.Retour_LigneViewHolder viewHolderAModifier;
    ActivityResultLauncher<Intent> resultListeEmplacement;
    String commentaire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_retour_pui);

        // Récupération du Retour grâce à la variable globale
        retourSelectionne = RetourOpenHelper.getRetourByID(db, Objects.requireNonNull(intent.getExtras()).getInt("retourSelectionneID"));

        // Affichage des informations de base
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitule());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero());

        // Gestion de la listView
        retourLigneListView = findViewById(R.id.listeView);
        retourLigneListView.setItemsCanFocus(true);
        retourLigneListView.setOnItemClickListener((parent, view, position, id) -> {
            Retour_Ligne retourLigne = adapter.retourLigne.get(position);

            viewHolderAModifier = adapter.viewHolderList.get(position);
            viewHolderAModifier.progressBar.setVisibility(View.VISIBLE);
            viewHolderAModifier.layoutPrincipal.setBackgroundColor(getResources().getColor(R.color.noirtransparent, null));

            Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ListeEmplacementRetourPUIActivity.class);
            Bundle detailRetourPUIBundle = DetailRetourPUIActivity.super.getBundle();
            detailRetourPUIBundle.putInt("produitID", retourLigne.getCode_produit());
            detailRetourPUIBundle.putSerializable("retourLigne", retourLigne);
            detailRetourPUIBundle.putInt("depotID", DepotOpenHelper.getDepotParReference(db, retourSelectionne.getRef_Depot_Dest()).getDepot_UID());
            detailRetourPUIIntent.putExtras(detailRetourPUIBundle);

            resultListeEmplacement.launch(detailRetourPUIIntent);
        });

        retourLigneList = new ArrayList<>();

        resultListeEmplacement = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RETOUR_LISTE_EMPLACEMENTS) {
                        onResume();
                    }
                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                afficherAlerteConfirmationRetour(DetailRetourPUIActivity.this, LayoutInflater.from(DetailRetourPUIActivity.this));
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération des retour_lige si nécessaire
        retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesBaseByRetour(db, retourSelectionne);

        //gestion des retours lignes
        for(Retour_Ligne retourLigneTemp : retourLigneList)
        {
            List<Retour_Ligne> retourLigneRetournee = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, retourLigneTemp.getCode_produit());

            if(retourLigneRetournee.isEmpty())
            {
                Produit produitCourant  = ProduitOpenHelper.getProduitByID(db, retourLigneTemp.getCode_produit());
                if(!produitCourant.getEmplacement_PUI_Defaut().contentEquals("") && produitCourant.getEmplacement_PUI_Defaut() != null)
                {
                    creationRetourLigne(retourLigneTemp, produitCourant);
                }
            }
        }

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        adapter = new Retour_Ligne_RetourPUIAdapter(DetailRetourPUIActivity.this, db, retourLigneList, retourSelectionne);
        retourLigneListView.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = layout.findViewById(R.id.messageFin);
        messageTextView.setText("Vous allez quitter le retour PUI, confirmez vous ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
            alertDialog.dismiss();
            retourService();
        });

        buttonAnnuler.setOnClickListener(v -> alertDialog.dismiss());
    }
    private void retourService()
    {
        Intent detailRetourPUIIntent = new Intent(DetailRetourPUIActivity.this, ServiceRetourPUIActivity.class);
        Bundle detailRetourPUIBundle = super.getBundle();
        detailRetourPUIIntent.putExtras(detailRetourPUIBundle);
        DetailRetourPUIActivity.this.startActivity(detailRetourPUIIntent);
        DetailRetourPUIActivity.this.finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSaveCircle);
        item.setOnMenuItemClickListener(item1 -> {
            // On vérifie que chaque élément a au moins un emplacement de retour
            boolean retourPuiValide = true;
            for (Retour_Ligne retour_ligneTemp : retourLigneList)
            {
                List<Retour_Ligne> retourLigneCourant = Retour_LigneOpenHelper.getAllRetourLignesByRetourProduitNeg(db, retourSelectionne, retour_ligneTemp.getCode_produit());
                int qteRetournerTemp = 0;

                for(Retour_Ligne ligneNegTemp : retourLigneCourant)
                {
                    qteRetournerTemp += (int) ligneNegTemp.getQte_Retourner();
                }

                if(qteRetournerTemp != retour_ligneTemp.getQte_avant_retour())
                {
                    retourPuiValide = false;
                    break;
                }
            }

            if (!retourPuiValide) {
                Alerte.afficherAlerte(DetailRetourPUIActivity.this, "Alerte", "Tous les éléments n'ont pas été retourné entièrement.", "alerte");
            }
            else
            {
                afficherModaleCommentaire(DetailRetourPUIActivity.this, getLayoutInflater());
            }

            return true;
        });
        return true;
    }

    private void afficherModaleCommentaire(Context context, LayoutInflater inflater)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_commentaire, null);
        TextView titre = layout.findViewById(R.id.messageFin);
        EditText editCommentaire = layout.findViewById(R.id.commentaire);
        LinearLayout buttonAnnuler = layout.findViewById(R.id.buttonAnnuler);
        LinearLayout buttonValider = layout.findViewById(R.id.buttonOk);
        titre.setText("Souhaitez-vous valider le retour à la PUI ?");
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
                validerRetourPUI();
                alertDialog.dismiss();
            }
        });
    }

    private void validerRetourPUI()
    {
        //on supprime les retours ligne de base
        for(Retour_Ligne retour_ligneTemp : retourLigneList)
        {
            Retour_LigneOpenHelper.supprimerUnRetourLigne(db, retour_ligneTemp);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.getPhiMR4UUID(), retour_ligneTemp.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.SUPPR);
        }

        //on recupère les retours ligne negatif
        List<Retour_Ligne> listRetourLigneNegatif = Retour_LigneOpenHelper.getAllRetourLignesNegByRetour(db, retourSelectionne);

        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", retourSelectionne.get_UID(), "", "Retour PUI");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);

        for (Retour_Ligne retour_ligneTemp : listRetourLigneNegatif) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retour_ligneTemp.getPhiMR4UUID(), retour_ligneTemp.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retour_ligneTemp.get_UID(), "", 0, (int)retour_ligneTemp.getQte_Retourner(), retour_ligneTemp.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);

            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
        }

        // Si tout est ok mise à jour du Retour
        String intitule = retourSelectionne.getIntitule();
        intitule = intitule.replace(getString(R.string.RetourPUIDemande), getString(R.string.RetourPUIEffectue));
        retourSelectionne.setIntitule(intitule.trim());
        retourSelectionne.setEn_Attente_de(getString(R.string.RetourPUIEffectue));
        retourSelectionne.setCommentaire(commentaire);
        long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);
        if (rowID != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        }

        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

        ElementASynchroniserOpenHelper.toutSynchroniser(DetailRetourPUIActivity.this, db, utilisateurConnecte, true);
        Toast.makeText(DetailRetourPUIActivity.this, "Retour PUI effectué", Toast.LENGTH_SHORT).show();
        retourService();
    }

    private void creationRetourLigne(Retour_Ligne retourLigneBase, Produit produit)
    {
        Random random = new Random();
        int retourLigneId = random.nextInt();
        if(retourLigneId > 0)
            retourLigneId = retourLigneId*-1;

        Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneBase);
        retourLigneCourant.set_UID(retourLigneId);
        retourLigneCourant.setRetourPUI_Zone(produit.getZone_PUI_Defaut());
        retourLigneCourant.setRetourPUI_Emplacement(produit.getEmplacement_PUI_Defaut());
        retourLigneCourant.setQte_Retourner(retourLigneBase.getQte_Retourner());

        long rowID = Retour_LigneOpenHelper.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
    }
}
