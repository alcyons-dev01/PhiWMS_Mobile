package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.text.Editable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by jessica on 07/06/2018.
 */

public class ProduitContexte {

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

    public List<String> stringList;

    public Bitmap actualPicture;

    public ProduitContexte(final Context context, final SQLiteDatabase db, final boolean modeRafale, final boolean modePhoto, final boolean modeCumule, final boolean doitEtreIdentique, final String designation, final boolean serialisation){
        this.context = context;
        this.db = db;
        this.modeRafale = modeRafale;
        this.modePhoto = modePhoto;
        this.modeCumule = modeCumule;
        this.doitEtreIdentique = doitEtreIdentique;
        this.designation = designation;
        this.serialisation = serialisation;

        code = "";
        bannerTexte = "Scanner un datamatrix d'un produit";
        scannerContexteProduit = String.valueOf(R.string.scannerContexteProduit);

        stringList = new ArrayList<>();
    }

    public void onTextWatcher(final Editable s){
        if (s.toString().endsWith("\n")) {
            Produit produit = null;
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(s.toString().substring(0, s.length() - 1));
            if (gs1Decoupe.size() != 1) {
                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if (produits.size() == 1) {
                    produit = produits.get(0);
                }
            }
            else
            {
                List<Produit> produits = ProduitOpenHelper.getProduitByCodeInconnu(db, s.toString().substring(0, s.length() - 1));
                if (produits.size() == 1) {
                    produit = produits.get(0);
                }
            }
            if (modePhoto && actualPicture != null) {
                if (!stringList.contains(s.toString().substring(0, s.length() - 1))) {
                    final ProgressDialog progressDialog = ProgressDialog.show(context, "Veuillez patienter", "Enregistrement de la photo en cours");
                    Thread mThread = new Thread() {
                        @Override
                        public void run() {
                            OutilsGestionPhotos.saveExternalStorageImageJPG(context, actualPicture, s.toString().substring(0, s.length() - 1));
                            progressDialog.dismiss();
                        }
                    };
                    mThread.start();
                }
            }
            if (modeRafale) {

                if (modeCumule) {
                    if(stringList == null)
                    {
                        stringList = new ArrayList<>();
                    }
                    stringList.add(s.toString().substring(0, s.length() - 1));
                    String toast = "Produit";
                    if (produit != null) {
                        toast = produit.getDesignation_ext();
                    }
                    int nbOccurence = 0;

                    for (String string : stringList) {
                        if (string.equals(s.toString().substring(0, s.length() - 1))) {
                            nbOccurence++;
                        }
                    }

                    Toast.makeText(context, toast + " ajouté " + String.valueOf(nbOccurence) + " fois.", Toast.LENGTH_SHORT).show();
                }
                code = "";
            } else {
                code = s.toString().substring(0, s.length() - 1);
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

                    if(stringRetourList != null)
                    {
                        for (String code : stringRetourList) {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(code);
                            if (gs1Decoupe.size() != 1) {
                                stringList.add(code);
                            }
                        }
                    }

                    break;
            }
        }
    }

    public boolean onTap(String chaine){
        boolean confirmation = true;

/*
        if(!serialisation)
        {
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(chaine);
            if (gs1Decoupe.size() != 1) {
                List<Produit> produits = ProduitOpenHelper.getProduitsParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                if (produits.size() == 1) {
                    Produit produit = produits.get(0);
                    if (doitEtreIdentique) {
                        if (!produit.getDesignation_interne().equals(designation)) {
                            confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouvé :\n" + chaine + "\nUn code GS1 a été trouvé mais ne correspond pas au produit selectionné (" + designation + "), \n\n Continuez ?", "OuiNon");
                        } else {
                            //confirmation = Alerte.afficherAlerte(context, "Attention", "Code trouve :\n" + chaine + "\nLe code GS1 correspond au produit sélectionné (" + designation + "), \n\n Continuez ?", "OuiNon");
                            confirmation = true;
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
*/


        return confirmation;
    }
}
