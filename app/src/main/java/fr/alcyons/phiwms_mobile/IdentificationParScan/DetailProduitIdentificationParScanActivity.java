package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailProduitIdentificationParScanActivity extends ServiceActivity {

    Produit produitSelectionne;

    String codeComplet;
    String messageAlerte;
    Boolean estCodeGS1;
    boolean confirmation;

    PackageManager pm;
    public ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produit_identification_scan);

        //gestion du package manager
        pm = DetailProduitIdentificationParScanActivity.this.getPackageManager();
        photo = (ImageView) findViewById(R.id.photo);

        messageAlerte = "";
        // Récupération des variables globales et du produit selectionné
        produitSelectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitSelectionneID"));
        if (intent.getExtras().getString("codeGS1") != null) {
            codeComplet = intent.getExtras().getString("codeGS1");
            estCodeGS1 = true;
        } else {
            codeComplet = intent.getExtras().getString("codeInconnue");
            messageAlerte = "Code scanné inconnue";
            estCodeGS1 = false;
        }

        //récupération photo
        Depot depot = DepotOpenHelper.getDepotPUI(db);
        MedicalObjective medicalObjective = new MedicalObjective(DetailProduitIdentificationParScanActivity.this, utilisateurConnecte, depot, depot, produitSelectionne, true);
        medicalObjective.getPictureImage("DetailProduitIdentificationParScanActivity");


        // Gestion du bouton de modification de code
        findViewById(R.id.boutonEditCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    Intent newIntent = new Intent(DetailProduitIdentificationParScanActivity.this, ScannerSearchOnlyActivity.class);
                    Bundle bundle = DetailProduitIdentificationParScanActivity.super.getBundle();
                    bundle.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(bundle);
                    DetailProduitIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    {
                        Intent newIntent = new Intent(DetailProduitIdentificationParScanActivity.this, BarcodeCaptureActivity.class);
                        Bundle bundle = DetailProduitIdentificationParScanActivity.super.getBundle();
                        bundle.putBoolean("isBoutonSuppressionExistant", true);
                        newIntent.putExtras(bundle);
                        DetailProduitIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                    }
                    else
                    {
                        Intent newIntent = new Intent(DetailProduitIdentificationParScanActivity.this, ScannerSearchOnlyActivity.class);
                        Bundle bundle = DetailProduitIdentificationParScanActivity.super.getBundle();
                        bundle.putBoolean("isBoutonSuppressionExistant", true);
                        newIntent.putExtras(bundle);
                        DetailProduitIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                    }
                }
            }
        });

        // Gestion du bouton de suppression de code
        findViewById(R.id.boutonSuppressionCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Attention", "Etes vous sûr de vouloir supprimer le code de ce produit ?", "OuiNon");
                if (confirmation) {
                    if (estCodeGS1) {
                        produitSelectionne.setGTIN("");
                    } else {
                        produitSelectionne.setCodeInconnue("");
                    }

                    long rowId = gestionnaireProduit.mettreAJourProduit(db, produitSelectionne);
                    if (rowId != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                    }
                    codeComplet = "";
                    messageAlerte = "";
                    onResume();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        String conditionnement = "";
        String numLot = "";
        String dateP = "";
        String numeroSerie = "";

        // On gère l'affichage en fonction de si codeComplet a déjà une valeur ou non
        if (estCodeGS1) {
            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
            if (gs1Decoupe.size() != 1) {
                dateP = gs1Decoupe.get(OutilsDecodage.dateDePeremption);
                conditionnement = gs1Decoupe.get(OutilsDecodage.conditionnementProduit);
                numLot = gs1Decoupe.get(OutilsDecodage.numeroLot);
                numeroSerie = gs1Decoupe.get(OutilsDecodage.numeroSerie);
                if (!produitSelectionne.getGTIN().equals(gs1Decoupe.get(OutilsDecodage.codeGtin))) {
                    produitSelectionne.setGTIN(gs1Decoupe.get(OutilsDecodage.codeGtin).trim());

                    long rowId = gestionnaireProduit.mettreAJourProduit(db, produitSelectionne);
                    if (rowId != -1) {
                        gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                    }
                }
            }
        } else {
            ((TextView) findViewById(R.id.warningPeremption)).setText(messageAlerte.trim());

            boolean produitAModifier = false;
            if (produitSelectionne.getCodeInconnue() == null) {
                produitAModifier = true;
            } else if (!produitSelectionne.getCodeInconnue().contentEquals(codeComplet)) {
                produitAModifier = true;
            }

            if (produitAModifier) {
                produitSelectionne.setCodeInconnue(codeComplet.trim());

                long rowId = gestionnaireProduit.mettreAJourProduit(db, produitSelectionne);
                if (rowId != -1) {
                    gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                }
            }
        }

        // Affichage des valeurs
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne().trim());
        ((TextView) findViewById(R.id.codeGS1)).setText(codeComplet.trim());
        ((TextView) findViewById(R.id.gtin)).setText(produitSelectionne.getGTIN().trim());
        ((TextView) findViewById(R.id.nomFournisseur)).setText(produitSelectionne.getFournisseur().trim());
        ((TextView) findViewById(R.id.referenceFournisseur)).setText(String.valueOf(produitSelectionne.getRef_fourni().trim()));
        ((TextView) findViewById(R.id.categorie)).setText(String.valueOf(produitSelectionne.getCategorie().trim()));
        ((TextView) findViewById(R.id.condAchat)).setText(String.valueOf(produitSelectionne.getCond_achat()).trim());
        ((TextView) findViewById(R.id.numLot)).setText(numLot.trim());
        ((TextView) findViewById(R.id.conditionnement)).setText(conditionnement.trim());
        ((TextView) findViewById(R.id.numSerie)).setText(numeroSerie.trim());

        // Gestion de la date de péremption
        if (dateP != "") {
            DateFormat dateFormat;
            if (dateP.length() == 5) {
                dateFormat = new SimpleDateFormat("yy-MM");
            } else {
                dateFormat = new SimpleDateFormat("yy-MM-dd");
            }

            Date datePeremption = null;

            try {
                datePeremption = dateFormat.parse(dateP);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (datePeremption != null) {
                Date now = new Date();
                if (now.after(datePeremption)) {
                    findViewById(R.id.warningPeremption).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.warningPeremption).setVisibility(View.GONE);
                }
                DateFormat newDateFormat = new SimpleDateFormat("MM-yyyy");
                String dateAAfficher = newDateFormat.format(datePeremption);
                ((TextView) findViewById(R.id.datePeremption)).setText(dateAAfficher);
            }
        }

        /*if(!numeroSerie.contentEquals("") && !produitSelectionne.isSuivi_Serialisation())
        {
            boolean activer_serialisation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Information", "Ce produit est sérialisé, voulez-vous mettre sa fiche à jour ?", "OuiNon");
            if(activer_serialisation)
            {
                boolean reception = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Information", "A quel moment souhaitez-vous sérialiser ?", "serialisation");
                if(reception)
                {
                    produitSelectionne.setSerialiser_Reception_Delivrance(true);
                }

                produitSelectionne.setSuivi_Serialisation(true);

                gestionnaireProduit.mettreAJourProduit(db, produitSelectionne);
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
            }
        }*/
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                    // Gestion du code GS1
                    String codeRecu = data.getStringExtra("code");
                    if(codeRecu != null)
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeRecu);
                        if (gs1Decoupe.size() != 1) {
                            // Si le code fourni est valide, on recharge l'activité actuelle avec le nouveau code
                            // Vérification utilisateur
                            confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Verification", "Etes vous sûrs de vouloir changer la référence GTIN ?", "OuiNon");
                            estCodeGS1 = true;
                        } else {
                            // Si le code fourni n'est pas un code GS1, on affiche un message d'erreur
                            confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Verification", "Etes vous sûrs de vouloir changer la référence Inconnue ?", "OuiNon");
                            estCodeGS1 = false;
                        }

                        if (confirmation) {
                            Intent newIntent = new Intent(DetailProduitIdentificationParScanActivity.this, DetailProduitIdentificationParScanActivity.class);
                            Bundle extras = super.getBundle();
                            extras.putInt("produitSelectionneID", produitSelectionne.getID_produit());
                            if (estCodeGS1) {
                                extras.putString("codeGS1", codeRecu);
                                extras.putString("codeInconnue", null);
                            } else {
                                extras.putString("codeGS1", null);
                                extras.putString("codeInconnue", codeRecu);
                            }

                            newIntent.putExtras(extras);

                            DetailProduitIdentificationParScanActivity.this.startActivity(newIntent);
                            DetailProduitIdentificationParScanActivity.this.finish();
                        }
                    }

                    break;
                }
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onBackPressed()
    {
        DetailProduitIdentificationParScanActivity.this.finish();
    }
}
