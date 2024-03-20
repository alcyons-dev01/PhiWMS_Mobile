package fr.alcyons.phimr4.BarcodeSearch.contexte;

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
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SurveillanceReferenceOpenHelper;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Reliquat;
import fr.alcyons.phimr4.Classes.PH_Serialisation;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Retour_Ligne;
import fr.alcyons.phimr4.Classes.SurveillanceReference;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phimr4.OutilsSerialisation.Serialisation;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 14/06/2019.
 */

public class ControleDesRetourScanContext {

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
    public int conditionnement;
    public List<String> GtinProduitScan;

    public List<String> stringList;
    public List<ObjetReceptionScannee> list_result;
    public ObjetReceptionScannee objetReceptionScanneeCourant;

    public String designationProduitCourant;
    public String referenceProduitCourant;
    public String numeroLotProduitCourant;
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
    public List<Integer> liste_id_retour_ligne;
    public int quantiteMaxNumberPicker;
    Retour_Ligne retour_ligne_courant;


    public ControleDesRetourScanContext(final Context context, final SQLiteDatabase db, final boolean modeRafale, final boolean modePhoto, final boolean modeCumule, final boolean doitEtreIdentique, final String designation, final boolean serialisation, final int conditionnement, Utilisateur utilisateurConnecte, int actionId, List<Integer> liste_id_retour_ligne){
        this.context = context;
        this.db = db;
        this.modeRafale = modeRafale;
        this.modePhoto = modePhoto;
        this.modeCumule = modeCumule;
        this.doitEtreIdentique = doitEtreIdentique;
        this.designation = designation;
        this.serialisation = serialisation;
        this.conditionnement = conditionnement;
        this.utilisateurConnecte = utilisateurConnecte;
        this.actionId = actionId;
        this.liste_id_retour_ligne = liste_id_retour_ligne;
        list_result = new ArrayList<>();
        objetReceptionScanneeCourant = new ObjetReceptionScannee("", 0, 0, "");

        code = "";
        bannerTexte = "Scanner une référence";
        scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);
        stringList = new ArrayList<>();
        GtinProduitScan = new ArrayList<>();
        serialisationService = new Serialisation(context, db, utilisateurConnecte);
        serieListe = new ArrayList<>();
        codeInconnu = false;
        uidEmplacementCourant = 0;
        liste_id_produit = new ArrayList<>();
        if(this.liste_id_retour_ligne != null)
        {
            for(Integer id_retour_ligne_courant : this.liste_id_retour_ligne)
            {
                Retour_Ligne retourCourant = Retour_LigneOpenHelper.getRetourLigneByID(db, id_retour_ligne_courant);
                if(retourCourant != null)
                {
                    liste_id_produit.add(retourCourant.getCode_produit());
                }
            }
        }
    }

    public void onTextWatcher(final Editable s){
        if (s.toString().endsWith("\n")) {
            Produit produit = null;
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s.toString().substring(0, s.length() - 1));
            if (gs1Decoupe.size() != 1) {
                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if(produits != null)
                {
                    if (produits.size() == 1) {
                        produit = produits.get(0);
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
                }
            }

            if (modeRafale) {

                if (modeCumule) {
                    if(stringList.indexOf(s.toString().substring(0, s.length()-1)) == -1)
                    {
                        if(produit != null && liste_id_produit.indexOf(produit.getID_produit()) != -1)
                        {
                            for(Integer id_retour_ligne_courant : liste_id_retour_ligne)
                            {
                                Retour_Ligne retour_ligne_temp = Retour_LigneOpenHelper.getRetourLigneByID(db, id_retour_ligne_courant);
                                if(retour_ligne_temp.getCode_produit() == produit.getID_produit())
                                {
                                    retour_ligne_courant = retour_ligne_temp;
                                }
                            }
                            String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                            String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                            String gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
                            String conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                            String date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);

                            if(codeInconnu)
                            {
                                gtin_courant = s.toString().substring(0, s.length() - 1);
                            }
                            PH_Serialisation serialisation_courante = null;

                            if(!codeInconnu)
                            {
                                /***Début de la sérialisation***/
                                if (!serie.contentEquals("") && produit.isSuivi_Serialisation() && serieListe.indexOf(serie) == -1) {
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

                                /***Fin de la sérialisation***/
                            }


                            if(GtinProduitScan.indexOf(gtin_courant) == -1)
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

                            quantiteMaxNumberPicker = (int)retour_ligne_courant.getQte_Demander();
                            int nb_occurence = 1;
                            doitEtreSupprimer = false;
                            index_objet_a_supprimer= -1;
                            for(ObjetReceptionScannee objet_courant : list_result)
                            {
                                index_objet_a_supprimer ++;

                                Map<String, String> gs1Decoupe_courant = OutilsDecodage.decouperGTIN(objet_courant.getGs1_scannee());
                                String lot_courant = gs1Decoupe_courant.get(OutilsDecodage.numeroLot);

                                if(gs1Decoupe_courant.get(OutilsDecodage.numeroSerie).contentEquals(serie) && gs1Decoupe_courant.get(OutilsDecodage.codeGtin).contentEquals(gtin_courant) && lot_courant.contentEquals(lot))
                                {
                                    nb_occurence ++;
                                    doitEtreSupprimer = true;
                                    objetReceptionScanneeCourant = objet_courant;
                                    break;
                                }
                            }

                            for(Integer id_retour_ligne_courant : liste_id_retour_ligne)
                            {
                                Retour_Ligne retour_ligne_courant = Retour_LigneOpenHelper.getRetourLigneByID(db, id_retour_ligne_courant);
                                if(retour_ligne_courant.getCode_produit() == produit.getID_produit())
                                {
                                    quantiteMaxNumberPicker = (int) retour_ligne_courant.getQte_Demander()-(int)retour_ligne_courant.getQte_Retourner();
                                }
                            }

                            quantite_a_afficher = dose;

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

                            objetReceptionScanneeCourant.setGs1_scannee(s.toString().substring(0, s.length() - 1));
                            codeInconnu = false;
                            AjoutDuProduit();
                        }
                        else
                        {
                            String activityName = context.getClass().getSimpleName();
                            if(activityName.contentEquals("BarcodeCaptureActivity"))
                            {
                                ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                            }
                            else if(activityName.contentEquals("ScannerSearchOnlyActivity"))
                            {
                                ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit inconnu");
                            }
                            else
                            {
                                ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit inconnu");
                            }

                            code = "";
                        }
                    }
                    else
                    {
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà scanné");
                        }
                        else if(activityName.contentEquals("ScannerSearchOnlyActivity"))
                        {
                            ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit inconnu");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit déjà scanné");
                        }
                    }
                }
                code = "";
            } else {
                code = s.toString().substring(0, s.length() - 1);
            }
        }
    }

    public boolean AjoutDuProduit()
    {
        if(objetReceptionScanneeCourant.getGs1_scannee().contentEquals(""))
            return false;
        else
        {
            if(doitEtreSupprimer)
            {
                list_result.remove(index_objet_a_supprimer);
            }
/*            ph_reliquat_courant.setQteReliquat_X(ph_reliquat_courant.getQteReliquat_X()-quantite_a_afficher);
            PH_ReliquatOpenHelper.mettreAJourUnPHReliquat(db, ph_reliquat_courant);*/
            retour_ligne_courant.setQte_Retourner(quantite_a_afficher);
            Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retour_ligne_courant);
            objetReceptionScanneeCourant.setQuantiteScannee(quantite_a_afficher);
            Map<String, String> gs1DecoupeAjout = OutilsDecodage.decouperGTIN(objetReceptionScanneeCourant.getGs1_scannee());
            if(!gs1DecoupeAjout.get(OutilsDecodage.numeroSerie).contentEquals(""))
            {
                stringList.add(objetReceptionScanneeCourant.getGs1_scannee());
            }

            String activityName = context.getClass().getSimpleName();
            if(activityName.contentEquals("ScannerSearchOnlyActivity"))
            {
                ((ScannerSearchOnlyActivity) context).afficherSnackBar("Produit ajouté");
            }

            list_result.add(objetReceptionScanneeCourant);
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

    public boolean onTap(String chaine){
        boolean confirmation = true;

        if(!serialisation)
        {
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(chaine);
            if (gs1Decoupe.size() != 0) {
                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if (produits.size() == 1) {
                    Produit produit = produits.get(0);
                    if (doitEtreIdentique) {
                        if (!produit.getDesignation_interne().equals(designation)) {
                            confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code GS1 a été trouvé mais ne correspond pas au produit selectionné (" + designation + "), \n\n Continuez ?", "OuiNon");
                        } else {
                            confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouve :\n" + chaine + "\nLe code GS1 correspond au produit sélectionné (" + designation + "), \n\n Continuez ?", "OuiNon");
                        }
                    } else {
                        confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code GS1 a été trouvé et correspond au produit suivant : \n" + produit.getDesignation_interne() + "\n\nContinuer ?", "OuiNon");
                    }
                } else {
                    confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code GS1 a été trouvé mais aucun produit n'y correspond. \n\nContinuer ?", "OuiNon");
                }
            } else {
                confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code a été trouvé, mais ce n'est pas un code GS1. \n\nContinuer ?", "OuiNon");
            }
        }

        return confirmation;
    }
}
