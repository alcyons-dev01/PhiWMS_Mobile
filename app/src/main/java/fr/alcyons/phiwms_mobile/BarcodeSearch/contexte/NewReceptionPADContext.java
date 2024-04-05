package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;

public class NewReceptionPADContext {
    private Context context;
    private SQLiteDatabase db;

    private boolean modeRafale = true;
    private boolean modePhoto = false;
    private boolean modeCumule = true;
    private boolean doitEtreIdentique = false;
    private boolean serialisation = false;
    private String designation = "";

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    public int conditionnement;
    public List<String> GtinProduitScan;

    public List<String> stringList;
    public List<ObjetReceptionScannee> list_result;
    public ObjetReceptionScannee objetReceptionScanneeCourant;

    public String designationProduitCourant;
    public String referenceProduitCourant;
    public String numeroLotProduitCourant;
    public String numeroSerieProduitCourant;
    public String peremptionProduitCourant;
    public String quantiteProduitCourant;
    private int index_objet_a_supprimer;
    private boolean doitEtreSupprimer;
    public int quantite_a_afficher;

    private Serialisation serialisationService;
    private Utilisateur utilisateurConnecte;
    private List<String> serieListe;
    private int actionId;
    private boolean codeInconnu;
    public int uidEmplacementCourant;
    public List<Integer> liste_id_produit;
    public List<Integer> liste_id_reliquat;
    public int quantiteMaxNumberPicker;
    public PH_Reliquat ph_reliquat_courant;
    public boolean validation;
    boolean modeTrace;
    public int conditionnementAchat;

