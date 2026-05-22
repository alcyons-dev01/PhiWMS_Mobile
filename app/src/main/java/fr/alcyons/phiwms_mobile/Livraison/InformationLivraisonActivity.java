package fr.alcyons.phiwms_mobile.Livraison;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.DocumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ActionUtilisateur_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_PreparationOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.PH_Preparation_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ParametresServeurOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.UtilisateurOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation;
import fr.alcyons.phiwms_mobile.Classes.PH_Preparation_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Utilisateur;
import fr.alcyons.phiwms_mobile.ListViewAdapters.PH_Preparation_Ligne_LivraisonAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.Dialogue;
import fr.alcyons.phiwms_mobile.Outils.Mail;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPDF;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionPhotos;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceLivraisonActivity;

import static fr.alcyons.phiwms_mobile.AuthentificationActivity.hasPermissions;

public class InformationLivraisonActivity extends ServiceActivity {

    private static final String TAG = "InformationLivraison";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // ═══════════════════════════════════════════
    // Données métier
    // ═══════════════════════════════════════════
    private PH_Preparation phPreparationSelectionnee;
    private List<PH_Preparation_Ligne> lignesList;
    private List<String> listeProduitRefuser;
    private Depot depot;
    private String adresse;
    private ActionUtilisateur newActionUtilisateur;

    // ═══════════════════════════════════════════
    // Email
    // ═══════════════════════════════════════════
    private String filename;
    private String signatureNameChauffeur;
    private String subject;
    private String body;

    // ═══════════════════════════════════════════
    // Photo
    // ═══════════════════════════════════════════
    private Bitmap photoLivraisonBitmap;
    private String photoLivraisonPhotoName;

    // ═══════════════════════════════════════════
    // UI
    // ═══════════════════════════════════════════
    private ListView listView;
    private PH_Preparation_Ligne_LivraisonAdapter adapter;
    private Dialogue dialogue;

    // ═══════════════════════════════════════════
    // Listeners
    // ═══════════════════════════════════════════
    private final View.OnClickListener onClickListenerValider = v -> {
        enregistrerLivraison();
    };
    private final View.OnClickListener clicValidationSignature = v -> {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());

        filename             = phPreparationSelectionnee.getUID() + "_" + date + "_Livraison.pdf";
        signatureNameChauffeur = phPreparationSelectionnee.getUID() + "_" + date + "_LivraisonSignature";

        verifyStoragePermissions(this);

