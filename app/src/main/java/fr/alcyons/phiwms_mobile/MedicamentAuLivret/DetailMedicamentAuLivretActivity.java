package fr.alcyons.phiwms_mobile.MedicamentAuLivret;

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

import fr.alcyons.phiwms_mobile.BaseDeDonnees.DBOpenHelper;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.Classes.Depot;
import fr.alcyons.phiwms_mobile.Classes.Produit;
import fr.alcyons.phiwms_mobile.Outils.Alerte;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.MedicalObjective;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhoto;
import fr.alcyons.phiwms_mobile.PrisePhoto.PrisePhotoV2;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class DetailMedicamentAuLivretActivity extends ServiceActivity {

    public InformationImportanteMedicament informationImportanteMedicament;
    public Bitmap medicamentPhoto_Bitmap;
    Produit medicament_Selectionne;
    int n = 1;
    List<Integer> produitID_List;

    // Permet de lancer l'activity BarcodeCaptureWithTakePicture
    View.OnClickListener onClickListener_Prendre_Photo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !android.os.Build.MANUFACTURER.contains("Zebra Technologies") && !android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                onMenuPhotoClick();
            }
        }
    };

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Code nécessaire à la mise en place d'une Activity contenant des Fragment*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_medicament_au_livret);

        //gestion du package manager
        pm = DetailMedicamentAuLivretActivity.this.getPackageManager();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        /* Code nécessaire à l'exécution du service */
        produitID_List = intent.getExtras().getIntegerArrayList("produitID_List");
        medicament_Selectionne = gestionnaireProduit.getProduitByID(db, intent.getExtras().getInt("produitID_Selectionne"));

        invalidateOptionsMenu();
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
        Intent detailMedicamentAuLivret_Intent = new Intent(DetailMedicamentAuLivretActivity.this, PrisePhotoV2.class);
        Bundle detailMedicamentAuLivret_Bundle = DetailMedicamentAuLivretActivity.super.getBundle();
        detailMedicamentAuLivret_Bundle.putString("nomProduit", medicament_Selectionne.getDesignation_ext());
        detailMedicamentAuLivret_Bundle.putInt("id_Produit", medicament_Selectionne.getID_produit());
        detailMedicamentAuLivret_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        detailMedicamentAuLivret_Intent.putExtras(detailMedicamentAuLivret_Bundle);
        DetailMedicamentAuLivretActivity.this.startActivityForResult(detailMedicamentAuLivret_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1:
                    if (resultCode == DetailMedicamentAuLivretActivity.RESULT_OK) {
                        String gs1 = data.getStringExtra("code");

                        int compteurErreur = 0;

                        if (gs1.length() == 13) {
                            medicament_Selectionne.setGTIN(gs1);
                            compteurErreur++;
                        } else {
                            Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(gs1);
                            medicament_Selectionne.setGTIN(gs1Decoupe.get(OutilsDecodage.codeGtin));
                            compteurErreur++;
                        }

                        long rowId = gestionnaireProduit.mettreAJourProduit(db, medicament_Selectionne);
                        if (rowId != -1) {
                            gestionnaireElementASynchroniser.ajouterElementASynchroniser(db, ProduitOpenHelper.Constantes.TABLE_PRODUIT, medicament_Selectionne.getPhiMR4UUID(), medicament_Selectionne.getID_produit(), DBOpenHelper.ActionsEAS.MAJ);
                        }


                        if (compteurErreur == 0) {
                            Alerte.afficherAlerte(DetailMedicamentAuLivretActivity.this, "Alerte", "Impossible de récupérer le code GTIN du produit.", "alerte");
                        } else {
                            Intent detailMedicamentAuLivret_Intent = new Intent(DetailMedicamentAuLivretActivity.this, PrisePhoto.class);
                            Bundle detailMedicamentAuLivret_Bundle = DetailMedicamentAuLivretActivity.super.getBundle();
                            detailMedicamentAuLivret_Bundle.putString("nomProduit", medicament_Selectionne.getDesignation_ext());
                            detailMedicamentAuLivret_Bundle.putInt("id_Produit", medicament_Selectionne.getID_produit());
                            // Nécessaire pour éviter le message " L'utilisateur connecté a été perdu "
                            detailMedicamentAuLivret_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                            detailMedicamentAuLivret_Intent.putExtras(detailMedicamentAuLivret_Bundle);
                            DetailMedicamentAuLivretActivity.this.startActivityForResult(detailMedicamentAuLivret_Intent, CodesEchangesActivites.RETOUR_PRISE_PHOTO);
                        }
                    }
                    break;
                case CodesEchangesActivites.RETOUR_PRISE_PHOTO:
                    //String photoProduit = data.getStringExtra("photoProduit");
                    List<String> listPhoto = (List<String>) data.getSerializableExtra("photoProduit");
                    for(String photoProduit : listPhoto)
                    {
                        if (photoProduit != null) {
                            Uri imageUri = Uri.parse(photoProduit);
                            if (imageUri != null) {
                                try {
                                    medicamentPhoto_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                informationImportanteMedicament.photo.setImageBitmap(medicamentPhoto_Bitmap);
                                Depot depot = gestionnaireDepot.getDepotPUI(db);
                                if (OutilsGestionConnexionReseau.isServerAccessible(DetailMedicamentAuLivretActivity.this)) {
                                    MedicalObjective medicalObjective = new MedicalObjective(this, utilisateurConnecte, depot, depot, medicament_Selectionne, true);
                                    medicalObjective.savePicture(medicamentPhoto_Bitmap, String.valueOf(n), "MedicamentAuLivret", false);

                                    n = n + 1;
                                    Toast.makeText(DetailMedicamentAuLivretActivity.this, "Image envoyée à Médical Objective", Toast.LENGTH_SHORT).show();
                                } else {
                                    medicament_Selectionne.setPhoto(medicamentPhoto_Bitmap.toString());
                                    Toast.makeText(DetailMedicamentAuLivretActivity.this, "L'image sera envoyée à la prochaine synchronisation", Toast.LENGTH_SHORT).show();
                                    informationImportanteMedicament.photo.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Initialisation des Fragment
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    informationImportanteMedicament = new InformationImportanteMedicament();
                    informationImportanteMedicament.setParametres(medicament_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationImportanteMedicament;
                case 1:
                    InformationPrescriptionMedicament informationPrescriptionMedicament = new InformationPrescriptionMedicament();
                    informationPrescriptionMedicament.setParametres(medicament_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationPrescriptionMedicament;
                case 2:
                    InformationComplementaireMedicament informationComplementaireMedicament = new InformationComplementaireMedicament();
                    informationComplementaireMedicament.setParametres(medicament_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationComplementaireMedicament;
                case 3:
                    InformationConservationMedicament informationConservationMedicament = new InformationConservationMedicament();
                    informationConservationMedicament.setParametres(medicament_Selectionne, db, onClickListener_Prendre_Photo, produitID_List);
                    return informationConservationMedicament;
            }
            Alerte.afficherAlerte(DetailMedicamentAuLivretActivity.this, "Erreur", "Veuillez contacter la société Alcyons (erreur onglets médicaments au livret)", "alerte");
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
                    return "Infos principales";
                case 2:
                    return "Infos complémentaires";
                case 3:
                    return "Conservation";
            }
            return null;
        }

    }

}