    public NewReceptionPADContext(final Context context, final SQLiteDatabase db, List<String> listGtinScannee, Utilisateur utilisateurConnecte, List<ObjetReceptionScannee> listObjet_scanne, List<Integer> liste_id_reliquat)
    {
        this.context = context;
        this.db = db;
        this.utilisateurConnecte = utilisateurConnecte;
        this.liste_id_reliquat = liste_id_reliquat;
        this.list_result = listObjet_scanne;
        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
        this.modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
        code = "";
        bannerTexte = "Scanner une référence";
        scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);
        stringList = listGtinScannee;
        if(stringList == null)
        {
            stringList = new ArrayList<>();
        }
        GtinProduitScan = listGtinScannee;
        if(GtinProduitScan == null)
        {
            GtinProduitScan = new ArrayList<>();
        }
        serialisationService = new Serialisation(context, db, utilisateurConnecte);
        serieListe = new ArrayList<>();
        codeInconnu = false;
        uidEmplacementCourant = 0;
        liste_id_produit = new ArrayList<>();
        if(this.liste_id_reliquat != null)
        {
            for(Integer id_reliquat_courant : this.liste_id_reliquat)
            {
                PH_Reliquat reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, id_reliquat_courant);
                liste_id_produit.add(reliquat_courant.getProduitID());
            }
        }

        validation = true;
    }

    public void onTextWatcher(final Editable chaine){
        String s = String.valueOf(chaine);
        if (s.endsWith("\n"))
        {
            s = s.substring(0, s.length() - 1);
        }

        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
        {
            if (!validation) {
                validation = false;
            }
            else
            {
                validation = false;
            }

            Produit produit = null;
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s);
            if (gs1Decoupe.size() != 1)
            {
                if(modeTrace)
                {
                    //gestion des traces
                    Random random = new Random();
                    String stringint = String.valueOf(random.nextInt());
                    int id = Integer.parseInt(stringint.substring(0, 5))*-1;
                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    TableTrace tableTrace = null;
                    long rowId = 0;
                    tableTrace = new TableTrace(id, date, "Context_Emplacement", "Récupération ASCII", gs1Decoupe.get(OutilsDecodage.gtin_code_ascii), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                    rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                    if(rowId != -1)
                    {
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getphiwms_mobileUUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                    }
                }

                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if(produits != null)
                {
                    if (produits.size() == 1) {
                        produit = produits.get(0);
                    }
                    else if(produits.size() > 1)
                    {
                        afficherErreur("inconnu");
                        code = "";
                        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
                    }
                }
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
                        afficherErreur("");
                        code = "";
                        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
                    }
                }
            }

            if (modeRafale) {
                if (modeCumule) {
                    /*if(!stringList.contains(s))
                    {*/
                        if(produit != null && liste_id_produit.contains(produit.getID_produit()))
                        {
                            for(Integer id_reliquat_courant : liste_id_reliquat)
                            {
                                PH_Reliquat ph_reliquat_temp = PH_ReliquatOpenHelper.getPH_ReliquatById(db, id_reliquat_courant);
                                if(ph_reliquat_temp.getProduitID() == produit.getID_produit())
                                {
                                    ph_reliquat_courant = ph_reliquat_temp;
                                }
                            }

                            if(ph_reliquat_courant.getQteReliquat_X()==0)
                            {
                                afficherErreur("Produit réceptionné");
                                objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
                                ph_reliquat_courant = null;
                            }
                            else
                            {
                                String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                                String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                                String gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                                String conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                                String date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

                                if(codeInconnu)
                                {
                                    gtin_courant = s;
                                }
                                PH_Serialisation serialisation_courante = null;

                                if(!codeInconnu)
                                {
                                    /***Début de la sérialisation***/
                                    if (!serie.contentEquals("") && produit.isSuivi_Serialisation() && !serieListe.contains(serie)) {
                                        numeroSerieProduitCourant = serie;
                                        serieListe.add(serie);
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
                                        ph_serialisation_uid = serialisationService.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(actionId), "", "");


                                        serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) ph_serialisation_uid);
                                        if (serialisation_courante == null) {
                                            serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByphiwms_mobileUUID(db, (int) ph_serialisation_uid);
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

                                            long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);

                                            if (rowUID_surveillance != -1) {
                                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                                try {
                                                    EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                                    class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateurConnecte.getMail(), db);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            ((BarcodeCaptureActivity) context).afficherAlerteFranceMVO(produit.getDesignation_interne(), resultat, serie, motif);

                                        } else {
                                            String messageTexteFranceMVO = "";
                                        }
                                    }
                                    else
                                    {
                                        numeroSerieProduitCourant = "";
                                    }

                                    /***Fin de la sérialisation***/
                                }

                                if(!GtinProduitScan.contains(gtin_courant))
                                {
                                    GtinProduitScan.add(gtin_courant);
                                }
                                int dose = 0;
                                String date_string = "";
                                if(!codeInconnu)
                                {
                                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                    Date date = new Date();

                                    try {
                                        date = dateFormat1.parse(date_peremption_courant);
                                        date_string =  dateFormat2.format(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if(!gs1Decoupe.get(OutilsDecodage.conditionnementProduit).contentEquals(""))
                                        dose = Integer.parseInt(gs1Decoupe.get(OutilsDecodage.conditionnementProduit));
                                }

                                if(dose == 0)
                                    dose = produit.getCond_achat();

                                conditionnementAchat = produit.getCond_achat();
                                doitEtreSupprimer = false;
                                index_objet_a_supprimer= -1;
                                for(ObjetReceptionScannee objet_courant : list_result)
                                {
                                    index_objet_a_supprimer ++;

                                    Map<String, String> gs1Decoupe_courant = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());
                                    String lot_courant = gs1Decoupe_courant.get(OutilsDecodage.numeroLot);

                                    if(!gs1Decoupe_courant.get(OutilsDecodage.numeroSerie).contentEquals(serie) && gs1Decoupe_courant.get(OutilsDecodage.codeGtin).contentEquals(gtin_courant) && lot_courant.contentEquals(lot))
                                    {
                                        objetReceptionScanneeCourant = objet_courant;
                                        supprimerObjetList();
                                        break;
                                    }
                                    else if(serie.contentEquals("") && gs1Decoupe_courant.get(OutilsDecodage.codeGtin).contentEquals(gtin_courant) && lot_courant.contentEquals(lot))
                                    {
                                        objetReceptionScanneeCourant = objet_courant;
                                        //supprimerObjetList();
                                        break;
                                    }
                                }


                                quantite_a_afficher = dose;
                                //quantite_a_afficher = (int)ph_reliquat_courant.getQteCommande()-(int)ph_reliquat_courant.getQteReliquat_X();

                                peremptionProduitCourant = date_string;
                                numeroLotProduitCourant = lot;
                                designationProduitCourant = produit.getDesignation_interne();
                                referenceProduitCourant = produit.getRef_fourni();
                                quantiteProduitCourant = String.valueOf(dose);

                                if(serialisation_courante != null)
                                {
                                    objetReceptionScanneeCourant.setResultat_france_mvo(serialisation_courante.getResultat());
                                }
                                else
                                {
                                    objetReceptionScanneeCourant.setResultat_france_mvo("");
                                }

                                objetReceptionScanneeCourant.setGs1_scannee(s);
                                codeInconnu = false;
                            }
                        }
                        else
                        {
                            afficherErreur("inconnu");
                            code = "";
                            objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
                            ph_reliquat_courant = null;
                        }
