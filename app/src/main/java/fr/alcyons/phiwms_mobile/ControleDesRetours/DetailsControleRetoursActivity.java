package fr.alcyons.phiwms_mobile.ControleDesRetours;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne_ControleRetour_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_ControleRetoursAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceAvecConnexionActivity;

/**
 * Created by olivier on 26/06/2019.
 */

public class DetailsControleRetoursActivity extends ServiceAvecConnexionActivity {

    Retour retourSelectionne;

    List<Retour_Ligne_ControleRetour_Adapte> retourLigneControleRetourAdapteList;
    ListView retourLigneControleRetourAdapteListView;
    Retour_Ligne_ControleRetoursAdapter retourLigneControleRetoursAdapter;
    Retour_Ligne_ControleRetoursAdapter.Retour_LigneViewHolder viewHolderAModifier;

    Serialisation serialisation;

    Context context;
    Spinner optionTri;
    String tri_choisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_controle_retours_new);

        //initialisation du context
        context = DetailsControleRetoursActivity.this;

        // Récupération des variables globales
        retourSelectionne = gestionnaireRetour.getRetourByID(db, intent.getExtras().getInt("retourSelectionneID"));

        serialisation = new Serialisation(DetailsControleRetoursActivity.this, db, utilisateurConnecte);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitulé());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero());

        // Récupération et initialisation de la listView
        retourLigneControleRetourAdapteListView = (ListView) findViewById(R.id.listeView);

        retourLigneControleRetourAdapteListView.setItemsCanFocus(true);
        retourLigneControleRetourAdapteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = retourLigneControleRetoursAdapter.retour_Lignes.get(position);
                Retour_Ligne retourLigne = gestionnaireRetour_Ligne.getRetourLigneByID(db, retourLigneAdapte.getRetourLigneID());
                Depot depot = gestionnaireDepot.getDepotParReference(db, retourSelectionne.getRef_Depot_Origine());

                viewHolderAModifier = retourLigneControleRetoursAdapter.retourLigneViewHolderList.get(position);

                Bundle DetailControleRetours_Bundle = DetailsControleRetoursActivity.super.getBundle();
                DetailControleRetours_Bundle.putInt("produitID", retourLigne.getCode_produit());
                DetailControleRetours_Bundle.putSerializable("retourLigneAdapte", retourLigneAdapte);
                DetailControleRetours_Bundle.putInt("depotID", depot.getDepot_UID());

                Intent DetailControleRetours_Intent = new Intent(DetailsControleRetoursActivity.this, ListeLotsControleDesRetoursActivity.class);
                DetailControleRetours_Intent.putExtras(DetailControleRetours_Bundle);
                DetailsControleRetoursActivity.this.startActivityForResult(DetailControleRetours_Intent, CodesEchangesActivites.RETOUR_LISTE_LOTS);
            }
        });

        // Initialisation de la liste contenant les Retour_Lignes
        retourLigneControleRetourAdapteList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();


        /* Code nécessaire afin de réaliser une requête à l' API */
        if (OutilsGestionConnexionReseau.isServerAccessible(DetailsControleRetoursActivity.this) && passageParOnCreate) {

            if (!swipeRefreshLayout.isRefreshing()) {
                mProgressDialog = ProgressDialog.show(DetailsControleRetoursActivity.this, "Veuillez patienter", "Synchronisation des stocks en cours");
            }

            RequestQueue requestQueue = Volley.newRequestQueue(DetailsControleRetoursActivity.this);
            String urlRequete = ParametresServeurOpenHelper.getPartieCommuneUrls(db) + DBOpenHelper.Urls.uriRequeteControleRetours+"/"+retourSelectionne.get_UID();

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, urlRequete, null,
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int nbResultat = response.getInt("resultCount");
                                if (nbResultat == 0) {
                                    String erreur = response.getString("erreur");
                                    if (erreur.equals(context.getString(R.string.tokenInvalide))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre identifiant de connexion est invalide, veuillez vous reconnecter.", "alerte");
                                    } else if (erreur.equals(context.getString(R.string.tokenExpire))) {
                                        Alerte.afficherAlerte(context, "Alerte", "Votre session de connexion est expirée, veuillez vous reconnecter.", "alerte");
                                    } else if (!erreur.contentEquals("Aucun PH_Retour trouvé")) {
                                        Alerte.afficherAlerte(context, "Erreur Requete", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Requete Service Contrôle des retours", "alerte");
                                    }
                                } else {
                                    JSONArray retourLignesJSONArray= response.getJSONArray("PH_Retour_Lignes");
                                    for (int k = 0; k < retourLignesJSONArray.length(); k++) {
                                        JSONObject retourLigneJSONObject = retourLignesJSONArray.getJSONObject(k);
                                        JSONArray stockLotEmplacementsJSONArray = retourLigneJSONObject.getJSONArray("ph_stock_lot_emplacements");

                                        for (int y = 0; y < stockLotEmplacementsJSONArray.length(); y++) {
                                            Stock_Lot_Emplacement_Light stockLotEmplacementLight = new Stock_Lot_Emplacement_Light(stockLotEmplacementsJSONArray.getJSONObject(y));
                                            gestionnaireStock_Lot_Emplacement.insererUnStock_Lot_EmplacementEnBDD(db, stockLotEmplacementLight);
                                        }
                                    }
                                }
                                passageParOnCreate = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.sendMessage(handler.obtainMessage());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley CdR", error.toString());
                            Alerte.afficherAlerte(DetailsControleRetoursActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur Volley : Contrôle des retours)", "alerte");
                        }
                    }
            ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", utilisateurConnecte.getToken());
                    return headers;
                }
            };
            obreq.setRetryPolicy(retryPolicy);
            requestQueue.add(obreq);
            try {
                Looper.loop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }

        // Récupération des retours_lignes du Retour présélectionné
        if (retourLigneControleRetourAdapteList.size() == 0) {
            for (Retour_Ligne retourLigne : gestionnaireRetour_Ligne.getAllRetourLignesByRetour(db, retourSelectionne)) {
                Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = new Retour_Ligne_ControleRetour_Adapte(retourLigne.get_UID());
                Produit produit = gestionnaireProduit.getProduitByID(db, retourLigne.getCode_produit());
                Depot depot = gestionnaireDepot.getDepotParReference(db, retourSelectionne.getRef_Depot_Origine());
                if(produit != null)
                {
                    for (Stock_Lot_Emplacement_Light stockLotEmplacement : gestionnaireStock_Lot_Emplacement.getAllStockLotEmplacementByProduitEtDepot(db, produit, depot)) {
                        retourLigneAdapte.getLotAdaptes().add(retourLigneAdapte.new LotAdapte(stockLotEmplacement));
                    }
                }
                retourLigneControleRetourAdapteList.add(retourLigneAdapte);
            }
        }

        tri_choisi = "Designation";
        optionTri = (Spinner) findViewById(R.id.optionTri);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.option_tri_retour_ligne, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        optionTri.setAdapter(adapter);
        if (tri_choisi != null) {
            int spinnerPosition = adapter.getPosition(tri_choisi);
            optionTri.setSelection(spinnerPosition);
        }
        optionTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                ((TextView) arg0.getChildAt(0)).setVisibility(View.INVISIBLE);
                String optionSelect = arg0.getItemAtPosition(position).toString();
                switch (optionSelect)
                {
                    case "Designation":
                        onClickTriDesignation();
                        break;
                    case "Place":
                        onClickTriParPlace();
                        break;

                    case "Catégorie":
                        onClickTriCategorie();
                        break;

                    case "Poids":
                        onClickTriParPoids();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_LISTE_LOTS:
                    Retour_Ligne_ControleRetour_Adapte retourLigneAdapte = retourLigneControleRetoursAdapter.retour_Lignes.get(retourLigneControleRetoursAdapter.retourLigneViewHolderList.indexOf(viewHolderAModifier));
                    retourLigneAdapte.setLotAdaptes((List<Retour_Ligne_ControleRetour_Adapte.LotAdapte>) data.getExtras().getSerializable("lotAdaptesList"));
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMenuSaveClick();
                return true;
            }
        });
        return true;
    }

    // Définition de l'action sur Click du bouton Save
    public void onMenuSaveClick() {

        int compteurReussiteGlobale = 0;
        int nbTotalLotsAvecValeurSaisie = 0;
        int compteurReussiteParLotAvecValeur = 0;

        for (Retour_Ligne_ControleRetour_Adapte retourLigneAdapte : retourLigneControleRetoursAdapter.retour_Lignes) {
            int nbLignesAvecValeur = 0;
            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : retourLigneAdapte.getLotAdaptes()) {
                if (lot.getQteSaisie() != 0) {
                    nbLignesAvecValeur++;
                    nbTotalLotsAvecValeurSaisie++;
                }
            }
            if (nbLignesAvecValeur == 0) {
                Alerte.afficherAlerte(DetailsControleRetoursActivity.this, "Alerte", "Tous les éléments n'ont pas été renseignés.", "alerte");
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
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), Integer.parseInt(ParametresServeurOpenHelper.getPortServeur(db)), "En attente", retourSelectionne.get_UID(), "", "Controle des retours");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        //fin de la création de l'action utilisateur


        for (Retour_Ligne_ControleRetour_Adapte retourLigneAdapte : retourLigneControleRetoursAdapter.retour_Lignes) {
            Retour_Ligne retourLigneCorrespondant = gestionnaireRetour_Ligne.getRetourLigneByID(db, retourLigneAdapte.getRetourLigneID());

            List<Retour_Ligne_ControleRetour_Adapte.LotAdapte> lotsSaisis = new ArrayList<>();
            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : retourLigneAdapte.getLotAdaptes()) {
                if (lot.getQteSaisie() != 0) {
                    lotsSaisis.add(lot);
                }
            }

            int compteurLotsDunSeulRetourLigne = 0;

            for (Retour_Ligne_ControleRetour_Adapte.LotAdapte lot : lotsSaisis) {
                Stock_Lot_Emplacement_Light stockLotEmplacementCourant = gestionnaireStock_Lot_Emplacement.getStock_Lot_EmplacementByID(db, lot.getStockLotEmplacementID());
                Retour_Ligne retourLigneCourant = new Retour_Ligne(retourLigneCorrespondant);

                retourLigneCourant.set_UID(retourLigneCorrespondant.get_UID()*-1);
                retourLigneCourant.setQte_Retourner(lot.getQteSaisie());
                retourLigneCourant.setLot_Retourner(lot.getNumLot());
                retourLigneCourant.setSerie_Retourner(lot.getNumSerie());
                retourLigneCourant.setPeremptionDate(lot.getDatePeremption());

                long rowID = gestionnaireRetour_Ligne.insererUnRetour_LigneEnBDD(db, retourLigneCourant);
                if (rowID != -1) {
                    compteurReussiteParLotAvecValeur++;
                    compteurLotsDunSeulRetourLigne++;
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneCourant.getPhiMR4UUID(), retourLigneCourant.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }

                if(!retourLigneCourant.getSerie_Retourner().contentEquals(""))
                {
                    Produit produit = ProduitOpenHelper.getProduitByID(db, retourLigneCourant.getCode_produit());
                    String gtin = produit.getGTIN();
                    if(gtin.length() > 14)
                    {
                        gtin = gtin.substring(2);
                    }
                    String peremption = "";
                    Date peremption_temp = null;
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat output = new SimpleDateFormat("yyMMdd");
                    try {
                        peremption_temp = input.parse(retourLigneCourant.getPeremptionDate());                 // parse input
                        peremption = output.format(peremption_temp);    // format output
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long uid_serialisation = serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, false, gtin, "GTIN", retourLigneCourant.getLot_Retourner(), peremption, retourLigneCourant.getSerie_Retourner(), "RETOUR", String.valueOf(retourLigneCourant.get_UID()), "", "");
                    PH_Serialisation serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int)uid_serialisation);
                    if(serialisation_courante != null)
                    {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, PH_SerialisationOpenHelper.Constantes.TABLE_PH_SERIALISATION, serialisation_courante.getSerialexpressUUID(), serialisation_courante.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    }
                }

            }

            if (compteurLotsDunSeulRetourLigne == lotsSaisis.size()) {
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigneCorrespondant.getPhiMR4UUID(), retourLigneCorrespondant.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                //gestion des actions lignes
                Random randomactionligne = new Random();
                int actionligneId = randomactionligne.nextInt();
                if(actionligneId > 0)
                    actionligneId= actionligneId*-1;

                ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigneCorrespondant.get_UID(), "", 0, (int)retourLigneCorrespondant.getQte_Retourner(), retourLigneCorrespondant.getProduit_Designation());
                ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
                gestionnaireRetour_Ligne.supprimerUnRetourLigne(db, retourLigneCorrespondant);
                compteurReussiteGlobale++;
            } else {
                compteurReussiteGlobale = 0;
            }
        }

        if (compteurReussiteParLotAvecValeur == nbTotalLotsAvecValeurSaisie) {
            retourSelectionne.setEn_Attente_de(getString(R.string.RepriseEffectuee));

            Date dateJour = new Date();
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            retourSelectionne.setDate_retour(format.format(dateJour));
            retourSelectionne.setDate_Validation(format.format(dateJour));

            long rowID = gestionnaireRetour.mettreAJourRetour(db, retourSelectionne);
            if (rowID != -1) {
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
            } else {
                // Si le retour n'est pas mis à jour, on remet le compteur à 0
                compteurReussiteGlobale = 0;
            }
        } else {
            compteurReussiteGlobale = 0;
        }

        if (compteurReussiteGlobale != retourLigneControleRetoursAdapter.retour_Lignes.size()) {
            // Si une erreur est survenue, on annule les modifications en vidant la table ElementASynchroniser
            Alerte.afficherAlerte(DetailsControleRetoursActivity.this, "Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", "alerte");
            gestionnaireElementASynchroniser.viderTableElementASynchroniser(db);
            DetailsControleRetoursActivity.this.finish();
            return;
        }

        Toast.makeText(DetailsControleRetoursActivity.this, "Retour contrôlé", Toast.LENGTH_SHORT).show();

        // Si possible, on essaie de mettre à jour les éléments
        if (OutilsGestionConnexionReseau.isServerAccessible(DetailsControleRetoursActivity.this)) {
            gestionnaireElementASynchroniser.toutSynchroniser(DetailsControleRetoursActivity.this, db, utilisateurConnecte, true);
        }
        DetailsControleRetoursActivity.this.finish();
        return;
    }

    public void affichageInfoCommentaire(View v) {
        String commentaireRetourner = Alerte.afficherAlerteInfoCommantaire(DetailsControleRetoursActivity.this, retourSelectionne.getCommentaire());
        if (commentaireRetourner != null) {
            retourSelectionne.setCommentaire(commentaireRetourner.trim());
        }
    }

    private void onClickTriDesignation()
    {
        tri_choisi = "Designation";
        Collections.sort(retourLigneControleRetourAdapteList, new Comparator<Retour_Ligne_ControleRetour_Adapte>() {
            @Override
            public int compare(Retour_Ligne_ControleRetour_Adapte o1, Retour_Ligne_ControleRetour_Adapte o2) {

                Retour_Ligne oo1 = Retour_LigneOpenHelper.getRetourLigneByID(db, o1.getRetourLigneID());
                Retour_Ligne oo2 = Retour_LigneOpenHelper.getRetourLigneByID(db, o2.getRetourLigneID());

                return oo1.getProduit_Designation().toLowerCase().compareTo(oo2.getProduit_Designation().toLowerCase());
            }
        });

        gestionAdapter();
    }

    private void onClickTriCategorie()
    {
        tri_choisi = "Catégorie";
        Collections.sort(retourLigneControleRetourAdapteList, new Comparator<Retour_Ligne_ControleRetour_Adapte>() {
            @Override
            public int compare(Retour_Ligne_ControleRetour_Adapte o1, Retour_Ligne_ControleRetour_Adapte o2) {

                Retour_Ligne oo1 = Retour_LigneOpenHelper.getRetourLigneByID(db, o1.getRetourLigneID());
                Retour_Ligne oo2 = Retour_LigneOpenHelper.getRetourLigneByID(db, o2.getRetourLigneID());

                Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
                Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());

                return produit1.getCategorie().toLowerCase().compareTo(produit2.getCategorie().toLowerCase());
            }
        });

        gestionAdapter();
    }


    private void onClickTriParPoids()
    {
        tri_choisi = "Poids";
        Collections.sort(retourLigneControleRetourAdapteList, new Comparator<Retour_Ligne_ControleRetour_Adapte>() {
            @Override
            public int compare(Retour_Ligne_ControleRetour_Adapte o1, Retour_Ligne_ControleRetour_Adapte o2) {

                Retour_Ligne oo1 = Retour_LigneOpenHelper.getRetourLigneByID(db, o1.getRetourLigneID());
                Retour_Ligne oo2 = Retour_LigneOpenHelper.getRetourLigneByID(db, o2.getRetourLigneID());

                Produit produit1 = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
                Produit produit2 = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());

                return  Double.compare(produit1.getPoids(), produit2.getPoids());
            }
        });

        gestionAdapter();
    }

    private void onClickTriParPlace()
    {
        tri_choisi = "Place";
        Collections.sort(retourLigneControleRetourAdapteList, new Comparator<Retour_Ligne_ControleRetour_Adapte>() {
            @Override
            public int compare(Retour_Ligne_ControleRetour_Adapte o1, Retour_Ligne_ControleRetour_Adapte o2) {

                Retour_Ligne oo1 = Retour_LigneOpenHelper.getRetourLigneByID(db, o1.getRetourLigneID());
                Retour_Ligne oo2 = Retour_LigneOpenHelper.getRetourLigneByID(db, o2.getRetourLigneID());
                String oo1EmplacementParDefaut = oo1.getEmplacementOrigine();
                String oo2EmplacementParDefaut = oo2.getEmplacementOrigine();

                if(oo1EmplacementParDefaut == null || oo1EmplacementParDefaut.contentEquals("")){
                    Produit produit = ProduitOpenHelper.getProduitByID(db, oo1.getCode_produit());
                    oo1EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();

                }
                if(oo2EmplacementParDefaut == null || oo2EmplacementParDefaut.contentEquals("")){
                    Produit produit = ProduitOpenHelper.getProduitByID(db, oo2.getCode_produit());
                    oo2EmplacementParDefaut = produit.getEmplacement_PUI_Defaut();
                }

                return oo1EmplacementParDefaut.compareTo(oo2EmplacementParDefaut);
            }
        });

        gestionAdapter();
    }

    public void gestionAdapter()
    {
        retourLigneControleRetoursAdapter = new Retour_Ligne_ControleRetoursAdapter(DetailsControleRetoursActivity.this, retourLigneControleRetourAdapteList, db);
        retourLigneControleRetourAdapteListView.setDivider(footer);
        retourLigneControleRetourAdapteListView.setAdapter(retourLigneControleRetoursAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent detailControleIntent = new Intent(DetailsControleRetoursActivity.this, ServiceControleRetoursActivity.class);
        Bundle detailControleBundle = super.getBundle();
        detailControleBundle.putString("Etat", "Retour");
        detailControleIntent.putExtras(detailControleBundle);
        DetailsControleRetoursActivity.this.startActivity(detailControleIntent);
        DetailsControleRetoursActivity.this.finish();
    }
}
