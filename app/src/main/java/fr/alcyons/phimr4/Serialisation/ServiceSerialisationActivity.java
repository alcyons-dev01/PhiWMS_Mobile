package fr.alcyons.phimr4.Serialisation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import fr.alcyons.phimr4.BarcodeSearch.BarcodeCaptureActivity;
import fr.alcyons.phimr4.BaseDeDonnees.Parametres_SerialisationOpenHelper;
import fr.alcyons.phimr4.Classes.Parametres_Serialisation;
import fr.alcyons.phimr4.Navigation.NavigationActivity;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.Outils.CodesEchangesActivites;
import fr.alcyons.phimr4.Outils.OutilsGestionConnexionReseau;
import fr.alcyons.phimr4.OutilsSerialisation.DecodageSerialisation;
import fr.alcyons.phimr4.OutilsSerialisation.Serialisation;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

/**
 * Created by olivier on 26/02/2019.
 */

public class ServiceSerialisationActivity extends ServiceActivity {

    AlertDialog alertDialog;
    private Context context = ServiceSerialisationActivity.this;
    private Serialisation serialisation;
    String Nom_Document = "";

    PackageManager pm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_serialiser);

        //gestion du package manager
        pm = ServiceSerialisationActivity.this.getPackageManager();

        serialisation = new Serialisation(context, db, utilisateurConnecte);

        boolean operationSerialisationAutorise = true;

        Nom_Document = "Non renseigné";
        Parametres_Serialisation parametres_serialisation = Parametres_SerialisationOpenHelper.getParametres_Serialisation(db);

        if(parametres_serialisation.getFranceMVO_identifiant().isEmpty() || parametres_serialisation.getFranceMVO_mdp().isEmpty()){
            operationSerialisationAutorise = false;
            Alerte.afficherAlerte(ServiceSerialisationActivity.this, "Erreur", "Vous n'avez pas renseigné les informations de votre compte France MVO vous ne pouvez pas réaliser d'opération de sérialisation.", "alerte");
        }
        else if(!parametres_serialisation.isFranceMVO_termesEtConditions()){
            operationSerialisationAutorise = false;
            Alerte.afficherAlerte(ServiceSerialisationActivity.this, "Erreur", "Vous n'avez pas accpeter les termes et conditions de France MVO vous ne pouvez pas réaliser d'opération de sérialisation.", "alerte");
        }

        if(operationSerialisationAutorise){
            modeSimple();
        }
        else{
            Alerte.afficherAlerte(ServiceSerialisationActivity.this, "Erreur", "Les informations du compte France MVO n'ont pas été renseignées correctement, veuillez contacter Alcyons s'il vous plaît.", "alerte");
            retourTableauDeBord();
        }
    }

    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            ServiceSerialisationActivity.this.finish();
        } else {
            switch (requestCode) {
                case CodesEchangesActivites.RETOUR_CODE_GS1: {
                      if (data.getExtras().getString("code") == null) {
                        Alerte.afficherAlerte(context, "Erreur", "Le code scanné est inconnu", "alerte");
                        retourTableauDeBord();
                          ServiceSerialisationActivity.this.finish();
                    } else {
                        String code = data.getExtras().getString("code");
                        Map<String, String> gs1Decoupe = DecodageSerialisation.decouperGTIN(code);
                        String codeGtin = gs1Decoupe.get(DecodageSerialisation.codeGtin);
                        String numeroLot = gs1Decoupe.get(DecodageSerialisation.numeroLot);
                        String numeroSerie = gs1Decoupe.get(DecodageSerialisation.numeroSerie);
                        String dateDePeremption = gs1Decoupe.get(DecodageSerialisation.dateDePeremption);

                        if(codeGtin == null)
                        {
                            Alerte.afficherAlerte(this, "Erreur", "Une erreur est survenue, le produit scanné n'est pas un code GS1", "alerte");
                            reload();
                        }
                        else
                        {
                            long ph_serialisation_uid = 0;
                            boolean differe = false;
                            if (!OutilsGestionConnexionReseau.isServerAccessible(ServiceSerialisationActivity.this))
                                differe = true;

                            ph_serialisation_uid = serialisation.Serialisation_Verifier(utilisateurConnecte.getId(), false, differe, codeGtin, "GTIN", numeroLot, dateDePeremption, numeroSerie, "Vérifier", "", Nom_Document, "Vérification");

                            if (ph_serialisation_uid > 0)
                                passerAuDetailSimple(ph_serialisation_uid);
                            else {
                                if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                                {
                                    modeSimple();
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void modeSimple()
    {
        Bundle VerifierSimpleBundle = ServiceSerialisationActivity.super.getBundle();
        VerifierSimpleBundle.putString("contexte", String.valueOf(R.string.scannerContexteProduit));
        VerifierSimpleBundle.putBoolean("isBoutonSuppressionExistant", false);
        VerifierSimpleBundle.putBoolean("modeRafale", false);
        VerifierSimpleBundle.putBoolean("modeEchantillon", false);
        VerifierSimpleBundle.putBoolean("serialisation", true);

        Intent verifierSimpleIntent = new Intent(ServiceSerialisationActivity.this, BarcodeCaptureActivity.class);
        verifierSimpleIntent.putExtras(VerifierSimpleBundle);
        ServiceSerialisationActivity.this.startActivityForResult(verifierSimpleIntent, CodesEchangesActivites.RETOUR_CODE_GS1);
    }

    private void passerAuDetailSimple(long ph_serialisation_uid)
    {
        Intent intent_vers_details_Simple = new Intent(ServiceSerialisationActivity.this, DetailProduitResultatSerialisationActivity.class);
        Bundle bundle_vers_details = new Bundle();
        bundle_vers_details.putInt("serialExpressUid", (int) ph_serialisation_uid);
        bundle_vers_details.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle_vers_details.putInt("serviceSelectionneID", serviceActuel.getId());
        bundle_vers_details.putBoolean("List", false);
        intent_vers_details_Simple.putExtras(bundle_vers_details);
        ServiceSerialisationActivity.this.startActivity(intent_vers_details_Simple);
        ServiceSerialisationActivity.this.finish();
    }

    private void retourTableauDeBord()
    {
        Intent identifierProduit_Intent = new Intent(ServiceSerialisationActivity.this, NavigationActivity.class);

        Bundle identifierProduit_Bundle = ServiceSerialisationActivity.super.getBundle();
        identifierProduit_Bundle.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        identifierProduit_Bundle.putInt("serviceSelectionneID", 1674);
        identifierProduit_Intent.putExtras(identifierProduit_Bundle);
        ServiceSerialisationActivity.this.startActivity(identifierProduit_Intent);
        ServiceSerialisationActivity.this.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void afficherAlertePatience()
    {
        TextView text_patient;
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceSerialisationActivity.this);
        LayoutInflater inflater = ServiceSerialisationActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.alerte_attente, null);

        text_patient = (TextView) layout.findViewById(R.id.text_patient);

        text_patient.setText("Veuillez patienter");
        text_patient.setBackgroundColor(getResources().getColor(R.color.vert3, null));

        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.setCancelable(false);
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public void reload()
    {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
