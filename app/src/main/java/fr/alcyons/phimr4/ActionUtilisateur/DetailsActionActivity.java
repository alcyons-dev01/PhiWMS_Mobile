package fr.alcyons.phimr4.ActionUtilisateur;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.res.ResourcesCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ServiceOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Service;
import fr.alcyons.phimr4.ListViewAdapters.ActionLigneAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionPhotoApercu;
import fr.alcyons.phimr4.PrisePhoto.PrisePhoto;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by olivier on 13/05/2019.
 */

public class DetailsActionActivity extends ServiceActivity {

    int id_action;
    List<ActionUtilisateur_Ligne> list_action_utilisateur;
    ActionUtilisateur actionUtilisateur;
    Context context;
    ActionLigneAdapter actionLigneAdapter;
    Map<String, List<ActionUtilisateur_Ligne>> listeAdapter;
    List<String> ListeEmplacement;
    List<ActionUtilisateur_Ligne> actionUtilisateur_ligneList;
    Service serviceConcerne;

    //Element graphique
    TextView statutAction;
    TextView dateAction;
    TextView typeAction;
    TextView phraseAction;
    ExpandableListView liste_view_action_ligne;
    ImageView photoActionUtilisateur;
    Bitmap photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_action);

        //initialisation des objets graphique
        statutAction = (TextView) findViewById(R.id.statutAction);
        dateAction = (TextView) findViewById(R.id.dateAction);
        typeAction = (TextView) findViewById(R.id.typeAction);
        phraseAction = (TextView) findViewById(R.id.phraseAction);
        liste_view_action_ligne = (ExpandableListView) findViewById(R.id.liste_view_action_ligne);
        photoActionUtilisateur = (ImageView) findViewById(R.id.photoActionUtilisateur);

        //Initilisation des variables
        context = DetailsActionActivity.this;
        listeAdapter = new LinkedHashMap<>();
        ListeEmplacement = new ArrayList<>();
        actionUtilisateur_ligneList = new ArrayList<>();
        id_action = intent.getExtras().getInt("actionId");
        actionUtilisateur = ActionUtilisateurOpenHelper.getActionUtilisateurByid(db, id_action);
    }

    @Override
    public void onResume() {
        super.onResume();


        if(actionUtilisateur != null)
        {
            photo = null;
            if(actionUtilisateur.getCheminPhoto()!= null)
            {
                try {
                    photo = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(actionUtilisateur.getCheminPhoto()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(photo != null)
            {
                photoActionUtilisateur.setImageBitmap(photo);
                photoActionUtilisateur.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            else
            {
                photoActionUtilisateur.setBackground(context.getResources().getDrawable(R.drawable.ic_photo_camera, null));
                photoActionUtilisateur.setBackgroundTintList(context.getResources().getColorStateList(R.color.noir, null));
                photoActionUtilisateur.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            list_action_utilisateur = ActionUtilisateur_LigneOpenHelper.getLigneByAction(db, id_action);
            serviceConcerne = ServiceOpenHelper.getServiceByID(db, actionUtilisateur.getServiceId());

            final Bitmap finalPhoto = photo;
            photoActionUtilisateur.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalPhoto != null)
                    {
                        Intent anotherIntent = new Intent(DetailsActionActivity.this, OutilsGestionPhotoApercu.class);
                        Bundle extras = new Bundle();
                        extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        anotherIntent.putExtra("image", actionUtilisateur.getCheminPhoto());
                        anotherIntent.putExtra("apercu", true);
                        anotherIntent.putExtras(extras);
                        DetailsActionActivity.this.startActivityForResult(anotherIntent, CodesEchangesActivites.RETOUR_LIEN_PHOTO);
                    }
                    else
                    {
                        Intent detailReceptionPui_Intent = new Intent(DetailsActionActivity.this, PrisePhoto.class);
                        Bundle detailReceptionPui_Bundle = DetailsActionActivity.super.getBundle();
                        // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                        detailReceptionPui_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                        detailReceptionPui_Bundle.putString("contexte", "photoAction");
                        detailReceptionPui_Intent.putExtras(detailReceptionPui_Bundle);
                        DetailsActionActivity.this.startActivityForResult(detailReceptionPui_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
                    }
                }
            });
        }
        else
        {
            onBackPressed();
        }


        //Gestion des valeurs dans les TextView
        statutAction.setText(actionUtilisateur.getStatut());

        String[] tab_date = actionUtilisateur.getDate().split(" ");
        String date = tab_date[0];
        String heure = tab_date[1];

        DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
        DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

        Date dateDate = new Date();

        try {
            dateDate = dateFormat1.parse(date);
            date =  dateFormat2.format(dateDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateAction.setText(date+" à "+heure);
        typeAction.setText(serviceConcerne.getNom());
        String type = "Réception";

        //gestion nom service
        String nom_service = serviceConcerne.getNom();
        if(nom_service.contentEquals("Préparation UF Scan"))
        {
            nom_service = "Préparation UF";
        }

        if(nom_service.contentEquals("Préparation PAD Scan"))
        {
            nom_service = "Préparation PAD";
        }

        //gestion de la récupération du nom des produits scannée
        switch (nom_service)
        {
            case "Verrou Pharmacie":
                type = "Verrou pharmacie";
                gestionAdapter();
                break;
            case "Quarantaine":
                type = "Mise en quarantaine";
                gestionAdapter();
                break;
            case "Inventaire scanner":
                type = "Inventaire";
                gestionAdapter();
                break;
            case "Réception PUI":
                type = "Réception";
                gestionAdapter();
                break;
            case "Contrôle des retours":
                type = "Contrôle";
                gestionAdapter();
                break;
            case "Contrôle des retours Scan":
                type = "Contrôle scanné";
                gestionAdapter();
                break;
            case "Retour PUI":
                type = "Retour à la PUI";
                gestionAdapter();
                break;
            case "Retour Frs":
                type = "Retour au fournisseur";
                gestionAdapter();
                break;
            case "Destruction":
                type = "Destruction";
                gestionAdapter();
                break;
            case "Plan de placement":
                type = "Placement";
                break;
            case "Identification Par Scan":
                type = "Identification";
                break;
            case "Zones et Emplacements":
                break;
            case "Préparation UF":
                type = "Préparation UF";
                listeAdapter = new LinkedHashMap<>();
                actionUtilisateur_ligneList = new ArrayList<>();
                ListeEmplacement = new ArrayList<>();
                boolean premierPassagePreparation = true;
                String ancienAdressagePreparation = "";
                int sizePreparation = list_action_utilisateur.size();
                int iterationPreparation = 0;
                for(ActionUtilisateur_Ligne courant : list_action_utilisateur)
                {
                    iterationPreparation ++;
                    Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, courant.getEmplacementId());
                    String adressage = "";
                    if(emplacement_courant != null)
                    {
                        adressage = emplacement_courant.getAdressage();
                    }

                    if(iterationPreparation == sizePreparation)
                    {
                        if(ListeEmplacement.contains(adressage))
                        {
                            if(!ancienAdressagePreparation.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                            {
                                listeAdapter.put(ancienAdressagePreparation, actionUtilisateur_ligneList);
                            }

                            if(listeAdapter.size() != 0)
                            {
                                actionUtilisateur_ligneList = listeAdapter.get(adressage);
                            }
                            if(actionUtilisateur_ligneList == null)
                            {
                                actionUtilisateur_ligneList = new ArrayList<>();
                            }
                            actionUtilisateur_ligneList.add(courant);

                        }
                        else
                        {
                            if(!ancienAdressagePreparation.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                            {
                                listeAdapter.put(ancienAdressagePreparation, actionUtilisateur_ligneList);
                            }
                            actionUtilisateur_ligneList = new ArrayList<>();
                            actionUtilisateur_ligneList.add(courant);
                            ListeEmplacement.add(adressage);
                        }
                        listeAdapter.put(adressage, actionUtilisateur_ligneList);
                    }
                    else if(!premierPassagePreparation)
                    {
                        if(!ListeEmplacement.contains(adressage))
                        {
                            listeAdapter.put(ancienAdressagePreparation, actionUtilisateur_ligneList);
                            actionUtilisateur_ligneList = new ArrayList<>();
                            ListeEmplacement.add(adressage);
                            actionUtilisateur_ligneList.add(courant);
                            ancienAdressagePreparation = adressage;
                        }
                        else
                        {
                            actionUtilisateur_ligneList.add(courant);
                        }
                    }
                    else
                    {
                        ListeEmplacement.add(adressage);
                        actionUtilisateur_ligneList.add(courant);
                        ancienAdressagePreparation = adressage;
                        premierPassagePreparation = false;
                    }
                }
                break;
            case "Préparation PAD":
                type = "Préparation PAD";
                listeAdapter = new LinkedHashMap<>();
                actionUtilisateur_ligneList = new ArrayList<>();
                ListeEmplacement = new ArrayList<>();
                boolean premierPassagePreparationPAD = true;
                String ancienAdressagePreparationPAD = "";
                int sizePreparationPAD = list_action_utilisateur.size();
                int iterationPreparationPAD = 0;
                for(ActionUtilisateur_Ligne courant : list_action_utilisateur)
                {
                    iterationPreparationPAD ++;
                    Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, courant.getEmplacementId());
                    String adressage = "";
                    if(emplacement_courant != null)
                    {
                        adressage = emplacement_courant.getAdressage();
                    }

                    if(iterationPreparationPAD == sizePreparationPAD)
                    {
                        if(ListeEmplacement.indexOf(adressage) != -1)
                        {
                            if(!ancienAdressagePreparationPAD.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                            {
                                listeAdapter.put(ancienAdressagePreparationPAD, actionUtilisateur_ligneList);
                            }

                            if(listeAdapter.size() != 0)
                            {
                                actionUtilisateur_ligneList = listeAdapter.get(adressage);
                            }
                            if(actionUtilisateur_ligneList == null)
                            {
                                actionUtilisateur_ligneList = new ArrayList<>();
                            }
                            actionUtilisateur_ligneList.add(courant);
                        }
                        else
                        {
                            if(!ancienAdressagePreparationPAD.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                            {
                                listeAdapter.put(ancienAdressagePreparationPAD, actionUtilisateur_ligneList);
                            }
                            actionUtilisateur_ligneList = new ArrayList<>();
                            actionUtilisateur_ligneList.add(courant);
                            ListeEmplacement.add(adressage);
                        }
                        listeAdapter.put(adressage, actionUtilisateur_ligneList);
                    }
                    else if(!premierPassagePreparationPAD)
                    {
                        if(ListeEmplacement.indexOf(adressage) == -1)
                        {
                            listeAdapter.put(ancienAdressagePreparationPAD, actionUtilisateur_ligneList);
                            actionUtilisateur_ligneList = new ArrayList<>();
                            ListeEmplacement.add(adressage);
                            actionUtilisateur_ligneList.add(courant);
                            ancienAdressagePreparationPAD = adressage;
                        }
                        else
                        {
                            actionUtilisateur_ligneList.add(courant);
                        }
                    }
                    else
                    {
                        ListeEmplacement.add(adressage);
                        actionUtilisateur_ligneList.add(courant);
                        ancienAdressagePreparationPAD = adressage;
                        premierPassagePreparationPAD = false;
                    }
                }
                break;
            case "Utiliser":
                type = "Utilisation";
                gestionAdapter();
                break;
            case "Demande Protocole PAD":
                type = "Demande d'un protocole PAD";
                gestionAdapter();
                break;
            case "Demande Dotation PAD":
                type = "Demande d'une dotation PAD";
                break;
            case "Demande Réassort":
                type = "Demande réassort";
                gestionAdapter();
                break;
            case "Dotation Service":
                type = "Dotation service";
                gestionAdapter();
                break;
            case "Demande Particuliere":
                type = "Demande particulière";
                gestionAdapter();
                break;
            case "Demande PleinVide":
                type = "Demande plein vide";
                gestionAdapter();
                break;
            case "Demande Dotation Urgence":
                type = "Demande de dotation Urgence";
                break;
            case "Retour Demandé":
                type = "Retour demandé";
                gestionAdapter();
                break;
            case "Livraison":
                type = "Livraison";
                gestionAdapter();
                break;
            case "Commander":
                type = "Commande";
                break;
            case "Réception PAD":
                type = "Réception PAD";
                listeAdapter = new LinkedHashMap<>();
                actionUtilisateur_ligneList = new ArrayList<>();
                ListeEmplacement = new ArrayList<>();
                boolean premierPassage = true;
                String ancienneDesignation = "";
                int sizeList = list_action_utilisateur.size();
                int iterationList = 0;
                for(ActionUtilisateur_Ligne courant : list_action_utilisateur)
                {
                    iterationList ++;
                    PH_Reliquat ph_reliquat = PH_ReliquatOpenHelper.getPH_ReliquatById(db, courant.getNumChamps());
                    if(ph_reliquat != null)
                    {
                        Produit produitCourant = ProduitOpenHelper.getProduitByID(db, ph_reliquat.getProduitID());
                        String adressage = produitCourant.getDesignation_interne();

                        if(iterationList == sizeList)
                        {
                            if(ListeEmplacement.indexOf(adressage) != -1)
                            {
                                if(!ancienneDesignation.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                                {
                                    listeAdapter.put(ancienneDesignation, actionUtilisateur_ligneList);
                                }

                                if(listeAdapter.size() != 0)
                                {
                                    actionUtilisateur_ligneList = listeAdapter.get(adressage);
                                }
                                if(actionUtilisateur_ligneList == null)
                                {
                                    actionUtilisateur_ligneList = new ArrayList<>();
                                }
                                actionUtilisateur_ligneList.add(courant);

                            }
                            else
                            {
                                if(!ancienneDesignation.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                                {
                                    listeAdapter.put(ancienneDesignation, actionUtilisateur_ligneList);
                                }
                                actionUtilisateur_ligneList = new ArrayList<>();
                                actionUtilisateur_ligneList.add(courant);
                                ListeEmplacement.add(adressage);
                            }
                            listeAdapter.put(adressage, actionUtilisateur_ligneList);
                        }
                        else if(!premierPassage)
                        {
                            if(ListeEmplacement.indexOf(adressage) == -1)
                            {
                                listeAdapter.put(ancienneDesignation, actionUtilisateur_ligneList);
                                actionUtilisateur_ligneList = new ArrayList<>();
                                ListeEmplacement.add(adressage);
                                actionUtilisateur_ligneList.add(courant);
                                ancienneDesignation = adressage;
                            }
                            else
                            {
                                actionUtilisateur_ligneList.add(courant);
                            }
                        }
                        else
                        {
                            ListeEmplacement.add(adressage);
                            actionUtilisateur_ligneList.add(courant);
                            ancienneDesignation = adressage;
                            premierPassage = false;
                        }
                    }
                }
                break;
            case "Réception Scannée":
                type = "Réception";
                listeAdapter = new LinkedHashMap<>();
                actionUtilisateur_ligneList = new ArrayList<>();
                ListeEmplacement = new ArrayList<>();
                boolean premier = true;
                String ancienAdressage = "";
                int size = list_action_utilisateur.size();
                int iteration = 0;
                for(ActionUtilisateur_Ligne courant : list_action_utilisateur)
                {
                    iteration ++;
                    Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, courant.getEmplacementId());
                    if(emplacement_courant != null)
                    {
                        String adressage = emplacement_courant.getAdressage();

                        if(iteration == size)
                        {
                            if(ListeEmplacement.indexOf(adressage) != -1)
                            {
                                if(!ancienAdressage.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                                {
                                    listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                                }

                                if(listeAdapter.size() != 0)
                                {
                                    actionUtilisateur_ligneList = listeAdapter.get(adressage);
                                }
                                if(actionUtilisateur_ligneList == null)
                                {
                                    actionUtilisateur_ligneList = new ArrayList<>();
                                }
                                actionUtilisateur_ligneList.add(courant);

                            }
                            else
                            {
                                if(!ancienAdressage.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                                {
                                    listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                                }
                                actionUtilisateur_ligneList = new ArrayList<>();
                                actionUtilisateur_ligneList.add(courant);
                                ListeEmplacement.add(adressage);
                            }
                            listeAdapter.put(adressage, actionUtilisateur_ligneList);
                        }
                        else if(!premier)
                        {
                            if(ListeEmplacement.indexOf(adressage) == -1)
                            {
                                listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                                actionUtilisateur_ligneList = new ArrayList<>();
                                ListeEmplacement.add(adressage);
                                actionUtilisateur_ligneList.add(courant);
                                ancienAdressage = adressage;
                            }
                            else
                            {
                                actionUtilisateur_ligneList.add(courant);
                            }
                        }
                        else
                        {
                            ListeEmplacement.add(adressage);
                            actionUtilisateur_ligneList.add(courant);
                            ancienAdressage = adressage;
                            premier = false;
                        }
                    }
                }
                break;
        }

        if(actionUtilisateur.getChampsParentId() != 0)
        {
            phraseAction.setText(type+" de "+list_action_utilisateur.size()+" produit(s) pour "+serviceConcerne.getNom()+" n°"+actionUtilisateur.getChampsParentId());
        }
        else
        {
            phraseAction.setText(type+" de "+list_action_utilisateur.size()+" produit(s) pour "+serviceConcerne.getNom()+". Aucun document associé");
        }


        //gestion de la couleur du bandeau suivant le statut de l'action
        switch (actionUtilisateur.getStatut())
        {
            case "En attente":
                statutAction.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_encours, null));
                break;
            case "En conflit":
                statutAction.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_conflit, null));
                break;
            case "Soumise":
                statutAction.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_soumis, null));
                break;
            case "Annulée":
                statutAction.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_annuler, null));
                break;
            default:
                statutAction.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.background_gestion_statut_action_valider, null));
                break;
        }
        
        //liste_view_action_ligne.setAdapter(null);
        actionLigneAdapter = new ActionLigneAdapter(DetailsActionActivity.this, db, ListeEmplacement, listeAdapter, utilisateurConnecte, type);
        liste_view_action_ligne.setAdapter(actionLigneAdapter);
        liste_view_action_ligne.setDivider(footer);
        expandAll();

        invalidateOptionsMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduitsChemin = data.getExtras().getString("photoProduit");
                    if (photoProduitsChemin == null || photoProduitsChemin.contentEquals("")) {
                    } else {
                        try {
                            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoProduitsChemin));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(photo!=null)
                        {
                            actionUtilisateur.setCheminPhoto(photoProduitsChemin);
                            ActionUtilisateurOpenHelper.mettreAJourActionUtilisateur(db, actionUtilisateur);
                            photoActionUtilisateur.setImageBitmap(photo);
                            photoActionUtilisateur.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                    }

                    break;
                case CodesEchangesActivites.RETOUR_LIEN_PHOTO:
                    String newLienPhoto = data.getExtras().getString("LienPhoto");
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(newLienPhoto));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(photo!=null)
                    {
                        actionUtilisateur.setCheminPhoto(newLienPhoto);
                        ActionUtilisateurOpenHelper.mettreAJourActionUtilisateur(db, actionUtilisateur);
                        photoActionUtilisateur.setImageBitmap(photo);
                        photoActionUtilisateur.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        DetailsActionActivity.this.finish();
    }

    private void expandAll() {
        int count = actionLigneAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            liste_view_action_ligne.expandGroup(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_annuler, menu);
        if(actionUtilisateur.getStatut().contentEquals("En attente"))
        {
            menu.findItem(R.id.annuleMenu).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.annuleMenu).setVisible(false);
        }

        return true;
    }

    // Nécessaire afin d'avoir l'item Search
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.annuleMenu);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                annulerAction();
                return true;
            }
        });

        return true;
    }

    private void annulerAction()
    {
        boolean confirmation = Alerte.afficherAlerte(DetailsActionActivity.this, "Confirmation", "Souhaitez-vous vraiment annuler", "OuiNon");
        if(confirmation)
        {
            actionUtilisateur.setStatut("Annulée");
            ActionUtilisateurOpenHelper.mettreAJourActionUtilisateur(db, actionUtilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, actionUtilisateur.getPhiMR4UUID(), actionUtilisateur.getId(), DBOpenHelper.ActionsEAS.MAJ);
            DetailsActionActivity.this.finish();
        }
    }

    private void gestionAdapter()
    {
        listeAdapter = new LinkedHashMap<>();
        actionUtilisateur_ligneList = new ArrayList<>();
        ListeEmplacement = new ArrayList<>();
        boolean premierPassage = true;
        String ancienAdressage = "";
        int size = list_action_utilisateur.size();
        int iteration = 0;
        for(ActionUtilisateur_Ligne courant : list_action_utilisateur)
        {
            iteration ++;
            Depot_Emplacement emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, courant.getEmplacementId());
            String adressage = "";
            if(emplacement_courant != null)
            {
                adressage = emplacement_courant.getAdressage();
            }
            else
            {
                adressage = "Références";
            }

            if(iteration == size)
            {
                if(ListeEmplacement.indexOf(adressage) != -1)
                {
                    if(!ancienAdressage.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                    {
                        listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                    }

                    if(listeAdapter.size() != 0)
                    {
                        actionUtilisateur_ligneList = listeAdapter.get(adressage);
                    }
                    if(actionUtilisateur_ligneList == null)
                    {
                        actionUtilisateur_ligneList = new ArrayList<>();
                    }
                    actionUtilisateur_ligneList.add(courant);

                }
                else
                {
                    if(!ancienAdressage.contentEquals("") && actionUtilisateur_ligneList.size()!=0)
                    {
                        listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                    }
                    actionUtilisateur_ligneList = new ArrayList<>();
                    actionUtilisateur_ligneList.add(courant);
                    ListeEmplacement.add(adressage);
                }
                listeAdapter.put(adressage, actionUtilisateur_ligneList);
            }
            else if(!premierPassage)
            {
                if(ListeEmplacement.indexOf(adressage) == -1)
                {
                    listeAdapter.put(ancienAdressage, actionUtilisateur_ligneList);
                    actionUtilisateur_ligneList = new ArrayList<>();
                    ListeEmplacement.add(adressage);
                    actionUtilisateur_ligneList.add(courant);
                    ancienAdressage = adressage;
                }
                else
                {
                    actionUtilisateur_ligneList.add(courant);
                }
            }
            else
            {
                ListeEmplacement.add(adressage);
                actionUtilisateur_ligneList.add(courant);
                ancienAdressage = adressage;
                premierPassage = false;
            }
        }
    }
}
