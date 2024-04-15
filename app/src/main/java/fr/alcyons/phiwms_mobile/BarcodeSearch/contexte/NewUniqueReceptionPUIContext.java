package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_ReceptionPUI_Adapte;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;

public class NewUniqueReceptionPUIContext {
    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    private List<String> ListeGTIN;
    private int userId;
    private int CommandeId;
    public int reliquat_id_courant;
    public boolean codeInconnu;
    public PH_Reliquat reliquat_courant;
    private List<String> stringList;
    private List<String> listeSerie;
    private Serialisation serialisationService;
    private Utilisateur utilisateurConnecte;
    public PH_Reliquat_ReceptionPUI_Adapte phReliquatReceptionPUIAdapte_courant;
    public PH_Reliquat_ReceptionPUI_Adapte.Lot nouveau_lot;
    public PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement nouvelEmplacement;
    public Produit produitCourant;
    public Depot_Emplacement emplacement_courant;
    public int qte_lot_courant = 0;
    public int conditionnement_achat;
    public boolean validation;
    public Depot_Emplacement emplacementPrecedent;
    public Produit produitPrecedent;

    public NewUniqueReceptionPUIContext(final Context context, final SQLiteDatabase db, Utilisateur utilisateurConnecte, List<String> ListeGTIN, int userId, int CommandeId, PH_Reliquat reliquat_courant, PH_Reliquat_ReceptionPUI_Adapte reliquat_receptionPuiAdapte, Depot_Emplacement emplacementPrecedent, Produit produitPrecedent){
        this.context = context;
        this.db = db;
        this.ListeGTIN = ListeGTIN;
        this.userId = userId;
        this.utilisateurConnecte = utilisateurConnecte;
        this.CommandeId = CommandeId;
        this.reliquat_courant = reliquat_courant;
        this.phReliquatReceptionPUIAdapte_courant = reliquat_receptionPuiAdapte;
        serialisationService = new Serialisation(context, db, utilisateurConnecte);
        nouveau_lot = null;
        validation = true;
        stringList = new ArrayList<>();
        this.emplacementPrecedent = emplacementPrecedent;
        this.produitPrecedent = produitPrecedent;
    }

