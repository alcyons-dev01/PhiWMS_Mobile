package fr.alcyons.phimr4.ReceptionScanne;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.CommandeOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Commande;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.ListViewAdapters.AjoutManuelReceptionScanneeAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;


public class AjoutManuelReceptionScanneeActivity extends ServiceActivity {

    //variable
    String numero_commande_courante;
    Commande commande_courante;
    List<PH_Reliquat> liste_reliquat_commande_courante;
    List<ObjetReceptionScannee> list_produit_scannee;
    Map<PH_Reliquat, List<ObjetReceptionScannee>> map_affichage_adapter;
    AjoutManuelReceptionScanneeAdapter ajout_manuel_reception_scannee_adapter;
    List<String> liste_designation_produit;
    String photoProduitsChemin;

    //objet graphique
    TextView nomReceptionSelection;
    TextView numeroCommande;
    ExpandableListView liste_view_produit_reception_scannee;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_manuel_produit_reception_scannee);

        numero_commande_courante = intent.getExtras().getString("NumeroCommande");
        list_produit_scannee = (List<ObjetReceptionScannee>) intent.getExtras().getSerializable("listeProduitScannee");
        photoProduitsChemin = intent.getExtras().getString("CheminPhoto");
        commande_courante = CommandeOpenHelper.getCommandeByNumero(db, numero_commande_courante);
        liste_reliquat_commande_courante = PH_ReliquatOpenHelper.getPH_ReliquatByCommandeNumero(db, commande_courante.getNumero());

        //initialisation des objets graphique
        liste_view_produit_reception_scannee = (ExpandableListView) findViewById(R.id.liste_view_produit_reception_scannee);
        nomReceptionSelection = (TextView) findViewById(R.id.nomReceptionSelection);
        numeroCommande = (TextView) findViewById(R.id.numeroCommande);

        //affichage des activités dans les objets graphique
        numeroCommande.setText(commande_courante.getNumero());
        nomReceptionSelection.setText(commande_courante.getFournisseur());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //initialisation des variables
        liste_designation_produit = new ArrayList<>();
        map_affichage_adapter = new LinkedHashMap<>();

        //construction de la liste pour les headers de l'adapter et initialisation de l'adapter
        for(PH_Reliquat reliquat_courant : liste_reliquat_commande_courante)
        {
            Produit produit_courant = ProduitOpenHelper.getProduitByID(db, reliquat_courant.getProduitID());
            liste_designation_produit.add(reliquat_courant.getDesignationCourte());
            List<ObjetReceptionScannee> liste_objet_courant = new ArrayList<>();
            map_affichage_adapter.put(reliquat_courant, liste_objet_courant);
        }

        //construction du mapAdapter
        GestionAdapter();

        ajout_manuel_reception_scannee_adapter = new AjoutManuelReceptionScanneeAdapter(AjoutManuelReceptionScanneeActivity.this, db, liste_reliquat_commande_courante, map_affichage_adapter, utilisateurConnecte);
        liste_view_produit_reception_scannee.setAdapter(ajout_manuel_reception_scannee_adapter);
        liste_view_produit_reception_scannee.setDivider(footer);

        invalidateOptionsMenu();
    }

    private void expandAll()
    {
        int count = ajout_manuel_reception_scannee_adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            liste_view_produit_reception_scannee.expandGroup(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_OBJET_RECEPTION_SCANNEE:
                    list_produit_scannee = new ArrayList<>();
                    liste_designation_produit = new ArrayList<>();
                    map_affichage_adapter = new LinkedHashMap<>();
                    list_produit_scannee = (List<ObjetReceptionScannee>)data.getExtras().getSerializable("listeProduitScannee");
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);

        boolean complet = true;
        for(PH_Reliquat reliquat : liste_reliquat_commande_courante)
        {
            if(reliquat.getQteReliquat_X() != 0)
            {
                complet = false;
            }
        }
        if(complet)
        {
            menu.findItem(R.id.menuAdd).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.menuAdd).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem item = menu.findItem(R.id.menuSave);
        MenuItem itemAdd = menu.findItem(R.id.menuAdd);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });

        itemAdd.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuAddClick();
                return true;
            }
        });

        return true;
    }

    public void onMenuAddClick()
    {
        Intent intent_vers_creation_lot = new Intent(AjoutManuelReceptionScanneeActivity.this, CreationLotAjoutManuelActivity.class);
        Bundle bundle_vers_creation_lot = new Bundle();
        bundle_vers_creation_lot.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle_vers_creation_lot.putSerializable("listeProduitScannee", (Serializable) list_produit_scannee);
        bundle_vers_creation_lot.putSerializable("listeReliquatCommande", (Serializable) liste_reliquat_commande_courante);
        bundle_vers_creation_lot.putStringArrayList("listeDesignationProduit", (ArrayList<String>)liste_designation_produit);

        intent_vers_creation_lot.putExtras(bundle_vers_creation_lot);
        AjoutManuelReceptionScanneeActivity.this.startActivityForResult(intent_vers_creation_lot, CodesEchangesActivites.RETOUR_OBJET_RECEPTION_SCANNEE);
    }

    public void onMenuSaveClick()
    {
        SauvegarderReception();
    }

    private void GestionAdapter()
    {
        for(ObjetReceptionScannee objet_courant : list_produit_scannee)
        {
            Map<String, String> gs1_decoupe_objet_courant = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());
            Produit produit_courant = null;
            if(gs1_decoupe_objet_courant.size() > 1)
            {
                produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1_decoupe_objet_courant.get(OutilsDecodage.codeGtin));
            }
            else
            {
                String code_inconnu = objet_courant.getGs1_scannee();
                if(code_inconnu.startsWith("ci"))
                {
                    Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                    code_inconnu = MapInconnu.get("Code_Inconnu");
                }

                List<Produit> list_produit_inconnu = ProduitOpenHelper.getProduitByCodeInconnu(db, code_inconnu);
                if(list_produit_inconnu.size() == 1)
                {
                    produit_courant = list_produit_inconnu.get(0);
                }
                else
                {
                    for(Produit produit : list_produit_inconnu)
                    {
                        for(PH_Reliquat reliquat_courant : liste_reliquat_commande_courante)
                        {
                            if(reliquat_courant.getProduitID() == produit.getID_produit())
                            {
                                produit_courant = produit;
                                break;
                            }
                        }

                        if(produit_courant != null)
                        {
                            break;
                        }
                    }
                }
            }

            if(produit_courant != null)
            {
                PH_Reliquat reliquat_a_modifier = null;
                for(Map.Entry<PH_Reliquat, List<ObjetReceptionScannee>> entry : map_affichage_adapter.entrySet())
                {
                    PH_Reliquat courant = entry.getKey();
                    if(courant.getProduitID() == produit_courant.getID_produit())
                    {
                        reliquat_a_modifier = courant;
                        break;
                    }
                }

                if(reliquat_a_modifier != null)
                {
                    List<ObjetReceptionScannee> liste_temporaire = map_affichage_adapter.get(reliquat_a_modifier);
                    liste_temporaire.add(objet_courant);
                    int qte_saisie = 0;
                    for(ObjetReceptionScannee objet_temp_courant : liste_temporaire)
                    {
                        qte_saisie = qte_saisie + objet_temp_courant.getQuantiteScannee();
                    }
                    reliquat_a_modifier.setQteLivraison(qte_saisie);
                    reliquat_a_modifier.setQteReliquat_X(reliquat_a_modifier.getQteCommande() - reliquat_a_modifier.getQteLivraison());
                    PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat_a_modifier);
                    map_affichage_adapter.put(reliquat_a_modifier, liste_temporaire);
                }
            }
        }
    }

    public void supprimerScan(int groupPosition, int childPosition)
    {
        boolean confirmer = Alerte.afficherAlerte(AjoutManuelReceptionScanneeActivity.this, "Confirmation", "Souhaitez-vous supprimer la ligne sélectionnée ?", "OuiNon");
        if (confirmer) {
            PH_Reliquat key = getKeyMap(map_affichage_adapter, groupPosition);
            List<ObjetReceptionScannee> list_group_select = map_affichage_adapter.get(key);
            ObjetReceptionScannee objet_a_supprimer = list_group_select.get(childPosition);
            key.setQteReliquat_X(key.getQteReliquat_X()+objet_a_supprimer.getQuantiteScannee());
            key.setQteLivraison(key.getQteLivraison()-objet_a_supprimer.getQuantiteScannee());
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, key);
            list_group_select.remove(childPosition);
            int index = -1;
            for(ObjetReceptionScannee objet_courant : list_produit_scannee)
            {
                index ++;
                if(objet_courant.getEmplacement_uid() == objet_a_supprimer.getEmplacement_uid() && objet_courant.getQuantiteScannee() == objet_a_supprimer.getQuantiteScannee() && objet_courant.getGs1_scannee().contentEquals(objet_a_supprimer.getGs1_scannee()))
                {
                    break;
                }
            }

            list_produit_scannee.remove(index);

            onResume();
        }
    }

    private static PH_Reliquat getKeyMap(Map Map, int index)
    {

        PH_Reliquat key = null;
        Map <PH_Reliquat,Object> hs = Map;
        int pos=0;
        for(Map.Entry<PH_Reliquat, Object> entry : hs.entrySet())
        {
            if(index==pos){
                key=entry.getKey();
            }
            pos++;
        }
        return key;
    }


    private void SauvegarderReception()
    {
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date =new Date();
        String date_string = parseFormat.format(date);

        if(photoProduitsChemin == null)
        {
            photoProduitsChemin = "";
        }

        //gestion de l'action
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        ActionUtilisateur action = new ActionUtilisateur(actionId);
        action.setUserId(utilisateurConnecte.getId());
        action.setDate(date_string);
        action.setServiceId(serviceActuel.getId());
        action.setEtablissementId(Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)));
        action.setStatut("En attente");
        if(numero_commande_courante != null)
        {
            action.setChampsParentId(commande_courante.getID_commande());
        }
        else
        {
            action.setChampsParentId(0);
        }

        action.setCheminPhoto(photoProduitsChemin);
        action.setActionName("Réception");


        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, action);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, action.getPhiMR4UUID(), action.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        List<ObjetReceptionScannee> listAEnvoyer = new ArrayList<>();
        boolean premierPassageListe = true;
        for(ObjetReceptionScannee courant : list_produit_scannee)
        {
            if(premierPassageListe)
            {
                listAEnvoyer.add(courant);
                premierPassageListe = false;
            }
            else
            {
                Map<String, String> gs1AAjouter = OutilsDecodage.decouperGTIN(courant.getGs1_scannee());
                if(gs1AAjouter.size() == 1)
                {
                    listAEnvoyer.add(courant);
                }
                else
                {
                    String gtin = gs1AAjouter.get(OutilsDecodage.codeGtin);
                    Produit produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                    if(produitCourant.isSuivi_Serialisation() && produitCourant.isSerialiser_Reception_Delivrance())
                    {
                        listAEnvoyer.add(courant);
                    }
                    else
                    {
                        String lot = gs1AAjouter.get(OutilsDecodage.numeroLot);
                        int uidEmpl = courant.getEmplacement_uid();
                        int quantiteCourante = courant.getQuantiteScannee();
                        ObjetReceptionScannee newObjet = new ObjetReceptionScannee(courant);
                        int indexTemp = 0;
                        boolean tempSupprimer = false;
                        for(ObjetReceptionScannee temp : listAEnvoyer)
                        {
                            Map<String, String> decoupeTemp = OutilsDecodage.decouperGTIN(temp.getGs1_scannee());
                            String tempLot = decoupeTemp.get(OutilsDecodage.numeroLot);
                            int tempUidEmp = temp.getEmplacement_uid();
                            int tempQuantite = temp.getQuantiteScannee();

                            if(tempLot != null)
                            {
                                if(tempLot.contentEquals(lot) && uidEmpl == tempUidEmp)
                                {
                                    newObjet.setQuantiteScannee(tempQuantite+quantiteCourante);
                                    tempSupprimer = true;
                                    break;
                                }
                            }
                            else
                            {
                                if(temp.getGs1_scannee().contentEquals(courant.getGs1_scannee()))
                                {
                                    newObjet.setQuantiteScannee(tempQuantite+quantiteCourante);
                                    tempSupprimer = true;
                                    break;
                                }
                            }

                            indexTemp++;
                        }

                        if(tempSupprimer)
                            listAEnvoyer.remove(indexTemp);

                        listAEnvoyer.add(newObjet);
                    }
                }
            }
        }

        for(ObjetReceptionScannee objet_courant : listAEnvoyer)
        {
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());

            PH_Reliquat ph_reliquat_courant = null;
            Produit produit_courant = null;

            if(gs1Decoupe.size() != 1 && !objet_courant.getGs1_scannee().startsWith("ci"))
            {
                produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            }
            else
            {
                String code_inconnu = objet_courant.getGs1_scannee();
                if(code_inconnu.startsWith("ci"))
                {
                    Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(code_inconnu);
                    code_inconnu = MapInconnu.get("Code_Inconnu");
                }

                List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, code_inconnu);

                if(list.size() == 1)
                    produit_courant = list.get(0);
                else
                {
                    for(Produit produit : list)
                    {
                        for(PH_Reliquat reliquat : liste_reliquat_commande_courante)
                        {
                            if(reliquat.getProduitID() == produit.getID_produit())
                            {
                                produit_courant = produit;
                                break;
                            }
                        }

                        if(produit_courant != null)
                        {
                            break;
                        }
                    }
                }
            }

            if(produit_courant != null)
            {
                String GS1 = "";
                if(produit_courant.isSerialiser_Reception_Delivrance())
                {
                    GS1 = gs1Decoupe.get(OutilsDecodage.gtin_Reconstruit_AvecSerie);
                }
                else
                {
                    GS1 = gs1Decoupe.get(OutilsDecodage.gtin_Reconstruit_SansSerie);
                }

                if(GS1 == null)
                {
                    GS1 = objet_courant.getGs1_scannee();
                }

                if(numero_commande_courante != null)
                {
                    ph_reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatByUnIdProduitetNumero(db, produit_courant.getID_produit(), numero_commande_courante);

                    if(ph_reliquat_courant != null)
                    {
                        Random randomactionligne = new Random();
                        int actionligneId = randomactionligne.nextInt();
                        if(actionligneId > 0)
                            actionligneId= actionligneId*-1;

                        ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, action.getId(), "PH_Reliquat", ph_reliquat_courant.getReliquat_UID(), GS1, objet_courant.getEmplacement_uid(), objet_courant.getQuantiteScannee(), produit_courant.getDesignation_interne());
                        ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                    }
                }
                else
                {
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if(actionligneId > 0)
                        actionligneId= actionligneId*-1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, action.getId(), "PH_Produits", produit_courant.getID_produit(), GS1, objet_courant.getEmplacement_uid(), objet_courant.getQuantiteScannee(), produit_courant.getDesignation_interne());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }
            }
        }

        onBackPressed();
    }
}
