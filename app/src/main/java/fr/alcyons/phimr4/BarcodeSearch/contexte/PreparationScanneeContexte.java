package fr.alcyons.phimr4.BarcodeSearch.contexte;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phimr4.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.PreparationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.Preparation_LigneOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.SurveillanceReferenceOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.ObjetReceptionScannee;
import fr.alcyons.phimr4.Classes.PH_Preparation;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne;
import fr.alcyons.phimr4.Classes.PH_Preparation_Ligne_Preparation_Adapte;
import fr.alcyons.phimr4.Classes.PH_Serialisation;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Classes.Stock_Lot_Emplacement_Light;
import fr.alcyons.phimr4.Classes.SurveillanceReference;
import fr.alcyons.phimr4.Classes.Utilisateur;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phimr4.OutilsSerialisation.GestionResultatNMVO;
import fr.alcyons.phimr4.OutilsSerialisation.Serialisation;
import fr.alcyons.phimr4.R;

/**
 * Created by olivier on 30/04/2019.
 */

public class PreparationScanneeContexte {
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
    public int uid_preparationLigneCourant;
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
    public int nbMaxQuantite;

    public boolean scanEmplacement;
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

    private List<PH_Preparation_Ligne>liste_preparation_ligne;
    public List<PH_Preparation_Ligne_Preparation_Adapte> liste_preparation_liste_adapte;


    public PreparationScanneeContexte(final Context context, final SQLiteDatabase db, List<String> ListeGTIN, int userId, TextView message, TextView messageFranceMVO, FloatingActionButton boutonSuppression, int preparation_id, List<PH_Preparation_Ligne_Preparation_Adapte> listedejascanne, int nb_produit_scanne){
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

        scanEmplacement = true;
        objetReceptionScanneeCourant = new ObjetReceptionScannee();
        liste_resultat = new ArrayList<>();
        liste_code_scanne = new ArrayList<>();
        serialisation_preparation = false;
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

                    List<ObjetReceptionScannee> stringRetourList = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeString");
                    List<String> listecodeDejaScannee = data.getExtras().getStringArrayList("listecodescannee");
                    if(listecodeDejaScannee != null)
                    {
                        stringList.addAll(listecodeDejaScannee);
                    }
                    liste_resultat = new ArrayList<>();
                    if(stringRetourList != null)
                    {
                        liste_resultat.addAll(stringRetourList);
                    }
                    else
                    {
                        stringRetourList = (List<ObjetReceptionScannee>) data.getExtras().getSerializable("listeObjet");
                        if(stringRetourList != null)
                        {
                            liste_resultat.addAll(stringRetourList);
                        }
                    }
                    break;
            }
        }
    }

    public boolean onTap(String chaine) {
        boolean confirmation = true;

        if(!scanEmplacement)
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

                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Produit non présent");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit non présent");
                        }
                    }

                    if(present && ph_preparation_ligne_courant.getQte_APreparer() == ph_preparation_ligne_courant.getQte_preparer())
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        String activityName = context.getClass().getSimpleName();
                        if(activityName.contentEquals("BarcodeCaptureActivity"))
                        {
                            ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà préparé en intégralité");
                        }
                        else
                        {
                            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit déjà préparé en intégralité");
                        }
                    }
                }
                else
                {
                    String activityName = context.getClass().getSimpleName();
                    if(activityName.contentEquals("BarcodeCaptureActivity"))
                    {
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                    }
                    else
                    {
                        ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit inconnu");
                    }
                }
            }
            else
            {
                designationProduitScanne = "";
                numeroLotProduitScanne = "";
                peremptionProduitScanne = "";
                quantiteAAfficher = 0;
                referenceProduitScanne = "";
                String activityName = context.getClass().getSimpleName();
                if(activityName.contentEquals("BarcodeCaptureActivity"))
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà scanné");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit déjà scanné");
                }
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
            else if(scan.contains("ALCYONS_ESSAI")) {
                Depot_Emplacement depot_emplacements = EmplacementOpenHelper.getEmplacementEssaiAlcyons(db, scan);
                if (depot_emplacements != null) {
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
        ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()+quantiteAAfficher);
        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
        //ph_preparation_ligne_courant.setQte_Demander(ph_preparation_ligne_courant.getQte_Demander()-quantiteAAfficher);
       // PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
        objetReceptionScanneeCourant.setQuantiteScannee(quantiteAAfficher);
        liste_code_scanne.add(objetReceptionScanneeCourant.getGs1_scannee());
        liste_resultat.add(objetReceptionScanneeCourant);
        objetReceptionScanneeCourant = new ObjetReceptionScannee();
        objetReceptionScanneeCourant.setEmplacement_uid(uidEmplacementCourant);
        designationProduitScanne = "";
        numeroLotProduitScanne = "";
        peremptionProduitScanne = "";
        quantiteAAfficher = 0;
        referenceProduitScanne = "";
        serialisation_preparation = false;
        String activityName = context.getClass().getSimpleName();
        if(activityName.contentEquals("BarcodeCaptureActivity"))
        {
            ((BarcodeCaptureActivity) context).afficherSnackBar("Produit préparé");
        }
        else
        {
            ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Produit préparé");
        }
        return true;
    }
}