/*                    }
                    else
                    {
                        afficherErreur("dejaScanne");
                        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
                        ph_reliquat_courant = null;
                    }*/
                }
                code = "";
            } else {
                code = s;
            }
        }
    }

    public boolean AjoutDuProduit()
    {
        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
            return false;
        else
        {
            ph_reliquat_courant.setQteReliquat_X(ph_reliquat_courant.getQteReliquat_X()-quantite_a_afficher);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, ph_reliquat_courant);
            objetReceptionScanneeCourant.setQuantiteScannee(quantite_a_afficher);
            if(!objetReceptionScanneeCourant.getGs1_scannee().startsWith("Code_Trac"))
            {
                Map<String, String> gs1DecoupeAjout = OutilsDecodage.decouperGTIN(objetReceptionScanneeCourant.getGs1_scannee());
                if(!gs1DecoupeAjout.get(OutilsDecodage.numeroSerie).contentEquals(""))
                {
                    stringList.add(objetReceptionScanneeCourant.getGs1_scannee());
                }
            }
            else
            {
                stringList.add(objetReceptionScanneeCourant.getGs1_scannee());
            }

            list_result.add(objetReceptionScanneeCourant);
            validation = true;
            objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
        }

        return true;
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
                    List<ObjetReceptionScannee> stringRetourList = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                    list_result = new ArrayList<>();
                    if(stringRetourList != null)
                    {
                        list_result.addAll(stringRetourList);
                    }
                    break;
            }
        }
    }

    public boolean onTap(String s){

        if (s.endsWith("\n"))
        {
            s = s.substring(0, s.length() - 1);
        }


        if (!validation) {
            //afficherErreur("Validation");
            validation = false;
            return false;
        }
        else
        {
            validation = false;
        }

        Produit produit = null;
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s);
        if (gs1Decoupe.size() != 1)
        {
            if(modeTrace)
            {
                //gestion des traces
                Random random = new Random();
                String stringint = String.valueOf(random.nextInt());
                int id = Integer.parseInt(stringint.substring(0, 5))*-1;
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                TableTrace tableTrace = null;
                long rowId = 0;
                tableTrace = new TableTrace(id, date, "Context_Emplacement", "Récupération ASCII", gs1Decoupe.get(OutilsDecodage.gtin_code_ascii), utilisateurConnecte.getIdentifiant(), utilisateurConnecte.getId());
                rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                if(rowId != -1)
                {
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getphiwms_mobileUUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            }

            List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            if(produits != null)
            {
                if (produits.size() == 1) {
                    produit = produits.get(0);
                }
                else if(produits.size() > 1)
                {
                    afficherErreur("inconnu");
                    code = "";
                    return false;
                }
            }
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
                    afficherErreur("");
                    code = "";
                    return false;
                }
            }
        }

        if (modeRafale) {
            if (modeCumule) {
                if(!stringList.contains(s))
                {
                    if(produit != null && liste_id_produit.contains(produit.getID_produit()))
                    {
                        for(Integer id_reliquat_courant : liste_id_reliquat)
                        {
                            PH_Reliquat ph_reliquat_temp = PH_ReliquatOpenHelper.getPH_ReliquatById(db, id_reliquat_courant);
                            if(ph_reliquat_temp.getProduitID() == produit.getID_produit())
                            {
                                ph_reliquat_courant = ph_reliquat_temp;
                            }
                        }

                        if(ph_reliquat_courant.getQteReliquat_X()==0)
                        {
                            afficherErreur("Produit réceptionné");
                            return false;
                        }

                        String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                        String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                        String gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                        String conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                        String date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

                        if(codeInconnu)
                        {
                            gtin_courant = s;
                        }
                        PH_Serialisation serialisation_courante = null;

                        if(!codeInconnu)
                        {
                            /***Début de la sérialisation***/
                            if (!serie.contentEquals("") && produit.isSuivi_Serialisation() && !serieListe.contains(serie)) {
                                numeroSerieProduitCourant = serie;
                                serieListe.add(serie);
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
                                ph_serialisation_uid = serialisationService.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(actionId), "", "");


                                serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) ph_serialisation_uid);
                                if (serialisation_courante == null) {
                                    serialisation_courante = PH_SerialisationOpenHelper.getPH_SerialisationByphiwms_mobileUUID(db, (int) ph_serialisation_uid);
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

                                    long rowUID_surveillance = SurveillanceReferenceOpenHelper.insererSurveillanceReferenceEnBDD(db, new_surveillance_reference);

                                    if (rowUID_surveillance != -1) {
                                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, SurveillanceReferenceOpenHelper.Constantes.TABLE_SURVEILLANCEREFERENCE, new_surveillance_reference.getSerialexpressUUID(), new_surveillance_reference.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                                        try {
                                            EnvoyerMailSurveillance class_mail = new EnvoyerMailSurveillance();
                                            class_mail.EnvoyerMailSerialisation(new_surveillance_reference.get_UID(), utilisateurConnecte.getMail(), db);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    ((BarcodeCaptureActivity) context).afficherAlerteFranceMVO(produit.getDesignation_interne(), resultat, serie, motif);

                                } else {
                                    String messageTexteFranceMVO = "";
                                }
                            }
                            else
                            {
                                numeroSerieProduitCourant ="";
                            }

                            /***Fin de la sérialisation***/
                        }

                        if(!GtinProduitScan.contains(gtin_courant))
                        {
                            GtinProduitScan.add(gtin_courant);
                        }
                        int dose = 0;
                        String date_string = "";
                        if(!codeInconnu)
                        {
                            DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                            DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                            Date date = new Date();

                            try {
                                date = dateFormat1.parse(date_peremption_courant);
                                date_string =  dateFormat2.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(!gs1Decoupe.get(OutilsDecodage.conditionnementProduit).contentEquals(""))
                                dose = Integer.parseInt(gs1Decoupe.get(OutilsDecodage.conditionnementProduit));
                        }

                        if(dose == 0)
                            dose = produit.getCond_achat();

                        conditionnementAchat = produit.getCond_achat();

                        doitEtreSupprimer = false;
                        index_objet_a_supprimer= -1;
                        for(ObjetReceptionScannee objet_courant : list_result)
                        {
                            index_objet_a_supprimer ++;

                            Map<String, String> gs1Decoupe_courant = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());
                            String lot_courant = gs1Decoupe_courant.get(OutilsDecodage.numeroLot);

                            if(!gs1Decoupe_courant.get(OutilsDecodage.numeroSerie).contentEquals(serie) && gs1Decoupe_courant.get(OutilsDecodage.codeGtin).contentEquals(gtin_courant) && lot_courant.contentEquals(lot))
                            {
                                objetReceptionScanneeCourant = objet_courant;
                                supprimerObjetList();
                                break;
                            }
                            else if(serie.contentEquals("") && gs1Decoupe_courant.get(OutilsDecodage.codeGtin).contentEquals(gtin_courant) && lot_courant.contentEquals(lot))
                            {
                                objetReceptionScanneeCourant = objet_courant;
                                supprimerObjetList();
                                break;
                            }
                        }


                        quantite_a_afficher = dose;
                        //quantite_a_afficher = (int)ph_reliquat_courant.getQteCommande()-(int)ph_reliquat_courant.getQteReliquat_X();

                        peremptionProduitCourant = date_string;
                        numeroLotProduitCourant = lot;
                        designationProduitCourant = produit.getDesignation_interne();
                        referenceProduitCourant = produit.getRef_fourni();
                        quantiteProduitCourant = String.valueOf(dose);

                        if(serialisation_courante != null)
                        {
                            objetReceptionScanneeCourant.setResultat_france_mvo(serialisation_courante.getResultat());
                        }
                        else
                        {
                            objetReceptionScanneeCourant.setResultat_france_mvo("");
                        }

                        objetReceptionScanneeCourant.setGs1_scannee(s);
                        codeInconnu = false;
                    }
                    else
                    {
                        afficherErreur("inconnu");
                        code = "";
                        return false;
                    }
                }
                else
                {
                    afficherErreur("dejaScanne");
                    return false;
                }
            }
            code = "";
        } else {
            code = s;
        }

        return true;
    }

    private void afficherErreur(String contextErreur)
    {
        String activityName = context.getClass().getSimpleName();

        switch (contextErreur)
        {
            case "Produit réceptionné":
                if(activityName.contentEquals("BarcodeCaptureActivity"))
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà réceptionné");
                }
                else if(activityName.contentEquals("BarcodePreparationActivity"))
                {
                    ((BarcodePreparationActivity) context).afficherSnackBar("Produit déjà réceptionné");
                }
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Produit déjà réceptionné");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit déjà réceptionné");
                }
                break;
            case "dejaScanne":
                if(activityName.contentEquals("BarcodeCaptureActivity"))
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà scanné");
                }
                else if(activityName.contentEquals("BarcodePreparationActivity"))
                {
                    ((BarcodePreparationActivity) context).afficherSnackBar("Produit déjà scanné");
                }
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Produit déjà scanné");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit déjà scanné");
                }
                break;
            case "Validation":
                if(activityName.contentEquals("BarcodeCaptureActivity"))
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Valider votre scan");
                }
                else if(activityName.contentEquals("BarcodePreparationActivity"))
                {
                    ((BarcodePreparationActivity) context).afficherSnackBar("Valider votre scan");
                }
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Valider votre scan");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Valider votre scan");
                }
            default:
                if(activityName.contentEquals("BarcodeCaptureActivity"))
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                }
                else if(activityName.contentEquals("BarcodePreparationActivity"))
                {
                    ((BarcodePreparationActivity) context).afficherSnackBar("Produit inconnu");
                }
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Produit inconnu");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit inconnu");
                }
                break;
        }
        code = "";
        validation = true;
    }

    private void supprimerObjetList()
    {
        ObjetReceptionScannee objetasupprimer = list_result.get(index_objet_a_supprimer);
        ph_reliquat_courant.setQteReliquat_X(ph_reliquat_courant.getQteReliquat_X()+objetasupprimer.getQuantiteScannee());
        PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, ph_reliquat_courant);
        list_result.remove(index_objet_a_supprimer);
    }
}
