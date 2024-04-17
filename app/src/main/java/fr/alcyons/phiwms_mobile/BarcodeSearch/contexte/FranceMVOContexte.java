package fr.alcyons.phiwms_mobile.BarcodeSearch.contexte;


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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_ReliquatOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.PH_Reliquat;
import fr.alcyons.phiwms_mobile.Classes.PH_Serialisation;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.SurveillanceReference;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.GestionCodeErreurNMVO;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.EnvoyerMailSurveillance;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.GestionResultatNMVO;
import fr.alcyons.phiwms_mobile.OutilsSerialisation.Serialisation;
import fr.alcyons.phiwms_mobile.R;

/**
 * Created by olivier on 05/03/2019.
 */

public class FranceMVOContexte  {

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

    public FranceMVOContexte(final Context context, final SQLiteDatabase db, String GTIN_Courant, int userId, TextView message, TextView messageFranceMVO, TextView compteurReliquat, int qteReliquat, FloatingActionButton boutonSuppression, int reliquat_uid){
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
        reliquat_courant = PH_ReliquatOpenHelper.getPH_ReliquatById(db, reliquat_uid);
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
        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(chaine);
        if (gs1Decoupe.size() > 1 && !gs1Decoupe.get(OutilsDecodage.numeroLot).contentEquals("")) {
            String serie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
            String lot = gs1Decoupe.get(OutilsDecodage.numeroLot);
            String date_peremtion = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
            date_peremtion = date_peremtion.substring(2);
            date_peremtion = date_peremtion.replace("-", "");
            String gtin = gs1Decoupe.get(OutilsDecodage.codeGtin);
            String conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
            gtin = gtin.substring(2);
            if(gtin.length() < GTIN_Courant.length())
            {
                gtin = "01"+gtin;
            }
            else if(gtin.length() > GTIN_Courant.length())
            {
                GTIN_Courant = "01"+GTIN_Courant;
            }

            if(gtin.contentEquals(GTIN_Courant))
            {
                if (serieList.indexOf(serie) == -1 || serie.contentEquals(""))
                {
                    String chaine_tableau = "";
                    String resultat = "";
                    boolean differe = false;
                    if(!serie.contentEquals(""))
                    {
                        if (!OutilsGestionConnexionReseau.isServerAccessible(context))
                            differe = true;

                        if(conditionnement.contentEquals(""))
                        {
                            Produit produit = ProduitOpenHelper.getUnProduitParGTIN(db, gtin);
                            if(produit == null)
                            {
                                produit = ProduitOpenHelper.getUnProduitParGTIN(db, "01"+gtin);
                            }

                            conditionnement = String.valueOf(produit.getCond_achat());
                        }
                        String ancien_serie = serie;
                        String last_char = serie.substring(serie.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            serie = serie.substring(0, serie.length()-1);
                        }

                        last_char = lot.substring(lot.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            lot = lot.substring(0, lot.length()-1);
                        }

                        last_char = conditionnement.substring(conditionnement.length()-1);
                        if(last_char.contentEquals("@"))
                        {
                            conditionnement = conditionnement.substring(0, conditionnement.length()-1);
                        }
                        long ph_serialisation_uid = 0;
                        if(reliquat_courant != null)
                        {
                            ph_serialisation_uid = serialisation.Serialisation_Verifier(userId, false, differe, gtin, "GTIN", lot, date_peremtion, serie, "CDE", reliquat_courant.getCommandeNumero(), "", "");
                        }
                        else
                        {
                            ph_serialisation_uid = serialisation.Serialisation_Verifier(userId, false, differe, gtin, "GTIN", lot, date_peremtion, serie, "Vérification", gtin, "", "");
                        }

                        PH_Serialisation serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int)ph_serialisation_uid);
                        if(serialisation == null)
                        {
                            serialisation = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int)ph_serialisation_uid);
                        }


                        if(gtin.length() > 14)
                        {
                            chaine_tableau = gtin+"21"+serie+"@17"+date_peremtion+"10"+lot+"@30"+conditionnement;
                        }
                        else
                        {
                            chaine_tableau = "01"+gtin+"21"+serie+"@17"+date_peremtion+"10"+lot+"@30"+conditionnement;
                        }

                        resultat = serialisation.getResultat();
                        if(resultat.contentEquals("INACTIVE") || resultat.contentEquals("UNKNOWN"))
                        {
                            Alerte.afficherAlerte(context, "Attention", "Le produit scanné est détruit ou inactif", "alerte");
                            messageTexteFranceMVO = GestionResultatNMVO.getResultat(serialisation.getRaison());
                            messageColor = Color.RED;
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

                            int produit_id = serialisation.getProduitUID();
                            int serialisationID = serialisation.get_UID();
                            String motif = GestionCodeErreurNMVO.getMessage(code);
                            String actionAMener = "";
                            String statut = "NON LU";
                            String traitePar = utilisateur.getIdentifiant();
                            String traiteDate = surveillanceDate;
                            String traiteHeure = surveillanceHeure;
                            String produitLot = serialisation.getNumeroLot();
                            String produitDatePéremption = serialisation.getDatePeremptionAAMMJJ();
                            String produitNumeroSerie = serialisation.getNumeroSerie();
                        }
                        else
                        {
                            messageTexteFranceMVO = "";
                        }
                        serieList.add(ancien_serie);
                    }
                    else
                    {
                        if(lotListe.indexOf(lot) == -1)
                        {
                            //ouvrir alerte saisie quantité
                            String quantite_saisie = Alerte.afficherAlerteEditText(context, "Information", "Quantité du numéro de lot :"+lot);
                            serieList.add(serie);
                            lotListe.add(lot);
                            String last_char = lot.substring(lot.length()-1);
                            if(last_char.contentEquals("@"))
                            {
                                lot = lot.substring(0, lot.length()-1);
                            }

                            chaine_tableau = "01"+gtin+"17"+date_peremtion+"10"+lot+"@30"+quantite_saisie;
                            resultat = "None";
                        }
                        else
                        {
                            messageTexte = "Déjà scanné";
                            messageColor = Color.RED;
                        }
                        messageTexteFranceMVO = "";
                    }

                    if(!chaine_tableau.contentEquals(""))
                    {
                        messageTexte = "Produit ajouté";
                        messageColor = Color.GREEN;
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                        tableau_renvoyer.put(chaine_tableau, resultat);
                        qteReliquat --;
                        stringList.add(chaine_tableau);
                    }

                }
                else
                {
                    messageTexte = "Déjà scanné";
                    messageColor = Color.RED;
                }
            }
            else
            {
                messageTexte = "Mauvais produit scanné";
                messageColor = Color.RED;
            }
        }
        else
        {
            messageTexte = "Barcode non interprété";
            messageColor = Color.RED;
        }
        message.setVisibility(View.VISIBLE);
        message.setText(messageTexte);
        compteurReliquat.setText(String.valueOf(qteReliquat)+" Reliquat(s) restant");
        message.setBackgroundColor(messageColor);
        messageFranceMVO.setText(messageTexteFranceMVO);

        if(qteReliquat == 0)
        {
            boutonSuppression.performClick();
        }

        return confirmation;
    }
}
