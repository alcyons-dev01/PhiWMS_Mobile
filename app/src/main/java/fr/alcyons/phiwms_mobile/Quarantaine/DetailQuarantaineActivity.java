package fr.alcyons.phiwms_mobile.Quarantaine;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DateFormat;
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
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.RetourOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.Retour_LigneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur;
import fr.alcyons.phiwms_mobile.Classes.ActionUtilisateur_Ligne;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Classes.Retour;
import fr.alcyons.phiwms_mobile.Classes.Retour_Ligne;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Retour_Ligne_QuarantaineAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.Services.ServiceQuarantaineActivity;

public class DetailQuarantaineActivity extends ServiceActivity {

    public Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolderAModifier;
    public TextView numeroLotTextView;
    public TextView datePeremtionTextView;
    Retour retourSelectionne;
    List<Retour_Ligne> retourLigneList;
    Retour_Ligne_QuarantaineAdapter retourLigneQuarantaineAdapter;
    ListView retourLigneListView;
    public boolean tous_inconnu;
    PackageManager pm;
    String commentaire;
    ActivityResultLauncher<Intent> resultScanCodeGs1;

    public void decoderCodeBarre(TextView dateAModifier, TextView numLotAModifier, Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder, String designation) {
        datePeremtionTextView = dateAModifier;
        numeroLotTextView = numLotAModifier;
        viewHolderAModifier = viewHolder;
        Intent detailQuarantaineIntent;
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            detailQuarantaineIntent = new Intent(DetailQuarantaineActivity.this, ScannerSearchOnlyActivity.class);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
            {
                detailQuarantaineIntent = new Intent(DetailQuarantaineActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                detailQuarantaineIntent = new Intent(DetailQuarantaineActivity.this, ScannerSearchOnlyActivity.class);
            }
        }

        Bundle detailQuarantaineBundle = super.getBundle();
        detailQuarantaineBundle.putBoolean("doitEtreIdentique", true);
        detailQuarantaineBundle.putString("Designation", designation);
        detailQuarantaineIntent.putExtras(detailQuarantaineBundle);
        resultScanCodeGs1.launch(detailQuarantaineIntent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_quarantaine);

        //gestion du package manager
        pm = DetailQuarantaineActivity.this.getPackageManager();

        // Récupération du retour grace à la variable globale
        int retour_UID = Objects.requireNonNull(intent.getExtras()).getInt("retourSelectionneID");
        retourSelectionne = RetourOpenHelper.getRetourByID(db, retour_UID);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.intitule)).setText(retourSelectionne.getIntitule());
        ((TextView) findViewById(R.id.numero)).setText(retourSelectionne.getNumero());
        ((TextView) findViewById(R.id.motif)).setText(retourSelectionne.getMotif());

        // Gestion de la listView
        retourLigneListView = findViewById(R.id.listeView);
        //retourLigneListView.setDivider(footer);
        retourLigneListView.setItemsCanFocus(true);
        retourLigneListView.setOnItemClickListener((parent, view, position, id) -> {
            Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(position);
            retourLigneQuarantaineAdapter.setModeModif(true, viewHolder);
            retourLigneQuarantaineAdapter.notifyDataSetChanged();
        });

        resultScanCodeGs1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (result.getResultCode() == CodesEchangesActivites.RETOUR_CODE_GS1) {
                        assert data != null;
                        String codeComplet = data.getStringExtra("code");
                        assert codeComplet != null;
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (gs1Decoupe.size() != 1) {
                            if (datePeremtionTextView != null && numeroLotTextView != null) {

                                Produit produitCourant = ProduitOpenHelper.getUnProduitParGTIN(db, gs1Decoupe.get(OutilsDecodage.codeGtin));
                                if (produitCourant == null) {
                                    Toast toast = Toast.makeText(DetailQuarantaineActivity.this, "Produit scanné inconnu", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                } else {
                                    Retour_Ligne retour_ligne = retourLigneQuarantaineAdapter.retourLigneList.get(retourLigneQuarantaineAdapter.viewHolderList.indexOf(viewHolderAModifier));

                                    if (produitCourant.getID_produit() == retour_ligne.getCode_produit()) {
                                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");

                                        Date date = new Date();

                                        try {
                                            date = dateFormat1.parse(Objects.requireNonNull(gs1Decoupe.get(OutilsDecodage.dateDePeremption)));
                                        } catch (Throwable e) {
                                            Log.e(TAG, "Error date peremption :", e);
                                        }

                                        assert date != null;
                                        String dateFinale = dateFormat2.format(date);

                                        datePeremtionTextView.setText(dateFinale);
                                        numeroLotTextView.setText(gs1Decoupe.get(OutilsDecodage.numeroLot));

                                        viewHolderAModifier.valeurDate = dateFinale;
                                        viewHolderAModifier.valeurLot = gs1Decoupe.get(OutilsDecodage.numeroLot);

                                        mettreAJourUnRetourLigne(retourLigneQuarantaineAdapter.retourLigneList.get(retourLigneQuarantaineAdapter.viewHolderList.indexOf(viewHolderAModifier)), viewHolderAModifier);
                                    } else {
                                        Toast toast = Toast.makeText(DetailQuarantaineActivity.this, "La référence scanné ne correspond pas au produit sélectionné.", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                                        toast.show();
                                    }
                                }
                            }
                        } else {
                            Toast toast = Toast.makeText(DetailQuarantaineActivity.this, "Le code fourni n'est pas un code GS1, veuillez réessayer.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }
                    }
                    invalidateOptionsMenu();

                });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Alerte.afficherAlerteConfirmation(DetailQuarantaineActivity.this, getLayoutInflater(), getBundle(), "Souhaitez vous quitter sans enregistrer les modifications ?", true, false,DetailQuarantaineActivity.this);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        tous_inconnu = true;
        retourLigneList = Retour_LigneOpenHelper.getAllRetourLignesByRetour(db, retourSelectionne);

        // Initialisation de l'adapter puis transfere de l'adapter à la listeView pour l'affichage
        retourLigneQuarantaineAdapter = new Retour_Ligne_QuarantaineAdapter(DetailQuarantaineActivity.this, db, retourLigneList);
        retourLigneListView.setAdapter(retourLigneQuarantaineAdapter);

        findViewById(R.id.btnValiderQuarantaine_CV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerte.afficherAlerteConfirmation(DetailQuarantaineActivity.this, getLayoutInflater(), getBundle(), "Confirmez-vous la validation de cette quarantaine ?", false, true, DetailQuarantaineActivity.this);
            }
        });

        // Dans votre Activity
        findViewById(R.id.fabAideSaisie).setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.aide_saisie_quarantaine, null);
            dialog.setContentView(sheetView);

            dialog.setCanceledOnTouchOutside(false); // empêche la fermeture ET bloque les clics derrière
            dialog.setCancelable(false);             // empêche aussi la fermeture avec le bouton retour

            // Fond transparent
            FrameLayout bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
            }

            sheetView.findViewById(R.id.btnToutDetruire).setOnClickListener(v2 -> {
                // logique tout détruire
                for (int i = 0; i < retourLigneQuarantaineAdapter.viewHolderList.size(); i++)
                {
                    Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(i);
                    viewHolder.toutDetruire();
                }
                dialog.dismiss();
            });
            sheetView.findViewById(R.id.btnToutRetournerPUI).setOnClickListener(v2 -> {
                // logique retour PUI
                for (int i = 0; i < retourLigneQuarantaineAdapter.viewHolderList.size(); i++) {
                    Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(i);
                    viewHolder.toutRetournerPUI();
                }
                dialog.dismiss();
            });
            sheetView.findViewById(R.id.btnToutRetournerFournisseur).setOnClickListener(v2 -> {
                // logique retour fournisseur
                for (int i = 0; i < retourLigneQuarantaineAdapter.viewHolderList.size(); i++) {
                    Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(i);
                    viewHolder.toutRetournerFrs();
                }
                dialog.dismiss();
            });
            sheetView.findViewById(R.id.btnReinitialiser).setOnClickListener(v2 -> {
                // logique réinitialiser
                for (int i = 0; i < retourLigneQuarantaineAdapter.viewHolderList.size(); i++) {
                    Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(i);
                    viewHolder.toutRemettreAZero();
                }
                dialog.dismiss();
            });
            sheetView.findViewById(R.id.btnFermerBottomSheet).setOnClickListener(v2 -> dialog.dismiss());

            dialog.show();
        });
    }

    public void mettreAJourUnRetourLigne(Retour_Ligne retourLigne, Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder) {
        //Transformation de la date de dd/MM/yyyy à yyyy-MM-dd
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
            assert dateFournie != null;
            retourLigne.setPeremptionDate(dateFormat.format(dateFournie));
        } catch (Throwable e) {
            Log.e(TAG, "Error date peremption :", e);
        }

        retourLigne.setLot_Retourner(viewHolder.valeurLot);
        retourLigne.setDestruction_Qte(viewHolder.valeurDestruction);
        retourLigne.setRetourPui_Qte(viewHolder.valeurPUI);
        retourLigne.setRetourFrs_Qte(viewHolder.valeurFrs);

        Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);
    }

    @Override
    public void confirmationService(){
        int compteurReussite = 0;
        int nbElements = retourLigneQuarantaineAdapter.viewHolderList.size();

        // Vérifie que toutes les quantités ont été répartie
        for (int i = 0; i < nbElements; i++) {

            Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(i);

            int qteDestruction = viewHolder.valeurDestruction == -1 ? 0 : viewHolder.valeurDestruction;
            int qteRetourPui = viewHolder.valeurPUI == -1 ? 0 : viewHolder.valeurPUI;
            int qteRetourFrs = viewHolder.valeurFrs == -1 ? 0 : viewHolder.valeurFrs;

            int sommeQte = qteDestruction + qteRetourFrs + qteRetourPui;

            if (sommeQte == viewHolder.valeurQteRetourner) {
                compteurReussite++;
            }
        }

        if (nbElements != compteurReussite) {
            Alerte.afficherAlerteInformation(DetailQuarantaineActivity.this, getLayoutInflater(), "Erreur", "Tous les éléments n'ont pas été correctement traités.", false, false);
            return;
        }

        //Création de l'action utilisateur
        Random random = new Random();
        int actionId = random.nextInt();
        if(actionId > 0)
            actionId= actionId*-1;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateDestruction =new Date();
        String date_string = parseFormat.format(dateDestruction);
        ActionUtilisateur new_action_utilisateur = new ActionUtilisateur(actionId, utilisateurConnecte.getId(), date_string, serviceActuel.getId(), utilisateurConnecte.getEtablissementId(), "En attente", retourSelectionne.get_UID(), "", "Quarantaine");
        ActionUtilisateurOpenHelper.insererActionUtilisateurEnBDD(db, new_action_utilisateur);
        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ActionUtilisateurOpenHelper.Constantes.TABLE_ACTION_UTILISATEUR, new_action_utilisateur.getPhiMR4UUID(), new_action_utilisateur.getId(), DBOpenHelper.ActionsEAS.AJOUT);
        //fin de la création de l'action utilisateur

        // Met à jour les Retour_Ligne
        compteurReussite = 0;
        for (Retour_Ligne retourLigne : retourLigneQuarantaineAdapter.retourLigneList)
        {
            Retour_Ligne_QuarantaineAdapter.Retour_LigneViewHolder viewHolder = retourLigneQuarantaineAdapter.viewHolderList.get(retourLigneQuarantaineAdapter.retourLigneList.indexOf(retourLigne));
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") DateFormat dateDecodeur = new SimpleDateFormat("dd/MM/yyyy");

            try {
                Date dateFournie = dateDecodeur.parse(viewHolder.valeurDate);
                assert dateFournie != null;
                retourLigne.setPeremptionDate(dateFormat.format(dateFournie));
            } catch (Throwable e) {
                Log.e(TAG, "Error date peremption :", e);
            }

            retourLigne.setLot_Retourner(viewHolder.valeurLot);
            retourLigne.setDestruction_Qte(viewHolder.valeurDestruction);
            retourLigne.setRetourPui_Qte(viewHolder.valeurPUI);
            retourLigne.setRetourFrs_Qte(viewHolder.valeurFrs);

            long rowID = Retour_LigneOpenHelper.mettreAJourUnRetourLigne(db, retourLigne);

            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, Retour_LigneOpenHelper.Constantes.TABLE_RETOUR_LIGNE, retourLigne.getPhiMR4UUID(), retourLigne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
                compteurReussite++;
            }

            Random randomactionligne = new Random();
            int actionligneId = randomactionligne.nextInt();
            if(actionligneId > 0)
                actionligneId= actionligneId*-1;

            ActionUtilisateur_Ligne actionUtilisateur_ligne = new ActionUtilisateur_Ligne(actionligneId, new_action_utilisateur.getId(), "Retour Ligne", retourLigne.get_UID(), "", 0, (int)retourLigne.getQte_Retourner(), retourLigne.getProduit_Designation());
            ActionUtilisateur_LigneOpenHelper.insererActionUtilisateurLigneEnBDD(db, actionUtilisateur_ligne);
        }

        if (compteurReussite == retourLigneQuarantaineAdapter.retourLigneList.size()) {
            retourSelectionne.setStatut(getString(R.string.statutValide));
            retourSelectionne.setEn_Attente_de(getString(R.string.Quarantaine));
            retourSelectionne.setCommentaire(commentaire);

            long rowID = RetourOpenHelper.mettreAJourRetour(db, retourSelectionne);

            if (rowID != -1) {
                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, RetourOpenHelper.Constantes.TABLE_RETOUR, retourSelectionne.getPhiMR4UUID(), retourSelectionne.get_UID(), ElementASynchroniserOpenHelper.ActionsEAS.MAJ);
            }
            if (statutConnexion) {
                ElementASynchroniserOpenHelper.toutSynchroniser(DetailQuarantaineActivity.this, db, utilisateurConnecte, true);
            }
            Toast.makeText(DetailQuarantaineActivity.this, "Demande d'enregistrement effectuée", Toast.LENGTH_SHORT).show();
        } else {
            Alerte.afficherAlerteInformation(DetailQuarantaineActivity.this, getLayoutInflater(),"Alerte", "Une erreur est survenue, aucun traitement ne sera effectué.", false, false);
            ElementASynchroniserOpenHelper.viderTableElementASynchroniser(db);
        }

        Intent serviceQuarantaineIntent = new Intent(DetailQuarantaineActivity.this, ServiceQuarantaineActivity.class);
        Bundle serviceQuarantaineBundle = DetailQuarantaineActivity.super.getBundle();

        serviceQuarantaineIntent.putExtras(serviceQuarantaineBundle);
        DetailQuarantaineActivity.this.startActivity(serviceQuarantaineIntent);
        DetailQuarantaineActivity.this.finish();
    }

    @Override
    public void retourService(Bundle bundle)
    {
        Intent detailQuanrantaineIntent = new Intent(DetailQuarantaineActivity.this, ServiceQuarantaineActivity.class);
        Bundle detailQuarantaineBundle = super.getBundle();
        detailQuanrantaineIntent.putExtras(detailQuarantaineBundle);
        DetailQuarantaineActivity.this.startActivity(detailQuanrantaineIntent);
        DetailQuarantaineActivity.this.finish();
    }
}
