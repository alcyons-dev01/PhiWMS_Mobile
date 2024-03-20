package fr.alcyons.phimr4.DispositifAuLivret;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.Depot;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.MedicalObjective;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.PrisePhoto.PrisePhoto;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class DetailDispositifAuLivretActivity extends ServiceActivity {

    public InformationImportanteDispositif informationImportanteDispositif;
    Produit dispositif_Selectionne;

    Bitmap dispositifPhoto_Bitmap;

    List<Integer> produitID_List;

    int n = 1;

    PackageManager pm;

    // Permet de lancer l'activity BarcodeCaptureWithTakePicture
    View.OnClickListener onClickListener_Prendre_Photo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onMenuPhotoClick();
        }
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Code nécessaire à la mise en place d'une Activity contenant des Fragment*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dispositif_au_livret);

        //Gestion du package manager
        pm = DetailDispositifAuLivretActivity.this.getPackageManager();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /* Code nécessaire à l'exécution du service */
        produitID_List = intent.getExtras().getIntegerArrayList("produitID_List");
        dispositif_Selectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID_Selectionne"));

        invalidateOptionsMenu();
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    if (resultCode == DetailDispositifAuLivretActivity.RESULT_OK) {
                        String gs1 = data.getStringExtra("code");

                        int compteurErreur = 0;

                        if (gs1.length() == 13) {
                            dispositif_Selectionne.setGTIN(gs1);
                            compteurErreur++;
                        } else {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
                            dispositif_Selectionne.setGTIN(gs1Decoupe.get(OutilsDecodage.codeGtin));
                            compteurErreur++;
                        }

                        long rowId = gestionnaireProduit.mettreAJourProduit(db, dispositif_Selectionne);
                        if (rowId != -1) {
                            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, dispositif_Selectionne.getPhiMR4UUID(), dispositif_Selectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                        }

                        if (compteurErreur == 0) {
                            Alerte.afficherAlerte(DetailDispositifAuLivretActivity.this, "Alerte", "Impossible de récupérer le code GTIN du produit.", "alerte");
                        } else {
                            Intent detailDispositifAuLivret_Intent = new Intent(DetailDispositifAuLivretActivity.this, PrisePhoto.class);
                            Bundle detailDispositifAuLivret_Bundle = DetailDispositifAuLivretActivity.super.getBundle();
                            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                            detailDispositifAuLivret_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            detailDispositifAuLivret_Bundle.putString("nomDispositif", dispositif_Selectionne.getDesignation_interne());
                            detailDispositifAuLivret_Intent.putExtras(detailDispositifAuLivret_Bundle);
                            DetailDispositifAuLivretActivity.this.startActivityForResult(detailDispositifAuLivret_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
                        }
                    }
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    String photoProduit = data.getStringExtra("photoProduit");
                    if (photoProduit != null) {
                        Uri imageUri = Uri.parse(photoProduit);
                        if (imageUri != null) {
                            //dispositifPhoto_Bitmap = BitmapFactory.decodeByteArray(photoProduits, 0, photoProduits.length);
                            try {
                                dispositifPhoto_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            informationImportanteDispositif.photo.setImageBitmap(dispositifPhoto_Bitmap);
                            Depot depot = gestionnaireDepot.getDepotPUI(db);
                            if (OutilsGestionConnexionReseau.isServerAccessible(DetailDispositifAuLivretActivity.this)) {
                                MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depot, depot, dispositif_Selectionne, true);
                                if(dispositifPhoto_Bitmap != null)
                                {
                                    medicalObjective.savePicture(dispositifPhoto_Bitmap, String.valueOf(n), "DispositifAuLivret", false);
                                    if(utilisateurConnecte.getIdentifiant().toUpperCase().contentEquals("ALCYONS"))
                                    {
                                        boolean master = Alerte.afficherAlerte(DetailDispositifAuLivretActivity.this, "Master", "Publier la photo sur master ?", "OuiNon");
                                        if(master)
                                        {
                                            medicalObjective.savePicture(dispositifPhoto_Bitmap, String.valueOf(n), "DispositifAuLivret", true);
                                        }
                                    }
                                    n = n + 1;
                                    Toast.makeText(DetailDispositifAuLivretActivity.this, "Image envoyée à Médical Objective", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dispositif_Selectionne.setPhoto(dispositifPhoto_Bitmap.toString());
                                Toast.makeText(DetailDispositifAuLivretActivity.this, "L'image sera envoyée à la prochaine synchronisation", Toast.LENGTH_SHORT).show();
                                informationImportanteDispositif.photo.setVisibility(View.VISIBLE);
                            }
                        }
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
        menu.findItem(R.id.menuPhoto).setVisible(true);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Récupération de l'item ADD et affectation de l'action à réaliser lors d'un clic
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            MenuItem item = menu.findItem(R.id.menuPhoto);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onMenuPhotoClick();
                    return true;
                }
            });
        }

        return true;
    }

    private void onMenuPhotoClick() {
        if (dispositif_Selectionne.getGTIN().length() == 0) {
            if(!android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                Intent detailDispositifAuLivret_Intent = new Intent(DetailDispositifAuLivretActivity.this, BarcodeCaptureActivity.class);
                Bundle detailDispositifAuLivret_Bundle = DetailDispositifAuLivretActivity.super.getBundle();
                detailDispositifAuLivret_Bundle.putBoolean("isBoutonSuppressionExistant", true);
                detailDispositifAuLivret_Bundle.putBoolean("modeRafale", false);
                detailDispositifAuLivret_Bundle.putBoolean("modePhoto", false);
                detailDispositifAuLivret_Intent.putExtras(detailDispositifAuLivret_Bundle);

                DetailDispositifAuLivretActivity.this.startActivityForResult(detailDispositifAuLivret_Intent, CodesEchangesActivites.RETOUR_CODE_GS1);
            }

        } else {
            Intent detailDispositifAuLivret_Intent = new Intent(DetailDispositifAuLivretActivity.this, PrisePhoto.class);
            Bundle detailDispositifAuLivret_Bundle = DetailDispositifAuLivretActivity.super.getBundle();
            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
            detailDispositifAuLivret_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
            detailDispositifAuLivret_Bundle.putString("nomDispositif", dispositif_Selectionne.getDesignation_interne());
            detailDispositifAuLivret_Intent.putExtras(detailDispositifAuLivret_Bundle);
            DetailDispositifAuLivretActivity.this.startActivityForResult(detailDispositifAuLivret_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Initialisation des Fragment
        @Override
        public Fragment getItem(int position) {
            // On retourne ici un objet héritant de la classe Fragment, on choisi la classe et donc la page à afficher en fonction de la position
            switch (position) {
                case 0:
                    informationImportanteDispositif = new InformationImportanteDispositif();
                    informationImportanteDispositif.setParametres(dispositif_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationImportanteDispositif;
                case 1:
                    InformationComplementaireDispositif informationComplementaireDispositif = new InformationComplementaireDispositif();
                    informationComplementaireDispositif.setParametres(dispositif_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationComplementaireDispositif;
                case 2:
                    InformationRisqueDispositif informationRisqueDispositif = new InformationRisqueDispositif();
                    informationRisqueDispositif.setParametres(dispositif_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationRisqueDispositif;
                case 3:
                    InformationConservationDispositif informationConservationDispositif = new InformationConservationDispositif();
                    informationConservationDispositif.setParametres(dispositif_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationConservationDispositif;
            }
            Alerte.afficherAlerte(DetailDispositifAuLivretActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur onglets dispositifs au livret)", "alerte");
            return null;
        }

        // Définition du nombre de Fragment
        @Override
        public int getCount() {
            return 4;
        }

        // Définition des libellées des Fragment
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Infos importantes";
                case 1:
                    return "Infos générales";
                case 2:
                    return "Risques";
                case 3:
                    return "Conservation";
            }
            return null;
        }
    }
}
