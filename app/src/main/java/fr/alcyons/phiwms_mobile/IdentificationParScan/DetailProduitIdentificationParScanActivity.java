package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.GS1Parser;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceIdentificationParScanActivity;

public class DetailProduitIdentificationParScanActivity extends ServiceActivity {

    Produit produitSelectionne;

    String codeComplet;
    String messageAlerte;
    String ancienGTIN;
    String ancienCodeInconnu;
    Boolean estCodeGS1;
    ImageView boutonValidation;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable lectureTerminee = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produit_identification_scan);

        //gestion du package manager
        messageAlerte = "";
        codeComplet = "";
        estCodeGS1 = false;
        // Récupération des variables globales et du produit selectionné
        produitSelectionne = ProduitOpenHelper.getProduitByID(db, Objects.requireNonNull(intent.getExtras()).getInt("produitSelectionneID"));
        ancienGTIN = produitSelectionne.getGTIN();
        ancienCodeInconnu = produitSelectionne.getCodeInconnue();
        if (intent.getExtras().getString("codeGS1") != null) {
            codeComplet = intent.getExtras().getString("codeGS1");
            estCodeGS1 = true;
        } else if(intent.getExtras().getString("codeInconnue") != null){
            codeComplet = intent.getExtras().getString("codeInconnue");
            messageAlerte = "Code scanné inconnue";
            estCodeGS1 = false;
        }

        if(!codeComplet.contentEquals(""))
        {
            if(produitSelectionne.getGTIN().contentEquals("01"+codeComplet) || produitSelectionne.getGTIN().contentEquals(codeComplet) || produitSelectionne.getCodeInconnue().contentEquals(codeComplet))
            {
                ((TextView) findViewById(R.id.codeGS1)).setText(codeComplet);
                ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.GONE);
                if(estCodeGS1)
                {
                    ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Code GS1)");
                }
                else
                {
                    ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Type inconnu)");
                }
            }
            else
            {
                if(estCodeGS1)
                {
                    produitSelectionne.setGTIN(codeComplet);
                    ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Code GS1)");
                }
                else
                {
                    produitSelectionne.setCodeInconnue(codeComplet);
                    ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Type inconnu)");
                }
                ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.VISIBLE);
            }
        }
        else if(produitSelectionne.getGTIN().contentEquals("") && produitSelectionne.getCodeInconnue().contentEquals(""))
        {
            ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.GONE);
            ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
            gestionEditText();
        }
        else
        {
            ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.VISIBLE);
        }

        //récupération photo
        String identification = produitSelectionne.getGTIN();
        if(identification.contentEquals(""))
        {
            identification = produitSelectionne.getCodeInconnue();
            if(!identification.contentEquals(""))
                estCodeGS1 = false;
        }
        else
            estCodeGS1 = true;

        if(estCodeGS1)
        {
            ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Code GS1)");
        }
        else
        {
            ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Type inconnu)");
        }

        // Affichage des valeurs
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne().trim());
        ((TextView) findViewById(R.id.codeGS1)).setText(identification);
        ((TextView) findViewById(R.id.nomFournisseur)).setText(produitSelectionne.getFournisseur().trim());
        ((TextView) findViewById(R.id.referenceFournisseur)).setText(produitSelectionne.getRef_fourni().trim());
        ((TextView) findViewById(R.id.categorie)).setText(produitSelectionne.getCategorie().trim());
        ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.VISIBLE);
        gestionClickSuppression();

        if(produitSelectionne.getClasse_numero() == 1)
        {
            ((LinearLayout) findViewById(R.id.zoneClassATC)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.zoneDCI)).setVisibility(View.VISIBLE);
            String classeATC = "";
            if(produitSelectionne.getClasseATC() != null)
                classeATC = produitSelectionne.getClasseATC();
            ((TextView) findViewById(R.id.classATC)).setText(classeATC);
            ((TextView) findViewById(R.id.dci)).setText(produitSelectionne.getDCI());
        }

        ((ImageView) findViewById(R.id.boutonValidation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ancienGTIN.contentEquals(produitSelectionne.getGTIN()) && ancienCodeInconnu.contentEquals(produitSelectionne.getCodeInconnue()))
                {
                    retourService(DetailProduitIdentificationParScanActivity.this.getBundle());
                }
                else
                {
                    onMenuSaveClick();
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(ancienGTIN.contentEquals(produitSelectionne.getGTIN()) && ancienCodeInconnu.contentEquals(produitSelectionne.getCodeInconnue())) {
                    retourService(DetailProduitIdentificationParScanActivity.this.getBundle());
                }
                else
                {
                    onMenuSaveClick();
                }
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void onMenuSaveClick()
    {
        Alerte.afficherAlerteConfirmation(DetailProduitIdentificationParScanActivity.this, getLayoutInflater(), getBundle(), "Souhaitez-vous enregistrer les modifications ?", false, true, DetailProduitIdentificationParScanActivity.this);
    }

    @Override
    public void confirmationService() {
        long rowId = ProduitOpenHelper.mettreAJourProduit(db, produitSelectionne);
        if (rowId != -1) {
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
            Random randomaction = new Random();
            int actionId = randomaction.nextInt();
            if(actionId > 0)
                actionId= actionId*-1;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateAction =new Date();
            String date_string = parseFormat.format(dateAction);
            ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", produitSelectionne.getID_produit(), "", "Identification Par Scan");
            ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "PH_Produit", produitSelectionne.getID_produit(), produitSelectionne.getGTIN(), 0, 0, produitSelectionne.getDesignation_interne());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateur_LigneOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR_LIGNE, actionUtilisateur_ligne.getPhiMR4UUID(), actionUtilisateur_ligne.getId(), DBOpenHelper.ActionsEAS.AJOUT);
            ElementASynchroniserOpenHelper.toutSynchroniser(DetailProduitIdentificationParScanActivity.this, db, utilisateurConnecte, false);
        }
        retourService(getBundle());
    }
    @Override
    public void retourService(final Bundle bundle)
    {
        Intent detailPreparationIntent = null;
        detailPreparationIntent = new Intent(DetailProduitIdentificationParScanActivity.this, ServiceIdentificationParScanActivity.class);

        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailProduitIdentificationParScanActivity.this.startActivity(detailPreparationIntent);
        DetailProduitIdentificationParScanActivity.this.finish();
    }

    private void gestionEditText()
    {
        ((EditText) findViewById(R.id.editcodescanne)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) return;

                final String texteBrut = s.toString();

                if (!texteBrut.endsWith("\n") && !texteBrut.endsWith("\r") && !texteBrut.endsWith("\t")) {
                    return;
                }

                if(!texteBrut.contentEquals(""))
                {
                    // Annule tout déclenchement précédent
                    if (lectureTerminee != null) {
                        handler.removeCallbacks(lectureTerminee);
                    }

                    // Planifie un déclenchement différé
                    lectureTerminee = new Runnable() {
                        @Override
                        public void run() {
                            String chaineRetourner = "";
                            GS1Parser.GS1Result result = GS1Parser.parseGS1Code(texteBrut);

                            String code = result.productCode;
                            boolean gtin = false;
                            if(!code.contentEquals(""))
                            {
                                chaineRetourner = code.trim();
                                gtin = true;
                            }
                            else
                            {
                                String texteNettoye = texteBrut.replaceAll("\u0000", "");
                                chaineRetourner = texteNettoye.trim();
                                gtin = false;
                            }

                            List<Produit> listeProduitIdentifier = ProduitOpenHelper.getProduitsByIdentification(db, chaineRetourner);
                            if(listeProduitIdentifier.isEmpty())
                            {
                                if(gtin)
                                {
                                    produitSelectionne.setGTIN(chaineRetourner);
                                }
                                else
                                {
                                    produitSelectionne.setCodeInconnue(chaineRetourner);
                                }

                                ((TextView) findViewById(R.id.codeGS1)).setText(chaineRetourner);
                                ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.GONE);
                                ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.VISIBLE);
                                ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.VISIBLE);
                                gestionClickSuppression();
                            }
                            else
                            {
                                produitSelectionne.setGTIN("");
                                produitSelectionne.setCodeInconnue("");
                                ((EditText) findViewById(R.id.editcodescanne)).setText("");
                                ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
                                Alerte.afficherAlerteInformation(DetailProduitIdentificationParScanActivity.this, getLayoutInflater(),"Erreur", "Code GTIN déjà utilisé pour une autre référence", false, false);
                            }
                        }
                    };

                    // Lance le traitement 200 ms après la dernière frappe
                    handler.postDelayed(lectureTerminee, 200);
                }
            }
        });
    }

    private void gestionClickSuppression()
    {
        ((ImageView) findViewById(R.id.suppressioncodescanne)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produitSelectionne.setGTIN("");
                produitSelectionne.setCodeInconnue("");
                ((TextView) findViewById(R.id.codeGS1)).setText("");
                ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.VISIBLE);
                ((EditText) findViewById(R.id.editcodescanne)).setText("");
                ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
                ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
                gestionEditText();
            }
        });
    }
}
