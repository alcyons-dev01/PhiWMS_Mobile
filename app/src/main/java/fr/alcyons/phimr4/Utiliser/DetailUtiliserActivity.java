package fr.alcyons.phimr4.Utiliser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureWithTakePicture;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_UtiliserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phimr4.Classes.ActionUtilisateur;
import fr.alcyons.phimr4.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.PH_Utiliser;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Produit_Utiliser_Adapte;
import fr.alcyons.phimr4.ListViewAdapters.Produit_UtiliserAdapter;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.Outils.OutilsGestionPhraseSnackbar;
import fr.alcyons.phimr4.PlanDePlacement.ListeZonesActivity;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceAvecConnexionActivity;
import fr.alcyons.phimr4.Stock.ListeStockActivity;

public class DetailUtiliserActivity extends ServiceAvecConnexionActivity {

    Depot depotOrigine;
    Produit_Utiliser_Adapte produitUtiliserAdapte;

    List<Produit_Utiliser_Adapte> produitUtiliserAdapteList;
    Produit_UtiliserAdapter produitUtiliserAdapter;
    ListView produitUtiliserListView;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_utiliser);

        //gestion du package manager
        pm = DetailUtiliserActivity.this.getPackageManager();

        produitUtiliserAdapteList = new ArrayList<>();
        if (savedInstanceState != null) {
            produitUtiliserAdapteList = (List<Produit_Utiliser_Adapte>) savedInstanceState.getSerializable("listeProduitAdaptes");
        }

        // Récupération du dépot sélectionné
        depotOrigine = gestionnaireDepot.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));

        //Récupération de la listView
        produitUtiliserListView = (ListView) findViewById(R.id.listeView);

        if (produitUtiliserAdapteList.size() == 0) {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                onClick_boutonScanDatamatrix(DetailUtiliserActivity.this.getCurrentFocus());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        ((TextView) findViewById(R.id.nbElementInAdapter)).setText(String.valueOf(produitUtiliserAdapteList.size()));

        if (produitUtiliserAdapteList.size() > 0) {
            produitUtiliserAdapter = new Produit_UtiliserAdapter(DetailUtiliserActivity.this, produitUtiliserAdapteList, db);
            produitUtiliserListView.setAdapter(produitUtiliserAdapter);
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_CODE_GS1:
                    List<String> listeString = data.getExtras().getStringArrayList("listeString");
                    if(listeString == null)
                    {
                        listeString = data.getExtras().getStringArrayList("stringList");
                    }
                    if(listeString != null)
                    {
                        int compteurErreur = 0;
                        for (String gs1 : listeString) {

                            if (gs1.length() == 13) {
                                Produit_Utiliser_Adapte produitAdapteCourant = new Produit_Utiliser_Adapte(gs1);
                                if (produitUtiliserAdapteList.contains(produitAdapteCourant)) {
                                    Produit_Utiliser_Adapte produitAdapte = produitUtiliserAdapteList.get(produitUtiliserAdapteList.indexOf(produitAdapteCourant));
                                    produitAdapte.setQte(produitAdapte.getQte() + 1);
                                } else {
                                    produitUtiliserAdapteList.add(produitAdapteCourant);
                                }
                            } else {
                                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);

                                String codeGTIN = gs1Decoupe.get(OutilsDecodage.codeGtin);
                                if (codeGTIN == null) {
                                    codeGTIN = "";
                                }
                                List<Produit> produitsCourants = gestionnaireProduit.getProduitsParGTIN(db, codeGTIN);

                                if (produitsCourants.size() != 1) {
                                    // Si la liste ne contient pas précisément un produit, une erreur est suvenue et le produit ne sera pas ajouté.
                                    compteurErreur++;
                                } else {
                                    Produit produit = produitsCourants.get(0);
                                    Produit_Utiliser_Adapte produitAdapteCourant = new Produit_Utiliser_Adapte(db, produit, gs1, depotOrigine);
                                    if (produitUtiliserAdapteList.contains(produitAdapteCourant)) {
                                        Produit_Utiliser_Adapte produitAdapte = produitUtiliserAdapteList.get(produitUtiliserAdapteList.indexOf(produitAdapteCourant));
                                        produitAdapte.setQte(produitAdapte.getQte() + 1);
                                    } else {
                                        produitUtiliserAdapteList.add(produitAdapteCourant);
                                    }
                                }
                            }

                        }
                        if (compteurErreur > 0) {
                            Alerte.afficherAlerte(DetailUtiliserActivity.this, "Alerte", "Impossible d'identifier " + String.valueOf(compteurErreur) + " produits que vous avez scannés, veuillez les ajouter manuellement.", "alerte");
                        }
                    }
                    break;
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    if (produitUtiliserAdapte != null) {
                        Depot_Zone zone = gestionnaireZone.getUneZoneByID(db, data.getExtras().getInt("zoneSelectionneeID"));
                        Depot_Emplacement emplacement = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));

                        produitUtiliserAdapte.setZoneID(zone.getZoneID());
                        produitUtiliserAdapte.setEmplacementID(emplacement.get_UID());
                    }
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    if (produitUtiliserAdapte != null) {
                        produitUtiliserAdapte.setNumLot(data.getStringExtra("numeroLot").trim());
                        produitUtiliserAdapte.setDatePeremption(data.getStringExtra("datePeremption"));
                        produitUtiliserAdapte.setId(data.getIntExtra("produitID", 0));

                        Produit produit = gestionnaireProduit.getProduitByID(db, data.getIntExtra("produitID", 0));

                        if (produit != null) {
                            String codeGS1 = "01" + produit.getGTIN();
                            produitUtiliserAdapte.setCodeGS1(codeGS1);
                        }

                        Depot_Zone depotZone = gestionnaireZone.getZoneByDepotEtNom(db, depotOrigine, data.getStringExtra("zoneNom"));
                        if(depotZone != null){
                            produitUtiliserAdapte.setZoneID(depotZone.getZoneID());
                            Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementZoneEtNom(db, depotZone, data.getStringExtra("emplacementNom"));
                            if(depotEmplacement != null){
                                produitUtiliserAdapte.setEmplacementID(depotEmplacement.get_UID());
                            }
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
        else
        {
            afficherSnackbarUtiliser("AucuneZone");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putSerializable("listeProduitAdaptes", (Serializable) produitUtiliserAdapteList);
        super.onSaveInstanceState(outstate);
    }

    public void onClick_boutonValider(View v){
        int compteurReussite = 0;

        if (produitUtiliserAdapteList.size() > 0) {
            for (Produit_Utiliser_Adapte produitUtiliserAdapte : produitUtiliserAdapteList) {
                if (produitUtiliserAdapte.getZoneID() == 0 || produitUtiliserAdapte.getEmplacementID() == 0) {
                    Alerte.afficherAlerte(DetailUtiliserActivity.this, "Alerte", "Tous les produits utilisés n'ont pas de zone ou d'emplacement.", "alerte");
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
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", 0, "", "Utiliser");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            //fin de la création de l'action utilisateur

            //Création des PH_Utiliser Correspondant
            for (Produit_Utiliser_Adapte produitUtiliserAdapte : produitUtiliserAdapteList) {

                int _UID =  UUID.randomUUID().hashCode();
                int depotUID = depotOrigine.getDepot_UID();
                double lat = depotOrigine.getLatitude();
                double lng = depotOrigine.getLongitude();
                String lot = produitUtiliserAdapte.getNumLot();
                String peremptionDate = produitUtiliserAdapte.getDatePeremption();
                int quantiteUtilisee = produitUtiliserAdapte.getQte();
                boolean controleEffectue = false;
                int controleQuantite = 0;

                Date d = new Date();
                SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleDateFormatHeure = new SimpleDateFormat("HH:mm:ss");

                String utilisationDate = simpleDateFormatDate.format(d);
                String utilisationHeure = simpleDateFormatHeure.format(d);

                int photoUID = _UID;
                String photoNom = produitUtiliserAdapte.getImage();

                // Produit
                int produitUID = 0;
                boolean produitSoumisTracabilite = false;

                Produit produitCorrespondant = gestionnaireProduit.getProduitByID(db, produitUtiliserAdapte.getId());
                if(produitCorrespondant != null){
                    produitUID = produitCorrespondant.getID_produit();
                    produitSoumisTracabilite = produitCorrespondant.isSuivi_Lot();
                }

                // Depot_Zone
                int zoneUID = 0;

                Depot_Zone depot_zone = gestionnaireZone.getUneZoneByID(db, produitUtiliserAdapte.getZoneID());
                if(depot_zone != null){
                    zoneUID = depot_zone.getZoneID();
                }

                // Depot_Emplacement
                int emplacementUID = 0;
                Depot_Emplacement depot_emplacement = gestionnaireEmplacement.getUnEmplacementByID(db, produitUtiliserAdapte.getEmplacementID());
                if(depot_emplacement != null){
                    emplacementUID = depot_emplacement.get_UID();
                }

                // Création et insertion en base du PH_Utiliser
                PH_Utiliser phUtiliser = new PH_Utiliser(_UID, lat, lng, quantiteUtilisee, zoneUID, emplacementUID, depotUID, photoNom, photoUID, produitUID, utilisationDate, utilisationHeure, controleEffectue, lot, peremptionDate, controleQuantite, produitSoumisTracabilite);
                int phUtiliserPhiMR4uid = (int) gestionnairePH_Utiliser.insererPH_UtiliserEnBDD(db, phUtiliser);

                // Ajout du PH_Utiliser au ElementASynchroniser
                long rowId = gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_UtiliserOpenHelper.Constantes.TABLE_PH_UTILISER, phUtiliserPhiMR4uid, phUtiliser.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.AJOUT);
                if (rowId != -1) {
                    compteurReussite++;
                    //gestion des actions lignes
                    Random randomactionligne = new Random();
                    int actionligneId = randomactionligne.nextInt();
                    if(actionligneId > 0)
                        actionligneId= actionligneId*-1;

                    ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Utiliser", phUtiliser.get_UID(), "", 0, (int)phUtiliser.getQuantiteUtilisee(), produitCorrespondant.getDesignation_interne());
                    ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                }
            }

            if (compteurReussite == produitUtiliserAdapteList.size()) {
                 if (OutilsGestionConnexionReseau.isServerAccessible(DetailUtiliserActivity.this)) {
                    gestionnaireElementASynchroniser.toutSynchroniser(DetailUtiliserActivity.this, db, utilisateurConnecte, true);
                }

                Toast.makeText(DetailUtiliserActivity.this, "Enregistrement en cours", Toast.LENGTH_SHORT).show();
                DetailUtiliserActivity.this.finish();
                return;
            } else {
                Alerte.afficherAlerte(DetailUtiliserActivity.this, "Alerte", "Une erreur est survenue", "alerte");
                gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
                DetailUtiliserActivity.this.finish();
                return;
            }
        } else {
            Alerte.afficherAlerte(DetailUtiliserActivity.this, "Attention", "Veuillez scanner au moins un produit pour valider", "alerte");

        }
    }

    public void onClick_boutonScanDatamatrix(View v){
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            List<String> listeCodesGS1 = new ArrayList<>();

            for (Produit_Utiliser_Adapte produitUtiliserAdapte : produitUtiliserAdapteList) {
                for (int i = 0; i < produitUtiliserAdapte.getQte(); i++) {
                    listeCodesGS1.add(produitUtiliserAdapte.getCodeGS1());
                }
            }

            Bundle DetailUtiliser_Bundle = DetailUtiliserActivity.super.getBundle();
            DetailUtiliser_Bundle.putBoolean("isBoutonSuppressionExistant", true);
            DetailUtiliser_Bundle.putBoolean("modePhoto", true);
            DetailUtiliser_Bundle.putBoolean("modeCumule", true);
            DetailUtiliser_Bundle.putBoolean("modeRafale", true);
            DetailUtiliser_Bundle.putStringArrayList("stringList", (ArrayList) listeCodesGS1);

            Intent DetailUtiliser_Intent = new Intent(DetailUtiliserActivity.this, ScannerSearchOnlyActivity.class);
            DetailUtiliser_Intent.putExtras(DetailUtiliser_Bundle);

            DetailUtiliserActivity.this.startActivityForResult(DetailUtiliser_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_GS1);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                List<String> listeCodesGS1 = new ArrayList<>();

                for (Produit_Utiliser_Adapte produitUtiliserAdapte : produitUtiliserAdapteList) {
                    for (int i = 0; i < produitUtiliserAdapte.getQte(); i++) {
                        listeCodesGS1.add(produitUtiliserAdapte.getCodeGS1());
                    }
                }

                Bundle DetailUtiliser_Bundle = DetailUtiliserActivity.super.getBundle();
                DetailUtiliser_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                DetailUtiliser_Bundle.putBoolean("modeRafale", true);
                DetailUtiliser_Bundle.putBoolean("modePhoto", true);
                DetailUtiliser_Bundle.putStringArrayList("stringList", (ArrayList) listeCodesGS1);

                Intent DetailUtiliser_Intent = new Intent(DetailUtiliserActivity.this, BarcodeCaptureWithTakePicture.class);
                DetailUtiliser_Intent.putExtras(DetailUtiliser_Bundle);

                DetailUtiliserActivity.this.startActivityForResult(DetailUtiliser_Intent, CodesEchangesActivites.RETOUR_LISTE_CODE_GS1);
            }
            else
            {
                Alerte.afficherAlerte(DetailUtiliserActivity.this, "Erreur", "Vous n'avez pas d'appareil photo", "alerte");
            }

        }

    }

    // Permet de lancer l'activity permettant de sélectionné une zone et emplacement
    public void modifierZoneEtEmplacementProduit(Produit_Utiliser_Adapte produitUtiliserAdapte) {
        this.produitUtiliserAdapte = produitUtiliserAdapte;

        Bundle DetailUtiliser_Bundle = super.getBundle();
        DetailUtiliser_Bundle.putInt("depotSelectionneID", depotOrigine.getDepot_UID());

        Intent DetailUtiliser_Intent = new Intent(DetailUtiliserActivity.this, ListeZonesActivity.class);
        DetailUtiliser_Intent.putExtras(DetailUtiliser_Bundle);

        DetailUtiliserActivity.this.startActivityForResult(DetailUtiliser_Intent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
    }

    // Permet de mofifier le lot du produit sélectionné
    public void modifierProduit(Produit_Utiliser_Adapte produitUtiliserAdapte) {
        this.produitUtiliserAdapte = produitUtiliserAdapte;

        Intent newIntent = new Intent(DetailUtiliserActivity.this, ListeStockActivity.class);
        Bundle extras = super.getBundle();
        extras.putInt("depotUID_Selectionne", depotOrigine.getDepot_UID());
        extras.putBoolean("contexteUtiliser",true);
        newIntent.putExtras(extras);

        DetailUtiliserActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    private void afficherSnackbarUtiliser(String nomService)
    {
        String erreur = OutilsGestionPhraseSnackbar.obtenirPhraseSnackbar(nomService);
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>"+erreur+"</b>", 0), Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();
    }
}
