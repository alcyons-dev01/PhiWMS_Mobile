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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametreUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.TableTraceOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.TableTrace;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.MainActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;

import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 20/05/2019.
 */

public class ProduitReceptionScanneeScannerSearchContexte extends MainActivity {
    private Context context;
    private SQLiteDatabase db;

    private boolean modeRafale = false;
    private boolean modePhoto = false;
    private boolean modeCumule = true;
    private boolean doitEtreIdentique = false;
    private boolean serialisation = false;
    private String designation = "";

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    public List<String> GtinProduitScan;

    public List<String> stringList;
    public List<ObjetReceptionScannee> listSameEmplacement;
    private ObjetReceptionScannee objetReceptionScanneeCourant;

    public String designationProduitCourant;
    public String referenceProduitCourant;
    public String numeroLotProduitCourant;
    public String peremptionProduitCourant;
    public String quantiteProduitCourant;
    public String emplacementProduitCourant;
    public String zoneProduitCourant;
    public boolean scanEmplacement;
    public boolean premier;

    public List<String> ZoneEmplacement;
    public Map<String, List<ObjetReceptionScannee>> mapExpandable;
    public List<ObjetReceptionScannee> liste_resultat;
    private String ancienZoneEmplacement;
    private List<String> serieListe;
    private Serialisation serialisationService;
    private int actionId;
    private Utilisateur utilisateurConnecte;
    private boolean differe;
    public int quantite_a_afficher;
    private Depot_Zone zone_courante;
    private boolean codeInconnu;
    private boolean initialisation;
    boolean modeTrace;

    public ProduitReceptionScanneeScannerSearchContexte(final Context context, final SQLiteDatabase db, final boolean modeRafale, final boolean modePhoto, final boolean modeCumule, final boolean doitEtreIdentique, final String designation, final boolean serialisation, Utilisateur utilisateurConnecte, int actionId){
        this.context = context;
        this.db = db;
        this.modeRafale = modeRafale;
        this.modePhoto = modePhoto;
        this.modeCumule = modeCumule;
        this.doitEtreIdentique = doitEtreIdentique;
        this.designation = designation;
        this.serialisation = serialisation;
        this.actionId = actionId;
        this.utilisateurConnecte = utilisateurConnecte;
        this.modeTrace = ParametreUtilisateurOpenHelper.getModeTrace(db);
        scanEmplacement = true;
        listSameEmplacement = new ArrayList<>();
        ZoneEmplacement = new ArrayList<>();
        mapExpandable = new LinkedHashMap<>();
        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");
        premier = true;

        code = "";
        bannerTexte = "Scanner un emplacement";
        scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);
        stringList = new ArrayList<>();
        GtinProduitScan = new ArrayList<>();
        liste_resultat = new ArrayList<>();
        serieListe = new ArrayList<>();
        serialisationService = new Serialisation(context, db, utilisateurConnecte);
        differe = false;
        codeInconnu = false;
        if (!statutConnexion)
            differe = true;

