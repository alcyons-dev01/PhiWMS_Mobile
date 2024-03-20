package fr.alcyons.phimr4.IdentificationParScan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsDecodage;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

public class ServiceIdentificationParScanActivity extends ServiceActivity {

    /* 
    *   Le booléen firstPassage permet de savoir si l'on passe pour la première fois par le onResume ou non, il permet de ne pas relancer l'activité
    * BarcodeCaptureWithGoogleVisionSearchActivity lorsqu'on ne passe pas pour la première fois. Il est nécessaire pour gérer le cas où
    * l'utilisateur retourne un code valide.
    *
    *   Lorsque cela arrive, l'application va tenter de lancer l'activité suivante (cf : "Si le code est valide") puis appeler la méthode onResume.
    * Sans la gestion du booléen firstPassage, la méthode onResume appellerait à nouveau l'activité BarcodeCaptureWithGoogleVisionSearchActivity ce
    * qui empêche parfois de passer à l'activité suivante. On évite donc de décoder si on ne passe pas pour la première fois
    * */
    boolean firstPassage = true;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_identification_par_scan);

        //gestion du package manager
        pm = ServiceIdentificationParScanActivity.this.getPackageManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (firstPassage) {
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell"))
            {
                // Si on passe pour la première fois, on lance l'activité de décodage.
                Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ScannerSearchOnlyActivity.class);
                Bundle extras = super.getBundle();
                extras.putBoolean("isBoutonSuppressionExistant", true);
                newIntent.putExtras(extras);
                ServiceIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                firstPassage = false;
            }
            else
            {
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                {
                    // Si on passe pour la première fois, on lance l'activité de décodage.
                    Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, BarcodeCaptureActivity.class);
                    Bundle extras = super.getBundle();
                    extras.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(extras);
                    ServiceIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                    firstPassage = false;
                }
                else
                {
                    // Si on passe pour la première fois, on lance l'activité de décodage.
                    Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ScannerSearchOnlyActivity.class);
                    Bundle extras = super.getBundle();
                    extras.putBoolean("isBoutonSuppressionExistant", true);
                    newIntent.putExtras(extras);
                    ServiceIdentificationParScanActivity.this.startActivityForResult(newIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
                    firstPassage = false;
                }
            }
        }
    }

    // Lorsqu'on lance une nouvelle activity avec " startActivityForResult ", action à réaliser à la fin de l'activity lancé suivant le " CodesEchangesActivites " passé
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServiceIdentificationParScanActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                    String codeComplet = data.getStringExtra("code");
                    if(codeComplet != null && !codeComplet.contentEquals(""))
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (gs1Decoupe.size() != 1) {
                            // Si le code est valide, on lance l'activité de liste des produits correspondants à ce code
                            Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                            Bundle extras = super.getBundle();
                            extras.putString("codeGS1", codeComplet);
                            newIntent.putExtras(extras);
                            ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                            ServiceIdentificationParScanActivity.this.finish();
                        } else {
                            // Si le code fourni n'est pas valide, on affiche un message d'erreur et on redémarre l'activité pour réinitialiser le booléen firstPassage
                            Toast toast = Toast.makeText(ServiceIdentificationParScanActivity.this, "Le code fourni n'est pas un code GS1, c'est un code inconnu.", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                            Bundle extras = super.getBundle();
                            extras.putString("codeInconnue", codeComplet);
                            newIntent.putExtras(extras);
                            ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                            ServiceIdentificationParScanActivity.this.finish();
                        }
                    }
                    else
                    {
                        Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                        Bundle extras = super.getBundle();
                        extras.putString("codeInconnue", "");
                        newIntent.putExtras(extras);
                        ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                        ServiceIdentificationParScanActivity.this.finish();
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
        super.onBackPressed();
    }
}