    public void onTextWatcher(final Editable s){
        String chaine = s.toString();

        if (chaine.endsWith("\n"))
        {
            chaine = chaine.toString().substring(0, chaine.length() - 1);
        }

        if(chaine.startsWith("PHITAGPLACE+") && nouveau_lot != null)
        {
/*            if(nouveau_lot != null && emplacement_courant != null)
            {
                ValiderScan();
            }*/
            String[] tab_emplacement = chaine.split(":");
            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);
        }
/*        else if(emplacement_courant == null)
        {
            ((BarcodePreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
            validation = true;
        }*/
        else if(chaine.startsWith("PHITAGPLACE+") && nouveau_lot == null)
        {
            ((ScannerPreparationActivity) context).afficherSnackBar("Veuillez scanner une référence");
            validation = true;
        }
        else if(emplacement_courant == null && nouveau_lot != null)
        {
            ((ScannerPreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
            validation = true;
        }
        else
        {
            if(nouveau_lot == null) {

                if (!validation) {
                    validation = false;
                } else {
                    validation = false;
                }

                Produit produit = null;
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
                            produitCourant = produits.get(0);
                        }
                        else if(produits.size() > 1)
                        {
                            String activityName = context.getClass().getSimpleName();
                            ((BarcodePreparationActivity)context).afficherSnackBar("Produit déjà préparer en intégralité");
                            nouveau_lot = null;
                            qte_lot_courant = 0;
                            validation = true;
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
                    List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.toString().substring(0, s.length()-1));
                    if(produits != null)
                    {
                        if (produits.size() == 1) {
                            produitCourant = produits.get(0);
                            codeInconnu = true;
                        }
                        else if(produits.size() > 1)
                        {
                            String activityName = context.getClass().getSimpleName();
                            ((BarcodePreparationActivity)context).afficherSnackBar("Produit déjà préparer en intégralité");
                            nouveau_lot = null;
                            qte_lot_courant = 0;
                            validation = true;
                            code = "";
                        }
                    }
                }


                //on vérifie que le produit fait bien partie de la préparation
                boolean produit_present = false;
                if(produitCourant != null)
                {
                    //on vérifie que le produit soit le même que le précédent
                    if(produitPrecedent != null)
                    {
                        if(produitCourant.getID_produit() == produitPrecedent.getID_produit())
                        {
                            if(produitCourant.getEmplacement_PUI_Defaut().contentEquals("") || produitCourant.getEmplacement_PUI_Defaut() == null)
                            {
                                if(emplacementPrecedent != null)
                                {
                                    emplacement_courant = emplacementPrecedent;
                                }
                            }
                            else
                            {
                                Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                                String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                                String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                                if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                                {
                                    Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                                    if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                                    {
                                        emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                                    }
                                }
                            }
                        }
                        else
                        {
                            Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                            String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                            String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                            if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                            {
                                Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                                if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                                {
                                    emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                                }
                            }
                        }
                    }
                    else
                    {
                        Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                        String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                        String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                        if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                        {
                            Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                            if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                            {
                                emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                            }
                        }
                    }

                    produit_present = true;
                    if(produitCourant.getID_produit() != reliquat_courant.getProduitID())
                    {
                        produit_present = false;
                    }

                    if(produitCourant.getCond_Achat_Gros_volume() != 0 && produitCourant.getCond_Achat_Gros_volume() <= reliquat_courant.getQteReliquat_X())
                    {
                        qte_lot_courant = (int)produitCourant.getCond_Achat_Gros_volume();
                    }
                    else if(produitCourant.getCond_achat() != 0 && produitCourant.getCond_achat() <= reliquat_courant.getQteReliquat_X())
                    {
                        qte_lot_courant = (int) produitCourant.getCond_achat();
                    }
                    else
                    {
                        qte_lot_courant = reliquat_courant.getQteReliquat_X();
                    }
                    conditionnement_achat = produitCourant.getCond_achat();
                }

                if(reliquat_courant != null && reliquat_courant.getQteReliquat_X() ==0)
                {
                    ((ScannerPreparationActivity)context).afficherSnackBar("Produit déjà préparer en intégralité");
                    nouveau_lot = null;
                    qte_lot_courant = 0;
                    emplacement_courant = null;
                    validation = true;
                }
                else if(!produit_present)
                {
                    ((ScannerPreparationActivity)context).afficherSnackBar("Mauvais produit scanné");
                    nouveau_lot = null;
                    qte_lot_courant = 0;
                    emplacement_courant = null;
                    validation = true;
                    //lot_courant = null;
                }
                else if(serie != null && !serie.contentEquals("") && produitCourant.isSerialiser_Reception_Delivrance() && produitCourant.isSuivi_Serialisation() && stringList.indexOf(chaine) == -1)
                {
                    if(produitCourant != null)
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
                                } else {
                                    String messageTexteFranceMVO = "";
                                }
                            }

                            /***Fin de la sérialisation***/
                        }
                        codeInconnu = false;
                        if(phReliquatReceptionPUIAdapte_courant!=null)
                        {
                            nouveau_lot = phReliquatReceptionPUIAdapte_courant.new Lot(lot, date_peremption_courant, "", "");
                            conditionnement_achat = produitCourant.getCond_achat();
                        }
                    }
                    else
                    {
                        ((ScannerPreparationActivity) context).afficherSnackBar("Produit inconnu");
                        nouveau_lot = null;
                        qte_lot_courant = 0;
                        phReliquatReceptionPUIAdapte_courant = null;
                        validation = true;
                        code = "";
                    }
                }
                else
                {
                    if(phReliquatReceptionPUIAdapte_courant!=null)
                    {
                        nouveau_lot = phReliquatReceptionPUIAdapte_courant.new Lot(lot, date_peremption_courant, "", "");
                        conditionnement_achat = produitCourant.getCond_achat();
                    }
                }
            }
        }
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_SCANNER:
                    emplacementPrecedent = (Depot_Emplacement) data.getExtras().getSerializable("EmplacementPrecedent");
                    produitPrecedent = (Produit) data.getExtras().getSerializable("ProduitPrecedent");
                    phReliquatReceptionPUIAdapte_courant = (PH_Reliquat_ReceptionPUI_Adapte) data.getExtras().getSerializable("reliquatAdapte");
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

        if(s.startsWith("PHITAGPLACE+") && nouveau_lot != null)
        {
/*            if(nouveau_lot != null && emplacement_courant != null)
            {
                ValiderScan();
            }*/
            String[] tab_emplacement = s.split(":");
            int emplacement_uid = Integer.parseInt(tab_emplacement[tab_emplacement.length-1]);

            emplacement_courant = EmplacementOpenHelper.getUnEmplacementByID(db, emplacement_uid);
        }
