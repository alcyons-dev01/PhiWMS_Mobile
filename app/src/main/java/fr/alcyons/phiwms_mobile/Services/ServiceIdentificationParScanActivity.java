package fr.alcyons.phiwms_mobile.Services;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import java.util.Map;

import fr.alcyons.phiwms_mobile.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phiwms_mobile.BarcodeSearch.ScannerSearchOnlyActivity;
import fr.alcyons.phiwms_mobile.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phiwms_mobile.IdentificationParScan.ListeProduitsIdentificationParScanActivity;
import fr.alcyons.phiwms_mobile.Navigation.NavigationActivity;
import fr.alcyons.phiwms_mobile.Outils.CodesEchangesActivites;
import fr.alcyons.phiwms_mobile.Outils.OutilsDecodage;
import fr.alcyons.phiwms_mobile.R;
import fr.alcyons.phiwms_mobile.ServiceActivity;

public class ServiceIdentificationParScanActivity extends ServiceActivity {
    boolean firstPassage = true;

    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_identification_par_scan);

        //gestion du package manager
        pm = ServiceIdentificationParScanActivity.this.getPackageManager();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceIdentificationParScanActivity.this, NavigationActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                intent.putExtras(extras);
                ServiceIdentificationParScanActivity.this.startActivity(intent);
                ServiceIdentificationParScanActivity.this.finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (firstPassage) {
            if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.toLowerCase().contains("honeywell") || android.os.Build.MANUFACTURER.toLowerCase().contains("google"))
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
                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServiceIdentificationParScanActivity.this.finish();
        } else {
            if (requestCode == CodesEchangesActivites.RETOUR_CODE_GS1) {
                String codeComplet = data.getStringExtra("code");
                if (codeComplet != null && !codeComplet.contentEquals("")) {
                    if(codeComplet.startsWith("PHITAGTIN:"))
                    {
                        String gtin = "";
                        String[] tabCode = codeComplet.toString().split(":");
                        if(tabCode.length == 2)
                        {
                            gtin = tabCode[1];
                        }
                        Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                        Bundle extras = super.getBundle();
                        extras.putString("codeGS1", gtin);
                        newIntent.putExtras(extras);
                        ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                        ServiceIdentificationParScanActivity.this.finish();
                    }
                    else
                    {
                        Map<String, String> gs1Decoupe = OutilsDecodage.decouperGTIN(codeComplet);
                        if (gs1Decoupe.size() != 1) {
                            // Si le code est valide, on lance l'activité de liste des produits correspondants à ce code
                            Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                            Bundle extras = super.getBundle();
                            extras.putString("codeGS1", gs1Decoupe.get("codeGtin"));
                            newIntent.putExtras(extras);
                            ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                            ServiceIdentificationParScanActivity.this.finish();
                        }
                        else
                        {
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

                } else {
                    Intent newIntent = new Intent(ServiceIdentificationParScanActivity.this, ListeProduitsIdentificationParScanActivity.class);
                    Bundle extras = super.getBundle();
                    extras.putString("codeInconnue", "");
                    newIntent.putExtras(extras);
                    ServiceIdentificationParScanActivity.this.startActivity(newIntent);
                    ServiceIdentificationParScanActivity.this.finish();
                }
            }
            invalidateOptionsMenu();
        }
    }
}
