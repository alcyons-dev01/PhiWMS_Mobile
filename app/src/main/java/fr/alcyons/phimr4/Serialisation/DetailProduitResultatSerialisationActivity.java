package fr.alcyons.phimr4.Serialisation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.alcyons.phimr4.BaseDeDonnees.PH_SerialisationOpenHelper;
import fr.alcyons.phimr4.BaseDeDonnees.ProduitOpenHelper;
import fr.alcyons.phimr4.Classes.PH_Serialisation;
import fr.alcyons.phimr4.Classes.Produit;
import fr.alcyons.phimr4.Outils.Alerte;
import fr.alcyons.phimr4.OutilsSerialisation.GestionRequeteTypeNMVO;
import fr.alcyons.phimr4.OutilsSerialisation.GestionResultatNMVO;
import fr.alcyons.phimr4.OutilsSerialisation.Serialisation;
import fr.alcyons.phimr4.R;
import fr.alcyons.phimr4.ServiceActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by olivier on 27/02/2019.
 */

public class DetailProduitResultatSerialisationActivity  extends ServiceActivity {

    public TextView phSerialisationStatutTextView;
    public TextView phSerialisationDateTextView;
    public TextView phSerialisationHeureTextView;
    public TextView phSerialisationRequeteTextView;
    public TextView phSerialisationProduitTextView;
    public TextView phSerialisationLotTextView;
    public TextView phSerialisationPeremptionDateTextView;
    public TextView phSerialisationSerieTextView;
    public TextView phSerialisationResultatTextView;
    public TextView phSerialisationRaisonTextView;

    private ProgressBar chargement_dispenser;
    private Button button_dispenser;
    private Button button_detruire;
    private Button button_deserialiser;
    private Button button_retour;
    private ProgressBar chargement_retour;
    private ProgressBar chargement_destruction;
    private ProgressBar chargement_deserialiser;
    private LinearLayout LinearProduitActif;
    private LinearLayout LinearProduitInactif;

