package fr.alcyons.phiwms_mobile.ZonesEtEmplacements;

import static fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.getEmplacementsParZone;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.insererUnDepotEmplacementEnBDD;
import static fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper.mettreAJourEmplacement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.DepotOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ElementASynchroniserOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.EmplacementOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ZoneOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Depot_Emplacement;
import fr.alcyons.phiwms_mobile.Classes.Depot_Zone;
import fr.alcyons.phiwms_mobile.ListViewAdapters.Depot_EmplacementAdapter;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.SimpleMultiChoiceModeListener;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailZoneSaisieActivity extends ServiceActivity {

    Depot_Zone depotZoneSelectionne;
    Depot depotSelectionne;

    List<Depot_Emplacement> depotEmplacementDeBaseList;
    List<Depot_Emplacement> depotEmplacementList;

    TextView caracteristiqueZoneTextView;
    TextView dataMatrixReferenceTextView;
    EditText typeRangementEditText;
    TextView nbEmplacementsTextView;

    ListView depotEmplacementListView;
    Depot_EmplacementAdapter adapter;
    Depot_EmplacementAdapter.Depot_EmplacementViewHolder depotEmplacementViewHolder;

    boolean editerEmplacement = false;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_zone_modifiable);

        //gestion du package manager
        pm = DetailZoneSaisieActivity.this.getPackageManager();

        depotZoneSelectionne = ZoneOpenHelper.getUneZoneByID(db, intent.getExtras().getInt("depotZoneSelectionneID"));
        depotSelectionne = DepotOpenHelper.getDepotParID(db, intent.getExtras().getInt("depotSelectionneID"));

        depotEmplacementListView = (ListView) findViewById(R.id.listeView);
        depotEmplacementList = new ArrayList<>();
        nbEmplacementsTextView = (TextView) findViewById(R.id.nbEmplacements);

        if (depotSelectionne != null) {
            String nomDepot = getNomDepot();
            ((TextView) findViewById(R.id.nomDepot)).setText(nomDepot);
        }
        if (depotZoneSelectionne != null) {
            ((TextView) findViewById(R.id.zoneId)).setText(String.valueOf(depotZoneSelectionne.getZoneID()));
            ((TextView) findViewById(R.id.nomZone)).setText(depotZoneSelectionne.getZoneName());

            dataMatrixReferenceTextView = (TextView) findViewById(R.id.identifiantCodeBarre);
            dataMatrixReferenceTextView.setText(String.valueOf(depotZoneSelectionne.getDataMatrixReference()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getDataMatrixReference()));

            // Afficher les éléments modifiables
            caracteristiqueZoneTextView = (TextView) findViewById(R.id.caracteristique);
            caracteristiqueZoneTextView.setText(String.valueOf(depotZoneSelectionne.getConservation()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getConservation()));

            // Gestion du clic sur les caractéristiques de conservation
           final View.OnClickListener clicSurIconeCaracteristique = v -> {
                ImageView imageCliquee = (ImageView) v;

                if(imageCliquee.getId() == R.id.symboleausec)
                {
                    caracteristiqueZoneTextView.setText(R.string.controle_hydrometrie);
                }
                else if(imageCliquee.getId() == R.id.symboletemperaturerefrigere)
                {
                    caracteristiqueZoneTextView.setText(R.string.controle_refrigeration);
                }
                else if(imageCliquee.getId() == R.id.symboleabriradiation)
                {
                    caracteristiqueZoneTextView.setText(R.string.controle_radiation);
                }
                else if(imageCliquee.getId() == R.id.symboletemperatureambiante)
                {
                    caracteristiqueZoneTextView.setText(R.string.controle_temperature);
                }
                else if(imageCliquee.getId() == R.id.symboleabrilumiere)
                {
                    caracteristiqueZoneTextView.setText(R.string.controle_luminosite);
                }
            };
            // Récupération des bouton caractéristiques de conservation
            findViewById(R.id.symboleausec).setOnClickListener(clicSurIconeCaracteristique);
            findViewById(R.id.symboletemperaturerefrigere).setOnClickListener(clicSurIconeCaracteristique);
            findViewById(R.id.symboleabriradiation).setOnClickListener(clicSurIconeCaracteristique);
            findViewById(R.id.symboletemperatureambiante).setOnClickListener(clicSurIconeCaracteristique);
            findViewById(R.id.symboleabrilumiere).setOnClickListener(clicSurIconeCaracteristique);

            typeRangementEditText = (EditText) findViewById(R.id.typesDeRangements);
            typeRangementEditText.setText(String.valueOf(depotZoneSelectionne.getType_Emplacement()).equals("null") ? "" : String.valueOf(depotZoneSelectionne.getType_Emplacement()));

            // Gestion des emplacements
            depotEmplacementDeBaseList = new ArrayList<>();

            depotEmplacementList = EmplacementOpenHelper.getEmplacementsParZoneID(db, depotZoneSelectionne.getZoneID());
            for (Depot_Emplacement depotEmplacement : depotEmplacementList) {
                depotEmplacementDeBaseList.add(depotEmplacement);
            }


            ImageView boutonAddEmplacement = (ImageView) findViewById(R.id.boutonAddEmplacement);
            if (editerEmplacement) {
                // Récupérer le bouton de création d'un nouvel emplacement

                boutonAddEmplacement.setOnClickListener(view -> {
                    String nomNouvelEmplacement = Alerte.afficherAlerteEditText(DetailZoneSaisieActivity.this, "Nouvel emplacement", "Entrez le nom du nouvel emplacement");
                    if (nomNouvelEmplacement != null) {
                        // Créer le nouvel Emplacement
                        Depot_Emplacement nouvelEmplacement = new Depot_Emplacement(nomNouvelEmplacement, null, null, null, null, depotZoneSelectionne.getZoneID(), depotSelectionne.getDepot_UID(), depotSelectionne.getDepot_Reference(), "");
                        depotEmplacementList.add(nouvelEmplacement);
                        onResume();
                    }
                });
            } else {
                boutonAddEmplacement.setVisibility(View.GONE);
            }

        }
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
    public void onResume() {
        super.onResume();

        invalidateOptionsMenu();

        adapter = new Depot_EmplacementAdapter(DetailZoneSaisieActivity.this, db, depotEmplacementList);

        if (editerEmplacement) {
            depotEmplacementListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            final SimpleMultiChoiceModeListener cml = new SimpleMultiChoiceModeListener(DetailZoneSaisieActivity.this, nbEmplacementsTextView, adapter);
            depotEmplacementListView.setMultiChoiceModeListener(cml);
            depotEmplacementListView.setOnItemLongClickListener((adapterView, view, position, l) -> {
                view.setActivated(true);
                ((ListView) view).setItemChecked(position, !((Depot_EmplacementAdapter) adapterView.getAdapter()).isPositionChecked(position));

                return false;
            });
        }

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
        {
            depotEmplacementListView.setOnItemClickListener((parent, view, position, id) -> {
                Depot_Emplacement depotEmplacementSelectionne = (Depot_Emplacement) adapter.getItem(position);

                if (depotEmplacementSelectionne != null) {

                    depotEmplacementViewHolder = adapter.viewHolderList.get(position);
                }

                Intent detailProduitPlanDePlacementIntent = new Intent(DetailZoneSaisieActivity.this, BarcodeCaptureActivity.class);
                Bundle detailProduitPlanDePlacementBundle = DetailZoneSaisieActivity.super.getBundle();
                detailProduitPlanDePlacementBundle.putString("bannerText", "Scanner un emplacement");
                detailProduitPlanDePlacementBundle.putString("contexte", String.valueOf(R.string.scannerContexteEmplacement));
                detailProduitPlanDePlacementIntent.putExtras(detailProduitPlanDePlacementBundle);
                DetailZoneSaisieActivity.this.startActivityForResult(detailProduitPlanDePlacementIntent, CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT);
            });
        }

        depotEmplacementListView.setAdapter(adapter);

        nbEmplacementsTextView.setText(String.valueOf(depotEmplacementList.size()));
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_EMPLACEMENT:
                    String code = data.getStringExtra("code");
                    if (!code.contentEquals("")) {
                        Depot_Emplacement depotEmplacementAdapter = adapter.depotEmplacementList.get(adapter.viewHolderList.indexOf(depotEmplacementViewHolder));
                        depotEmplacementAdapter.setCode_GLN(code.trim());
                        Toast toast = Toast.makeText(DetailZoneSaisieActivity.this, "Code GLN enregistré !", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(DetailZoneSaisieActivity.this, "Code GLN non enregistré !", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Récupération du menu action et utilisation de l'item ADD
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        menu.findItem(R.id.menuSave).setVisible(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        MenuItem item = menu.findItem(R.id.menuSave);
        item.setOnMenuItemClickListener(item1 -> {
            onMenuSaveClick();
            return true;
        });
        return true;
    }

    private void onMenuSaveClick() {
        enregistrerModification();
        onBackPressed();
    }

    // Sauvegader les modifications en base de données
    public void enregistrerModification() {
        int phimr4uuid = depotZoneSelectionne.getPhiMR4UUID();
        depotZoneSelectionne = new Depot_Zone(depotZoneSelectionne.getZoneID(), depotZoneSelectionne.getZoneName(), depotZoneSelectionne.getZoneLongitude(), depotZoneSelectionne.getZoneLatitude(), dataMatrixReferenceTextView.getText().toString(), depotZoneSelectionne.getDepotID(), caracteristiqueZoneTextView.getText().toString(), depotZoneSelectionne.getDepot_Reference(), typeRangementEditText.getText().toString(), depotZoneSelectionne.getEmplacements());
        depotZoneSelectionne.setPhiMR4UUID(phimr4uuid);

        long rowId = ZoneOpenHelper.mettreAJourZone(db, depotZoneSelectionne);
        if (rowId != -1) {

            if (depotEmplacementDeBaseList.isEmpty()) {
                for (Depot_Emplacement depotEmplacement : adapter.depotEmplacementList) {
                    insererUnDepotEmplacementEnBDD(db, depotEmplacement);
                    ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, depotEmplacement.getPhiMR4UUID(), depotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);
                }
            } else {
                // Est-ce qu'un emplacement a été supprimé ?
                for (Depot_Emplacement depotEmplacementDeBase : depotEmplacementDeBaseList) {
                    boolean aSupprimer = true;
                    for (Depot_Emplacement depotEmplacement : adapter.depotEmplacementList) {
                        if (depotEmplacement.get_UID() == depotEmplacementDeBase.get_UID()) {
                            aSupprimer = false;
                            break;
                        }
                    }
                    if (aSupprimer) {
                        db.delete(EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, DBOpenHelper.Constantes.CLE_COL_phiwms_mobileUUID + "=?", new String[]{String.valueOf(depotEmplacementDeBase.getPhiMR4UUID())});
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, depotEmplacementDeBase.getPhiMR4UUID(), depotEmplacementDeBase.get_UID(), DBOpenHelper.ActionsEAS.SUPPR);
                    }
                }
                // Est-ce qu'un emplacement a été ajouté ?
                for (Depot_Emplacement depotEmplacement : adapter.depotEmplacementList) {
                    boolean aAjouter = true;
                    for (Depot_Emplacement depotEmplacementDeBase : depotEmplacementDeBaseList) {
                        if (depotEmplacementDeBase.get_UID() == depotEmplacement.get_UID()) {
                            aAjouter = false;
                            break;
                        }
                    }
                    if (aAjouter) {
                        insererUnDepotEmplacementEnBDD(db, depotEmplacement);
                        ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, depotEmplacement.getPhiMR4UUID(), depotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.AJOUT);

                    }
                }
                // Est)ce qu'un emplacement a été mis à jour ?
                List<Depot_Emplacement> depotEmplacementNouvelleList = getEmplacementsParZone(db, depotZoneSelectionne);
                for (Depot_Emplacement depotEmplacementNouvelle : depotEmplacementNouvelleList) {
                    for (Depot_Emplacement depotEmplacement : adapter.depotEmplacementList) {
                        if (!depotEmplacementNouvelle.getCode_GLN().contentEquals(depotEmplacement.getCode_GLN())) {
                            mettreAJourEmplacement(db, depotEmplacement);
                            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, EmplacementOpenHelper.Constantes.TABLE_DEPOT_EMPLACEMENT, depotEmplacement.getPhiMR4UUID(), depotEmplacement.get_UID(), DBOpenHelper.ActionsEAS.MAJ);
                        }
                    }
                }
            }

            Toast.makeText(DetailZoneSaisieActivity.this, "Mise à jour effectuée", Toast.LENGTH_SHORT).show();
            ElementASynchroniserOpenHelper.ajouterElementASynchroniser(db, ZoneOpenHelper.Constantes.TABLE_DEPOT_ZONE, depotZoneSelectionne.getPhiMR4UUID(), depotZoneSelectionne.getZoneID(), DBOpenHelper.ActionsEAS.MAJ);

            ElementASynchroniserOpenHelper.toutSynchroniser(DetailZoneSaisieActivity.this, db, utilisateurConnecte, true);
        }

    }

}
