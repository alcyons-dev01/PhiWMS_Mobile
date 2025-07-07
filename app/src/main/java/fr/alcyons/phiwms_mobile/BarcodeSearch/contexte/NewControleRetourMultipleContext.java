package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodePreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerPreparationActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.negative.BarcodeCaptureNegativeActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat_Reception_Adapte;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;

public class NewControleRetourMultipleContext  {
    private Context context;
    private SQLiteDatabase db;

    public String code;
    public String bannerTexte;
    public boolean codeInconnu;
    private List<Retour_Ligne> list_retour_ligne;
    public Retour_Ligne retour_ligne_courant;
    private Utilisateur utilisateurConnecte;
    public PH_Reliquat_Reception_Adapte.Lot nouveau_lot;
    public Produit produitCourant;
    public int qte_lot_courant = 0;
    public boolean validation;
    public String lot = "";
    public String serie = "";
    public String gtin_courant ="";
    public String conditionnementString ="";
    public String date_peremption_courant="";
    public int quantiteAAfficher;
    public int quantiteRestante;
    public int conditionnementDistribution;
    int retour_id;
    Retour retour_courant;

    public NewControleRetourMultipleContext(final Context context, final SQLiteDatabase db, Utilisateur utilisateurConnecte, int retour_id){
        this.context = context;
        this.db = db;
        this.utilisateurConnecte = utilisateurConnecte;
        nouveau_lot = null;
        validation = true;
        this.retour_id = retour_id;
        this.retour_courant = RetourOpenHelper.getRetourByID(db, retour_id);
        quantiteAAfficher = 0;
        //quantiteRestante = (int)retour_ligne_courant.getQte_Demander()-(int)retour_ligne_courant.getQte_Retourner();
        list_retour_ligne = new ArrayList<>();

        list_retour_ligne = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retour_courant);
    }

    public void onTextWatcher(final Editable chaine){
        String s = chaine.toString();
        if (s.endsWith("\n"))
        {
            s = s.substring(0, s.length() - 1);
        }

        if (!validation) {
            validation = false;
        }
        else
        {
            validation = false;
        }

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s);

        if (gs1Decoupe.size() != 1)
        {
            List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            if(produits.size() == 0)
                produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtinSansAi));

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
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                }

                validation = true;
                code = "";
            }

            lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
            gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
            conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
            date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        }
        else
        {
            List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.substring(0, s.length()-1));

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
                else if(activityName.contentEquals("ScannerPreparationActivity"))
                {
                    ((ScannerPreparationActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                }
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                }

                validation = true;
                code = "";
            }
        }

        boolean produit_present = false;
        if(produitCourant != null)
        {
            for(Retour_Ligne retour_ligne:list_retour_ligne)
            {
                if(produitCourant.getID_produit() == retour_ligne.getCode_produit())
                {
                    produit_present = true;
                    conditionnementDistribution = (int)produitCourant.getCond_distrib();
                    retour_ligne_courant = retour_ligne;
                }
            }
        }

        if(!produit_present)
        {
            String activityName = context.getClass().getSimpleName();

            if(activityName.contentEquals("BarcodePreparationActivity"))
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Le produit n'est pas présent dans la liste");
            }
            else if(activityName.contentEquals("ScannerPreparationActivity"))
            {
                ((ScannerPreparationActivity) context).afficherSnackBar("Le produit n'est pas présent dans la liste");
            }
            validation = true;
            lot = "";
            serie = "";
            date_peremption_courant = "";
            quantiteAAfficher = 0;
        }
        else
        {
            quantiteRestante = (int)retour_ligne_courant.getQte_Demander()-(int)retour_ligne_courant.getQte_Retourner();
            if(quantiteRestante == 0)
            {
                ((ScannerPreparationActivity)context).afficherSnackBar("Le produit est retourné en intégralité");
                validation = true;
            }
            quantiteAAfficher = quantiteRestante;
        }
    }

    public void onActivityResult(int requestCode, Intent data){
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_SCANNER:
                    lot = data.getExtras().getString("numLot");
                    serie = data.getExtras().getString("numSerie");
                    date_peremption_courant = data.getExtras().getString("datePeremption");
                    quantiteAAfficher = data.getExtras().getInt("qteActuelle");
                    retour_ligne_courant = Retour_LigneOpenHelper.getRetourLigneByID(db, data.getExtras().getInt("retourLigneId"));
                    break;
            }
        }
    }

    public boolean onTap(String s){
        boolean confirmation = true;
        if (s.endsWith("\n"))
        {
            s = s.substring(0, s.length() - 1);
        }

        if (!validation) {
            validation = false;
            return false;
        }
        else
        {
            validation = false;
        }

        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s);

        if (gs1Decoupe.size() != 1)
        {
            List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
            if(produits.size() == 0)
                produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtinSansAi));

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

            lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
            gtin_courant = gs1Decoupe.get(OutilsDecodage.codeGtin);
            conditionnementString = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
            date_peremption_courant = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
        }
        else
        {
            List<Produit> produits  = ProduitOpenHelper.getProduitByCodeInconnu(db, s.substring(0, s.length()-1));

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
                else
                {
                    ((BarcodeCaptureNegativeActivity) context).afficherSnackBar("Le code concerne plusieurs produits");
                }

                validation = true;
                code = "";
                return false;
            }
        }

        boolean produit_present = false;
        if(produitCourant != null)
        {
            for(Retour_Ligne retour_ligne:list_retour_ligne)
            {
                if(produitCourant.getID_produit() == retour_ligne.getCode_produit())
                {
                    produit_present = true;
                    conditionnementDistribution = (int)produitCourant.getCond_distrib();
                    retour_ligne_courant = retour_ligne;
                }
            }
        }

        if(!produit_present)
        {
            ((BarcodePreparationActivity)context).afficherSnackBar("Le produit n'est pas présent dans la liste");
            validation = true;
            return false;
        }
        else
        {
            quantiteRestante = (int)retour_ligne_courant.getQte_Demander()-(int)retour_ligne_courant.getQte_Retourner();
            if(quantiteRestante == 0)
            {
                ((BarcodePreparationActivity)context).afficherSnackBar("Le produit est retourné en intégralité");
                validation = true;
                return false;
            }
            quantiteAAfficher = quantiteRestante;

        }

        return confirmation;
    }
}