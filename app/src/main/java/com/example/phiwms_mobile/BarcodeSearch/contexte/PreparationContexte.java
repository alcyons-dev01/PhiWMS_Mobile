package com.example.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.SurveillanceReferenceOpenHelper;
import com.example.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import com.example.phiwms_mobile.Classes.PH_Preparation;
import com.example.phiwms_mobile.Classes.PH_Preparation_Ligne;
import com.example.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import com.example.phiwms_mobile.Classes.PH_Serialisation;
import com.example.phiwms_mobile.Classes.Produit;
import com.example.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import com.example.phiwms_mobile.Classes.SurveillanceReference;
import com.example.phiwms_mobile.Classes.Utilisateur;
import com.example.phiwms_mobile.Outils.Alerte;
import com.example.phiwms_mobile.Outils.CodesEchangesActivites;
import com.example.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import com.example.phiwms_mobile.Outils.OutilsDecodage;
import com.example.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import com.example.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import com.example.phiwms_mobile.OutilsSerialisation.GestionResultatNMVO;
import com.example.phiwms_mobile.OutilsSerialisation.Serialisation;
import com.example.phiwms_mobile.R;

/**
 * Created by olivier on 28/03/2019.
 */

public class PreparationContexte {


    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    private List<String> ListeGTIN;
    private int userId;
    private TextView message;
    private TextView messageFranceMVO;
    private FloatingActionButton boutonSuppression;

    public List<String> stringList;
    public Map<Integer, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>> tableau_renvoyer;
    private String messageTexte;
    private String messageTexteFranceMVO;
    private int messageColor;
    private ToneGenerator toneGen1;
    private Utilisateur utilisateur;
    private PH_Preparation preparation_courante;
    public int nb_produit_scanne;
    private Serialisation serialisation;

    private List<PH_Preparation_Ligne>liste_preparation_ligne;
    public List<PH_Preparation_Ligne_Preparation_Adapte> liste_preparation_liste_adapte;


    public PreparationContexte(final Context context, final SQLiteDatabase db, List<String> ListeGTIN, int userId, TextView message, TextView messageFranceMVO, FloatingActionButton boutonSuppression, int preparation_id, List<PH_Preparation_Ligne_Preparation_Adapte> listedejascanne, int nb_produit_scanne){
        this.context = context;
        this.db = db;
        this.ListeGTIN = ListeGTIN;
        this.userId = userId;
        this.message = message;
        this.messageFranceMVO = messageFranceMVO;

        this.boutonSuppression = boutonSuppression;
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        messageFranceMVO.setVisibility(View.VISIBLE);

        code = "";
        bannerTexte = "Scanner le datamatrix d'un produit";
        scannerContexteProduit = String.valueOf(R.string.scannerContextePreparation);

        stringList = new ArrayList<>();
        this.nb_produit_scanne = nb_produit_scanne;
        tableau_renvoyer = new HashMap<Integer, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>>();
        utilisateur = UtilisateurOpenHelper.getUtilisateurByID(db, userId);
        serialisation = new Serialisation(context, db, utilisateur);
        preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparation_id);
        liste_preparation_ligne = new ArrayList<>();
        liste_preparation_liste_adapte = listedejascanne;