        emplacementProduitCourant = "";
        zoneProduitCourant = "";
        initialisation = false;
    }

    public void onTextWatcher(final Editable s){
        if(!scanEmplacement)
        {

            if (s.toString().endsWith("\n")) {
                Produit produit = null;
                Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s.toString().substring(0, s.length() - 1));
                if (gs1Decoupe.size() != 1) {
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

                        String[] tab_ascii = gs1Decoupe.get(OutilsDecodage.gtin_code_ascii).trim().split("\\|");
                        //String chaineReel = "";
                        List<String> chaineReel = new ArrayList<>();
                        for(int i = 0; i < tab_ascii.length; i++)
                        {
                            int courant = Integer.parseInt(tab_ascii[i]);
                            String correspondance = (String) Character.toString((char)courant);
                            chaineReel.add(courant+"->"+correspondance);
                        }
                        //Alerte.afficherAlerteList(context, "Code", s.toString(), chaineReel, "alerte");

                        rowId = TableTraceOpenHelper.insererTableTraceEnBDD(db, tableTrace);
                        if(rowId != -1)
                        {
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, TableTraceOpenHelper.Constantes.TABLE_TABLE_TRACE, tableTrace.getPhiMR4UUID(), tableTrace.getId(), DBOpenHelper.ActionsEAS.AJOUT);
                        }
                    }

                    List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                    if (produits.size() == 1) {
                        produit = produits.get(0);
                    }
                    else if(produits.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }

                        code = "";
                        return;
                    }
                }
                else
                {
                    List<Produit> list = ProduitOpenHelper.getProduitsParCodeInconnue(db, s.toString().substring(0, s.length()-1));
                    if(list.size() == 1)
                    {
                        produit = list.get(0);
                        codeInconnu = true;
                    }
                    else if(list.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }

                        code = "";
                        return;
                    }
                }

                String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                String gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                String conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                String date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

                if (modeRafale) {
                    if(stringList.indexOf(s.toString().substring(0, s.length()-1)) == -1)
                    {
                        if(produit != null)
                        {
                            AjoutDuProduit();

                            stringList.add(s.toString().substring(0, s.length() - 1));

                            PH_Serialisation serialisation_courante = null;

                            if(codeInconnu)
                            {
                                gtin_courant = s.toString().substring(0, s.length()-1);
                            }

                            if(!codeInconnu)
                            {
                                /***Début de la sérialisation***/
                                if (!serie.contentEquals("") && produit.isSerialiser_Reception_Delivrance() && produit.isSuivi_Serialisation() && serieListe.indexOf(serie) == -1) {
                                    serieListe.add(serie);
                                    String resultat = "";

                                    long ph_serialisation_uid = 0;
                                    String peremptionSerialisation = date_peremption_courant;
                                    peremptionSerialisation = peremptionSerialisation.substring(2);
                                    peremptionSerialisation = peremptionSerialisation.replace("-", "");
                                    ph_serialisation_uid = Serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(actionId), "", "");


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
                                        String produitLot = serialisation_courante.getNumeroLot();
                                        String produitDatePéremption = serialisation_courante.getDatePeremptionAAMMJJ();
                                        String produitNumeroSerie = serialisation_courante.getNumeroSerie();

                                        SurveillanceReference new_surveillance_reference = new SurveillanceReference(id_surveillance, surveillanceDate, surveillanceHeure, produit_id, serialisationID, motif, actionAMener, statut, traitePar, surveillanceDate, surveillanceHeure, produitLot, produitDatePéremption, produitNumeroSerie);

                                        ((ScannerSearchOnlyActivity) context).afficherAlerteFranceMVO(produit.getDesignation_interne(), resultat, serie, motif);
                                    }
                                }

                                /***Fin de la sérialisation***/

                            }


                            if(GtinProduitScan.indexOf(gtin_courant) == -1)
                            {
                                GtinProduitScan.add(gtin_courant);
                            }
                            String date_string = "";
                            int dose = 0;

                            if(!codeInconnu)
                            {
                                DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                Date date = new Date();

                                try {
                                    date = dateFormat1.parse(date_peremption_courant);
                                    date_string = dateFormat2.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(!conditionnementString.contentEquals(""))
                                {
                                    if(conditionnementString.indexOf("@")!= -1)
                                    {
                                        String[] tab_conditionnement = conditionnementString.split("@");
                                        conditionnementString = tab_conditionnement[0];
                                    }
                                    dose = (int)Integer.parseInt(conditionnementString.trim());
                                }
                            }

                            if(dose == 0)
                                dose = (int) produit.getCond_distrib();

                            quantite_a_afficher = dose;
                            peremptionProduitCourant = date_string;
                            numeroLotProduitCourant = lot;
                            designationProduitCourant = produit.getDesignation_interne();
                            referenceProduitCourant = produit.getRef_fourni();
                            quantiteProduitCourant = String.valueOf(dose);
                            objetReceptionScanneeCourant.setGs1_scannee(s.toString().substring(0, s.length() - 1));
                            objetReceptionScanneeCourant.setQuantiteScannee(dose);
                            codeInconnu = false;
                            if(serialisation_courante != null)
                                objetReceptionScanneeCourant.setResultat_france_mvo(serialisation_courante.getResultat());

                            AjoutDuProduit();
                        }
                        else
                        {
                            ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit inconnu");
                            code = "";
                        }
                    }
                    else if(serie == null || serie.contentEquals(""))
                    {
                        if(produit != null)
                        {
                            AjoutDuProduit();

                            stringList.add(s.toString().substring(0, s.length() - 1));

                            PH_Serialisation serialisation_courante = null;

                            if(codeInconnu)
                            {
                                gtin_courant = s.toString().substring(0, s.length()-1);
                            }


                            if(GtinProduitScan.indexOf(gtin_courant) == -1)
                            {
                                GtinProduitScan.add(gtin_courant);
                            }
                            String date_string = "";
                            int dose = 0;

                            if(!codeInconnu)
                            {
                                DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                                DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                Date date = new Date();

                                try {
                                    date = dateFormat1.parse(date_peremption_courant);
                                    date_string = dateFormat2.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(!conditionnementString.contentEquals(""))
                                {
                                    if(conditionnementString.indexOf("@") != -1)
                                    {
                                        String[] tab_conditionnement = conditionnementString.split("@");
                                        conditionnementString = tab_conditionnement[0];
                                    }
                                    dose = Integer.parseInt(conditionnementString);
                                }
                            }

                            if(dose == 0)
                                dose = (int) produit.getCond_distrib();

                            quantite_a_afficher = dose;
                            peremptionProduitCourant = date_string;
                            numeroLotProduitCourant = lot;
                            designationProduitCourant = produit.getDesignation_interne();
                            referenceProduitCourant = produit.getRef_fourni();
                            quantiteProduitCourant = String.valueOf(dose);
                            objetReceptionScanneeCourant.setGs1_scannee(s.toString().substring(0, s.length() - 1));
                            objetReceptionScanneeCourant.setQuantiteScannee(dose);
                            codeInconnu = false;
                            if(serialisation_courante != null)
                                objetReceptionScanneeCourant.setResultat_france_mvo(serialisation_courante.getResultat());

                            AjoutDuProduit();
                        }
                        else
                        {
                            ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit inconnu");
                            code = "";
                        }
                    }
                    else
                    {
                        code = "";
                        ((ScannerSearchOnlyActivity)context).afficherSnackBar("Produit déjà scanné");
                    }
                } else {
                    code = s.toString().substring(0, s.length() - 1);
                }
            }
        }
        else
        {
            if (s.toString().endsWith("\n"))
            {
                AjoutDuProduit();

                String scan = s.toString().substring(0, s.length()-1);
                if (scan.startsWith("PHITAGPLACE+")) {
                    String[] scan_tab = scan.split(":");
                    int uid = Integer.parseInt(scan_tab[scan_tab.length-1]);
                    Depot_Emplacement depot_emplacements = EmplacementOpenHelper.getUnEmplacementByID(db, uid);
                    if(depot_emplacements != null)
                    {
                        if(premier)
                        {
                            zone_courante = ZoneOpenHelper.getUneZoneByID(db, depot_emplacements.getZoneID());
                            emplacementProduitCourant = depot_emplacements.getAdressage();
                            zoneProduitCourant = zone_courante.getZoneName();
                            objetReceptionScanneeCourant.setEmplacement_uid(depot_emplacements.get_UID());
                            ZoneEmplacement.add(zone_courante+" - "+emplacementProduitCourant);
                            ancienZoneEmplacement = zone_courante+" - "+emplacementProduitCourant;
                            premier = false;
                        }
                        else
                        {
                            if(initialisation)
                            {
                                mapExpandable.put(ancienZoneEmplacement, listSameEmplacement);
                                initialisation = false;
                            }
                            listSameEmplacement = new ArrayList<>();
                            zone_courante = ZoneOpenHelper.getUneZoneByID(db, depot_emplacements.getZoneID());
                            emplacementProduitCourant = depot_emplacements.getAdressage();
                            zoneProduitCourant = zone_courante.getZoneName();
                            objetReceptionScanneeCourant.setEmplacement_uid(depot_emplacements.get_UID());
                            int index = ZoneEmplacement.indexOf(zone_courante+" - "+emplacementProduitCourant);
                            if(index == -1)
                            {
                               // ZoneEmplacement.add(zone_courante+" - "+emplacementProduitCourant);
                                ancienZoneEmplacement = zone_courante+" - "+emplacementProduitCourant;
                            }
                            else
                            {
                                ancienZoneEmplacement = ZoneEmplacement.get(index);
                                listSameEmplacement.addAll(mapExpandable.get(ancienZoneEmplacement));
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean AjoutDuProduit()
    {

        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
            return false;
        else if(objetReceptionScanneeCourant.getEmplacement_uid() == 0)
            return false;
        else
        {
            int index = ZoneEmplacement.indexOf(zone_courante+" - "+emplacementProduitCourant);
            if(index == -1) {
                ZoneEmplacement.add(zone_courante + " - " + emplacementProduitCourant);
            }
            VerificationProduit();
            liste_resultat.add(objetReceptionScanneeCourant);
            listSameEmplacement.add(objetReceptionScanneeCourant);
            objetReceptionScanneeCourant = new ObjetReceptionScannee("", objetReceptionScanneeCourant.getEmplacement_uid(), 0, "");
            mapExpandable.put(ancienZoneEmplacement, listSameEmplacement);
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
                    List<String> stringRetourList = data.getExtras().getStringArrayList("stringList");

                    for (String code : stringRetourList) {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                        if (gs1Decoupe.size() != 0) {
                            stringList.add(code);
                        }
                    }
                    break;
            }
        }
    }

    public void ChangerQuantite(int quantite)
    {
        objetReceptionScanneeCourant.setQuantiteScannee(quantite);
    }

    private void VerificationProduit()
    {
        int index = -1;
        boolean present = false;
        Map<String, String> gs1DecoupeCourant = OutilsDecodage.decouperGTIN(objetReceptionScanneeCourant.getGs1_scannee());
        String gtinCourant = gs1DecoupeCourant.get(OutilsDecodage.codeGtin);
        String serieCourant = gs1DecoupeCourant.get(OutilsDecodage.numeroSerie);
        String lotCourant = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
        int emplacementCourant = objetReceptionScanneeCourant.getEmplacement_uid();
        int quantiteCourante = objetReceptionScanneeCourant.getQuantiteScannee();

        //gestion dans la liste des resultats
        for(ObjetReceptionScannee objetTemp : liste_resultat)
        {
            index ++;
            Map<String, String> gs1DecoupeTemp = OutilsDecodage.decouperGTIN(objetTemp.getGs1_scannee());
            String gtinTemp = gs1DecoupeTemp.get(OutilsDecodage.codeGtin);
            String serieTemp = gs1DecoupeTemp.get(OutilsDecodage.numeroSerie);
            String lotTemp = gs1DecoupeTemp.get(OutilsDecodage.numeroLot);
            int emplacementTemp = objetTemp.getEmplacement_uid();
            if(gs1DecoupeCourant.size() != 1)
            {
                if(gs1DecoupeTemp.size() != 1)
                {
                    if(serieCourant.contentEquals("") && serieTemp.contentEquals(""))
                    {
                        if(gtinCourant.contentEquals(gtinTemp) && lotCourant.contentEquals(lotTemp) && emplacementCourant == emplacementTemp)
                        {
                            int quantiteTemp = objetTemp.getQuantiteScannee();
                            objetReceptionScanneeCourant.setQuantiteScannee(quantiteCourante+quantiteTemp);
                            present = true;
                            break;
                        }
                    }
                    else
                    {
                        if(gtinCourant.contentEquals(gtinTemp) && lotCourant.contentEquals(lotTemp) && emplacementCourant == emplacementTemp && serieCourant.contentEquals(serieTemp))
                        {
                            int quantiteTemp = objetTemp.getQuantiteScannee();
                            objetReceptionScanneeCourant.setQuantiteScannee(quantiteCourante+quantiteTemp);
                            present = true;
                            break;
                        }
                    }

                }
            }
            else
            {
                if(objetTemp.getGs1_scannee().contentEquals(objetReceptionScanneeCourant.getGs1_scannee()) && emplacementCourant == emplacementTemp)
                {
                    int quantiteTemp = objetTemp.getQuantiteScannee();
                    objetReceptionScanneeCourant.setQuantiteScannee(quantiteCourante+quantiteTemp);
                    present = true;
                    break;
                }
            }

        }

        if(present)
        {
            liste_resultat.remove(index);
            List<ObjetReceptionScannee> listTemp = new ArrayList<>();
            listTemp.addAll(mapExpandable.get(ancienZoneEmplacement));
            index = -1;
            present = false;
            if(listTemp != null)
            {
                for(ObjetReceptionScannee objetTemp : listTemp)
                {
                    index ++;
                    Map<String, String> gs1DecoupeTemp = OutilsDecodage.decouperGTIN(objetTemp.getGs1_scannee());
                    String gtinTemp = gs1DecoupeTemp.get(OutilsDecodage.codeGtin);
                    String lotTemp = gs1DecoupeTemp.get(OutilsDecodage.numeroLot);
                    int emplacementTemp = objetTemp.getEmplacement_uid();
                    if(gs1DecoupeCourant.size()!=1)
                    {
                        if(gs1DecoupeTemp.size() != 1)
                        {
                            if(gtinCourant.contentEquals(gtinTemp) && lotCourant.contentEquals(lotTemp) && emplacementCourant == emplacementTemp)
                            {
                                present = true;
                                break;
                            }
                        }
                    }
                    else
                    {
                        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(objetTemp.getGs1_scannee()) && emplacementCourant == emplacementTemp)
                        {
                            present = true;
                            break;
                        }
                    }
                }
            }

            if(present)
            {
                listTemp.remove(index);
                listSameEmplacement.clear();
                listSameEmplacement.addAll(listTemp);
            }
        }
    }

    public void Initialisation(List<ObjetReceptionScannee> liste_resultat)
    {
        List<ObjetReceptionScannee> listTemp = new ArrayList<>();
        boolean premierPassage = true;
        int size = liste_resultat.size();
        int index = 0;
        String ancienEmplacement = "";
        for(ObjetReceptionScannee courant : liste_resultat)
        {
            index ++;
            int emplacement_iud = courant.getEmplacement_uid();
            Depot_Emplacement emplacementCourant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_iud);
            zone_courante = ZoneOpenHelper.getUneZoneByID(db, emplacementCourant.getZoneID());
            String adressage = zone_courante+" - "+emplacementCourant.getAdressage();

            if(index == size)
            {

                if(ZoneEmplacement.indexOf(adressage) == -1)
                {
                    mapExpandable.put(ancienEmplacement, listTemp);
                    ancienEmplacement = adressage;
                    ZoneEmplacement.add(adressage);
                    listTemp = new ArrayList<>();
                    listTemp.add(courant);
                }
                else
                {
                    listTemp.add(courant);
                }
                mapExpandable.put(adressage, listTemp);
                listSameEmplacement = new ArrayList<>();
                listSameEmplacement.addAll(listTemp);
                emplacementProduitCourant = zone_courante+" - "+emplacementCourant.getAdressage();
                zoneProduitCourant = zone_courante.getZoneName();
                objetReceptionScanneeCourant.setEmplacement_uid(emplacement_iud);
                emplacementProduitCourant = emplacementCourant.getAdressage();
                ancienZoneEmplacement = adressage;
            }
            else if(premierPassage)
            {
                ancienEmplacement = adressage;
                if(ZoneEmplacement.indexOf(adressage) == -1)
                {
                    ZoneEmplacement.add(adressage);
                    listTemp.add(courant);
                }
                else
                {
                    listTemp.addAll(mapExpandable.get(adressage));
                    listTemp.add(courant);
                }

                premierPassage = false;
            }
            else
            {
                if(ZoneEmplacement.indexOf(adressage) == -1)
                {
                    mapExpandable.put(ancienEmplacement, listTemp);
                    listTemp = new ArrayList<>();
                    listTemp.add(courant);
                    ancienEmplacement = adressage;
                }
                else
                {
                    if(mapExpandable.get(adressage) != null)
                    {
                        listTemp.addAll(mapExpandable.get(adressage));
                        listTemp.add(courant);
                    }
                }
            }
        }
        initialisation = true;
        premier = false;
    }
}