        Bitmap bitmap = dialogue.signaturePad.getSignatureBitmap();
        OutilsGestionPhotos.saveExternalStorageImageJPEG(this, bitmap, signatureNameChauffeur);

        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            String img_str = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
            phPreparationSelectionnee.setSignature_Livraison(img_str);
        }

        dialogue.dialog.dismiss();
        dialogue = new Dialogue(this, onClickListenerValider, utilisateurConnecte);
        dialogue.padCommentairePhotoLivraison();
    };

    // ═══════════════════════════════════════════
    // Cycle de vie
    // ═══════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_livraison);

        initialiserDonnees(savedInstanceState);
        initialiserVues();
        configurerChevronAdresse();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        actualiserIndicateurs();
        initialiserListView();
        configurerFab();
        configurerClicLignes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ServiceLivraisonActivity.class);
        intent.putExtras(super.getBundle());
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CodesEchangesActivites.RESULT_SIGNATURE:
                if (data != null) finish();
                break;

            case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                if (data != null) {
                    String chemin = Objects.requireNonNull(data.getExtras()).getString("photoProduit");
                    if (chemin != null && !chemin.isEmpty()) {
                        try {
                            photoLivraisonBitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), Uri.parse(chemin));
                        } catch (IOException e) {
                            Log.e(TAG, "Erreur lecture photo", e);
                        }
                    }
                }
                break;
        }
    }

    // ═══════════════════════════════════════════
    // Initialisation
    // ═══════════════════════════════════════════
    private void initialiserDonnees(Bundle savedInstanceState) {
        listeProduitRefuser = new ArrayList<>();

        int uid = (savedInstanceState != null)
                ? savedInstanceState.getInt("ph_preparationUID_Selectionne")
                : intent.getIntExtra("ph_preparationUID_Selectionne", 0);

        phPreparationSelectionnee = PH_PreparationOpenHelper.getPH_PreparationByID(db, uid);

        List<PH_Preparation_Ligne> toutes = PH_Preparation_LigneOpenHelper
                .getAllPHPreparationLignesParPHPreparation(db, phPreparationSelectionnee);

        lignesList = new ArrayList<>();
        for (PH_Preparation_Ligne ligne : toutes) {
            if (ligne.getQte_livrer() > 0) lignesList.add(ligne);
        }
        lignesList.sort(Comparator.comparing(PH_Preparation_Ligne::getProduitDesignation));

        for (PH_Preparation_Ligne ligne : lignesList) {
            ligne.setAccepter(true);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
        }

        depot  = DepotOpenHelper.getDepotParReference(db, phPreparationSelectionnee.getDepotDestinataireReference());
        adresse = construireAdresse();
    }

    @SuppressLint("SetTextI18n")
    private void initialiserVues() {
        listView = findViewById(R.id.listeView);

        // Date de livraison
        String dateLivraison = formaterDate(phPreparationSelectionnee.getLivraisonPrevueDate(),
                "yyyy-MM-dd", "dd/MM/yyyy");

        ((TextView) findViewById(R.id.uidPHPreparation))
                .setText("N°" + phPreparationSelectionnee.getUID());
        ((TextView) findViewById(R.id.livraisonPrevueDate)).setText(dateLivraison);
        ((TextView) findViewById(R.id.identiteClient)).setText(depot.getNom().trim());
        ((TextView) findViewById(R.id.telephone)).setText(depot.getTel().trim());
        ((TextView) findViewById(R.id.adresse)).setText(adresse.trim());

        // URGENT
        if (!phPreparationSelectionnee.isURGENT()) {
            findViewById(R.id.isUrgent).setVisibility(View.GONE);
        }

        // Commentaire
        String commentaire = phPreparationSelectionnee.getCommentaires();
        if (commentaire != null && !commentaire.isEmpty()) {
            ((TextView) findViewById(R.id.commentaire)).setText(commentaire.trim());
        } else {
            findViewById(R.id.zoneCommentaire).setVisibility(View.GONE);
        }
    }

    private void actualiserIndicateurs() {
        List<PH_Preparation_Ligne> lignes = PH_Preparation_LigneOpenHelper
                .getALivrerPHPreparationLignesParPHPreparation(db, phPreparationSelectionnee);

        int nbColis = calculerNbColis(lignes);

        ((TextView) findViewById(R.id.montantTTC)).setText(String.valueOf((int) phPreparationSelectionnee.getMontant_TTC()));
        ((TextView) findViewById(R.id.poidsTotal)).setText(String.valueOf((int) phPreparationSelectionnee.getPoids()));
        ((TextView) findViewById(R.id.volume)).setText(String.valueOf((int) phPreparationSelectionnee.getVolume()));
        ((TextView) findViewById(R.id.nbRef)).setText(String.valueOf(lignes.size()));
        ((TextView) findViewById(R.id.nbColis)).setText(String.valueOf(nbColis));
    }

    private void initialiserListView() {
        adapter = new PH_Preparation_Ligne_LivraisonAdapter(this, lignesList);
        listView.setDivider(footer);
        listView.setAdapter(adapter);
    }

    // ═══════════════════════════════════════════
    // Configuration UI
    // ═══════════════════════════════════════════
    private void configurerChevronAdresse() {
        findViewById(R.id.layoutIdentite).setOnClickListener(v -> {
            View layoutAdresse   = findViewById(R.id.layoutAdresse);
            View deployer        = findViewById(R.id.deployerAdresse);
            View replier         = findViewById(R.id.replierAdresse);

            boolean estVisible = layoutAdresse.getVisibility() == View.VISIBLE;
            layoutAdresse.setVisibility(estVisible ? View.GONE : View.VISIBLE);
            deployer.setVisibility(estVisible ? View.VISIBLE : View.GONE);
            replier.setVisibility(estVisible ? View.GONE : View.VISIBLE);
        });
    }

    private void configurerFab() {
        findViewById(R.id.floatingMenu).setOnClickListener(v -> ouvrirBottomSheet());
    }

    private void configurerClicLignes() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PH_Preparation_Ligne ligne = lignesList.get(position);
            boolean accepter = !ligne.getAccepter();

            ligne.setAccepter(accepter);
            ligne.setQte_livrer(accepter ? ligne.getQte_APreparer() : 0);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);

            view.findViewById(R.id.Accepter).setVisibility(accepter ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.Refuser).setVisibility(accepter ? View.GONE : View.VISIBLE);

            adapter.notifyDataSetChanged();
        });
    }

    // ═══════════════════════════════════════════
    // Bottom Sheet
    // ═══════════════════════════════════════════
    private void ouvrirBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.aide_saisie_livraison, null);
        dialog.setContentView(sheetView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        FrameLayout bottomSheet = dialog.findViewById(
                com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) bottomSheet.setBackgroundColor(Color.TRANSPARENT);

        sheetView.findViewById(R.id.btnFermerBottomSheet)
                .setOnClickListener(v -> dialog.dismiss());

        sheetView.findViewById(R.id.boutonToutRefuser)
                .setOnClickListener(v -> {
                    toutRefuser();
                    dialog.dismiss();
                });

        sheetView.findViewById(R.id.boutonPatientAbsent)
                .setOnClickListener(v -> gererPatientAbsent(dialog));

        sheetView.findViewById(R.id.boutonSignatureLivreur)
                .setOnClickListener(v -> {
                    dialogue = new Dialogue(this, clicValidationSignature, utilisateurConnecte);
                    dialogue.signaturePadOpen(true);
                    dialog.dismiss();
                });

        dialog.show();
    }

    // ═══════════════════════════════════════════
    // Actions métier
    // ═══════════════════════════════════════════
    private void toutRefuser() {
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = listView.getChildAt(i);
            if (view != null) {
                view.findViewById(R.id.Accepter).setVisibility(View.GONE);
                view.findViewById(R.id.Refuser).setVisibility(View.VISIBLE);
                view.findViewById(R.id.layoutValidation)
                        .setBackgroundColor(getColor(R.color.rouge2));
            }
            lignesList.get(i).setAccepter(false);
            lignesList.get(i).setQte_livrer(0);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, lignesList.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void gererPatientAbsent(BottomSheetDialog dialog) {
        boolean confirmer = Alerte.afficherAlerte(this, "Confirmer",
                "Confirmez-vous que le patient est absent ?", "OuiNon");

        if (!confirmer) {
            dialog.dismiss();
            return;
        }

        phPreparationSelectionnee.setStatut("Refuser");
        phPreparationSelectionnee.setMotif("Patient absent");

        for (PH_Preparation_Ligne ligne : lignesList) {
            ligne.setQte_livrer(0);
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                    PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                    ligne.getPhiMR4UUID(), ligne.get_UID(),
                    ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
        }

        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationSelectionnee);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION,
                phPreparationSelectionnee.getPhiMR4UUID(),
                phPreparationSelectionnee.getUID(),
                ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, true);
        }

        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        subject = "phiwms_mobile - " + depot.getNom()
                + " - Livraisons N°" + phPreparationSelectionnee.getUID()
                + " Refusé - " + date;
        body = "Madame, Monsieur, \n\n"
                + "La livraison N°" + phPreparationSelectionnee.getUID()
                + " à destination de " + depot.getNom()
                + " a été refusée car le patient est absent.\n"
                + "Ceci est un message automatique merci de ne pas répondre\n\n";

        String email = getEmail();
        if (email != null) {
            new SendEmailTask().execute(email);
            onBackPressed();
        }
    }

    private void enregistrerLivraison() {
        // Commentaire
        String commentaire = dialogue.commentaireEditText.getText().toString();
        if (!commentaire.isEmpty()) {
            phPreparationSelectionnee.setCommentaires(commentaire);
        }

        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        phPreparationSelectionnee.setLivraisonDate(date);
        phPreparationSelectionnee.setLivree(true);
        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, phPreparationSelectionnee);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION,
                phPreparationSelectionnee.getPhiMR4UUID(),
                phPreparationSelectionnee.getUID(),
                ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, true);
        }

        creerActionUtilisateur();
        enregistrerLignes();
        construireEmail();

        try {
            new OutilsGestionPDF(true).createLivraisonV2(this, filename,
                    signatureNameChauffeur, db, phPreparationSelectionnee);
        } catch (IOException | DocumentException e) {
            Log.e(TAG, "Erreur création PDF", e);
        }

        String email = getEmail();
        if (email != null) new SendEmailTask().execute(email);

        dialogue.dialog.dismiss();
        phPreparationSelectionnee.setLivreur_userID(utilisateurConnecte.getId());
        mettreAJourPhPreparation(phPreparationSelectionnee);
        Toast.makeText(this, "Livraison effectuée", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void creerActionUtilisateur() {
        Random random = new Random();
        int actionId = -Math.abs(random.nextInt());

        @SuppressLint("SimpleDateFormat")
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        newActionUtilisateur = new ActionUtilisateur(actionId,
                utilisateurConnecte.getId(), dateString, serviceActuel.getId(),
                utilisateurConnecte.getEtablissementId(), "En attente",
                phPreparationSelectionnee.getUID(), "", "Livraison");

        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, newActionUtilisateur);
    }

    private void enregistrerLignes() {
        for (PH_Preparation_Ligne ligne : lignesList) {
            if (ligne.getQte_APreparer() != ligne.getQte_livrer()) {
                listeProduitRefuser.add(ligne.getProduitDesignation()
                        + " -> Qté à préparer : " + ligne.getQte_APreparer()
                        + " - Qté livrée : " + ligne.getQte_livrer());
            }
            PH_Preparation_LigneOpenHelper.mettreAJourUnPHPreparationLigne(db, ligne);
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                    PH_Preparation_LigneOpenHelper.Constantes.TABLE_PH_PREPARATION_LIGNE,
                    ligne.getPhiMR4UUID(), ligne.get_UID(),
                    ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(
                    db, buildActionLigne(ligne));
        }
    }

    @NonNull
    private ActionUtilisateur_Ligne buildActionLigne(PH_Preparation_Ligne ligne) {
        int id = -Math.abs(new Random().nextInt());
        return new ActionUtilisateur_Ligne(id, newActionUtilisateur.getId(),
                "Ph_Preparation_Ligne", ligne.get_UID(), "",
                0, ligne.getQte_livrer(), ligne.getProduitDesignation());
    }

    private void construireEmail() {
        Depot depotDest = DepotOpenHelper.getDepotParID(db, phPreparationSelectionnee.getDepotDestinataireID());
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String commentaire = phPreparationSelectionnee.getCommentaires();
        if (commentaire == null || commentaire.isEmpty()) commentaire = "Pas de commentaire saisi";

        String preparerPar = "", validerPar = "", livrerPar = "";

        Utilisateur livreur = UtilisateurOpenHelper.getUtilisateurByID(
                db, phPreparationSelectionnee.getLivreur_userID());
        if (livreur != null) livrerPar = livreur.getNom() + " " + livreur.getPrenom();

        if (phPreparationSelectionnee.getPreparateur() != null) {
            String[] parts = phPreparationSelectionnee.getPreparateur().split("\\(");
            preparerPar = parts[0];
            validerPar = parts[1].replace(")", "");
        }

        subject = "phiwms_mobile - " + depotDest.getNom()
                + " - Livraisons N°" + phPreparationSelectionnee.getUID()
                + " - " + date;

        if (listeProduitRefuser.isEmpty()) {
            body = buildBodyLivraisonComplete(depotDest, preparerPar, validerPar, livrerPar, commentaire);
        } else {
            body = buildBodyLivraisonPartielle(depotDest, preparerPar, validerPar, livrerPar, commentaire);
        }

        if (photoLivraisonBitmap != null) {
            photoLivraisonPhotoName = phPreparationSelectionnee.getUID()
                    + "_" + date + "_LivraisonPhoto";
            verifyStoragePermissions(this);
        }
    }

    private String buildBodyLivraisonComplete(Depot d, String prep, String valid, String livr, String commentaire) {
        return "Madame, Monsieur,\n\n"
                + "La livraison N°" + phPreparationSelectionnee.getUID()
                + " à destination de " + d.getNom() + " a été réalisée.\n"
                + "Préparé par " + prep + "\n"
                + "Validé par " + valid + "\n"
                + "Livré par " + livr + "\n"
                + "Vous pourrez trouver ci-joint le bon de livraison signé.\n"
                + "Commentaire : " + commentaire + "\n\n"
                + "Ceci est un message automatique merci de ne pas répondre\n\n";
    }

    private String buildBodyLivraisonPartielle(Depot d, String prep, String valid, String livr, String commentaire) {
        StringBuilder refus = new StringBuilder();
        for (String r : listeProduitRefuser) refus.append(r).append("\n");

        return "Madame, Monsieur,\n\n"
                + "La livraison N°" + phPreparationSelectionnee.getUID()
                + " à destination de " + d.getNom() + " a été réalisée.\n"
                + "Préparé par " + prep + "\n"
                + "Validé par " + valid + "\n"
                + "Livré par " + livr + "\n"
                + "Les produits suivants n'ont pas été livrés ou sont livrés en partie :\n\n"
                + refus
                + "\nVous pourrez trouver ci-joint le bon de livraison signé.\n"
                + "Commentaire : " + commentaire + "\n\n"
                + "Ceci est un message automatique merci de ne pas répondre\n\n";
    }

    // ═══════════════════════════════════════════
    // Utilitaires
    // ═══════════════════════════════════════════
    private String construireAdresse() {
        String adr;
        if (depot.getDepot_Reference().contains("PAD") && depot.isPAD_Utiliser_Adresse_Vacances()) {
            adr = depot.getPAD_Vacances_Adr1() + ", ";
            if (depot.getPAD_Vacances_Adr2().length() > 1) adr += depot.getPAD_Vacances_Adr2() + ", ";
            adr += depot.getPAD_Vacances_CP() + " " + depot.getPAD_Vacances_Ville();
        } else {
            adr = depot.getAdresse1() + ", ";
            if (depot.getAdresse2().length() > 1) adr += depot.getAdresse2() + ", ";
            adr += depot.getCP() + " " + depot.getVille();
        }
        return adr;
    }

    private String formaterDate(String dateStr, String formatEntree, String formatSortie) {
        try {
            @SuppressLint("SimpleDateFormat") DateFormat decodeur = new SimpleDateFormat(formatEntree);
            @SuppressLint("SimpleDateFormat") DateFormat encodeur = new SimpleDateFormat(formatSortie);
            Date date = decodeur.parse(dateStr);
            return date != null ? encodeur.format(date) : "";
        } catch (ParseException e) {
            Log.e(TAG, "Erreur formatage date", e);
            return "";
        }
    }

    private int calculerNbColis(List<PH_Preparation_Ligne> lignes) {
        int total = 0;
        for (PH_Preparation_Ligne ligne : lignes) {
            int qte = ligne.getQte_livrer();
            if (qte <= 0) continue;

            Produit produit = ProduitOpenHelper.getProduitByID(db, ligne.getProduitID());
            if (produit == null) continue;

            int cond = produit.getCond_achat();
            int colis = (cond > 0) ? (qte / cond) + (qte % cond != 0 ? 1 : 0) : 1;
            total += Math.max(colis, 1);
        }
        return total;
    }

    private String getEmail() {
        String email = ParametresServeurOpenHelper.getMailPharmacie(db);
        if ("ADH".equals(utilisateurConnecte.getEtablissement()))
            email = "livraison.pui@adh-asso.net";
        if ("ALCYONS".equalsIgnoreCase(utilisateurConnecte.getIdentifiant()))
            email = "dev01@alcyons.fr";
        return email;
    }

    public void mettreAJourPhPreparation(PH_Preparation preparation) {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        preparation.setLivree(true);
        preparation.setLivraisonDate(date);
        preparation.setStatut(preparation.getStatut().contains("en") ? "Délivrée en partie" : "Délivrée");

        PH_PreparationOpenHelper.mettreAJourUnPHPreparation(db, preparation);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db,
                PH_PreparationOpenHelper.Constantes.TABLE_PH_PREPARATION,
                preparation.getPhiMR4UUID(), preparation.getUID(),
                ElementASynchroniserOpenHelper.ActionsEAS.MAJ);

        if (statutConnexion) {
            ElementASynchroniserOpenHelper.toutSynchroniser(this, db, utilisateurConnecte, true);
        }
    }

    // ═══════════════════════════════════════════
    // Permissions
    // ═══════════════════════════════════════════
    public static void verifyStoragePermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    // ═══════════════════════════════════════════
    // Navigation
    // ═══════════════════════════════════════════
    public void telephoneDepot(View v) {
        String[] perms = { Manifest.permission.CALL_PHONE };
        if (!hasPermissions(this, perms)) {
            ActivityCompat.requestPermissions(this, perms, 1);
        } else {
            startActivity(new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:" + depot.getTel())));
        }
    }

    public void adresseDepot(View v) {
        String map = "https://www.google.com/maps/search/?api=1&query=" + adresse;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(map)));
    }

    public void onClickMenuPhoto() {
        Intent intent = new Intent(this, PrisePhoto.class);
        Bundle bundle = super.getBundle();
        bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle.putInt("serviceSelectionneID", serviceActuel.getId());
        bundle.putInt("preparationUID", phPreparationSelectionnee.getUID());
        bundle.putString("contexte", "priseDePhotoLivraison");
        intent.putExtras(bundle);
        startActivityForResult(intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    // ═══════════════════════════════════════════
    // Envoi email (AsyncTask)
    // ═══════════════════════════════════════════
    private class SendEmailTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String email = params[0];
            Mail sender = new Mail(InformationLivraisonActivity.this, email, true, db, utilisateurConnecte);
            try {
                if (filename == null || filename.isEmpty()) {
                    sender.sendMailVerification(subject, body);
                } else if (photoLivraisonPhotoName == null || photoLivraisonPhotoName.isEmpty()) {
                    sender.sendMail(subject, body, "Documents/" + filename);
                } else {
                    sender.sendMailPDFAndPhoto(subject, body,
                            "Documents/" + filename,
                            "Documents/" + photoLivraisonPhotoName + ".jpeg");
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur envoi email", e);
            }
            return null;
        }
    }
}