    private Serialisation serialisation;
    public int serialExpressUid;
    Produit produits;
    private PH_Serialisation ph_serialisation_courant;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_detail_serialisation);

        serialisation = new Serialisation(DetailProduitResultatSerialisationActivity.this, db, utilisateurConnecte);

        phSerialisationStatutTextView = (TextView) findViewById(R.id.phSerialisationStatut);
        phSerialisationDateTextView = (TextView) findViewById(R.id.phSerialisationDate);
        phSerialisationHeureTextView = (TextView) findViewById(R.id.phSerialisationHeure);
        phSerialisationRequeteTextView = (TextView) findViewById(R.id.phSerialisationRequete);
        phSerialisationProduitTextView = (TextView) findViewById(R.id.phSerialisationProduit);
        phSerialisationLotTextView = (TextView) findViewById(R.id.phSerialisationLot);
        phSerialisationPeremptionDateTextView = (TextView) findViewById(R.id.phSerialisationPeremptionDate);
        phSerialisationSerieTextView = (TextView) findViewById(R.id.phSerialisationSerie);
        phSerialisationResultatTextView = (TextView) findViewById(R.id.phSerialisationResultat);
        phSerialisationRaisonTextView = (TextView) findViewById(R.id.phSerialisationRaison);

        chargement_dispenser = (ProgressBar) findViewById(R.id.chargement_dispenser);
        button_dispenser = (Button) findViewById(R.id.button_dispenser);
        button_detruire = (Button) findViewById(R.id.button_detruire);
        button_deserialiser = (Button) findViewById(R.id.button_deserialiser);
        button_retour = (Button) findViewById(R.id.button_retour);
        chargement_destruction = (ProgressBar) findViewById(R.id.chargement_destruction);
        chargement_deserialiser = (ProgressBar) findViewById(R.id.chargement_deserialiser);
        chargement_retour = (ProgressBar) findViewById(R.id.chargement_retour);

        LinearProduitActif = (LinearLayout) findViewById(R.id.LinearProduitActif);
        LinearProduitInactif = (LinearLayout) findViewById(R.id.LinearProduitInactif);

        LinearProduitActif.setVisibility(GONE);
        LinearProduitInactif.setVisibility(GONE);

        //Récupérer la référence selectionner
        serialExpressUid = intent.getExtras().getInt("serialExpressUid");

        ph_serialisation_courant = gestionnaireSerialisation.getPH_SerialisationByid(db, serialExpressUid);
        if(ph_serialisation_courant == null)
            ph_serialisation_courant = gestionnaireSerialisation.getPH_SerialisationByPhiMR4UUID(db, serialExpressUid);

        if(ph_serialisation_courant != null)
        {
            String statut_ph_serialisation = ph_serialisation_courant.getStatut();
            //Gestion des statuts
            if (statut_ph_serialisation.contentEquals("En attente"))
                phSerialisationStatutTextView.setTextColor(getResources().getColor(R.color.orange_fonce, null));
            else
                phSerialisationStatutTextView.setTextColor(getResources().getColor(R.color.vert3, null));

            phSerialisationStatutTextView.setText(statut_ph_serialisation);


            String resultat_ph_serialisation = ph_serialisation_courant.getResultat();
            if (resultat_ph_serialisation.contentEquals("INACTIVE")) {
                String raison = GestionResultatNMVO.getResultat(ph_serialisation_courant.getRaison());
                phSerialisationRaisonTextView.setText(raison);
                phSerialisationResultatTextView.setText(resultat_ph_serialisation);
                phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.orange_fonce, null));
                if(!raison.contentEquals("Référence détruite"))
                {
                    LinearProduitInactif.setVisibility(VISIBLE);
                }
            } else if (resultat_ph_serialisation.contentEquals("ACTIVE")) {
                phSerialisationRaisonTextView.setVisibility(GONE);
                phSerialisationResultatTextView.setText(resultat_ph_serialisation);
                phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.vert3, null));
                LinearProduitActif.setVisibility(VISIBLE);
            } else {
                phSerialisationRaisonTextView.setVisibility(GONE);
                phSerialisationResultatTextView.setText(resultat_ph_serialisation);
                phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.rouge, null));
            }

            phSerialisationDateTextView.setText(ph_serialisation_courant.getDemandeDate());
            phSerialisationHeureTextView.setText(ph_serialisation_courant.getDemandeHeure());
            String requete_type = GestionRequeteTypeNMVO.getType(ph_serialisation_courant.getReqType());
            phSerialisationRequeteTextView.setText(requete_type);

            String phSerialisationProduit = "Produit non renseigné";
            if (ph_serialisation_courant.getProduitUID() != 0) {
                produits = ProduitOpenHelper.getProduitByID(db, ph_serialisation_courant.getProduitUID());
                if (produits != null)
                    phSerialisationProduit = produits.getDesignation_interne();
            }
            phSerialisationProduitTextView.setText(phSerialisationProduit);

            phSerialisationLotTextView.setText(ph_serialisation_courant.getNumeroLot());

            // Afficher la date de peremption au format JJ/MM/AA
            SimpleDateFormat dateFormatYYMMDD = new SimpleDateFormat("yyMMdd");

            Date dateYYMMDD = null;
            String dateDDMMYYYY = "";
            try {
                dateYYMMDD = dateFormatYYMMDD.parse(ph_serialisation_courant.getDatePeremptionAAMMJJ());
                SimpleDateFormat dateFormatddMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
                dateDDMMYYYY = dateFormatddMMYYYY.format(dateYYMMDD);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            phSerialisationPeremptionDateTextView.setText(dateDDMMYYYY);
            phSerialisationSerieTextView.setText(ph_serialisation_courant.getNumeroSerie());
        }
        else
        {
            onBackPressed();
        }

    }

    public void onResume()
    {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_datamatrix, menu);


        menu.findItem(R.id.deleteMenu).setVisible(false);
        menu.findItem(R.id.rechercheMenu).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        //On regarde quel item a été cliqué grâce à son id et on déclenche une action
        switch (menuItem.getItemId()) {
            case R.id.scanner:
                Intent intent_retour_scan = new Intent(DetailProduitResultatSerialisationActivity.this, ServiceSerialisationActivity.class);
                Bundle bundle_retour_scan = new Bundle();
                bundle_retour_scan.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
                bundle_retour_scan.putInt("serviceSelectionneID", getIntent().getExtras().getInt("serviceSelectionneID"));
                intent_retour_scan.putExtras(bundle_retour_scan);
                DetailProduitResultatSerialisationActivity.this.startActivity(intent_retour_scan);
                DetailProduitResultatSerialisationActivity.this.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed()
    {
        boolean gestionListe = false;
        gestionListe = getIntent().getExtras().getBoolean("List");
        if(gestionListe)
        {
            DetailProduitResultatSerialisationActivity.this.finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void onClick_DispenserProduit(View v)
    {
        chargement_dispenser.setVisibility(VISIBLE);
        button_dispenser.setVisibility(GONE);
        button_detruire.setClickable(false);
        button_detruire.setBackgroundColor(getResources().getColor(R.color.gris_fonce, null));

        //gestion du mouvement
        ph_serialisation_courant.setMvtType("SUPPLIED");
        ph_serialisation_courant.setMvtUID(String.valueOf(ph_serialisation_courant.get_UID()));
        long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, ph_serialisation_courant);

        //On dispense
         long UID = serialisation.Serialisation_Serialiser(utilisateurConnecte.getId(), false, false, "SUPPLIED", ph_serialisation_courant.getProduitCodeValue(), ph_serialisation_courant.getProduitCodeSheme(), ph_serialisation_courant.getNumeroLot(), ph_serialisation_courant.getDatePeremptionAAMMJJ(), ph_serialisation_courant.getNumeroSerie(), "Dispenser", ph_serialisation_courant.getMvtUID());

        ph_serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int) UID);
        if(ph_serialisation_courant == null)
            ph_serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int)UID);
        phSerialisationResultatTextView.setVisibility(VISIBLE);
        String resultat_ph_serialisation = ph_serialisation_courant.getResultat();
        String raison = ph_serialisation_courant.getRaison();

        if (resultat_ph_serialisation.contentEquals("INACTIVE") && (raison.contentEquals("SUPPLIED") || raison.contentEquals("SAMPLE"))) {
            raison = "La référence a bien été dispensée";
            phSerialisationRaisonTextView.setVisibility(VISIBLE);
            phSerialisationRaisonTextView.setText(raison);
            phSerialisationResultatTextView.setText(resultat_ph_serialisation);
            phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.orange_fonce, null));
        } else {
            raison = "La référence n'a pas été dispensée";
            phSerialisationRaisonTextView.setVisibility(VISIBLE);
            phSerialisationRaisonTextView.setText(raison);
            phSerialisationResultatTextView.setText(resultat_ph_serialisation);
            phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.rouge, null));
        }

        LinearProduitActif.setVisibility(GONE);
    }

    public void onClick_DetruireProduit(View v)
    {
        chargement_destruction.setVisibility(VISIBLE);
        button_detruire.setVisibility(GONE);
        button_dispenser.setClickable(false);
        button_dispenser.setBackgroundColor(getResources().getColor(R.color.gris_fonce, null));

        //gestion du mouvement
        ph_serialisation_courant.setMvtType("Détruire");
        ph_serialisation_courant.setMvtUID(String.valueOf(ph_serialisation_courant.get_UID()));
        long rowUID = PH_SerialisationOpenHelper.mettreAJourPH_SerialisationEnBDD(db, ph_serialisation_courant);

        //on détruit
        long UID = serialisation.Serialisation_Serialiser(utilisateurConnecte.getId(), false, false, "DESTROYED", ph_serialisation_courant.getProduitCodeValue(), ph_serialisation_courant.getProduitCodeSheme(), ph_serialisation_courant.getNumeroLot(), ph_serialisation_courant.getDatePeremptionAAMMJJ(), ph_serialisation_courant.getNumeroSerie(), "Detruire", ph_serialisation_courant.getMvtUID());

        //on récupère le PH_Serialisation modifié
        ph_serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationByPhiMR4UUID(db, (int) UID);
        if(ph_serialisation_courant == null)
            ph_serialisation_courant = PH_SerialisationOpenHelper.getPH_SerialisationByid(db, (int) UID);
        phSerialisationResultatTextView.setVisibility(VISIBLE);
        String resultat_ph_serialisation = ph_serialisation_courant.getResultat();
        String raison = ph_serialisation_courant.getRaison();
        if (resultat_ph_serialisation.contentEquals("INACTIVE") && raison.contentEquals("DESTROYED")) {
            raison = "La référence a bien été détruite";
            phSerialisationRaisonTextView.setVisibility(VISIBLE);
            phSerialisationRaisonTextView.setText(raison);
            phSerialisationResultatTextView.setText(resultat_ph_serialisation);
            phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.orange_fonce, null));
        } else {
            raison = "La référence n'a pas été détruite";
            phSerialisationRaisonTextView.setVisibility(VISIBLE);
            phSerialisationRaisonTextView.setText(raison);
            phSerialisationResultatTextView.setText(resultat_ph_serialisation);
            phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.rouge, null));
        }

        LinearProduitActif.setVisibility(GONE);
    }

    public void onClick_button_deserialiser(View v)
    {
        chargement_deserialiser.setVisibility(VISIBLE);
        button_deserialiser.setVisibility(GONE);

        //On dispense
        String action = "";
        if (ph_serialisation_courant.getRaison().contentEquals("SUPPLIED"))
            action = "SUPPLIED";
        else if (ph_serialisation_courant.getRaison().contentEquals("SAMPLE"))
            action = "SAMPLE";
        else if (ph_serialisation_courant.getRaison().contentEquals("DESTROYED"))
            action = "DESTROYED";

        if (action.contentEquals("")) {
            Alerte.afficherAlerte(DetailProduitResultatSerialisationActivity.this, "Erreur", "Une erreur est survenue, le produit ne peut pas être désérialiser.", "alerte");
            chargement_deserialiser.setVisibility(GONE);
            button_deserialiser.setVisibility(VISIBLE);
            button_deserialiser.setClickable(false);
        } else if (action.contentEquals("DESTROYED")) {
            Alerte.afficherAlerte(DetailProduitResultatSerialisationActivity.this, "Erreur", "Le produit a été détruit et ne peut pas être désérialiser.", "alerte");
            chargement_deserialiser.setVisibility(GONE);
            button_deserialiser.setVisibility(VISIBLE);
            button_deserialiser.setClickable(false);
        } else {
            //gestion du mouvement
            ph_serialisation_courant.setMvtType("Désérialiser");
            ph_serialisation_courant.setMvtUID(String.valueOf(ph_serialisation_courant.get_UID()));
            long rowUID = gestionnaireSerialisation.mettreAJourPH_SerialisationEnBDD(db, ph_serialisation_courant);

            button_deserialiser.setClickable(true);
            long UID = serialisation.Serialisation_Deserialiser(utilisateurConnecte.getId(), false, false, action, ph_serialisation_courant.getProduitCodeValue(), ph_serialisation_courant.getProduitCodeSheme(), ph_serialisation_courant.getNumeroLot(), ph_serialisation_courant.getDatePeremptionAAMMJJ(), ph_serialisation_courant.getNumeroSerie(), "Retour", ph_serialisation_courant.getMvtUID());

            ph_serialisation_courant = gestionnaireSerialisation.getPH_SerialisationByid(db, (int) UID);
            if(ph_serialisation_courant == null)
                ph_serialisation_courant = gestionnaireSerialisation.getPH_SerialisationByPhiMR4UUID(db, (int)UID);
            phSerialisationResultatTextView.setVisibility(VISIBLE);
            String resultat_ph_serialisation = ph_serialisation_courant.getResultat();
            phSerialisationRaisonTextView.setVisibility(GONE);
            phSerialisationResultatTextView.setText(resultat_ph_serialisation);
            phSerialisationResultatTextView.setTextColor(getResources().getColor(R.color.vert3, null));

            button_deserialiser.setVisibility(GONE);
            Alerte.afficherAlerte(DetailProduitResultatSerialisationActivity.this, "Succès", "Le produit a bien été désérialisé", "alerte");
            onClick_button_retour(getWindow().getDecorView().getRootView());
        }
    }

    public void onClick_button_retour(View v)
    {
        button_retour.setVisibility(GONE);
        button_deserialiser.setClickable(false);
        chargement_retour.setVisibility(VISIBLE);
        Intent intent_retour_scan = new Intent(DetailProduitResultatSerialisationActivity.this, ServiceSerialisationActivity.class);
        Bundle bundle_retour_scan = new Bundle();
        bundle_retour_scan.putInt("utilisateurConnecteID", utilisateurConnecte.getId());
        bundle_retour_scan.putInt("serviceSelectionneID", getIntent().getExtras().getInt("serviceSelectionneID"));
        intent_retour_scan.putExtras(bundle_retour_scan);
        DetailProduitResultatSerialisationActivity.this.startActivity(intent_retour_scan);
        DetailProduitResultatSerialisationActivity.this.finish();
    }
}

