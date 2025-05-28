package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.ToneGenerator;
import android.text.Editable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;

public class PreparationMultipleContext
{
    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    private List<String> ListeGTIN;
    private int userId;

    public List<String> stringList;
    public Map<Integer, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>> tableau_renvoyer;
    private ToneGenerator toneGen1;
    private Utilisateur utilisateur;
    private PH_Preparation preparation_courante;
    private Serialisation serialisation;
    public int preparation_ligne_id;
    private PH_Preparation_Ligne peparationLigne;
    private List<String> listeSerie;
    private Serialisation serialisationService;
    private Utilisateur utilisateurConnecte;
    public boolean codeInconnu;
    List<PH_Preparation_Ligne> liste_ph_preparation_ligne;
    public PH_Preparation_Ligne_Preparation_Adapte.LotAdapte lot_courant;
    public PH_Preparation_Ligne_Preparation_Adapte.LotAdapte ancien_lot;
    public PH_Preparation_Ligne_Preparation_Adapte phPreparationLignePreparationAdapte;
    public List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List;
    public List<String> liste_lot;
    public boolean new_lot;
    public Produit produit = null;
    public Depot_Emplacement emplacement_courant;
    boolean validation;
    public String emplacementDisponible;
    public List<Integer> liste_emplacement_disponible;

    public List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte> liste_preparation_liste_adapte;