/*        else if(emplacement_courant == null)
        {
            ((BarcodePreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
            validation = true;
        }*/
        else if(s.startsWith("PHITAGPLACE+") && nouveau_lot == null)
        {
            ((BarcodePreparationActivity) context).afficherSnackBar("Veuillez scanner une référence");
            validation = true;
        }
        else if(emplacement_courant == null && nouveau_lot != null)
        {
            ((BarcodePreparationActivity) context).afficherSnackBar("Veuillez scanner un emplacement");
            validation = true;
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
                        produitCourant = produits.get(0);
                    }
                    else if(produits.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else if(activityName.contentEquals("BarcodePreparationActivity"))
                        {
                            ((BarcodePreparationActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
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
                        produitCourant = produits.get(0);
                        codeInconnu = true;
                    }
                    else if(produits.size() > 1)
                    {
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else if(activityName.contentEquals("BarcodePreparationActivity"))
                        {
                            ((BarcodePreparationActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                        }
                        validation = true;
                        code = "";
                        return false;
                    }
                }
            }

            //on vérifie que le produit fait bien partie de la préparation
            boolean produit_present = false;
            if(produitCourant != null)
            {
                //on vérifie que le produit soit le même que le précédent
                if(produitPrecedent != null)
                {
                    if(produitCourant.getID_produit() == produitPrecedent.getID_produit())
                    {
                        if(produitCourant.getEmplacement_PUI_Defaut().contentEquals("") || produitCourant.getEmplacement_PUI_Defaut() == null)
                        {
                            if(emplacementPrecedent != null)
                            {
                                emplacement_courant = emplacementPrecedent;
                            }
                        }
                        else
                        {
                            Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                            String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                            String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                            if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                            {
                                Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                                if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                                {
                                    emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                                }
                            }
                        }
                    }
                    else
                    {
                        Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                        String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                        String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                        if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                        {
                            Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                            if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                            {
                                emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                            }
                        }
                    }
                }
                else
                {
                    Depot depot_pui = DepotOpenHelper.getDepotPUI(db);
                    String zone_pui_defaut = produitCourant.getZone_PUI_Defaut();
                    String emplacemement_pui_defaut = produitCourant.getEmplacement_PUI_Defaut();

                    if(zone_pui_defaut != null && !zone_pui_defaut.contentEquals("") && depot_pui != null)
                    {
                        Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depot_pui, zone_pui_defaut);

                        if(zone_courante!=null && emplacemement_pui_defaut != null && !emplacemement_pui_defaut.contentEquals(""))
                        {
                            emplacement_courant = EmplacementOpenHelper.getUnEmplacementZoneEtNom(db, zone_courante, emplacemement_pui_defaut);
                        }
                    }
                }

                produit_present = true;
                if(produitCourant.getID_produit() != reliquat_courant.getProduitID())
                {
                    produit_present = false;
                }

                if(produitCourant.getCond_Achat_Gros_volume() != 0 && produitCourant.getCond_Achat_Gros_volume() <= reliquat_courant.getQteReliquat_X())
                {
                    qte_lot_courant = (int)produitCourant.getCond_Achat_Gros_volume();
                }
                else if(produitCourant.getCond_achat() != 0 && produitCourant.getCond_achat() <= reliquat_courant.getQteReliquat_X())
                {
                    qte_lot_courant = (int) produitCourant.getCond_achat();
                }
                else
                {
                    qte_lot_courant = reliquat_courant.getQteReliquat_X();
                }
                conditionnement_achat = produitCourant.getCond_achat();
            }

            if(reliquat_courant != null && reliquat_courant.getQteReliquat_X() ==0)
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Produit déjà préparer en intégralité");
                validation = true;
                emplacement_courant = null;
            }
            else if(!produit_present)
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Mauvais produit scanné");
                validation = true;
                emplacement_courant = null;
                //lot_courant = null;
            }
            else if(serie != null && !serie.contentEquals("") && stringList.indexOf(s.toString().substring(0, s.length()-1)) == -1)
            {
                if(produitCourant != null)
                {
                    if(codeInconnu)
                    {
                        gtin_courant = s.toString().substring(0, s.length() - 1);
                    }
                    PH_Serialisation serialisation_courante = null;

                    if(!codeInconnu)
                    {

                        /***Début de la sérialisation***/

                        if (!serie.contentEquals("") && produitCourant.isSerialiser_Reception_Delivrance() && produitCourant.isSuivi_Serialisation() && listeSerie.indexOf(serie) == -1) {
                            listeSerie.add(serie);
                            String resultat = "";
                            boolean differe = false;
                            if (!OutilsGestionConnexionReseau.isServerAccessible(context))
                                differe = true;

                            if (conditionnementString.contentEquals("")) {
                                produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gtin_courant);
                                if (produitCourant == null) {
                                    produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, "01" + gtin_courant);
                                }
                            }

                            long ph_serialisation_uid = 0;
                            String peremptionSerialisation = date_peremption_courant;
                            peremptionSerialisation = peremptionSerialisation.substring(2);
                            peremptionSerialisation = peremptionSerialisation.replace("-", "");
                            ph_serialisation_uid = serialisationService.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, gtin_courant, "GTIN", lot, peremptionSerialisation, serie, "ActionUtilisateur", String.valueOf(0), "", "");


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


                                //((BarcodeCaptureActivity) context).afficherAlerteFranceMVO(produit.getDesignation_interne(), resultat, serie, motif);

                            } else {
                                String messageTexteFranceMVO = "";
                            }
                        }

                        /***Fin de la sérialisation***/
                    }
                    codeInconnu = false;
                    if(phReliquatReceptionPUIAdapte_courant!=null)
                    {
                        nouveau_lot = phReliquatReceptionPUIAdapte_courant.new Lot(lot, date_peremption_courant, "", "");
                        conditionnement_achat = produitCourant.getCond_achat();
                    }
                }
                else
                {
                    String activityName = context.getClass().getSimpleName();
                    if(activityName.contentEquals("BarcodeCaptureActivity"))
                    {
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                    }
                    else if(activityName.contentEquals("BarcodePreparationActivity"))
                    {
                        ((BarcodePreparationActivity) context).afficherSnackBar("Produit inconnu");
                    }
                    else
                    {
                        ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit inconnu");
                    }
                    validation = true;
                    code = "";
                }
            }
            else
            {
                if(phReliquatReceptionPUIAdapte_courant!=null)
                {
                    nouveau_lot = phReliquatReceptionPUIAdapte_courant.new Lot(lot, date_peremption_courant, "", "");
                    conditionnement_achat = produitCourant.getCond_achat();
                }
            }
        }



        return confirmation;
    }

    public void ValiderScan()
    {
        //on garde en mémoire l'emplacement scanné
        emplacementPrecedent = emplacement_courant;
        if(produitCourant != null)
            produitPrecedent = produitCourant;

        if(phReliquatReceptionPUIAdapte_courant != null && emplacement_courant != null && nouveau_lot!=null)
        {
            Depot_Zone zone = ZoneOpenHelper.getUneZoneByID(db, emplacement_courant.getZoneID());
            nouvelEmplacement = phReliquatReceptionPUIAdapte_courant.new ZoneEtEmplacement(zone.getZoneID(), zone.getZoneName(), emplacement_courant.get_UID(), emplacement_courant.getAdressage(), qte_lot_courant);

            //vérification de l'existence ou non du lot
            int index_lot = -1;
            int index_emplacement = -1;
            boolean emplacement_existant = false;
            for(PH_Reliquat_ReceptionPUI_Adapte.Lot lot_courant : phReliquatReceptionPUIAdapte_courant.getlotList())
            {
                index_lot++;
                if(lot_courant.getNumeroLot().contentEquals(nouveau_lot.getNumeroLot()))
                {
                    for(PH_Reliquat_ReceptionPUI_Adapte.ZoneEtEmplacement zoneEtEmplacement_courant : lot_courant.getZoneEtEmplacementList())
                    {
                        index_emplacement ++;
                        if(nouvelEmplacement.getEmplacementId() == zoneEtEmplacement_courant.getEmplacementId())
                        {
                            emplacement_existant = true;
                            nouvelEmplacement.setQuantite(nouvelEmplacement.getQuantite()+zoneEtEmplacement_courant.getQuantite());
                            break;
                        }
                    }

                    if(emplacement_existant)
                    {
                        lot_courant.getZoneEtEmplacementList().remove(index_emplacement);
                        break;
                    }
                }
            }

            if(emplacement_existant)
                phReliquatReceptionPUIAdapte_courant.getlotList().remove(index_lot);

            nouveau_lot.getZoneEtEmplacementList().add(nouvelEmplacement);
            phReliquatReceptionPUIAdapte_courant.getlotList().add(nouveau_lot);

            //mise à jour du ph_reliquat
            reliquat_courant.setQteLivraison(reliquat_courant.getQteLivraison()+qte_lot_courant);
            reliquat_courant.setQteReliquat_X(reliquat_courant.getQteReliquat_X()-qte_lot_courant);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, reliquat_courant);

            //on remet les valeurs à null
            nouvelEmplacement = null;
            nouveau_lot = null;
            qte_lot_courant = 0;
        }
        validation = true;
    }

}

