package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Stock_Lot_EmplacementLightOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 06/06/2019.
 */

public class PreparationScanneeScanProduitScannerSearchContexte {
    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    private List<String> ListeGTIN;
    private int userId;
    private TextView message;
    public int uid_preparationLigneCourant;
    public List<String> stringList;
    public Map<Integer, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>> tableau_renvoyer;
    private String messageTexte;
    private String messageTexteFranceMVO;
    private int messageColor;
    //private ToneGenerator toneGen1;
    private Utilisateur utilisateur;
    private PH_Preparation preparation_courante;
    private Serialisation serialisation;
    public int nbMaxQuantite;

    public String emplacementProduitCourant;
    public String zoneProduitCourant;
    private int uidEmplacementCourant;
    private ObjetReceptionScannee objetReceptionScanneeCourant;
    public String designationProduitScanne;
    public String referenceProduitScanne;
    public int quantiteAAfficher;
    public String numeroLotProduitScanne;
    public String peremptionProduitScanne;
    private PH_Preparation_Ligne ph_preparation_ligne_courant;
    public List<ObjetReceptionScannee> liste_resultat;
    public List<String> liste_code_scanne;
    public boolean serialisation_preparation;
    public int quantite_max_number_picker;
    public List<String> liste_designation_produit;
    public boolean emplacement_saisie = true;
    public Map<String, String> Map_Zone_Emplacement_Defaut;

    private List<PH_Preparation_Ligne>liste_preparation_ligne;
    public Map<String, List<ObjetReceptionScannee>> mapExpandable;