        if(liste_preparation_liste_adapte == null)
        {
            liste_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesAPreparerParPHPreparation(db, preparation_courante);
            liste_preparation_liste_adapte = new ArrayList<>();
            for(int i = 0; i < liste_preparation_ligne.size(); i++)
            {
                PH_Preparation_Ligne_Preparation_Adapte nouveau = new PH_Preparation_Ligne_Preparation_Adapte(liste_preparation_ligne.get(i).get_UID());
                liste_preparation_liste_adapte.add(nouveau);
            }
        }
    }

    public void onTextWatcher(final Editable s){

    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    String codeRetourne = data.getStringExtra("code");
                    if (!codeRetourne.contentEquals(""))
                        code = codeRetourne;
                    break;
                default: //CodesEchangesActivites.RETOUR_LISTE_CODE_GS1 et CodesEchangesActivites.RESULT_BOUTON_FERMETURE_BARCODE_SEARCH
                    stringList.clear();
                    List<String> stringRetourList = data.getExtras().getStringArrayList("stringList");

                    for (String code : stringRetourList) {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        if (gs1Decoupe.size() != 0) {
                            if(stringList.indexOf(gs1Decoupe.get(OutilsDecodage.numeroSerie)) == -1)
                                stringList.add(gs1Decoupe.get(OutilsDecodage.numeroSerie));
                        }
                    }
                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        boolean confirmation = true;
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(chaine);
        if(stringList.indexOf(chaine) == -1)
        {

            if(chaine.contentEquals("Code_Trac_Med_ALCYONS") || chaine.contentEquals("Code_Trac_Dm_ALCYONS"))
            {
                Produit produit = ProduitOpenHelper.getUnProduitByCodeInconnu(db, chaine);
                if(produit !=null)
                {
                    PH_Preparation_Ligne preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getUnPHPreparationLignesAPreparerParPHPreparationetProduit(db, preparation_courante, produit.getID_produit());

                    if(preparation_ligne_courant!= null)
                    {
                        messageTexteFranceMVO = "";
                        Stock_Lot_Emplacement_Light nouveau_stock_lot_emplacement = new Stock_Lot_Emplacement_Light(produit.getCond_achat(),"", gs1Decoupe.get(OutilsDecodage.dateDePeremption), produit.getEmplacement_PUI_Defaut(), preparation_courante.getDepotDestinataireReference(), produit.getZone_PUI_Defaut(),produit.getID_produit(), produit.getCond_achat(), "");


                        for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                        {
                            if(liste_preparation_liste_adapte.get(i).getPh_preparationLigneID() == preparation_ligne_courant.get_UID())
                            {
                                List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>liste = liste_preparation_liste_adapte.get(i).getLotAdaptes();
                                PH_Preparation_Ligne_Preparation_Adapte.LotAdapte nouveauLot = liste_preparation_liste_adapte.get(i).new LotAdapte(nouveau_stock_lot_emplacement);
                                boolean deja_present = false;
                                int quantite_scanne = 0;
                                for(int j = 0; j < liste.size(); j++)
                                {
                                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot_courant = liste.get(j);
                                    if(lot_courant.getNumSerie().contentEquals(nouveauLot.getNumSerie()) && lot_courant.getNumLot().contentEquals(nouveauLot.getNumLot()))
                                    {
                                        deja_present = true;
                                    }

                                    quantite_scanne = quantite_scanne+lot_courant.getQteSaisie();
                                }

                                if(!deja_present)
                                {
                                    if(quantite_scanne >= preparation_ligne_courant.getQte_Demander())
                                    {
                                        messageTexteFranceMVO = "Quantité demandée atteinte";
                                    }
                                    else
                                    {
                                        if(nouveauLot.getQteSaisie() > preparation_ligne_courant.getQte_Demander())
                                        {
                                            nouveauLot.setQteSaisie(preparation_ligne_courant.getQte_Demander());
                                        }
                                        liste_preparation_liste_adapte.get(i).getLotAdaptes().add(nouveauLot);
                                        nb_produit_scanne++;
                                        messageTexte = "Produit ajouté";
                                        messageColor = Color.GREEN;
                                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                        messageTexteFranceMVO = "";
                                    }
                                }
                                else
                                {
                                    messageTexte = "Produit déjà scanné";
                                    messageColor = Color.RED;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            else if (gs1Decoupe.size() > 1 && !gs1Decoupe.get(OutilsDecodage.numeroLot).contentEquals("") && !gs1Decoupe.get(OutilsDecodage.numeroSerie).contentEquals("")) {
                String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                String date_peremtion = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                date_peremtion = date_peremtion.substring(2);
                date_peremtion = date_peremtion.replace("-", "");
                String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
                String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);

                Produit produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                PH_Preparation_Ligne preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getUnPHPreparationLignesAPreparerParPHPreparationetProduit(db, preparation_courante, produit_courant.getID_produit());

                if(preparation_ligne_courant != null)
                {
                    String resultat = "";
                    boolean differe = false;
                    if(!serie.contentEquals(""))
                    {
                        if (!OutilsGestionConnexionReseau.isServerAccessible(context))
                            differe = true;

                        if(conditionnement.contentEquals(""))
                        {
                            Produit produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                            if(produit == null)
                            {
                                produit = ProduitOpenHelper.getUnProduitParGTIN(db, "01"+gtin);
                            }

                            conditionnement = String.valueOf(produit.getCond_achat());
                        }
                        String ancien_serie = serie;
                        String last_char = serie.substring(serie.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            serie = serie.substring(0, serie.length()-1);
                        }

                        last_char = lot.substring(lot.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            lot = lot.substring(0, lot.length()-1);
                        }

                        last_char = conditionnement.substring(conditionnement.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            conditionnement = conditionnement.substring(0, conditionnement.length()-1);
                        }
                        long ph_serialisation_uid = 0;
                        if(preparation_courante != null)
                        {
                            ph_serialisation_uid = serialisation.Serialisation_Verifier(userId, false, differe, gtin, "GTIN", lot, date_peremtion, serie, "Vérification", String.valueOf(preparation_courante.getUID()), "", "");
                        }
                        else
                        {
                            ph_serialisation_uid = serialisation.Serialisation_Verifier(userId, false, differe, gtin, "GTIN", lot, date_peremtion, serie, "Vérification", gtin, "", "");
                        }

                        PH_Serialisation serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int)ph_serialisation_uid);
                        if(serialisation == null)
                        {
                            serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int)ph_serialisation_uid);
                        }

                        resultat = serialisation.getResultat();
                        if(resultat.contentEquals("INACTIVE") || resultat.contentEquals("UNKNOWN"))
                        {
                            Alerte.afficherAlerte(context, "Attention", "Le produit scanné est détruit ou inactif", "alerte");
                            messageTexteFranceMVO = GestionResultatNMVO.getResultat(serialisation.getRaison());
                            messageColor = Color.RED;
                            Random SurveillanceReferenceRandom = new Random();
                            int id_surveillance = SurveillanceReferenceRandom.nextInt();
                            if (id_surveillance > 0) {
                                id_surveillance = id_surveillance * -1;
                            }
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String surveillanceDate = sdf.format(calendar.getTime());

                            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                            String surveillanceHeure = mdformat.format(calendar.getTime());

                            int produit_id = serialisation.getProduitUID();
                            int serialisationID = serialisation.get_UID();
                            String motif = GestionCodeErreurNMVO.getMessage(code);
                            String actionAMener = "";
                            String statut = "NON LU";
                            String traitePar = utilisateur.getIdentifiant();
                            String traiteDate = surveillanceDate;
                            String traiteHeure = surveillanceHeure;
                            String produitLot = serialisation.getNumeroLot();
                            String produitDatePéremption = serialisation.getDatePeremptionAAMMJJ();
                            String produitNumeroSerie = serialisation.getNumeroSerie();

                            SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                            long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);

                            if (rowUID_surveillance != -1) {
                                // ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                try {
                                    EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                    class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateur.getMail(), db);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else
                        {
                            messageTexteFranceMVO = "";
                            Stock_Lot_Emplacement_Light nouveau_stock_lot_emplacement = new Stock_Lot_Emplacement_Light(produit_courant.getCond_achat(),lot, gs1Decoupe.get(OutilsDecodage.dateDePeremption), produit_courant.getEmplacement_PUI_Defaut(), preparation_courante.getDepotDestinataireReference(), produit_courant.getZone_PUI_Defaut(),produit_courant.getID_produit(), produit_courant.getCond_achat(), serie);

                            for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                            {
                                if(liste_preparation_liste_adapte.get(i).getPh_preparationLigneID() == preparation_ligne_courant.get_UID())
                                {
                                    List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>liste = liste_preparation_liste_adapte.get(i).getLotAdaptes();
                                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte nouveauLot = liste_preparation_liste_adapte.get(i).new LotAdapte(nouveau_stock_lot_emplacement);
                                    boolean deja_present = false;
                                    int quantite_scanne = 0;
                                    for(int j = 0; j < liste.size(); j++)
                                    {
                                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot_courant = liste.get(j);
                                        if(lot_courant.getNumSerie().contentEquals(nouveauLot.getNumSerie()) && lot_courant.getNumLot().contentEquals(nouveauLot.getNumLot()))
                                        {
                                            deja_present = true;
                                        }

                                        quantite_scanne = quantite_scanne+lot_courant.getQteSaisie();
                                    }

                                    if(!deja_present)
                                    {
                                        if(quantite_scanne >= preparation_ligne_courant.getQte_Demander())
                                        {
                                            messageTexteFranceMVO = "Quantité demandée atteinte";
                                        }
                                        else
                                        {
                                            if(nouveauLot.getQteSaisie() > preparation_ligne_courant.getQte_Demander())
                                            {
                                                nouveauLot.setQteSaisie(preparation_ligne_courant.getQte_Demander());
                                            }
                                            liste_preparation_liste_adapte.get(i).getLotAdaptes().add(nouveauLot);
                                            nb_produit_scanne++;
                                            messageTexte = "Produit ajouté";
                                            messageColor = Color.GREEN;
                                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                            messageTexteFranceMVO = "";
                                        }
                                    }
                                    else
                                    {
                                        messageTexte = "Produit déjà scanné";
                                        messageColor = Color.RED;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                else
                {
                    messageTexte = "Mauvais produit scanné";
                    messageColor = Color.RED;
                }
            }
            else if(!gs1Decoupe.get(OutilsDecodage.numeroLot).contentEquals("") && !gs1Decoupe.get(OutilsDecodage.codeGtin).contentEquals("") && !gs1Decoupe.get(OutilsDecodage.dateDePeremption).contentEquals(""))
            {
                Produit produit_scanne = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                Stock_Lot_Emplacement_Light nouveau_stock_lot_emplacement = new Stock_Lot_Emplacement_Light(produit_scanne.getCond_achat(), gs1Decoupe.get(OutilsDecodage.numeroLot), gs1Decoupe.get(OutilsDecodage.dateDePeremption), produit_scanne.getEmplacement_PUI_Defaut(), preparation_courante.getDepotDestinataireReference(), produit_scanne.getZone_PUI_Defaut(),produit_scanne.getID_produit(), produit_scanne.getCond_achat(),"");
                PH_Preparation_Ligne preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getUnPHPreparationLignesAPreparerParPHPreparationetProduit(db, preparation_courante, produit_scanne.getID_produit());

                if(preparation_ligne_courant != null)
                {
                    for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                    {
                        if(liste_preparation_liste_adapte.get(i).getPh_preparationLigneID() == preparation_ligne_courant.get_UID())
                        {

                            List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>liste = liste_preparation_liste_adapte.get(i).getLotAdaptes();
                            PH_Preparation_Ligne_Preparation_Adapte.LotAdapte nouveauLot = liste_preparation_liste_adapte.get(i).new LotAdapte(nouveau_stock_lot_emplacement);
                            boolean deja_present = false;
                            int quantite_scanne = 0;
                            for(int j = 0; j < liste.size(); j++)
                            {
                                PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot_courant = liste.get(j);
                                if(lot_courant.getNumSerie().contentEquals(nouveauLot.getNumSerie()) && lot_courant.getNumLot().contentEquals(nouveauLot.getNumLot()))
                                {
                                    deja_present = true;
                                }

                                quantite_scanne = quantite_scanne+lot_courant.getQteSaisie();
                            }

                            if(!deja_present)
                            {
                                if(quantite_scanne >= preparation_ligne_courant.getQte_Demander())
                                {
                                    messageTexteFranceMVO = "Quantité demandée atteinte";
                                }
                                else
                                {
                                    if(nouveauLot.getQteSaisie() > preparation_ligne_courant.getQte_Demander())
                                    {
                                        nouveauLot.setQteSaisie(preparation_ligne_courant.getQte_Demander());
                                    }
                                    liste_preparation_liste_adapte.get(i).getLotAdaptes().add(nouveauLot);
                                    nb_produit_scanne++;
                                    messageTexte = "Produit ajouté";
                                    messageColor = Color.GREEN;
                                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                                    messageTexteFranceMVO = "";
                                }

                            }
                            else
                            {
                                messageTexte = "Produit déjà scanné";
                                messageColor = Color.RED;
                            }
                            break;
                        }
                    }
                }
                else
                {
                    messageTexte = "Produit introuvable";
                    messageColor = Color.RED;
                }
            }
            else
            {
                messageTexte = "Produit inconnu";
                messageColor = Color.RED;
            }
            stringList.add(chaine);
        }
        else
        {
            messageTexte = "Produit déjà scanné";
            messageColor = Color.RED;
        }

        message.setVisibility(View.VISIBLE);
        message.setText(messageTexte);
        message.setBackgroundColor(messageColor);
        messageFranceMVO.setText(messageTexteFranceMVO);

        return confirmation;
    }
}