    public PreparationMultipleContext(final Context context, final SQLiteDatabase db, List<String> ListeGTIN, int userId, int preparation_id, List<PH_Preparation_Ligne_Preparation_Adapte> phPreparationLignePreparationAdapte_List, List<String> liste_lot){
        this.context = context;
        this.db = db;
        this.ListeGTIN = ListeGTIN;
        this.userId = userId;
        this.preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparation_id);
        this.listeSerie = new ArrayList<>();
        this.liste_lot = liste_lot;
        if(this.liste_lot == null)
            this.liste_lot = new ArrayList<>();
        this.new_lot = false;
        this.phPreparationLignePreparationAdapte_List = phPreparationLignePreparationAdapte_List;
        this.utilisateurConnecte = UtilisateurOpenHelper.getUtilisateurByID(db, userId);
        this.liste_ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, preparation_courante);
        serialisationService = new Serialisation(context, db, utilisateurConnecte);
        this.stringList = new ArrayList<>();
        validation = true;
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();

        if (chaine.endsWith("\n"))
        {
            chaine = chaine.toString().substring(0, chaine.length() - 1);
        }

        if(chaine.startsWith("PHITAGPLACE+"))
        {
            String[] tab_emplacement = chaine.split(":");
            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);
        }
        else if(emplacement_courant == null && utilisateurConnecte.getEtablissement().contentEquals("ADH"))
        {
            ((ScannerPreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
        }
        else
        {
            if(lot_courant == null)
            {
                if (!validation) {
                    validation = false;
                }
                else
                {
                    validation = false;
                }
                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(chaine);
                String lot = "";
                String serie = "";
                String gtin_courant ="";
                String conditionnementString ="";
                String date_peremption_courant="";
                if (gs1Decoupe.size() != 1)
                {

                    List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                    if(produits != null)
                    {
                        if (produits.size() == 1) {
                            produit = produits.get(0);
                        }
                        else if(produits.size() > 1)
                        {
                            String activityName = context.getClass().getSimpleName();
        /*                          if(activityName.contentEquals("BarcodeCaptureActivity"))
                                {
                                    ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                                }
                                else
                                {
                                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                                }*/
                            lot_courant = null;
                            code = "";
                        }
                    }
                    lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                    serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                    gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                    conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                    date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                }
                else
                {
                    List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, chaine);
                    if(produits != null)
                    {
                        if (produits.size() == 1) {
                            produit = produits.get(0);
                            codeInconnu = true;
                        }
                        else if(produits.size() > 1)
                        {
                            String activityName = context.getClass().getSimpleName();
        /*                            if(activityName.contentEquals("BarcodeCaptureActivity"))
                                {
                                    ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                                }
                                else
                                {
                                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                                }*/
                            lot_courant = null;
                            code = "";
                        }
                    }
                }

                //on vérifie que le produit fait bien partie de la préparation
                boolean produit_present = false;
                if(produit != null)
                {
                    for(PH_Preparation_Ligne courant : liste_ph_preparation_ligne)
                    {
                        if(courant.getProduitID() == produit.getID_produit())
                        {
                            produit_present = true;
                            preparation_ligne_id = courant.get_UID();
                            peparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);
                            if(emplacement_courant == null)
                            {
                                PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, peparationLigne.getPreparationID());
                                Depot depotorigine = DepotOpenHelper.getDepotParID(db, preparation.getDepotOrigineID());
                                Depot_Zone zonepreparationligne = ZoneOpenHelper.getZoneByDepotEtNom(db, depotorigine, peparationLigne.getZoneDepot());
                                emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zonepreparationligne, peparationLigne.getEmplacementParDefaut());
                            }

                            for(PH_Preparation_Ligne_Preparation_Adapte adapte_courant : phPreparationLignePreparationAdapte_List)
                            {
                                if(adapte_courant.getPh_preparationLigneID() == peparationLigne.get_UID())
                                {
                                    phPreparationLignePreparationAdapte = adapte_courant;
                                    liste_preparation_liste_adapte = new ArrayList<>();
                                    liste_preparation_liste_adapte.addAll(phPreparationLignePreparationAdapte.getLotAdaptes());
                                    break;
                                }
                            }
                        }
                    }
                }

                if(!produit_present)
                {
                    ((ScannerPreparationActivity)context).afficherSnackBar("Produit non présent dans la liste");
                    lot_courant = null;
                }
                else if(peparationLigne.getQte_APreparer() == 0)
                {
                    ((ScannerPreparationActivity)context).afficherSnackBar("Produit préparé en intégralité");
                    lot_courant = null;
                    preparation_ligne_id = 0;
                }
                else if(serie != null && !serie.contentEquals("") && stringList.indexOf(chaine) == -1)
                {
                    if(produit != null)
                    {
                        if(codeInconnu)
                        {
                            gtin_courant = chaine;
                        }
                        PH_Serialisation serialisation_courante = null;

                        if(!codeInconnu)
                        {
                            /***Début de la sérialisation***/
                            if (!serie.contentEquals("") && produit.isSerialiser_Reception_Delivrance() && produit.isSuivi_Serialisation() && listeSerie.indexOf(serie) == -1) {
                                listeSerie.add(serie);
                                String resultat = "";
                                boolean differe = false;
                                if (!OutilsGestionConnexionReseau.isServerAccessible(context))
                                    differe = true;

                                if (conditionnementString.contentEquals("")) {
                                    produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtin_courant);
                                    if (produit == null) {
                                        produit = ProduitOpenHelper.getUnProduitParGTIN(db, "01" + gtin_courant);
                                    }
                                }

                                long ph_serialisation_uid = 0;
                                String peremptionSerialisation = date_peremption_courant;
                                peremptionSerialisation = peremptionSerialisation.substring(2);
                                peremptionSerialisation = peremptionSerialisation.replace("-", "");
                                ph_serialisation_uid = serialisationService.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(0), "", "");


                                serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) ph_serialisation_uid);
                                if (serialisation_courante == null) {
                                    serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int) ph_serialisation_uid);
                                }

                                resultat = serialisation_courante.getResultat();
                                if (resultat.contentEquals("INACTIVE") || resultat.contentEquals("UNKNOWN")) {
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

                                    int produit_id = serialisation_courante.getProduitUID();
                                    int serialisationID = serialisation_courante.get_UID();
                                    String motif = GestionCodeErreurNMVO.getMessage(code);
                                    String actionAMener = "";
                                    String statut = "NON LU";
                                    String traitePar = utilisateurConnecte.getIdentifiant();
                                    String traiteDate = surveillanceDate;
                                    String traiteHeure = surveillanceHeure;
                                    String produitLot = serialisation_courante.getNumeroLot();
                                    String produitDatePéremption = serialisation_courante.getDatePeremptionAAMMJJ();
                                    String produitNumeroSerie = serialisation_courante.getNumeroSerie();

                                    SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                                    //long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);
                                    long rowUID_surveillance = -1;

                                    if (rowUID_surveillance != -1) {
                                        //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                        try {
                                            EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                            //class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateurConnecte.getMail(), db);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    String messageTexteFranceMVO = "";
                                }
                            }

                            /***Fin de la sérialisation***/
                        }
                        codeInconnu = false;
                        PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);

                        for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                        {

                            PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = liste_preparation_liste_adapte.get(i);
                            if(courant.getNumLot().contentEquals(lot))
                            {
                                int qte_restante = ph_preparation_ligne.getQte_Demander()- ph_preparation_ligne.getQte_preparer();
                                lot_courant = courant;
                                break;
                            }
                            else
                            {
                                lot_courant = null;
                            }
                        }

                        if(lot_courant == null)
                        {
                            PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne.getPreparationID());
                            int qte_restante = ph_preparation_ligne.getQte_Demander()- ph_preparation_ligne.getQte_preparer();

                            if(emplacement_courant == null)
                            {
                                Depot depotorigine = DepotOpenHelper.getDepotParID(db, preparation.getDepotOrigineID());
                                Depot_Zone zonepreparationligne = ZoneOpenHelper.getZoneByDepotEtNom(db, depotorigine, ph_preparation_ligne.getZoneDepot());
                                emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zonepreparationligne, ph_preparation_ligne.getEmplacementParDefaut());
                            }

                            Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                            Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(qte_restante, lot, date_peremption_courant, emplacement_courant.getAdressage(), preparation.getDepotOrigineReference(), zone_courante.getZoneName(), produit.getID_produit(), 0, serie);
                            Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                            lot_courant = phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement);
                            liste_preparation_liste_adapte.add(lot_courant);
                        }

                        if(liste_lot.indexOf(lot_courant.getNumLot()) != -1)
                        {
        /*                    ph_preparation_ligne.setQte_preparer(ph_preparation_ligne.getQte_preparer()-lot_courant.getQteSaisie());
                            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                            lot_courant.setQteSaisie(0);*/
                        }
                        else
                        {
                            liste_lot.add(lot_courant.getNumLot());
                        }
                    }
                    else
                    {
                        String activityName = context.getClass().getSimpleName();
                        lot_courant = null;
                        code = "";
                    }
                }
                else
                {
                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);

                    for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                    {

                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = liste_preparation_liste_adapte.get(i);
                        if(courant.getNumLot().contentEquals(lot))
                        {
                            int qte_restante = ph_preparation_ligne.getQte_Demander()- ph_preparation_ligne.getQte_preparer();
                            lot_courant = courant;
                            break;
                        }
                        else
                        {
                            lot_courant = null;
                        }
                    }

                    if(lot_courant == null)
                    {
                        PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne.getPreparationID());
                        int qte_restante = ph_preparation_ligne.getQte_Demander()- ph_preparation_ligne.getQte_preparer();

                        if(emplacement_courant == null)
                        {
                            Depot depotorigine = DepotOpenHelper.getDepotParID(db, preparation.getDepotOrigineID());
                            Depot_Zone zonepreparationligne = ZoneOpenHelper.getZoneByDepotEtNom(db, depotorigine, ph_preparation_ligne.getZoneDepot());
                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zonepreparationligne, ph_preparation_ligne.getEmplacementParDefaut());
                        }

                        Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                        Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(qte_restante, lot, date_peremption_courant, emplacement_courant.getAdressage(), preparation.getDepotOrigineReference(), zone_courante.getZoneName(), produit.getID_produit(), 0, serie);
                        Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                        lot_courant = phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement);
                        liste_preparation_liste_adapte.add(lot_courant);
                    }

                    if(liste_lot.indexOf(lot_courant.getNumLot()) != -1)
                    {
        /*                ph_preparation_ligne.setQte_preparer(ph_preparation_ligne.getQte_preparer()-lot_courant.getQteSaisie());
                        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                        lot_courant.setQteSaisie(0);*/
                    }
                    else
                    {
                        liste_lot.add(lot_courant.getNumLot());
                    }
                }
            }
        }
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_SCANNER:
                    phPreparationLignePreparationAdapte_List = new ArrayList<>();
                    phPreparationLignePreparationAdapte_List = (List<PH_Preparation_Ligne_Preparation_Adapte>) data.getExtras().getSerializable("lotAdapteList");
                    liste_lot = data.getStringArrayListExtra("liste_lot");
                    break;
            }
        }
    }

    public boolean onTap(String s){
        boolean confirmation = true;

        if (s.toString().endsWith("\n"))
        {
            s = s.toString().substring(0, s.length() - 1);
        }

        if(s.startsWith("PHITAGPLACE+"))
        {
            String[] tab_emplacement = s.split(":");
            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);
        }
        else if(emplacement_courant == null && utilisateurConnecte.getEtablissement().contentEquals("ADH"))
        {
            ((BarcodePreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
        }
        else
        {
            if (!validation) {
                //afficherErreur("Validation");
                validation = false;
                return false;
            }
            else
            {
                validation = false;
            }

            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s);
            String lot = "";
            String serie = "";
            String gtin_courant ="";
            String conditionnementString ="";
            String date_peremption_courant="";
            if (gs1Decoupe.size() != 1)
            {

                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if(produits != null)
                {
                    if (produits.size() == 1) {
                        produit = produits.get(0);
                    }
                    else if(produits.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
    /*                          if(activityName.contentEquals("BarcodeCaptureActivity"))
                            {
                                ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                            }
                            else
                            {
                                ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                            }*/

                        validation = true;
                        code = "";
                        return false;
                    }
                }
                lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
            }
            else
            {
                List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.toString().substring(0, s.length()-1));
                if(produits != null)
                {
                    if (produits.size() == 1) {
                        produit = produits.get(0);
                        codeInconnu = true;
                    }
                    else if(produits.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
    /*                            if(activityName.contentEquals("BarcodeCaptureActivity"))
                            {
                                ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                            }
                            else
                            {
                                ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                            }*/
                        validation = false;
                        code = "";
                        return false;
                    }
                }
            }

            //on vérifie que le produit fait bien partie de la préparation
            boolean produit_present = false;
            if(produit != null)
            {
                for(PH_Preparation_Ligne courant : liste_ph_preparation_ligne)
                {
                    if(courant.getProduitID() == produit.getID_produit())
                    {
                        produit_present = true;
                        preparation_ligne_id = courant.get_UID();
                        peparationLigne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);
                        for(PH_Preparation_Ligne_Preparation_Adapte adapte_courant : phPreparationLignePreparationAdapte_List)
                        {
                            if(adapte_courant.getPh_preparationLigneID() == peparationLigne.get_UID())
                            {
                                phPreparationLignePreparationAdapte = adapte_courant;
                                liste_preparation_liste_adapte = new ArrayList<>();
                                liste_preparation_liste_adapte.addAll(phPreparationLignePreparationAdapte.getLotAdaptes());
                                break;
                            }
                        }
                    }
                }
            }

            if(!produit_present)
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Produit non présent dans la liste");
                lot_courant = null;
                validation = true;
            }
            else if(peparationLigne.getQte_APreparer() == 0)
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Produit déjà préparé en intégralité");
                lot_courant = null;
                validation = true;
            }
            else if(serie != null && !serie.contentEquals("") && stringList.indexOf(s.toString().substring(0, s.length()-1)) == -1)
            {
                if(produit != null)
                {
                    if(codeInconnu)
                    {
                        gtin_courant = s.toString().substring(0, s.length() - 1);
                    }
                    PH_Serialisation serialisation_courante = null;

                    if(!codeInconnu)
                    {
                        /***Début de la sérialisation***/
                        if (!serie.contentEquals("") && produit.isSerialiser_Reception_Delivrance() && produit.isSuivi_Serialisation() && listeSerie.indexOf(serie) == -1) {
                            listeSerie.add(serie);
                            String resultat = "";
                            boolean differe = false;
                            if (!OutilsGestionConnexionReseau.isServerAccessible(context))
                                differe = true;

                            if (conditionnementString.contentEquals("")) {
                                produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtin_courant);
                                if (produit == null) {
                                    produit = ProduitOpenHelper.getUnProduitParGTIN(db, "01" + gtin_courant);
                                }
                            }

                            long ph_serialisation_uid = 0;
                            String peremptionSerialisation = date_peremption_courant;
                            peremptionSerialisation = peremptionSerialisation.substring(2);
                            peremptionSerialisation = peremptionSerialisation.replace("-", "");
                            ph_serialisation_uid = serialisationService.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(0), "", "");


                            serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) ph_serialisation_uid);
                            if (serialisation_courante == null) {
                                serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int) ph_serialisation_uid);
                            }

                            resultat = serialisation_courante.getResultat();
                            if (resultat.contentEquals("INACTIVE") || resultat.contentEquals("UNKNOWN")) {
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

                                int produit_id = serialisation_courante.getProduitUID();
                                int serialisationID = serialisation_courante.get_UID();
                                String motif = GestionCodeErreurNMVO.getMessage(code);
                                String actionAMener = "";
                                String statut = "NON LU";
                                String traitePar = utilisateurConnecte.getIdentifiant();
                                String traiteDate = surveillanceDate;
                                String traiteHeure = surveillanceHeure;
                                String produitLot = serialisation_courante.getNumeroLot();
                                String produitDatePéremption = serialisation_courante.getDatePeremptionAAMMJJ();
                                String produitNumeroSerie = serialisation_courante.getNumeroSerie();

                                SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, traiteDate, traiteHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                                //long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);
                                long rowUID_surveillance = -1;

                                if (rowUID_surveillance != -1) {
                                    //ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                    try {
                                        EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                        //class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateurConnecte.getMail(), db);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                //((BarcodeCaptureActivity) context).afficherAlerteFranceMVO(produit.getDesignation_interne(), resultat, serie, motif);

                            } else {
                                String messageTexteFranceMVO = "";
                            }
                        }

                        /***Fin de la sérialisation***/
                    }
                    codeInconnu = false;
                    PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);

                    for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                    {

                        PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = liste_preparation_liste_adapte.get(i);
                        if(courant.getNumLot().contentEquals(lot))
                        {
                            int qte_restante = ph_preparation_ligne.getQte_Demander()- ph_preparation_ligne.getQte_preparer();
                            lot_courant = courant;
                            break;
                        }
                        else
                        {
                            lot_courant = null;
                        }
                    }

                    if(lot_courant == null)
                    {
                        PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne.getPreparationID());
                        int qte_restante = ph_preparation_ligne.getQte_APreparer();
                        Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                        Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(qte_restante, lot, date_peremption_courant, emplacement_courant.getAdressage(), preparation.getDepotOrigineReference(), zone_courante.getZoneName(), produit.getID_produit(), 0, serie);
                        Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                        lot_courant = phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement);
                        liste_preparation_liste_adapte.add(lot_courant);
                    }

                    if(liste_lot.indexOf(lot_courant.getNumLot()) != -1)
                    {
    /*                    ph_preparation_ligne.setQte_preparer(ph_preparation_ligne.getQte_preparer()-lot_courant.getQteSaisie());
                        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                        lot_courant.setQteSaisie(0);*/
                    }
                    else
                    {
                        liste_lot.add(lot_courant.getNumLot());
                    }
                }
                else
                {
                    String activityName = context.getClass().getSimpleName();
    /*                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit inconnu");
                        }*/
                    validation = true;
                    code = "";
                }
            }
            else
            {
                PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);

                for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                {

                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = liste_preparation_liste_adapte.get(i);
                    if(courant.getNumLot().contentEquals(lot))
                    {
                        lot_courant = courant;
                        break;
                    }
                    else
                    {
                        lot_courant = null;
                    }
                }

                if(lot_courant == null)
                {
                    PH_Preparation preparation = PH_PreparationOpenHelper.getPH_PreparationByID(db, ph_preparation_ligne.getPreparationID());
                    int qte_restante = ph_preparation_ligne.getQte_APreparer();
                    Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
                    Stock_Lot_Emplacement_Light newStockLotEmplacement = new Stock_Lot_Emplacement_Light(qte_restante, lot, date_peremption_courant, emplacement_courant.getAdressage(), preparation.getDepotOrigineReference(), zone_courante.getZoneName(), produit.getID_produit(), 0, serie);
                    Stock_Lot_EmplacementLightOpenHelper.insererUnStock_Lot_EmplacementEnBDD(db, newStockLotEmplacement);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Stock_Lot_EmplacementLightOpenHelper.Constantes.TABLE_STOCK_LOT_EMPLACEMENT, newStockLotEmplacement.getPhiMR4UUID(), newStockLotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                    lot_courant = phPreparationLignePreparationAdapte.new LotAdapte(newStockLotEmplacement);
                    liste_preparation_liste_adapte.add(lot_courant);
                }

                if(liste_lot.indexOf(lot_courant.getNumLot()) != -1)
                {
    /*                ph_preparation_ligne.setQte_preparer(ph_preparation_ligne.getQte_preparer()-lot_courant.getQteSaisie());
                    PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne);
                    lot_courant.setQteSaisie(0);*/
                }
                else
                {
                    liste_lot.add(lot_courant.getNumLot());
                }
            }
        }

        return confirmation;
    }

    public void ValiderScan(int quantite)
    {
        /**
         * TODO : vérifier l'emplacement du lot scanné avec l'emplacement scanné avec l'appareil
         */
        if(lot_courant != null)
        {
            PH_Preparation_Ligne ph_preparation_ligne = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparation_ligne_id);
            if(ph_preparation_ligne != null)
            {
                peparationLigne.setQte_preparer(peparationLigne.getQte_preparer()+quantite);
                peparationLigne.setQte_APreparer(peparationLigne.getQte_APreparer()-quantite);
                PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, peparationLigne);
                lot_courant.setQteSaisie(lot_courant.getQteSaisie()+quantite);
                if(emplacement_courant != null)
                {
                    lot_courant.setEmplacement(emplacement_courant.getAdressage());
                }
                int index_suppression = -1;
                boolean lot_trouver = false;
                for(int i = 0; i < liste_preparation_liste_adapte.size(); i++)
                {
                    PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant = liste_preparation_liste_adapte.get(i);
                    if(courant.getNumLot().contentEquals(lot_courant.getNumLot()))
                    {
                        index_suppression = i;
                        lot_trouver = true;
                        break;
                    }
                }

                if(lot_trouver)
                {
                    liste_preparation_liste_adapte.remove(index_suppression);
                    liste_preparation_liste_adapte.add(lot_courant);
                    phPreparationLignePreparationAdapte.setLotAdaptes(new ArrayList<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>());
                    phPreparationLignePreparationAdapte.getLotAdaptes().addAll(liste_preparation_liste_adapte);
                    int index_supr = -1;
                    boolean adapte_trouver = false;
                    for(int i = 0; i < phPreparationLignePreparationAdapte_List.size(); i++)
                    {
                        if(phPreparationLignePreparationAdapte_List.get(i).getPh_preparationLigneID() == phPreparationLignePreparationAdapte.getPh_preparationLigneID())
                        {
                            adapte_trouver = true;
                            index_supr = i;
                            break;
                        }
                    }
                    if(adapte_trouver)
                    {
                        phPreparationLignePreparationAdapte_List.remove(index_supr);
                        phPreparationLignePreparationAdapte_List.add(phPreparationLignePreparationAdapte);
                        lot_courant = null;
                        preparation_ligne_id = -1;
                        validation = true;
                    }
                }
            }
        }
    }

    public boolean emplacementLotVerifier(String emplacement, String lot)
    {
        if(emplacement == null)
        {
            return false;
        }
        else
        {
            if(lot.indexOf("\n") !=-1)
            {
                lot = lot.substring(0, lot.length()-1);
            }

            boolean emplacementok = false;
            emplacementDisponible = "";
            int compteur = 0;
            liste_emplacement_disponible = new ArrayList<>();

            for(PH_Preparation_Ligne_Preparation_Adapte.LotAdapte courant : liste_preparation_liste_adapte)
            {
                String lot_courant = courant.getNumLot();
                String emplacement_courant = courant.getEmplacement();
                Stock_Lot_Emplacement_Light stock_courant = Stock_Lot_EmplacementLightOpenHelper.getStock_Lot_EmplacementByID(db, courant.getStockLotEmplacementID());
                if(stock_courant != null)
                {
                    Depot depot_courant = DepotOpenHelper.getDepotParReference(db, stock_courant.getDepot_Reference());
                    if(depot_courant != null)
                    {
                        Depot_Zone depot_zone = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_courant, stock_courant.getZone());
                        if(depot_zone != null)
                        {
                            Depot_Emplacement depot_emplacement = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, depot_zone, stock_courant.getEmplacement());
                            if(depot_emplacement != null)
                                liste_emplacement_disponible.add(depot_emplacement.get_UID());
                        }
                    }
                }
                emplacementDisponible+=emplacement_courant;

                if(compteur != liste_preparation_liste_adapte.size()-1)
                    emplacementDisponible+=",";

                if(lot.contentEquals(lot_courant) && emplacement.contentEquals(emplacement_courant))
                {
                    emplacementok = true;
                    break;
                }

                compteur++;
            }

            return emplacementok;
        }
    }
}
