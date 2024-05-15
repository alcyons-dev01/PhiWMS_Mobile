package fr.alcyons.phiwms_mobile.ZonesEtEmplacements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_ZoneAdapter;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.SimpleMultiChoiceModeListener;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;
import fr.alcyons.phiwms_mobile.VerrouPharmacie.ServiceVerrouPharmacieActivity;

public class ListeZonesParDepotActivity extends ServiceActivity {

    Depot depotSelectionne;

    Depot_ZoneAdapter adapter;
    ListView zoneListView;
    List<Depot_Zone> depotZoneList;
    PackageManager pm;
    TextView nbElementInAdapter;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton boutonRechercheDataMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_zones_par_depot);

        // Récupération du dépot sélectionné par l'utilisateur
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));
        pm = ListeZonesParDepotActivity.this.getPackageManager();
        if (depotSelectionne != null) {
            String nomDepot = getNomDepot();
            ((TextView) findViewById(R.id.nomDepot)).setText(nomDepot);
        }

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        boutonRechercheDataMatrix = (FloatingActionButton) findViewById(R.id.boutonRechercheDataMatrix);
        boutonRechercheDataMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionMenu.close(true);
                Intent intentVersScanner = null;
                Bundle bundleVersScanner = new Bundle();
                bundleVersScanner.putBoolean("isBoutonSuppressionExistant", true);
                bundleVersScanner.putBoolean("modeRafale", false);
                bundleVersScanner.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                bundleVersScanner.putString("contexte", String.valueOf(R.string.scannerContexteZoneEtEmplacement));

                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !Build.MANUFACTURER.contains("Zebra Technologies") && !Build.MANUFACTURER.toLowerCase().contains("honeywell"))
                {
                    intentVersScanner = new Intent(ListeZonesParDepotActivity.this, BarcodeCaptureActivity.class);
                }
                else
                {
                    intentVersScanner = new Intent(ListeZonesParDepotActivity.this, ScannerSearchOnlyActivity.class);
                    bundleVersScanner.putInt("scannerContexteInt", R.string.scannerContexteZoneEtEmplacement);
                }

                intentVersScanner.putExtras(bundleVersScanner);
                ListeZonesParDepotActivity.this.startActivityForResult(intentVersScanner, CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT);
            }
        });

        nbElementInAdapter = (TextView) findViewById(R.id.nbElementInAdapter);

        // Récupération de la listeView des zones
        zoneListView = (ListView) findViewById(R.id.listeView);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                } else {
                    ListeZonesParDepotActivity.this.finish();
                }
            }
        });
    }

    private String getNomDepot() {
        String nomDepot = depotSelectionne.getNom();
        if(utilisateurConnecte.getIdentifiant().contentEquals("ALCYONS") && depotSelectionne.getStructure().contentEquals("PAD"))
        {
            String[] tab_nom = depotSelectionne.getNom().split(" ");
            String nom = tab_nom[0];
            if(nom.length() > 2)
            {
                nom = nom.substring(0, 3)+"...";
            }
            else
            {
                nom = nom +"...";
            }
            String prenom = tab_nom[1];
            if(prenom.length() > 2)
            {
                prenom = prenom.substring(0, 3)+"...";
            }
            else
            {
                prenom = prenom+"...";
            }
            nomDepot = nom+" "+prenom;
        }
        return nomDepot;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();

        // Récupération de la liste des zones à afficher
        depotZoneList = ZoneOpenHelper.getZonesEtEmplacementsParDepot(db, depotSelectionne);

        // Modifier le nombre de zones trouvées
        nbElementInAdapter.setText(String.valueOf(depotZoneList.size()));

        // Creation de l'adapter qui va gérer la liste des zones
        adapter = new Depot_ZoneAdapter(ListeZonesParDepotActivity.this, db, depotZoneList, zoneListView);

        // Lier le clic sur une zone à la fonction
        zoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Depot_Zone zoneSelectionnee = (Depot_Zone) adapter.getItem(position);

                if (zoneSelectionnee != null) {
                    // Récupérer les éléments nécessaires à la prochaine activité
                    Intent listeZonesParDepotIntent = new Intent(ListeZonesParDepotActivity.this, DetailZoneActivity.class);
                    Bundle listeZonesParDepotBundle = ListeZonesParDepotActivity.super.getBundle();
                    listeZonesParDepotBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
                    listeZonesParDepotBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
                    listeZonesParDepotIntent.putExtras(listeZonesParDepotBundle);

                    // Appeler la prochaine activité
                    ListeZonesParDepotActivity.this.startActivity(listeZonesParDepotIntent);
                }

            }
        });

        zoneListView.setAdapter(adapter);

        modeCreation();
    }

    // Permet de gérer si dans le menu se trouve ou non le bouton de suppression
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.prepareOptionsMenu(menu, adapter, null, "Nom zone...");
        return true;
    }
    public void modeCreation() {
        if (depotZoneList.size() == 1) {
            if (depotZoneList.get(0).getZoneName().equals("Zone")) {
                // Récupérer le bouton de création d'une nouvelle zone
                FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

                fabAdd.setVisibility(View.VISIBLE);

                fabAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nomNouvelleZone = Alerte.afficherAlerteEditText(ListeZonesParDepotActivity.this, "Nouvelle zone", "Entrez le nom de la nouvelle zone");
                        if (nomNouvelleZone != null) {
                            int idNouvelleZone = ZoneOpenHelper.creerUnNouvelIdDeZone(db);
                            if (idNouvelleZone != -1) {
                                // Créer la nouvelle zone
                                Depot_Zone nouvelleZone = new Depot_Zone(idNouvelleZone, nomNouvelleZone, 0.0, 0.0, "", depotSelectionne.getDepot_UID(), "", depotSelectionne.getDepot_Reference(), "");

                                // Créer le nouvel Emplacement
                                Depot_Emplacement nouvelEmplacement = new Depot_Emplacement(nomNouvelleZone, "", "", "", "", idNouvelleZone, depotSelectionne.getDepot_UID(), depotSelectionne.getDepot_Reference(), "");

                                // Insérer la zone et l'emplacement en BDD locale ainsi que dans la table élément à synchroniser
                                EmplacementOpenHelper.insererUnDepotEmplacementEnBDD(db, nouvelEmplacement);
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, nouvelEmplacement.getPhiMR4UUID(), nouvelEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                                ZoneOpenHelper.insererUnDepotZoneEnBDD(db, nouvelleZone);
                                ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE, nouvelleZone.getPhiMR4UUID(), nouvelleZone.getZoneID(), DBOpenHelper.ActionsEAS.AJOUT);

                                // Ajouter l'emplacement à la zone
                                nouvelleZone.addEmplacement(nouvelEmplacement);
                                adapter.add(nouvelleZone);
                                adapter.notifyDataSetChanged();

                                // Modifier le nombre de zones trouvées
                                TextView nbZones = (TextView) findViewById(R.id.nbElementInAdapter);
                                nbZones.setText(String.valueOf(adapter.getCount()));
                            } else {
                                Alerte.afficherAlerte(ListeZonesParDepotActivity.this, "Erreur BDD", "Veuillez contacter la société Alcyons ! \n Référence à transmettre : Création Zone", "alerte");
                            }
                        }
                        floatingActionMenu.close(true);
                    }
                });

                zoneListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                final SimpleMultiChoiceModeListener cml = new SimpleMultiChoiceModeListener(ListeZonesParDepotActivity.this, nbElementInAdapter, adapter);
                zoneListView.setMultiChoiceModeListener(cml);
                zoneListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
                    view.setActivated(true);
                    ((ListView) view).setItemChecked(position, !((Depot_ZoneAdapter) adapterView.getAdapter()).isPositionChecked(position));
                    return false;
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodesEchangesActivites.RETOUR_ZONE_ET_EMPLACEMENT) {
            if (data != null) {
                //Cacher le clavier
                InputMethodManager imm = (InputMethodManager) ListeZonesParDepotActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = ListeZonesParDepotActivity.this.getCurrentFocus();
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String code = Objects.requireNonNull(data.getExtras()).getString("code");
                if (code != null) {
                    Depot_Emplacement emplacement = EmplacementOpenHelper.getUnEmplacementByID(db, Integer.parseInt(code));
                    Depot_Zone zoneSelectionnee = ZoneOpenHelper.getUneZoneByID(db, emplacement.getZoneID());

                    if (zoneSelectionnee != null) {
                        // Récupérer les éléments nécessaires à la prochaine activité
                        Intent listeZonesParDepotIntent = getListeZonesParDepotIntent(zoneSelectionnee);

                        // Appeler la prochaine activité
                        ListeZonesParDepotActivity.this.startActivity(listeZonesParDepotIntent);
                    } else {
                        afficherSnackBarZoneInconnue();
                    }
                } else {
                    afficherSnackBarZoneInconnue();
                }
            } else {
                afficherSnackBarZoneInconnue();
            }
        }
    }

    @NonNull
    private Intent getListeZonesParDepotIntent(Depot_Zone zoneSelectionnee) {
        Intent listeZonesParDepotIntent = new Intent(ListeZonesParDepotActivity.this, DetailZoneActivity.class);
        Bundle listeZonesParDepotBundle = ListeZonesParDepotActivity.super.getBundle();
        listeZonesParDepotBundle.putInt("depotSelectionneID", depotSelectionne.getDepot_UID());
        listeZonesParDepotBundle.putInt("zoneSelectionneeID", zoneSelectionnee.getZoneID());
        listeZonesParDepotBundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        listeZonesParDepotIntent.putExtras(listeZonesParDepotBundle);
        return listeZonesParDepotIntent;
    }

    public void afficherSnackBarZoneInconnue() {
        Snackbar snackbar;
        snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), Html.fromHtml("<b>Zone scannée inconnue</b>", 0), Snackbar.LENGTH_LONG);
        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getResources().getColor(R.color.rouge2, null));
        TextView textView = (TextView) layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(TypedValue.TYPE_STRING, 8);
        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.BOTTOM;
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();
    }
}
