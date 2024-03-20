package fr.alcyons.phimr4.PlanDePlacement;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Depot_Emplacement;
import fr.alcyons.phimr4.Classes.Depot_Zone;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class DetailProduitPlanDePlacementActivity extends ServiceActivity {

    Produit produitSelectionne;

    TypeDepot typeDepotChoisi;

    PackageManager pm;

    Depot depotPUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produit_plan_de_placement);

        //gestion du package manager
        pm = DetailProduitPlanDePlacementActivity.this.getPackageManager();

        // Récupération du produit sélectionné par rapport à la variable globale
        produitSelectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitSelectionneID"));

        // Récupération du depot PUI
        depotPUI = DepotOpenHelper.getDepotPUI(db);

        // Affichage des informations de base
        ((TextView) findViewById(R.id.nomProduit)).setText(produitSelectionne.getDesignation_interne());
        ((TextView) findViewById(R.id.refProduit)).setText(produitSelectionne.getRef_fourni());
        ((TextView) findViewById(R.id.fournisseurProduit)).setText(produitSelectionne.getFournisseur());
        ((TextView) findViewById(R.id.nomZonePUI)).setText(produitSelectionne.getZone_PUI_Defaut());
        ((TextView) findViewById(R.id.nomEmplacementPUI)).setText(produitSelectionne.getEmplacement_PUI_Defaut());


        // Affectation des fonctions setOnClickListener
        findViewById(R.id.boutonModifPUI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeDepotChoisi = TypeDepot.PUI;
                clicBoutonModificationListener();
            }
        });
        findViewById(R.id.boutonCodePUI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeDepotChoisi = TypeDepot.PUI;
                clicBoutonDataMatrixListener();
            }
        });

        ((TextView) findViewById(R.id.nomEmplacementPUI)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeDepotChoisi = TypeDepot.PUI;
                String nomZone = produitSelectionne.getZone_PUI_Defaut();
                if(nomZone == null && nomZone.contentEquals(""))
                {
                    clicBoutonModificationListener();
                }
                else
                {
                    Depot_Zone zone_courante = ZoneOpenHelper.getZoneByDepotEtNom(db, depotPUI, nomZone);
                    Intent intentListeEmplacement = new Intent(DetailProduitPlanDePlacementActivity.this, ListeEmplacementActivity.class);
                    Bundle bundleListeEmplacement = new Bundle();
                    bundleListeEmplacement.putInt("zoneSelectionneeID", zone_courante.getZoneID());
                    bundleListeEmplacement.putInt("depotSelectionneID", depotPUI.getDepot_UID());
                    bundleListeEmplacement.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                    intentListeEmplacement.putExtras(bundleListeEmplacement);
                    DetailProduitPlanDePlacementActivity.this.startActivityForResult(intentListeEmplacement, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
                }
            }
        });


        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT:
                    Depot_Emplacement depotEmplacement = gestionnaireEmplacement.getUnEmplacementByID(db, data.getExtras().getInt("emplacementSelectionneID"));
                    Depot_Zone depotZone = gestionnaireZone.getUneZoneByID(db, data.getExtras().getInt("zoneSelectionneeID"));

                    int idTextViewZone = -1;
                    int idTextViewEmplacement = -1;

                    switch (typeDepotChoisi) {
                        case PUI:
                            idTextViewEmplacement = R.id.nomEmplacementPUI;
                            idTextViewZone = R.id.nomZonePUI;
                            ((TextView) findViewById(R.id.nomZonePUI)).setText(depotZone.getZoneName().trim());
                            ((TextView) findViewById(R.id.nomEmplacementPUI)).setText(depotEmplacement.getAdressage().trim());
                            break;
                        case UF:

                    }

                    afficherAlerteConfirmation(DetailProduitPlanDePlacementActivity.this, LayoutInflater.from(DetailProduitPlanDePlacementActivity.this), idTextViewEmplacement, idTextViewZone, depotEmplacement, depotZone);

                    break;
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    String code = data.getStringExtra("code");
                    if (code != null && !code.contentEquals("") ) {
                        String[] tab_code = code.split(":");
                        int emplacement_id = Integer.parseInt(tab_code[tab_code.length-1]);
                        Depot_Emplacement emplacementRetourne = gestionnaireEmplacement.getUnEmplacementByID(db, emplacement_id);
                        Depot_Zone zoneEmplacementRetourne = gestionnaireZone.getUneZoneByID(db, emplacementRetourne.getZoneID());

                        int textViewZoneID = -1;
                        int TextViewEmplacementID = -1;

                        switch (typeDepotChoisi) {
                            case PUI:
                                TextViewEmplacementID = R.id.nomEmplacementPUI;
                                textViewZoneID = R.id.nomZonePUI;
                                ((TextView) findViewById(R.id.nomZonePUI)).setText(zoneEmplacementRetourne.getZoneName().trim());
                                ((TextView) findViewById(R.id.nomEmplacementPUI)).setText(emplacementRetourne.getAdressage().trim());
                                break;

                        }

                        afficherAlerteConfirmation(DetailProduitPlanDePlacementActivity.this, LayoutInflater.from(DetailProduitPlanDePlacementActivity.this), TextViewEmplacementID, textViewZoneID, emplacementRetourne, zoneEmplacementRetourne);
                    }
                    else
                    {
                        switch (typeDepotChoisi) {
                            case PUI:
                                findViewById(R.id.boutonModifPUI).performClick();
                                break;

                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    public enum TypeDepot {
        PUI,
        UF,
        PAD
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retour, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem valider = menu.findItem(R.id.retourMenu);
        valider.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onBackPressed();
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        DetailProduitPlanDePlacementActivity.this.finish();
    }

    public void afficherAlerteConfirmation(Context context, LayoutInflater inflater, final int TextViewEmplacementID, final int textViewZoneID, final Depot_Emplacement emplacementRetourne, final Depot_Zone zoneEmplacementRetourne) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout = inflater.inflate(R.layout.alerte_confirmation_quitter, null);

        LinearLayout zoneok = (LinearLayout) layout.findViewById(R.id.buttonOk);
        LinearLayout buttonAnnuler = (LinearLayout) layout.findViewById(R.id.buttonAnnuler);
        TextView messageFin = (TextView) layout.findViewById(R.id.messageFin);
        messageFin.setText("Voulez-vous enregistrer les modifications ?");

        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        zoneok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeDepotChoisi) {
                    case PUI:
                        produitSelectionne.setZone_PUI_Defaut(zoneEmplacementRetourne.getZoneName().trim());
                        produitSelectionne.setEmplacement_PUI_Defaut(emplacementRetourne.getAdressage().trim());
                        break;
                    case UF:
                        produitSelectionne.setZone_UF_Defaut(zoneEmplacementRetourne.getZoneName().trim());
                        produitSelectionne.setEmplacement_UF_Defaut(emplacementRetourne.getAdressage().trim());
                        break;
                }

                gestionnaireProduit.mettreAJourProduit(db, produitSelectionne);
                gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, produitSelectionne.getPhiMR4UUID(), produitSelectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                ((TextView) findViewById(TextViewEmplacementID)).setText(emplacementRetourne.getAdressage().trim());
                ((TextView) findViewById(textViewZoneID)).setText(zoneEmplacementRetourne.getZoneName().trim());
                alertDialog.dismiss();
            }
        });

        buttonAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (typeDepotChoisi) {
                    case PUI:
                        ((TextView) findViewById(R.id.nomZonePUI)).setText(produitSelectionne.getZone_PUI_Defaut());
                        ((TextView) findViewById(R.id.nomEmplacementPUI)).setText(produitSelectionne.getEmplacement_PUI_Defaut());
                        break;

                }

                alertDialog.dismiss();
            }
        });
    }

    // Fonction permettant de définir l'action sur Click du bouton modifier
    public void clicBoutonModificationListener() {
        Intent detailProduitPlanDePlacementIntent = new Intent(DetailProduitPlanDePlacementActivity.this, ListeDepotsActivity.class);
        Bundle detailProduitPlanDePlacementBundle = DetailProduitPlanDePlacementActivity.super.getBundle();
        Depot depotSelectionne = gestionnaireDepot.getPUICourant(db);
        if (depotSelectionne != null) {
            Intent newIntent = new Intent(DetailProduitPlanDePlacementActivity.this, ListeZonesActivity.class);
            Bundle extras = DetailProduitPlanDePlacementActivity.super.getBundle();
            extras.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
            newIntent.putExtras(extras);
            DetailProduitPlanDePlacementActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
        } else {
            detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
            DetailProduitPlanDePlacementActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
        }
    }

    public void clicBoutonDataMatrixListener() {
        Intent detailProduitPlanDePlacementIntent = null;
        Bundle detailProduitPlanDePlacementBundle = DetailProduitPlanDePlacementActivity.super.getBundle();

        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            detailProduitPlanDePlacementIntent = new Intent(DetailProduitPlanDePlacementActivity.this, ScannerSearchOnlyActivity.class);
            detailProduitPlanDePlacementBundle.putBoolean("activerTextSuppression", true);
            detailProduitPlanDePlacementBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un emplacement");
            detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        }
        else
        {
            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            {
                detailProduitPlanDePlacementIntent = new Intent(DetailProduitPlanDePlacementActivity.this, BarcodeCaptureActivity.class);
            }
            else
            {
                detailProduitPlanDePlacementIntent = new Intent(DetailProduitPlanDePlacementActivity.this, ScannerSearchOnlyActivity.class);
                detailProduitPlanDePlacementBundle.putBoolean("activerTextSuppression", true);
                detailProduitPlanDePlacementBundle.putString("TextBannerManuel", "Scannez le datamatrix d'un emplacement");
            }
        }

        detailProduitPlanDePlacementBundle.putBoolean("isBoutonSuppressionExistant", true);
        detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
        detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
        detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
        DetailProduitPlanDePlacementActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
    }
}