    public PreparationScanneeScanProduitScannerSearchContexte(final Context context, final SQLiteDatabase db, int userId, TextView message, int preparation_id){
        this.context = context;
        this.db = db;
        this.userId = userId;
        this.message = message;

        //toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        code = "";
        bannerTexte = "Scanner le datamatrix d'un produit";
        scannerContexteProduit = String.valueOf(R.string.scannerContextePreparation);

        stringList = new ArrayList<>();
        tableau_renvoyer = new HashMap<Integer, List<PH_Preparation_Ligne_Preparation_Adapte.LotAdapte>>();
        utilisateur = UtilisateurOpenHelper.getUtilisateurByID(db, userId);
        serialisation = new Serialisation(context, db, utilisateur);
        preparation_courante = PH_PreparationOpenHelper.getPH_PreparationByID(db, preparation_id);
        liste_preparation_ligne = new ArrayList<>();
        if(preparation_courante != null)
        {
            liste_preparation_ligne = PH_Preparation_LigneOpenHelper.getAllPHPreparationLignesParPHPreparation(db, preparation_courante);
            liste_designation_produit = new ArrayList<>();

            for(PH_Preparation_Ligne ligneCourante : liste_preparation_ligne)
            {
                Produit produitCourant = ProduitOpenHelper.getProduitByID(db, ligneCourante.getProduitID());
                String entete = produitCourant.getDesignation_interne()+"-"+ligneCourante.getQte_APreparer();
                if(liste_designation_produit.indexOf(entete) == -1)
                {
                    liste_designation_produit.add(entete);
                }
            }

            objetReceptionScanneeCourant = new ObjetReceptionScannee();
            liste_resultat = new ArrayList<>();
            liste_code_scanne = new ArrayList<>();
            mapExpandable = new LinkedHashMap<>();
            Map_Zone_Emplacement_Defaut = new LinkedHashMap<>();
            serialisation_preparation = false;
        }
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();
        if (chaine.endsWith("\n")) {
            chaine = chaine.substring(0, chaine.length() - 1);
        }
        Produit produit_courant = null;
        if(!chaine.startsWith("PHITAGPLACE+"))
        {
            //check si le produit scanné est le meme que le précédent qui n'a pas encore d'emplacement
            boolean continuer = true;

                //gestion du dernier produit scannee
            Produit produit_last = null;
            if(liste_resultat.size()>0)
            {
                Map<String, String> gs1DecoupeLast  = OutilsDecodage.decouperGTIN(liste_resultat.get(liste_resultat.size()-1).getGs1_scannee());

                if(gs1DecoupeLast.size() != 1)
                {
                    produit_last = ProduitOpenHelper.getUnProduitParGTIN(db, gs1DecoupeLast.get(OutilsDecodage.codeGtin));
                }
                else
                {
                    produit_last = ProduitOpenHelper.getUnProduitByCodeInconnu(db, liste_resultat.get(liste_resultat.size()-1).getGs1_scannee());
                }
            }

            //gestion du produit courant
            Map<String, String> gs1DecoupeCourant  = OutilsDecodage.decouperGTIN(chaine);

            if(gs1DecoupeCourant.size() != 1)
            {
                produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1DecoupeCourant.get(OutilsDecodage.codeGtin));
                numeroLotProduitScanne = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                String date_peremption_courant = gs1DecoupeCourant.get(OutilsDecodage.dateDePeremption);
                DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                Date date = new Date();

                try {
                    date = dateFormat1.parse(date_peremption_courant);
                    peremptionProduitScanne =  dateFormat2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                produit_courant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, chaine);
            }

            if(liste_resultat.size()>0)
            {
                if(produit_courant != null  && produit_last != null && liste_resultat.get(liste_resultat.size()-1).getEmplacement_uid() == 0 && produit_courant.getID_produit() != produit_last.getID_produit())
                {
                    continuer = false;
                }
            }

            if(!continuer)
            {
                //emplacement_saisie = false;
                //designationProduitScanne = "";
                ((ScannerSearchOnlyActivity) context).afficherSnackBar("Scannez l'emplacement des produit précédent");
            }
            else if(liste_code_scanne.indexOf(chaine) == -1)
            {
                /*Map<String, String> gs1DecoupeCourant  = OutilsDecodage.decouperGTIN(chaine);

                if(gs1DecoupeCourant.size() != 1)
                {
                    produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1DecoupeCourant.get(OutilsDecodage.codeGtin));
                    numeroLotProduitScanne = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                    String date_peremption_courant = gs1DecoupeCourant.get(OutilsDecodage.dateDePeremption);
                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                    Date date = new Date();

                    try {
                        date = dateFormat1.parse(date_peremption_courant);
                        peremptionProduitScanne =  dateFormat2.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    produit_courant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, chaine);
                }*/


                if(produit_courant != null)
                {
                    boolean present = false;
                    for(PH_Preparation_Ligne courant : liste_preparation_ligne)
                    {
                        if(courant.getProduitID() == produit_courant.getID_produit())
                        {
                            designationProduitScanne = produit_courant.getDesignation_interne();
                            referenceProduitScanne = produit_courant.getRef_fourni();
                            ph_preparation_ligne_courant = courant;
                            objetReceptionScanneeCourant.setGs1_scannee(chaine);

                            //gestion de la sérialisation
                            if(produit_courant.isSuivi_Serialisation() && !produit_courant.isSerialiser_Reception_Delivrance())
                            {
                                serialisation_preparation = true;
                                quantiteAAfficher = produit_courant.getCond_achat();
                            }
                            else
                            {
                                serialisation_preparation = false;
                                quantite_max_number_picker = ph_preparation_ligne_courant.getQte_APreparer();
                                quantiteAAfficher = (int) produit_courant.getCond_distrib();
                            }

                            if(quantiteAAfficher > quantite_max_number_picker)
                            {
                                quantiteAAfficher = quantite_max_number_picker;
                            }

                            present = true;
                            break;
                        }
                    }

                    if(!present)
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit non présent");
                    }

                    if(present && ph_preparation_ligne_courant.getQte_APreparer() == ph_preparation_ligne_courant.getQte_preparer())
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit déjà préparé en intégralité");
                    }

                    ajouterProduit();

                }
                else
                {
                    designationProduitScanne = "";
                    numeroLotProduitScanne = "";
                    peremptionProduitScanne = "";
                    quantiteAAfficher = 0;
                    referenceProduitScanne = "";
                    ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit inconnu");
                }
            }
            else
            {
                designationProduitScanne = "";
                numeroLotProduitScanne = "";
                peremptionProduitScanne = "";
                quantiteAAfficher = 0;
                referenceProduitScanne = "";
                ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit déjà scanné");
            }
        }
        else
        {
            if (chaine.startsWith("PHITAGPLACE+")) {
                String[] scan_tab = chaine.split(":");
                int uid = Integer.parseInt(scan_tab[scan_tab.length-1]);
                Depot_Emplacement depot_emplacements = EmplacementOpenHelper.getUnEmplacementByID(db, uid);
                if(depot_emplacements != null)
                {
                    Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, depot_emplacements.getZoneID());
                    emplacementProduitCourant = depot_emplacements.getAdressage();
                    zoneProduitCourant = zone_courante.getZoneName();

                    for(ObjetReceptionScannee objet_courant : liste_resultat)
                    {
                        if(objet_courant.getEmplacement_uid() == 0)
                        {
                            objet_courant.setEmplacement_uid(depot_emplacements.get_UID());
                        }
                    }


                    //objetReceptionScanneeCourant.setEmplacement_uid(depot_emplacements.get_UID());
                    //uidEmplacementCourant = depot_emplacements.get_UID();
                    //emplacement_saisie = true;
                    objetReceptionScanneeCourant = new ObjetReceptionScannee();
                    uidEmplacementCourant = 0;
                    //designationProduitScanne = produit_courant.getDesignation_interne();
                    numeroLotProduitScanne = "";
                    peremptionProduitScanne = "";
                    quantiteAAfficher = 0;
                    referenceProduitScanne = "";
                    uidEmplacementCourant = 0;
                    serialisation_preparation = false;
                }
            }
        }
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

    public boolean onTap(String chaine) {
        boolean confirmation = true;

        if(!chaine.startsWith("PHITAGPLACE+"))
        {
            if(liste_code_scanne.indexOf(chaine) == -1)
            {
                Map<String, String> gs1DecoupeCourant  = OutilsDecodage.decouperGTIN(chaine);
                Produit produit_courant = null;
                if(gs1DecoupeCourant.size() != 1)
                {
                    produit_courant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1DecoupeCourant.get(OutilsDecodage.codeGtin));
                    numeroLotProduitScanne = gs1DecoupeCourant.get(OutilsDecodage.numeroLot);
                    String date_peremption_courant = gs1DecoupeCourant.get(OutilsDecodage.dateDePeremption);
                    DateFormat dateFormat1 = new SimpleDateFormat("yy-MM-dd");
                    DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                    Date date = new Date();

                    try {
                        date = dateFormat1.parse(date_peremption_courant);
                        peremptionProduitScanne =  dateFormat2.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    produit_courant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, chaine);
                }


                if(produit_courant != null)
                {
                    boolean present = false;
                    for(PH_Preparation_Ligne courant : liste_preparation_ligne)
                    {
                        if(courant.getProduitID() == produit_courant.getID_produit())
                        {
                            designationProduitScanne = produit_courant.getDesignation_interne();
                            referenceProduitScanne = produit_courant.getRef_fourni();
                            ph_preparation_ligne_courant = courant;
                            objetReceptionScanneeCourant.setGs1_scannee(chaine);

                            //gestion de la sérialisation
                            if(produit_courant.isSuivi_Serialisation() && !produit_courant.isSerialiser_Reception_Delivrance())
                            {
                                serialisation_preparation = true;
                                quantiteAAfficher = produit_courant.getCond_achat();
                            }
                            else
                            {
                                serialisation_preparation = false;
                                quantite_max_number_picker = ph_preparation_ligne_courant.getQte_APreparer();
                                quantiteAAfficher = (int) produit_courant.getCond_distrib();
                            }

                            present = true;
                            break;
                        }
                    }

                    if(!present)
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit non présent");
                    }

                    if(present && ph_preparation_ligne_courant.getQte_APreparer() == ph_preparation_ligne_courant.getQte_preparer())
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà préparé en intégralité");
                    }
                }
                else
                {
                    designationProduitScanne = "";
                    numeroLotProduitScanne = "";
                    peremptionProduitScanne = "";
                    quantiteAAfficher = 0;
                    referenceProduitScanne = "";
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                }
            }
            else
            {
                designationProduitScanne = "";
                numeroLotProduitScanne = "";
                peremptionProduitScanne = "";
                quantiteAAfficher = 0;
                referenceProduitScanne = "";
                ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà scanné");
            }
        }
        else
        {
            String scan = chaine;
            if (scan.endsWith("\n")) {
                scan = scan.substring(0, scan.length() - 1);
            }

            if (scan.startsWith("PHITAGPLACE+")) {
                String[] scan_tab = scan.split(":");
                int uid = Integer.parseInt(scan_tab[scan_tab.length-1]);
                Depot_Emplacement depot_emplacements = EmplacementOpenHelper.getUnEmplacementByID(db, uid);
                if(depot_emplacements != null)
                {
                    Depot_Zone zone_courante = ZoneOpenHelper.getUneZoneByID(db, depot_emplacements.getZoneID());
                    emplacementProduitCourant = depot_emplacements.getAdressage();
                    zoneProduitCourant = zone_courante.getZoneName();
                    objetReceptionScanneeCourant.setEmplacement_uid(depot_emplacements.get_UID());
                    uidEmplacementCourant = depot_emplacements.get_UID();
                }
            }
        }

        return confirmation;
    }

    public boolean ajouterProduit()
    {

        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
            return false;
        else if(designationProduitScanne.contentEquals(""))
            return false;
        else
        {
            String enteteExpandable = designationProduitScanne+"-"+ph_preparation_ligne_courant.getQte_APreparer();
            List<ObjetReceptionScannee> list_temp = mapExpandable.get(enteteExpandable);
            if(list_temp == null)
            {
                list_temp = new ArrayList<>();
            }

            list_temp.add(objetReceptionScanneeCourant);
            mapExpandable.put(enteteExpandable, list_temp);

            ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()+quantiteAAfficher);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
            // PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
            objetReceptionScanneeCourant.setQuantiteScannee(quantiteAAfficher);
            liste_code_scanne.add(objetReceptionScanneeCourant.getGs1_scannee());
            liste_resultat.add(objetReceptionScanneeCourant);
            objetReceptionScanneeCourant = new ObjetReceptionScannee();
            uidEmplacementCourant = 0;
            //designationProduitScanne = produit_courant.getDesignation_interne();
            numeroLotProduitScanne = "";
            peremptionProduitScanne = "";
            quantiteAAfficher = 0;
            referenceProduitScanne = "";
            uidEmplacementCourant = 0;
            serialisation_preparation = false;
            ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit préparé");
        }

        return true;
    }

    public void Initialisation(List<ObjetReceptionScannee> liste_resultat_temp)
    {
        if(liste_resultat_temp.size() > 0)
        {
            for(PH_Preparation_Ligne ligneCourante : liste_preparation_ligne)
            {
                boolean inserer = false;
                for(ObjetReceptionScannee objetCourant : liste_resultat_temp)
                {
                    Produit produitCourant = null;
                    Map<String, String> gs1Courant = OutilsDecodage.decouperGTIN(objetCourant.getGs1_scannee());
                    if(gs1Courant.size() != 1 && !objetCourant.getGs1_scannee().startsWith("ci"))
                    {
                        produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Courant.get(OutilsDecodage.codeGtin));
                    }
                    else
                    {
                        String codeinconnu = objetCourant.getGs1_scannee();
                        if(codeinconnu.startsWith("ci"))
                        {
                            Map<String, String> MapInconnu = OutilsDecodage.decouperCodeInconnnu(codeinconnu);
                            codeinconnu = MapInconnu.get("Code_Inconnu");
                        }
                        if(!codeinconnu.contentEquals(""))
                        {
                            produitCourant = ProduitOpenHelper.getUnProduitByCodeInconnu(db, codeinconnu);
                        }

                        if(produitCourant == null)
                        {
                            if(ligneCourante.getProduitDesignation().contentEquals(codeinconnu))
                            {
                                produitCourant = ProduitOpenHelper.getProduitByID(db, ligneCourante.getProduitID());
                            }
                        }
                    }

                    if(produitCourant != null)
                    {
                        if(produitCourant.isSuivi_Lot())
                        {
                            PH_Preparation_Ligne ph_preparation_ligne = null;

                            if(ligneCourante.getProduitID() == produitCourant.getID_produit())
                            {
                                ph_preparation_ligne = ligneCourante;
                                String entete = produitCourant.getDesignation_interne()+"-"+ph_preparation_ligne.getQte_APreparer();
                                List<ObjetReceptionScannee> lisTemp = mapExpandable.get(entete);
                                if(lisTemp == null)
                                {
                                    lisTemp = new ArrayList<>();
                                }
                                lisTemp.add(objetCourant);
                                mapExpandable.put(entete, lisTemp);
                                inserer = true;
                            }
                        }
                    }
                }

                if(!inserer)
                {
                    Produit produit_courant = ProduitOpenHelper.getProduitByID(db, ligneCourante.getProduitID());
                    if(produit_courant != null)
                    {
                        if(produit_courant.isSuivi_Lot())
                        {
                            String entete = ligneCourante.getProduitDesignation()+"-"+ligneCourante.getQte_APreparer();
                            List<ObjetReceptionScannee> lisTemp = mapExpandable.get(entete);
                            if(lisTemp == null)
                            {
                                lisTemp = new ArrayList<>();
                            }
                            mapExpandable.put(entete, lisTemp);
                        }
                    }
                }
            }
        }
        else
        {
            Depot depot = DepotOpenHelper.getDepotPUI(db);

            for(PH_Preparation_Ligne ligneCourante : liste_preparation_ligne)
            {
                Produit produit_courant = ProduitOpenHelper.getProduitByID(db, ligneCourante.getProduitID());
                if(produit_courant != null)
                {
                    if(produit_courant.isSuivi_Lot())
                    {
                        String entete = ligneCourante.getProduitDesignation()+"-"+ligneCourante.getQte_APreparer();
                        if(!Map_Zone_Emplacement_Defaut.containsKey(ligneCourante.getProduitDesignation()))
                        {
                           Stock_Lot_Emplacement_Light stock_lot_emplacement_light = Stock_Lot_EmplacementLightOpenHelper.getPremierStockLotEmplacementByProduitEtDepot(db, produit_courant, depot);
                           if(stock_lot_emplacement_light != null)
                           {
                            Map_Zone_Emplacement_Defaut.put(ligneCourante.getProduitDesignation(), stock_lot_emplacement_light.getZone()+"%"+stock_lot_emplacement_light.getEmplacement());
                           }
                        }
                        List<ObjetReceptionScannee> lisTemp = mapExpandable.get(entete);
                        if(lisTemp == null)
                        {
                            lisTemp = new ArrayList<>();
                        }
                        mapExpandable.put(entete, lisTemp);
                    }
                }
            }
        }
    }
}
