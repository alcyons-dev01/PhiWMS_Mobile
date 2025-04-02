package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.Classes.ObjetReceptionScannee;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;

import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.GestionResultatNMVO;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;

public class PreparationScanneeScanProduitContext {

    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public String scannerContexteProduit;
    private String GTIN_Courant;
    private int userId;
    private TextView message;
    private TextView messageFranceMVO;
    private TextView compteurReliquat;
    private FloatingActionButton boutonSuppression;

    public List<String> stringList;
    private List<String> serieList;
    private List<String> lotListe;
    public Map<String, String> tableau_renvoyer;
    private String messageTexte;
    private String messageTexteFranceMVO;
    private int messageColor;
    public int qteReliquat;
    private ToneGenerator toneGen1;
    private Utilisateur utilisateur;
    private PH_Reliquat reliquat_courant;

    private Serialisation serialisation;

    public boolean scanEmplacement;
    public List<String> liste_code_scanne;
    public String numeroLotProduitScanne;
    public String peremptionProduitScanne;
    public List<PH_Preparation_Ligne> liste_preparation_ligne;
    public String designationProduitScanne;
    public String referenceProduitScanne;
    public PH_Preparation_Ligne ph_preparation_ligne_courant;
    public ObjetReceptionScannee objetReceptionScanneeCourant;
    public boolean serialisation_preparation;
    public int quantiteAAfficher;
    public String emplacementProduitCourant;
    public String zoneProduitCourant;
    public int quantite_max_number_picker;
    public int uidEmplacementCourant;
    public List<ObjetReceptionScannee> liste_resultat;

    public PreparationScanneeScanProduitContext(final Context context, final SQLiteDatabase db, String GTIN_Courant, int userId, TextView message, TextView messageFranceMVO, TextView compteurReliquat, int qteReliquat, FloatingActionButton boutonSuppression, int preparationLigneID){
        this.context = context;
        this.db = db;
        this.GTIN_Courant = GTIN_Courant;
        this.userId = userId;
        this.message = message;
        this.messageFranceMVO = messageFranceMVO;
        this.compteurReliquat = compteurReliquat;
        this.qteReliquat = qteReliquat;
        this.boutonSuppression = boutonSuppression;
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        messageFranceMVO.setVisibility(View.VISIBLE);
        compteurReliquat.setVisibility(View.VISIBLE);

        code = "";
        bannerTexte = "Scanner un datamatrix d'un produit";
        scannerContexteProduit = String.valueOf(R.string.scannerContexteFranceMVO);

        stringList = new ArrayList<>();
        serieList = new ArrayList<>();
        tableau_renvoyer = new HashMap<String, String>();
        lotListe = new ArrayList<>();
        utilisateur = UtilisateurOpenHelper.getUtilisateurByID(db, userId);
        serialisation = new Serialisation(context, db, utilisateur);
        compteurReliquat.setText(String.valueOf(qteReliquat)+" Reliquat(s) restant");
        ph_preparation_ligne_courant = PH_Preparation_LigneOpenHelper.getPH_Preparation_LigneByID(db, preparationLigneID);

        scanEmplacement = true;
        liste_code_scanne = new ArrayList<>();
        numeroLotProduitScanne = "";
        peremptionProduitScanne = "";
        liste_preparation_ligne = new ArrayList<>();
        liste_resultat = new ArrayList<>();
        designationProduitScanne = "";
        referenceProduitScanne = "";
        serialisation_preparation = false;
        quantiteAAfficher = 0;
        uidEmplacementCourant = 0;
        emplacementProduitCourant = "";
        zoneProduitCourant = "";
        objetReceptionScanneeCourant = new ObjetReceptionScannee();
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
        if(!scanEmplacement)
        {
            if(liste_code_scanne.indexOf(chaine) == -1)
            {
                Map<String, String> gs1DecoupeCourant  = OutilsDecodage.decouperGTIN(chaine);
                Produit produit_courant = null;
                if(gs1DecoupeCourant.size() != 0)
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
                    if(produit_courant.getGTIN().contentEquals(GTIN_Courant))
                    {
                        present = true;
                    }

                    if(!present)
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit différent du produit sélectionné");
                    }
                    else if(ph_preparation_ligne_courant.getQte_APreparer() == ph_preparation_ligne_courant.getQte_preparer())
                    {
                        designationProduitScanne = "";
                        numeroLotProduitScanne = "";
                        peremptionProduitScanne = "";
                        quantiteAAfficher = 0;
                        referenceProduitScanne = "";
                        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà préparé en intégralité");
                    }
                    else
                    {
                        designationProduitScanne = produit_courant.getDesignation_interne();
                        referenceProduitScanne = produit_courant.getRef_fourni();
                        quantiteAAfficher = produit_courant.getCond_achat();
                        quantite_max_number_picker = ph_preparation_ligne_courant.getQte_APreparer();
                        objetReceptionScanneeCourant.setGs1_scannee(chaine);
                    }
                }
                else
                {
                    ((BarcodeCaptureActivity) context).afficherSnackBar("Produit inconnu");
                }
            }
            else
            {
                ((BarcodeCaptureActivity) context).afficherSnackBar("Produit déjà scanné");
            }
        }
        else
        {
            String scan = chaine;
            if (scan.endsWith("\n")) {
                scan = scan.substring(0, scan.length() - 1);
            }

            if (scan.startsWith("PHITAGPLACE")) {
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
        ph_preparation_ligne_courant.setQte_preparer(ph_preparation_ligne_courant.getQte_preparer()+quantiteAAfficher);
        PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ph_preparation_ligne_courant);
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
        ((BarcodeCaptureActivity) context).afficherSnackBar("Produit préparé");
        return true;
    }
}
