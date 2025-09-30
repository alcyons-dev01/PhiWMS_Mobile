package fr.alcyons.phiwms_mobile.IdentificationParScan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
        codeComplet = "";
        estCodeGS1 = false;
        // Récupération des variables globales et du produit selectionné
        produitSelectionne = ProduitOpenHelper.getProduitByID(db, Objects.requireNonNull(intent.getExtras()).getInt("produitSelectionneID"));
        ancienGTIN = produitSelectionne.getGTIN();
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
            ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
        }
        else if(produitSelectionne.getGTIN().contentEquals("") && produitSelectionne.getCodeInconnue().contentEquals(""))
        {
            ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.VISIBLE);
        }
        else
        {
            ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.VISIBLE);
        }

        //récupération photo
        Depot depot = DepotOpenHelper.getDepotPUI(db);
        MedicalObjective medicalObjective = new MedicalObjective(DetailProduitIdentificationParScanActivity.this, utilisateurConnecte, depot, depot, produitSelectionne, true);
        medicalObjective.getPictureImage("DetailProduitIdentificationParScanActivity");


        // Gestion du bouton de modification de code
        findViewById(R.id.boutonEditCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
                {
                    Intent newIntent = new Intent(DetailProduitIdentificationParScanActivity.this, ScannerSearchOnlyActivity.class);
                    Bundle bundle = DetailProduitIdentificationParScanActivity.super.getBundle();
                    bundle.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(bundle);
                    DetailProduitIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                }
                else
                {
                    if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
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
        findViewById(R.id.boutonSuppressionCode).setOnClickListener(v -> {
            boolean confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Attention", "Etes vous sûr de vouloir supprimer le code de ce produit ?", "OuiNon");
            if (confirmation) {
                if (estCodeGS1) {
                    produitSelectionne.setGTIN("");
                } else {
                    produitSelectionne.setCodeInconnue("");
                }
                codeComplet = "";
                messageAlerte = "";
                onResume();
            }
        });

        // On gère l'affichage en fonction de si codeComplet a déjà une valeur ou non
        if (estCodeGS1) {
            if (produitSelectionne.getGTIN().equals("") || produitSelectionne.getGTIN() == null) {
                produitSelectionne.setGTIN(codeComplet);
            }
        }
        else
        {
            boolean produitAModifier = false;
            if (produitSelectionne.getCodeInconnue() == null) {
                produitAModifier = true;
            } else if (!produitSelectionne.getCodeInconnue().contentEquals(codeComplet)) {
                produitAModifier = true;
            }

            if (produitAModifier) {
                produitSelectionne.setCodeInconnue(codeComplet.trim());
            }
        }


        String identification = produitSelectionne.getGTIN();
        if(identification.contentEquals(""))
            identification = produitSelectionne.getCodeInconnue();

        if(!identification.contentEquals(""))
            ((ImageView) findViewById(R.id.boutonEditCode)).setVisibility(View.GONE);
        // Affichage des valeurs
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne().trim());
        ((TextView) findViewById(R.id.codeGS1)).setText(identification);
        ((TextView) findViewById(R.id.nomFournisseur)).setText(produitSelectionne.getFournisseur().trim());
        ((TextView) findViewById(R.id.referenceFournisseur)).setText(produitSelectionne.getRef_fourni().trim());
        ((TextView) findViewById(R.id.categorie)).setText(produitSelectionne.getCategorie().trim());
        ((TextView) findViewById(R.id.condAchat)).setText(String.valueOf(produitSelectionne.getCond_achat()).trim());
        ((TextView) findViewById(R.id.condDistrib)).setText(String.valueOf(produitSelectionne.getCond_distrib()).trim());

    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        //((TextView) findViewById(R.id.numLot)).setText(numLot.trim());
       // ((TextView) findViewById(R.id.conditionnement)).setText(conditionnement.trim());
        //((TextView) findViewById(R.id.numSerie)).setText(numeroSerie.trim());

        // Gestion de la date de péremption
        /*if (!Objects.equals(dateP, "")) {
            DateFormat dateFormat;
            assert dateP != null;
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
                        String codeGtin = "";
                        if(codeRecu.startsWith("PHITAGTIN:"))
                        {
                            String[] tabCode = codeRecu.toString().split(":");
                            if(tabCode.length == 2)
                            {
                                estCodeGS1 = true;
                                codeGtin = tabCode[1];
                            }
                        }
                        else
                        {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeRecu);
                            if (gs1Decoupe.size() != 1) {
                                // Si le code fourni est valide, on recharge l'activité actuelle avec le nouveau code
                                // Vérification utilisateur
                                //confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Verification", "Etes vous sûrs de vouloir changer la référence GTIN ?", "OuiNon");
                                estCodeGS1 = true;
                                codeGtin = gs1Decoupe.get("codeGtin");
                            } else {
                                // Si le code fourni n'est pas un code GS1, on affiche un message d'erreur
                                //confirmation = Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Verification", "Etes vous sûrs de vouloir changer la référence Inconnue ?", "OuiNon");
                                estCodeGS1 = false;
                            }
                        }

                        confirmation = true;
                        if (confirmation) {
                            if (estCodeGS1) {
                                List<Produit> listeProduitRecherche = ProduitOpenHelper.getProduitsParGTIN(db, codeGtin);
                                boolean modifiable = true;
                                for(Produit produitCourant : listeProduitRecherche)
                                {
                                    if(produitCourant.getID_produit() != produitSelectionne.getID_produit())
                                    {
                                        modifiable = false;
                                        break;
                                    }
                                }
                                if(modifiable)
                                {
                                    produitSelectionne.setGTIN(codeGtin);
                                    ((TextView) findViewById(R.id.codeGS1)).setText(codeGtin);
                                    ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Erreur", "Code GTIN déjà utilisé pour une autre référence", "alerte");
                                }
                            } else {
                                List<Produit> listeProduitRecherche = ProduitOpenHelper.getProduitByCodeInconnu(db, codeRecu);
                                boolean modifiable = true;
                                for(Produit produitCourant : listeProduitRecherche)
                                {
                                    if(produitCourant.getID_produit() != produitSelectionne.getID_produit())
                                    {
                                        modifiable = false;
                                        break;
                                    }
                                }
                                if(modifiable)
                                {
                                    produitSelectionne.setCodeInconnue(codeRecu);
                                    ((TextView) findViewById(R.id.codeGS1)).setText(codeRecu);
                                    ((TextView) findViewById(R.id.referenceIdentifie)).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.warningNonIdentifie)).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.referenceEnCoursIdentification)).setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    Alerte.afficherAlerte(DetailProduitIdentificationParScanActivity.this, "Erreur", "Code d'identification déjà utilisé pour une autre référence", "alerte");
                                }

                            }
                        }
                    }
                    break;
                }
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);

        menu.findItem(R.id.menuSaveCircle).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSave = menu.findItem(R.id.menuSaveCircle);

        itemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(ancienGTIN.contentEquals(produitSelectionne.getGTIN()))
                {
                    retourService(DetailProduitIdentificationParScanActivity.this.getBundle());
                }
                else
                {
                    onMenuSaveClick();
                }
                return true;
            }
        });

        return true;
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
        if(ancienGTIN.contentEquals(produitSelectionne.getGTIN())) {
            retourService(DetailProduitIdentificationParScanActivity.this.getBundle());
        }
        else
        {
            onMenuSaveClick();
        }
    }
}
