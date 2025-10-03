package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerProduitActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
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
            if(produitSelectionne.getGTIN().contentEquals(codeComplet) || produitSelectionne.getCodeInconnue().contentEquals(codeComplet))
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
                ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.VISIBLE);

                ((ImageView) findViewById(R.id.suppressioncodescanne)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        produitSelectionne.setGTIN("");
                        produitSelectionne.setCodeInconnue("");
                        ((TextView) findViewById(R.id.codeGS1)).setText("");
                        ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.GONE);
                        ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.GONE);
                        ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.VISIBLE);
                        ((EditText) findViewById(R.id.editcodescanne)).setText("");
                        ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
                        ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.GONE);
                    }
                });
            }
        }
        else if(produitSelectionne.getGTIN().contentEquals("") && produitSelectionne.getCodeInconnue().contentEquals(""))
        {
            ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.GONE);
            ((EditText) findViewById(R.id.editcodescanne)).requestFocus();

            ((EditText) findViewById(R.id.editcodescanne)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String codeIdentification = "";

                    if (s.toString().endsWith("\n")) {
                        String codeRecu = s.toString().trim();
                        if(codeRecu.startsWith("PHITAGTIN:"))
                        {
                            String[] tabCode = codeRecu.toString().split(":");
                            if(tabCode.length == 2)
                            {
                                estCodeGS1 = true;
                                codeIdentification = tabCode[1];
                            }
                        }
                        else
                        {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeRecu);
                            if (gs1Decoupe.size() != 1) {
                                estCodeGS1 = true;
                                codeIdentification = gs1Decoupe.get("codeGtin");
                            } else {
                                estCodeGS1 = false;
                                codeIdentification = codeRecu;
                            }
                        }
                    }
                    boolean modifiable = true;

                    if(!codeIdentification.contentEquals(""))
                    {
                        if(estCodeGS1)
                        {
                            List<Produit> listeProduitRecherche = ProduitOpenHelper.getProduitsParGTIN(db, codeIdentification);
                            for(Produit produitCourant : listeProduitRecherche)
                            {
                                if(produitCourant.getID_produit() != produitSelectionne.getID_produit())
                                {
                                    modifiable = false;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            List<Produit> listeProduitRecherche = ProduitOpenHelper.getProduitsParCodeInconnue(db, codeIdentification);
                            for(Produit produitCourant : listeProduitRecherche)
                            {
                                if(produitCourant.getID_produit() != produitSelectionne.getID_produit())
                                {
                                    modifiable = false;
                                    break;
                                }
                            }
                        }

                        if(modifiable)
                        {
                            if(estCodeGS1)
                            {
                                produitSelectionne.setGTIN(codeIdentification);
                                ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Code GS1)");
                            }
                            else
                            {
                                produitSelectionne.setCodeInconnue(codeIdentification);
                                ((TextView) findViewById(R.id.referenceIdentifie)).setText("RÉFÉRENCE IDENTIFIÉE (Type inconnu)");
                            }
                            ((TextView) findViewById(R.id.codeGS1)).setText(codeIdentification);
                            ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
                            ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.GONE);
                            ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.VISIBLE);
                            ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.VISIBLE);

                            ((ImageView) findViewById(R.id.suppressioncodescanne)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    produitSelectionne.setGTIN("");
                                    produitSelectionne.setCodeInconnue("");
                                    ((TextView) findViewById(R.id.codeGS1)).setText("");
                                    ((LinearLayout) findViewById(R.id.layoutcodeGS1)).setVisibility(View.GONE);
                                    ((ImageView) findViewById(R.id.suppressioncodescanne)).setVisibility(View.GONE);
                                    ((LinearLayout) findViewById(R.id.layouteditcode)).setVisibility(View.VISIBLE);
                                    ((EditText) findViewById(R.id.editcodescanne)).setText("");
                                    ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
                                    ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.GONE);
                                }
                            });
                        }
                        else
                        {
                            produitSelectionne.setGTIN("");
                            produitSelectionne.setCodeInconnue("");
                            ((EditText) findViewById(R.id.editcodescanne)).setText("");
                            ((EditText) findViewById(R.id.editcodescanne)).requestFocus();
                            Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Erreur", "Code GTIN déjà utilisé pour une autre référence", "alerte");
                        }
                    }

                    ((EditText) findViewById(R.id.editcodescanne)).setShowSoftInputOnFocus(false);
                }
            });
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
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void onMenuSaveClick()
    {
        afficherAlerteConfirmationRetour(DetailProduitIdentificationParScanActivity.this, LayoutInflater.from(DetailProduitIdentificationParScanActivity.this), DetailProduitIdentificationParScanActivity.super.getBundle());
    }

    public void afficherAlerteConfirmationRetour(Context context, LayoutInflater inflater, final Bundle bundle) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_mail, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageTextView = (TextView) layout.findViewById(R.id.messageFin);
        messageTextView.setText("Souhaitez vous enregistrer les modifications ?");
        builder.setView(layout);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(v -> {
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
            alertDialog.dismiss();
            retourService(bundle);
        });

        buttonAnnuler.setOnClickListener(v -> {
            alertDialog.dismiss();
            retourService(bundle);
        });
    }

    private void retourService(final Bundle bundle)
    {
        Intent detailPreparationIntent = null;
        detailPreparationIntent = new Intent(DetailProduitIdentificationParScanActivity.this, ServiceIdentificationParScanActivity.class);

        Bundle detailPreparationBundle = super.getBundle();
        detailPreparationIntent.putExtras(detailPreparationBundle);
        DetailProduitIdentificationParScanActivity.this.startActivity(detailPreparationIntent);
        DetailProduitIdentificationParScanActivity.this.finish();
    }

    @Override
    public void onBackPressed()
    {
        if(ancienGTIN.contentEquals(produitSelectionne.getGTIN()) && ancienCodeInconnu.contentEquals(produitSelectionne.getCodeInconnue())) {
            retourService(DetailProduitIdentificationParScanActivity.this.getBundle());
        }
        else
        {
            onMenuSaveClick();
        }
    }
}
